/**MemSeg.java
 * MemSeg implements a basic construct for memory segments
 * in the context of operating systems.
 */
public class MemSeg {
	//  Private Fields
	private boolean	inUse;		//  Memory allocated for a job
	private int		segID;		//  ID of this memory segment
	private int		segSize;	//  Size of this memory segment (MB)
	
	/**
	 * Sole constructor.  For invocation by superclass, typicaly explicit.
	 * Segment inUse will be set to false by default.
	 
	@param  id	the ID number this segment will have
	 *
	 
	@param  size	the memory size (in MB) this segment should have
	 *
	 
	@author Timothy Daniel Bowden
	 */
	public MemSeg(int id, int size) {
		inUse 	= false;
		segID 	= id;
		segSize = size;
	}
	
	/**
	 * Returns whether or not the memory segment has a job stored in it.
	 
	 @return  true if segment is in use.  Otherwise, false.
	  *
	 
	 @author Timothy Daniel Bowden
	 */
	public boolean isInUse() { return inUse; }
	
	/**
	 * Returns the current memory segment ID number.
	 
	 @return  segID
	  *
	  
	 @author Timothy Daniel Bowden
	 */
	public int getSegID() { return segID; }
	
	/**
	 * Returns the current memory segment size (in MB)
	 
	 @return  memory segment size
	  *
	  
	 @author Timothy Daniel Bowden
	 */
	public int getSegSize() { return segSize; }
	
	/**
	 * This is used to set the memory segment to be in use or not.
	 
	 @param  b	whether or not the memory is being used.
	  *
	 
	 @author Timothy Daniel Bowden
	 */
	public void setUse(boolean b) {
		inUse = b;
	}
}