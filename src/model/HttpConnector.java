package model;

import java.util.HashMap;
import java.util.Map;

import io.kubernetes.client.openapi.ApiException;

/**
 * This class handles the connection of the application to the installed client-server infrastructure in the cluster to receive all connections between pods via a http request.
 * Furthermore a transformation for all received connections is provided to transform the received ips (veth0s, NAT connections) into pod names. 
 * @author Alexis T. Bernhard
 *
 */
class HttpConnector {

	/**
	 * Gets all connections of all pods in the cluster by calling a server pod, which receives all connections from pods to other pods by using conntrack on every node.
	 * Afterwards the connections represented as ips are transformed to pod names.
	 * @param pods A map of pods which maps a combination of the pod ip (veth0) and the node name to the pod name.
	 * @param client The kubernetes client to connect to the cluster.
	 * @param install true if the kubernetes infrastructure needs to be installed in the cluster
	 * @return A map which maps a pod name to another pod name to represent a connection between them.
	 * @throws ApiException thrown if the connection to the server pod fails
	 * @throws InterruptedException thrown if a user interrupts the thread
	 */
	Map<String, String> getConnections(Map<String, String> pods, KubClient client, boolean install) throws ApiException, InterruptedException {
		HttpConntrackCallback cb = new HttpConntrackCallback();
		if (install)
			Thread.sleep(30000); // time required to install the client-server infrastructure in the cluster
		client.getApi().connectGetNamespacedServiceProxyWithPathAsync("collector-service", "detector", "print", null, cb);
		return transformConnections(pods, cb.getConnections());
	}

	/**
	 * Transforms a list of connections from ips to pod names by using the input pod map
	 * @param pods A map of pods which maps a combination of the pod ip (veth0) and the node name to the pod name.
	 * @param connections A map of connections which maps a node name on all its connections represented as ips in a string. Each connection stands as string in the value of the map in a new line.
	 * @return A map which maps a pod name to another pod name to represent a connection between them.
	 */
	private Map<String, String> transformConnections(Map<String, String> pods, Map<String, String> connections) {
		Map<String, String> edges = new HashMap<>();
		for (Map.Entry<String, String> connectionEntry : connections.entrySet()) {
			String[] nodeConnections = connectionEntry.getValue().split("\n");
			for (int i = 0; i < nodeConnections.length; i++) {
				String[] ips = nodeConnections[i].split("[= ]+");
				if (ips.length > 1) {
					String source = pods.get((ips[1] + "#" + connectionEntry.getKey()));
					if (source != null && ips.length > 3) { // filters unwanted pods like kube-system pods or unrelated pods
						String target = pods.get((ips[3] + "#" + connectionEntry.getKey()));
						if (target != null) {
							edges.put(source, target);
						}
					}
				}
			}
		}
		return edges;
	}
}