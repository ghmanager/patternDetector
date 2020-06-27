package view;

import java.io.FileNotFoundException;
import java.util.List;

import controller.GraphException;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import model.ReadableGraph;

/**
 * This class serves as facade and as an entry point for all other classes in the view package.
 * @author Alexis T. Bernhard
 *
 */
public class View {

	/**
	 * The width of the screen of the application.
	 */
	static final int WIDTH = 800;

	/**
	 * The height of the screen of the application.
	 */
	static final int HEIGHT = 600;

	/**
	 * The single instance of the define authentication scene to choose the method of authentication.
	 */
	private DefineAuthenticationView defAuthScene;

	/**
	 * The single instance of the authentication scene to insert all data for authentication.
	 */
	private AuthenticationView authScene;

	/**
	 * The single instance of the error message dialog to print and handle  error messages.
	 */
	private AppDialog dialog;

	/**
	 * The single instance of the waiting scene to wait for the results of the program.
	 */
	private WaitingView waitScene;

	/**
	 * The single instance of the graph scene to print out all result graphs.
	 */
	private GraphView graphScene;

	/**
	 * Initializes the view by initializing all scenes
	 * @throws FileNotFoundException error thrown due to the background image path
	 */
	public View() {

		defAuthScene = new DefineAuthenticationView();
		waitScene = new WaitingView();
		graphScene = new GraphView();
		authScene = new AuthenticationView();
		dialog = new AppDialog();
		this.makeBackground();
	}

	/**
	 * Gets the single instance of the define authentication scene to choose the method of authentication.
	 * @return the define authentication scene
	 */
	public DefineAuthenticationView getDefineAuthenticationScene() {
		return defAuthScene;
	}

	/**
	 * Gets the single instance of the authentication scene to insert all data for authentication.
	 * @param textFields the number of text fields which should be displayed
	 * @param urlAuth indicates if the checkbox for an alternative SSL option is available and if a button to choose a file should be available
	 * @return the authentication scene
	 */
	public AuthenticationView getAuthenticationScene(int textFields, boolean urlAuth) {
		if (authScene.getTextFields() == null) {
			authScene.setInputLayout(textFields, !urlAuth);
		}
		if (urlAuth) {
			authScene.validateSSL();
		}
		return authScene;
	}

	/**
	 * Gets the single instance of the waiting scene to wait for the results of the program.
	 * @return the waiting scene
	 */
	public WaitingView getWaitingScene() {
		return waitScene;
	}

	/**
	 * Gets the single instance of the error message dialog to print and handle  error messages.
	 * @return the error message dialog
	 */
	public AppDialog getAppDialog() {
		return dialog;
	}

	/**
	 * Gets the single instance of the graph scene to print out all result graphs.
	 * @param graphs a list of graphs to be displayed by the view
	 * @return the graph scene
	 * @throws GraphException thrown if an input graph contains misplaced constructions (e.g. null vertices)
	 * @throws FileNotFoundException error thrown due to the background image path
	 */
	public GraphView getGraphScene(List<ReadableGraph> graphs) throws GraphException {
		graphScene.getImgPane().initGraphs(graphs);
		return graphScene;
	}

	/**
	 * Draw all graphs (positioning) and creates all edges of the graph
	 * @throws GraphException thrown if an input graph contains misplaced constructions (e.g. null vertices)
	 */
	public void showGraph() throws GraphException {
		graphScene.getImgPane().drawGraphs();
	}

	/**
	 * Creates the background of all scenes by setting a background image
	 * @throws FileNotFoundException the file of the image is not available
	 */
	private void makeBackground() {
		BackgroundFill[] fills = new BackgroundFill[1];
		fills[0] = new BackgroundFill(Color.TRANSPARENT, null, null);
		BackgroundImage[] images = new BackgroundImage[1];
		Image image = new Image(this.getClass().getClassLoader().getResourceAsStream("bkgicon.png"));
		BackgroundImage bkgfill = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(0.9, BackgroundSize.AUTO, true, false, false, false));
		images[0] = bkgfill;
		Background background = new Background(fills, images);

		defAuthScene.getVBox().setBackground(background);
		authScene.getVBox().setBackground(background);
		waitScene.getVBox().setBackground(background);
		graphScene.getPane().setBackground(background);
	}
}
