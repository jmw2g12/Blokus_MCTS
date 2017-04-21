import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class MCTSPlayer extends Player{
	MCTS mcts;
	int iterations = 100;
	double iteration_multiplication_factor = 1.3;
	String scoringMethod;
	String weightingMethod = "size";
	
	public MCTSPlayer(Board board, Random rand, ArrayList<Piece> pieces, String pieceCode, ArrayList<Player> allPlayers, int startingCorner, int iterations, String scoringMethod, String weightingMethod){
		super(board,rand,pieces,pieceCode,allPlayers,startingCorner);
		piecesRemaining = new ArrayList<Piece>(pieces);
		piecesOnBoard = new ArrayList<Piece>();
		this.weightingMethod = weightingMethod;
		strategy = "mcts_" + Integer.toString(iterations) + "_" + scoringMethod + "_" + weightingMethod;
		mcts = new MCTS(this, weightingMethod);
		this.iterations = iterations;
		this.scoringMethod = scoringMethod;
	}
	public Piece choosePiece(){
		board.setCurrentPlayer(this);
		if (piecesOnBoard.size() >= 3){
			Move m = mcts.runMCTS(board, iterations, false, scoringMethod);
			iterations = (int)Math.round((double)iterations * iteration_multiplication_factor);
			if (m == null) return null;
			return m.getPiece();
		}else{
			return chooseSetPlayPiece();
		}
	}
	public int explorationScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getWidth()-1,board.getHeight()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.manhattanDistance(b.coordinate);
		}
		return score;
		
	}
	public int sizeScore(Piece p){
		return p.blocks.size();
	}
	public Piece chooseSetPlayPiece(){
		int bestScore = 0;
		int pieceScore = 0;
		Board cloned;
		ArrayList<Piece> best = new ArrayList<Piece>();
		for (Piece p : possibleMoves){
			pieceScore = explorationProductScore(p);
			if (pieceScore > bestScore){
				best.clear();
				best.add(p);
				bestScore = pieceScore;
			}else if (pieceScore == bestScore){
				best.add(p);
			}
		}
		//System.out.println("finished testing board");
		int n = rand.nextInt(best.size());
		return best.get(n);
	}
	public int explorationProductScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getWidth()-1,board.getHeight()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.productScore(b.coordinate);
		}
		return score;
		
	}
}
