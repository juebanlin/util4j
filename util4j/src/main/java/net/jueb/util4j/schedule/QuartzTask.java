package net.jueb.util4j.schedule;

import org.quartz.Job;

public interface QuartzTask extends Job {

	public abstract String getName();

	public abstract String getGroup();

	public abstract String getCrond();
}
