/**JobQueue.java
 * JobQueue implements all necessary logic associated
 * with a job queue that an operating system uses to 
 * track and schedule jobs for execution.
 * This class functions as a shell around a Vector<Job>.
 */
import java.util.Vector;
public class JobQueue{
	private Vector<Job> jQueue;
	/**
	 * The default constructor creates a new, empty job queue to a capacity
	 * of NUM_JOBS field in HOS.java.
	@see HOS
	**/
	public JobQueue(){
		jQueue = new Vector<Job>(HOS.NUM_JOBS);
	}
	/**
	@return a reference to the next job in FIFO (first-in-first-out) order
	@throws	NoValidNextJobException	There are no jobs whose status is NEW in the job queue.
	@see JobStatus
	**/
	public Job nextFIFO() throws NoValidNextJobException{
		Job nextJob = null;
		//Search for NEW jobs to return
		for(Job j : jQueue){
			if(nextJob == null && j.getStatus() == JobStatus.NEW){
				nextJob = j;
			}
		}
		//If no NEW jobs are available
			//Search for WAITING jobs to return
		if(nextJob!=null){return nextJob;}
		for(Job j : jQueue){
			if(nextJob == null && j.getStatus() == JobStatus.WAITING){
				nextJob = j;
			}
		}
		//If there are no NEW or WAITING jobs, throw an exception
		if(nextJob==null){throw new NoValidNextJobException();}
		return nextJob;
	}
	/**
	@return a reference to the next job in SJF (shortest job first) order
	@throws	NoValidNextJobException	There are no jobs whose status is NEW in the job queue.
	@see JobStatus
	**/
	public Job nextSJF() throws NoValidNextJobException{
		Job shortestJob = null; //Tracks the current shortest job on each iteration
		//Search for the next shortest NEW job
		for(int i = 0; i<jQueue.size(); i++){
			if(jQueue.elementAt(i).getStatus()==JobStatus.NEW){
				if(shortestJob!=null 
					&& shortestJob.getTimeRequested() > jQueue.elementAt(i).getTimeRequested()
					&& shortestJob.getJobID() != jQueue.elementAt(i).getJobID()){
					//Update shortest job
					shortestJob = jQueue.elementAt(i);
				}
				if(shortestJob==null){
					shortestJob = jQueue.elementAt(i);
				}
			}
		}
		//Search for the next shortest WAITING job if no NEW jobs were set
		if(shortestJob!=null){return shortestJob;}
		for(int i = 0; i<jQueue.size(); i++){
			if(jQueue.elementAt(i).getStatus()==JobStatus.WAITING){
				if(shortestJob!=null 
					&& shortestJob.getTimeRequested() > jQueue.elementAt(i).getTimeRequested()
					&& shortestJob.getJobID() != jQueue.elementAt(i).getJobID()){
					
					shortestJob = jQueue.elementAt(i);
				}
				if(shortestJob==null){
					shortestJob = jQueue.elementAt(i);
				}
			}
		}
		//If no shortest job was ever set, then throw an exception
		if(shortestJob==null){throw new NoValidNextJobException();}
		return shortestJob;
	}
	/**
	 * Adds new jobs on the queue (appends them to the end of the vector).
	@param	j	the job to be added to the job queue
	@throws	JobQueueFullException	The job queue has been filled to capacity of NUM_JOBS.
	@see HOS
	**/
	public void addJob(Job j) throws JobQueueFullException{
		if(jQueue.capacity() == HOS.NUM_JOBS && jQueue.size() < jQueue.capacity()){
			jQueue.addElement(j);
		}
		else{throw new JobQueueFullException();}
	}
	/**
	 * Resets the status of all jobs in the job queue.
	@see Job
	**/
	public void reset(){
		for(Job j : jQueue){
			j.reset();
		}
	}
	
	/**
	 * Resets the changed flag for all jobs in the job queue.
	@see Job
	**/
	public void resetChanged(){
		for(Job j : jQueue){
			j.setChanged(false);
		}
	}
	/**
	 * Returns job at index i.
	**/
	public Job getJobAt(int i){
		return jQueue.elementAt(i);
	}
	/**
	@return string represetation of each job separated by new lines
	**/
	public String toString(){
		String jQueueString = "";
		for(Job j : jQueue){
			jQueueString = jQueueString + j;
		}
		return jQueueString;
	}
}