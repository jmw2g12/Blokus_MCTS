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
				bottomLeftStarter.blocks.get(0).starter_block = true;
				bottomLeftStarter.place_piece(bottomLeftStarter.blocks.get(0),new Coord(-1,-1));
				board.putStartingPieceOnBoard(bottomLeftStarter,pieceCode);
				break;
			case 2 :
				Piece topLeftStarter = pieces.get(0).clone();
				topLeftStarter.blocks.get(0).starter_block = true;
				topLeftStarter.place_piece(topLeftStarter.blocks.get(0),new Coord(-1,board.getHeight()));
				board.putStartingPieceOnBoard(topLeftStarter,pieceCode);
				break;
			case 3 :
				Piece topRightStarter = pieces.get(0).clone();
				topRightStarter.blocks.get(0).starter_block = true;
				topRightStarter.place_piece(topRightStarter.blocks.get(0),new Coord(board.getWidth(),board.getHeight()));
				board.putStartingPieceOnBoard(topRightStarter,pieceCode);
				break;
			case 4 :
				Piece bottomRightStarter = pieces.get(0).clone();
				bottomRightStarter.blocks.get(0).starter_block = true;
				bottomRightStarter.place_piece(bottomRightStarter.blocks.get(0),new Coord(board.getWidth(),-1));
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
		possibleMoves = moveToPieceList(board.getMoves(this));
		//System.out.println("num of possible moves: " + possibleMoves.size());
		//System.out.println(countTrue(board.getPiecesDown(this)) + " == " + piecesOnBoard.size());
		//printBools(board.getPiecesDown(this));
		if (possibleMoves.size() == 0){
			finished = true;
			//System.out.println("There are no more moves available! Player in " + startingCorner + " is finished.");
			return false;
		}
		p = choosePiece();
		if (p == null){
			finished = true;
			//System.out.println("There is no move chosen! Player in " + startingCorner + " is finished.");
			return false;
		}
		board.putPieceOnBoard(p,pieceCode);
		removePiece(piecesRemaining.get(p.ID),true);
		piecesOnBoard.add(p);
				
		return true;
	}
	public ArrayList<Piece> moveToPieceList(ArrayList<Move> ml){
		ArrayList<Piece> pl = new ArrayList<Piece>();
		for (Move m : ml){
			pl.add(m.getPiece());
		}
		return pl;
	}
	public int countTrue(ArrayList<Boolean> bl){
		int count = 0;
		for (Boolean b : bl){
			if (b) count++;
		}
		return count;
	}
	public void printBools(ArrayList<Boolean> bl){
		int count = 0;
		for (Boolean b : bl){
			if (b) System.out.print("#" + count + "   ");
			count++;
		}
		System.out.println();
	}
	public abstract Piece choosePiece();
	public void removePiece(Piece piece, boolean removePermutations){
		if (removePermutations){
			for (Piece p : (ArrayList<Piece>)piecesRemaining.clone()){
				if (p.isSamePiece(piece)) piecesRemaining.remove(p);
			}
		}else{
			piecesRemaining.remove(piece);
		}
	}
	public void updatePieceIDs(){
		int counter = 0;
		for (Piece p : piecesRemaining){
			p.ID = counter;
			counter++;
		}
	}
	public void printRemainingPieces(){
		int counter = 0;
		for (Piece p : piecesRemaining){
			System.out.println("Piece " + counter + ": ");
			//p.print_piece();
			for (String s : p.getPieceDiagram(" ")){
				System.out.println(s);
			}
			System.out.println("");
			counter++;
		}
	}
	public String nBlanks(int n){
		String s = "";
		for (int i = 0; i < n; i++){
			s += " ";
		}
		return s;
	}
	public void padPieceDiagram(ArrayList<String> pieceArray, int rows){ //has 3 things in, size = 3, index up to 2, rows=5, pad with 2 rows, up until index = 4
		pieceArray.ensureCapacity(rows);
		int cols = pieceArray.get(0).length();
		for (int i = 0; i < rows; i++){
			if (i >= pieceArray.size()){
				pieceArray.add(nBlanks(cols));
			}
		}
	}
	public ArrayList<Piece> printPiecesInLine(ArrayList<Piece> piecesRemaining, int maxColumns, int xSpacing, int ySpacing, boolean uniqueOnly, boolean representationOnly){
		ArrayList<Piece> pieceList = new ArrayList<Piece>();
		ArrayList<String> printArray = new ArrayList<String>();
		ArrayList<String> pieceDiagram = new ArrayList<String>();
		ArrayList<Piece> printList;
		if (uniqueOnly){
			printList = getUniquePiecesFromOrderedList(piecesRemaining);
		}else{
			printList = piecesRemaining;
		}
		int columnCounter = 0;
		int maxRows = 0;
		int pieceCounter = 0;
 		for (Piece p : printList){
 			pieceDiagram = p.getPieceRepresentation(" ",Character.toString((char)248),false);
 			if (pieceDiagram.get(0).length() + columnCounter > maxColumns){
 				//print out pieces up until now
 				printArray = new ArrayList<String>(maxRows);
 				for (int i = 0; i < maxRows; i++){
 					printArray.add("");
 				}
 				for (Piece toPrint : pieceList){
 					pieceDiagram = toPrint.getPieceRepresentation(" ",Character.toString((char)248),true,pieceList.indexOf(toPrint)+pieceCounter);
 					padPieceDiagram(pieceDiagram,maxRows);
 					for (int i = 0; i < maxRows; i++){
 						printArray.set(i,printArray.get(i)+pieceDiagram.get(i)+nBlanks(xSpacing));
 					}
 				}
 				for (String s : printArray){
 					System.out.println(s);
 				}
 				for (int i = 0; i < ySpacing; i++){
 					System.out.println(nBlanks(columnCounter));
 				}
 				
 				//reset 2 arraylists and col/row counters
 				pieceCounter += pieceList.size();
 				pieceList = new ArrayList<Piece>();
 				maxRows = 0;
 				columnCounter = 0;
 			}
 			pieceDiagram = p.getPieceRepresentation(" ",Character.toString((char)248),false);
 			pieceList.add(p);
 			columnCounter += pieceDiagram.get(0).length();
 			if (pieceDiagram.size() > maxRows) maxRows = pieceDiagram.size();
 		}
 		//print out pieces up until now
 		printArray = new ArrayList<String>(maxRows);
 		for (int i = 0; i < maxRows; i++){
 			printArray.add("");
 		}
 		for (Piece toPrint : pieceList){
 			pieceDiagram = toPrint.getPieceRepresentation(" ",Character.toString((char)248),!representationOnly,pieceList.indexOf(toPrint)+pieceCounter); //changed here! was true
 			padPieceDiagram(pieceDiagram,maxRows);
 			for (int i = 0; i < maxRows; i++){
 				printArray.set(i,printArray.get(i)+pieceDiagram.get(i)+nBlanks(xSpacing));
 			}
 		}
 		for (String s : printArray){
 			System.out.println(s);
 		}
 		return printList;
	}
	public ArrayList<Piece> getUniquePiecesFromOrderedList(ArrayList<Piece> list){
		ArrayList<Piece> result = new ArrayList<Piece>();
		boolean first = true;
		Piece prev = list.get(0);
		for (Piece p : list){
			if (first == false){
				if (!p.isSamePiece(prev)){
					result.add(p);
				}
			}else{
				result.add(p);
			}
			first = false;
			prev = p;
		}
		return result;
	}
}
