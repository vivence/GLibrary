package ghost.library.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class ObserverManagerImpl implements IObserverManager {

	private Map<IObserver, Set<IObserverTarget>> observers_;
	
	@Override
	public final void attachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (null == observer || null == target)
		{
			return;
		}
		if (null == observers_)
		{
			observers_ = new HashMap<IObserver, Set<IObserverTarget>>();
		}
		Set<IObserverTarget> tasks = observers_.get(observer);
		if (null == tasks)
		{
			tasks = new HashSet<IObserverTarget>();
			tasks.add(target);
			observers_.put(observer, tasks);
		}
		else
		{
			tasks.add(target);
		}
	}

	@Override
	public final void detachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (null == observer || null == target)
		{
			return;
		}
		if (null != observers_)
		{
			Set<IObserverTarget> tasks = observers_.get(observer);
			if (null != tasks)
			{
				tasks.remove(target);
				if (tasks.isEmpty())
				{
					observers_.remove(observer);
				}
			}
		}
	}
	
	public final void clearTaskObservers()
	{
		if (null != observers_)
		{
			Map<IObserver, Set<IObserverTarget>> temp = observers_;
			observers_ = null;
			Set<Entry<IObserver, Set<IObserverTarget>>> elements = temp.entrySet();
			for (Entry<IObserver, Set<IObserverTarget>> element : elements)
			{
				IObserver observer = element.getKey();
				Set<IObserverTarget> targets = element.getValue();
				for (IObserverTarget target : targets)
				{
					target.removeObserver(observer);
				}
			}
		}
	}

}
