// Represents the Sudoku board
import java.util.ArrayList;

public class Board {
	Square[][] squares;
	
	public Board(Square[][] squares){
		this.squares = squares;
	}
	
	public int get(int row, int col){
		return squares[row][col].get();
	}
	
	public boolean set(int row, int col, int value){
		if(this.squares[row][col].set(value)) return true;
		return false;
	}
	
	public Square nextEmpty(){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(this.get(i,j) == 0) return this.squares[i][j];
			}
		}
		return new Square(-1,-1,-1,false);
	}
	
	// Get currently allowable assignments for variable
	public ArrayList<Integer> varDomain(int row, int col){
		ArrayList<Integer> d = new ArrayList<Integer>();
		for(int i=1; i<10; i++){
			if(meetsConstraints(row, col, i) && !this.squares[row][col].isGiven())
				d.add(i);
		}
		return d;
	}
	
	// Return list of other variables in row, column, and box
	public ArrayList<Square> getNeighbors(int row, int col){ // May need to check constraints
//		Square current = new Square(row, col, 0, false);
		ArrayList<Square> vars = new ArrayList<Square>();
		
		// Get row neighbors
		for(int i=0; i<9; i++){
			// Skip if same 
			if(i == col) continue;
			if(!this.squares[row][i].isGiven() && !vars.contains(squares[row][i]))
				vars.add(this.squares[row][i]);
		}
		
		// Get column neighbors
		for(int j=0; j<9; j++){
			// Skip if same
			if(j == row) continue;
			if(!this.squares[j][col].isGiven() && !vars.contains(squares[j][col]))
				vars.add(squares[j][col]);
		}
		
		// Get box neighbors
		int r, c, limR, limC;
		if(row<3){
			r=0;
			limR=r+3;
		}else if(row>2 && row<6){
			r=3;
			limR=r+3;
		}else{
			r=6;
			limR=r+3;
		}
		
		if(col<3){
			c=0;
			limC=c+3;
		}else if(col>2 && col<6){
			c=3;
			limC=c+3;
		}else{
			c=6;
			limC=c+3;
		}
		
		for(int k=r; k<limR; k++){
			for(int l=c; l<limC; l++){
				// Skip if same
				if(k==row && l==col) continue;
				if(!this.squares[k][l].isGiven() && !vars.contains(this.squares[k][l])){
					vars.add(this.squares[k][l]);
				}
			}
		}
		return vars;
	}
	
	// Get all currently blank squares
	public ArrayList<Square> getVars(){
		ArrayList<Square> vars = new ArrayList<Square>();
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(!squares[i][j].isGiven() && squares[i][j].value == 0) // THIS CHANGED
					vars.add(squares[i][j]);
			}
		}
		return vars;
	}
	
	public boolean complete(){
		if(this.nextEmpty().value == -1) return true;
		return false;
	}
	
	// Reset board
	public void clear(){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(!squares[i][j].isGiven()) this.set(i,j,0);
			}
		}
	}
	
	public boolean noSolution(){
		if(this.complete() && !meetsConstraints()){
			return true;
		}
		return false;
	}
	
	public boolean meetsConstraints(){
		// check all as is
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(!checkRow(i,j,this.get(i,j))) return false;
				if(!checkCol(i,j,this.get(i,j))) return false;
				if(!checkBox(i,j,this.get(i,j))) return false;
			}
		}
		return true;
	}
	
	public boolean meetsConstraints(int row, int col, int v){
		if(checkRow(row, col, v) && checkCol(row, col, v) && checkBox(row, col, v)) return true;
		return false;
	}
	
	public boolean checkRow(int row, int col, int v){
		for(int i=0; i<9; i++){
			if(this.squares[row][i].get()==v) return false;
		}
		return true;
	}
	
	public boolean checkCol(int row, int col, int v){
		for(int i=0; i<9; i++){
			if(this.squares[i][col].get()==v) return false;
		}
		return true;
	}
	
	public boolean checkBox(int row, int col, int v){
		int r, c, limR, limC;
		if(row<3){
			r=0;
			limR=r+3;
		}else if(row>2 && row<6){
			r=3;
			limR=r+3;
		}else{
			r=6;
			limR=r+3;
		}
		
		if(col<3){
			c=0;
			limC=c+3;
		}else if(col>2 && col<6){
			c=3;
			limC=c+3;
		}else{
			c=6;
			limC=c+3;
		}
		
		for(int i=r; i<limR; i++){
			for(int j=c; j<limC; j++){
				if(this.squares[i][j].get()==v) return false;
			}
		}
		return true;
	}
}
