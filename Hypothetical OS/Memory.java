/**Memory.java
 * Memory provides a basic implementation of physical memory
 * in the context of operating systems.
 */
 public class Memory {
	//  Constant fields
	public static final int 	NUM_SEG = 7;
	public static final int[]	SEG_SIZES = { 32, 48, 24, 16, 64, 32, 24 };
	
	//  Private fields
	private MemSeg[] memory;		//  The working array which represents the memory
	private int wastedSpace;		//  Keeps track of wasted segments (total in MB) in memory
	
	/**
	 * Sole constructor.  For invocation by superclass, explicitly.
	 * Memory will be created according to the constants specified in the source.
	 
	@see MemSeg
	 *
	 
	@author Timothy Daniel Bowden
	 */
	public Memory() {
		//  Create array with number of memory segments
		memory = new MemSeg[NUM_SEG];
		
		//  Populate array according to constant specification
		//  Add up memory sizes for wastedSpace (all memory is "wasted" initially)
		for( int i = 0; i < memory.length; i++ ) {
			memory[i] = new MemSeg(i, SEG_SIZES[i]);
			wastedSpace += SEG_SIZES[i];
		}
	}
	
	/**
	 * Tries to allocate a given job to memory according to the "First-Fit" allocation strategy.
	 
	@param	j		the job to be allocated
	 *
	 
	@throws NoFreeMemSegmentsException There is no free segment to allocate in memory.
	 *
	 
	@see	Job
	 *
	 
	@author Timothy Daniel Bowden
	 */
	public void mallocFF(Job j) throws NoFreeMemSegmentsException{
		//  Try to find a valid memory segment and place job
		for( int i = 0; i < memory.length; i++ ) {
			if(memory[i].getSegSize() >= j.getMemRequested() && !memory[i].isInUse()) {
				memory[i].setUse(true);
				j.setSegID(i);
				wastedSpace -= memory[i].getSegSize();
				return;
			}
		}
		
		//  No valid memory segment
		throw new NoFreeMemSegmentsException();
	}
	
	/**
	 * Tries to allocate a given job to memory according to the "Best-Fit" allocation strategy.
	 
	@param	j		the job to be allocated
	 *
	 
	@throws NoFreeMemSegmentsException There is no free segment to allocate in memory.
	 *
	 
	@see	Job
	 *
	 
	@author Timothy Daniel Bowden
	 */
	public void mallocBF(Job j) throws NoFreeMemSegmentsException{
		int id = -1;  				//  Holds ID of best fit segment
		int diff = 0;				//  Hold current size difference between job size and best fit segment size
		
		//  Find smallest matching segment
		for( int i = 0; i < memory.length; i++ ) {
			if(id == -1 && j.getMemRequested() < memory[i].getSegSize() && !memory[i].isInUse()){
				id = memory[i].getSegID();
				diff = memory[i].getSegSize() - j.getMemRequested();
			}
			if(j.getMemRequested() < memory[i].getSegSize() && memory[i].getSegSize() - j.getMemRequested() < diff && !memory[i].isInUse()) {
				id = memory[i].getSegID();
				diff = memory[i].getSegSize() - j.getMemRequested();
			}
		}
		
		//  A valid memory segment was found
		if(id >= 0) {
			memory[id].setUse(true);
			j.setSegID(id);
			wastedSpace -= memory[id].getSegSize();
			return;
		}
		//  No valid segment
		throw new NoFreeMemSegmentsException();
	}
	
	/**
	 * Deallocates a given job from main memory.
	 
	@param	j		the job to be deallocated
	 *
	 
	@throws JobNotInMemoryException The designated job does not exist in memory.
	 *
	 
	@see	Job
	 *
	 
	@author Timothy Daniel Bowden
	 */
	public void dealloc(Job j) throws JobNotInMemoryException{
		//  Try to find matching job and remove it from memory.
		for( int i = 0; i < memory.length; i++) {
			if(memory[i].getSegID() == j.getSegID()) {
				memory[i].setUse(false);
				j.setSegID(-1);
				wastedSpace += memory[i].getSegSize();
				return;
			}
		}
		
		//  If the job wasn't found in memory
		throw new JobNotInMemoryException();
	}
	
	/**
	 * Returns the current total of unused segment sizes.
	 
	 @return  total size of unused space (in MB)
	  *
	 
	 @author Timothy Daniel Bowden
	 */
	public int getWastedSpace() {
		return wastedSpace;
	}
	
	public String toString() {
		String s;
		s = "Current Memory:" + "\n" +
			"ID" + "\t\t" + "Size" + "\t\t" + "Use" + "\n" +
		    "=============================================" + "\n";
		for(int i = 0; i < NUM_SEG; i++) {
			s += memory[i].getSegID() + "\t\t" + memory[i].getSegSize() + "\t\t" + memory[i].isInUse() + "\n";
		}
		
		s += "Wasted Space: " + wastedSpace;
		
		return s;
	}
}