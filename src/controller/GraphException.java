package controller;

/**
 * A graph exception is an exception to signalize an error while building or reading the internal graph.
 * @author Alexis T. Bernhard
 *
 */
public class GraphException extends Exception {

	/**
	 * The generated version id for a GraphException
	 */
	private static final long serialVersionUID = -923901146971928599L;	

	/**
	 * Initializes a new GraphException with null as exception message.
	 */
	public GraphException() {
		super();
	}
	
	/**
	 * Initializes a new GraphException and set its message to the input message.
	 * @param message the message to be shown for exception handling
	 */
	public GraphException(String message) {
		super(message);
	}
	
}
