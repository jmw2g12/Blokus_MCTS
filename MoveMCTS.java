
/**
 * Moves are simple to manage for Connect Four. We
 * store in each move only the row of the board
 * where this piece will be inserted. The board
 * itself is responsible for implementing
 * the function that actually performs this move.
 */
public class MoveMCTS implements Move {
	Piece piece;
	
	public MoveMCTS(Piece piece){
		this.piece = piece;
	}
	
	public Piece getPiece(){
		return piece;
	}
	
	public String toString(){
		return piece.getStringRepresentation();
	}
}