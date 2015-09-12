// Represent a square on the Sudoku board

public class Square {
	int col;
	int row;
	int value;
	boolean given; // not really used
	
	public Square(int row, int col, int value, boolean given){
		this.row = row;
		this.col = col;
		this.value = value;
		this.given = given;
	}
	
	public int get(){
		return value;
	}
	
	public boolean set(int val){
		if(!this.isGiven()){
			value = val;
			return true;
		}
		return false;
	}
	
	public boolean isGiven(){
		return given;
	}
	
	public void setGiven(){
		given = true;
	}
}
