# TFTP UDP Client

This java-based program uses UDP (User Datagram Protocol) to send files via TFTP (Trivial File Transfer Protocol). Only one file may be sent at a time.

### Installation

This is a standalone program. Just compile with Java and run!

### Usage

This program uses a simple GUI to facilitate file transfers.
The program can be set to two modes (send and receive) via buttons. These two modes are designed to be used together, where one client is set to send and another is set to receive.

#### Receive Mode

In receive mode, the program waits indefinitely until a file is received.
- To cancel receive mode, the program must be restarted.    
- The program listens on port 9001 for incomming packets.
- All received files are saved to the same directory the client is in.  
- If a file of the same name already exists, it will be overwritten.  
- Once a file has been received, the user may once again select either Send or Receive.

#### Send Mode

In send mode, the program prompts the user to browse for a file they wish to transmit.
- After choosing a file, the user is then prompted to enter the target IP address of the receiving device.
- Once both the file and IP address are specified, the program immediately sends the file.
- When the file transfer is complete, the user may once again select either Send or Receive.

#### Text Area
Above the Send and Receive buttons is a simple text area that displays some basic information about the file transfer(s) (time stamps, packet info, etc.).

### Credits
[dbowden713](https://github.com/dbowden713)
