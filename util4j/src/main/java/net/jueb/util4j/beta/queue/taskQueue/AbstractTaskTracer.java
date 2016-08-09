package net.jueb.util4j.beta.queue.taskQueue;

public abstract class AbstractTaskTracer implements TaskTracer{

	private final QueueTask task;
	private long seq;
	private long appendTime;
	private long runStartTime;
	private long runEndTime;
	
	public AbstractTaskTracer(QueueTask task) {
		super();
		this.task = task;
	}

	public long getSeq() {
		return seq;
	}

	protected void setSeq(long seq) {
		this.seq = seq;
	}

	public long getAppendTime() {
		return appendTime;
	}

	protected void setAppendTime(long appendTime) {
		this.appendTime = appendTime;
	}

	public long getRunStartTime() {
		return runStartTime;
	}

	protected void setRunStartTime(long runStartTime) {
		this.runStartTime = runStartTime;
	}

	public long getRunEndTime() {
		return runEndTime;
	}

	protected void setRunEndTime(long runEndTime) {
		this.runEndTime = runEndTime;
	}

	public final QueueTask getTask() {
		return task;
	}
}
