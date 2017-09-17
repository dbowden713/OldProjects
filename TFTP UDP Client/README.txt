Run TFTP_UDP.java.
You must provide your own simple text file (.txt) to send and receive.

GUI Help

Receive Button:
The Receive button sets the program in Receive mode.
In receive mode, the program waits until a file is received.
Receive mode cannot be canceled to receiving a file unless the program is closed and restarted.
Once a file has been received, the user may once again select either Send or Receive.
All received files are saved to the same directory where TFTP_UDP.java is saved.

Send Button:
The Send button prompts the user to browse for a file they wish to transmit.
After choosing a file, the user is then prompted to enter the target IP address of the receiving device.
Once both the file and IP address are specified, the program immediately sends the file.
When the file transfer is complete, the user may once again select either Send or Receive.

Text Area:
Above the Send and Receive buttons is a simple text area that displays some basic information about the file transfer(s).