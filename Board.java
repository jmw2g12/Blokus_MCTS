import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;
import java.util.Arrays;
import java.lang.Math;
import java.io.FileNotFoundException;
import java.io.IOException;
public class Board implements BoardMCTS{
	String[][] board;
	Integer boardSize;
	ArrayList<String> allPieceCodes = new ArrayList<String>();
	ArrayList<Pair<Piece,String>> piecesDown = new ArrayList<Pair<Piece,String>>();
	ArrayList<Player> players = new ArrayList<Player>();
	Player currentPlayer;
	ArrayList<Boolean> p1PiecesDown = new ArrayList<Boolean>();
	ArrayList<Boolean> p2PiecesDown = new ArrayList<Boolean>();
	ArrayList<Piece> allPieces = new ArrayList<Piece>();
	ValueNet vn = new ValueNet();
	
	public Board(int size){
		this.boardSize = size;
		this.board = new String[size][size];
		for (int i = 0; i < 21; i++){
			p1PiecesDown.add(new Boolean(false));
			p2PiecesDown.add(new Boolean(false));
		}
	}
	public Board clone(){
		Board b = new Board(boardSize);
		for (int i = 0; i < boardSize; i++){
			for (int j = 0; j < boardSize; j++){
				b.board[i][j] = cloneString(board[i][j]);
			}
		}
		b.allPieceCodes = new ArrayList<String>(allPieceCodes);
		b.piecesDown = new ArrayList<Pair<Piece,String>>(piecesDown);
		b.setPlayers(players);
		b.currentPlayer = currentPlayer;
		b.p1PiecesDown = new ArrayList<Boolean>(p1PiecesDown);
		b.p2PiecesDown = new ArrayList<Boolean>(p2PiecesDown);
		b.allPieces = allPieces;
		return b;
	}
	public Integer getBoardSize(){
		return boardSize;
	}
	public String[][] getArray(){
		return board;
	}
	public void setPlayers(ArrayList<Player> players){
		this.players = players;
		currentPlayer = players.get(0);
	}
	public void setPieces(ArrayList<Piece> pieces){
		this.allPieces = pieces;
	}
	public boolean gameOver(){
		return !doPlayersHaveRemainingMoves();
	}	
	public int getCurrentPlayer(){
		return players.indexOf(currentPlayer);
	}
	public void setCurrentPlayer(Player p){
		currentPlayer = p;
	}
	public int getQuantityOfPlayers(){
		return players.size();
	}
	public double[] getBinaryScore(){
		double[] score = new double[2];
		if (gameOver()){	//need to update if more than 2 players are playing
			int p1Score = blocksOnBoard(players.get(0).getPieceCode());
			int p2Score = blocksOnBoard(players.get(1).getPieceCode());
			if (p1Score > p2Score){
				score[0] = 1.0d;
				score[1] = 0.0d;
			}else if (p1Score < p2Score){
				score[0] = 0.0d;
				score[1] = 1.0d;
			}else{
				score[0] = 0.5d;
				score[1] = 0.5d;
			}
		}else{
			score[0] = 0.0d;
			score[1] = 0.0d;
		}
		return score;
	}
	public double[] getProductScore(){
		double[] score = new double[2];
		Coord startingCoord = new Coord(0,0);
		String p1pieceCode = players.get(0).getPieceCode();
		int p1score = 0;
		for (Pair<Piece,String> p : piecesDown){
			if (!p.getR().equals(p1pieceCode)) continue;
			for (Block b : p.getL().blocks){
				p1score += startingCoord.productScore(b.coordinate);
			}
		}
		
		startingCoord = new Coord(getWidth()-1,getHeight()-1);
		String p2pieceCode = players.get(1).getPieceCode();
		int p2score = 0;
		for (Pair<Piece,String> p : piecesDown){
			if (!p.getR().equals(p2pieceCode)) continue;
			for (Block b : p.getL().blocks){
				p2score += startingCoord.productScore(b.coordinate);
			}
		}
		
		score[0] = p1score - p2score;
		score[1] = p2score - p1score;
		
		return score;
	}
	public double[] getScore(){
		double[] score = new double[2];
		
		int p1Blocks = blocksOnBoard(players.get(0).getPieceCode());
		int p2Blocks = blocksOnBoard(players.get(1).getPieceCode());
		
		score[0] = p1Blocks - p2Blocks;
		score[1] = p2Blocks - p1Blocks;
		
		return score;
	}
	public void bPrint(){
		print();
	}
	public ArrayList<Boolean> getPiecesDown(Player p){
		ArrayList<Boolean> piecesDown = new ArrayList<Boolean>();
		if (p == players.get(0)){
			piecesDown = p1PiecesDown;
		}else if (p == players.get(1)){
			piecesDown = p2PiecesDown;
		}
		return piecesDown;
	}
	public ArrayList<Boolean> getPiecesDown(){
		ArrayList<Boolean> piecesDown = new ArrayList<Boolean>();
		if (currentPlayer == players.get(0)){
			piecesDown = p1PiecesDown;
		}else if (currentPlayer == players.get(1)){
			piecesDown = p2PiecesDown;
		}
		return piecesDown;
	}
	public ArrayList<Move> getMoves(){
		return getMoves(currentPlayer);	
	}
	public ArrayList<Move> getMoves(Player pl){
		ArrayList<Pair<Block,Integer>> cornerBlocks = new ArrayList<Pair<Block,Integer>>();
		ArrayList<Pair<Block,Integer>> connectableBlocks = new ArrayList<Pair<Block,Integer>>();
		ArrayList<Piece> possibleMoves = new ArrayList<Piece>();
		
		String pieceCode = pl.getPieceCode();
		ArrayList<Boolean> piecesDown = new ArrayList<Boolean>();
		if (pl == players.get(0)){
			piecesDown = p1PiecesDown;
		}else if (pl == players.get(1)){
			piecesDown = p2PiecesDown;
		}
		
		//*************************************************************************

		cornerBlocks = getCornerBlocks(pieceCode);
//		System.out.println("num of corner blocks: " + cornerBlocks.size());
//		System.out.println("corner blocks:");
//		for (Pair<Block,Integer> bi : cornerBlocks){
//			System.out.println(bi.getL().coordinate.to_string() + " in dir " + bi.getR());
//		}
//		System.out.println("/corner blocks--");
//		
//		System.out.println("");
//		System.out.println("");
		
		connectableBlocks = getConnectableBlocks(cornerBlocks,pieceCode);
//		System.out.println("num of connectable blocks: " + connectableBlocks.size());
//		
//		System.out.println("connectable blocks:");
//		for (Pair<Block,Integer> bi : connectableBlocks){
//			System.out.println(bi.getL().coordinate.to_string() + " in dir " + bi.getR());
//		}
//		System.out.println("/connectable blocks--");
		
		
		possibleMoves = getPossibleMoves(connectableBlocks, pieceCode, piecesDown);
		
		/*for (Piece p : possibleMoves){
			System.out.println("here at " + possibleMoves.indexOf(p) + " out of " + possibleMoves.size());
			Board temp = new Board(14);
			temp.setPlayers(players);
			temp.setPieces(allPieces);
			temp.putTestPieceOnBoard(p, pieceCode);
			temp.print();
		}*/
		
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Piece piece : possibleMoves){
			moves.add(new MoveMCTS(piece));
		}
		return moves;
		
		//*************************************************************************
		
	}

	public ArrayList<Piece> getPossibleMoves(ArrayList<Pair<Block,Integer>> connectables, String pieceCode, ArrayList<Boolean> piecesDown){
		ArrayList<Piece> pl = new ArrayList<Piece>();
		for (Pair<Block,Integer> bi : connectables){
			for (Piece p : filterPiecesDown(allPieces,piecesDown)){
				Piece toPlace = p.clone();
				for (Block b : toPlace.blocks){
					if (canBlockConnect(b,bi.getR())){
						//want to toPlace equivalent of b
						//toPlace.place_piece(b, bi.getL(), bi.getR());
						placePiece(b,bi.getL(),bi.getR());
						if (doesPieceFit(toPlace,pieceCode)){
							pl.add(toPlace.clone());
						}
					}
					
				}
			}
		}
		return pl;
	}
	public void placePiece(Block b, Block connectTo, int direction){
		//System.out.println(direction + " from " + connectTo.coordinate.to_string());
		if (direction == 1){
			b.set_adjacent_coords_to_null();
			b.coordinate = new Coord(connectTo.coordinate.x+1,connectTo.coordinate.y+1);
			b.set_adjacent_coords();
		}else if (direction == 2){
			b.set_adjacent_coords_to_null();
			b.coordinate = new Coord(connectTo.coordinate.x+1,connectTo.coordinate.y-1);
			b.set_adjacent_coords();
		}else if (direction == 3){
			b.set_adjacent_coords_to_null();
			b.coordinate = new Coord(connectTo.coordinate.x-1,connectTo.coordinate.y-1);
			b.set_adjacent_coords();
		}else if (direction == 4){
			b.set_adjacent_coords_to_null();
			b.coordinate = new Coord(connectTo.coordinate.x-1,connectTo.coordinate.y+1);
			b.set_adjacent_coords();
		}
	}
	public boolean canBlockConnect(Block b, int direction){
		switch (direction){
		case 1:
			return b.bottomleft;
		case 2:
			return b.topleft;
		case 3:
			return b.topright;
		case 4:
			return b.bottomright;
		}
		System.out.println("fatal error in canBlockConnect");
		return false;
	}
	public ArrayList<Piece> filterPiecesDown(ArrayList<Piece> pl, ArrayList<Boolean> piecesDown){
		ArrayList<Piece> unused = new ArrayList<Piece>();
		for (Piece p : pl){
			if (!piecesDown.get(p.pieceNumber)) unused.add(p);
		}
		return unused;
	}
	public boolean doPlayersHaveRemainingMoves(){
		for (Player p : players){		
			if (getMoves(p).size() > 0) return true;
		}
		return false;
		
	}
	public boolean doesCurrentPlayerHaveRemainingMoves(){
		return getMoves().size() > 0;	
	}
	public boolean doesPlayerHaveRemainingMoves(Player p){
		return getMoves(p).size() > 0;
	}
	public ArrayList<Integer> numberOfPossibleMovesForEachPlayer(){
		ArrayList<Integer> numMoves = new ArrayList<Integer>();
		for (Player p : players){
			numMoves.add(getMoves(p).size());
		}
		return numMoves;
	}
	public double[] getMoveWeights(String weightingMethod) {
		return getMoveWeights(getMoves(),weightingMethod);
	}
	public double[] getMoveWeights(ArrayList<Move> moves, String weighting_method) {
		if (weighting_method.equals("size") || weighting_method.equals("")){
			double[] result = new double[moves.size()];
			for (Move m : moves){
				result[moves.indexOf(m)] = Math.pow((double)m.getPiece().size(),1.5);
			}
			return result;
		}else if (weighting_method.equals("heat")){
			double[] result = new double[moves.size()];
			for (Move m : moves){
				result[moves.indexOf(m)] = explorationHeatMapScore(m.getPiece());
			}
			return result;
		}else if (weighting_method.equals("product")){
			double[] result = new double[moves.size()];
			for (Move m : moves){
				result[moves.indexOf(m)] = explorationProductScore(m.getPiece());
			}
			return result;
		}else if (weighting_method.equals("uct") || weighting_method.equals("ucb")){
			double[] result = new double[moves.size()];
			for (Move m : moves){
				result[moves.indexOf(m)] = explorationProductScore(m.getPiece());
			}
			return result;
		}else{
			return null;
		}
	}
	public int explorationHeatMapScore(Piece p){
		int[][] smallStride = new int[boardSize/2][boardSize/2];
		int[][] largeStride = new int[boardSize/7][boardSize/7];
		double[][] cellPoints = new double[boardSize][boardSize];
		for (int x = 0; x < boardSize; x++){
			for (int y = 0; y < boardSize; y++){
				//System.out.println(" getting cell at " + x + ", " + y + " = " + getFromCoordinate(x,y));
				if (getFromCoordinate(x,y) != null && getFromCoordinate(x,y).equals(currentPlayer.getPieceCode())){
					int x_small_idx = (int)Math.floor(x/2);
					int x_large_idx = (int)Math.floor(x/7);
					int y_small_idx = (int)Math.floor(y/2);
					int y_large_idx = (int)Math.floor(y/7);
					smallStride[y_small_idx][x_small_idx]++;
					largeStride[y_large_idx][x_large_idx]++;
				}
			}
		}
		for (int x = 0; x < boardSize; x++){
			for (int y = 0; y < boardSize; y++){
				cellPoints[y][x] = 10 - smallStride[(int)Math.floor(y/2)][(int)Math.floor(x/2)] - 0.1*largeStride[(int)Math.floor(y/7)][(int)Math.floor(x/7)];
				//System.out.print(cellPoints[y][x] + "   ");
			}
			//System.out.println("");
		}
		Coord c;
		int result = 0;
		for (Block b : p.blocks){
			c = b.coordinate;
			//System.out.println("Adding " + cellPoints[c.y][c.x] + " to " + result + " at from coordinate " + c.x + " , " + c.y);
			result += cellPoints[c.y][c.x];
		}
		//System.out.println("Returning result : " + result);
		return result;
	}
	public int explorationProductScore(Piece p){
		Coord startingCoord = (currentPlayer.startingCorner == 1 ? new Coord(0,0) : new Coord(getWidth()-1,getHeight()-1));
		int score = 0;
		for (Block b : p.blocks){
			score += startingCoord.productScore(b.coordinate);
		}
		return score;
		
	}
	public Board duplicate(){
		return clone();
	}
	public String cloneString(String s){
		return (s == null ? null : new String(s));
	}
	public String getFromCoordinate(int x, int y){
		if (x < boardSize && y < boardSize && x >= 0 && y >= 0){
			return board[y][x];
		}
		return null;
	}
	public boolean doesPieceFit(Piece p, String pieceCode){
		if (pieceCode.equals(players.get(0).pieceCode)){
			if (p1PiecesDown.get(p.pieceNumber)) return false;
		}else if (pieceCode.equals(players.get(1).pieceCode)){
			if (p2PiecesDown.get(p.pieceNumber)) return false;
		}
		for (Block b : p.blocks){
			int x = b.coordinate.x;
			int y = b.coordinate.y;
			if (x < 0) return false;
			if (y < 0) return false;
			if (x >= boardSize) return false;
			if (y >= boardSize) return false;
			if (getFromCoordinate(x,y) != null && !getFromCoordinate(x,y).equals("")) return false;
			if (getFromCoordinate(x,y) != null && getFromCoordinate(x,y).equals(players.get(0).getPieceCode())) return false;
			if (getFromCoordinate(x,y) != null && getFromCoordinate(x,y).equals(players.get(1).getPieceCode())) return false;
			if (getFromCoordinate(x,y+1) != null && getFromCoordinate(x,y+1).equals(pieceCode)) return false;
			if (getFromCoordinate(x+1,y) != null && getFromCoordinate(x+1,y).equals(pieceCode)) return false;
			if (getFromCoordinate(x,y-1) != null && getFromCoordinate(x,y-1).equals(pieceCode)) return false;
			if (getFromCoordinate(x-1,y) != null && getFromCoordinate(x-1,y).equals(pieceCode)) return false;
		}
		return true;
	}
	public boolean isConnectorFree(Block b, int direction, String pieceCode){
		int x = b.coordinate.x;
		int y = b.coordinate.y;
		switch (direction){
			case 1 : // direction one relates to top right
				if (b.coordinate.x+1 >= boardSize) return false;		
				if (b.coordinate.y+1 >= boardSize) return false;
				if (board[b.coordinate.y+1][b.coordinate.x+1] != null && !board[b.coordinate.y+1][b.coordinate.x+1].equals("")) return false; 
				if (getFromCoordinate(x+1,y+2) != null && getFromCoordinate(x+1,y+2).equals(pieceCode)) return false;
				if (getFromCoordinate(x+2,y+1) != null && getFromCoordinate(x+2,y+1).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x+1,y) != null && getFromCoordinate(x+1,y).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x,y+1) != null && getFromCoordinate(x,y+1).equals(pieceCode)) return false;
				return true;
			case 2 : // direction one relates to bottom right
				if (b.coordinate.x+1 >= boardSize) return false;		
				if (b.coordinate.y-1 < 0) return false;
				if (board[b.coordinate.y-1][b.coordinate.x+1] != null && !board[b.coordinate.y-1][b.coordinate.x+1].equals("")) return false; 
				if (getFromCoordinate(x+2,y-1) != null && getFromCoordinate(x+2,y-1).equals(pieceCode)) return false;
				if (getFromCoordinate(x+1,y-2) != null && getFromCoordinate(x+1,y-2).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x,y-1) != null && getFromCoordinate(x,y-1).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x+1,y) != null && getFromCoordinate(x+1,y).equals(pieceCode)) return false;
				return true;
			case 3 : // direction one relates to bottom left
				if (b.coordinate.x-1 < 0) return false;		
				if (b.coordinate.y-1 < 0) return false;
				if (board[b.coordinate.y-1][b.coordinate.x-1] != null && !board[b.coordinate.y-1][b.coordinate.x-1].equals("")) return false; 
				if (getFromCoordinate(x-1,y-2) != null && getFromCoordinate(x-1,y-2).equals(pieceCode)) return false;
				if (getFromCoordinate(x-2,y-1) != null && getFromCoordinate(x-2,y-1).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x-1,y) != null && getFromCoordinate(x-1,y).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x,y-1) != null && getFromCoordinate(x,y-1).equals(pieceCode)) return false;
				return true;
			case 4 : // direction one relates to top left
				if (b.coordinate.x-1 < 0) return false;		
				if (b.coordinate.y+1 >= boardSize) return false;
				if (board[b.coordinate.y+1][b.coordinate.x-1] != null && !board[b.coordinate.y+1][b.coordinate.x-1].equals("")) return false; 
				if (getFromCoordinate(x-1,y+2) != null && getFromCoordinate(x-1,y+2).equals(pieceCode)) return false;
				if (getFromCoordinate(x-2,y+1) != null && getFromCoordinate(x-2,y+1).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x-1,y) != null && getFromCoordinate(x-1,y).equals(pieceCode)) return false;
				if (!b.starter_block && getFromCoordinate(x,y+1) != null && getFromCoordinate(x,y+1).equals(pieceCode)) return false;
				return true;
		}
		return false;
	}
	public void putPieceOnBoard(Piece p, String pieceCode){
		// System.out.println("here 1");
 		if (pieceCode.equals(players.get(0).pieceCode)){
 			p1PiecesDown.set(p.pieceNumber, new Boolean(true));
 		}else if (pieceCode.equals(players.get(1).pieceCode)){
			p2PiecesDown.set(p.pieceNumber, new Boolean(true));
		}

		piecesDown.add(new Pair<Piece,String>(p,pieceCode));
		//pieces_left.remove(p);
		for (Block b : p.blocks){
			Coord c = b.coordinate;
			board[c.y][c.x] = pieceCode;
		}
		if (currentPlayer == players.get(0)){
			if (doesPlayerHaveRemainingMoves(players.get(1))) currentPlayer = players.get(1);
		}else{
			if (doesPlayerHaveRemainingMoves(players.get(0))) currentPlayer = players.get(0);
		}
	}
	public void putTestPieceOnBoard(Piece p, String pieceCode){
		for (Block b : p.blocks){
			Coord c = b.coordinate;
			board[c.y][c.x] = pieceCode;
		}
	}
	public void makeMove(Move m, String pieceCode){
		putPieceOnBoard(m.getPiece(), pieceCode);
	}
	public void makeMove(Move m){
		putPieceOnBoard(m.getPiece(), currentPlayer.getPieceCode());
	}
	public void makeMove(Move m, int playerId){		
		putPieceOnBoard(m.getPiece(), players.get(playerId).getPieceCode());
	}
	public void putStartingPieceOnBoard(Piece p, String pieceCode){
		allPieceCodes.add(pieceCode);
		piecesDown.add(new Pair<Piece,String>(p,pieceCode));
	}
	public static boolean isNumeric(String str){
    	return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	public void print(){
		System.out.print("+");
		for (int j = 0; j < boardSize; j++){
			System.out.print(" - ");
		}
		System.out.print("+");
		System.out.println("");
		for (int i = boardSize-1; i >= 0; i--){
			System.out.print("|");
			for (int j = 0; j < boardSize; j++){
				if (board[i][j] != null && board[i][j].length() > 1){
					System.out.print(" " + board[i][j] + "");
				}else if (board[i][j] != null && board[i][j].length() == 1){
					System.out.print(" " + board[i][j] + " ");
				}else{
					System.out.print(" " + ((char)183) + " ");
				}
			}
			System.out.println("|");
		}
		System.out.print("+");
		for (int j = 0; j < boardSize; j++){
			System.out.print(" - ");
		}
		System.out.print("+");
		System.out.println("");
	}
	public void printCleanDisplay(String pieceCode){
		System.out.print("+");
		for (int j = 0; j < boardSize; j++){
			System.out.print(" - ");
		}
		System.out.print("+");
		System.out.println("");
		for (int i = boardSize-1; i >= 0; i--){
			System.out.print("|");
			for (int j = 0; j < boardSize; j++){
				if (board[i][j] != null && board[i][j].length() > 1){
					System.out.print(" " + board[i][j] + "");
				}else if (board[i][j] != null && board[i][j].length() == 1){
					if (board[i][j].equals(pieceCode)){
						System.out.print(" " + ((char)248) + " ");
					}else{
						if (isNumeric(board[i][j])){
							System.out.print(" " + board[i][j] + " ");
						}else{
							System.out.print(" " + ((char)183) + " ");
						}
					}
				}else{
					System.out.print("   ");
				}
			}
			System.out.println("|");
		}
		System.out.print("+");
		for (int j = 0; j < boardSize; j++){
			System.out.print(" - ");
		}
		System.out.print("+");
		System.out.println("");
	}
	public void printOptionsBoard(ArrayList<Pair<Block,Integer>> connectableBlocks){
		ArrayList<Coord> optionCoords = new ArrayList<Coord>();
		int coord_id = 1;
		for (Pair<Block,Integer> p : connectableBlocks){	
			switch (p.getR()){
				case 1 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y+1));
					break;
				case 2 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y-1));
					break;
				case 3 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y-1));
					break;
				case 4 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y+1));
					break;
			}
			coord_id++;
		}
		print();
		for (Coord coord : optionCoords){ 
			board[coord.y][coord.x] = null;
		}
	}
	public void printOptionsBoard(ArrayList<Pair<Block,Integer>> connectableBlocks, String pieceCode){
		ArrayList<Coord> optionCoords = new ArrayList<Coord>();
		int coord_id = 1;
		for (Pair<Block,Integer> p : connectableBlocks){	
			switch (p.getR()){
				case 1 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y+1));
					break;
				case 2 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y-1));
					break;
				case 3 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y-1));
					break;
				case 4 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y+1));
					break;
			}
			coord_id++;
		}
		//printCleanDisplay(pieceCode);
		print();
		for (Coord coord : optionCoords){ 
			board[coord.y][coord.x] = null;
		}
	}
	public void printOptionsBoard(String pieceCode){
		ArrayList<Coord> optionCoords = new ArrayList<Coord>();
		int coord_id = 1;
		for (Pair<Block,Integer> p : getConnectableBlocks(getCornerBlocks(pieceCode),pieceCode)){	
			switch (p.getR()){
				case 1 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y+1));
					break;
				case 2 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x+1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x+1,p.getL().coordinate.y-1));
					break;
				case 3 : 
					board[p.getL().coordinate.y-1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y-1));
					break;
				case 4 : 
					board[p.getL().coordinate.y+1][p.getL().coordinate.x-1] = Integer.toString(coord_id);
					optionCoords.add(new Coord(p.getL().coordinate.x-1,p.getL().coordinate.y+1));
					break;
			}
			coord_id++;
		}
		print();
		for (Coord coord : optionCoords){ 
			board[coord.y][coord.x] = null;
		}
	}
	public ArrayList<Pair<Block,Integer>> getCornerBlocks(String pieceCode){
		ArrayList<Pair<Block,Integer>> result = new ArrayList<Pair<Block,Integer>>();
		for (Pair<Piece,String> p : piecesDown){
			if (p.getR().equals(pieceCode)){
				for (Pair<Block,Integer> b : p.getL().connectable_blocks()){
					result.add(b);
				}
			}
		}
		return result;
	}
	public ArrayList<Pair<Block,Integer>> getConnectableBlocks(ArrayList<Pair<Block,Integer>> corners, String pieceCode){
		ArrayList<Pair<Block,Integer>> result = new ArrayList<Pair<Block,Integer>>();
		for (Pair<Block,Integer> p : corners){	
			if (isConnectorFree(p.getL(),p.getR(),pieceCode)) result.add(p);
		}
		return result;
	}
	public int getHeight(){ return boardSize; }
	public int getWidth(){ return boardSize; }
	public ArrayList<String> getAllPieceCodes(){ return new ArrayList<String>(allPieceCodes); }
	public void saveScoresToFile(Player... players){
		File workingDir = new File(System.getProperty("user.dir"));
		File scoresDir = new File(workingDir,"score_data_smcts");
		if (!scoresDir.exists()) scoresDir.mkdir();
		for (int i = 0; i < 0xFFFF; i++){
			if (!(new File(scoresDir,"scores" + i + ".txt").exists())){
				File scoreFile = new File(scoresDir,"scores" + i + ".txt");
				try{
					scoreFile.createNewFile();
				}catch(IOException ioe){
					System.out.println("Caught ioe");
				}
				try{
					PrintWriter out = new PrintWriter(scoreFile);
					for (int j = 0; j < Math.min(players.length, 4); j++){
    					out.println(players[j].getStrategy() + "," + blocksOnBoard(players[j].getPieceCode()));
    				}
    				out.close();
				}catch (FileNotFoundException fnfe){
					System.out.println("Caught fnfe");
				}
				break;
			}
		}
	}
	public void outputPlayingData(Player... players){
		System.out.println("---");
		System.out.println("1: " + players[0].getStrategy() + "1");
		System.out.println("2: " + players[1].getStrategy() + "2");
		boolean found_starter = false;
		for (Pair<Piece,String> p : piecesDown){
			found_starter = false;
			String line = "";
			if (p.getR().equals(players[0].getPieceCode())){
				line = "1: ";
				for (Block b : p.getL().blocks){
					line += (13-b.coordinate.x) + "," + b.coordinate.y + ";";
					if ((13-b.coordinate.x) == -1 || b.coordinate.y == -1 || (13-b.coordinate.x) == boardSize || b.coordinate.y == boardSize) found_starter = true;
				}
			}else{
				line = "2: ";
				for (Block b : p.getL().blocks){
					line += b.coordinate.x + "," + (13-b.coordinate.y) + ";";
					if (b.coordinate.x == -1 || (13-b.coordinate.y) == -1 || b.coordinate.x == boardSize || (13-b.coordinate.y) == boardSize) found_starter = true;
				}
			}
			if (!found_starter) System.out.println(line);
		}
	}
	public void savePlayingDataToFile(Player... players){
		File workingDir = new File(System.getProperty("user.dir"));
		File dataDir = new File(workingDir,"playing_data_smcts");
		if (!dataDir.exists()) dataDir.mkdir();
		for (int i = 0; i < Double.POSITIVE_INFINITY; i++){
			File movesFile = new File(dataDir,"moves" + i + ".txt");
			if (!movesFile.exists()){
				try{
					movesFile.createNewFile();
				}catch(IOException ioe){
					System.out.println("Caught ioe");
				}
				try{
					PrintWriter out = new PrintWriter(movesFile);
    				out.println("1: " + players[0].getStrategy() + "1");
    				out.println("2: " + players[1].getStrategy() + "2");
    				boolean found_starter = false;
    				for (Pair<Piece,String> p : piecesDown){
    					found_starter = false;
    					String line = "";
    					if (p.getR().equals(players[0].getPieceCode())){
    						line = "1: ";
    						for (Block b : p.getL().blocks){
								line += (13-b.coordinate.x) + "," + b.coordinate.y + ";";
								if ((13-b.coordinate.x) == -1 || b.coordinate.y == -1 || (13-b.coordinate.x) == boardSize || b.coordinate.y == boardSize) found_starter = true;
							}
    					}else{
    						line = "2: ";
    						for (Block b : p.getL().blocks){
								line += b.coordinate.x + "," + (13-b.coordinate.y) + ";";
								if (b.coordinate.x == -1 || (13-b.coordinate.y) == -1 || b.coordinate.x == boardSize || (13-b.coordinate.y) == boardSize) found_starter = true;
							}
    					}
    					if (!found_starter) out.println(line);
    				}
    				out.close();
				}catch (FileNotFoundException fnfe){
					System.out.println("Caught fnfe");
				}
				break;
			}
		}
	}
	public int blocksOnBoard(String pieceCode){
		int blockCount = 0;
		for (int i = 0; i < boardSize; i++){
			for (int j = 0; j < boardSize; j++){
				if (board[i][j] != null && board[i][j].equals(pieceCode)) blockCount++;
			}
		}
		return blockCount;
	}
	public ArrayList<Piece> getPiecesFromCode(String pieceCode){
		ArrayList<Piece> result = new ArrayList<Piece>();
		for (Pair<Piece,String> p : piecesDown){
			if (p.getR().equals(pieceCode)) result.add(p.getL());
		}
		return result;
	}
}
