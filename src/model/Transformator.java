package model;

import java.util.List;
import java.util.Map;

import controller.GraphException;
import io.kubernetes.client.openapi.ApiException;

/**
 * This class transforms a kubernetes cluster architecture to a graph.
 * @author Alexis T. Bernhard
 *
 */
class Transformator {
	
	/**
	 * Transforms the input system into a graph.
	 * @param client the kubernetes client class to connect to the kubernetes client
	 * @param graph the graph to be filled with vertices and edges
	 * @param connector the http connector to connect to the infrastructure server to get the edges of the graph
	 * @param install true if the kubernetes infrastructure needs to be installed in the cluster
	 * @throws ApiException thrown if the list call to the kubernetes client fails.
	 * @throws GraphException  thrown if an operation can't be performed on a graph
	 * @throws InterruptedException thrown if a user interrupts the thread
	 */
	void transform(KubClient client, Graph graph, HttpConnector connector, boolean install, boolean mock) throws ApiException, GraphException, InterruptedException {
		Map<String, String> pods = client.getPodList();
		int serviceIndex = graph.getVertices().size();
		int podIndex = 0;
		for (Map.Entry<String, String> pod : pods.entrySet()) {
			ModifiableVertex vertex = new Vertex(pod.getValue(), podIndex + serviceIndex);
			graph.addVertex(vertex);
			podIndex++;
		}
		serviceIndex = graph.getVertices().size();
		List<String> services = client.getServiceList();
		for (int i = 0; i < services.size(); i++) {
			ModifiableVertex vertex = new Vertex(services.get(i), i + serviceIndex);
			graph.addVertex(vertex);
		}
		if (install) {
			KubDetectorRegistry registry = new KubDetectorRegistry(client);
			registry.register();
		}
		graph.generateAdjacencyMatrices();
		if (mock) {
			List<List<String>> ends = client.getEndpointList();
			for (List<String> end : ends) {
				graph.addEdge(graph.getVertexByName(end.get(0)), graph.getVertexByName(end.get(1)), 0);
			}
		} else {
			Map<String, String> connections = connector.getConnections(pods, client, install);
			for (Map.Entry<String, String> connection : connections.entrySet()) {
				graph.addEdge(graph.getVertexByName(connection.getKey()), graph.getVertexByName(connection.getValue()), 0);
			}
		}
	}
}
