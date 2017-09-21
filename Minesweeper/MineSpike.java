// spike for random mines and numbers

import java.util.*;

public class MineSpike {
	private String[][] board;
	private int rows;
	private int cols;
	private int numMines;
	
	public MineSpike() {
		rows = 10;
		cols = 10;
		board = new String[rows][cols];
		numMines = 10;
	}
	
	public MineSpike(int r, int c, int mines) {
		rows = r;
		cols = c;
		board = new String[r][c];
		numMines = mines;
	}
	
	public void fillBoard() {
		Random ran = new Random();
		for(int i = 0; i < numMines; i++) {
			int r = ran.nextInt(rows);
			int c = ran.nextInt(cols);
			board[r][c] = "X";
		}
		int r = 0;
		int c = 0;
		while(r < rows) { //runs until the r variable = rows
			int xCount = 0; // tracks the number of mines around a particular spot
			for(int i = -1; i == 1; i++) { //checks each spot around the board[r][c] for
				for(int j = -1; j == 1; j++) {
					try {
						if(board[r + i][c + j].equals("X")) {
							xCount++;
						}
					}
					catch (ArrayIndexOutOfBoundsException e) {}
				}
			}
			if(xCount == 0 && !board[r][c].equals("X")) {
				board[r][c] = " ";
			}
			else if(!board[r][c].equals("X")) {
				Integer mineCount = new Integer(xCount);
				board[r][c] = mineCount.toString();
			}
			c++; //increases the c (column) variable by 1
			if(c == cols) { //if c = cols variable, resets c to 0 and increases r (row) by 1
				c = 0; 
				r++;
			}
		}
	}
}