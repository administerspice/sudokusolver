// Yujun Jiang 
// Sudoku Solver implementing Recursive Backtracking, AC-3, and Forward Checking

import java.util.*;

public class BacktrackingSearch {
	public static int moveCount = 0;
	public static Board board;
	
	// AC-3 variable domains
	public static HashMap<Square, ArrayList<Integer>> domains = new HashMap<Square, ArrayList<Integer>>();
	public static HashMap<Square, ArrayList<Integer>> inferences = new HashMap<Square, ArrayList<Integer>>();
	public static LinkedList<Arc> arcList = new LinkedList<Arc>();
	public static HashMap<Square, ArrayList<Integer>> removed = new HashMap<Square, ArrayList<Integer>>(); 
	
	public static void main(String[] args){
		makeBoard();
		System.out.println("Puzzle:");
		printBoard();
		// Initialize domains
		for(Square s : board.getVars()){
			domains.put(s, board.varDomain(s.row, s.col));
			inferences.put(s, board.varDomain(s.row, s.col));
		}
		if(!solve()){
			System.out.println("There is no solution.");
			printBoard();
		}else{
			System.out.println("Solution:");
			printBoard();
		}
		solve();
		System.out.println("Number of Assignments: "+moveCount);
	}
	
	public static boolean solve(){
//		return RecursiveBackTracking(board.nextEmpty());
//		return AC3();
		// Forward checking
		return forwardChecking(board.nextEmpty(), inferences);
	}
	
	public static boolean RecursiveBackTracking(Square empty){
		if(board.complete()) return true;
		// Change back to full domain
//		for(int i : board.varDomain(empty.row, empty.col)){
		for(int i : domains.get(empty)){
			if(board.meetsConstraints(empty.row, empty.col, i)){
				board.set(empty.row, empty.col, i);
				moveCount++;
			}else{
				continue;
			}
			if(RecursiveBackTracking(board.nextEmpty()))
				return true;
			else 
				board.set(empty.row, empty.col, 0);
		}
		return false;
	}
	
	public static void forwardCheck(){
		board.clear();
		for(Square s : domains.keySet()){
			if(domains.get(s).size() == 1){
				board.set(s.row, s.col, domains.get(s).get(0));
				moveCount++;
			}
		}
//		backtrack(board.nextEmpty());
	}
	
	public static boolean forwardChecking(Square empty, HashMap<Square, ArrayList<Integer>> doms){
		if(board.complete()) return true;
		
//		for(int i : doms.get(empty)){
		for(int i : board.varDomain(empty.row, empty.col)){
			if(board.meetsConstraints(empty.row, empty.col, i)){
				board.set(empty.row, empty.col, i);
				moveCount++;
				// remove
//				for(Square s : board.getNeighbors(empty.row, empty.col)){
//					if(s!=empty) inferences.get(s).remove(new Integer(i));
//				}
				
				for(Square s : board.getNeighbors(empty.row, empty.col)){
					if(inferences.get(s).size() == 0 && s!=empty){ // If a neighbor has has no values left
						doms.get(empty).remove(new Integer(i)); // Remove value from older domains
						board.set(empty.row, empty.col, 0);  // Remove new assignment
//						break;
						return forwardChecking(board.nextEmpty(), doms);
					}
				}

				if(forwardChecking(board.nextEmpty(), inferences)){
					return true;
				}
				else{
					board.set(empty.row, empty.col, 0);
				}
				
			}
		}
		return false;
	}
	
	public static boolean AC3(){
		ArrayList<Square> allVars = board.getVars();
		// Initialize domains and arcList
		for(Square s : allVars)
			domains.put(s, board.varDomain(s.row, s.col));
//		for(Square s : domains.keySet()) System.out.println(domains.get(s).size());
		
		for(Square s : allVars) {
			for(Square t : board.getNeighbors(s.row, s.col)){
				Arc newArc = new Arc(s, t);
				if(s != t && !arcList.contains(newArc)){
					arcList.add(newArc);
				}
			}
		}

		Arc arc;
//		Outer:
		while(arcList.peek() != null){
			if(board.complete()) return true;
			arc = arcList.poll();
			if(domains.get(arc.x).size() > 0 && board.meetsConstraints(arc.x.row, arc.x.col, domains.get(arc.x).get(0))){
				if(board.set(arc.x.row, arc.x.col, domains.get(arc.x).get(0)))moveCount++;
			}else{
				continue;
			}

			if(revise(arc.x, arc.y)){
				if(board.set(arc.x.row, arc.x.col, 0)) moveCount--;
//				board.set(arc.y.row, arc.y.col, 0);
//				if(domains.get(arc.x).size() > 0 && board.meetsConstraints(arc.x.row, arc.x.col, domains.get(arc.x).get(0))){
//					if(board.set(arc.x.row, arc.x.col, domains.get(arc.x).get(0)))moveCount++;
//				}else{
//					continue;
//				}
				if(!domains.keySet().contains(arc.x)){
					System.out.println("Program terminated while analyzing "+arc.x.row+", "+arc.x.col);
					return false;
				}
//				if(domains.get(arc.x).size() == 0 && board.get(arc.x.row, arc.x.col) == 0){
////					System.out.println("Program terminated while analyzing "+arc.x.row+", "+arc.x.col);
//					for(Square s : domains.keySet()){
//						if(domains.get(s).size() > 0)
//							return AC3();
//					}
//					return false; // no solution
//				}
				for(Square k : board.getNeighbors(arc.x.row, arc.x.col)){
					Arc newArc = new Arc(k, arc.x);
					if(k != arc.x){
//						board.set(arc.x.row, arc.x.col, 0);
//						board.set(k.row, k.col, 0);
						arcList.add(newArc);
					}
				}
			}
		}
		
		return AC3();
		
//		return true;
	}
	
	public static boolean revise(Square x, Square y){
		boolean revised = false;
		Iterator<Integer> it = domains.get(x).iterator();
		while(it.hasNext()){
//		for(Integer i : domains.get(x)){
			int i = it.next();
			if(!anyY(x, y)){
//				domains.get(x).remove(i);
				it.remove();
//				if(removed.containsKey(x)){
//					removed.get(x).add(i);
//				}else{
//					ArrayList<Integer> dom = new ArrayList<Integer>();
//					dom.add(i);
//					removed.put(x, dom);
//				}
				revised = true;
			}
		}
		return revised;
	}
	
	// check if there are any available Y values satisfy constraints for x
	public static boolean anyY(Square x, Square y){
		ArrayList<Square> tmp = new ArrayList<Square>();
		for(Integer i : domains.get(y)){
//			System.out.println(i);
			if(board.meetsConstraints(y.row, y.col, i)){
				return true;
			}
		}
		return false;
	}
	
	public static void printBoard(){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				System.out.print(board.get(i,j)+" ");
				if((j+1)%3 == 0) System.out.print(" ");
			}
			if((i+1)%3 == 0) System.out.println();
			System.out.println();
		}
	}
	
	public static void makeBoard(){
		// Create new board:
		Square[][] squares = new Square[9][9];
		// Initialize
		for(int i=0; i<9; i++){	// Rows
			for(int j=0; j<9; j++){ // Columns
				squares[i][j] = new Square(i, j, 0, false);
			}
		}
		
		// Rows 0-2
		squares[0][1].set(9);
		squares[0][7].set(2);
		squares[0][8].set(5);
		squares[1][0].set(4);
		squares[1][2].set(5);
		squares[1][3].set(2);
		squares[2][1].set(6);
		squares[2][4].set(3);

		// Rows 3-5
		squares[3][0].set(2);
		squares[3][1].set(8);
		squares[3][3].set(3);
		squares[3][6].set(1);
		squares[3][8].set(9);
		squares[4][3].set(8);
		squares[4][5].set(1);
		squares[5][0].set(3);
		squares[5][2].set(7);
		squares[5][5].set(6);
		squares[5][7].set(4);
		squares[5][8].set(8);

		// Rows 6-8
		squares[6][4].set(1);
		squares[6][7].set(8);
		squares[7][5].set(3);
		squares[7][6].set(7);
		squares[7][8].set(2);
		squares[8][0].set(6);
		squares[8][1].set(3);
		squares[8][7].set(9);
		
		// Rows 0-2
		squares[0][1].setGiven();
		squares[0][7].setGiven();
		squares[0][8].setGiven();
		squares[1][0].setGiven();
		squares[1][2].setGiven();
		squares[1][3].setGiven();
		squares[2][1].setGiven();
		squares[2][4].setGiven();

		// Rows 3-5
		squares[3][0].setGiven();
		squares[3][1].setGiven();
		squares[3][3].setGiven();
		squares[3][6].setGiven();
		squares[3][8].setGiven();
		squares[4][3].setGiven();
		squares[4][5].setGiven();
		squares[5][0].setGiven();
		squares[5][2].setGiven();
		squares[5][5].setGiven();
		squares[5][7].setGiven();
		squares[5][8].setGiven();

		// Rows 6-8
		squares[6][4].setGiven();
		squares[6][7].setGiven();
		squares[7][5].setGiven();
		squares[7][6].setGiven();
		squares[7][8].setGiven();
		squares[8][0].setGiven();
		squares[8][1].setGiven();
		squares[8][7].setGiven();
		
		// Add values to board:
		board = new Board(squares);
	}
}
