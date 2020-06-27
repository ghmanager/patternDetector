package model;

import controller.GraphException;
import controller.PatternType;

/**
 * This class implements the ApiGateway pattern by producing a graph which represents the pattern with three clients and three services.
 * @author Alexis T. Bernhard
 *
 */
class ApiGateway extends Pattern {

	/**
	 * Initializes a new ApiGateway pattern by setting a new graph.
	 */
	ApiGateway() {
		this.pGraph = new Graph();
		this.patternType = PatternType.API_GATEWAY;
		this.pGraph.setPattern(this.patternType);
	}

	@Override
	void createPattern() throws GraphException {
		Vertex apiGateway = new Vertex("ApiGateway", 0);
		apiGateway.setRole(PatternType.API_GATEWAY.getRoles()[0]);
		pGraph.addVertex(apiGateway);
		Vertex client1 = new Vertex("Client1", 1);
		Vertex client2 = new Vertex("Client2", 2);
		Vertex client3 = new Vertex("Client3", 3);
		client1.setRole(PatternType.API_GATEWAY.getRoles()[1]);
		client2.setRole(PatternType.API_GATEWAY.getRoles()[1]);
		client3.setRole(PatternType.API_GATEWAY.getRoles()[1]);
		pGraph.addVertex(client1);
		pGraph.addVertex(client2);
		pGraph.addVertex(client3);
		Vertex service1 = new Vertex("Service1", 4);
		Vertex service2 = new Vertex("Service2", 5);
		Vertex service3 = new Vertex("Service3", 6);
		service1.setRole(PatternType.API_GATEWAY.getRoles()[2]);
		service2.setRole(PatternType.API_GATEWAY.getRoles()[2]);
		service3.setRole(PatternType.API_GATEWAY.getRoles()[2]);
		pGraph.addVertex(service1);
		pGraph.addVertex(service2);
		pGraph.addVertex(service3);
		
		pGraph.generateAdjacencyMatrices();
		
		pGraph.addEdge(client1, apiGateway, 0);
		pGraph.addEdge(client2, apiGateway, 0);
		pGraph.addEdge(client3, apiGateway, 0);
		pGraph.addEdge(apiGateway, service1, 0);
		pGraph.addEdge(apiGateway, service2, 0);
		pGraph.addEdge(apiGateway, service3, 0);
	}
}
