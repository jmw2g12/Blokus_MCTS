import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;
import java.util.Arrays;

public class HumanPlayer extends Player{
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public HumanPlayer(Board board, ArrayList<Piece> pieces, String pieceCode, int startingCorner){
		super(board,pieces,pieceCode,startingCorner);
		strategy = "human";
	}
	
	public Piece choosePiece(ArrayList<Piece> possibleMoves){ 
		return acceptUserInput(possibleMoves);
	}
	public String[] acceptValues(int numOfConnectors){
		String[] input = {""};
		while(true){
			System.out.println("");
			System.out.println("Please enter: piece - block - connector");
	
			String line = "";
			try{
				line = reader.readLine();
				input = line.split(" ");
			}catch (IOException ioe){
				continue;
			}
			if (input.length >= 3 && isNumeric(input[0]) && isNumeric(input[1]) && isNumeric(input[2])){
				if (Integer.parseInt(input[0]) < piecesRemaining.size() && Integer.parseInt(input[0]) >= 0){
					if (Integer.parseInt(input[1]) < piecesRemaining.get(Integer.parseInt(input[0])).blocks.size() && Integer.parseInt(input[1]) >= 0){
						if (Integer.parseInt(input[2]) <= numOfConnectors && Integer.parseInt(input[2]) >= 1){
							break;
						}else{
							System.out.println("Invalid input! Choose one of the connectors on the board, " + ((numOfConnectors == 1) ? ("of which there is only 1.") : (" from 1 to " + numOfConnectors + ".")));
						}
					}else{
						System.out.println("Invalid input! Piece " + input[0] + " only has " + piecesRemaining.get(Integer.parseInt(input[0])).blocks.size() + ((piecesRemaining.get(Integer.parseInt(input[0])).blocks.size()==1) ? " block." : " blocks."));
					}
				}else{
					System.out.println("Invalid input! Please enter a valid piece ID (<=" + (piecesRemaining.size()-1) + ").");
				}
			}else{
				if (input.length >= 1 && input[0].equals("q")){
					System.out.println('\n' + "Goodbye!" + '\n' + '\n');
					System.exit(0);
				}else if (input[0].equals("resign")){
					return null;
				}
				System.out.println("Invalid input! Please enter 3 digits.");
			}
		}
		return input;
	}
	public Piece acceptUserInput(ArrayList<Piece> possibleMoves){
		
		ArrayList<Pair<Block,Integer>> connectableBlocks = board.getConnectableBlocks(board.getCornerBlocks(pieceCode),pieceCode);
		printBoardAndOptions(connectableBlocks);
		
		Piece p;
		String[] input = {""};
		
		while(true){
			
			input = acceptValues(connectableBlocks.size());
			if (input == null) return null;
			p = piecesRemaining.get(Integer.parseInt(input[0])).clone();
			Block bl = p.blocks.get(Integer.parseInt(input[1]));
			Block con = connectableBlocks.get(Integer.parseInt(input[2])-1).getL();
			Integer dir = connectableBlocks.get(Integer.parseInt(input[2])-1).getR();
			
			Coord c = new Coord();
			if (dir == 1){
				c = new Coord(con.coordinate.x+1,con.coordinate.y+1);
			}else if (dir == 2){
				c = new Coord(con.coordinate.x+1,con.coordinate.y-1);
			}else if (dir == 3){
				c = new Coord(con.coordinate.x-1,con.coordinate.y-1);
			}else if (dir == 4){
				c = new Coord(con.coordinate.x-1,con.coordinate.y+1);
			}
			p.placePiece(bl,c);
		
			if (!board.doesPieceFit(p,pieceCode)){
				System.out.println("Please enter the id of a piece that fits!");
			}else{
				System.out.println("");
				break;
			}
		}
		return p;
	}
	public void printBoardAndOptions(ArrayList<Pair<Block,Integer>> connectableBlocks){
		System.out.println();
		System.out.println();
		board.printOptionsBoard(pieceCode, connectableBlocks);
		System.out.println();
		printPiecesInLine(piecesRemaining, 120, 3, 0);
	}
	public String nBlanks(int n){
		String s = "";
		for (int i = 0; i < n; i++){
			s += " ";
		}
		return s;
	}
	public void padPieceDiagram(ArrayList<String> pieceArray, int rows){ 
		pieceArray.ensureCapacity(rows);
		int cols = pieceArray.get(0).length();
		for (int i = 0; i < rows; i++){
			if (i >= pieceArray.size()){
				pieceArray.add(nBlanks(cols));
			}
		}
	}
	public ArrayList<Piece> printPiecesInLine(ArrayList<Piece> piecesRemaining, int maxColumns, int xSpacing, int ySpacing){
		ArrayList<Piece> pieceList = new ArrayList<Piece>();
		ArrayList<String> printArray = new ArrayList<String>();
		ArrayList<String> pieceDiagram = new ArrayList<String>();

		int columnCounter = 0;
		int maxRows = 0;
		int pieceCounter = 0;
 		for (Piece p : piecesRemaining){
 			pieceDiagram = p.getPieceRepresentation(" ",Character.toString((char)248),false);
 			if (pieceDiagram.get(0).length() + columnCounter > maxColumns){
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
 		return piecesRemaining;
	}
}
