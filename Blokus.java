import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;
public class Blokus{
	final int boardSize = 14;
	final int maxPieceSize = 5;

	ArrayList<Piece> pieces = new ArrayList<Piece>();
	ArrayList<Player> players = new ArrayList<Player>();
	Random rand = new Random();
	Board board = new Board(boardSize);
	
	public static void main(String[] args){
	
		Blokus b = new Blokus();
		b.initPieces();
		
		b.rand = new Random(System.currentTimeMillis());
		Player p1,p2;
		
		PolicyNet vn = new PolicyNet();
		
		if (args.length >= 2){
			p1 = b.generatePlayer(args[0],"1",1);
			p2 = b.generatePlayer(args[1],"2",3);
		}else{
			System.out.println("Program requires at least 2 arguments");
			System.out.println("Remember MCTS players take args:");
			System.out.println("boolean startingPlayout, int limit, double explorationConstant, String weightingMethod, String scoringMethod, String finalSelect");
			return;
		}

		b.players.add(p1);
		b.players.add(p2);
		b.board.setPlayers(b.players);
		b.board.setPieces(b.pieces);
		
		
		int finishedCount = 0;	
		while(finishedCount < b.players.size()){
			for (Player p : b.players){
				if (!p.isFinished()){
					//b.board.print();
					//System.out.println(b.board.getMoves().size());
					if (!p.takeMove()) finishedCount++;
				}
			}
		}
		//b.board.saveScoresToFile(p1,p2);
		//b.board.savePlayingDataToFile(p1,p2);
		
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (Player p : b.players){
			scores.add(b.board.blocksOnBoard(p.getPieceCode()));
		}
		//System.out.println("Scores :");
		for (int i = 0; i < 2; i++){
			System.out.println("Player " + i + " ( " + b.players.get(i).strategy + " ) = " + scores.get(i));
		}
	}
	public class InvalidPlayerException extends Exception{
		public InvalidPlayerException (String msg){
			super(msg);
		}
	}
	public Player generatePlayer(String strategy, String pieceCode, int corner){
		Player p;
		if (strategy.equals("human")){
			return new HumanPlayer(board,rand,pieces,pieceCode,players,corner);
		}else if (strategy.equals("random")){
			return new RandomPlayer(board,rand,pieces,pieceCode,players,corner);
		}else if (strategy.equals("policynet") || strategy.equals("policy")){
			return new PolicyNetPlayer(board,rand,pieces,pieceCode,players,corner);
		}else if (strategy.equals("heuristic")){
			return new HeuristicPlayer(board,rand,pieces,pieceCode,players,corner);
		}else if (strategy.equals("mctstester")){
			return new MCTSTesterPlayer(board,rand,pieces,pieceCode,players,corner);
		}else if (strategy.startsWith("mcts")){
			return new MCTSPlayer(board,rand,pieces,pieceCode,players,corner,strategy.split("_")[1].equals("playout"),Integer.parseInt(strategy.split("_")[2]),Double.parseDouble(strategy.split("_")[3]),strategy.split("_")[4],strategy.split("_")[5],strategy.split("_")[6]);
		}
		System.out.println("*** Invalid player strategy given! ***");
		return null;
	}
	public void printAllPieces(){
		int counter = 0;
		for (Piece p : pieces){
			System.out.println("Piece " + counter + ": ");
			p.printPiece();
			System.out.println("");
			counter++;
		}
	}
	public void initPieces(){
		Block b1 = new Block();
		Block b2 = new Block();
		Block b3 = new Block();
		Block b4 = new Block();
		Block b5 = new Block();
		pieces.add(new Piece(b1));
		pieces.get(pieces.size()-1).pieceNumber = 0;
		
		if (maxPieceSize == 1) return;
		
		
		b1 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		pieces.add(new Piece(b1,b2));
		pieces.get(pieces.size()-1).pieceNumber = 1;
		
		b1 = new Block();
		b2 = new Block();
		b1.addLeft(b2);
		b2.addRight(b1);
		pieces.add(new Piece(b1,b2));
		pieces.get(pieces.size()-1).pieceNumber = 1;
		
		if (maxPieceSize == 2) return;
		
		
		b1 = new Block();
		b2 = new Block();
		b1.addRight(b2);
		b2.addLeft(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 2;	
			
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 2;
				
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addRight(b3);
		b3.addLeft(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 2;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b1.addLeft(b2);
		b2.addRight(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 2;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 3;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b1.addLeft(b2);
		b2.addRight(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		pieces.add(new Piece(b1,b2,b3));
		pieces.get(pieces.size()-1).pieceNumber = 3;
		
		if (maxPieceSize == 3) return;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectLeft(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectBottom(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectTop(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		b3.addRight(b4);
		b4.addLeft(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		b3.addLeft(b4);
		b4.addRight(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addLeft(b2);
		b2.addRight(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		b3.addTop(b4);
		b4.addBottom(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addLeft(b2);
		b2.addRight(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		b3.addBottom(b4);
		b4.addTop(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 4;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addRight(b2);
		b1.addBottom(b4);
		b2.addLeft(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		b3.addLeft(b4);
		b4.addRight(b3);
		b4.addTop(b1);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 5;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addRight(b2);
		b2.addLeft(b1);
		b2.addTop(b3);
		b3.addBottom(b2);
		b2.addRight(b4);
		b4.addLeft(b2);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 6;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addRight(b2);
		b2.addLeft(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		b2.addRight(b4);
		b4.addLeft(b2);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 6;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		b2.addBottom(b4);
		b4.addTop(b2);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 6;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addRight(b3);
		b3.addLeft(b2);
		b2.addBottom(b4);
		b4.addTop(b2);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 6;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addRight(b2);
		b2.addLeft(b1);
		b2.addBottom(b3);
		b3.addTop(b2);
		b3.addRight(b4);
		b4.addLeft(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 7;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addRight(b2);
		b2.addLeft(b1);
		b2.addTop(b3);
		b3.addBottom(b2);
		b3.addRight(b4);
		b4.addLeft(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 7;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addLeft(b3);
		b3.addRight(b2);
		b3.addBottom(b4);
		b4.addTop(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 7;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.addBottom(b2);
		b2.addTop(b1);
		b2.addRight(b3);
		b3.addLeft(b2);
		b3.addBottom(b4);
		b4.addTop(b3);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 7;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 8;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		pieces.add(new Piece(b1,b2,b3,b4));
		pieces.get(pieces.size()-1).pieceNumber = 8;
		
		if (maxPieceSize == 4) return;
		
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b2.connectBottom(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b3.connectBottom(b4);
		b2.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectRight(b4);
		b3.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b3.connectBottom(b4);
		b3.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectRight(b3);
		b2.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectLeft(b3);
		b3.connectTop(b4);
		b2.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b3.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectLeft(b3);
		b3.connectTop(b4);
		b3.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 9;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 10;
		
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 10;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectBottom(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectTop(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectRight(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectLeft(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectTop(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
	
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectRight(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 11;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b3.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b3.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b3.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectTop(b4);
		b3.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b3.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b3.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b3.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectTop(b4);
		b3.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 12;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b1.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b2.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b2.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b3.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b3.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectTop(b1);
		b1.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 13;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b2.connectBottom(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 14;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b2.connectLeft(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 14;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b2.connectTop(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 14;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b2.connectRight(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 14;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectRight(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 15;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b3.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 15;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectTop(b3);
		b3.connectLeft(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 15;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectRight(b3);
		b3.connectTop(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 15;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectBottom(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 16;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectLeft(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 16;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectTop(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 16;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectRight(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 16;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 17;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 17;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 17;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 17;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 18;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 18;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 18;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 18;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectTop(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectRight(b2);
		b2.connectRight(b3);
		b3.connectRight(b4);
		b4.connectTop(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectBottom(b3);
		b3.connectBottom(b4);
		b4.connectRight(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectLeft(b2);
		b2.connectLeft(b3);
		b3.connectLeft(b4);
		b4.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectTop(b2);
		b2.connectTop(b3);
		b3.connectTop(b4);
		b4.connectLeft(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 19;
		
		b1 = new Block();
		b2 = new Block();
		b3 = new Block();
		b4 = new Block();
		b5 = new Block();
		b1.connectBottom(b2);
		b2.connectLeft(b3);
		b2.connectRight(b4);
		b2.connectBottom(b5);
		pieces.add(new Piece(b1,b2,b3,b4,b5));
		pieces.get(pieces.size()-1).pieceNumber = 20;
	}
	public void printCharacters(int to){
		System.out.println("----Start----");
		for (int i = 0; i <= to; i++){
			System.out.println(i + ":    " + ((char)i));
		}
		System.out.println("----End----");
	}

}
