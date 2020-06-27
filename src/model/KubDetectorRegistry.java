package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Capabilities;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1DaemonSetSpec;
import io.kubernetes.client.openapi.models.V1DaemonSetUpdateStrategy;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LocalObjectReference;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import io.kubernetes.client.openapi.models.V1SecurityContext;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1EmptyDirVolumeSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;

/**
 * Registers a client-server infrastructure to get the connections between pods.
 * @author Alexis T. Bernhard
 *
 */
class KubDetectorRegistry {

	/**
	 * The port used for communication between the client and the server of the infrastructure.
	 */
	static final int PORT = 31843;
	
	/**
	 * The name of the secret used for authentication to docker hub.
	 */
	private static final String SECRET_NAME = "tracer-secret";
	
	/**
	 * The name of the deployment of the server installed in the kubernetes cluster (defines the server pod).
	 */
	private static final String SERVER_DEPLOYMENT_NAME = "collector";
	
	/**
	 * The name of the service of the server pod in the kubernetes cluster.
	 */
	private static final String SERVER_SERVICE_NAME = "collector-service";
	
	/**
	 * The name of the daemonset used to define all clients in the kubernetes cluster.
	 */
	private static final String DAEMONSET_NAME = "detector-agent-daemon";
	
	/**
	 * The name of the volume used to share data between the procspy and the conntrack containers in a client pod.
	 */
	private static final String VOLUME_NAME = "procspy-mount";

	/**
	 * The namespace where all new pods/ services and deployments are added. A way to get solely all elements involved of detection.
	 */
	static final String NAMESPACE = "detector";
	
	/**
	 * The application api used for registration 
	 */
	private AppsV1Api appApi;
	
	/**
	 * The core api used for registration.
	 */
	private CoreV1Api api;

	/**
	 * Initializes the registry by setting the api and appApi clients.
	 * @param client the kubernetes client class to get the corresponding apis
	 */
	KubDetectorRegistry(KubClient client) {
		api = client.getApi();
		appApi = client.getAppApi();
	}

	/**
	 * Registers a daemonset and a server pod with a service and a deployment in the kubernetes cluster
	 * @throws ApiException thrown if a creation/ deletion call to the kubernetes client fails
	 */
	void register() throws ApiException {
		this.deleteDaemonSet(); // reset all created kubernetes entities for multiple runs
		this.deleteCollector();
		this.deleteSecret();
		this.createNamespace();
		this.createSecret();
		Map<String, String> serverLabels = this.createServerDeployment();
		this.createServerService(serverLabels);
		this.createDaemonSet();
	}

	/**
	 * Creates the namespace where all new pods/ services and deployments are added. A way to get solely all elements involved of detection, if this namespace doesn't exist.
	 * @throws ApiException thrown if a creation call to the kubernetes client fails
	 */
	private void createNamespace() throws ApiException {
		V1NamespaceList namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null);
		boolean created = false;
		for (V1Namespace knamespace : namespaces.getItems()) {
			if (knamespace.getMetadata().getName().equals(NAMESPACE)) {
				created = true;
			}
		}
		if (!created) {
			V1Namespace kubnamespace = new V1Namespace();
			kubnamespace.setApiVersion("v1");
			kubnamespace.setKind("Namespace");
			V1ObjectMeta nameMeta = new V1ObjectMeta();
			nameMeta.setName(NAMESPACE);
			kubnamespace.setMetadata(nameMeta);
			api.createNamespace(kubnamespace, null, null, null);
		}
	}

	/**
	 * Creates the server pod by using a deployment.
	 * @return labels of the created pod to match the created pod with other kubernetes entities like a service
	 * @throws ApiException thrown if a creation call to the kubernetes client fails
	 */
	private Map<String, String> createServerDeployment() throws ApiException {
		V1Deployment appDeployment = new V1Deployment();
		appDeployment.setApiVersion("apps/v1");
		appDeployment.setKind("Deployment");
		V1ObjectMeta appDeploymentMeta = new V1ObjectMeta();
		appDeploymentMeta.setName(SERVER_DEPLOYMENT_NAME);
		appDeploymentMeta.setNamespace(NAMESPACE);
		appDeployment.setMetadata(appDeploymentMeta);

		V1DeploymentSpec depSpec = new V1DeploymentSpec();
		V1LabelSelector depSelector = new V1LabelSelector();

		Map<String, String> labels = new HashMap<>();
		labels.put("app", SERVER_DEPLOYMENT_NAME);
		depSelector.setMatchLabels(labels);
		depSpec.setSelector(depSelector);
		V1PodTemplateSpec depTemplate = new V1PodTemplateSpec();
		V1ObjectMeta depLabelMetadata = new V1ObjectMeta();
		depLabelMetadata.setLabels(labels);
		depTemplate.setMetadata(depLabelMetadata);
		V1PodSpec appDepSpec = new V1PodSpec();
		List<V1Container> appContainers = new ArrayList<>();
		V1Container appContainer = new V1Container();
		appContainer.setName(SERVER_DEPLOYMENT_NAME);
		appContainer.setImage("1ma2conn3trac/conntrack_reader:v0.843-server");
		List<V1ContainerPort> appPorts = new ArrayList<>();
		V1ContainerPort appPort = new V1ContainerPort();
		appPort.setContainerPort(PORT);
		appPorts.add(appPort);
		appContainer.setPorts(appPorts);
		appContainers.add(appContainer);
		appDepSpec.setContainers(appContainers);
		List<V1LocalObjectReference> imagePullSecrets = new ArrayList<>();
		V1LocalObjectReference secretRef = new V1LocalObjectReference();
		secretRef.setName(SECRET_NAME);
		imagePullSecrets.add(secretRef);
		appDepSpec.setImagePullSecrets(imagePullSecrets);
		depTemplate.setSpec(appDepSpec);
		depSpec.setTemplate(depTemplate);
		appDeployment.setSpec(depSpec);
		appApi.createNamespacedDeployment(NAMESPACE, appDeployment, null, null, null);
		return labels;
	}
	
	/**
	 * Creates a nodeport service for the server pod to communicate with the pod from other nodes.
	 * @param labels labels of the server pod to get the correct pod
	 * @throws ApiException
	 */
	private void createServerService(Map<String, String> labels) throws ApiException {
		V1Service appService = new V1Service();
		appService.setApiVersion("v1");
		appService.setKind("Service");
		V1ObjectMeta appServiceMeta = new V1ObjectMeta();
		appServiceMeta.setName(SERVER_SERVICE_NAME);
		appServiceMeta.setNamespace(NAMESPACE);
		appServiceMeta.setLabels(labels);
		appService.setMetadata(appServiceMeta);

		V1ServiceSpec appServiceSpec = new V1ServiceSpec();
		appServiceSpec.setType("NodePort");
		appServiceSpec.setSelector(labels);

		List<V1ServicePort> ports = new ArrayList<>();
		V1ServicePort appServicePort = new V1ServicePort();
		appServicePort.setProtocol("TCP");
		appServicePort.setPort(PORT);
		appServicePort.setNodePort(PORT);
		IntOrString port = new IntOrString(PORT);
		appServicePort.targetPort(port);
		ports.add(appServicePort);
		appServiceSpec.setPorts(ports);
		appService.setSpec(appServiceSpec);
		api.createNamespacedService(NAMESPACE, appService, null, null, null);
	}

	/**
	 * Creates a private secret for docker hub to be a private repository
	 * @throws ApiException thrown if a creation call to the kubernetes client fails
	 */
	private void createSecret() throws ApiException {
		V1Secret secret = new V1Secret();
		secret.setApiVersion("v1");
		secret.setKind("Secret");
		V1ObjectMeta secretMeta = new V1ObjectMeta();
		secretMeta.setName(SECRET_NAME);
		secretMeta.setNamespace(NAMESPACE);
		secret.setMetadata(secretMeta);
		secret.setType("kubernetes.io/dockerconfigjson");
		String dockercfg = "{\"auths\":{\"docker.io\":{\"username\":\"1ma2conn3trac\",\"password\":\"pw4con6trac1nma\",\"email\":\"netterdude@mail.de\",\"auth\":\"MW1hMmNvbm4zdHJhYzpwdzRjb242dHJhYzFubWE=\"}}}";
		Map<String, byte[]> data = new HashMap<>();
		data.put(".dockerconfigjson", dockercfg.getBytes());
		secret.setData(data);

		api.createNamespacedSecret(NAMESPACE, secret, null, null, null);
	}

	/**
	 * This method adds a daemon set, which inserts a detector-agent-pod into every node of the cluster.
	 *  The agent performs a connection detection and sends back the results to the main detector app
	 * @throws ApiException exception thrown if a daemon set can't be created1
	 */
	private void createDaemonSet() throws ApiException {
		V1DaemonSet daemonset = new V1DaemonSet();

		daemonset.setApiVersion("apps/v1");
		daemonset.setKind("DaemonSet");

		// set metadata of new daemonset
		V1ObjectMeta daemonMeta = new V1ObjectMeta();
		daemonMeta.setName(DAEMONSET_NAME);
		Map<String, String> daemonLabels = new HashMap<>();
		daemonLabels.put("name", DAEMONSET_NAME);
		daemonLabels.put("app", "pattern-detector");
		daemonMeta.setLabels(daemonLabels);
		daemonMeta.setNamespace(NAMESPACE);
		daemonset.setMetadata(daemonMeta);

		V1DaemonSetSpec daemonspec = new V1DaemonSetSpec();
		daemonspec.setMinReadySeconds(5); // specify that the daemonset is available min. 5 seconds
		V1DaemonSetUpdateStrategy updater = new V1DaemonSetUpdateStrategy();
		updater.setType("RollingUpdate");
		daemonspec.setUpdateStrategy(updater); // updates all agents with the rolling update strategy (see https://kubernetes.io/docs/tutorials/kubernetes-basics/update/update-intro/)

		V1LabelSelector daemonSelector = new V1LabelSelector();
		daemonSelector.setMatchLabels(daemonLabels);
		daemonspec.setSelector(daemonSelector);


		V1PodTemplateSpec daemonTemp = new V1PodTemplateSpec();
		V1ObjectMeta tempMeta = new V1ObjectMeta();
		tempMeta.setLabels(daemonLabels);
		daemonTemp.setMetadata(tempMeta);

		V1PodSpec conntrackSpec = new V1PodSpec();
		
		// create a volume for shared-data in the pod
		V1Volume volumesItem = new V1Volume();
		volumesItem.setName(VOLUME_NAME);
		V1EmptyDirVolumeSource emptyDir = new V1EmptyDirVolumeSource(); // volume existence depends on the pod
		volumesItem.setEmptyDir(emptyDir);
		conntrackSpec.addVolumesItem(volumesItem);
		
		List<V1Container> containers = new ArrayList<>();
		V1Container agent = this.createClientContainer();
		V1Container procspy = this.createProcspyContainer();
		containers.add(procspy);
		containers.add(agent);
		conntrackSpec.setContainers(containers);

		conntrackSpec.setDnsPolicy("ClusterFirstWithHostNet");
		conntrackSpec.setHostNetwork(true); // required for reaching the host from the app to make a http request
		conntrackSpec.setHostPID(true); // check if hostpid is required (offers hostpid to app)
		List<V1LocalObjectReference> imagePullSecrets = new ArrayList<>();
		V1LocalObjectReference secretRef = new V1LocalObjectReference();
		secretRef.setName(SECRET_NAME);
		imagePullSecrets.add(secretRef);
		conntrackSpec.setImagePullSecrets(imagePullSecrets);

		daemonTemp.setSpec(conntrackSpec);
		daemonspec.setTemplate(daemonTemp);
		daemonset.setSpec(daemonspec);

		appApi.createNamespacedDaemonSet(NAMESPACE, daemonset, null, null, null);

	}
	
	/**
	 * Creates a container for a conntrack client
	 * @return the container specification
	 */
	private V1Container createClientContainer() {
		V1Container agent = new V1Container();

		agent.setName("detector-agent");
		agent.setImage("1ma2conn3trac/conntrack_reader:v0.844-client");
		List<V1ContainerPort> agentPorts = new ArrayList<>();
		V1ContainerPort port = new V1ContainerPort();
		port.setContainerPort(PORT);
		port.setProtocol("TCP");
		agentPorts.add(port);
		agent.setPorts(agentPorts);
		V1EnvVar envItem = new V1EnvVar();
		envItem.setName("NODE_NAME");
		V1EnvVarSource valueFrom = new V1EnvVarSource();
		V1ObjectFieldSelector fieldRef = new V1ObjectFieldSelector();
		String fieldPath = "spec.nodeName"; // gets the node name of the node to send it as id to the server
		fieldRef.setFieldPath(fieldPath);
		valueFrom.setFieldRef(fieldRef);
		envItem.setValueFrom(valueFrom);
		agent.addEnvItem(envItem);
		
		// set volume to get data from procspy container (intra-pod communication)
		V1VolumeMount volumeMountsItem = new V1VolumeMount();
		volumeMountsItem.setName(VOLUME_NAME);
		volumeMountsItem.setMountPath("/data");
		agent.addVolumeMountsItem(volumeMountsItem);

		// set capabilities correct to allow netfilter an access to the network sockets
		V1SecurityContext securityContext = new V1SecurityContext();
		V1Capabilities capabilities = new V1Capabilities();
		List<String> capList = new ArrayList<>();
		capList.add("NET_ADMIN");
		capabilities.setAdd(capList);
		securityContext.setCapabilities(capabilities);
		agent.setSecurityContext(securityContext);
		return agent;
	}
	
	/**
	 * Creates a container for a procspy container
	 * @return the container specification
	 */
	private V1Container createProcspyContainer() {
		V1Container procspy = new V1Container();

		procspy.setName("procspy");
		procspy.setImage("1ma2conn3trac/conntrack_reader:v0.844-procspy");
		
		V1VolumeMount volumeMountsItem = new V1VolumeMount();
		volumeMountsItem.setName(VOLUME_NAME);
		volumeMountsItem.setMountPath("/data");
		procspy.addVolumeMountsItem(volumeMountsItem);
		return procspy;
	}

	/**
	 * Deletes the detector daemonset from the kubernetes cluster, if such a daemonset exists
	 * @throws ApiException exception thrown when the daemon can't delete the daemonset
	 */
	private void deleteDaemonSet() throws ApiException {
		V1DaemonSetList daemonsets = appApi.listNamespacedDaemonSet(NAMESPACE, null, null, null, null, null, null, null, null, null);
		for (V1DaemonSet daemonset : daemonsets.getItems()) {
			if (daemonset.getMetadata().getName().equals(DAEMONSET_NAME)) {
				appApi.deleteNamespacedDaemonSet(DAEMONSET_NAME, NAMESPACE, null, null, null, null, null, null);
			}
		}
	}

	/**
	 * Deletes the detector daemonset from the kubernetes cluster, if such a daemonset exists
	 * @throws ApiException exception thrown when the daemon can't delete the daemonset
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	/*	private void deleteNamespace(boolean error) throws ApiException, IOException, InterruptedException {
		V1NamespaceList namespaces = api.listNamespace(null, null, null, null, null, null, null, null, null);
		for (V1Namespace knamespace : namespaces.getItems()) {
			if (knamespace.getMetadata().getName().equals(namespace)) {
				Response response = api.deleteNamespaceCall(namespace, null, null, null, null, null, null, null).execute();
				TimeUnit.SECONDS.sleep(10); // time delay needed for deletion, otherwise a creation does not succeed

				if(!response.isSuccessful()) {// fix of kubernetes api issue, see https://github.com/kubernetes-client/java/issues/86
					throw new ApiException("Deletion of namespace failed. One reason might be given in https://github.com/kubernetes-client/java/issues/86.");
				}
			}
		}
	}*/

	/**
	 * Deletes the private secret for docker hub to be a private repository
	 * @throws ApiException thrown if a creation call to the kubernetes client fails
	 */
	private void deleteSecret() throws ApiException {
		V1SecretList secrets = api.listNamespacedSecret(NAMESPACE, null, null, null, null, null, null, null, null, null);
		for (V1Secret secret : secrets.getItems()) {
			if (secret.getMetadata().getName().equals(SECRET_NAME)) {
				api.deleteNamespacedSecret(SECRET_NAME, NAMESPACE, null, null, null, null, null, null);
			}
		}
	}

	/**
	 * Deletes the server pod with the deployment and the service from the kubernetes cluster
	 * @throws ApiException thrown if a creation call to the kubernetes client fails
	 */
	private void deleteCollector() throws ApiException {
		V1DeploymentList deployments = appApi.listNamespacedDeployment(NAMESPACE, null, null, null, null, null, null, null, null, null);
		for (V1Deployment deployment : deployments.getItems()) {
			if (deployment.getMetadata().getName().equals(SERVER_DEPLOYMENT_NAME)) {
				appApi.deleteNamespacedDeployment(SERVER_DEPLOYMENT_NAME, NAMESPACE, null, null, null, null, null, null);
			}
		}

		V1ServiceList services = api.listNamespacedService(NAMESPACE, null, null, null, null, null, null, null, null, null);
		for (V1Service service : services.getItems()) {
			if (service.getMetadata().getName().equals(SERVER_SERVICE_NAME)) {
				api.deleteNamespacedService(SERVER_SERVICE_NAME, NAMESPACE, null, null, null, null, null, null);
			}
		}
	}
}
