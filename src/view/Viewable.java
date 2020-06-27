package view;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * This class gives a common framework to create a viewable scene with its content 
 * @author Alexis T. Bernhard
 *
 */
public abstract class Viewable {
	
	/**
	 * The main scene to display.
	 */
	private Scene scene;
	
	/**
	 * Gets the main scene to display.
	 * @return the scene
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * Creates a new scene and overrides the old scene if existing.
	 * @param container the main content of the view all aggregated in one container
	 */
	public void createScene(Parent container) {
		scene = new Scene(container, View.WIDTH, View.HEIGHT);
	}
}
