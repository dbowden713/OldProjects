import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class ConnectFour extends JFrame implements MouseListener {
	private JLabel[][] tiles;

	public ConnectFour() {
		setLayout(new GridLayout(6, 7));
		setTitle("Connect Four");
		tiles = new JLabel[6][7];
		int count = 1;
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = new JLabel(String.valueOf(count));
				count++;
				tiles[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				tiles[i][j].setHorizontalAlignment(JLabel.CENTER);
				tiles[i][j].setPreferredSize(new Dimension(100, 100));
				tiles[i][j].addMouseListener(this);
				add(tiles[i][j]);
			}
		}
	}
	
	private int locateCol(Object source) {
		int loc;
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				if(source == tiles[i][j]) {
					loc = j;
					return loc;
				}
			}
		}
		return -1;
	}
	
	public void mouseClicked(MouseEvent event) {
		int loc = locateCol(event.getSource());
		for(int i = 0; i < tiles[i].length; i++)
			tiles[loc][i].setBorder(BorderFactory.createLineBorder(Color.green));
		//int[] move = locateMove(loc[0], loc[1]);
		//if(move != null) {
		//	tiles[move[0]][move[1]].setText(tiles[loc[0]][loc[1]].getText());
		//	tiles[loc[0]][loc[1]].setText("");
		//	tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(Color.black));
		//	}
		//if(checkForWin()) {
		//	JOptionPane.showMessageDialog(this, " You win!", "You win!", JOptionPane.INFORMATION_MESSAGE);
		//}
	}
	public void mousePressed(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {
/* 		int[] loc = locateTile(event.getSource());
		int[] move = locateMove(loc[0], loc[1]);
		Color color = Color.red;
		if(move != null) color = Color.green;
		if(!(tiles[loc[0]][loc[1]].getText().equals(""))) 
			tiles[loc[0]][loc[1]].setBorder(BorderFactory.createLineBorder(color, 4)); */
	}
	
	public void mouseExited(MouseEvent event) {
		JLabel label = (JLabel)event.getSource();
		label.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
/* 	public static void main(String [] args) {
		ConnectFour newBoard = new ConnectFour();
		newBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newBoard.pack();
		newBoard.setVisible(true);
	} */
}