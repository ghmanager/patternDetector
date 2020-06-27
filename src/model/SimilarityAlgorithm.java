package model;

import org.ejml.simple.SimpleMatrix;

import controller.PatternType;

/**
 * This class implements a similarity algorithm by setting a similarity score
 * @author Alexis T. Bernhard
 *
 */
public class SimilarityAlgorithm {

	private static final double TOLERANCE = 0.001;

	SimpleMatrix perform(Graph system, Graph pattern) {
		if (system == null || pattern == null ||
				pattern.getVertices().isEmpty() || system.getVertices().isEmpty()||
				system.getAdjacencyMatrix() == null || pattern.getAdjacencyMatrix() == null ||
				system.getAdjacencyMatrix().length == 0 || system.getAdjacencyMatrix()[0].length == 0 ||
				pattern.getAdjacencyMatrix().length == 0 || pattern.getAdjacencyMatrix()[0].length == 0)
			return null;
		SimpleMatrix res = new SimpleMatrix(pattern.getVertices().size(), system.getVertices().size());
		SimpleMatrix systemMatrix;
		if (pattern.getPattern() == PatternType.LEADER_ELECTION) {
			systemMatrix = new SimpleMatrix(system.getReplicaAdjacencyMatrix());
		} else {
			systemMatrix = new SimpleMatrix(system.getAdjacencyMatrix());
		}
		SimpleMatrix patternMatrix = new SimpleMatrix(pattern.getAdjacencyMatrix());

		res = res.plus(getSimilarityScore(systemMatrix, patternMatrix));

		return res;
	}

	private static SimpleMatrix getSimilarityScore(SimpleMatrix mxS, SimpleMatrix mxP) {
		int m = mxS.numRows();
		int n = mxP.numCols();

		if (mxS.isIdentical(new SimpleMatrix(mxS.numRows(), mxS.numCols()), 0.0) || mxP.isIdentical(new SimpleMatrix(mxP.numRows(), mxP.numCols()), 0.0))
			return new SimpleMatrix(n, m);

		SimpleMatrix res = new SimpleMatrix(n, m);
		res.fill(1.0);
		SimpleMatrix prevRes = new SimpleMatrix(n, m);
		boolean flag = false;
		int i = 0;

		while(!flag) {
			SimpleMatrix temp1 = mxP.mult(res).mult(mxS.transpose());
			SimpleMatrix temp2 = mxP.transpose().mult(res).mult(mxS);
			SimpleMatrix temp = temp1.plus(temp2);
			res = temp.divide(norm1(temp));
			i++;

			if (i % 2 == 0) {
				flag = convergence(res, prevRes);
				prevRes = res;
			}
		}
		return res;
	}

	private static boolean convergence(SimpleMatrix mxA, SimpleMatrix mxB) {
		for (int i = 0; i < mxA.numRows(); i++) {
			for (int j = 0; j < mxA.numCols(); j++) {
				if(Math.abs(mxA.get(i, j) - mxB.get(i, j)) > TOLERANCE)
					return false;
			}
		}
		return true;
	}

	public static double norm1(SimpleMatrix mxM) {
		double f = 0;
		for (int j = 0; j < mxM.numCols(); j++) {
			double s = 0;
			for (int i = 0; i < mxM.numRows(); i++) {
				s += Math.abs(mxM.get(i, j));
			}
			f = Math.max(f, s);
		}
		return f;
	}
}
