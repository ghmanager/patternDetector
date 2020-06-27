package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * This class designs the user interface of inserting user data by putting all elements in a vertical box
 * @author Alexis T. Bernhard
 *
 */
public class AuthenticationView extends Viewable {
	
	/**
	 * The content container of the scene.
	 */
	private VBox vBox;
	
	/**
	 * The finish button
	 */
	private Button button;

	/**
	 * The button to choose a directory from a javafx DirectoryChooser dialog.
	 */
	private Button chooseDirectory;

	/**
	 * The array containing all input textfields, where users can enter data.
	 */
	private TextField[] fields;

	/**
	 * The array containing all labels used in this layout to explain the connected textfields.
	 */
	private Label[] labels;

	/**
	 * The checkbox to describe whether to enable an SSL (Secure Sockets Layer) encryption, otherwise TLS (Transport Layer Security) is used
	 */
	private CheckBox sslCheck;
	
	/**
	 * Gets the content container of the scene.
	 * @return the view box
	 */
	public VBox getVBox() {
		return vBox;
	}

	/**
	 * Gets the array containing all input textfields, where users can enter data (set in setInputLayout).
	 * @return the array of textfields
	 */
	public TextField[] getTextFields() {
		return fields;
	}

	/**
	 * Gets the finish button to stop entering data in the textfields and close this user interface (scene).
	 * @return the finsih button
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Gets the button to choose a directory from a javafx DirectoryChooser dialog.
	 * @return the directory chooser button
	 */
	public Button getChooseDirectory() {
		return chooseDirectory;
	}

	/**
	 * Gets the array containing all labels used in this layout to explain the connected textfields (set in setInputLayout).
	 * @return the array of labels
	 */
	public Label[] getLabels() {
		return labels;
	}

	/**
	 * Gets the checkbox to describe whether to enable an SSL (Secure Sockets Layer) encryption, otherwise TLS (Transport Layer Security) is used
	 * @return the checkbox
	 */
	public CheckBox getSSLCheckBox() {
		return sslCheck;
	}

	/**
	 * Initializes the authentication scene. Set its design (including padding, spacing and adding alignment boxes) and adding the finish button.
	 * Warning: the labels and textfields are initialzed and set in the setInputLayout method (they are just initialized as null)!
	 */
	AuthenticationView() {
		vBox = new VBox();
		vBox.setPadding(new Insets(5,5,5,5));
		vBox.setSpacing(5);
		labels = null;
		sslCheck = null;
		fields = null;
		chooseDirectory = null;

		VBox alignVBox = new VBox();
		alignVBox.setAlignment(Pos.BASELINE_CENTER);
		HBox alignHBox = new HBox();
		alignHBox.setAlignment(Pos.CENTER);

		button = new Button();
		button.setText("Finish");
		alignHBox.getChildren().add(button);
		alignVBox.getChildren().add(alignHBox);
		vBox.getChildren().add(alignVBox);
	}

	/**
	 * This method adds the textfields and labels as top elements of the scene. The labels wrap their size to the text length of their text.
	 * Hint: The text isn't specified and must be set by the caller. 2nd: The order of added elements is labels[0], fields[0], labels[1], fields[1], ...
	 * @param numFields the fixed number of text fields (sets the number of labels to the same size)
	 */
	void setInputLayout(int numFields, boolean directoryChoosing) {
		if (numFields > 0) {
			fields = new TextField[numFields];
			labels = new Label[numFields];
			for (int i = 0; i < 2 * numFields; i++) {
				if (i % 2 == 0) {
					Label label = new Label();
					label.setWrapText(true);
					labels[i / 2] = label;
					vBox.getChildren().add(i, label);
				} else {
					TextField field = new TextField();
					fields[i / 2] = field;
					if ((i + 1 >= 2 * numFields) && directoryChoosing) {
						HBox inlineBox = new HBox();
						inlineBox.setSpacing(5);
						HBox.setHgrow(field, Priority.ALWAYS);
						inlineBox.getChildren().add(field);
						createDirectoryChooserButton();
						inlineBox.getChildren().add(chooseDirectory);
						vBox.getChildren().add(i, inlineBox);
					} else {
						vBox.getChildren().add(i, field);
					}
				}
			}
		}
	}

	/**
	 * Creates a button to enable choosing a file from the file system
	 */
	void createDirectoryChooserButton() {
		chooseDirectory = new Button();
		chooseDirectory.setText("+");
		Tooltip explanation = new Tooltip();
		explanation.setText("Opens the file system to choose the directory of your config file.");
		chooseDirectory.setTooltip(explanation);
	}

	/**
	 * This method adds the checkbox as second last element (before the finish button) to the scene ands a text to it
	 */
	void validateSSL() {
		sslCheck = new CheckBox("Enable SSL Encryption");
		vBox.getChildren().add(vBox.getChildren().size() - 1, sslCheck);
	}
}
