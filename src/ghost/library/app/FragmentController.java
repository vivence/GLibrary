package ghost.library.app;

import ghost.library.R;
import ghost.library.ui.widget.IFullScreenView;
import ghost.library.utility.IObserver;
import ghost.library.utility.IObserverManager;
import ghost.library.utility.IObserverTarget;
import ghost.library.utility.ITimerManager;
import ghost.library.utility.ITimerReceiver;
import ghost.library.utility.Log;
import ghost.library.utility.ObserverManagerImpl;
import ghost.library.utility.TimerManagerImpl;
import junit.framework.Assert;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class FragmentController 
	implements IActivity, IObserverManager, ITimerManager
{
	public static final String LOG_TAG = "fragment";
	
	public static final String PARAM_CONTROLLER_CLASS = "controller_class";
	public static final String PARAM_OPEN_SPLASH = "open_splash";
	
	private android.support.v4.app.Fragment fragment_;
	
	private TimerManagerImpl timerManagerImpl_;
	
	private boolean openSplash_ = false;
	private boolean splashClosing_ = false;
	private boolean actionBarInited_ = false;
	private int statusBarHeight_;
	private ObserverManagerImpl observerManagerImpl_;
	
	public FragmentController(android.support.v4.app.Fragment fragment)
	{
		// TODO Auto-generated constructor stub
		Assert.assertNotNull(fragment);
		fragment_ = fragment;
	}
	
	public android.support.v4.app.Fragment getFragment()
	{
		return fragment_;
	}
	
	public void scrollListToHead()
	{
		try
		{
			ListView listView = (ListView)fragment_.getView().findViewById(R.id.list);
			if (null != listView)
			{
				listView.setSelection(0);
			}
		}
		catch (ClassCastException e)
		{
			// TODO: handle exception
		}
	}
	
	public final void clearTaskObservers()
	{
		if (null != observerManagerImpl_)
		{
			observerManagerImpl_.clearTaskObservers();
			observerManagerImpl_ = null;
		}
	}
	
	public ActionBar getSupportActionBar()
	{
		if (fragment_ instanceof SherlockFragment)
		{
			return ((SherlockFragment) fragment_).getSherlockActivity().getSupportActionBar();
		}
		return null;
	}
	
	public Resources getResources()
	{
		return fragment_.getResources();
	}
	
	public Window getWindow()
	{
		return fragment_.getActivity().getWindow();
	}
	
	public boolean actionBarInited()
	{
		return actionBarInited_;
	}
	
	@Override
	public final void initActionBar()
	{
		if (actionBarInited_)
		{
			return;
		}
		doInitActionBar();
		actionBarInited_ = true;
	}
	
	protected void doInitActionBar()
	{
		
	}
	
	@Override
	public Runnable getRunnableAfterSplashOpened()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final void openSplash()
	{
		if (!openSplash_)
		{
			return;
		}
		openSplash_ = false;
		doOpenSplash();
	}
	
	protected void doOpenSplash()
	{
		
	}
	
	@Override
	public void closeSplash(Runnable runnableAfterSplashClosed)
	{
		if (splashClosing_)
		{
			return;
		}
		splashClosing_ = true;
		doCloseSplash();
	}
	
	protected void doCloseSplash()
	{
		
	}
	
	@Override
	public int getStatusBarHeight()
	{
		if (0 == statusBarHeight_)
		{
			Rect displayRect = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRect);
			statusBarHeight_ = displayRect.top;
		}
		return statusBarHeight_;
	}
	
	@Override
	public View getFaceView()
	{
		View faceView = getWindow().getDecorView();
//		if (null != faceView && faceView instanceof ViewGroup)
//		{
//			faceView = faceView.findViewById(android.R.id.content);
//		}
		return faceView;
	}
	
	@Override
	public View getFullScreenView()
	{
		// TODO Auto-generated method stub
		View faceView = getFaceView();
		if (null != faceView)
		{
			return faceView.findViewById(R.id.full_screen);
		}
		return null;
	}
	
	@Override
	public boolean openFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewOpened)
	{
		if (null == fullScreenView)
		{
			return false;
		}
		try
		{
			ViewGroup faceView = (ViewGroup)getFaceView();
			if (null != faceView)
			{
				View oldFullScreenView = faceView.findViewById(R.id.full_screen);
				if (null != oldFullScreenView)
				{
					return false;
				}
				
				if (fullScreenView instanceof IFullScreenView)
				{
					fullScreenView.setId(R.id.full_screen);
					((IFullScreenView)fullScreenView).open(faceView, runnableAfterFullScreenViewOpened);
					return true;
				}
			}
		}
		catch (ClassCastException e)
		{
			// TODO: handle exception
		}
		return false;
	}
	
	@Override
	public boolean closeFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewClosed)
	{
		if (null == fullScreenView)
		{
			return false;
		}
		try
		{
			ViewGroup faceView = (ViewGroup)getFaceView();
			if (null != faceView)
			{
				if (fullScreenView instanceof IFullScreenView)
				{
					fullScreenView.setId(R.id.full_screen);
					((IFullScreenView)fullScreenView).close(faceView, runnableAfterFullScreenViewClosed);
				}
			}
		}
		catch (ClassCastException e)
		{
			// TODO: handle exception
		}
		return false;
	}
	@Override
	public final void attachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (null == observer || null == target)
		{
			return;
		}
		if (null == observerManagerImpl_)
		{
			observerManagerImpl_ = new ObserverManagerImpl();
		}
		observerManagerImpl_.attachObserver(observer, target);
	}
	
	@Override
	public final void detachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		if (null == observer || null == target)
		{
			return;
		}
		if (null != observerManagerImpl_)
		{
			observerManagerImpl_.detachObserver(observer, target);
		}
	}
	
	// fragment
	
	public void onFragmentAttach(android.app.Activity activity)
	{
		Log.i(LOG_TAG, getClass().getName()+"[onAttach]");
	}
	
	public void onFragmentCreate(Bundle savedInstanceState)
	{
		Log.i(LOG_TAG, getClass().getName()+"[onCreate]");
		
		Bundle paramBundle = savedInstanceState;
		if (null == savedInstanceState) 
		{
			paramBundle = fragment_.getArguments();
		}
		if (null !=
				paramBundle)
		{
			openSplash_ = paramBundle.getBoolean(PARAM_OPEN_SPLASH, false);
		}
		splashClosing_ = false;
	}
	
	public View onFragmentCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		Log.i(LOG_TAG, getClass().getName()+"[onCreateView]");
		return null;
	}
	
	public void onActivityCreate(Bundle savedInstanceState)
	{
		Log.i(LOG_TAG, getClass().getName()+"[onActivityCreate]");
	}
	
	public void onFragmentDestroyView()
	{
		Log.i(LOG_TAG, getClass().getName()+"[onDestroyView]");
	}
	
	public void onFragmentDestroy()
	{
		Log.i(LOG_TAG, getClass().getName()+"[onDestroy]");
		clearTaskObservers();
		clearAllTimers();
	}
	
	public void onFragmentDetach()
	{
		Log.i(LOG_TAG, getClass().getName()+"[onDetach]");
	}

	public void onFragmentStart()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onStart]");
	}
	
	public void onFragmentStop()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onStop]");
	}
	
	public void onFragmentResume()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onResume]");
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.resume();
		}
	}
	
	public void onFragmentPause()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onPause]");
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.pause();
		}
	}
	
	public void onFragmentSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onSaveInstanceState]");
	}
	
	public void onFragmentConfigurationChanged(Configuration newConfig)
	{
		
	}
	
	public void onFragmentPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
	}
	
	public void onFragmentCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
	}
	
	public boolean onFragmentOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case android.R.id.home:
			return true;
		default:
			return false;
		}
	}
	
	public void onFragmentOptionsMenuClosed(android.view.Menu menu)
	{
		// TODO Auto-generated method stub
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void attachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		if (null == timerManagerImpl_)
		{
			timerManagerImpl_ = new TimerManagerImpl(new Handler());
		}
		timerManagerImpl_.attachReceiver(millis, receiver);
	}

	@Override
	public void detachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.detachReceiver(millis, receiver);
		}
	}

	@Override
	public void clearTimer(long millis)
	{
		// TODO Auto-generated method stub
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.clearTimer(millis);
		}
	}

	@Override
	public void clearAllTimers()
	{
		// TODO Auto-generated method stub
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.clearAllTimers();
		}
	}
}
