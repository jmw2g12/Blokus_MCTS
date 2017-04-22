import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;
public abstract class Player{
	String strategy;
	Board board;
	int startingCorner;
	boolean finished = false;
	ArrayList<Piece> pieces;
	ArrayList<Piece> piecesOnBoard;
	ArrayList<Piece> piecesRemaining;
	ArrayList<Player> allPlayers;
	String pieceCode;
	ArrayList<Pair<Block,Integer>> cornerBlocks = new ArrayList<Pair<Block,Integer>>();
	ArrayList<Pair<Block,Integer>> connectableBlocks = new ArrayList<Pair<Block,Integer>>();
	ArrayList<Piece> possibleMoves = new ArrayList<Piece>();
	Random rand = new Random();
	boolean firstMove = true;
	
	public Player(){}
	public Player(Board board, Random rand, ArrayList<Piece> pieces, String pieceCode, ArrayList<Player> allPlayers, int startingCorner){
		this.board = board;
		this.pieces = pieces;
		this.piecesRemaining = new ArrayList<Piece>(pieces);
		this.piecesOnBoard = new ArrayList<Piece>();
		this.pieceCode = pieceCode;
		this.allPlayers = allPlayers;
		this.rand = rand;
		this.startingCorner = startingCorner;
	}
	
	public int numberOfMoves(){
		return piecesOnBoard.size();
	}
	public int getStartingCorner(){
		return startingCorner;
	}
	public void placeStarterBlock(){
		switch (startingCorner){
			case 1 :
				Piece bottomLeftStarter = pieces.get(0).clone();
				bottomLeftStarter.blocks.get(0).starterBlock = true;
				bottomLeftStarter.placePiece(bottomLeftStarter.blocks.get(0),new Coord(-1,-1));
				board.putStartingPieceOnBoard(bottomLeftStarter,pieceCode);
				break;
			case 2 :
				Piece topLeftStarter = pieces.get(0).clone();
				topLeftStarter.blocks.get(0).starterBlock = true;
				topLeftStarter.placePiece(topLeftStarter.blocks.get(0),new Coord(-1,board.getBoardSize()));
				board.putStartingPieceOnBoard(topLeftStarter,pieceCode);
				break;
			case 3 :
				Piece topRightStarter = pieces.get(0).clone();
				topRightStarter.blocks.get(0).starterBlock = true;
				topRightStarter.placePiece(topRightStarter.blocks.get(0),new Coord(board.getBoardSize(),board.getBoardSize()));
				board.putStartingPieceOnBoard(topRightStarter,pieceCode);
				break;
			case 4 :
				Piece bottomRightStarter = pieces.get(0).clone();
				bottomRightStarter.blocks.get(0).starterBlock = true;
				bottomRightStarter.placePiece(bottomRightStarter.blocks.get(0),new Coord(board.getBoardSize(),-1));
				board.putStartingPieceOnBoard(bottomRightStarter,pieceCode);
				break;
		}
		firstMove = false;
	}
	public String getStrategy(){ return strategy; }
	public String getPieceCode(){ return pieceCode; }
	public boolean isNumeric(String str){ return str.matches("[+-]?\\d*(\\.\\d+)?"); }
	public boolean isFinished(){ return finished; }
	public ArrayList<Piece> getPiecesRemaining(){ return piecesRemaining; }
	public boolean takeMove(){	
		if (firstMove) placeStarterBlock();
		if (strategy.equals("human")) updatePieceIDs();
		Piece p;
		possibleMoves = board.getMoves(this);
		if (possibleMoves.size() == 0){
			finished = true;
			return false;
		}
		p = choosePiece();
		if (p == null){
			finished = true;
			return false;
		}
		board.putPieceOnBoard(p,pieceCode);
		removePiece(piecesRemaining.get(p.ID),true);
		piecesOnBoard.add(p);
				
		return true;
	}
	public abstract Piece choosePiece();
	public void updatePieceIDs(){
		int counter = 0;
		for (Piece p : piecesRemaining){
			p.ID = counter;
			counter++;
		}
	}
	public void removePiece(Piece piece, boolean removePermutations){
		if (removePermutations){
			for (Piece p : (ArrayList<Piece>)piecesRemaining.clone()){
				if (p.isSamePiece(piece)) piecesRemaining.remove(p);
			}
		}else{
			piecesRemaining.remove(piece);
		}
	}
}
