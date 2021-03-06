package ghost.library.concurrent;

import ghost.library.utility.IObserverTarget;
import ghost.library.utility.ObserverTargetImpl;

public class Task implements Runnable, IObserverTarget {
	
	public static interface IObserver extends ghost.library.utility.IObserver {
		public void onStateChanged(Task task, State oldState, State newState);
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
	
	private boolean canceled_ = false;
	private boolean aborted_ = false;
	
	private ObserverTargetImpl observerTargetImpl_;

	public Task()
	{
		// TODO Auto-generated constructor stub
	}
	
	public synchronized State getState()
	{
		return state_;
	}
	public synchronized boolean setState(State state)
	{
		if (state_ == state)
		{
			return true;
		}
		if (state_.canReachNextState(state))
		{
			State oldState = state_;
			state_ = state;
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
	public synchronized boolean await()
	{
		return setState(State.AWAITING);
	}
	
	public synchronized boolean isExecuting()
	{
		return State.EXECUTING == getState();
	}
	public synchronized boolean execute()
	{
		return setState(State.EXECUTING);
	}
	
	public synchronized boolean isFinished()
	{
		return State.FINISHED == getState();
	}
	public synchronized boolean finish()
	{
		return setState(State.FINISHED);
	}
	
	public synchronized boolean isCanceled()
	{
		return canceled_;
	}
	public synchronized boolean cancel()
	{
		if (setState(State.FINISHED))
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
		if (setState(State.FINISHED))
		{
			aborted_ = true;
			return true;
		}
		return false;
	}
	
	public synchronized boolean reset()
	{
		if (State.NONE == state_)
		{
			return true;
		}
		if (setState(State.NONE))
		{
			canceled_ = false;
			aborted_ = false;
			return true;
		}
		return false;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addObserver(ghost.library.utility.IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null == observerTargetImpl_)
		{
			observerTargetImpl_ = new ObserverTargetImpl();
		}
		observerTargetImpl_.addObserver(observer);
	}

	@Override
	public void removeObserver(ghost.library.utility.IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.removeObserver(observer);
		}
	}

	@Override
	public void clearObserver()
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
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.notifyObservers(methodName, parameters);
		}
	}

}
