package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * This class creates a waiting scene to show while waiting for results. It is implemented as vbox
 * @author Alexis T. Bernhard
 *
 */
public class WaitingView extends Viewable {
	
	/**
	 * The content container of the scene.
	 */
	private VBox vBox;
	
	/**
	 * The label which indicates that the user needs to be patient
	 */
	private Label label;
	
	/**
	 * Gets the content container of the scene.
	 * @return the view box
	 */
	public VBox getVBox() {
		return vBox;
	}
	
	/**
	 * Gets the label which indicates that the user needs to be patient
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}
	
	/**
	 * Initializes the waiting scene by setting up the text and content of the label.
	 */
	WaitingView() {
		vBox = new VBox();
		vBox.setPadding(new Insets(5,5,5,5));
		label = new Label("Please wait a few seconds...");
		label.setWrapText(true);
		vBox.getChildren().add(label);
	}
}
