package view;

import controller.GraphException;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * This class represents an edge of a graph and draws an arrow on the corresponding cell pane. The arrow is represented as a path. 
 * @author Alexis T. Bernhard
 *
 */
public class GraphEdge extends Path {

	/**
	 * The source cell, where the arrow starts
	 */
	private Cell source;

	/**
	 * The target cell, where the arrow ends
	 */
	private Cell target;

	/**
	 * The x coordinate of the starting point of the arrow on the cell pane
	 */
	private double xStart = 0;

	/**
	 * The x coordinate of the ending point of the arrow on the cell pane
	 */
	private double xEnd = 0;

	/**
	 * The y coordinate of the starting point of the arrow on the cell pane
	 */
	private double yStart = 0;

	/**
	 * The y coordinate of the ending point of the arrow on the cell pane
	 */
	private double yEnd = 0;

	/**
	 * gets the source cell of the arrow
	 * @return the source cell
	 */
	Cell getSource() {
		return source;
	}

	/**
	 * gets the target cell of the arrow
	 * @return the target cell
	 */
	Cell getTarget() {
		return target;
	}

	@SuppressWarnings("unused") // used to forbid a call without arguments
	private GraphEdge() {
		throw new AssertionError("The empty constructor should never be called.");
	}

	/**
	 * Creates a graph edge by drawing a filled arrow between the two given cells
	 * @param source the cell where the arrow starts
	 * @param target the cell where the arrow ends
	 * @param color the color of the arrow to draw, if null the arrow is black
	 * @param headSize the size of the head of the arrow, in comparison to the arrow
	 * @throws GraphException thrown if an exception in locating the arrow occurs
	 */
	GraphEdge(Cell source, Cell target, Color color, int headSize) throws GraphException {
		super();
		this.source = source;
		this.target = target;
		if (color == null) {
			color = Color.BLACK;
		}
		if (headSize <= 0) {
			headSize = 1;
		}

		this.strokeProperty().bind(fillProperty());
		this.setFill(color);

		this.locateArrow();

		this.getElements().add(new MoveTo(xStart, yStart));
		this.getElements().add(new LineTo(xEnd, yEnd));
		this.drawArrow(xStart, yStart, xEnd, yEnd, headSize);
	}

	/**
	 * locates the starting and ending points of an arrow, needs to be used before the usage of those points
	 * @throws GraphException thrown if an exception in locating the arrow occurs
	 */
	private void locateArrow() throws GraphException {
		Bounds srcBounds = source.getBoundsInParent();
		Bounds targetBounds = target.getBoundsInParent();

		// contains all differences in positions on every side
		double[] positions = {target.getLayoutX() - (source.getLayoutX() + srcBounds.getWidth()),
				source.getLayoutX() - (target.getLayoutX() + targetBounds.getWidth()),
				target.getLayoutY() - (source.getLayoutY() + srcBounds.getHeight()),
				source.getLayoutY() - (target.getLayoutY() + targetBounds.getHeight())};

		// chooses the side for drawing an arrow by getting the maximal difference
		int max = this.argmax(positions);

		// sets the arrow to the side given by argmax
		switch(max) {
		case 0: // right arrow
			if (positions[max] > 0) { // makes overlaps look better
				xStart = source.getLayoutX() + srcBounds.getWidth();
			} else {
				xStart = source.getLayoutX();
			}
			yStart = source.getLayoutY() + srcBounds.getHeight() / 2;
			xEnd = target.getLayoutX();
			yEnd = target.getLayoutY() + targetBounds.getHeight() / 2;
			break;
		case 1: // left arrow
			if (positions[max] > 0) {
				xStart = source.getLayoutX();
			} else {
				xStart = source.getLayoutX() + srcBounds.getWidth();
			}
			yStart = source.getLayoutY() + srcBounds.getHeight() / 2;
			xEnd = target.getLayoutX() + targetBounds.getWidth();
			yEnd = target.getLayoutY() + targetBounds.getHeight() / 2;
			break;
		case 2: // downwards arrow
			if (positions[max] > 0) { // makes overlaps look better
				yStart = source.getLayoutY() + srcBounds.getHeight();
			} else {
				yStart = source.getLayoutY();
			}
			xStart = srcBounds.getMinX() + srcBounds.getWidth() / 2;
			xEnd = targetBounds.getMinX() + targetBounds.getWidth() / 2;
			yEnd = target.getLayoutY();
			break;
		case 3: //upwards arrow
			if (positions[max] > 0) { // makes overlaps look better
				yStart = source.getLayoutY();
			} else {
				yStart = source.getLayoutY() + srcBounds.getHeight();
			}
			xStart = srcBounds.getMinX() + srcBounds.getWidth() / 2;
			yStart = source.getLayoutY();
			xEnd = targetBounds.getMinX() + targetBounds.getWidth() / 2;
			yEnd = target.getLayoutY() + targetBounds.getHeight();
			break;
		default:
			throw new GraphException("No shortest edge to place the edge on the vertex could be estimated. " + max + "is not in the range between 0-3 for all vertex sides.");
		}
	}

	/**
	 * Moves in a circle to add endpoints for the triangle to define the arrow head
	 * @param xStart the starting x-value, where the arrow starts
	 * @param yStart the starting y-value, where the arrow starts
	 * @param xEnd the ending x-value of the arrow (of the arrowhead)
	 * @param yEnd the ending y-value of the arrow = (of the arrowhead)
	 * @param headSize the size of the arrowhead
	 */
	private void drawArrow(double xStart, double yStart, double xEnd, double yEnd, double headSize) {
		//ArrowHead
		double angle = Math.atan2((yEnd - yStart), (xEnd - xStart)) - Math.PI / 2.0;
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		//point1
		double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * headSize + xEnd;
		double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * headSize + yEnd;

		//point2
		double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * headSize + xEnd;
		double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * headSize + yEnd;

		this.getElements().add(new LineTo(x1, y1));
		this.getElements().add(new LineTo(x2, y2));
		this.getElements().add(new LineTo(xEnd, yEnd));
	}

	/**
	 * calculates the maximum of a given array
	 * @param diffs the array to define the maximum 
	 * @return the index to the maximum of the array
	 */
	private int argmax(double[] diffs)
	{
		int bestIdx = -1;
		double maxDiff = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < diffs.length; i++) {
			double diff = diffs[i];
			if (diff > maxDiff) {
				maxDiff = diff;
				bestIdx = i;
			}
		}
		return bestIdx;
	}
}
