package ghost.library.app;

import ghost.library.R;
import ghost.library.utility.IObserver;
import ghost.library.utility.IObserverManager;
import ghost.library.utility.IObserverTarget;
import ghost.library.utility.ITimerManager;
import ghost.library.utility.ITimerReceiver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SupportFragmentActivity 
	extends SherlockFragmentActivity
	implements IActivity, IObserverManager, ITimerManager 
{	
	private ActivityController controller_;
	
	private boolean allowStartActivity_ = true;
	
	protected ActivityController createController(Class<? extends ActivityController> controllerClass)
	{
		if (null != controllerClass)
		{
			try
			{
				Constructor<? extends ActivityController> constructor = controllerClass.getConstructor(android.app.Activity.class);
				if (null != constructor)
				{
					return constructor.newInstance(this);
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
			catch (InstantiationException e)
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
		return new ActivityController(this);
	}
	
	protected void startActivityAnimation(Intent intent)
	{
		if (0 != (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP))
		{
			overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
		}
		else
		{
			overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
		}
	}
	
	protected void finishAnimation()
	{
		overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
	}
	
	// IActivity
	
	@Override
	public Runnable getRunnableAfterSplashOpened()
	{
		return controller_.getRunnableAfterSplashOpened();
	}
	
	@Override
	public void openSplash()
	{
		controller_.openSplash();
	}
	
	@Override
	public void closeSplash(Runnable runnableAfterSplashClosed)
	{
		controller_.closeSplash(runnableAfterSplashClosed);
	}
	
	@Override
	public void initActionBar()
	{
		controller_.initActionBar();
	}
	
	@Override
	public int getStatusBarHeight()
	{
		return controller_.getStatusBarHeight();
	}
	
	@Override
	public View getFaceView()
	{
		return controller_.getFaceView();
	}
	
	@Override
	public View getFullScreenView()
	{
		// TODO Auto-generated method stub
		return controller_.getFullScreenView();
	}
	
	@Override
	public boolean openFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewOpened)
	{
		return controller_.openFullScreenView(fullScreenView, runnableAfterFullScreenViewOpened);
	}
	
	@Override
	public boolean closeFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewClosed)
	{
		return controller_.closeFullScreenView(fullScreenView, runnableAfterFullScreenViewClosed);
	}
	
	// ObserverManager
	
	@Override
	public final void attachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		controller_.attachObserver(observer, target);
	}

	@Override
	public final void detachObserver(IObserver observer, IObserverTarget target)
	{
		// TODO Auto-generated method stub
		controller_.detachObserver(observer, target);
	}
	
	// android.app.Activity
	
	@TargetApi(11)
	@Override
	public void startActivities(Intent[] intents)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivities(intents);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(16)
	@Override
	public void startActivities(Intent[] intents, Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivities(intents, options);
			allowStartActivity_ = false;
		}
	}
	
	@Override
	public void startActivity(Intent intent)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivity(intent);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(16)
	@Override
	public void startActivity(Intent intent, Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivity(intent, options);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityForResult(intent, requestCode);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(16)
	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityForResult(intent, requestCode, options);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@Override
	public void startActivityFromChild(android.app.Activity child,
			Intent intent, int requestCode)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityFromChild(child, intent, requestCode);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(16)
	@Override
	public void startActivityFromChild(android.app.Activity child,
			Intent intent, int requestCode, Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityFromChild(child, intent, requestCode, options);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(11)
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityFromFragment(fragment, intent, requestCode);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@TargetApi(16)
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent,
			int requestCode, Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			super.startActivityFromFragment(fragment, intent, requestCode, options);
			startActivityAnimation(intent);
			allowStartActivity_ = false;
		}
	}
	
	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			boolean ret = super.startActivityIfNeeded(intent, requestCode);
			allowStartActivity_ = false;
			return ret;
		}
		return false;
	}
	
	@TargetApi(16)
	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode,
			Bundle options)
	{
		// TODO Auto-generated method stub
		if (allowStartActivity_)
		{
			boolean ret = super.startActivityIfNeeded(intent, requestCode, options);
			allowStartActivity_ = false;
			return ret;
		}
		return false;
	}
	
	@Override
	public void finish()
	{
		// TODO Auto-generated method stub
		super.finish();
		finishAnimation();
	}
	
	@Override
	public void finishActivity(int requestCode)
	{
		// TODO Auto-generated method stub
		super.finishActivity(requestCode);
		finishAnimation();
	}
	
	@Override
	public void finishActivityFromChild(android.app.Activity child,
			int requestCode)
	{
		// TODO Auto-generated method stub
		super.finishActivityFromChild(child, requestCode);
		finishAnimation();
	}
	
	@Override
	public void finishFromChild(android.app.Activity child)
	{
		// TODO Auto-generated method stub
		super.finishFromChild(child);
		finishAnimation();
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		initActionBar();
		openSplash();
	}
	
	@Override
	public void setContentView(View view)
	{
		// TODO Auto-generated method stub
		super.setContentView(view);
		initActionBar();
		openSplash();
	}
	
	@Override
	public void setContentView(View view, LayoutParams params)
	{
		// TODO Auto-generated method stub
		super.setContentView(view, params);
		initActionBar();
		openSplash();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/**
		 * if call this mehod, bugssssss
		 */
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		allowStartActivity_ = true;
		
		Class<? extends ActivityController> controllerClass = null;
		Bundle paramBundle = savedInstanceState;
		if (null == savedInstanceState) 
		{
			paramBundle = getIntent().getExtras();
		}
		if (null != paramBundle)
		{
			try
			{
				controllerClass = (Class<? extends ActivityController>)paramBundle.getSerializable(ActivityController.PARAM_CONTROLLER_CLASS);
			}
			catch (ClassCastException e)
			{
				// TODO: handle exception
			}
		}
		controller_ = createController(controllerClass);
		controller_.onActivityCreate(savedInstanceState);
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		controller_.onActivityDestroy();
		controller_ = null;
	}
	
	@Override
	protected void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		controller_.onActivityStart();
	}
	
	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		controller_.onActivityStop();
	}
	
	@Override
	protected void onRestart()
	{
		// TODO Auto-generated method stub
		super.onRestart();
		allowStartActivity_ = true;
		controller_.onActivityRestart();
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		allowStartActivity_ = true;
		controller_.onActivityResume();
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		controller_.onActivityPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		controller_.onActivitySaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		controller_.onActivityRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		controller_.onActivityNewIntent(intent);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		// TODO Auto-generated method stub
//		if (controller_.onActivityKeyDown(keyCode, event))
//		{
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (controller_.onActivityKeyUp(keyCode, event))
		{
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		controller_.onActivityConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		if (controller_.onActivityPrepareOptionsMenu(menu))
		{
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		controller_.onActivityCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		if (controller_.onActivityOptionsItemSelected(item))
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onOptionsMenuClosed(android.view.Menu menu)
	{
		// TODO Auto-generated method stub
		super.onOptionsMenuClosed(menu);
		controller_.onActivityOptionsMenuClosed(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		controller_.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		if (!controller_.onActivityBackPressed())
		{
			super.onBackPressed();
		}
	}
	
	// timer manager

	@Override
	public void attachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		controller_.attachReceiver(millis, receiver);
	}

	@Override
	public void detachReceiver(long millis, ITimerReceiver receiver)
	{
		// TODO Auto-generated method stub
		controller_.detachReceiver(millis, receiver);
	}

	@Override
	public void clearTimer(long millis)
	{
		// TODO Auto-generated method stub
		controller_.clearTimer(millis);
	}

	@Override
	public void clearAllTimers()
	{
		// TODO Auto-generated method stub
		controller_.clearAllTimers();
	}

}
