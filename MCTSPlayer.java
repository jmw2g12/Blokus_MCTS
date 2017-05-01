import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class MCTSPlayer extends Player{
	private MCTS mcts;
	private boolean limitByTime = true;
	private long moveTime = 30000;
	private int iterations = 400;
	private double iteration_multiplication_factor = 1.3;
	private String scoringMethod = "difference";
	private String weightingMethod = "size";
	private boolean startingPlayout = true;
	private String finalSelect = "max";
	
	public MCTSPlayer(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner, boolean startingPlayout, int limit, double explorationConstant, String weightingMethod, String scoringMethod, String finalSelect){
		super(board,pieces,pieceCode,startingCorner);
		this.mcts = new MCTS(this, explorationConstant, weightingMethod, scoringMethod,limitByTime,finalSelect);
		
		this.weightingMethod = weightingMethod;
		this.scoringMethod = scoringMethod;
		this.startingPlayout = startingPlayout;
		this.finalSelect = finalSelect;
		
		if (limitByTime){
			this.moveTime = limit;
		}else{
			this.iterations = limit;
		}
		
		this.strategy = "mcts_" + (startingPlayout ? "playout" : "noplayout") + "_" + (limitByTime ? moveTime : iterations) + "_" + explorationConstant + "_" + scoringMethod + "_" + weightingMethod + "_" + finalSelect;
	}
	public Piece choosePiece(ArrayList<Piece> possibleMoves){
		board.setCurrentPlayer(this);
		if (piecesOnBoard.size() >= 4 || !startingPlayout){
			long startTime = System.currentTimeMillis();
			Piece p = mcts.runMCTS(board, (limitByTime ? moveTime : iterations));
			if (!limitByTime){
				//System.out.println("Time elapsed : " + (System.currentTimeMillis() - startTime));
				iterations = (int)Math.round((double)iterations * iteration_multiplication_factor);
			}
			if (p == null) return null;
			return p;
		}else{
			return chooseSetPlayPiece(possibleMoves);
		}
	}
	public Piece chooseSetPlayPiece(ArrayList<Piece> possibleMoves){
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
		int n = rand.nextInt(best.size());
		return best.get(n);
	}
	public int explorationProductScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getBoardSize()-1,board.getBoardSize()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.productScore(b.coordinate);
		}
		return score;
		
	}
}
