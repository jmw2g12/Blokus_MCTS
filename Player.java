import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public abstract class Player{
	public String strategy;
	public Board board;
	public int startingCorner;
	public boolean finished = false;
	public ArrayList<Piece> pieces;
	public ArrayList<Piece> piecesOnBoard;
	public ArrayList<Piece> piecesRemaining;
	public String pieceCode;
	public Random rand = new Random();
	public boolean firstMove = true;
	
	public Player(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner){
		this.board = board;
		this.pieces = pieces;
		this.piecesRemaining = new ArrayList<Piece>(pieces);
		this.piecesOnBoard = new ArrayList<Piece>();
		this.pieceCode = pieceCode;
		this.rand = new Random();
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
		updatePieceIDs();
		Piece p;
		ArrayList<Piece> possibleMoves = board.getMoves(this);
		if (possibleMoves.size() == 0){
			finished = true;
			return false;
		}
		p = choosePiece(possibleMoves);
		if (p == null){
			finished = true;
			return false;
		}
		board.putPieceOnBoard(p,pieceCode);
		removePiece(piecesRemaining.get(p.ID));
		piecesOnBoard.add(p);
				
		return true;
	}
	public abstract Piece choosePiece(ArrayList<Piece> possibleMoves);
	public void updatePieceIDs(){
		int counter = 0;
		for (Piece p : piecesRemaining){
			p.ID = counter;
			counter++;
		}
	}
	public void removePiece(Piece piece){
		for (Piece p : (ArrayList<Piece>)piecesRemaining.clone()){
			if (p.getPieceNumber() == piece.getPieceNumber()) piecesRemaining.remove(p);
		}
	}
}
