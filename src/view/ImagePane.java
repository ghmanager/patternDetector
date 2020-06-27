package view;

import java.util.ArrayList;
import java.util.List;

import controller.GraphException;
import controller.PatternType;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.ReadableGraph;
import model.ReadableVertex;

/**
 * A superclass for all cellpanes to control the switch between the cell panes.
 * @author Alexis T. Bernhard
 *
 */
public class ImagePane extends Pane {

	/**
	 * The current visible pane of the pane list.
	 */
	private int curPane;

	/**
	 * A list of all resulting cell panes of all graphs as results of the model.
	 */
	private List<CellPane> cellPanes;

	/**
	 * Gets a list of all resulting cell panes of all graphs as results of the model.
	 * @return the list of cell panes
	 */
	public List<CellPane> getCellPanes() {
		return cellPanes;
	}

	/**
	 * Gets the current visible pane of the pane list.
	 * @return the current pane by index
	 */
	public int getCurrentPaneIndex() {
		return curPane;
	}

	/**
	 * Creates a new image pane by opening the first pane and initializing the internal list of graph panes
	 */
	ImagePane() {
		this.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		cellPanes = new ArrayList<>();
		curPane = 0;
	}

	/**
	 * Initializes the graph list and designs the corresponding elements of the graph scene (the two buttons and the graph pane) and sets all vertexes in the cell pane into the scene.
	 * @param graphs a list of graphs to draw in the scene by setting a cell pane for every graph and adding this cell pane into the cell pane list.
	 * @throws GraphException thrown if an input graph contains misplaced constructions (e.g. null vertices)
	 */
	public void initGraphs(List<ReadableGraph> graphs) throws GraphException {
		cellPanes.clear();
		this.getChildren().clear();
		curPane = 0;

		for (ReadableGraph graph : graphs) {
			CellPane imagepane = new CellPane(graph);
			imagepane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			imagepane.managedProperty().bind(this.visibleProperty());
			imagepane.setVisible(false);
			this.getChildren().add(imagepane);
			for (ReadableVertex vertex : graph.getVertices()) {
				imagepane.addCell(vertex);
			}
			if (cellPanes.isEmpty()) {
				imagepane.setVisible(true);
			}
			cellPanes.add(imagepane);

			imagepane.minWidthProperty().bind(this.widthProperty()); // force the width and height properties of the image panes to enable access to the width, height and the bounds in the parent later
			imagepane.maxWidthProperty().bind(this.widthProperty());
			imagepane.minHeightProperty().bind(this.heightProperty());
			imagepane.maxHeightProperty().bind(this.heightProperty());
		}



	}

	/**
	 * Draw all graphs by making the layout of all cell panes and adding corresponding edges.
	 * @throws GraphException thrown if an input graph contains misplaced constructions (e.g. null vertices, wrong roles, ...)
	 */
	public void drawGraphs() throws GraphException {
		for (CellPane imgpane : cellPanes) {
			if (imgpane.getGraph().getPattern() == PatternType.API_GATEWAY) {
				Layout layout = new GatewayLayout(imgpane);
				layout.execute();
			} else if (imgpane.getGraph().getPattern() == PatternType.SCATTER_GATHER) {
				Layout layout = new ScatterGatherLayout(imgpane);
				layout.execute();
			} else {
				Layout layout = new LeaderElectionLayout(imgpane);
				layout.execute();
			}
			for (int i = 0; i < imgpane.getGraph().getVertices().size(); i++) {
				for (int j = 0; j < imgpane.getGraph().getVertices().size(); j++) {
					if (imgpane.getGraph().getAdjacencyMatrix()[i][j] == 1 && !imgpane.addEdge(imgpane.getCellByIndex(i), imgpane.getCellByIndex(j))) {
						throw new GraphException("One of the connected cells of an edge is null. This occurs while edge creation.");
					}
				}
			}
		}
	}

	/**
	 * Shows the next cell pane in the internal image pane list. This cell pane represents a graph.
	 */
	public void showNextPane() {
		if (curPane < cellPanes.size() && curPane >= 0) {
			cellPanes.get(curPane).setVisible(false);
			if (curPane == cellPanes.size() - 1) {
				curPane = 0;
			} else {
				curPane++;
			}
			cellPanes.get(curPane).setVisible(true);
		}
	}

	/**
	 * Shows the previous cell pane in the internal image pane list. This cell pane represents a graph.
	 */
	public void showPrevPane() {
		if (curPane < cellPanes.size() && curPane >= 0) {
			cellPanes.get(curPane).setVisible(false);
			if (curPane == 0) {
				curPane = cellPanes.size() - 1;
			} else {
				curPane--;
			}
			cellPanes.get(curPane).setVisible(true);
		}
	}

}
