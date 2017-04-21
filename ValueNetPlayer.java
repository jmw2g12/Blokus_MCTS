import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class ValueNetPlayer extends Player{
	ValueNet vn;
	public ValueNetPlayer(Board board, Random rand, ArrayList<Piece> pieces, String pieceCode, ArrayList<Player> allPlayers, int startingCorner){
		super(board,rand,pieces,pieceCode,allPlayers,startingCorner);
		piecesRemaining = new ArrayList<Piece>(pieces);
		piecesOnBoard = new ArrayList<Piece>();
		strategy = "valuenet";
		this.vn = new ValueNet();
	}
	public Piece choosePiece(){
		double bestScore = 0.0;
		double pieceScore = 0.0;
		Board cloned;
		ArrayList<Piece> best = new ArrayList<Piece>();
		for (Piece p : possibleMoves){
			cloned = board.clone();
			cloned.putPieceOnBoard(p,pieceCode);
			pieceScore = vn.getValue(cloned,startingCorner == 1);
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
}
