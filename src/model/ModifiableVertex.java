package model;

/**
 * This interface extends a readable vertex by giving the option to modify the vertex by giving it a role and an index
 * @author Alexis T. Bernhard
 *
 */
interface ModifiableVertex extends ReadableVertex {

	/**
	 * Sets the role of the vertex to a role determined by pattern matching
	 * @param role the resulting role of the vertex, determined by pattern matching
	 */
	void setRole(String role);

	/**
	 * Sets the index of the vertex to a index of a graph
	 * @param role the resulting index of the vertex in the vertex list of the graph
	 */
	public void setIndex(int index);
}
