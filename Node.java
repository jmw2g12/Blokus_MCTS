/**
 *	Class adapted from code found at: https://github.com/theKGS/MCTS/
 *	Original author: theKGS
 */

import java.util.ArrayList;
import java.util.Set;

public class Node {
	public double[] score = {0, 0};
	public double games = 0;
	public Piece move;
	public ArrayList<Node> unvisitedChildren;
	public ArrayList<Node> children;
	public Node parent;
	public int player;
	
	public Node(Board b) {
		children = new ArrayList<Node>();
		player = b.getCurrentPlayer();
		score = new double[b.getQuantityOfPlayers()];
	}

	public Node(Board b, Piece m, Node prnt) {
		children = new ArrayList<Node>();
		parent = prnt;
		move = m;
		Board tempBoard = b.clone();
		tempBoard.makeMove(m);
		player = tempBoard.getCurrentPlayer();
		score = new double[b.getQuantityOfPlayers()];
	}

	public double upperConfidenceBound(double c) {
		return score[parent.player] / games  + c
				* Math.sqrt(Math.log(parent.games + 1) / games);
	}

	public void backPropagateScore(double[] scr) {
		this.games++;
		for (int i = 0; i < scr.length; i++)
			this.score[i] += scr[i];

		if (parent != null)
			parent.backPropagateScore(scr);
	}

	public void expandNode(Board currentBoard){
		ArrayList<Piece> possibleMoves = currentBoard.getMoves(player);
		unvisitedChildren = new ArrayList<Node>();
		for (int i = 0; i < possibleMoves.size(); i++) {
			Node temp = new Node(currentBoard, possibleMoves.get(i), this);
			unvisitedChildren.add(temp);
		}
	}

	public ArrayList<Node> select(double explorationConstant){
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<Node> bestNodes = new ArrayList<Node>();
		double tempBest = 0;
		for (Node s : children) {
			tempBest = s.upperConfidenceBound(explorationConstant);
			if (tempBest > bestValue) {
				bestNodes.clear();
				bestNodes.add(s);
				bestValue = tempBest;
			} else if (tempBest == bestValue) {
				bestNodes.add(s);
			}
		}
		return bestNodes;
	}
}
