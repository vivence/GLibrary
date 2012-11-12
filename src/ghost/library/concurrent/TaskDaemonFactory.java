package ghost.library.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskDaemonFactory {
	
	private static class TaskDaemonWorkOnThread extends TaskDaemon implements Runnable {

		private long workCycleMillis_;
		
		private ExecutorService service_;
		private boolean running_ = false;
		
		public TaskDaemonWorkOnThread(long workCycleMillis)
		{
			// TODO Auto-generated constructor stub
			workCycleMillis_ = workCycleMillis;
		}
		
		@Override
		public synchronized boolean isWorking()
		{
			// TODO Auto-generated method stub
			return running_;
		}

		@Override
		public synchronized void startWork()
		{
			// TODO Auto-generated method stub
			if (!running_)
			{
				running_ = true;
				if (null == service_)
				{
					service_ = Executors.newSingleThreadExecutor();
				}
				service_.execute(this);
			}
		}

		@Override
		public synchronized void stopWork()
		{
			// TODO Auto-generated method stub
			if (running_)
			{
				running_ = false;
				if (null != service_)
				{
					service_.shutdownNow();
				}
			}
		}

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			boolean exit = false;
			while (!exit)
			{
				checkTasks();
				try
				{
					Thread.sleep(workCycleMillis_);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
				}
				synchronized (this)
				{
					exit = !running_;
				}
			}
		}
		
	}

	private TaskDaemonFactory()
	{
		// TODO Auto-generated constructor stub
		
	}
	
	public static TaskDaemon newTaskDaemonWorkOnThread(long workCycleMillis)
	{
		return new TaskDaemonWorkOnThread(workCycleMillis);
	}

}
