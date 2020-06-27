package view;

import java.util.ArrayList;
import java.util.List;

import controller.GraphException;
import javafx.scene.layout.Pane;
import model.ReadableGraph;
import model.ReadableVertex;

/**
 * This class represents the cellpane of a graph scene as pane, every cellpane draws one underlying graph instance 
 * @author Alexis T. Bernhard
 *
 */
public class CellPane extends Pane {

	/**
	 * A list of all cells of the cell pane, every cell represents a node of the underlying graph.
	 */
	private List<Cell> cells;

	/**
	 * A list of edges of the cell pane, every edge represents an edge in the cell pane between two nodes.
	 */
	private List<GraphEdge> edges;

	/**
	 * The graph connected to the cell pane, which should be drawn.
	 */
	private ReadableGraph graph;

	/**
	 * Gets the graph connected to the cell pane, which should be drawn.
	 * @return the graph
	 */
	ReadableGraph getGraph() {
		return graph;
	}

	/**
	 * Initializes all contents, including the contained cells and lines and the zoom pane
	 */
	CellPane(ReadableGraph graph) {
		this.graph = graph;
		cells = new ArrayList<>();
		edges = new ArrayList<>();
	}

	/**
	 * Gets all cells of the cell pane
	 * @return the list of arrows in a cell pane
	 */
	List<Cell> getCells() {
		return cells;
	}

	/**
	 * Gets a specified cell through an index 
	 * @param index defines a cell through a position in the inner cell list, this needs to be equal to the position in the vertex list
	 * @return the cell at the index of the internal cell list specified or null for an invalid input index
	 */
	Cell getCellByIndex(int index) {
		if (index < 0 || index >= cells.size()) {
			return null;
		} else {
			return cells.get(index);
		}
	}

	/**
	 * Gets all arrows of the cell pane
	 * @return the list of arrows in a cell pane
	 */
	List<GraphEdge> getLines() {
		return edges;
	}

	/**
	 * Gets an arrow through an index
	 * @param index defines a arrow through a position in the inner arrow list
	 * @return the arrow of a given graph edge by an index or null for an invalid input index
	 */
	GraphEdge getLineByIndex(int index) {
		if (index < 0 || index >= edges.size()) {
			return null;
		} else {
			return edges.get(index);
		}
	}

	/**
	 * Adds a cell to the cell pane
	 * @param vertex the vertex which should be added as cell
	 * @return true, if the operation succeeded, false 
	 * @throws GraphException thrown if the added vertex has invalid attributes or is null
	 */
	void addCell(ReadableVertex vertex) throws GraphException {
		if (vertex != null) {
			Cell cell = new Cell(vertex.getName(), vertex.getRole());
			cells.add(cell);
			this.getChildren().add(cell);
		} else {
			throw new GraphException("A vertex of an output graph is null.");
		}
	}

	/**
	 * Removes a cell from the cell pane 
	 * @param vertex the vertex which need to be removed
	 * @return true, if the operation succeeded, false otherwise 
	 * @throws GraphException thrown if the vertex to remove has invalid attributes or is null
	 */
	void removeCell(ReadableVertex vertex) throws GraphException {
		boolean successful = false;
		if (vertex != null) {
			for (Cell cell : cells) {
				if (cell.getName().equals(vertex.getName())) {
					successful = cells.remove(cell);
					this.getChildren().remove(cell);
					for (GraphEdge edge : edges) { // delete all connected edges
						if (edge.getSource().getName().equals(cell.getName()) ||
								edge.getTarget().getName().equals(cell.getName())) {
							this.removeEdge(edge);
						}
					}
				}
			}
		}
		if (!successful) {
			throw new GraphException("A vertex to remove from the view has invalid attributes or is null.");
		}
	}

	/**
	 * Adds an arrow to the cell pane
	 * @param source the starting point of the cell pane
	 * @param target the target point of the cell pane
	 * @return true, if the operation succeeded, false otherwise 
	 * @throws GraphException thrown if an exception in locating the arrow occurs
	 */
	boolean addEdge(Cell source, Cell target) throws GraphException {
		boolean successful = false;
		if (source != null && target != null) {
			GraphEdge arrow = new GraphEdge(source, target, null, 4);
			edges.add(arrow);
			this.getChildren().add(arrow);
			successful = true;
		}
		return successful;
	}

	/**
	 * Removes an arrow from the cell pane
	 * @param edge the graph edge to remove
	 * @return true, if the operation succeeded, false otherwise 
	 */
	void removeEdge(GraphEdge edge) {
		edges.remove(edge);
		this.getChildren().remove(edge);
	}
}
