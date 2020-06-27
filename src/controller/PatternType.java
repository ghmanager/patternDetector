package controller;

/**
 * This class contains all possible types of pattern to detect in the given cluster and defines the roles which could appear in this type of pattern.
 * @author Alexis T. Bernhard
 *
 */
public enum PatternType {

	API_GATEWAY(new String[] {"apiGateway", "client", "service"}, new int[]{1, 2, 2}, new int[] {1, Integer.MAX_VALUE, Integer.MAX_VALUE}),
	SCATTER_GATHER(new String[] {"rootContainer", "client", "childContainer"}, new int[]{1, 1, 2}, new int[] {1, 1, Integer.MAX_VALUE}),
	LEADER_ELECTION(new String[] {"leader", "follower"}, new int[]{1, 2}, new int[] {1, Integer.MAX_VALUE}); 

	/**
	 * The roles which could appear in this type of pattern.
	 */
	private String[] roles;

	/**
	 * Minimal number of occurrences of a role
	 */
	private int[] minRoleAppearances;

	/**
	 * Maximal number of occurrences of a role
	 */
	private int[] maxRoleAppearances;

	/**
	 * Sets the roles which could appear in this type of pattern and their min and max occurences.
	 * @param the roles to set as complete list.
	 */
	private PatternType(String[] roles, int[] minOccurences, int[] maxOccurences) {
		if (roles != null && minOccurences != null && maxOccurences != null) {
			this.roles = roles;
			this.minRoleAppearances = minOccurences;
			this.maxRoleAppearances = maxOccurences;
		}
	}

	/**
	 * Get the minimal occurrences of the roles.
	 * @return the minimal occurrences of the roles.
	 */
	public int[] getMinAppearances() {
		return minRoleAppearances;
	}

	/**
	 * Get the maximal occurrences of the roles.
	 * @return the maximal occurrences of the roles.
	 */
	public int[] getMaxAppearances() {
		return maxRoleAppearances;
	}

	/**
	 * Gets the roles which could appear in this type of pattern.
	 * @return the roles as an array of strings
	 */
	public String[] getRoles() {
		return roles;
	}

	/**
	 * Gets the the min occurrences of the role at the given index
	 * @param index the index of the searched role of the pattern type
	 * @return the min occurrences of the role at the given index
	 */
	public int getMinAppearance(int index) {
		if (index >= 0 && index < minRoleAppearances.length) {
			return  minRoleAppearances[index];
		} else {
			return -2;
		}
	}

	/**
	 * Gets the the max occurrences of the role at the given index
	 * @param index the index of the searched role of the pattern type
	 * @return the max occurrences of the role at the given index
	 */
	public int getMaxAppearance(int index) {
		if (index >= 0 && index < minRoleAppearances.length) {
			return  maxRoleAppearances[index];
		} else {
			return -2;
		}
	}

	/**
	 * Gets the index of the first time the input role is found in the array.
	 * @param role the role to get the index from
	 * @return the index of the first appearance of the input role in the array of strings of the specified input type.
	 */
	public int getRoleIndex(String role) {
		int result = -1;
		for (int i = 0; i < roles.length; i++) {
			if (roles[i].equals(role)) {
				result = i;
			}
		}
		return result;
	}
}
