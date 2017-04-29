
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class MCTS {
	private Random random;
	private Node rootNode;
	
	private String scoringMethod;
	private String weightingMethod = "";
	private double explorationConstant = Math.sqrt(2.0);
	private boolean limitByTime = false;
	private String finalSelect = "";
	
	private Player p;
	
	PolicyNet vn;

	public MCTS(Player p, double explorationConstant, String weightingMethod, String scoringMethod, boolean limitByTime, String finalSelect) {
		random = new Random();
		this.explorationConstant = explorationConstant;
		this.weightingMethod = weightingMethod;
		this.scoringMethod = scoringMethod;
		this.limitByTime = limitByTime;
		this.finalSelect = finalSelect;
		this.p = p;
		vn = new PolicyNet();
	}

	/**
	 * Run a UCT-MCTS simulation for a number of iterations.
	 * 
	 * @param startingBoard starting board
	 * @param runs how many iterations to think
	 * @param bounds enable or disable score bounds.
	 * @return
	 */
	public Piece runMCTS(Board startingBoard, long limit) {
		rootNode = new Node(startingBoard);
		
		if (limitByTime){
			long startTime = System.currentTimeMillis();
			int printed = 0;
			int printTotal = 100;
			int shouldPrint = 0;
			//System.out.print("|");
			//for (int i = 0; i < printTotal; i++){
				//System.out.print("-");
			//}
			//System.out.println("|");
			//System.out.print(" ");
			while (System.currentTimeMillis() - startTime < limit) {
				select(startingBoard.duplicate(), rootNode);
				//shouldPrint = Math.round(100*(System.currentTimeMillis()-startTime)/limit);
				//for (int i = printed; i < shouldPrint; i++){
					//System.out.print("~");
				//}
				//printed = shouldPrint;
			}
			//System.out.println("");
		}else{
			for (int i = 0; i < limit; i++) {
				select(startingBoard.duplicate(), rootNode);
			}
		}

		return finalSelect(rootNode);
	}
	public double[] getScore(Board b){
		if (scoringMethod.equals("binary")){
			return b.getBinaryScore();
		}else if (scoringMethod.equals("difference")){
			return b.getScore();
		}else{
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
				}
				
				if (!node.unvisitedChildren.isEmpty()) { //there are still unvisited children
					Node temp = node.unvisitedChildren.remove(random.nextInt(node.unvisitedChildren.size()));
					node.children.add(temp);
					//System.out.println("temp.player = " + temp.player + ", b.getCurrentPlayer() = " + b.getCurrentPlayer());
					b.makeMove(temp.move);
					return new AbstractMap.SimpleEntry<Board, Node>(b, temp);
				} else {	//all children have been visited
					//System.out.println("------------ here ------------");
					ArrayList<Node> bestNodes = node.select(explorationConstant);
					Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
					node = finalNode;
					//System.out.println("node.player = " + node.player + ", b.getCurrentPlayer() = " + b.getCurrentPlayer());
					b.makeMove(finalNode.move);
					//System.out.println("---");
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
		Board cloned;
		
		//System.out.println("Final choice: ------------------------------------");
		for (Node s : n.children) {
			cloned = p.board.clone();
			cloned.makeMove(s.move,cloned.getCurrentPlayer());
			//cloned.print();
			if (finalSelect.equals("robust")){
				tempBest = s.games;
			}else if (finalSelect.equals("max")){
				tempBest = s.score[n.player];
			}else{
				tempBest = Math.pow(s.score[n.player],1.2)/s.games;
			}
			//System.out.println("s.games = " + s.games + ", s.score[n.player] = " + s.score[n.player]);
			//System.out.println("score : " + tempBest);
			if (tempBest > bestValue) {
				bestNodes.clear();
				bestNodes.add(s);
				bestValue = tempBest;
			} else if (tempBest == bestValue) {
				bestNodes.add(s);
			}
		}
		
		if (bestNodes.size() == 0) return null;
		
		//System.out.println("Chose: --------");
		Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
		//cloned = p.board.clone();
		//cloned.makeMove(finalNode.move,cloned.getCurrentPlayer());
		//cloned.print();
		//System.out.println("finalNode.games = " + finalNode.games + ", finalNode.score[n.player] = " + finalNode.score[n.player]);
		//tempBest = Math.pow(finalNode.score[n.player],2.2)/finalNode.games;
		//System.out.println("score : " + tempBest);
		
		return finalNode.move;
	}

	/**
	 * Playout function for MCTS
	 * 
	 * @param state
	 * @return
	 */
	private double[] playout(Node state, Board board) {
		//System.out.println("running playout");
		ArrayList<Piece> moves;
		Piece mv;
		Board brd = board.duplicate();
		while (!brd.gameOver()) {
			moves = brd.getMoves();
			mv = getRandomMove(brd,moves);
			int currentPlayer = brd.getCurrentPlayer();
			brd.makeMove(mv,brd.getCurrentPlayer());
			//System.out.println("move for player " + currentPlayer);
			//brd.print();
		}
		return getScore(brd);
	}

	private Piece getRandomMove(Board board, ArrayList<Piece> moves) {
		double[] weights = getMoveWeights(moves, board);
		
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
	
	public double[] getMoveWeights(ArrayList<Piece> moves, Board b) {
	
		if (weightingMethod.equals("size")){
			double[] result = new double[moves.size()];
			for (Piece m : moves){
				result[moves.indexOf(m)] = Math.pow((double)m.getSize(),1.5);
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
			return new double[moves.size()];
		}
	}
}
