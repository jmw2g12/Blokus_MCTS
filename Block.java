import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class Block{
	public boolean starter_block = false;
	public int ID;
	public Coord coordinate;
	public Block top, right, bottom, left;
	public ArrayList<Integer> neighbour_ids = new ArrayList<Integer>();
	public boolean topleft, topright, bottomleft, bottomright;
	public Block(Block top, Block right, Block bottom, Block left){
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	public void add_top(Block b){
		topleft = (b == null) ? true : false;
		topright = (b == null) ? true : false;
		top = b;
	}
	public boolean hasTopLeft(){ return topleft; }
	public boolean hasTopRight(){ return topright; }
	public boolean hasBottomLeft(){ return bottomleft; }
	public boolean hasBottomRight(){ return bottomright; }
	public void add_right(Block b){
		bottomright = (b == null) ? true : false;
		topright = (b == null) ? true : false;
		right = b;
	}
	public void add_bottom(Block b){
		bottomleft = (b == null) ? true : false;
		bottomright = (b == null) ? true : false;
		bottom = b;
	}
	public void add_left(Block b){
		topleft = (b == null) ? true : false;
		bottomleft = (b == null) ? true : false;
		left = b;
	}
	public void connectTop(Block b){
		add_top(b);
		b.add_bottom(this);
	}
	public void connectRight(Block b){
		add_right(b);
		b.add_left(this);
	}
	public void connectBottom(Block b){
		add_bottom(b);
		b.add_top(this);
	}
	public void connectLeft(Block b){
		add_left(b);
		b.add_right(this);
	}
	public void print_connectors(){
		System.out.println("Top right = " + topright);
		System.out.println("Bottom right = " + bottomright);
		System.out.println("Bottom left = " + bottomleft);
		System.out.println("Top left = " + topleft);
	}
	public Block(){
		this.top = null;
		this.right = null;
		this.bottom = null;
		this.left = null;
		topleft = true;
		topright = true;
		bottomleft = true;
		bottomright = true;			
	}
	public void print(){
		System.out.println("top = " + ((top != null) ? top.ID : "0") + ", right = " + ((right != null) ? right.ID : "0") + ", bottom = " + ((bottom != null) ? bottom.ID : "0") + ", left = " + ((left != null) ? left.ID : "0"));
	}
	public void set_adjacent_coords(){
		if (top != null && top.coordinate == null){
			top.coordinate = new Coord(coordinate.x,coordinate.y+1);
			top.set_adjacent_coords();
		}
		if (right != null && right.coordinate == null){
			right.coordinate = new Coord(coordinate.x+1,coordinate.y);
			right.set_adjacent_coords();
		}
		if (bottom != null && bottom.coordinate == null){
			bottom.coordinate = new Coord(coordinate.x,coordinate.y-1);
			bottom.set_adjacent_coords();
		}
		if (left != null && left.coordinate == null){
			left.coordinate = new Coord(coordinate.x-1,coordinate.y);
			left.set_adjacent_coords();
		}			
	}
	
	public void set_adjacent_coords_to_null(){
		if (top != null && top.coordinate != null){
			top.coordinate = null;
			top.set_adjacent_coords_to_null();
		}
		if (right != null && right.coordinate != null){
			right.coordinate = null;
			right.set_adjacent_coords_to_null();
		}
		if (bottom != null && bottom.coordinate != null){
			bottom.coordinate = null;
			bottom.set_adjacent_coords_to_null();
		}
		if (left != null && left.coordinate != null){
			left.coordinate = null;
			left.set_adjacent_coords_to_null();
		}			
	}
	
	public Block clone(){
		Block result = new Block();
		result.coordinate = coordinate.clone();
		result.starter_block = starter_block;
		result.ID = ID;
		result.topright = topright;
		result.bottomright = bottomright;
		result.bottomleft = bottomleft;
		result.topleft = topleft;
		if (top != null){
			result.neighbour_ids.add(0,top.ID);
		}else{
			result.neighbour_ids.add(0,null);
		}
		if (right != null){
			result.neighbour_ids.add(1,right.ID);
		}else{
			result.neighbour_ids.add(1,null);
		}
		if (bottom != null){
			result.neighbour_ids.add(2,bottom.ID);
		}else{
			result.neighbour_ids.add(2,null);
		}
		if (left != null){
			result.neighbour_ids.add(3,left.ID);
		}else{
			result.neighbour_ids.add(3,null);
		}
		return result;
	}
}
