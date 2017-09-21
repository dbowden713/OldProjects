/**HOS.java
 * Core class.
 * Simulates the methodology that a hypothetical operating system
 * schedules jobs from the disk through to execution.
 */
 
 /*
Progression of Job States Throughout Simulation:
	A job is NEW when it has just been created and still exists ONLY in the job queue (not in memory).
	A job is READY when it has been allocated to memory but is not RUNNING.
	A job is RUNNING when it is on the CPU (decrementing the burst time by TIME_QTM).
	A job is WAITING when it is not in memory and is no longer NEW.
		(This case arises when there are more jobs than memory can hold.)
	A job is FINISHED when its time remaining is 0.
*/
 
 import java.io.*;
 import java.util.*;
 
public class HOS{
	private JobQueue jQueue;		//Represents the job queue of the OS
	private Memory memory;			//Represents the physical memory of the system
	private Vector<Job> rdyQueue;	//Represents subset READY jobs in jQueue
	private PrintWriter output;		//Used to write simulation feedback to text file
	private int finished = 0;		//Counts how many jobs have finished.
	private int waiting = 0;		//Counts how many jobs are waiting.
	//Specify several relevant fields.
	//Allows for highly customizable simulation conditions
	public static final int MEM_MAX = 64;			//Upper bound of physical memory segment size
	public static final int MEM_MIN = 16;			//Lower bound of physical memory segment size
	public static final int TIME_MAX = 15;			//Upper bound of job time requests
	public static final int TIME_MIN = 5;			//Lower bound of job time requests
	public static final int NUM_JOBS = 20;			//Max number of jobs
	public static final int SIM_DURATION = 30;		//Specifies how many time units the simulation will run
	public static final int PARALLEL_PROCS = 4;		//Specifies how many processor cores are in the system
													//which translates into how many concurrent processes execute
	public static final int TIME_QTM = 1;			//Specifies the time quantum used in Round Robin
	
	/**
	 * Core constructor of HOS. Initializes and starts simulation.
	**/
	public HOS() {
		jQueue = new JobQueue();
		populateJobQueue();
		startSim();
	}
	
	/**
	 * Populates the job queue with jobs,
	 * where each job has randomized time and memory requests.
	 @see JobQueue
	**/
	private void populateJobQueue(){
		int memReq;
		int timeReq;
		try{
			//Creates the values for memory and time requests
			for(int i=0; i<NUM_JOBS; i++){
				memReq = MEM_MIN + (int)((MEM_MAX-MEM_MIN) * Math.random());
				timeReq = TIME_MIN + (int)((TIME_MAX-TIME_MIN) * Math.random());
				//Try to add the job
				jQueue.addJob(new Job(i, memReq, timeReq));
			}
		}
		//Catch if the job queue is full
		catch(JobQueueFullException JQFE){
			System.out.println("Job queue is full.");
		}
	}
	
	/**
	 * Runs the FIFO, First Fit case of the simulation.
	 @see JobStatus
	 @see JobQueue
	 @see Memory
	**/
	private void FIFO_FF(){
		memory = new Memory();
		//Initialize log file.
		try {
			output = new PrintWriter(new File("FIFO_FF.txt"));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		//Output appropriate headers to log file.
		System.out.println("Job Selection: FIFO\nMemory Allocation: FF");
		output.println("Job Selection: FIFO\nMemory Allocation: FF");
		displayHeader();
		//At t=0, fill memory according to this case's
		//job selection and memory allocation methodologies.
		for(int i=0; i<NUM_JOBS; i++){
			Job j = null;
			//Attempt to assign job to memory
			try{
				j = jQueue.nextFIFO();
				memory.mallocFF(j);
				//If allocation was successful, then the job is READY
				j.setStatus(JobStatus.READY);
				j.setChanged(true);
			}
			//If memory is already full, the job must wait.
			catch(NoFreeMemSegmentsException NFMSE){
				j.setStatus(JobStatus.WAITING);
				j.setChanged(true);
			}
			//Should never happen: see NoValidNextJobException's 
			catch(NoValidNextJobException NVNJE){
				System.out.println("FIFO_FF() threw this exception.");
				output.println("FIFO_FF() threw this exception.");
			}
		}
		//Display the t=0 slice first and footer.
		displaySlice(0);
		displayFooter();
		//Fill ready queue with ready jobs from job queue
		rdyQueue = new Vector<Job>(Memory.NUM_SEG);
		for(int i=0; i<Memory.NUM_SEG; i++){
			for(int j=0; j<NUM_JOBS; j++){
				if(jQueue.getJobAt(j).getSegID()==i){
					rdyQueue.addElement(jQueue.getJobAt(j));
				}
			}
		}
		//Runs simulation beginning at t=1 through to end of simulation.
		int count = -1;
		for(int t=1; t<SIM_DURATION; t+=TIME_QTM){
			//Case 1: Handle decrementing jobs according to PARALLEL_PROCS (Round Robin)
			for(int i=0; i<PARALLEL_PROCS; i++){
				if(rdyQueue.size() != 0) {
					//"++count%rdyQueue.size()" changes the index within rdyQueue in round robin fashion
					rdyQueue.elementAt(++count%rdyQueue.size()).decrementTimeRemaining();
				}
			}
			//display time slice with header
			displayHeader();
			displaySlice(t);
			//Case 2: Handle when jobs finish
				//a) deallocating memory
				//b) remove job from rdyQueue
				//c) try to allocate next valid job
				//d) if allocation succeeds, add job to rdyQueue
			for(int i=0; i<rdyQueue.size(); i++){
				if(rdyQueue.elementAt(i).getTimeRemaining()<=0){
					//a) deallocating memory
					try{
						memory.dealloc(rdyQueue.elementAt(i));
					}
					//Should never happen: The job should ALWAYS be in memory if its in rdyQueue
					catch(JobNotInMemoryException JNIME){
						System.out.println("Very Bad Day.");
						output.println("Very Bad Day.");
					}
					//b) remove job from rdyQueue
					rdyQueue.removeElementAt(i);
					//c) try to allocate next valid job
					for(int k=0; k<NUM_JOBS; k++){
						Job j = null;
						try{
							j = jQueue.nextFIFO();
							memory.mallocFF(j);
							j.setStatus(JobStatus.READY);
							j.setChanged(true);
							//d) if allocation succeeds, add job to rdyQueue
							rdyQueue.insertElementAt(j,i);
						}
						//Job should continue WAITING if there is no room in memory for it.
						catch(NoFreeMemSegmentsException NFMSE){
							j.setStatus(JobStatus.WAITING);
						}
						//Do nothing if there are no valid jobs to allocate
						//i.e. just continue the simulation
						catch(NoValidNextJobException NVNJE){
							//System.out.println("No valid jobs available to allocate.");
						}
					}
				}
			}
			//This is the final print to the console and log file.
			displayFooter();
		}
		System.out.println("See FIFO_FF.txt for text log.");
		//Reset the job queue for the next case of the simulation.
		jQueue.reset();
		output.close();
	}
	
	/**
	 * Runs the FIFO, Best Fit case of the simulation.
	 @see JobStatus
	 @see JobQueue
	 @see Memory
	**/
	private void FIFO_BF(){
		//See the comments for FIFO_FF since the code is identical to FIFO_FF
		//The only differences in code between this case and FIFO_FF are the method calls:
		//e.g. i) here we use mallocBF() instead of mallocFF()
		memory = new Memory();
		try {
			output = new PrintWriter(new File("FIFO_BF.txt"));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Job Selection: FIFO\nMemory Allocation: BF");
		output.println("Job Selection: FIFO\nMemory Allocation: BF");
		displayHeader();
		for(int i=0; i<NUM_JOBS; i++){
			Job j = null;
			try{
				j = jQueue.nextFIFO();
				//i)
				memory.mallocBF(j);
				j.setStatus(JobStatus.READY);
				j.setChanged(true);
			}
			catch(NoFreeMemSegmentsException NFMSE){
				j.setStatus(JobStatus.WAITING);
				j.setChanged(true);
			}
			catch(NoValidNextJobException NVNJE){
				System.out.println("FIFO_BF() threw this exception.");
				output.println("FIFO_BF() threw this exception.");
			}
		}
		displaySlice(0);
		displayFooter();
		rdyQueue = new Vector<Job>(Memory.NUM_SEG);
		for(int i=0; i<Memory.NUM_SEG; i++){
			for(int j=0; j<NUM_JOBS; j++){
				if(jQueue.getJobAt(j).getSegID()==i){
					rdyQueue.addElement(jQueue.getJobAt(j));
				}
			}
		}
		int count = -1;
		for(int t=1; t<SIM_DURATION; t+=TIME_QTM){
			//Case 1: decrementing jobs according to PARALLEL_PROCS
			for(int i=0; i<PARALLEL_PROCS; i++){
				if(rdyQueue.size() != 0) {
					rdyQueue.elementAt(++count%rdyQueue.size()).decrementTimeRemaining();
				}
			}
			//display time slice
			displayHeader();
			displaySlice(t);
			//Case 2: when job finishes
				//deallocating memory
				//remove job from rdyQueue
				//try to allocate next valid job
				//if allocation succeeds, add job to rdyQueue
			for(int i=0; i<rdyQueue.size(); i++){
				if(rdyQueue.elementAt(i).getTimeRemaining()<=0){
					try{
						memory.dealloc(rdyQueue.elementAt(i));
					}
					catch(JobNotInMemoryException JNIME){
						System.out.println("Very Bad Day.");
						output.println("Very Bad Day.");
					}
					rdyQueue.removeElementAt(i);
					for(int k=0; k<NUM_JOBS; k++){
						Job j = null;
						try{
							j = jQueue.nextFIFO();
							//i)
							memory.mallocBF(j);
							j.setStatus(JobStatus.READY);
							j.setChanged(true);
							rdyQueue.insertElementAt(j,i);
						}
						catch(NoFreeMemSegmentsException NFMSE){
							j.setStatus(JobStatus.WAITING);
						}
						catch(NoValidNextJobException NVNJE){
							//System.out.println("No valid jobs available to allocate.");
						}
					}
				}
			}
			displayFooter();
		}
		System.out.println("See FIFO_BF.txt for text log.");
		//Reset the job queue for the next case of the simulation.
		jQueue.reset();
		output.close();
	}
	
	/**
	 * Runs the SJF, Best Fit case of the simulation.
	 @see JobStatus
	 @see JobQueue
	 @see Memory
	**/
	private void SJF_BF(){
		//See the comments for FIFO_FF since the code is identical to FIFO_FF
		//The only differences in code between this case and FIFO_FF are the method calls:
		//e.g. i)here we use mallocBF() instead of mallocFF()
		//	  ii)and we use nextSJF() instead of nextFIFO()
		memory = new Memory();
		try {
			output = new PrintWriter(new File("SJF_BF.txt"));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Job Selection: SJF\nMemory Allocation: BF");
		output.println("Job Selection: SJF\nMemory Allocation: BF");
		displayHeader();
		for(int i=0; i<NUM_JOBS; i++){
			Job j = null;
			try{
				//ii)
				j = jQueue.nextSJF();
				//i)
				memory.mallocBF(j);
				j.setStatus(JobStatus.READY);
				j.setChanged(true);
			}
			catch(NoFreeMemSegmentsException NFMSE){
				j.setStatus(JobStatus.WAITING);
				j.setChanged(true);
			}
			catch(NoValidNextJobException NVNJE){
				System.out.println("SJF_BF() threw this exception.");
				output.println("SJF_BF() threw this exception.");
			}
		}
		displaySlice(0);
		displayFooter();
		rdyQueue = new Vector<Job>(Memory.NUM_SEG);
		for(int i=0; i<Memory.NUM_SEG; i++){
			for(int j=0; j<NUM_JOBS; j++){
				if(jQueue.getJobAt(j).getSegID()==i){
					rdyQueue.addElement(jQueue.getJobAt(j));
				}
			}
		}
		int count = -1;
		for(int t=1; t<SIM_DURATION; t+=TIME_QTM){
			//Case 1: decrementing jobs according to PARALLEL_PROCS
			for(int i=0; i<PARALLEL_PROCS; i++){
				if(rdyQueue.size() != 0) {
					rdyQueue.elementAt(++count%rdyQueue.size()).decrementTimeRemaining();
				}
			}
			//display time slice
			displayHeader();
			displaySlice(t);
			//Case 2: when job finishes
				//deallocating memory
				//remove job from rdyQueue
				//try to allocate next valid job
				//if allocation succeeds, add job to rdyQueue
			for(int i=0; i<rdyQueue.size(); i++){
				if(rdyQueue.elementAt(i).getTimeRemaining()<=0){
					try{
						memory.dealloc(rdyQueue.elementAt(i));
					}
					catch(JobNotInMemoryException JNIME){
						System.out.println("Very Bad Day.");
						output.println("Very Bad Day.");
					}
					rdyQueue.removeElementAt(i);
					for(int k=0; k<NUM_JOBS; k++){
						Job j = null;
						try{
							//ii)
							j = jQueue.nextSJF();
							//i)
							memory.mallocBF(j);
							j.setStatus(JobStatus.READY);
							j.setChanged(true);
							rdyQueue.insertElementAt(j,i);
						}
						catch(NoFreeMemSegmentsException NFMSE){
							j.setStatus(JobStatus.WAITING);
						}
						catch(NoValidNextJobException NVNJE){
							//System.out.println("No valid jobs available to allocate.");
						}
					}
				}
			}
			displayFooter();
		}
		System.out.println("See SJF_BF.txt for text log.");
		//Reset the job queue for the next case of the simulation.
		jQueue.reset();
		output.close();
	}
	
	/**
	 * Starts the entire simulation, includes all cases.
	**/
	private void startSim(){
		System.out.println("===================================================");
		FIFO_FF();
		System.out.println("===================================================");
		FIFO_BF();
		System.out.println("===================================================");
		SJF_BF();
		System.out.println("===================================================");
	}
	
	/**
	 * Displays simulation feedback in column format as per documentation\project.doc.
	 * Also tracks waiting and finished jobs.
	**/
	private void displaySlice(int t){
		waiting = 0;
		finished = 0;
		//For each job, display its associated information
		for(int i=0; i<NUM_JOBS; i++){
			if(jQueue.getJobAt(i).getChanged()) {
				System.out.println(t+"\t"+jQueue.getJobAt(i));
				output.println(t+"\t"+jQueue.getJobAt(i));
			}
			//Update waiting job count
			if(jQueue.getJobAt(i).getStatus() == JobStatus.WAITING) {
				waiting++;
			}
			//Update finished job count
			if(jQueue.getJobAt(i).getStatus() == JobStatus.FINISHED) {
				finished++;
			}
		}
		jQueue.resetChanged();
	}
	
	/**
	 * Sets/Displays column names for each slice.
	**/
	private void displayHeader(){
		System.out.println("TIME\tID\tSEGMENT\tMEM REQUEST\tTIME REMAIN\tMESSAGES\n");
		output.println("TIME\tID\tSEGMENT\tMEM REQUEST\tTIME REMAIN\tMESSAGES\n");
	}
	
	/**
	 * Displays current wasted space in memory, waiting jobs, and finished jobs.
	**/
	private void displayFooter() {
		System.out.println("Wasted Space: " + memory.getWastedSpace());
		System.out.println("Waiting Jobs: " + waiting);
		System.out.println("Finished Jobs: " + finished);
		System.out.println("---------------------------------------------------");
		output.println("Wasted Space: " + memory.getWastedSpace());
		output.println("Waiting Jobs: " + waiting);
		output.println("Finished Jobs: " + finished);
		output.println("---------------------------------------------------");
	}
	
	/**
	 * Entry point of the Hypothetical Operating System simulation.
	**/
	public static void main(String [] args){
		new HOS();
	}
}