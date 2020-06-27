package model;

import controller.GraphException;
import controller.PatternType;

class LeaderElection extends Pattern {

	/**
	 * Initializes a new ApiGateway pattern by setting a new graph.
	 */
	LeaderElection() {
		this.pGraph = new Graph();
		this.patternType = PatternType.LEADER_ELECTION;
		this.pGraph.setPattern(this.patternType);
	}
	
	@Override
	void createPattern() throws GraphException {
		Vertex leader = new Vertex("Leader", 0);
		leader.setRole(PatternType.LEADER_ELECTION.getRoles()[0]);
		pGraph.addVertex(leader);
		Vertex follower = new Vertex("Follower", 1);
		Vertex follower2 = new Vertex("Follower2", 2);
		follower.setRole(PatternType.LEADER_ELECTION.getRoles()[1]);
		follower2.setRole(PatternType.LEADER_ELECTION.getRoles()[1]);
		pGraph.addVertex(follower);
		pGraph.addVertex(follower2);
		
		pGraph.generateAdjacencyMatrices();
		
		pGraph.addEdge(leader, follower, 1);
		pGraph.addEdge(leader, follower2, 1);
	}

}
