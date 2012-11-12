package ghost.library.concurrent;

import ghost.library.utility.IObserverTarget;
import ghost.library.utility.ObserverTargetImpl;

public class Task implements IObserverTarget {
	
	public static final long KEEP_ALIVE_TIME_MILLIS = 10 * 1000;
	
	public static interface IObserver extends ghost.library.utility.IObserver {
		public void onStateChanged(Task task, State oldState, State newState);
	}
	
	public static interface Context{
		public void interrupt();
	}
	
	public static enum State {
		NONE{
			@Override
			public boolean canReachNextState(State nextState)
			{
				// TODO Auto-generated method stub
				switch (nextState)
				{
				case AWAITING:
					return true;
				default:
					return false;
				}
			}
		},
		AWAITING{
			@Override
			public boolean canReachNextState(State nextState)
			{
				// TODO Auto-generated method stub
				switch (nextState)
				{
				case EXECUTING:
				case FINISHED:
					return true;
				default:
					return false;
				}
			}
		},
		EXECUTING{
			@Override
			public boolean canReachNextState(State nextState)
			{
				// TODO Auto-generated method stub
				switch (nextState)
				{
				case FINISHED:
					return true;
				default:
					return false;
				}
			}
		},
		FINISHED{
			@Override
			public boolean canReachNextState(State nextState)
			{
				// TODO Auto-generated method stub
				switch (nextState)
				{
				case NONE:
					return true;
				default:
					return false;
				}
			}
		};
		
		public abstract boolean canReachNextState(State nextState);
	}
	
	private State state_ = State.NONE;
	private Context context_;
	
	private boolean canceled_ = false;
	private boolean aborted_ = false;
	private long lastKeepAliveTime_;
	
	private ObserverTargetImpl observerTargetImpl_;

	public Task()
	{
		// TODO Auto-generated constructor stub
	}
	
	public synchronized State getState()
	{
		return state_;
	}
	public synchronized boolean setState(State state, Context context)
	{
		if (state_ == state)
		{
			return true;
		}
		if (state_.canReachNextState(state))
		{
			State oldState = state_;
			state_ = state;
			context_ = context;
			notifyObservers(
					"onStateChanged", 
					new NotifyMethodParam(Task.class, this), 
					new NotifyMethodParam(State.class, oldState), 
					new NotifyMethodParam(State.class, state_));
			return true;
		}
		return false;
	}
	
	public synchronized boolean isAwaiting()
	{
		return State.AWAITING == getState();
	}
	public synchronized boolean await(Context context)
	{
		return setState(State.AWAITING, context);
	}
	
	public synchronized boolean isExecuting()
	{
		return State.EXECUTING == getState();
	}
	public synchronized boolean execute(Context context)
	{
		if (setState(State.EXECUTING, context))
		{
			lastKeepAliveTime_ = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public synchronized boolean isFinished()
	{
		return State.FINISHED == getState();
	}
	public synchronized boolean finish()
	{
		return setState(State.FINISHED, null);
	}
	
	public synchronized boolean isCanceled()
	{
		return canceled_;
	}
	public synchronized boolean cancel()
	{
		if (setState(State.FINISHED, null))
		{
			canceled_ = true;
			return true;
		}
		return false;
	}
	
	public synchronized boolean isAborted()
	{
		return aborted_;
	}
	public synchronized boolean abort()
	{
		Context context = context_;
		if (setState(State.FINISHED, null))
		{
			aborted_ = true;
			if (null != context)
			{
				context.interrupt();
			}
			return true;
		}
		return false;
	}
	
	public long getKeepAliveTimeMillis()
	{
		return KEEP_ALIVE_TIME_MILLIS;
	}
	public synchronized boolean isAlive()
	{
		switch (state_)
		{
		case EXECUTING:
			return (System.currentTimeMillis()-lastKeepAliveTime_) < getKeepAliveTimeMillis();
		default:
			return true;
		}
	}
	public synchronized void keepAlive()
	{
		lastKeepAliveTime_ = System.currentTimeMillis();
	}
	
	public synchronized boolean reset()
	{
		if (State.NONE == state_)
		{
			return true;
		}
		if (setState(State.NONE, null))
		{
			canceled_ = false;
			aborted_ = false;
			return true;
		}
		return false;
	}

	@Override
	public synchronized void addObserver(ghost.library.utility.IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null == observerTargetImpl_)
		{
			observerTargetImpl_ = new ObserverTargetImpl();
		}
		observerTargetImpl_.addObserver(observer);
	}

	@Override
	public synchronized void removeObserver(ghost.library.utility.IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.removeObserver(observer);
		}
	}

	@Override
	public synchronized void clearObserver()
	{
		// TODO Auto-generated method stub
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.clearObserver();
		}
	}
	
	@Override
	public void notifyObservers(String methodName,
			NotifyMethodParam... parameters)
	{
		// TODO Auto-generated method stub
		ObserverTargetImpl observerTargetImpl = null;
		synchronized (this)
		{
			if (null != observerTargetImpl_)
			{
				observerTargetImpl_.beginSafeNotify();
				observerTargetImpl = observerTargetImpl_;
			}
		}
		if (null != observerTargetImpl)
		{
			observerTargetImpl.safeNotify(methodName, parameters);
		}
		synchronized (this)
		{
			if (null != observerTargetImpl_ && observerTargetImpl == observerTargetImpl_)
			{
				observerTargetImpl_.endSafeNotify();
			}
		}
	}

}
