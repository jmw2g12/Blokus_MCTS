import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class HeuristicPlayer extends Player{
	public HeuristicPlayer(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner){
		super(board,pieces,pieceCode,startingCorner);
		strategy = "heuristic";
	}
	public Piece choosePiece(ArrayList<Piece> possibleMoves){
		int bestScore = 0;
		int pieceScore = 0;
		ArrayList<Piece> best = new ArrayList<Piece>();
		for (Piece p : possibleMoves){
			pieceScore = board.explorationHeatMapScore(p);
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
	public int explorationManhattanScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getBoardSize()-1,board.getBoardSize()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.manhattanDistance(b.coordinate);
		}
		return score;
		
	}
	public int explorationProductScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getBoardSize()-1,board.getBoardSize()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.productScore(b.coordinate);
		}
		return score;
		
	}
	public int sizeScore(Piece p){
		return p.blocks.size();
	}
}
