package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.kubernetes.client.openapi.ApiException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Model;
import model.ReadableGraph;
import view.AuthenticationView;
import view.DefineAuthenticationView;
import view.GraphView;
import view.View;
import view.WaitingView;

/**
 * Controls the application (incl. starting) and its control flow by using a javafx.
 * @author Alexis T. Bernhard
 *
 */
public class Controller extends Application {

	private static final String TITEL = "Detected pattern: ";

	/**
	 * The mainStage of the application, where all scenes are rendered.
	 */
	private Stage mainStage;

	/**
	 * The overall exception handler to handle all occurring exceptions of the application.- 
	 */
	private ExceptionHandler exHandler;

	/**
	 * The single facade model class to perform the main operations (see mvc pattern).
	 */
	private Model model;

	/**
	 * The single facade view class to display the application (see mvc pattern).
	 */
	private View view;

	/**
	 * Initializes the controller and inits the model and view.
	 */
	public Controller() {
		model = new Model();
		exHandler = new ExceptionHandler();
	}

	/**
	 * Initializes (launches) the javafx application.
	 */
	public void initialize() {
		launch();
	}

	@Override
	public void start(Stage detector) {
		detector.setTitle("Pattern Detector");
		detector.setResizable(false);

		Rectangle2D screen = Screen.getPrimary().getVisualBounds();

		detector.setMaxHeight(screen.getHeight());
		detector.setMaxWidth(screen.getWidth());

		mainStage = detector;
		view = new View();



		// inits first scene in the authentication process
		DefineAuthenticationView authscene = view.getDefineAuthenticationScene();
		authscene.getButton().setOnAction(actionEvent -> {
			if (!authscene.getComboBox().getSelectionModel().isEmpty()) {
				setAuthScene(authscene.getComboBox().getValue());
			}
		});
		authscene.createScene(authscene.getVBox());
		detector.setScene(authscene.getScene());

		detector.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		detector.show();
	}

	/**
	 * Switches to and handles the authentication data input screen.
	 * @param authmethod the desired method to authenticate the client to the kubernetes cluster
	 */
	private void setAuthScene(InputType authmethod) {
		boolean urlAuth = (authmethod != InputType.CONFIG);
		AuthenticationView auscene = view.getAuthenticationScene(authmethod.getAuthData().length, urlAuth);
		for (int i = 0; i < auscene.getLabels().length; i++) {
			auscene.getLabels()[i].setText("Please enter " + authmethod.getAuthData()[i] + " your cluster:");
		}
		if (!urlAuth && auscene.getChooseDirectory() != null) {
			auscene.getChooseDirectory().setOnAction(actionEvent -> {
				FileChooser fileChooser = new FileChooser();
				File selectedDirectory = fileChooser.showOpenDialog(mainStage);
				if (selectedDirectory != null && auscene.getTextFields().length > 0) {
					auscene.getTextFields()[auscene.getTextFields().length - 1].setText(selectedDirectory.getAbsolutePath());
				} else if (auscene.getTextFields().length > 0) {
					auscene.getTextFields()[auscene.getTextFields().length - 1].setText("No Directory selected");
				}
			});
		}
		auscene.getButton().setOnAction(actionEvent -> {
			if (authmethod.getAuthData().length > 0) {
				String[] authdata = new String[auscene.getTextFields().length];
				for (int i = 0; i < auscene.getTextFields().length; i++) {
					authdata[i] = auscene.getTextFields()[i].getText();
				}
				if (urlAuth) {
					if (auscene.getSSLCheckBox().isSelected()) {
						authdata[authdata.length - 1] = "true";
					} else {
						authdata[authdata.length - 1] = "false";
					}
				}
				try {
					model.authenticate(authmethod, authdata);
					setWaitingScene(true);
				} catch (IOException e) {
					exHandler.handleException("Input/ Output Exception: Your input data is invalid. Please try again.", view);
				} catch (ApiException e) {
					exHandler.handleException("Api Exception: The Kubernetes API is not available, please check your cluster connection.", view);
				}
			}
		});
		auscene.createScene(auscene.getVBox());
		mainStage.setScene(auscene.getScene());
	}

	/**
	 * Switches to the waiting scene and generates the result data.
	 */
	private void setWaitingScene(boolean install) {
		WaitingView waitscene = view.getWaitingScene();
		if (install)
			waitscene.createScene(waitscene.getVBox());
		try {
			List<ReadableGraph> graphs = model.generateGraphs(install);
			mainStage.setScene(waitscene.getScene());
			this.setGraphScene(graphs, install);
		} catch (ApiException e) {
			exHandler.handleError(e.toString(), view);
		} catch (GraphException e) {
			exHandler.handleException(e.toString(), view);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			exHandler.handleError("Interrupted Exception: The process to wait for output results was manually interrupted.", view);
		} catch (CloneNotSupportedException e) {
			exHandler.handleError("CloneNotSupportedException: The operation of cloning a vertex is not supported.", view);
		}
	}

	/**
	 * Switches to the graph scene and gives options to display all graphs.
	 * @param graphs a list of graphs to be displayed by this method
	 */
	private void setGraphScene(List<ReadableGraph> graphs, boolean install) {
		GraphView gscene;
		try {
			gscene = view.getGraphScene(graphs);
			gscene.getButtonLeft().setOnAction(actionEvent -> {
				gscene.getImgPane().showPrevPane();
				if (!graphs.isEmpty())
					gscene.getHeader().setText(TITEL + graphs.get(gscene.getImgPane().getCurrentPaneIndex()).getPattern().toString().toLowerCase());
			});
			gscene.getButtonRight().setOnAction(actionEvent ->  {
				gscene.getImgPane().showNextPane();
				if (!graphs.isEmpty())
					gscene.getHeader().setText(TITEL + graphs.get(gscene.getImgPane().getCurrentPaneIndex()).getPattern().toString().toLowerCase());
			});
			gscene.getButtonRerun().setOnAction(actionEvent -> this.setWaitingScene(false));
			if (!graphs.isEmpty()) {
				gscene.getHeader().setText(TITEL + graphs.get(0).getPattern().toString().toLowerCase());
			} else {
				gscene.getHeader().setText("No pattern detected!");
			}
			if (install)
				gscene.createScene(gscene.getPane());

			mainStage.setScene(gscene.getScene());
			Platform.runLater(() -> {
				try {
					view.showGraph();
				} catch (GraphException e) {
					exHandler.handleError(e.toString(), view);
				}
			}
					);
		} catch (GraphException e) {
			exHandler.handleError(e.toString(), view);
		}
	}

	/**
	 * Starts the program by initializing the controller.
	 * @param args empty argument string array
	 */
	public static void main(String[] args) {
		Controller controller = new Controller();
		controller.initialize();
	}

}
