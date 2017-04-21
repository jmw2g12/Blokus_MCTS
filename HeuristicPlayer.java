import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class HeuristicPlayer extends Player{
	public HeuristicPlayer(Board board, Random rand, ArrayList<Piece> pieces, String pieceCode, ArrayList<Player> allPlayers, int startingCorner){
		super(board,rand,pieces,pieceCode,allPlayers,startingCorner);
		piecesRemaining = new ArrayList<Piece>(pieces);
		piecesOnBoard = new ArrayList<Piece>();
		strategy = "heuristic";
	}
	public Player clone(){
		return new HeuristicPlayer(board,rand,new ArrayList<Piece>(pieces),pieceCode,allPlayers,startingCorner);
	}
	public Piece choosePiece(){
		int bestScore = 0;
		int pieceScore = 0;
		//Board cloned;
		ArrayList<Piece> best = new ArrayList<Piece>();
		for (Piece p : possibleMoves){
			//cloned = board.clone();
			//cloned.putPieceOnBoard(p,pieceCode);
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
		//System.out.println("-------------------------------------------");
		return best.get(n);
	}
	public int explorationManhattanScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getWidth()-1,board.getHeight()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.manhattanDistance(b.coordinate);
		}
		return score;
		
	}
	public int explorationProductScore(Piece p){
		Coord startingCoord = (startingCorner == 1 ? new Coord(0,0) : new Coord(board.getWidth()-1,board.getHeight()-1));
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
