package model;

/**
 * This interface gives read access to a vertex with a name, a role and an index.
 * @author Alexis T. Bernhard
 *
 */
public interface ReadableVertex extends Cloneable {

	/**
	 * Gets the name of the vertex
	 * @return the name of the vertex
	 */
	public String getName();
	
	/**
	 * Gets the index of the vertex in the adjacency matrix
	 * @return the index of the vertex in the adjacency matrix
	 */
	public int getIndex();
	
	/**
	 * publicly gets the role of the vertex in a graph, showed by the view/ defines a layer
	 * @return the role of the vertex in a graph
	 */
	public String getRole();
}
