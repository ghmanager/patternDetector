package view;

import javafx.scene.control.Alert;

/**
 * This class opens an alert to use it as interface for exception handling.
 * @author Alexis T. Bernhard
 *
 */
public class AppDialog extends Alert {

	/**
	 * Initializes the alert with a null error as default setting. However, those default settings should be overritten by a caller.
	 */
	public AppDialog() {
		super(AlertType.ERROR);
		this.setHeaderText("Unknown Error.");
		this.setTitle("Error");
	}
}
