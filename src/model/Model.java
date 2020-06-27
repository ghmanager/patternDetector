package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.GraphException;
import controller.InputType;
import io.kubernetes.client.openapi.ApiException;

/**
 * This class serves as facade and as an entry point for all other classes in the model package.
 * @author Alexis T. Benrhard
 *
 */
public class Model {

	/**
	 * Mocks the kubernetes client, so no working kubernetes cluster is requiered, just used for testing
	 */
	private static final boolean MOCK = true;

	/**
	 * The unique instance of the kubernetes client, which provides an entrance to the general kubernetes client.
	 */
	private KubClient client;

	/**
	 * The instance of the authentication class, which handles the authentication of a user to a kubernetes cluster.
	 */
	private Authentication auth;

	/**
	 * The instance of the transistor class, which handles the transformation of a kubernetes cluster architecture to a graph.
	 */
	private Transformator trans;

	/**
	 * The instance of the graph resulting from a kubernetes cluster transformation and representing the full architecture of the cluster without any changes regarding pattern detection
	 */
	private Graph graph;

	/**
	 * The instance of the http connector class, which handles the connection of the application to the installed client-server infrastructure in the cluster to receive all connections between pods via a http request.
	 */
	private HttpConnector connector;

	/**
	 * A list of patterns used to detect the contained pattern in an existing graph of the kubernetes cluster architecture.
	 */
	private List<Pattern> patternList;

	/**
	 * The instance of the pattern detector class, which detects a list of pattern in a given graph.
	 */
	private PatternDetector detector;

	/**
	 * Gets the unique instance of the kubernetes client, which provides an entrance to the official kubernetes client.
	 * @return the kubernetes client
	 */
	KubClient getClient() {
		return client;
	}

	/**
	 * Initializes a model instance by initalizing all attributes.
	 */
	public Model() {
		client = new KubClient();
		auth = new Authentication();
		trans = new Transformator();
		graph = new Graph();
		connector = new HttpConnector();
		detector = new PatternDetector();

		patternList = new ArrayList<>();
	}

	/**
	 * Authenticates the user with the authentication method and data to the kubernetes cluster 
	 * @param method the method of authentication
	 * @param data the user inserted string array of the authentication data fitting to the authentication method
	 * @throws IOException thrown if no authentication method and data is inserted.
	 * @throws ApiException thrown if the list call to the kubernetes client fails.
	 */
	public void authenticate(InputType method, String[] data) throws IOException, ApiException {
		auth.authenticate(client, method, data);
	}

	/**
	 * Generates the result graphs by creating all patterns, building the graph of the kubernetes cluster architecture and detecting all pattern instances in this graph.
	 * @param install true if the kubernetes infrastructure needs to be installed in the cluster
	 * @return the list of result graphs representing instances of all detected patterns in the graph
	 * @throws ApiException thrown if the list call to the kubernetes client fails.
	 * @throws GraphException  thrown if an operation can't be performed on a graph
	 * @throws InterruptedException thrown if a user interrupts the thread
	 * @throws CloneNotSupportedException 
	 */
	public List<ReadableGraph> generateGraphs(boolean install) throws ApiException, GraphException, InterruptedException, CloneNotSupportedException {
		List<ReadableGraph> resGraphs = new ArrayList<>();
		if (install) {
			Pattern apiGateway = new ApiGateway();
			Pattern scattergather = new ScatterGather();
			Pattern leaderelection = new LeaderElection();
			leaderelection.createPattern();
			apiGateway.createPattern();
			scattergather.createPattern();
			patternList.add(apiGateway);
			patternList.add(scattergather);
			patternList.add(leaderelection);
		}
		if (MOCK) {
			if (install) {
				TestGraph test = new TestGraph();
				graph = test.getGraph();
				for (Pattern pattern : this.patternList) {
					resGraphs.addAll(detector.detect(graph, pattern, 0));
				}
			} else {
				graph.clear();
				trans.transform(client, graph, connector, install, MOCK);
				TestApiGatewayDetector testDetector = new TestApiGatewayDetector();
				resGraphs.addAll(testDetector.detect(graph));
			}
		} else {
			if (!install)
				graph.clear();
			trans.transform(client, graph, connector, install, MOCK);
			for (Pattern pattern : this.patternList) {
				resGraphs.addAll(detector.detect(graph, pattern, 0));
			}
		}
		return resGraphs;
	}
}
