package view;

import controller.GraphException;

/**
 * This abstract class defines and executes the locations of nodes of a graph
 * @author Alexis T. Bernhard
 *
 */
abstract class Layout {

	/**
	 * Represents the value of the padding between the CellPane border and the generation nodes
	 */
	protected static final double OFFSET = 4;
	
	/**
	 * The cell pane to work with
	 */
	protected CellPane pane;
	
	/**
	 * Applies a layout to all graphs of a graph scene
	 * @throws GraphException  thrown if the graph of the pane contains wrong roles to work with.
	 */
	abstract void execute() throws GraphException;
}
