package controller;

import java.util.Optional;

import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import view.View;

/**
 * This class handles exceptions and errors by screening them on a dialog window and optionally quitting the application.
 * @author Alexis T. Bernhard
 *
 */
public class ExceptionHandler {

	/**
	 * Opens a dialog to display an exception for the end-user. An exception does not result in an error and the application is not exited.
	 * @param exMessage the exception message displayed for the user.
	 * @param view the view to open a dialog to display the exception
	 */
	public void handleException(String exMessage, View view) {
		if (exMessage != null) {
			view.getAppDialog().setHeaderText("Exception");
			view.getAppDialog().setContentText(exMessage);
			view.getAppDialog().setAlertType(AlertType.WARNING);
			view.getAppDialog().getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			view.getAppDialog().show();
		} else {
			this.handleError(exMessage, view);
		}
	}

	/**
	 * Opens a dialog to display an exception for the end-user. An error is fatal and leads to an exit of the program.
	 * @param exMessage the exception message displayed for the user.
	 * @param view the view to open a dialog to display the error.
	 */
	public void handleError(String exMessage, View view) {
		view.getAppDialog().setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		if (exMessage == null) {
			view.getAppDialog().setContentText("Unknown error detected. Please contact a developer.\nThe application will be stoped.");
		} else {
			view.getAppDialog().setContentText(exMessage + "\nThe application will be stoped.");
		}
		view.getAppDialog().setHeaderText("Error");
		view.getAppDialog().setAlertType(AlertType.ERROR);
		view.getAppDialog().getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		Optional<ButtonType> acceptance = view.getAppDialog().showAndWait();
		if (acceptance.isPresent()) {
			Platform.exit();
			System.exit(1);
		 }
	}
}