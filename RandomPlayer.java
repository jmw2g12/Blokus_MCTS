import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class RandomPlayer extends Player{
	
	public RandomPlayer(Board board, Random rand, ArrayList<Piece> pieces, String pieceCode, ArrayList<Player> allPlayers, int startingCorner){
		super(board,rand,pieces,pieceCode,allPlayers,startingCorner);
		piecesRemaining = new ArrayList<Piece>(pieces);
		piecesOnBoard = new ArrayList<Piece>();
		strategy = "random";
	}
	public Piece choosePiece(){
		int n = rand.nextInt(possibleMoves.size());
		return possibleMoves.get(n);
	}
	public Player clone(){
		return new RandomPlayer(board,rand,new ArrayList<Piece>(pieces),pieceCode,allPlayers,startingCorner);
	}

}
