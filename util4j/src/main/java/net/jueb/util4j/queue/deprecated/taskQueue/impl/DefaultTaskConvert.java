package net.jueb.util4j.queue.deprecated.taskQueue.impl;

import net.jueb.util4j.queue.deprecated.taskQueue.Task;
import net.jueb.util4j.queue.deprecated.taskQueue.TaskConvert;

final public class DefaultTaskConvert implements TaskConvert{

	public Task convert(Runnable task)
	{
		if(task instanceof Task)
		{
			return (Task)task;
		}else
		{
			return new RunnableTaskAdapter(task);
		}
	}
}
