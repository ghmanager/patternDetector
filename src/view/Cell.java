package view;

import controller.GraphException;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;

/**
 * This class represents a cell in the cellpane. A cell is initialized as label and represents a node of the underlying graph
 * @author Alexis T. Bernhard
 *
 */
public class Cell extends Label {

	/**
	 * The name of the cell, written as text of the label
	 */
	private String name;
	
	/**
	 * The role of the cell, represinting a certain role of the node in the graph, used for the layout to locate the cell
	 */
	private String role;

	/**
	 * Gets the unique name of the cell
	 * @return the unique cell name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * publicly gets the role of the cell
	 * @return the role of the cell
	 */
	public String getRole() {
		return role;
	}
	

	/**
	 * Initializes a cell by setting its name, its text, its color with a gradient and a border to draw a node with a rectangular border with rounded edges
	 * @param name the name of the cell, represented by an unique vertex name
	 * @param role the role of the cell, used by the layout to locate the position of the cell in the cellpane
	 * @throws GraphException thrown if the cell has the forbidden name null
	 */
	Cell(String name, String role) throws GraphException {
		if (name == null || role == null) {
			throw new GraphException("The input graph contains a node with the forbidden name and/ or roleName null.");
		}
		this.name = name;
		this.role = role;
		Stop[] stops = new Stop[] { new Stop(0, Color.LIGHTGREEN), new Stop(1, Color.GREEN)};
		LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
		
		this.setText(name);
		this.setWrapText(true);
		Tooltip tooltip = new Tooltip();
		tooltip.setText("Pattern role: " + this.role);
		this.setTooltip(tooltip);
		this.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(7), Insets.EMPTY)));
		this.setOpacity(0.9);
		this.setTextAlignment(TextAlignment.CENTER);
		this.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(7), BorderWidths.DEFAULT)));
		this.setPadding(new Insets(2, 5, 2, 5));
	}
}