package model;

import java.util.List;

import controller.GraphException;
import controller.PatternType;

/**
 * This interface gives a read access to a graph with readable vertices and an adjacency matrix for edges between them.
 * @author Alexis T. Bernhard
 *
 */
public interface ReadableGraph {
	
	/**
	 * Gets all vertices of graph
	 * @return a list of vertex Objects
	 */
	public List<? extends ReadableVertex> getVertices();

	/**
	 * Gets a vertex of the graph by the specified name
	 * @param name the name vertex to find in the graph
	 * @return the desired Vertex
	 */
	public ReadableVertex getVertexByName(String name);
	
	/**
	 * Gets a vertex of the graph by the specified index from the vertex list
	 * @param index the index of the vertex in the vertex list of the graph 
	 * @return the desired vertex or null if the index is out of range of the vertex list
	 */
	public ReadableVertex getVertexByIndex(int index);
	
	/**
	 * Gets the adjacency matrix of the graph containing all edges
	 * @return the adjacency matrix
	 */
	public double[][] getAdjacencyMatrix();
	
	/**
	 * Gets the type of connection in the adjacency matrix between vertex1 and vertex 2 
	 * @param vertex1 the index in the vertex list of the first vertex, the order of the vertices does not matter
	 * @param vertex2 the index in the vertex list of the second vertex, the order of the vertices does not matter
	 * @return -1: undefined vertex indices; 0: no connection; 1: directed edge from v1 to v2; 2: directed edge from v2 to v1; 3: undirected edge (both directions) between v1 and v2
	 */
	public int getConnectionByIndex(int vertex1, int vertex2);
	
	/**
	 * Gets the pattern which was applied for this graph
	 * @return the applied pattern of this graph, null for the initial full graph
	 */
	public PatternType getPattern();
	


	/**
	 * Gets an array of role appearances to store how many nodes with a certain role are part of the graph.
	 * @return the array of role appearances
	 */
	public int[] getAllRoleAppearances();

	/**
	 * Gets the number of role appearances by a given role
	 * @param role the role for which to get the appearances
	 * @return the role appearances or -1 if the role does not exist
	 */
	public int getRoleAppearance(String role);
	
	/**
	 * Gets the number of role appearances by a given index
	 * @param index the index of the pattern role in the role list
	 * @return the role appearances or -1 if the role does not exist
	 * @throws GraphException thrown if the requested index is out of bounds
	 */
	public int getRoleAppearance(int index) throws GraphException;
}
