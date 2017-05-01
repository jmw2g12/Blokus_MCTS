import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class PolicyNetPlayer extends Player{
	private PolicyNet pn;
	public PolicyNetPlayer(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner){
		super(board,pieces,pieceCode,startingCorner);
		this.pn = new PolicyNet();
		strategy = "policynet";
	}
	public Piece choosePiece(ArrayList<Piece> possibleMoves){
		Double bestScore = Double.NEGATIVE_INFINITY;
		double pieceScore = 0.0;
		Board cloned;
		ArrayList<Piece> best = new ArrayList<Piece>();
		for (Piece p : possibleMoves){
			cloned = board.clone();
			cloned.putPieceOnBoard(p,pieceCode);
			pieceScore = pn.getValue(cloned,startingCorner == 1);
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
