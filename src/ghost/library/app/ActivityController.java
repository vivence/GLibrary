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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ActivityController 
	implements IActivity, IObserverManager, ITimerManager
{
	public static final String LOG_TAG = "activity";
	
	public static final String PARAM_CONTROLLER_CLASS = "controller_class";
	public static final String PARAM_OPEN_SPLASH = "open_splash";
	
	private android.app.Activity activity_;
	
	private TimerManagerImpl timerManagerImpl_;
	
	private boolean openSplash_ = false;
	private boolean splashClosing_ = false;
	private boolean actionBarInited_ = false;
	private int statusBarHeight_;
	private ObserverManagerImpl observerManagerImpl_;
	
	private View.OnClickListener titleClickListener_ = new View.OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			ActivityController.this.scrollListToHead();
		}
	};
	
	public ActivityController(android.app.Activity activity)
	{
		// TODO Auto-generated constructor stub
		Assert.assertNotNull(activity);
		activity_ = activity;
	}
	
	public android.app.Activity getActivity()
	{
		return activity_;
	}
	
	public void scrollListToHead()
	{
		try
		{
			ListView listView = (ListView)activity_.findViewById(R.id.list);
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
		if (activity_ instanceof SherlockActivity)
		{
			return ((SherlockActivity) activity_).getSupportActionBar();
		}
		else if (activity_ instanceof SherlockFragmentActivity)
		{
			return ((SherlockFragmentActivity) activity_).getSupportActionBar();
		}
		return null;
	}
	
	public Resources getResources()
	{
		return activity_.getResources();
	}
	
	public Window getWindow()
	{
		return activity_.getWindow();
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
		ActionBar actionBar = getSupportActionBar();
		if (null == actionBar)
		{
			return;
		}
		if (0 != (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP))
		{
			actionBar.setHomeButtonEnabled(true);
		}
		if (Build.VERSION_CODES.ICE_CREAM_SANDWICH > Build.VERSION.SDK_INT)
		{
			View faceView = getFaceView();
			if (null != faceView)
			{
				View actionBarView = faceView.findViewById(com.actionbarsherlock.R.id.abs__action_bar);
				if (null != actionBarView)
				{
					actionBarView.setOnClickListener(titleClickListener_);
				}
			}
		}
		else
		{
			// bug: title text view clickable
			if (null != actionBar && (0 == (actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_TITLE)))
			{
				View faceView = getFaceView();
				if (null != faceView)
				{
					View contentView = faceView.findViewById(android.R.id.content);
					if (null != contentView)
					{
						ViewParent parent = contentView.getParent();
						if (null != parent && parent instanceof ViewGroup)
						{
							ViewGroup parentView = (ViewGroup)parent;
							int childCount = parentView.getChildCount();
							for (int i = 0; i < childCount; i++)
							{
								View childView = parentView.getChildAt(i);
								if (childView.getClass().getName().equals("com.android.internal.widget.ActionBarContainer"))
								{
									ViewGroup container = (ViewGroup)childView;
									int containerChildCount = container.getChildCount();
									for (int j = 0; j < containerChildCount; j++)
									{
										View containerChildView = container.getChildAt(j);
										if (containerChildView.getClass().getName().equals("com.android.internal.widget.ActionBarView"))
										{
											containerChildView.setOnClickListener(titleClickListener_);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
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
		doCloseSplash(runnableAfterSplashClosed);
	}
	
	protected void doCloseSplash(Runnable runnableAfterSplashClosed)
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
	
	// activity
	
	protected ActivityStack getActivityStack()
	{
		return null;
	}
	
	public final void onActivityCreate(Bundle savedInstanceState)
	{
		Log.i(LOG_TAG, getClass().getName()+"[onCreate]");
		ActivityStack activityStack = getActivityStack();
		if (null != activityStack)
		{
			activityStack.onActivityCreate(activity_);
		}
		Bundle paramBundle = savedInstanceState;
		if (null == savedInstanceState) 
		{
			paramBundle = activity_.getIntent().getExtras();
		}
		if (null != paramBundle)
		{
			openSplash_ = paramBundle.getBoolean(PARAM_OPEN_SPLASH, false);
		}
		splashClosing_ = false;
	}
	
	public void doOnActivityCreate(Bundle savedInstanceState)
	{
		
	}
	
	public void onActivityDestroy()
	{
		Log.i(LOG_TAG, getClass().getName()+"[onDestroy]");
		ActivityStack activityStack = getActivityStack();
		if (null != activityStack)
		{
			activityStack.onActivityDestroy(activity_);
		}
		clearTaskObservers();
		clearAllTimers();
	}

	public void onActivityStart()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onStart]");
	}
	
	public void onActivityStop()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onStop]");
		ActivityStack activityStack = getActivityStack();
		if (null != activityStack)
		{
			activityStack.onActivityStop(activity_);
		}
	}
	
	public void onActivityRestart()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onRestart]");
		ActivityStack activityStack = getActivityStack();
		if (null != activityStack)
		{
			activityStack.onActivityRestart(activity_);
		}
	}
	
	public void onActivityResume()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onResume]");
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.resume();
		}

		ActivityStack activityStack = getActivityStack();
		if (null != activityStack)
		{
			activityStack.onActivityResume(activity_);
		}
	}
	
	public void onActivityPause()
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onPause]");
		if (null != timerManagerImpl_)
		{
			timerManagerImpl_.pause();
		}
	}
	
	public void onActivitySaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onSaveInstanceState]");
	}
	
	public void onActivityRestoreInstanceState(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, getClass().getName()+"[onRestoreInstanceState]");
	}
	
	public void onActivityNewIntent(Intent intent)
	{
		
	}
	
//	public boolean onActivityKeyDown(int keyCode, KeyEvent event)
//	{
//		// TODO Auto-generated method stub
//		if (Build.VERSION_CODES.ECLAIR > Build.VERSION.SDK_INT)
//		{
//			if (KeyEvent.KEYCODE_BACK == keyCode)
//			{
//				View view = getFullScreenView();
//				if (view instanceof SplashView && splashClosing_)
//				{
//					return true;
//				}
//				if (null != view && view instanceof IFullScreenView)
//				{
//					closeFullScreenView(view, null);
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	public boolean onActivityKeyUp(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
//		if (Build.VERSION_CODES.ECLAIR <= Build.VERSION.SDK_INT)
//		{
			if (KeyEvent.KEYCODE_BACK == keyCode)
			{
				View view = getFullScreenView();
				if (splashClosing_)
				{
					return true;
				}
				if (null != view && view instanceof IFullScreenView)
				{
					closeFullScreenView(view, null);
					return true;
				}
			}
//		}
		return false;
	}
	
	public void onActivityConfigurationChanged(Configuration newConfig)
	{
		
	}
	
	public boolean onActivityPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onActivityCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
	}
	
	public boolean onActivityOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case android.R.id.home:
			getActivity().finish();
			return true;
		default:
			return false;
		}
	}
	
	public void onActivityOptionsMenuClosed(android.view.Menu menu)
	{
		// TODO Auto-generated method stub
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		
	}
	
	public boolean onActivityBackPressed()
	{
		return false;
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
