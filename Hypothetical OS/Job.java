/**Job.java
 * Job implements a basic construct for jobs
 * in the context of operating systems.
 */
public class Job{
	private int jobID, memRequested,
				segID, timeRequested,
				timeRemaining;
	private JobStatus status;
	private boolean changed;
	/**
	 * Constructor generates a new job (process) based on three integer arguments.
	@param	id		the job id
	@param	memReq	the memory space requested by the job
	@param	timeReq	the CPU burst time requested by the job
	**/
	public Job(int id, int memReq, int timeReq){
		jobID = id;
		memRequested = memReq;
		timeRequested = timeReq;
		timeRemaining = timeRequested;
		segID = -1;
		status = JobStatus.NEW;
		changed = false;
	}
	/**
	 * Default constructor generates a new job (process),
	 *  initializing every attribute of the job to -1.
	**/
	public Job(){
		this(-1,-1,-1);
	}
	/**
	@return the integer representing the id of the job.
	**/
	public int getJobID(){return jobID;}
	/**
	@return the integer representing the memory requested by the job.
	**/
	public int getMemRequested(){return memRequested;}
	/**
	@return the integer representing the memory segment id that the job currently occupies.
	**/
	public int getSegID(){return segID;}
	/**
	@return the integer representing the CPU burst time (execution time) requested by the job.
	**/
	public int getTimeRequested(){return timeRequested;}
	/**
	@return the integer representing the time remaining that the job must execute before terminating.
	**/
	public int getTimeRemaining(){return timeRemaining;}
	/**
	@return the current status that the job is in.
	@see JobStatus
	**/
	public JobStatus getStatus(){return status;}
	/**
	 * Sets the segment id of the job to a specified integer.
	 * Typically used when allocating jobs in memory.
	@param	id	the integer id of a memory segment
	**/
	public void setSegID(int id){segID = id;}
	/**
	 * Decrements the time left that a job must execute before terminating.
	**/
	public void decrementTimeRemaining(){
		if(status != JobStatus.FINISHED) {
			timeRemaining -= HOS.TIME_QTM;
		}
		if(timeRemaining<=0 && status != JobStatus.FINISHED){
			status = JobStatus.FINISHED;
		}
		changed = true;
	}
	/**
	 * Sets the status of the job to a specified enumerated value.
	@see JobStatus
	**/
	public void setStatus(JobStatus s){status = s;}
	/**
	 * Resets the timeRemaining and status of the job to original
	 * timeRequested and NEW respectively.
	@see JobStatus
	**/
	public void reset(){
		timeRemaining = timeRequested;
		segID = -1;
		status = JobStatus.NEW;
	}
	
	/**
	 * Keep track of whether this job changed in a time slice.
	 @param b  sets the flag for this job
	 */
	public void setChanged(boolean b) {
		changed = b;
	}
	
	/**
	 * Returns whether or not this job has been changed in a time slice.
	 @return the current value of changed
	 */
	public boolean getChanged() {
		return changed;
	}
	/**
	 * Overrides the default toString() method inherited from Object.
	@return a string representation of the job.
	**/
	public String toString(){
	//Outputs a string in the format:
	//jID mReq sID tReq tRem status
		String jobString = "";
		jobString = jobString + jobID + "\t" 
			+ segID + "\t"
			+ memRequested + "\t\t" 
			+ timeRemaining + "\t\t" + status;
		return jobString;
	}
}