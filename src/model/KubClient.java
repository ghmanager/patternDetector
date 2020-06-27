package model;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.InputType;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;

/**
 * This class represents the connection to the official kubernetes java client and supports different required getting and setting operations.
 * @author Alexis T. Benrhard
 *
 */
class KubClient {

	/**
	 * An array of strings which contain data to authenticate to the kubernetes cluster for the kubernetes client.
	 */
	private String[] authdata;

	/**
	 * A map containing information about pods of the kubernetes cluster, it maps a pod id (made out of the pod ip (veth0) and the node name) to the pod name.
	 */
	private Map<String, String> pods;

	/**
	 * A list of service of the kubernetes cluster.
	 */
	private List<String> services;

	/**
	 * A list of endpoints in the kubernetes cluster. An endpoint is the connection between a service and a pod.
	 */
	private List<List<String>> ends;

	/**
	 * The api to query core requests to the kubernetes client
	 */
	private CoreV1Api api;

	/**
	 * The api to query creation and deletion requests to the kubernetes client
	 */
	private AppsV1Api appApi;

	/**
	 * The method to authenticate to the kubernetes cluster.
	 */
	private InputType authmethod;

	/**
	 * Gets an array of strings which contain data to authenticate to the kubernetes cluster for the kubernetes client
	 * @return an array of authentication data
	 */
	String[] getAuthenticationData() {
		return authdata;
	}

	/**
	 * Gets the api to query core requests to the kubernetes client
	 * @return the kubernetes client core api 
	 */
	CoreV1Api getApi() {
		return api;
	}

	/**
	 * Gets the api to query creation and deletion requests to the kubernetes client
	 * @return the kubernetes client application api
	 */
	AppsV1Api getAppApi() {
		return appApi;
	}

	/**
	 * Sets the whole array of authentication to the given input. the array contains data to authenticate to the kubernetes cluster for the kubernetes client.
	 * @param authdata the authentication data to set
	 */
	void setAuthenticationData(String[] authdata) {
		this.authdata = authdata;
	}

	/**
	 * Sets the method to authenticate to the kubernetes cluster to the input method
	 * @param authmethod the authentication method to set
	 */
	void setAuthmethod(InputType authmethod) {
		this.authmethod = authmethod;
	}
	
	/**
	 * Gets infos of all pods of the default namespace.
	 * @return A map containing information about pods of the kubernetes cluster, it maps a pod id (made out of the pod ip (veth0) and the node name) to the pod name.
	 * @throws ApiException thrown if the list call to the kubernetes client fails
	 */
	Map<String, String> getPodList() throws ApiException {
		if (!pods.isEmpty()) {
			pods.clear();
		}
		V1PodList podList = api.listNamespacedPod("default", null, null, null, null, null, null, null, null, null);
		for (V1Pod pod : podList.getItems()) {
			pods.put(pod.getStatus().getPodIP() + "#" + pod.getSpec().getNodeName(), pod.getMetadata().getName());
		}
		return pods;
	}

	/**
	 * Gets infos of all services of the default namespace.
	 * @return A list of service of the kubernetes cluster.
	 * @throws ApiException thrown if the list call to the kubernetes client fails
	 */
	List<String> getServiceList() throws ApiException {
		if (!services.isEmpty()) {
			services.clear();
		}
		V1ServiceList serviceList = api.listNamespacedService("default", null, null, null, null, null, null, null, null, null);
		for (V1Service service : serviceList.getItems()) {
			if (!service.getMetadata().getName().equals("kubernetes"))
				services.add(service.getMetadata().getName());
		}
		return services;
	}

	/**
	 * Gets a list of endpoints in the kubernetes cluster. An endpoint is the connection between a service and a pod.
	 * @return the list of endpoints
	 * @throws ApiException thrown if the list call to the kubernetes client fails
	 */
	List<List<String>> getEndpointList() throws ApiException {
		if (!ends.isEmpty()) {
			ends.clear();
		}
		V1EndpointsList endpointList = api.listEndpointsForAllNamespaces(null, null, null, null, null, null, null, null, null);

		for (V1Endpoints end : endpointList.getItems()) {

			// merges services and endpoints and filters the kubernetes service
			boolean found = false; // indicates that an endpoint is a service
			for (String service : services) {
				if (service.equals(end.getMetadata().getName()) &&
						!service.equals("kubernetes") &&
						!service.equals("kube-dns") &&
						!service.equals("dashboard-metrics-scraper") &&
						!service.equals("kubernetes-dashboard"))
					found = true;
			}

			if (found && end.getSubsets() != null) {
				for (V1EndpointSubset subset : end.getSubsets()) {
					if (subset != null) {
						for (V1EndpointAddress address : subset.getAddresses()) {
							List<String> endpoint = new ArrayList<>();
							endpoint.add(end.getMetadata().getName());
							endpoint.add(address.getTargetRef().getName());
							ends.add(endpoint);
						}
					}
				}
			}
		}
		return ends;
	}

	/**
	 * Starts the connection to the kubernetes client with the authentication method and data. Initializes the internal apis and the internal lists
	 * @throws IOException thrown if no authentication method and data is inserted.
	 * @throws ApiException thrown if the list call to the kubernetes client fails.
	 */
	void start() throws IOException, ApiException {
		ApiClient client;
		switch(authmethod) {
		case CONFIG: client = Config.fromConfig(authdata[0]); break;
		case URL: client = Config.fromUrl(authdata[0], Boolean.getBoolean(authdata[1])); break;
		case TOKEN: client = Config.fromToken(authdata[0], authdata[1]); break;
		case USERPASSWORD: client = Config.fromUserPassword(authdata[0], authdata[1], authdata[2], Boolean.getBoolean(authdata[3])); break;
		default: throw new IOException();
		}

		// set the global default api-client to the in-cluster one from above
		Configuration.setDefaultApiClient(client);

		// the CoreV1Api loads default api-client from global configuration. 
		api = new CoreV1Api();
		appApi = new AppsV1Api();
		pods = new HashMap<>();
		services = new ArrayList<>();
		ends = new ArrayList<>();

		client.setDebugging(true);
	}
}
