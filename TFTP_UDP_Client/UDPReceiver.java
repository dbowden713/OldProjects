import java.net.*;
import java.io.*;
import java.util.*;

public class UDPReceiver implements Runnable{
	public void run ( ) {
	  try{
		// Create a datagram socket, bound to the specific port 2000
		DatagramSocket socket = new DatagramSocket(TFTP_UDP.PORT);
		File outFile = new File("output.txt");

		TFTP_UDP.output("Bound to local port " + socket.getLocalPort() + '\n');

		// Create a datagram packet, containing a maximum buffer of 256 bytes
		DatagramPacket packet = new DatagramPacket( new byte[256], 256 );

		// Receive a packet - remember by default this is a blocking operation
		socket.receive(packet);

		TFTP_UDP.output("Packet received at: " + new Date( ) + '\n');
		// Display packet information
		InetAddress remote_addr = packet.getAddress();
		TFTP_UDP.output("Sender: " + remote_addr.getHostAddress( ) + '\n');
		TFTP_UDP.output("From port: " + packet.getPort() + '\n');

		// Display packet contents, by reading from byte array
		ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData());

		// Display only up to the length of the original UDP packet
		String message = "";
		for (int i=0; i < packet.getLength(); i++)  {
			int data = bin.read();
			if (data == -1)
				break;
			else{
				//reconstruct the message being sent
				message = message + String.valueOf((char)data);
			}
		}
		
		//parse the received message
		Scanner msgScanner = new Scanner(message);
		//pull out the file name
		String fileName = msgScanner.nextLine();
		//create a new file with the correct file name
		TextFileManager receivedFile = new TextFileManager(fileName,fileName);
		String temp = "";
		//append the rest of the message to the contents of the file
		while(msgScanner.hasNext()){
			temp = msgScanner.nextLine();
			if(msgScanner.hasNext()) temp += '\n';
			receivedFile.appendFileContents(temp);
		}
		//save the received file after the entire message is appended
		receivedFile.saveFile();
		TFTP_UDP.output("File Received: " + fileName + '\n');
		msgScanner.close();
		socket.close();
		
		TFTP_UDP.output("============================\n");
		
	}
	catch (IOException e) 	{
		TFTP_UDP.output("Error - " + e + '\n');
	}
	finally { 
		// Re-enable buttons after process finishes.
		TFTP_UDP.receiveButton.setEnabled(true);
		TFTP_UDP.sendButton.setEnabled(true);
	}

   } //end of run
} //end of class definition
