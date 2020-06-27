package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * This class designs the user interface of the resulting graphs found by the model, consisting of several elements grouped in a horizontal box
 * @author Alexis T. Bernhard
 *
 */
public class GraphView extends Viewable {

	/**
	 * The content container of the scene.
	 */
	private BorderPane pane;
	
	/**
	 * The pane to put on all resulting images of the input graphs.
	 */
	private ImagePane imgPane;
	
	/**
	 * The label for the header of the graph scene.
	 */
	private Label header;

	/**
	 * The left button, which indicates that the previous cell pane in the internal cell pane list should be visible.
	 */
	private Button left;

	/**
	 * The right button, which indicates that the next cell pane in the internal cell pane list should be visible.
	 */
	private Button right;

	/**
	 * The rerun button to rerun the search for result graphs without installing the kubernetes cluster again.
	 */
	private Button rerun;

	/**
	 * Gets the pane to put on all resulting images of the input graphs.
	 * @return the border pane
	 */
	public ImagePane getImgPane() {
		return imgPane;
	}
	
	/**
	 * Gets the label for the header of the graph scene.
	 * @return the label
	 */
	public Label getHeader() {
		return header;
	}

	/**
	 * Gets the content container of the scene.
	 * @return the border pane
	 */
	public BorderPane getPane() {
		return pane;
	}

	/**
	 * Gets the left button, which replaces the graph of the image pane with the next descending graph
	 * @return the left button
	 */
	public Button getButtonLeft() {
		return left;
	}

	/**
	 * Gets the right button, which replaces the graph of the image pane with the next ascending graph
	 * @return the right button
	 */
	public Button getButtonRight() {
		return right;
	}

	/**
	 * Gets the rerun button to rerun the search for result graphs without installing the kubernetes cluster again.
	 * @return the rerun button
	 */
	public Button getButtonRerun() {
		return rerun;
	}

	/**
	 * Initializes a graph scene by starting with an empty graph list with current pane 0 and creating some design parameters (like padding, spacing and alignment)
	 */
	GraphView() {

		pane = new BorderPane();
		pane.setPadding(new Insets(5,5,5,5));
		
		header = new Label("No graphs detected. Please try to rerun.");
		BorderPane.setAlignment(header, Pos.CENTER);
		header.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
		BorderPane.setMargin(header, new Insets(5,5,5,5));
		pane.setTop(header);

		left = new Button();
		left.setText("<");
		BorderPane.setAlignment(left, Pos.CENTER);
		pane.setLeft(left);
		
		imgPane = new ImagePane();
		pane.setCenter(imgPane);
		BorderPane.setMargin(imgPane, new Insets(5,5,5,5));

		right = new Button();
		right.setText(">");
		BorderPane.setAlignment(right, Pos.CENTER);
		pane.setRight(right);

		rerun = new Button();
		rerun.setText("Rerun");
		BorderPane.setAlignment(rerun, Pos.CENTER);
		rerun.setAlignment(Pos.CENTER);
		pane.setBottom(rerun);
	}
}
