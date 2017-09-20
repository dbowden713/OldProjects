import javax.swing.*;
import javax.swing.Timer.*;
import java.text.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

enum ClrPlr{RED,BLACK,NONE}

public class Board extends JFrame implements MouseListener
{
	public static ImageIcon blankSq = new ImageIcon("blank.GIF");
	public static ImageIcon redSq = new ImageIcon("red.GIF");
	public static ImageIcon blackSq = new ImageIcon("black.GIF");
	public static ImageIcon whiteSq = new ImageIcon("white.GIF");
	public static ImageIcon blackCheck = new ImageIcon("blackChecker.GIF");
	public static ImageIcon redCheck = new ImageIcon("redChecker.GIF");
	private JLabel imgLabel,redLabel,blkLabel;
	private static JLabel[][] board;
	private int row,col;
	private int rowLast;
	private int colLast;
	private  JLabel[] topLabel;
	private JLabel inst;
	private JPanel panel=new JPanel();
	private JPanel insPanel;
	javax.swing.Timer time;



	private static ClrPlr plrClr=ClrPlr.RED;
	private ClrPlr plrWin=ClrPlr.NONE;

	public Board()
	{
		row=6;
		col=7;

		board=new JLabel[row][col];
		topLabel = new JLabel[7];
		
		insPanel = new JPanel();
		
		inst =  new JLabel("Connect Four!", JLabel.CENTER);
		inst.setFont(new Font("Serif", Font.BOLD, 48));
		insPanel.add(inst);
		
		JPanel topPanel=new JPanel(new GridLayout(1,7));
		for(int i=0; i<7; i++)
		{
			topLabel[i] = new JLabel(whiteSq);
			topLabel[i].addMouseListener(this);
			topPanel.add(topLabel[i]);
			topLabel[i].setBorder(BorderFactory.createLineBorder(Color.black));
		}

		redLabel=new JLabel(redSq);
		blkLabel=new JLabel(blackSq);

		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				imgLabel= new JLabel(blankSq);
				board[i][j]=imgLabel;
			}
		}

		panel.setLayout(new GridLayout(row,col));
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				panel.add(board[i][j]);
			}
		}
		
		JPanel upperPanel = new JPanel(new GridLayout(2,1));
		insPanel.setPreferredSize(new Dimension(50,50));
		upperPanel.add(insPanel);
		upperPanel.add(topPanel);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(upperPanel, BorderLayout.NORTH);
		this.getContentPane().add(panel,BorderLayout.CENTER);
	}
	
	private int locateTile(Object source) 
	{
        int loc;
        for(int i = 0; i < topLabel.length; i++) 
        { 
			if(source == topLabel[i])
			{
				loc = i;
				return loc;
			}
            
        }
        return 0;
    }

	public void mouseClicked(MouseEvent event)
	{
		int loc = locateTile(event.getSource());
		int col=loc;
		int row=5;
		int total=0;
		GamePlayG game=new GamePlayG();
		
		topLabel[loc].setIcon(whiteSq);


		boolean empty=true;
		while(empty)
		{
			if(board[row][col].getIcon()==blankSq)
			{
				if(plrClr==ClrPlr.RED)
				{
					board[row][col].setIcon(redSq);
					empty=false;
				}
				else
				{
					board[row][col].setIcon(blackSq);
					empty=false;
				}
			}
			else
				row--;
			
		}
		
		rowLast=row;
		colLast=col;
		String result;

		ClrPlr	plrWin=game.CheckWin(plrClr,rowLast,colLast,board);
		boolean endOfBoard = false;
		int fullCount = 0;
		for(int i = 0; i < 6; i++) { //counts the number of squares with something in them
			for(int j = 0; j < 7; j++) {
				if(board[i][j].getIcon() != blankSq) {
					fullCount++;
				}
			}
		}
		if(fullCount == 42) { // there are 42 squares on the board so if 42 squares have something in them, the board is labeled as full
			endOfBoard = true;
		}
		total++;

		switch(plrClr)
				{
					case RED:
						topLabel[colLast].setIcon(blackCheck);
						plrClr=ClrPlr.BLACK;
						break;
					case BLACK:
						topLabel[colLast].setIcon(redCheck);
						plrClr=ClrPlr.RED;
						break;
					default:
						plrClr=ClrPlr.NONE;
				}

		int again=5;
		if(plrWin!=ClrPlr.NONE)
			{
				if(plrWin==ClrPlr.RED)
				{
					result="Player Red wins!!";
					JOptionPane.showMessageDialog(null,result);
					game.redPlr.setWin();
					game.blkPlr.setLoss();
				}
				else if(plrWin==ClrPlr.BLACK)
				{
					result="Player Black wins!!";
					JOptionPane.showMessageDialog(null,result);

					game.redPlr.setLoss();
					game.redPlr.setWin();
				}
			/*	else if(endOfBoard)
				{
					result="There is a tie!";
					JOptionPane.showMessageDialog(null,result);
					game.redPlr.setTie();
					game.blkPlr.setTie();
				}*/
				again=JOptionPane.showConfirmDialog(null,"Play again?");

			}
			
		if(endOfBoard && plrWin == ClrPlr.NONE) { //checks if in the last spot on the board and sets the tie if there is no winning condition met.
			result = "There is a tie!";
			JOptionPane.showMessageDialog(null, result);
			game.redPlr.setTie();
			game.blkPlr.setTie();
			again = JOptionPane.showConfirmDialog(null, "Play again?");
		}

			if(again==JOptionPane.YES_OPTION)
					{
						clrBrd();
						plrWin=ClrPlr.NONE;
					}
			else if(again==JOptionPane.NO_OPTION)
			{
				int rW=game.redPlr.getWins();
				int rL=game.redPlr.getLoss();
				int bW=game.blkPlr.getWins();
				int bL=game.blkPlr.getLoss();
				int tie=game.redPlr.getTie();
				result="Player Red: \n"+
				"Number Wins: " + rW + "\n Number Losses: " +rL+
				"\n Player Black: \n Number Wins: "+bW+"\n Number Losses: "
				+bL+"\n Total Ties: "+tie;
				JOptionPane.showMessageDialog(null,result);

				System.exit(0);
			}
		saveGame();
	}
	public void mousePressed(MouseEvent event){}
	public void mouseReleased(MouseEvent event){}
	public void mouseEntered(MouseEvent event)
	{
		int loc = locateTile(event.getSource());
		if(plrClr == ClrPlr.BLACK)
			topLabel[loc].setIcon(blackCheck);
		else
			topLabel[loc].setIcon(redCheck);
	}
	public void mouseExited(MouseEvent event)
	{
		int loc = locateTile(event.getSource());
		topLabel[loc].setIcon(whiteSq);
	}


	public  int getRowLastEntered()
	{

		return rowLast;
	}
	public  int getColLastEntered()
	{
		return colLast;
	}

	public  ClrPlr getColor()
	{
		if(plrClr==ClrPlr.RED)
			return ClrPlr.BLACK;
		else
			return ClrPlr.RED;

	}

	public  JLabel[][] getBoard()
	{
		return board;
	}

	public ClrPlr getWinner()
	{
		return plrWin;
	}

	public void clrBrd()
	{
		plrWin=ClrPlr.NONE;
		plrClr = ClrPlr.RED;
		for(int i=0;i<row;i++)
				{
					for(int j=0;j<col;j++)
					{

						board[i][j].setIcon(blankSq);
					}
		}

	}
	
	public boolean isFull()
	{
		for(int i=0; i < board.length; i++)
			for(int j=0; j<board[0].length; j++)
				if(board[i][j].getIcon() == blankSq)
					return false;
					
		return true;
	}

	protected void saveGame() {
		try {
			PrintWriter output = new PrintWriter(new File("save.txt"));
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[0].length; j++) {
					output.print(board[i][j].getIcon() + " ");
				}
			}
			output.print(plrClr);
			output.close();
		}
		catch(java.io.FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "The file save.txt was not found.  Game will not be saved...", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	protected static void loadGame() {
		try{
			Scanner input = new Scanner(new File("save.txt"));
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[0].length; j++) {
					String icon = input.next();
					if(icon.equals("blank.GIF"))
						board[i][j].setIcon(blankSq);
					if(icon.equals("red.GIF"))
						board[i][j].setIcon(redSq);
					if(icon.equals("black.GIF"))
						board[i][j].setIcon(blackSq);
				}
			}
			String nextColor = input.next();
			if(nextColor.equals("RED")) plrClr = ClrPlr.RED;
			if(nextColor.equals("BLACK")) plrClr = ClrPlr.BLACK;
			input.close();
		}
		catch(java.io.FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "The file save.txt was not found.  Game can not be loaded...", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
}