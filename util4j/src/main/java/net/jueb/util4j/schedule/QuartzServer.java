package net.jueb.util4j.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzServer {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private Scheduler sched;

	public void start() throws Exception
	{
		SchedulerFactory sf = new StdSchedulerFactory();
		sched = sf.getScheduler();
		sched.start();
	}
	
	public boolean isStarted() throws SchedulerException {
		return sched!=null && sched.isStarted();
	}
	
	public void reStart()throws Exception
	{
		synchronized (log) {
			if (sched != null && sched.isStarted()) {
				sched.clear();
				sched.shutdown();
			}
			start();
		}
	}

	public void addTask(QuartzTask task) throws Exception {
		JobDetail jobDetail = newJob(task.getClass()).withIdentity(task.getName(), task.getGroup()).build();
		Trigger trigger = newTrigger().withIdentity(task.getName(), task.getGroup())
				.withSchedule(cronSchedule(task.getCrond())).build();
		addTask(jobDetail, trigger);
	}
	
	public void addTask(JobDetail jobDetail,Trigger trigger) throws Exception {
		sched.scheduleJob(jobDetail, trigger);
	}
}
