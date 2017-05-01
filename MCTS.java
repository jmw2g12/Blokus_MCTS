
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
	
	private Player player;
	
	private PolicyNet pn;

	public MCTS(Player player, double explorationConstant, String weightingMethod, String scoringMethod, boolean limitByTime, String finalSelect) {
		random = new Random();
		this.explorationConstant = explorationConstant;
		this.weightingMethod = weightingMethod;
		this.scoringMethod = scoringMethod;
		this.limitByTime = limitByTime;
		this.finalSelect = finalSelect;
		this.player = player;
		pn = new PolicyNet();
	}
	
	public Piece runMCTS(Board startingBoard, long limit) {
		rootNode = new Node(startingBoard);
		
		if (limitByTime){
			long startTime = System.currentTimeMillis();
			
			while (System.currentTimeMillis() - startTime < limit) {
				select(startingBoard.clone(), rootNode);
			}
		}else{
			for (int i = 0; i < limit; i++) {
				select(startingBoard.clone(), rootNode);
			}
		}

		return finalSelect(rootNode);
	}
	public double[] getScore(Board b){
		if (scoringMethod.equals("binary")){
			return b.getBinaryScore();
		}else if (scoringMethod.equals("difference")){
			return b.getDifferenceScore();
		}else{
			return b.getDifferenceScore();
		}
	}
	
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
				
				if (!node.unvisitedChildren.isEmpty()) { 
					Node temp = node.unvisitedChildren.remove(random.nextInt(node.unvisitedChildren.size()));
					node.children.add(temp);
					b.makeMove(temp.move);
					return new AbstractMap.SimpleEntry<Board, Node>(b, temp);
				} else {
					ArrayList<Node> bestNodes = node.select(explorationConstant);
					Node finalNode = bestNodes.get(random.nextInt(bestNodes.size()));
					node = finalNode;
					b.makeMove(finalNode.move);
				}
			}
		}
	}
	
	private Piece finalSelect(Node n) {
		double bestValue = Double.NEGATIVE_INFINITY;
		double tempBest;
		ArrayList<Node> bestNodes = new ArrayList<Node>();
		Board cloned;
		
		for (Node s : n.children) {
			cloned = player.board.clone();
			cloned.makeMove(s.move,cloned.getCurrentPlayer());
			
			if (finalSelect.equals("robust")){
				tempBest = s.games;
			}else if (finalSelect.equals("max")){
				tempBest = s.score[n.player];
			}else{
				tempBest = Math.pow(s.score[n.player],1.2)/s.games;
			}
			
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
	
	private double[] playout(Node state, Board board) {
		ArrayList<Piece> moves;
		Piece mv;
		Board brd = board.clone();
		while (!brd.gameOver()) {
			moves = brd.getMoves();
			mv = getRandomMove(brd,moves);
			int currentPlayer = brd.getCurrentPlayer();
			brd.makeMove(mv,brd.getCurrentPlayer());
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
		}else if (weightingMethod.equals("valuenet") || weightingMethod.equals("value") || weightingMethod.equals("policynet") || weightingMethod.equals("policy")){
			double[] result = new double[moves.size()];
			Board temp;
			for (Piece m : moves){
				temp = b.clone();
				b.putPieceOnBoard(m,player.getPieceCode());
				result[moves.indexOf(m)] = pn.getValue(b,player.getStartingCorner() == 1);
			}
			return result;
		}else{
			return new double[moves.size()];
		}
	}
}
