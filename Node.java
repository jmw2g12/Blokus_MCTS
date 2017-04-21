
import java.util.ArrayList;
import java.util.Set;

public class Node {
	public double[] score;
	public double games;
	public Piece move;
	public ArrayList<Node> unvisitedChildren;
	public ArrayList<Node> children;
	public Set<Integer> rVisited;
	public Node parent;
	public int player;
	public double[] pess;
	public double[] opti;
	public boolean pruned;
	public String weightingMethod = "none";
	
	/**
	 * This creates the root node
	 * 
	 * @param b
	 */
	public Node(Board b, String weightingMethod) {
		children = new ArrayList<Node>();
		this.weightingMethod = weightingMethod;
		player = b.getCurrentPlayer();
		score = new double[b.getQuantityOfPlayers()];
		pess = new double[b.getQuantityOfPlayers()];
		opti = new double[b.getQuantityOfPlayers()];
		for (int i = 0; i < b.getQuantityOfPlayers(); i++)
			opti[i] = 1;
	}

	/**
	 * This creates non-root nodes
	 * 
	 * @param b
	 * @param m
	 * @param prnt
	 */
	public Node(Board b, Piece m, Node prnt, String weightingMethod) {
		children = new ArrayList<Node>();
		this.weightingMethod = weightingMethod;
		parent = prnt;
		move = m;
		Board tempBoard = b.duplicate();
		tempBoard.makeMove(m,player);
		player = tempBoard.getCurrentPlayer();
		score = new double[b.getQuantityOfPlayers()];
		pess = new double[b.getQuantityOfPlayers()];
		opti = new double[b.getQuantityOfPlayers()];
		for (int i = 0; i < b.getQuantityOfPlayers(); i++)
			opti[i] = 1;
	}

	/**
	 * Return the upper confidence bound of this state
	 * 
	 * @param c
	 *            typically sqrt(2). Increase to emphasize exploration. Decrease
	 *            to incr. exploitation
	 * @param t
	 * @return
	 */
	public double upperConfidenceBound(double c) {
		return score[parent.player] / games  + c
				* Math.sqrt(Math.log(parent.games + 1) / games);
	}

	/**
	 * Update the tree with the new score.
	 * @param scr
	 */
	public void backPropagateScore(double[] scr) {
		this.games++;
		for (int i = 0; i < scr.length; i++)
			this.score[i] += scr[i];

		if (parent != null)
			parent.backPropagateScore(scr);
	}

	/**
	 * Expand this node by populating its list of
	 * unvisited child nodes.
	 * @param currentBoard
	 */
	public void expandNode(Board currentBoard){
		ArrayList<Piece> legalMoves = currentBoard.getMoves();
		unvisitedChildren = new ArrayList<Node>();
		for (int i = 0; i < legalMoves.size(); i++) {
			Node tempState = new Node(currentBoard, legalMoves.get(i), this, weightingMethod);
			unvisitedChildren.add(tempState);
		}
	}

	/**
	 * Produce a list of viable nodes to visit. The actual 
	 * selection is done in runMCTS
	 * @param optimisticBias
	 * @param pessimisticBias
	 * @param explorationConstant
	 * @return
	 */
	public ArrayList<Node> select(double optimisticBias, double pessimisticBias, double explorationConstant){
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<Node> bestNodes = new ArrayList<Node>();
		for (Node s : children) {
			if (s.pruned == false) {
				final double tempBest = s.upperConfidenceBound(explorationConstant)
						+optimisticBias * s.opti[player]
						+pessimisticBias * s.pess[player];

				if (tempBest > bestValue) {
					bestNodes.clear();
					bestNodes.add(s);
					bestValue = tempBest;
				} else if (tempBest == bestValue) {
					bestNodes.add(s);
				}
			}
		}
		
		return bestNodes;
	}
	
	/**
	 * Set the bounds in the given node and propagate the values 
	 * back up the tree. When bounds are first created they are
	 * both equivalent to a player's score.
	 * 
	 * @param optimistic
	 * @param pessimistic
	 */
	public void backPropagateBounds(double[] score) {
		for (int i = 0; i < score.length; i++) {
			opti[i] = score[i];
			pess[i] = score[i];
		}

		if (parent != null)
			parent.backPropagateBoundsHelper();
	}

	private void backPropagateBoundsHelper() {
		for (int i = 0; i < opti.length; i++) {
			if (i == player) {
				opti[i] = 0;
				pess[i] = 0;
			} else {
				opti[i] = 1;
				pess[i] = 1;
			}
		}

		for (int i = 0; i < opti.length; i++) {
			for (Node c : children) {
				if (i == player) {
					if (opti[i] < c.opti[i])
						opti[i] = c.opti[i];
					if (pess[i] < c.pess[i])
						pess[i] = c.pess[i];
				} else {
					if (opti[i] > c.opti[i])
						opti[i] = c.opti[i];
					if (pess[i] > c.pess[i])
						pess[i] = c.pess[i];
				}
			}
		}

		if (!unvisitedChildren.isEmpty()) {
			for (int i = 0; i < opti.length; i++) {
				if (i == player) {
					opti[i] = 1;
				} else {
					pess[i] = 0;
				}
			}
		}

		pruneBranches();
		if (parent != null)
			parent.backPropagateBoundsHelper();
	}

	public void pruneBranches() {
		for (Node s : children) {
			if (pess[player] >= s.opti[player]) {
				s.pruned = true;
			}
		}

		if (parent != null)
			parent.pruneBranches();
	}

	/**
	 * Select a child node at random and return it.
	 * @param board
	 * @return
	 */
	public int randomSelect(Board board) {
		double []weights = board.getMoveWeights(weightingMethod);
		
		double totalWeight = 0.0d;
		for (int i = 0; i < weights.length; i++)
		{
		    totalWeight += weights[i];
		}
		
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < weights.length; ++i)
		{
		    random -= weights[i];
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		return randomIndex;
	}
}
