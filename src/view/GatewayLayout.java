package view;

import java.util.List;

import controller.GraphException;
import controller.PatternType;

/**
 * This class sets the locations of nodes of a graph following the api gateway pattern
 * @author Alexis T. Bernhard
 *
 */
public class GatewayLayout extends Layout {

	/**
	 * Initializes an api gateway layout
	 * @param pane the cell pane of the graph to be layouted an drawn on this cell pane
	 */
	GatewayLayout(CellPane pane) {
		this.pane = pane;
	}

	@Override
	void execute() throws GraphException {
		List<Cell> cells = pane.getCells();
		int[] counters = new int[pane.getGraph().getAllRoleAppearances().length];

		for (Cell cell : cells) {
			int appears = pane.getGraph().getRoleAppearance(cell.getRole());
			if (cell.getRole().equals(PatternType.API_GATEWAY.getRoles()[0])) {
				if (appears == 1) {
					double x = OFFSET + (pane.getMaxWidth() - 2 * OFFSET - cell.getBoundsInParent().getWidth()) / 2;
					double y = OFFSET + (pane.getMaxHeight() - 2 * OFFSET - cell.getBoundsInParent().getHeight()) / 2;
					cell.relocate(x, y);
					counters[1]++;
				} else {
					throw new GraphException("A graph for the pattern " + PatternType.API_GATEWAY.toString() + " contains " + appears + " seed roles which is not the desired number of roles (only exactly one role is allowed).");
				}
			} else if (cell.getRole().equals(PatternType.API_GATEWAY.getRoles()[1])) {
				double x = OFFSET;
				double y = 10 * OFFSET + counters[0] * (pane.getMaxHeight() - 20 * OFFSET - cell.getBoundsInParent().getHeight()) / (appears - 1);
				cell.relocate(x, y);
				counters[0]++;
			} else if (cell.getRole().equals(PatternType.API_GATEWAY.getRoles()[2])) {
				double x = OFFSET + pane.getMaxWidth() - 2 * OFFSET - cell.getBoundsInParent().getWidth();
				double y = 10 * OFFSET + counters[2] * (pane.getMaxHeight() - 20 * OFFSET - cell.getBoundsInParent().getHeight()) / (appears - 1);
				cell.relocate(x, y);
				counters[2]++;
			} else {
				throw new GraphException("The role of the cell " + cell.getRole() + " is not supported for the pattern type " + PatternType.API_GATEWAY.toString());
			}
		}
	}
}
