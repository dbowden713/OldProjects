/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package MulticastChatRoom;
import java.io.*;
import java.net.*;

public class MulticastSender extends Thread {
    private MulticastSocket mSocket;
    private InetAddress mAddr;
    private byte[] buffer;
    private String message;
    private int port;
    
    public MulticastSender(String ipNum, int portNum){
        try{
			// create socket and buffer for messages
            message = "";
            port = portNum;
            mSocket = new MulticastSocket();
            mAddr = InetAddress.getByName(ipNum);
            buffer = message.getBytes();
            
        }
        catch(IOException ioe){
            //Do something
        }
    }
    
    public void sendMsg(String msg){
        message = msg;
        buffer = message.getBytes();
        try{
			// send a message to the multicast group
            mSocket.send(new DatagramPacket(buffer,buffer.length,mAddr,port));
        }
        catch(SocketException se){
            //do something
        }
        catch(IOException ioe){
            //do something
        }
    }
    
    public void run(){
        // no need for this
    }
}
