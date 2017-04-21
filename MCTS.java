
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class MCTS {
	private Random random;
	private Node rootNode;
	private double explorationConstant = Math.sqrt(2.0);
	private double pessimisticBias;
	private double optimisticBias;

	private boolean scoreBounds;
	private String scoringMethod;
	private boolean trackTime; 
	
	private String weightingMethod = "";
	
	private Player p;
	
	ValueNet vn;

	public MCTS(Player p, String weightingMethod) {
		random = new Random();
		this.weightingMethod = weightingMethod;
		this.p = p;
		vn = new ValueNet();
	}

	/**
	 * Run a UCT-MCTS simulation for a number of iterations.
	 * 
	 * @param startingBoard starting board
	 * @param runs how many iterations to think
	 * @param bounds enable or disable score bounds.
	 * @return
	 */
	public Piece runMCTS(Board startingBoard, int runs, boolean bounds, String scoringMethod) {
		scoreBounds = bounds;
		this.scoringMethod = scoringMethod;
		rootNode = new Node(startingBoard,weightingMethod);
		
		
		long startTime = System.nanoTime();

		for (int i = 0; i < runs; i++) {
			select(startingBoard.duplicate(), rootNode);
			
			/*if (runs < 10){
				System.out.println(i + "/" + runs);
			}else{
				if (i % (runs/10) == 0){
					System.out.println((i/(runs/10)+1) + "/10");
				}
			}*/
		}

		long endTime = System.nanoTime();

		if (this.trackTime)
			System.out.println("Thinking time per move in milliseconds: "
					+ (endTime - startTime) / 1000000);

		return finalSelect(rootNode);
	}
	public double[] getScore(Board b){
		switch(scoringMethod){
			case "binary":
				return b.getBinaryScore();
			case "difference":
				return b.getScore();
			case "exploration":
				return b.getProductScore();
			default:
				return b.getScore();
		}
	}

	/**
	 * This represents the select stage, or default policy, of the algorithm.
	 * Traverse down to the bottom of the tree using the selection strategy
	 * until you find an unexpanded child node. Expand it. Run a random playout.
	 * Backpropagate results of the playout.
	 * 
	 * @param node
	 *            Node from which to start selection
	 * @param brd
	 * 			  Board state to work from.
	 */
	private void _select(Board currentBoard, Node currentNode) {
		while (true) {
			if (currentBoard.gameOver()) {
				currentNode.backPropagateScore(getScore(currentBoard));
				if (scoreBounds) {
					currentNode.backPropagateBounds(getScore(currentBoard));
				}
				return;
			}

			if (currentNode.unvisitedChildren == null) {
				currentNode.expandNode(currentBoard);
			}

			if (currentNode.player >= 0){
				if (!currentNode.unvisitedChildren.isEmpty()) {
					Node temp = currentNode.unvisitedChildren.remove(random.nextInt(currentNode.unvisitedChildren.size()));
					currentNode.children.add(temp);
					currentBoard.makeMove(temp.move,currentNode.player);
					playout(temp, currentBoard);
					return;
				} else {
					ArrayList<Node> bestNodes = currentNode.select(optimisticBias, pessimisticBias, explorationConstant);
					
					if (currentNode == rootNode && bestNodes.isEmpty())
						return;
					
					Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
					currentNode = finalNode;
					currentBoard.makeMove(finalNode.move,currentNode.player);
				}
			} else {
				if (currentNode.rVisited == null)
					currentNode.rVisited = new HashSet<Integer>();
				
				int indexOfMove = currentNode.randomSelect(currentBoard);
				
				if (currentNode.rVisited.contains(indexOfMove)){
					currentNode = currentNode.unvisitedChildren.get(indexOfMove);
					currentBoard.makeMove(currentNode.move,currentNode.player);
				} else {
					currentNode = currentNode.unvisitedChildren.get(indexOfMove);
					currentBoard.makeMove(currentNode.move,currentNode.player);
					playout(currentNode, currentBoard);
					return;					
				}
			}
		}
	}
	
	/**
	 * This represents the select stage, or default policy, of the algorithm.
	 * Traverse down to the bottom of the tree using the selection strategy
	 * until you find an unexpanded child node. Expand it. Run a random playout.
	 * Backpropagate results of the playout.
	 * 
	 * @param node
	 *            Node from which to start selection
	 * @param brd
	 * 			  Board state to work from.
	 */
	private void select(Board currentBoard, Node currentNode){
		Map.Entry<Board, Node> tuple = treePolicy(currentBoard, currentNode);
		double[] score = playout(tuple.getValue(), tuple.getKey());
		tuple.getValue().backPropagateScore(score);
	}
	
	private Map.Entry<Board, Node> treePolicy(Board b, Node node) { 
		while(true) {
			if (b.gameOver()) {
				return new AbstractMap.SimpleEntry<Board, Node>(b, node);
			} else {
				if (node.unvisitedChildren == null) {
					node.expandNode(b); 
					Board temp = b.clone();
					ArrayList<Piece> moves = b.getMoves();
				}
				
				if (!node.unvisitedChildren.isEmpty()) {
					Node temp = node.unvisitedChildren.remove(random.nextInt(node.unvisitedChildren.size()));
					node.children.add(temp);
					b.makeMove(temp.move);
					return new AbstractMap.SimpleEntry<Board, Node>(b, temp);
				} else {
					ArrayList<Node> bestNodes = node.select(optimisticBias, pessimisticBias, explorationConstant);
					Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
					node = finalNode;
					b.makeMove(finalNode.move,finalNode.player);
				}
			}
		}
	}
	
	
	/**
	 * This is the final step of the algorithm, to pick the best move to
	 * actually make.
	 * 
	 * @param n
	 *            this is the node whose children are considered
	 * @return the best Move the algorithm can find
	 */
	private Piece finalSelect(Node n) {
		double bestValue = Double.NEGATIVE_INFINITY;
		double tempBest;
		ArrayList<Node> bestNodes = new ArrayList<Node>();

		for (Node s : n.children) {
			System.out.println("games = " + s.games);
			tempBest = s.games;
			tempBest += s.opti[n.player] * optimisticBias;
			tempBest += s.pess[n.player] * pessimisticBias;
			if (tempBest > bestValue) {
				bestNodes.clear();
				bestNodes.add(s);
				bestValue = tempBest;
			} else if (tempBest == bestValue) {
				bestNodes.add(s);
			}
		}
		
		if (bestNodes.size() == 0) return null;
		
		Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
		
		return finalNode.move;
	}

	/**
	 * Playout function for MCTS
	 * 
	 * @param state
	 * @return
	 */
	private double[] playout(Node state, Board board) {
		ArrayList<Piece> moves;
		Piece mv;
		Board brd = board.duplicate();
		while (!brd.gameOver()) {
			moves = brd.getMoves();
			mv = getRandomMove(brd,moves,state);
			brd.makeMove(mv,brd.getCurrentPlayer());
		}
		return getScore(brd);
	}

	private Piece getRandomMove(Board board, ArrayList<Piece> moves, Node state) {
		double[] weights = getMoveWeights(moves, weightingMethod, board, state);
		
		double totalWeight = 0.0d;
		double minimum = 0.0d;
		for (int i = 0; i < weights.length; i++){
		    if (weights[i] < minimum) minimum = weights[i];
		}
		for (int i = 0; i < weights.length; i++){
		    weights[i] -= minimum;
		    totalWeight += weights[i];
		}
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < weights.length; ++i){
		    random -= weights[i];
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		return moves.get(randomIndex);
	}
	
	public double[] getMoveWeights(ArrayList<Piece> moves, String weightingMethod, Board b, Node state) {
		if (weightingMethod.equals("size") || weightingMethod.equals("")){
			double[] result = new double[moves.size()];
			for (Piece m : moves){
				result[moves.indexOf(m)] = Math.pow((double)m.size(),1.5);
			}
			return result;
		}else if (weightingMethod.equals("heat") || weightingMethod.equals("exploration")){
			double[] result = new double[moves.size()];
			for (Piece m : moves){
				result[moves.indexOf(m)] = b.explorationHeatMapScore(m);
			}
			return result;
		}else if (weightingMethod.equals("product")){
			double[] result = new double[moves.size()];
			for (Piece m : moves){
				result[moves.indexOf(m)] = b.explorationProductScore(m);
			}
			return result;
		}else if (weightingMethod.equals("uct") || weightingMethod.equals("ucb")){
			double[] result = new double[moves.size()];
			System.out.println("unvisitedChildren.size() = " + (state.unvisitedChildren == null ? "null" : state.unvisitedChildren.size()));
			System.out.println("children.size() = " + (state.children == null ? "null" : state.children.size()));
			System.out.println("rVisited.size() = " + (state.rVisited == null ? "null" : state.rVisited.size()));
			System.out.println("games = " + state.games);
			
			System.out.println("state.parent is null ? " + (state.parent == null ? "true" : "false"));
			System.out.println("parent.unvisitedChildren.size() = " + ((state.parent == null || state.parent.unvisitedChildren == null) ? "null" : state.parent.unvisitedChildren.size()));
			System.out.println("parent.children.size() = " + ((state.parent == null || state.parent.children == null) ? "null" : state.parent.children.size()));
			System.out.println("parent.rVisited.size() = " + ((state.parent == null || state.parent.rVisited == null) ? "null" : state.parent.rVisited.size()));
			System.out.println("parent.games = " + (state.parent == null ? "null" : state.parent.games));
	
			for (Piece m : moves){
				result[moves.indexOf(m)] = b.explorationProductScore(m);
			}
			return result;
		}else if (weightingMethod.equals("valuenet") || weightingMethod.equals("value")){
			double[] result = new double[moves.size()];
			Board temp;
			for (Piece m : moves){
				temp = b.clone();
				b.putPieceOnBoard(m,p.getPieceCode());
				result[moves.indexOf(m)] = vn.getValue(b,p.getStartingCorner() == 1);
			}
			return result;
		}else{
			System.out.println("HERE :(");
			return null;
		}
	}
	
	/**
	 * Sets the exploration constant for the algorithm. You will need to find
	 * the optimal value through testing. This can have a big impact on
	 * performance. Default value is sqrt(2)
	 * 
	 * @param exp
	 */
	public void setExplorationConstant(double exp) {
		explorationConstant = exp;
	}

	/**
	 * This is multiplied by the pessimistic bounds of any
	 * considered move during selection.	 
	 * @param b
	 */
	public void setPessimisticBias(double b) {
		pessimisticBias = b;
	}

	/**
	 * This is multiplied by the optimistic bounds of any
	 * considered move during selection.
	 * @param b
	 */
	public void setOptimisticBias(double b) {
		optimisticBias = b;
	}

	public void setTimeDisplay(boolean displayTime) {
		this.trackTime = displayTime;
	}
}
