package ghost.library.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.Assert;
import android.os.Handler;

public class TimerManagerImpl implements ITimerManager {
	
	private Map<Long, Set<ITimerReceiver>> receivers_;
	private Map<Long, Runnable> timers_;
	private Handler handler_;
	private boolean paused_ = false;
	
	public TimerManagerImpl(Handler handler)
	{
		// TODO Auto-generated constructor stub
		Assert.assertNotNull(handler);
		handler_ = handler;
	}
	
	private void notifyTimer(long millis)
	{
		if (null != receivers_)
		{
			Set<ITimerReceiver> receivers = receivers_.get(millis);
			if (null != receivers)
			{
				for (ITimerReceiver receiver : receivers)
				{
					receiver.onTimer(millis);
				}
			}
		}
	}
	
	private void startTimer(final long millis)
	{
		if (null == timers_)
		{
			timers_ = new TreeMap<Long, Runnable>();
		}
		Runnable timer = timers_.get(millis);
		if (null == timer)
		{
			timer = new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					TimerManagerImpl.this.notifyTimer(millis);
					TimerManagerImpl.this.handler_.postDelayed(
							this, millis);
				}
			};
			timers_.put(millis, timer);
			if (!paused_)
			{
				handler_.postDelayed(timer, millis);
			}
		}
	}
	
	private void endTimer(long millis)
	{
		if (null != timers_)
		{
			Runnable timer = timers_.remove(millis);
			if (null != timer)
			{
				handler_.removeCallbacks(timer);
			}
		}
	}
	
	private void endAllTimers()
	{
		if (null != timers_)
		{
			Collection<Runnable> timers = timers_.values();
			for (Runnable timer : timers)
			{
				handler_.removeCallbacks(timer);
			}
			timers_.clear();
			timers_ = null;
		}
	}
	
	public void resume()
	{
		if (paused_)
		{
			paused_ = false;
			if (null != timers_)
			{
				Set<Entry<Long, Runnable>> elements = timers_.entrySet();
				if (null != elements)
				{
					for (Entry<Long, Runnable> element : elements)
					{
						handler_.postDelayed(element.getValue(), element.getKey());
					}
				}
			}
		}
	}
	
	public void pause()
	{
		if (!paused_)
		{
			paused_ = true;
			Collection<Runnable> timers = timers_.values();
			for (Runnable timer : timers)
			{
				handler_.removeCallbacks(timer);
			}
		}
	}

	@Override
	public void attachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		if (null == receiver || 0 >= millis)
		{
			return;
		}
		if (null == receivers_)
		{
			receivers_ = new TreeMap<Long, Set<ITimerReceiver>>();
		}
		Set<ITimerReceiver> receivers = receivers_.get(millis);
		if (null == receivers)
		{
			receivers = new HashSet<ITimerReceiver>();
			receivers.add(receiver);
			receivers_.put(millis, receivers);
			startTimer(millis);
		}
		else
		{
			receivers.add(receiver);
		}
	}

	@Override
	public void detachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		if (null == receiver || 0 >= millis)
		{
			return;
		}
		if (null != receivers_)
		{
			Set<ITimerReceiver> receivers = receivers_.get(millis);
			if (null != receivers)
			{
				receivers.remove(receiver);
				if (receivers.isEmpty())
				{
					receivers_.remove(millis);
					endTimer(millis);
				}
			}
		}
	}

	@Override
	public void clearTimer(long millis)
	{
		// TODO Auto-generated method stub
		if (null != receivers_)
		{
			receivers_.remove(millis);
			if (receivers_.isEmpty())
			{
				receivers_ = null;
			}
		}
		endTimer(millis);
	}

	@Override
	public void clearAllTimers()
	{
		// TODO Auto-generated method stub
		if (null != receivers_)
		{
			receivers_.clear();
			receivers_ = null;
		}
		endAllTimers();
	}

}
