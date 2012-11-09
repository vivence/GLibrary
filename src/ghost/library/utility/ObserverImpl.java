package ghost.library.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class ObserverImpl implements IObserver {
	
	private static final Class<?>[] s_managerMethodParamTypes = new Class<?>[]{
		IObserver.class,
		IObserverTarget.class
	};
	
	protected void managerSelf(String managerMethodName, IObserverTarget target)
	{
		try
		{
			IObserverManager manager = getManager();
			if (null != manager)
			{
				Method method = IObserverManager.class.getMethod(
						managerMethodName, s_managerMethodParamTypes);
				method.invoke(manager, this, target);
			}
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@Override
	public final void onAttachTarget(IObserverTarget target)
	{
		// TODO Auto-generated method stub
		managerSelf("attachObserver", target);
		doOnAttachTarget(target);
	}
	
	protected void doOnAttachTarget(IObserverTarget target){}
	
	@Override
	public final void onDetachTarget(IObserverTarget target)
	{
		// TODO Auto-generated method stub
		managerSelf("detachObserver", target);
		doOnDetachTarget(target);
	}
	
	protected void doOnDetachTarget(IObserverTarget target){}
	
	protected abstract IObserverManager getManager();

}
