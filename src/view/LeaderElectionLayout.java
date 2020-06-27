package view;

import java.util.List;

import controller.GraphException;
import controller.PatternType;

public class LeaderElectionLayout extends Layout {

	/**
	 * Initializes a leader election layout
	 * @param pane the cell pane of the graph to be designed and drawn on this cell pane
	 */
	LeaderElectionLayout(CellPane pane) {
		this.pane = pane;
	}

	@Override
	void execute() throws GraphException {
		List<Cell> cells = pane.getCells();
		int[] counters = new int[pane.getGraph().getAllRoleAppearances().length];

		for (Cell cell : cells) {
			int appears = pane.getGraph().getRoleAppearance(cell.getRole());
			if (cell.getRole().equals(PatternType.LEADER_ELECTION.getRoles()[0])) {
				if (appears == 1) {
					double x = OFFSET + (pane.getMaxWidth() - 2 * OFFSET - cell.getBoundsInParent().getWidth()) / 2;
					double y = OFFSET;
					cell.relocate(x, y);
					counters[0]++;
				} else {
					throw new GraphException("A graph for the pattern " + PatternType.LEADER_ELECTION.toString() + " contains " + appears + " seed roles which is not the desired number of roles (only exactly one role is allowed).");
				}
			} else if (cell.getRole().equals(PatternType.LEADER_ELECTION.getRoles()[1])) {
				double x = OFFSET + counters[1] * (pane.getMaxWidth() - 2 * OFFSET - cell.getBoundsInParent().getWidth()) / (appears - 1);
				double y = OFFSET + (pane.getMaxHeight() - 2 * OFFSET - cell.getBoundsInParent().getHeight()) / 2;
				cell.relocate(x, y);
				counters[1]++;
			} else {
				throw new GraphException("The role of the cell " + cell.getName() + " is not supported for the pattern type " + PatternType.LEADER_ELECTION.toString());
			}
		}
	}

}
