import java.net.*;
import java.io.*;
import java.util.*;

public class UDPSender implements Runnable{
	//use localhost to experiment on a standalone computer
	private String hostname;
	private String message;
	
	public UDPSender(String host, String msg){
		hostname = host;
		message = msg;
	}
	
	public void run () {
		try {
			// Create a datagram socket, look for the first available port
			DatagramSocket socket = new DatagramSocket();

			TFTP_UDP.output("Using local port: " + socket.getLocalPort() + '\n');
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			PrintStream pOut = new PrintStream(bOut);
			pOut.print(message);
			//convert printstream to byte array
			byte [ ] bArray = bOut.toByteArray();
			// Create a datagram packet, containing a maximum buffer of 256 bytes
			DatagramPacket packet=new DatagramPacket( bArray, bArray.length );

			TFTP_UDP.output("Looking for hostname " + hostname + '\n');
			//get the InetAddress object
			InetAddress remote_addr = InetAddress.getByName(hostname);
			//check its IP number
			TFTP_UDP.output("Hostname has IP address = " + remote_addr.getHostAddress() + '\n');
			//configure the DataGramPacket
			packet.setAddress(remote_addr);
			packet.setPort(TFTP_UDP.PORT);
			//send the packet
			socket.send(packet);
			TFTP_UDP.output("Packet sent at: " + new Date() + '\n');

			// Display packet information
			TFTP_UDP.output("Sent by  : " + InetAddress.getLocalHost() + '\n' );
			TFTP_UDP.output("Send from: " + packet.getPort() + '\n');

			
			TFTP_UDP.output("=======================\n");
			
		}
		catch (UnknownHostException ue){
			TFTP_UDP.output("Unknown host "+hostname + '\n');
		}
		catch (IOException e)	{
			TFTP_UDP.output("Error - " + e + '\n');
		}
		finally { 
			// Re-enable buttons after process finishes.
			TFTP_UDP.receiveButton.setEnabled(true);
			TFTP_UDP.sendButton.setEnabled(true);
		}

	}//end of run
}//end of class definition
