package net.jueb.util4j.queue.taskQueue.impl;

import net.jueb.util4j.queue.taskQueue.Task;

public class TaskQueueUtil {

	public static Task convert(Runnable task)
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
