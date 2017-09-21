import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

enum Mine{MINE,NONE,ONE,TWO,THREE,FOUR,FIVE, SIX, SEVEN, EIGHT};

public class MineSweeper extends JFrame implements MouseListener, ActionListener {
	private JLabel[][] tiles;
	private JMenuBar menuBar = new JMenuBar();
	private JPanel board = new JPanel();
	private JMenu menu;
	private JMenuItem menuItem;
	private JLabel numMines;
	private JPanel panel = new JPanel();
	private JLabel timeLabel = new JLabel("0");
	private javax.swing.Timer timer = new javax.swing.Timer(1000, this);
	private int seconds;
	private String currentDifficulty = "";
	private Random generator = new Random();
	private int randomRow, randomCol;
	private Mine[][] mineBoard;
	private ImageIcon sq1, sq2, sq3, sq4, sq5, sq6, sq7, sq8, sq0, unsel, mine;
	private int numTiles, numTilesUn;
	private JLabel face = new JLabel(new ImageIcon("img/smile.jpg"));
	
	public MineSweeper() {
		MineSweeper defaultInit = new MineSweeper("Easy");
	}
	
	private MineSweeper(String difficulty) {
		
		buildMenu();
		setLayout(new BorderLayout());
		
		currentDifficulty = difficulty;
		
		// load resource files for tiles
		sq0 = new ImageIcon("img/sq0.GIF");
		sq1 = new ImageIcon("img/sq1.GIF");
		sq2 = new ImageIcon("img/sq2.GIF");
		sq3 = new ImageIcon("img/sq3.GIF");
		sq4 = new ImageIcon("img/sq4.GIF");
		sq5 = new ImageIcon("img/sq5.GIF");
		sq6 = new ImageIcon("img/sq6.GIF");
		sq7 = new ImageIcon("img/sq7.GIF");
		sq8 = new ImageIcon("img/sq8.GIF");
		mine = new ImageIcon("img/mine.GIF");
		unsel = new ImageIcon("img/unselected.png");
		
		// easy - 10x10 board with 10 mines
		if(difficulty.equals("Easy")) {
			tiles = new JLabel[10][10];
			mineBoard = new Mine[10][10];
			board.setLayout(new GridLayout(10, 10));
			numMines = new JLabel("10");
			setMines(10, 10, 10);
			numTiles = 90;
			numTilesUn = 0;
		}
		
		// medium - 20x20 board with 50 mines
		if(difficulty.equals("Medium")) {
			tiles = new JLabel[20][20];
			mineBoard = new Mine[20][20];
			board.setLayout(new GridLayout(20, 20));
			numMines = new JLabel("50");
			setMines(50, 20, 20);
			numTiles = 350;
			numTilesUn = 0;
		}
		
		// hard - 30x30 board with 100 mines
		if(difficulty.equals("Hard")) {
			tiles = new JLabel[30][30];
			mineBoard = new Mine[30][30];
			board.setLayout(new GridLayout(30, 30));
			numMines = new JLabel("100");
			setMines(100, 30, 30);
			numTiles = 800;
			numTilesUn = 0;
		}

		// create grid
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = new JLabel();
				tiles[i][j].setPreferredSize(new Dimension(20, 20));
				tiles[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				tiles[i][j].setIcon(unsel);
				tiles[i][j].addMouseListener(this);
				board.add(tiles[i][j]);
			}
		}

		// create scoreboard
		panel.add(new JLabel("Mines:"));
		panel.add(numMines);
		panel.add(face);
		panel.add(new JLabel("Time:"));
		panel.add(timeLabel);
		
		add(panel, BorderLayout.NORTH);
		board.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		face.setBorder(BorderFactory.createEmptyBorder(0, tiles.length*3, 0, tiles.length*3));
		add(board);
		pack();
		setLocationRelativeTo(null);
		setTitle("Minesweeper");
		setIconImage(new ImageIcon("img/icon.png").getImage());
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// places mines on a hidden "mineboard" to check against as the user plays
	private void setMines(int numMines, int numRows, int numCols) {
		
		randomRow = generator.nextInt(numRows);
		randomCol = generator.nextInt(numCols);
		int numMinesplaced = 0;
		
		for(int i=0; i< numMines; i++)
		{
			if(mineBoard[randomRow][randomCol] != Mine.MINE)
				mineBoard[randomRow][randomCol] = Mine.MINE;
			else
				numMines++;
			randomRow = generator.nextInt(numRows);
			randomCol = generator.nextInt(numCols);
			numMinesplaced++;
		}
		
		int mineSurround = 0;
		
		for(int i=0; i<mineBoard.length; i++)
		{
			for(int j=0; j<mineBoard[0].length; j++)
			{
				try{
				if(mineBoard[i-1][j] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i+1][j] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i][j+1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i][j-1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i+1][j+1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i-1][j-1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i-1][j+1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i+1][j-1] == Mine.MINE)
					mineSurround++;
				}catch(ArrayIndexOutOfBoundsException e){}
				try{
				if(mineBoard[i][j] == Mine.MINE)
					mineSurround = 10;
				}catch(ArrayIndexOutOfBoundsException e){}
				
					
				if(mineSurround == 1)
					mineBoard[i][j] = Mine.ONE;
				else if(mineSurround == 2)
					mineBoard[i][j] = Mine.TWO;
				else if(mineSurround == 3)
					mineBoard[i][j] = Mine.THREE;
				else if(mineSurround == 4)
					mineBoard[i][j] = Mine.FOUR;
				else if(mineSurround == 5)
					mineBoard[i][j] = Mine.FIVE;
				else if(mineSurround == 0)
					mineBoard[i][j] = Mine.NONE;
				
					
				mineSurround = 0;
			}	
		}
	}
	
	private void buildMenu() {
		menu = new JMenu("Game");
		menuItem = new JMenuItem("New Game");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Easy");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Medium");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Hard");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("High Scores");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuBar.add(menu);
		menu = new JMenu("Help");
		menuItem = new JMenuItem("How to play");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("About...");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	// used to find the specific tile a user has clicked on
	private int[] locateTile(Object source) {
		int[] loc = new int[2];
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				if(source == tiles[i][j]) {
					loc[0] = i;
					loc[1] = j;
					return loc;
				}
			}
		}
		return null;
	}
	
	// floodfill recursive function to check for mines and flag the board appropriately
	private void sweep(int row, int col) {
		if(tiles[row][col].getIcon() != unsel) return;
		
		numTilesUn++;
		
		// check how many mines are around this tile
		if(mineBoard[row][col] == Mine.ONE)
			tiles[row][col].setIcon(sq1);
		else if(mineBoard[row][col] == Mine.TWO)
			tiles[row][col].setIcon(sq2);
		else if(mineBoard[row][col] == Mine.THREE)
			tiles[row][col].setIcon(sq3);
		else if(mineBoard[row][col] == Mine.FOUR)
			tiles[row][col].setIcon(sq4);
		else if(mineBoard[row][col] == Mine.FIVE)
			tiles[row][col].setIcon(sq5);
		else if(mineBoard[row][col] == Mine.SIX)
			tiles[row][col].setIcon(sq6);
		else if(mineBoard[row][col] == Mine.SEVEN)
			tiles[row][col].setIcon(sq7);
		else if(mineBoard[row][col] == Mine.EIGHT)
			tiles[row][col].setIcon(sq8);
		else if(mineBoard[row][col] == Mine.NONE)
			tiles[row][col].setIcon(sq0);
		

		// floodfill
		if(mineBoard[row][col] == Mine.NONE)
		{
			try{
			 if(mineBoard[row+1][col] != Mine.MINE && tiles[row+1][col].getIcon() == unsel)
				sweep(row+1, col);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row-1][col] != Mine.MINE && tiles[row-1][col].getIcon() == unsel)
				sweep(row-1, col);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row][col+1] != Mine.MINE && tiles[row][col+1].getIcon() == unsel)
				sweep(row, col+1);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row][col-1] != Mine.MINE && tiles[row][col-1].getIcon() == unsel)
				sweep(row, col-1);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row+1][col+1] != Mine.MINE && tiles[row+1][col+1].getIcon() == unsel)
				sweep(row+1, col+1);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row+1][col-1] != Mine.MINE && tiles[row+1][col-1].getIcon() == unsel)
				sweep(row+1, col-1);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row-1][col+1] != Mine.MINE && tiles[row-1][col+1].getIcon() == unsel)
				sweep(row-1, col+1);
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
			 if(mineBoard[row-1][col-1] != Mine.MINE && tiles[row-1][col-1].getIcon() == unsel)
				sweep(row-1, col-1);
			}catch(ArrayIndexOutOfBoundsException e){}
		}
	}
	
	// if there are only mines left on the board and you haven't lost, you win
	private void checkWin() {
		if(numTilesUn == numTiles)
		{
			face.setIcon(new ImageIcon("img/cool.jpg"));
			JOptionPane.showMessageDialog(null,"You Win!!");
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper(currentDifficulty);
			timer.stop();
		}
	}
	
	private void displayScores() {
		String easyName, easyTime, medName, medTime, hardName, hardTime;
		easyName = easyTime = medName = medTime = hardName = hardTime = "N/A";
		JOptionPane.showMessageDialog(null, "Difficulty      Name             Best Time\n" + 
											"Easy                 " + easyName + "           " + easyTime + "\n" +
											"Medium	           " + medName + "            " + medTime + "\n" +
											"Hard                 " + hardName + "           " + hardTime, "High Scores", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void mouseClicked(MouseEvent e) {
		
		int[] loc = locateTile(e.getSource());
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			sweep(loc[0], loc[1]);
			if(!timer.isRunning())
				timer.start(); // start the timer on the first "sweep"
				
			checkWin();
		}
			
		// when user clicked a mine
		if(mineBoard[loc[0]][loc[1]] == Mine.MINE && e.getButton() == MouseEvent.BUTTON1 && !tiles[loc[0]][loc[1]].getIcon().toString().equals("img/flagged.png"))
		{
			for(int i=0; i<mineBoard.length; i++)
				for(int j=0; j<mineBoard[0].length; j++)
					if(mineBoard[i][j] == Mine.MINE)
						tiles[i][j].setIcon(mine);
						
			timer.stop();
			face.setIcon(new ImageIcon("img/dead.jpg"));
			JOptionPane.showMessageDialog(null,"You lose!");
			tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.RED));
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper(currentDifficulty);
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		int[] loc = locateTile(e.getSource());
		tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.RED));
	}
	
	public void mouseExited(MouseEvent e) {
		int[] loc = locateTile(e.getSource());
		tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public void mousePressed(MouseEvent e) {
		// find which tile was clicked
		int[] loc = locateTile(e.getSource());
		
		//for left-click
		if(e.getButton() == MouseEvent.BUTTON1) {
			tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.GREEN));
			face.setIcon(new ImageIcon("img/uhoh.jpg"));
		}
			
		//for right-click, flag a tile as a mine
		if(e.getButton() == MouseEvent.BUTTON3) {
			tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.ORANGE));
			if(tiles[loc[0]][loc[1]].getIcon().toString().equals("img/flagged.png")) {
				tiles[loc[0]][loc[1]].setIcon(unsel);
				numMines.setText(Integer.toString(Integer.parseInt(numMines.getText()) + 1));
			}
			else if(tiles[loc[0]][loc[1]].getIcon().toString().equals("img/unselected.png") && !numMines.getText().equals("0")) {
				tiles[loc[0]][loc[1]].setIcon(new ImageIcon("img/flagged.png"));
				numMines.setText(Integer.toString(Integer.parseInt(numMines.getText()) - 1));
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		face.setIcon(new ImageIcon("img/smile.jpg"));
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer){
			seconds++;
			timeLabel.setText(String.valueOf(seconds));
			return;
		}
		//format for checking menu options
		if(((JMenuItem)e.getSource()).getText().equals("New Game")) {
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper(currentDifficulty);
		}
		if(((JMenuItem)e.getSource()).getText().equals("Easy")) {
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper("Easy");
		}
		if(((JMenuItem)e.getSource()).getText().equals("Medium")) {
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper("Medium");
		}
		if(((JMenuItem)e.getSource()).getText().equals("Hard")) {
			dispose();
			MineSweeper newGame;
			newGame = new MineSweeper("Hard");
		}
		if(((JMenuItem)e.getSource()).getText().equals("High Scores")) {
			displayScores();
		}
		if(((JMenuItem)e.getSource()).getText().equals("How to play")) {
			JOptionPane.showMessageDialog(null, "Click the squares to sweep for mines.\n" +
			"If the square wasn't a mine, the safe\nareas around the square are revealed.\n" +
			"A square with a number indicates how\nmany mines are touching that square.\n" +
			"You win if you can uncover all of the mines.\nClick on a mine and the game is over!", 
			"How to play", JOptionPane.INFORMATION_MESSAGE);
		}
		if(((JMenuItem)e.getSource()).getText().equals("About...")) {
			JOptionPane.showMessageDialog(null, "Minesweeper\nCreated by Daniel Bowden", "About", JOptionPane.INFORMATION_MESSAGE);
		}
		if(((JMenuItem)e.getSource()).getText().equals("Exit"))
			System.exit(0);
	}
	
	public static void main(String[] args) {
		MineSweeper init = new MineSweeper();
	}
}