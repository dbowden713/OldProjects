/*
 *	TextFileManager
 *	Loads and saves simple text files.
 */
import java.io.*;
import java.util.*;
public class TextFileManager{
	private Scanner fileScanner;
	private PrintWriter fileWriter;
	private String absFileName, relFileName, fileContents;
	
	//Constructor takes String file names for both input and output files.
	public TextFileManager(String abs, String rel){
		absFileName = abs;
		relFileName = rel;
		clearFileContents();
	}
	
	//Load a file based on fileName, overwrites filecontents with what is currently in the file.
	public void loadFile(){
		try{
			clearFileContents();
			fileContents = relFileName;
			fileScanner = new Scanner(new File(absFileName));
			while(fileScanner.hasNext()){
				fileContents = fileContents + "\n" + fileScanner.nextLine();
			}
			fileScanner.close();
		}
		catch(FileNotFoundException fnfe){System.out.println("Could not find:\n" + relFileName);}
	}
	
	//Save a file based on fileName and current filecontents.
	public void saveFile(){
		try{
			fileWriter = new PrintWriter(relFileName);
			fileWriter.print(fileContents);
			fileWriter.close();
		}
		catch(IOException ioe){System.out.println(ioe);}
	}
	
	//Getters and setters for all String attributes of this class.
	public void setFileContents(String newContents){fileContents = newContents;}
	public void appendFileContents(String extraContents){fileContents = fileContents + extraContents;}
	public void clearFileContents(){fileContents = "";}
	public String getFileName(){return relFileName;}
	public String getFileContents(){return fileContents;}
}