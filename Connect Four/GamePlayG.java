import javax.swing.*;
import java.text.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class GamePlayG
{
	public static Player redPlr=new Player(ClrPlr.RED);
	public static Player blkPlr=new Player(ClrPlr.BLACK);
	private ImageIcon blankSq,redSq,blackSq,check;


	public ClrPlr CheckWin(ClrPlr plr,int row,int col,JLabel[][] board)
	{
		int total=0;

		int totalSame=1;

		if(plr==ClrPlr.RED)
			check=Board.redSq;
		else if(plr==ClrPlr.BLACK)
			check=Board.blackSq;
		else
			check=Board.blankSq;

		// Horizontal
		boolean start = false;
		int count = 0;
		for(int i = -3; i <=3; i++) {
			try {
				if(board[row][col+i].getIcon() == check)
					start = true;
				else
					start = false;
				if(start) count++;
				else count = 0;
				if(count >= 4) return plr;
			}
			catch(ArrayIndexOutOfBoundsException e) {}
		}

		// Vertical
		start = false;
		count = 0;
		for(int i = -3; i <=3; i++) {
			try {
				if(board[row+i][col].getIcon() == check)
					start = true;
				else
					start = false;
				if(start) count++;
				else count = 0;
				if(count >= 4) return plr;
			}
			catch(ArrayIndexOutOfBoundsException e) {}
		}

		// Down-diagonal
		start = false;
		count = 0;
		for(int i = -3, j = -3 ; i <= 3 && j <= 3; i++, j++) {
			try {
				if(board[row+i][col+j].getIcon() == check)
					start = true;
				else
					start = false;
				if(start) count++;
				else count = 0;
				if(count >= 4) return plr;
			}
			catch(ArrayIndexOutOfBoundsException e) {}
		}

		// Up-diagonal
		start = false;
		count = 0;
		for(int i = 3, j = -3 ; i >= -3 && j <= 3; i--, j++) {
			try {
				if(board[row+i][col+j].getIcon() == check)
					start = true;
				else
					start = false;
				if(start) count++;
				else count = 0;
				if(count >= 4) return plr;
			}
			catch(ArrayIndexOutOfBoundsException e) {}
		}

		return ClrPlr.NONE;

	}

	public static void main(String[] args)
	{
		Board board = new Board();
		int choice = JOptionPane.showConfirmDialog(null, "Would you like to load a saved game?", "Load?", JOptionPane.YES_NO_OPTION);
		if(choice == JOptionPane.YES_OPTION) {
			Board.loadGame();
		}
		board.setTitle("Connect-Four");
		board.setSize(new Dimension(600,700));
		board.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//board.pack();
		board.setVisible(true);
	}
}































