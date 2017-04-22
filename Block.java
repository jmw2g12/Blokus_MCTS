import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class Block{
	public boolean starterBlock = false;
	public int ID;
	public Coord coordinate;
	public Block top, right, bottom, left;
	public ArrayList<Integer> neighbourIds = new ArrayList<Integer>();
	public boolean topleft, topright, bottomleft, bottomright;
	public Block(Block top, Block right, Block bottom, Block left){
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
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
	public Block clone(){
		Block result = new Block();
		result.coordinate = coordinate.clone();
		result.starterBlock = starterBlock;
		result.ID = ID;
		result.topright = topright;
		result.bottomright = bottomright;
		result.bottomleft = bottomleft;
		result.topleft = topleft;
		if (top != null){
			result.neighbourIds.add(0,top.ID);
		}else{
			result.neighbourIds.add(0,null);
		}
		if (right != null){
			result.neighbourIds.add(1,right.ID);
		}else{
			result.neighbourIds.add(1,null);
		}
		if (bottom != null){
			result.neighbourIds.add(2,bottom.ID);
		}else{
			result.neighbourIds.add(2,null);
		}
		if (left != null){
			result.neighbourIds.add(3,left.ID);
		}else{
			result.neighbourIds.add(3,null);
		}
		return result;
	}
	public boolean hasTopLeft(){ return topleft; }
	public boolean hasTopRight(){ return topright; }
	public boolean hasBottomLeft(){ return bottomleft; }
	public boolean hasBottomRight(){ return bottomright; }
		public void addTop(Block b){
		topleft = (b == null) ? true : false;
		topright = (b == null) ? true : false;
		top = b;
	}
	public void addRight(Block b){
		bottomright = (b == null) ? true : false;
		topright = (b == null) ? true : false;
		right = b;
	}
	public void addBottom(Block b){
		bottomleft = (b == null) ? true : false;
		bottomright = (b == null) ? true : false;
		bottom = b;
	}
	public void addLeft(Block b){
		topleft = (b == null) ? true : false;
		bottomleft = (b == null) ? true : false;
		left = b;
	}
	public void connectTop(Block b){
		addTop(b);
		b.addBottom(this);
	}
	public void connectRight(Block b){
		addRight(b);
		b.addLeft(this);
	}
	public void connectBottom(Block b){
		addBottom(b);
		b.addTop(this);
	}
	public void connectLeft(Block b){
		addLeft(b);
		b.addRight(this);
	}
	public void setAdjacentCoords(){
		if (top != null && top.coordinate == null){
			top.coordinate = new Coord(coordinate.x,coordinate.y+1);
			top.setAdjacentCoords();
		}
		if (right != null && right.coordinate == null){
			right.coordinate = new Coord(coordinate.x+1,coordinate.y);
			right.setAdjacentCoords();
		}
		if (bottom != null && bottom.coordinate == null){
			bottom.coordinate = new Coord(coordinate.x,coordinate.y-1);
			bottom.setAdjacentCoords();
		}
		if (left != null && left.coordinate == null){
			left.coordinate = new Coord(coordinate.x-1,coordinate.y);
			left.setAdjacentCoords();
		}			
	}
	
	public void setAdjacentCoordsToNull(){
		if (top != null && top.coordinate != null){
			top.coordinate = null;
			top.setAdjacentCoordsToNull();
		}
		if (right != null && right.coordinate != null){
			right.coordinate = null;
			right.setAdjacentCoordsToNull();
		}
		if (bottom != null && bottom.coordinate != null){
			bottom.coordinate = null;
			bottom.setAdjacentCoordsToNull();
		}
		if (left != null && left.coordinate != null){
			left.coordinate = null;
			left.setAdjacentCoordsToNull();
		}			
	}
}
