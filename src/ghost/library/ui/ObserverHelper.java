package ghost.library.ui;

import ghost.library.utility.IObserverManager;
import ghost.library.utility.ObserverImpl;
import android.content.Context;

public abstract class ObserverHelper extends ObserverImpl {
	
	@Override
	protected final IObserverManager getManager()
	{
		// TODO Auto-generated method stub
		Context attachedContext = getAttachedContext();
		if (null != attachedContext && attachedContext instanceof IObserverManager)
		{
			return (IObserverManager)attachedContext;
		}
		android.support.v4.app.Fragment attachedFragment = getAttachedFragment();
		if (null != attachedFragment && attachedFragment instanceof IObserverManager)
		{
			return (IObserverManager)attachedFragment;
		}
		return null;
	}
	
	protected abstract Context getAttachedContext();
	protected abstract android.support.v4.app.Fragment getAttachedFragment();
}
