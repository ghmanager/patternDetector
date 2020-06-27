package model;

import controller.GraphException;
import controller.PatternType;

/**
 * This abstract class defines a super class for specific pattern types and provides a framework with an internal graph and a pattern type both with a getter and a method to create this pattern.
 * @author Alexis T. Bernhard
 *
 */
abstract class Pattern {

	/**
	 * The general graph representing the pattern.
	 */
	protected Graph pGraph;
	
	/**
	 * The type of pattern which is represented through this class.
	 */
	protected PatternType patternType;
	
	/**
	 * Returns the graph representing the pattern
	 * @return the graph
	 */
	Graph getGraph() {
		return pGraph;
	}
	
	/**
	 * Returns the type of the pattern
	 * @return the pattern type
	 */
	PatternType getPatternType() {
		return patternType;
	}
	
	/**
	 * Creates the pattern by generating the graph of the pattern
	 * @throws GraphException  thrown if an operation can't be performed on a graph
	 */
	abstract void createPattern() throws GraphException;
}
