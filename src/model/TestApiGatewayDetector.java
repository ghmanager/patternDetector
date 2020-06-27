package model;

import java.util.ArrayList;
import java.util.List;

import controller.GraphException;
import controller.PatternType;
import javafx.util.Pair;

class TestApiGatewayDetector {

	List<ReadableGraph> detect(Graph graph) throws GraphException {
		List<Graph> graphBuf = new ArrayList<>();
		List<ReadableGraph> resGraphs = new ArrayList<>();
		List<Pair<Integer, Integer>> edgesBuffer = new ArrayList<>();
		double[][] edges = graph.getAdjacencyMatrix();
		for (int i = 0; i < edges.length; i++) {
			boolean pattern = false;
			for (int j = 0; j < edges[i].length; j++) {
				if (edges[i][j] == 1 && !pattern) {
					graphBuf.add(new Graph());
					Graph resGraph = graphBuf.get(graphBuf.size() - 1);
					resGraph.setPattern(PatternType.API_GATEWAY);
					ModifiableVertex vertex = graph.getVertexByIndex(i);
					vertex.setRole("apiGateway");
					vertex.setIndex(resGraph.getVertices().size());
					resGraph.addVertex(vertex);
					ModifiableVertex callee = graph.getVertexByIndex(j);
					callee.setRole("service");
					callee.setIndex(resGraph.getVertices().size());
					resGraph.addVertex(callee);
					edgesBuffer.add(new Pair<Integer, Integer>(vertex.getIndex(), callee.getIndex()));
					pattern = true;
				} else if (edges[i][j] == 1) {
					Graph resGraph = graphBuf.get(graphBuf.size() - 1);
					ModifiableVertex callee = graph.getVertexByIndex(j);
					callee.setRole("service");
					callee.setIndex(resGraph.getVertices().size());
					resGraph.addVertex(callee);
					edgesBuffer.add(new Pair<Integer, Integer>(resGraph.getVertexByName(graph.getVertexByIndex(i).getName()).getIndex(), callee.getIndex()));
				}
				if (pattern && j == edges[i].length - 1) {
					Graph resGraph = graphBuf.get(graphBuf.size() - 1);
					resGraph.generateAdjacencyMatrices();
					for (Pair<Integer, Integer> edge : edgesBuffer) {
						resGraph.addEdgeByIndex(edge.getKey(), edge.getValue(), 0);
					}
					edgesBuffer.clear();
					pattern = false;
					resGraphs.add(resGraph);
				}
			}
		}
		return resGraphs;
	}
}
