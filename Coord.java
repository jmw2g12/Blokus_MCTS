public class Coord{
	public int x,y;
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	public Coord(){
		this.x = 0;
		this.y = 0;
	}
	public String to_string(){
		return x + "," + y;
	}
	public int manhattanDistance(Coord c){
		//System.out.println("these coords : " + this.x + ", " + this.y);
		//System.out.println("c's coords : " + c.x + ", " + c.y);
		//System.out.println("result = " + (Math.abs(c.x - this.x) + Math.abs(c.y - this.y)));
		return (Math.abs(c.x - this.x) + Math.abs(c.y - this.y));
	}
	public int productScore(Coord c){
		return (Math.abs(c.x - this.x) * Math.abs(c.y - this.y));
	}
	public Coord clone(){
		Coord result = new Coord(x,y);
		return result;
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Coord && ((Coord)obj).x == x && ((Coord)obj).y == y) return true;
		return false;
	}
}
