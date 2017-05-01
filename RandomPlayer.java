import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class RandomPlayer extends Player{
	public RandomPlayer(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner){
		super(board,pieces,pieceCode,startingCorner);
		strategy = "random";
	}
	public Piece choosePiece(ArrayList<Piece> possibleMoves){
		int n = rand.nextInt(possibleMoves.size());
		return possibleMoves.get(n);
	}
}
