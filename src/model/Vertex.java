package model;

/**
 * This class represents a vertex as implementation of a modifiable vertex with a name, an index of the vertex in the graph vertex list and a role in a pattern instance
 * @author Alexis T. Bernhard
 *
 */
class Vertex implements ModifiableVertex {

	/**
	 * The unique name of the vertex
	 */
	protected String name;
	
	/**
	 * The unique index of the vertex in the graph vertex list
	 */
	protected int index;
	
	/**
	 * The role of the vertex in the graph: result of the graph matching algorithm
	 */
	protected String role;
	
	/**
	 * publicly gets the name of the vertex, used by the view
	 * @return the name of the vertex
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * publicly gets the index of the vertex in the corresponding vertex list, used by the view
	 * @return the index of the vertex in the corresponding vertex list
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the index of the vertex to a index of a graph
	 * @param role the resulting index of the vertex in the vertex list of the graph
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * publicly gets the role of the vertex in a graph, showed by the view/ defines a layer
	 * @return the role of the vertex in a graph
	 */
	public String getRole() {
		return role;
	}
	
	/**
	 * Sets the role of the vertex to a role determined by pattern matching
	 * @param role the resulting role of the vertex, determined by pattern matching
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	@SuppressWarnings("unused") // used to prevent calls to uninstantiated vertices
	private Vertex() {
		throw new AssertionError("The empty constructor should never be called.");
	}
	
	/**
	 * Initializes a new Vertex
	 * @param name the unique name of the vertex in the graph
	 * @param index the unique index of the vertex in the vertex list of the graph
	 */
	Vertex(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}
	
}
