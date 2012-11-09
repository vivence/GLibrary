package ghost.library.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class ObserverTargetImpl implements IObserverTarget {

	private Set<IObserver> observers_;
	
	public boolean hasObserver()
	{
		return null != observers_ ? observers_.isEmpty() : false;
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
		if (null != observers_)
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
				for (IObserver observer : observers_)
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

}
