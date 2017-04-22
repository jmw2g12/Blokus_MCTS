import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class Piece{
	int ID;
	int pieceNumber = -1;
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public Piece(Block... b){
		int counter = 0;
		for (Block bl : b){
			bl.ID = counter;
			blocks.add(bl);
			counter++;
		}
		blocks.get(0).set_adjacent_coords_to_null();
		blocks.get(0).coordinate = new Coord(0,0);
		blocks.get(0).set_adjacent_coords();
		int min_x = 0xFF;
		int min_y = 0xFF;
		for (Block bl : b){
			if (bl.coordinate.x < min_x) min_x = bl.coordinate.x;
			if (bl.coordinate.y < min_y) min_y = bl.coordinate.y;
		}
		for (Block bl : b){
			bl.coordinate.x -= min_x;
			bl.coordinate.y -= min_y;
		}			
	}
	public Piece(){
	}
	public int size(){
		return blocks.size();
	}
	public void place_piece(Block b, Coord c){
		for (Block bl : blocks){
			bl.coordinate = null;
		}
		b.coordinate = c;
		b.set_adjacent_coords();
	}
	public void print(){
		for (Block b : blocks){
			b.print();
		}
	}
	public void print_coordinates(){
		for (Block b : blocks){
			System.out.println("Block # = " + b.ID + "    Coordinates = " + b.coordinate.x + ", " + b.coordinate.y);
		}
	}
	public void printPiece(){
		int max_x = -0xFF;
		int max_y = -0xFF;
		ArrayList<Coord> coordinates = new ArrayList<Coord>();
		for (Block b : blocks){
			if (b.coordinate.x > max_x) max_x = b.coordinate.x;
			if (b.coordinate.y > max_y) max_y = b.coordinate.y;
			coordinates.add(b.coordinate);
		}
		for (int j = max_y+1; j >= -1; j--){
			for (int i = -1; i <= max_x+1; i++){
				System.out.print((coordinates.contains(new Coord(i,j))) ? (blocks.get(coordinates.indexOf(new Coord(i,j))).ID) : "-");
			}
			System.out.println("");
		}
	}
	public ArrayList<String> getPieceDiagram(String blank){
		int max_x = -0xFF;
		int max_y = -0xFF;
		ArrayList<String> result = new ArrayList<String>();
		String line = new String("");
		ArrayList<Coord> coordinates = new ArrayList<Coord>();
		for (Block b : blocks){
			if (b.coordinate.x > max_x) max_x = b.coordinate.x;
			if (b.coordinate.y > max_y) max_y = b.coordinate.y;
			coordinates.add(b.coordinate);
		}
		for (int j = max_y+1; j >= -1; j--){
			for (int i = -1; i <= max_x+1; i++){
				line = line + ((coordinates.contains(new Coord(i,j))) ? (blocks.get(coordinates.indexOf(new Coord(i,j))).ID) : blank);
			}
			result.add(line);
			line = new String("");
		}
		return result;
	}
	public ArrayList<String> getPieceRepresentation(String blank, String nonBlank, boolean numberBlocks, int... number){
		int max_x = -0xFF;
		int max_y = -0xFF;
		ArrayList<String> result = new ArrayList<String>();
		String line = new String("");
		ArrayList<Coord> coordinates = new ArrayList<Coord>();
		for (Block b : blocks){
			if (b.coordinate.x > max_x) max_x = b.coordinate.x;
			if (b.coordinate.y > max_y) max_y = b.coordinate.y;
			coordinates.add(b.coordinate);
		}
		for (int j = max_y+2; j >= -1; j--){
			for (int i = -1; i <= max_x+1; i++){
				line = line + ((coordinates.contains(new Coord(i,j))) ? (numberBlocks ? blocks.get(coordinates.indexOf(new Coord(i,j))).ID : nonBlank) : blank);
			}
			result.add(line);
			line = new String("");
		}
		if (number.length != 0){
			result.set(0,"#"+number[0]+result.get(0).substring((number[0] < 10) ? 2 : ((number[0] >= 10) ? 3 : 4)));
			String line2 = (number[0] < 10) ? "--" : ((number[0] >= 10) ? "---" : "----");
			for (int i = (number[0] < 10) ? 2 : ((number[0] >= 10) ? 3 : 4); i < result.get(0).length(); i++){
				line2 += blank;
			}
			result.set(1,line2);
		}
		return result;
	}
	public void printPieceDiagram(){
		System.out.println();
		for (String s : getPieceDiagram(" ")){
			System.out.println(s);
		}
		System.out.println();
	}
	public void printPieceRepresentation(){
		for (String s : getPieceRepresentation(" ",Character.toString((char)248),false)){
			System.out.println(s);
		}
		System.out.println();
	}
	public String getStringRepresentation(){
		String result = "";
		ArrayList<String> rep = getPieceRepresentation(" ",Character.toString((char)248),false);
		for (String s : rep){
			result = result.concat(s);
			result = result.concat(System.lineSeparator());
		}
		return result;
	}
	public boolean comparePieceRepresentations(Piece p){
		ArrayList<String> rep1 = getPieceRepresentation("-",Character.toString((char)248),false);
		ArrayList<String> rep2 = p.getPieceRepresentation("-",Character.toString((char)248),false);
		
		int counter = 0;
		if (rep1.size() == rep2.size()){
			for (String s : rep1){
				if (!s.equals(rep2.get(counter++))) return false;
			}
			return true;
		}
		return false;
	}
	public boolean isSamePiece(Piece p){
		if (comparePieceRepresentations(p)) return true;
		if (comparePieceRepresentations(p.rotateCW())) return true;
		if (comparePieceRepresentations(p.rotateCW().rotateCW())) return true;
		if (comparePieceRepresentations(p.rotateCCW())) return true;
		if (comparePieceRepresentations(p.flipHorizontal())) return true;
		if (comparePieceRepresentations(p.flipHorizontal().rotateCW())) return true;
		if (comparePieceRepresentations(p.flipHorizontal().rotateCW().rotateCW())) return true;
		if (comparePieceRepresentations(p.flipHorizontal().rotateCCW())) return true;
		return false;
	}
	public void place_piece(Block placed_block, Block connector_block, int direction){
		Coord c = new Coord();
		switch (direction){
			case 1 :
				c = new Coord(connector_block.coordinate.x+1,connector_block.coordinate.y+1);
				break;
			case 2 :
				c = new Coord(connector_block.coordinate.x+1,connector_block.coordinate.y-1);
				break;
			case 3 :
				c = new Coord(connector_block.coordinate.x-1,connector_block.coordinate.y-1);
				break;
			case 4 :
				c = new Coord(connector_block.coordinate.x-1,connector_block.coordinate.y+1);
				break;
		}
		place_piece(placed_block,c);
	}
	public ArrayList<Pair<Block,Integer>> getConnectableBlocks(){
		ArrayList<Pair<Block,Integer>> result = new ArrayList<Pair<Block,Integer>>();
		for (Block b : blocks){
			if (b.topright) result.add(new Pair<Block,Integer>(b,1));
			if (b.bottomright) result.add(new Pair<Block,Integer>(b,2));
			if (b.bottomleft) result.add(new Pair<Block,Integer>(b,3));
			if (b.topleft) result.add(new Pair<Block,Integer>(b,4));
		}
		return result;
	}

	public Piece clone(){
		Piece result = new Piece();
		result.ID = ID;
		result.pieceNumber = pieceNumber;
		ArrayList<Block> cloned_blocks = new ArrayList<Block>();
		ArrayList<Block> linked_cloned_blocks = new ArrayList<Block>();
		for (Block b : blocks){
			cloned_blocks.add(b.clone());
			linked_cloned_blocks.add(b.clone());
		}
		for (Block b : cloned_blocks){
			if (blocks.get(cloned_blocks.indexOf(b)).top != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).top = linked_cloned_blocks.get(b.neighbour_ids.get(0));
			if (blocks.get(cloned_blocks.indexOf(b)).right != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).right = linked_cloned_blocks.get(b.neighbour_ids.get(1));
			if (blocks.get(cloned_blocks.indexOf(b)).bottom != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).bottom = linked_cloned_blocks.get(b.neighbour_ids.get(2));
			if (blocks.get(cloned_blocks.indexOf(b)).left != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).left = linked_cloned_blocks.get(b.neighbour_ids.get(3));
		}
		result.blocks = linked_cloned_blocks;
		return result;
	}
	public Piece rotateCCW(){
		ArrayList<Block> cloned_blocks = new ArrayList<Block>();
		ArrayList<Block> linked_cloned_blocks = new ArrayList<Block>();
		for (Block b : blocks){
			cloned_blocks.add(b.clone());
			linked_cloned_blocks.add(b.clone());
		}
		for (Block b : cloned_blocks){
			if (blocks.get(cloned_blocks.indexOf(b)).right != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).top = linked_cloned_blocks.get(b.neighbour_ids.get(1));
			if (blocks.get(cloned_blocks.indexOf(b)).bottom != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).right = linked_cloned_blocks.get(b.neighbour_ids.get(2));
			if (blocks.get(cloned_blocks.indexOf(b)).left != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).bottom = linked_cloned_blocks.get(b.neighbour_ids.get(3));
			if (blocks.get(cloned_blocks.indexOf(b)).top != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).left = linked_cloned_blocks.get(b.neighbour_ids.get(0));
		}
		for (Block b : linked_cloned_blocks){
			b.coordinate = null;
		}
		Piece result = new Piece(linked_cloned_blocks.toArray(new Block[linked_cloned_blocks.size()]));
		result.ID = ID;
		return result;
	}
	public Piece rotateCW(){
		ArrayList<Block> cloned_blocks = new ArrayList<Block>();
		ArrayList<Block> linked_cloned_blocks = new ArrayList<Block>();
		for (Block b : blocks){
			cloned_blocks.add(b.clone());
			linked_cloned_blocks.add(b.clone());
		}
		for (Block b : cloned_blocks){
			if (blocks.get(cloned_blocks.indexOf(b)).left != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).top = linked_cloned_blocks.get(b.neighbour_ids.get(3));
			if (blocks.get(cloned_blocks.indexOf(b)).top != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).right = linked_cloned_blocks.get(b.neighbour_ids.get(0));
			if (blocks.get(cloned_blocks.indexOf(b)).right != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).bottom = linked_cloned_blocks.get(b.neighbour_ids.get(1));
			if (blocks.get(cloned_blocks.indexOf(b)).bottom != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).left = linked_cloned_blocks.get(b.neighbour_ids.get(2));
		}
		for (Block b : linked_cloned_blocks){
			b.coordinate = null;
		}
		Piece result = new Piece(linked_cloned_blocks.toArray(new Block[linked_cloned_blocks.size()]));
		result.ID = ID;
		return result;
	}
	public Piece flipHorizontal(){
		ArrayList<Block> cloned_blocks = new ArrayList<Block>();
		ArrayList<Block> linked_cloned_blocks = new ArrayList<Block>();
		for (Block b : blocks){
			cloned_blocks.add(b.clone());
			linked_cloned_blocks.add(b.clone());
		}
		for (Block b : cloned_blocks){
			if (blocks.get(cloned_blocks.indexOf(b)).bottom != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).top = linked_cloned_blocks.get(b.neighbour_ids.get(2));
			if (blocks.get(cloned_blocks.indexOf(b)).right != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).right = linked_cloned_blocks.get(b.neighbour_ids.get(1));
			if (blocks.get(cloned_blocks.indexOf(b)).top != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).bottom = linked_cloned_blocks.get(b.neighbour_ids.get(0));
			if (blocks.get(cloned_blocks.indexOf(b)).left != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).left = linked_cloned_blocks.get(b.neighbour_ids.get(3));
		}
		for (Block b : linked_cloned_blocks){
			b.coordinate = null;
		}
		Piece result = new Piece(linked_cloned_blocks.toArray(new Block[linked_cloned_blocks.size()]));
		result.ID = ID;
		return result;
	}
	public Piece flipVertical(){
		ArrayList<Block> cloned_blocks = new ArrayList<Block>();
		ArrayList<Block> linked_cloned_blocks = new ArrayList<Block>();
		for (Block b : blocks){
			cloned_blocks.add(b.clone());
			linked_cloned_blocks.add(b.clone());
		}
		for (Block b : cloned_blocks){
			if (blocks.get(cloned_blocks.indexOf(b)).top != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).top = linked_cloned_blocks.get(b.neighbour_ids.get(0));
			if (blocks.get(cloned_blocks.indexOf(b)).left != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).right = linked_cloned_blocks.get(b.neighbour_ids.get(3));
			if (blocks.get(cloned_blocks.indexOf(b)).bottom != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).bottom = linked_cloned_blocks.get(b.neighbour_ids.get(2));
			if (blocks.get(cloned_blocks.indexOf(b)).right != null) linked_cloned_blocks.get(cloned_blocks.indexOf(b)).left = linked_cloned_blocks.get(b.neighbour_ids.get(1));
		}
		for (Block b : linked_cloned_blocks){
			b.coordinate = null;
		}
		Piece result = new Piece(linked_cloned_blocks.toArray(new Block[linked_cloned_blocks.size()]));
		result.ID = ID;
		return result;
	}
	public ArrayList<Piece> getAllPieceVariations(){
		ArrayList<Piece> allVariations = new ArrayList<Piece>();
		
		allVariations.add(this);
		if (!exactPieceExistsInArrayList(allVariations,rotateCW())) allVariations.add(rotateCW());
		if (!exactPieceExistsInArrayList(allVariations,rotateCW().rotateCW())) allVariations.add(rotateCW().rotateCW());
		if (!exactPieceExistsInArrayList(allVariations,rotateCCW())) allVariations.add(rotateCCW());
		if (!exactPieceExistsInArrayList(allVariations,flipHorizontal())) allVariations.add(flipHorizontal());
		if (!exactPieceExistsInArrayList(allVariations,flipHorizontal().rotateCW())) allVariations.add(flipHorizontal().rotateCW());
		if (!exactPieceExistsInArrayList(allVariations,flipHorizontal().rotateCW().rotateCW())) allVariations.add(flipHorizontal().rotateCW().rotateCW());
		if (!exactPieceExistsInArrayList(allVariations,flipHorizontal().rotateCCW())) allVariations.add(flipHorizontal().rotateCCW());

		return allVariations;
	}
	public boolean exactPieceExistsInArrayList(ArrayList<Piece> al, Piece p1){
		for (Piece p2 : al){
			if (p1.comparePieceRepresentations(p2)) return true;
		}
		return false;
	}
	public int findPieceInArrayList(ArrayList<Piece> al){
		for (Piece p2 : al){
			if (comparePieceRepresentations(p2)) return al.indexOf(p2);
		}
		System.out.println("Piece does not exist in arraylist!   Piece.findPieceInArrayList(ArrayList<Piece>)");
		return -1;
	}
}
