/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package MulticastChatRoom;
import java.net.*;
import java.io.*;

public class MulticastReceiver extends Thread{
    private InetAddress mAddr;
    private MulticastSocket mSocket;
    public MulticastReceiver(String ipNum, int portNum){
        try{
            mAddr = InetAddress.getByName(ipNum);
            mSocket = new MulticastSocket(portNum);
            mSocket.joinGroup(mAddr);
        }
        catch(UnknownHostException uhe){
            //Do something
        }
        catch(IOException ioe){
            //Do something else
        }
    }
    public void run(){
        try{
            while (true){
				// wait for packets and write received text to the text area
                byte[] buffer = new byte[8192];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                mSocket.receive(dp);
                String receivedMsg = new String(dp.getData(), "8859_1");
                //if(MulticastChatGUI.getJTxtArea().equals(""))  MulticastChatGUI.setJTxtArea(receivedMsg.trim());
				//else MulticastChatGUI.setJTxtArea(MulticastChatGUI.getJTxtArea() + "\n" + receivedMsg.trim());

				MulticastChatGUI.setJTxtArea(
					MulticastChatGUI.getJTxtArea().equals("") ? 
					receivedMsg.trim() : 
					"\n" + receivedMsg.trim()
				);
            }
        }
        catch(IOException ioe){
            System.out.println("Error: " + ioe);
        }
    }
}
