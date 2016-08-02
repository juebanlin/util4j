package net.jueb.util4j.queue.taskQueue.impl;

import net.jueb.util4j.queue.taskQueue.AbstractTaskTracer;
import net.jueb.util4j.queue.taskQueue.QueueTask;

class DefaultTaskTracer extends AbstractTaskTracer{

	protected DefaultTaskTracer(QueueTask task) {
		super(task);
	}
	
	@Override
	protected void setSeq(long seq) {
		super.setSeq(seq);
	}

	@Override
	protected void setAppendTime(long appendTime) {
		super.setAppendTime(appendTime);
	}

	@Override
	protected void setRunStartTime(long runStartTime) {
		super.setRunStartTime(runStartTime);
	}

	@Override
	protected void setRunEndTime(long runEndTime) {
		super.setRunEndTime(runEndTime);
	}

	@Override
	public String toString() {
		return "DefaultTaskTracer [getSeq()=" + getSeq() + ", getTask()=" + getTask() + "]";
	}
}
