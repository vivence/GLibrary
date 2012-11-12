package ghost.library.concurrent;

import ghost.library.concurrent.Task.State;
import ghost.library.utility.IObserverTarget;

import java.util.HashSet;
import java.util.Set;

public abstract class TaskDaemon implements Task.IObserver {
	
	private Set<Task> tasks_;

	public TaskDaemon()
	{
		// TODO Auto-generated constructor stub
	}
	
	public abstract boolean isWorking();
	public abstract void startWork();
	public abstract void stopWork();
	
	protected final synchronized void registerTask(Task task)
	{
		if (null == tasks_)
		{
			tasks_ = new HashSet<Task>();
		}
		tasks_.add(task);
		if (!isWorking())
		{
			startWork();
		}
	}
	
	protected final synchronized void unregisterTask(Task task)
	{
		if (null != tasks_)
		{
			tasks_.remove(task);
			if (tasks_.isEmpty())
			{
				tasks_ = null;
				if (isWorking())
				{
					stopWork();
				}
			}
		}
	}
	
	public final void checkTasks()
	{
		Set<Task> tasks = null;
		synchronized (this)
		{
			if (null != tasks_)
			{
				tasks = new HashSet<Task>(tasks_);
			}
		}
		if (null != tasks)
		{
			Set<Task> deadTasks = new HashSet<Task>();
			for (Task task : tasks)
			{
				if (!task.isAlive())
				{
					task.abort();
					deadTasks.add(task);
				}
			}
			synchronized (this)
			{
				if (null != tasks_)
				{
					for (Task task : deadTasks)
					{
						tasks_.remove(task);
					}
					if (tasks_.isEmpty())
					{
						tasks_ = null;
						if (isWorking())
						{
							stopWork();
						}
					}
				}
			}
		}
	}

	@Override
	public final void onStateChanged(Task task, State oldState, State newState)
	{
		// TODO Auto-generated method stub
		if (Task.State.EXECUTING == newState)
		{
			registerTask(task);
		}
		else
		{
			unregisterTask(task);
		}
	}

	@Override
	public final void onAttachTarget(IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (target instanceof Task)
		{
			if (Task.State.EXECUTING == ((Task)target).getState())
			{
				registerTask((Task)target);
			}
		}
	}

	@Override
	public final void onDetachTarget(IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (target instanceof Task)
		{
			unregisterTask((Task)target);
		}
	}

}
