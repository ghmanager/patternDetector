package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.ejml.simple.SimpleMatrix;

import controller.GraphException;
import controller.PatternType;

/**
 * This class detects patterns in a given graph by using a similarity algorithm.
 * @author Alexis T. Bernhard
 *
 */
class PatternDetector {

	/**
	 * The threshhold for the algorithm to detect patterns.
	 */
	private static final double THRESHOLD = 0.0001;

	/**
	 * The similarity algorithm to detect patterns.
	 */
	private SimilarityAlgorithm algorithm;


	/**
	 * Initializes the pattern detector by initializing the algorithm to detect this pattern.
	 */
	PatternDetector() {
		algorithm = new SimilarityAlgorithm();
	}

	/**
	 * Detects a pattern in a graph
	 * @param graph the graph to search for the patterns
	 * @param pattern the pattern to be searched for
	 * @param connectingMemberRoles the number of roles of nodes, which connect the seed to margin members with no direct connection to a seed
	 * @return a list of graphs where every graph stands for one occurrence of the pattern in the graph.
	 * 			Thereby one graph consists of all involved nodes and edges of the pattern.
	 * @throws GraphException thrown if an operation can't be performed on a graph
	 * @throws CloneNotSupportedException 
	 */
	List<ReadableGraph> detect(Graph graph, Pattern pattern, int connectingMemberRoles) throws GraphException, CloneNotSupportedException {

		// generate graph instances out of the entries
		List<ReadableGraph> resGraphs = new ArrayList<>();

		SimpleMatrix scores = algorithm.perform(graph, pattern.getGraph());
		if (scores == null)
			return resGraphs;

		// a member can have three roles: seed, normal member and margin member
		// observation: many patterns in microservices/ containerization are quite small and have only one central node (set it as seed) and connected to it many margin members
		// idea: spare effort by just look for connections of the seed and full members and not of margin members
		// hint: connections between margin members are not included in the graph to reduce the amount of irrelevant edges
		List<Member> seeds = new ArrayList<>();
		List<Member> marginMembers = new ArrayList<>();
		List<Member> members = new ArrayList<>();

		// search for possible members of a pattern and add the entry to a role list
		for (int i = 0; i < scores.numRows(); i++) {
			System.out.print("|");
			for (int j = 0; j < scores.numCols(); j++) {
				if (pattern.getPatternType() == PatternType.SCATTER_GATHER) {
					System.out.print(scores.get(i, j) + "-");
				}
				if (scores.get(i, j) >= THRESHOLD) {
					Member member = new Member(j, i, scores.get(i, j));
					if (i == 0) {
						seeds.add(member);
					} else if (i <= connectingMemberRoles) {
						members.add(member);
					} else {
						marginMembers.add(member);
					}
				}
			}
			System.out.println("|");
		}

		Collections.sort(members, new MemberComparator());
		Collections.sort(marginMembers, new MemberComparator());
		members.addAll(marginMembers);

		List<Member> includedMembers = new ArrayList<>();
		boolean addMember;
		boolean hasConnection;

		// functions: assures the same node is not in the pattern twice, same node should not be in two pattern roles
		for (Member seed : seeds) {
			Graph instance = new Graph();
			instance.setPattern(pattern.getPatternType());
			instance.initRoleAppearances();
			includedMembers.add(seed);
			instance.increaseRoleAppearance(instance.getPattern().getRoleIndex(pattern.getGraph().getVertexByIndex(seed.getPattern()).getRole()));
			for (Member newMember : members) {
				addMember = true; // indicates whether this member can be added or not
				hasConnection = false; // indicates if this new member has a connection to the corresponding seed
				ListIterator<Member> it = includedMembers.listIterator();
				while (addMember && it.hasNext()) {
					Member itMember = it.next();
					if (itMember.getNode() == newMember.getNode()) { // checks if the node already appears in the list in another role
						addMember = false;
					} else if (itMember.getPattern() <= connectingMemberRoles &&
							graph.getConnectionByIndex(itMember.getNode(), newMember.getNode()) > 0) { // checks for a connection to an inner member or seed
						hasConnection = true;
					}
				}
				if (addMember && hasConnection) {
					includedMembers.add(newMember);
					instance.increaseRoleAppearance(instance.getPattern().getRoleIndex(pattern.getGraph().getVertexByIndex(newMember.getPattern()).getRole()));
				}
			}

			boolean deleteInstance = false;

			for (int patternRole = 0; patternRole < pattern.getPatternType().getRoles().length; patternRole++) {
				if (instance.getRoleAppearance(patternRole) < pattern.getPatternType().getMinAppearance(patternRole) ||
						instance.getRoleAppearance(patternRole) > pattern.getPatternType().getMaxAppearance(patternRole)) {
					deleteInstance = true;
				}
			}

			if (!deleteInstance) {

				// create all vertices of the graph out of the included members
				for (Member member : includedMembers) {
					Vertex newVertex = (Vertex) ((Vertex) graph.getVertexByIndex(member.getNode())).clone();
					newVertex.setRole(pattern.getGraph().getVertexByIndex(member.getPattern()).getRole());		
					instance.addVertex(newVertex);
				}

				// generates edges, adds only edges which include inner members (no connections between margin members)
				instance.generateAdjacencyMatrices();
				ListIterator<Member> it = includedMembers.listIterator();
				while (it.hasNext() && it.nextIndex() <= connectingMemberRoles) {
					Member fullMember = it.next();
					ListIterator<Member> iter = includedMembers.listIterator();
					while (iter.hasNext()) {
						Member member = iter.next();
						int connectionType = graph.getConnectionByIndex(fullMember.getNode(), member.getNode());
						if (connectionType == 1) {
							instance.addEdgeByIndex(it.previousIndex(), iter.previousIndex(), 0);
						} else if (connectionType == 2) {
							instance.addEdgeByIndex(iter.previousIndex(), it.previousIndex(), 0);
						} else if (connectionType > 2) {
							instance.addEdgeByIndex(iter.previousIndex(), it.previousIndex(), 0);
							instance.addEdgeByIndex(it.previousIndex(), iter.previousIndex(), 0);
						}
					}
				}

				resGraphs.add(instance);
			} else {
				instance.clear();
			}
			includedMembers.clear();
		}
		return resGraphs;
	}

	/**
	 * This class represents a member which is a specific node of an instance of a specific pattern detected in the graph.
	 * @author Alexis T. Bernhard
	 *
	 */
	private class Member {
		/**
		 * The index of the vertex list of the input graph, representing one specific vertex.
		 */
		private int node;

		/**
		 * The index of the vertex list of the pattern graph, representing one specific vertex.
		 */
		private int pattern;

		/**
		 * The similarity score, which represents the similarity of the node as a member of the input graph and as a member of a pattern.
		 */
		private double score;

		/**
		 * Initializes a member a sets its attributes.
		 * @param node the index of the vertex list of the input graph, representing one specific vertex
		 * @param pattern the index of the vertex list of the pattern graph, representing one specific vertex
		 * @param roleNum the role number, following roles exist: seed, normal memebr and margin member
		 * @param score the similarity score, which represents the similarity of the node as a member of the input graph and as a member of a pattern
		 */
		public Member (int node, int pattern, double score) {
			this.node = node;
			this.pattern = pattern;
			this.score = score;
		}

		/**
		 * Gets the index of the vertex list of the input graph, representing one specific vertex.
		 * @return the vertex index
		 */
		public int getNode() {
			return node;
		}

		/**
		 * Gets the index of the vertex list of the pattern graph, representing one specific vertex.
		 * @return the vertex index
		 */
		public int getPattern() {
			return pattern;
		}

		/**
		 * Gets the similarity score, which represents the similarity of the node as a member of the input graph and as a member of a pattern.
		 * @return the similarity score
		 */
		public double getScore() {
			return score;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) { 
				return true; 
			}

			if (!(o instanceof Member)) { 
				return false; 
			}
			Member m = (Member) o; 

			return m.node == this.node && m.pattern == this.pattern;
		}

		@Override
		public int hashCode() {
			return 2 * this.node + 3 * this.pattern; 
		}
	}

	/**
	 * Compares two members with each other by their score.
	 * @author Alexis T. Bernhard
	 */
	private class MemberComparator implements Comparator<Member> {

		@Override
		public int compare(Member m1, Member m2) {
			Double m1score = m1.getScore();
			Double m2score = m2.getScore();
			return m2score.compareTo(m1score);
		}

	}
}
