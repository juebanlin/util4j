package net.jueb.util4j.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzTaskAdatper implements QuartzTask {

	private final String name;
	private final String group;
	private final String crond;
	private final Runnable runnable;
	public QuartzTaskAdatper(String name, String group, String crond, Runnable runnable) {
		super();
		this.name = name;
		this.group = group;
		this.crond = crond;
		this.runnable = runnable;
	}
	public String getName() {
		return name;
	}
	public String getGroup() {
		return group;
	}
	public String getCrond() {
		return crond;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		runnable.run();
	}
}
