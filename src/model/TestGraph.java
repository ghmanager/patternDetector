package model;

import controller.GraphException;

public class TestGraph {
	
	/**
	 * The general graph representing the pattern
	 */
	protected Graph graph;
	
	/**
	 * Returns the graph representing the pattern
	 * @return the graph
	 */
	Graph getGraph() {
		return graph;
	}
	
	TestGraph() throws GraphException {
		this.graph = new Graph();
		
		Vertex apiGateway = new Vertex("api-gateway", 0);
		
		Vertex discovery = new Vertex("discovery-service", 1);
		Vertex clientau = new Vertex("client-author", 2);
		Vertex article = new Vertex("article-service", 3);
		Vertex articleg1 = new Vertex("article-gatherer1", 4);
		Vertex articleg2 = new Vertex("article-gatherer2", 5);
		Vertex clientar = new Vertex("client-article", 6);
		Vertex author1 = new Vertex("author-service1", 7);
		Vertex author2 = new Vertex("author-service2", 8);
		Vertex author3 = new Vertex("author-service3", 9);
		graph.addVertex(apiGateway);
		graph.addVertex(discovery);
		graph.addVertex(article);
		graph.addVertex(articleg1);
		graph.addVertex(articleg2);
		graph.addVertex(clientau);
		graph.addVertex(clientar);
		graph.addVertex(author1);
		graph.addVertex(author2);
		graph.addVertex(author3);
		
		graph.generateAdjacencyMatrices();
		
		graph.addEdge(clientau, apiGateway, 0);
		graph.addEdge(clientar, apiGateway, 0);
		graph.addEdge(apiGateway, discovery, 0);
		graph.addEdge(apiGateway, author1, 0);
//		graph.addEdge(apiGateway, author2);
//		graph.addEdge(apiGateway, author3);
		graph.addEdge(author1, author2, 1);
		graph.addEdge(author1, author3, 1);
		graph.addEdge(apiGateway, article, 0);
		graph.addEdge(article, articleg1, 0);
		graph.addEdge(article, articleg2, 0);
	}
}
