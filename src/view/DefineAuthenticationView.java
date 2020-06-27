package view;

import controller.InputType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 
 * This class designs the user interface of choosing the method to authenticate by putting all elements in a vertical box
 * @author Alexis T. Bernhard
 *
 */
public class DefineAuthenticationView extends Viewable {
	
	/**
	 * The content container of the scene.
	 */
	private VBox vBox;

	/**
	 * The label to explain the methods presented in the combo box
	 */
	private Label label;
	
	/**
	 * The combo box containing all possible method to connect to the kubernetes cluster
	 */
	private ComboBox<InputType> combobox;
	
	/**
	 * The button to preceed to the next step (which is the authentication)
	 */
	private Button button;
	
	/**
	 * Gets the content container of the scene.
	 * @return the view box
	 */
	public VBox getVBox() {
		return vBox;
	}
	
	/**
	 * Gets the combobox, which contains all possible input types to connect to kubernetes
	 * @return the combobox of the scene
	 */
	public ComboBox<InputType> getComboBox() {
		return combobox;
	}
	
	/**
	 * Gets the next button, which should 
	 * @return the next button which calls the next scene: Authentication and defines the chosen input type through the combobox
	 */
	public Button getButton() {
		return button;
	}
	
	/**
	 * Gets the label to describe the combobox
	 * @return the label to the combobox
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Initializes this scene by instantiating and setting a label, a combobox and a next button
	 */
	DefineAuthenticationView() {
		vBox = new VBox();
		vBox.setPadding(new Insets(5,5,5,5));
		vBox.setSpacing(5);
		label = new Label("Choose your desired method of authentication:");
		label.setWrapText(true);
		vBox.getChildren().add(label);
		
		combobox = new ComboBox<>();
		for (InputType type : InputType.values())
			combobox.getItems().add(type);
		vBox.getChildren().add(combobox);

		VBox alignVBox = new VBox();
		alignVBox.setAlignment(Pos.BASELINE_CENTER);
		HBox alignHBox = new HBox();
		alignHBox.setAlignment(Pos.CENTER);
		
		button = new Button();
		button.setText("Next");
		button.setLayoutX(1000);
		alignHBox.getChildren().add(button);
		alignVBox.getChildren().add(alignHBox);
		alignVBox.setMaxHeight(Double.MAX_VALUE);
		VBox.setVgrow(alignVBox, Priority.ALWAYS);
		vBox.getChildren().add(alignVBox);
	}
}
