package model;

import controller.GraphException;
import controller.PatternType;

public class ScatterGather extends Pattern {

	/**
	 * Initializes a new scatter gather pattern instance by setting a new graph.
	 */
	ScatterGather() {
		this.pGraph = new Graph();
		this.patternType = PatternType.SCATTER_GATHER;
		this.pGraph.setPattern(this.patternType);
	}
	
	@Override
	void createPattern() throws GraphException {
		Vertex root = new Vertex("Root Container", 0);
		root.setRole(PatternType.SCATTER_GATHER.getRoles()[0]);
		pGraph.addVertex(root);
		Vertex client1 = new Vertex("Client", 1);
		client1.setRole(PatternType.SCATTER_GATHER.getRoles()[1]);
		pGraph.addVertex(client1);
		Vertex child = new Vertex("Child Container", 2);
		Vertex child2 = new Vertex("Child Container2", 3);
		Vertex child3 = new Vertex("Child Container3", 4);
		child.setRole(PatternType.SCATTER_GATHER.getRoles()[2]);
		child2.setRole(PatternType.SCATTER_GATHER.getRoles()[2]);
		child3.setRole(PatternType.SCATTER_GATHER.getRoles()[2]);
		pGraph.addVertex(child);
		pGraph.addVertex(child2);
		pGraph.addVertex(child3);
		
		pGraph.generateAdjacencyMatrices();
		
		pGraph.addEdge(client1, root, 0);
		pGraph.addEdge(root, child, 0);
		pGraph.addEdge(root, child2, 0);
		pGraph.addEdge(root, child3, 0);
	}

}
