/*
 *  TFTP_UDP
 *	Core Module
 *	User chooses whether to send or receive.
 *		SEND:	User must specify target host and message.
 *		RECEIVE:	Program waits for an incomming packet.
 */

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.util.*;


public class TFTP_UDP extends JFrame implements ActionListener{
	public static final int PORT = 9001;
	private Thread sender, receiver;
	public static JButton sendButton = new JButton("Send");
	public static JButton receiveButton = new JButton("Receive");
	private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
	private JPanel panel = new JPanel();
	public static JTextArea output = new JTextArea(10, 30);
	public static boolean buttonsEnabled;
	
	public TFTP_UDP(){
		buildGUI();
	}
	
	private void buildGUI() {
		setLayout(new BorderLayout());
		

		//  Add text output panel
		output.setEditable(false);
		panel.add(output);
		add(panel, BorderLayout.NORTH);
		
		//  Add send and receive buttons
		panel = new JPanel();
		sendButton.addActionListener(this);
		panel.add(sendButton);
		receiveButton.addActionListener(this);
		panel.add(receiveButton);
		add(panel, BorderLayout.SOUTH);
		
		//  Window maintenance
		pack();
		setLocationRelativeTo(null);
		setTitle("TFTP Using UDP");
		setIconImage(new ImageIcon("network.png").getImage());
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e) {
		// Clear output screen before each send/receive operation.
		flush();
		if(e.getSource() == sendButton) {
			// Disable buttons while sending.
			receiveButton.setEnabled(false);
			sendButton.setEnabled(false);
			buttonsEnabled = false;
			
			// Open the file-selector window.
			int returnVal = fc.showOpenDialog(this);
			File file = fc.getSelectedFile();
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				output("Sending: " + file.getName() + '\n');
				//load the file to be sent
				TextFileManager textFile = new TextFileManager(file.toString(),file.getName());
				textFile.loadFile();
				//send the file once IP and filecontents are obtained
				sender = new Thread(new UDPSender(JOptionPane.showInputDialog("Enter Target IP: "), textFile.getFileContents()));
				sender.start();
			} else {
				output("Open command cancelled by user.\n");
			}
		}
		
		if(e.getSource() == receiveButton) {
			// Disable buttons while receiving.
			receiveButton.setEnabled(false);
			sendButton.setEnabled(false);
			buttonsEnabled = false;
			
			// Start the receiver thread.
			receiver = new Thread(new UDPReceiver());
			receiver.start();
		}
	}
	
	public void flush(){
		// Clears the output screen.
		output.setText("");
	}
	
	public static void output(String s) {
		// Prints to the output screen.
		output.append(s);
	}
	
	public static void main(String[] args){
		//  Set style to default windows
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
		catch (Exception e) { System.out.println("Error: " + e); }
		finally { new TFTP_UDP(); }
	}
}