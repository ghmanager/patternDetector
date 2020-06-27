package view;

import java.util.List;
import java.util.Random;

/**
 * This class sets the locations of nodes of a graph to a random position
 * @author Alexis T. Bernhard
 *
 */
class RandomLayout extends Layout {

	/**
	 * Initializes the random layout, which serves as default layout, if no other layouts are chosen
	 * @param pane the cell pane to display the layout
	 */
	RandomLayout(CellPane pane) {
		this.pane = pane;
	}

	@Override
	void execute() {
		Random random = new Random();
		List<Cell> cells = pane.getCells();

		for (Cell cell : cells) {

			double x = OFFSET / 2 + random.nextDouble() * (pane.getMaxWidth() - 2 * OFFSET - cell.getWidth());
			double y = OFFSET / 2 + random.nextDouble() * (pane.getMaxHeight() - 2 * OFFSET - cell.getHeight());

			cell.relocate(x, y);
		}
	}

}
