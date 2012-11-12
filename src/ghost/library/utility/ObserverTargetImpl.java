package ghost.library.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class ObserverTargetImpl implements IObserverTarget {

	private Set<IObserver> observers_;
	private Set<IObserver> tempObservers_;
	
	public boolean hasObserver()
	{
		return null != observers_ ? observers_.isEmpty() : false;
	}
	
	public void beginSafeNotify()
	{
		if (null != observers_)
		{
			tempObservers_ = new HashSet<IObserver>(observers_);
		}
		else
		{
			tempObservers_ = null;
		}
	}
	
	public void endSafeNotify()
	{
		tempObservers_ = null;
	}
	
	public void safeNotify(String methodName, NotifyMethodParam... parameters)
	{
		notifyObservers(tempObservers_, methodName, parameters);
	}
	
	public static void notifyObservers(Set<IObserver> observers, String methodName, NotifyMethodParam... parameters)
	{
		if (null != observers)
		{
			try
			{
				Class<?>[] paramClasses = new Class<?>[parameters.length];
				for (int i = 0; i < paramClasses.length; ++i)
				{
					paramClasses[i] = parameters[i].paramClass;
				}
				Method method = IObserver.class.getMethod(methodName, paramClasses);
				Object[] params = new Object[parameters.length];
				for (int i = 0; i < params.length; ++i)
				{
					params[i] = parameters[i].param;
				}
				for (IObserver observer : observers)
				{
					try
					{
						method.invoke(observer, params);
					}
					catch (IllegalArgumentException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (InvocationTargetException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
			}
		}
	}

	@Override
	public void addObserver(IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null == observer)
		{
			return;
		}
		if (null == observers_)
		{
			observers_ = new HashSet<IObserver>();
		}
		if (observers_.add(observer))
		{
			observer.onAttachTarget(this);
		}
	}

	@Override
	public void removeObserver(IObserver observer)
	{
		// TODO Auto-generated method stub
		if (null == observer)
		{
			return;
		}
		if (null != observer)
		{
			if (observers_.remove(observer))
			{
				observer.onDetachTarget(this);
			}
		}
	}
	
	@Override
	public void clearObserver()
	{
		// TODO Auto-generated method stub
		if (null != observers_)
		{
			Set<IObserver> temp = observers_;
			observers_ = null;
			for (IObserver observer : temp)
			{
				observer.onDetachTarget(this);
			}
		}
	}

	@Override
	public void notifyObservers(String methodName, NotifyMethodParam... parameters)
	{
		// TODO Auto-generated method stub
		notifyObservers(observers_, methodName, parameters);
	}

}
