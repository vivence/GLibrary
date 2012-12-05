package ghost.library.app;

import ghost.library.R;
import ghost.library.utility.IObserver;
import ghost.library.utility.IObserverManager;
import ghost.library.utility.IObserverTarget;
import ghost.library.utility.ITimerManager;
import ghost.library.utility.ITimerReceiver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SupportFragment 
	extends SherlockFragment
	implements IActivity, IObserverManager, ITimerManager 
{	
	private FragmentController controller_;
	
	private boolean allowStartActivity_ = true;
	
	protected FragmentController createController(Class<? extends FragmentController> controllerClass)
	{
		if (null != controllerClass)
		{
			try
			{
				Constructor<? extends FragmentController> constructor = controllerClass.getConstructor(android.support.v4.app.Fragment.class);
				if (null != constructor)
				{
					return constructor.newInstance(this);
				}
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (java.lang.InstantiationException e)
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
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FragmentController(this);
	}
	
	protected void startActivityAnimation(Intent intent)
	{
		if (0 != (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP))
		{
			getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
		}
		else
		{
			getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
		}
	}
	
	protected void startAnimation(FragmentTransaction ft)
	{
		getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
	}
	
	protected void finishAnimation(FragmentTransaction ft)
	{
		ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
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
	
	public void startFragment(android.support.v4.app.Fragment fragment)
	{
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		startAnimation(ft);
		ft.replace(getId(), fragment);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	public void finish()
	{
		// TODO Auto-generated method stub
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.remove(this);
		ft.commit();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		super.onAttach(activity);
		allowStartActivity_ = true;
		controller_ = createController(FragmentController.class);
		controller_.onFragmentAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
//		Class<? extends FragmentController> controllerClass = null;
//		Bundle paramBundle = savedInstanceState;
//		if (null == savedInstanceState) 
//		{
//			paramBundle = getArguments();
//		}
//		if (null != paramBundle)
//		{
//			try
//			{
//				controllerClass = (Class<? extends FragmentController>)paramBundle.getSerializable(FragmentController.PARAM_CONTROLLER_CLASS);
//			}
//			catch (ClassCastException e)
//			{
//				// TODO: handle exception
//			}
//		}
		
		controller_.onFragmentCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view  = controller_.onFragmentCreateView(inflater, container, savedInstanceState);
		if (null != view)
		{
			return view;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		controller_.onActivityCreate(savedInstanceState);
		initActionBar();
		openSplash();
	}
	
	@Override
	public void onDestroyView()
	{
		// TODO Auto-generated method stub
		super.onDestroyView();
		controller_.onFragmentDestroyView();
	}
	
	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		controller_.onFragmentDestroy();
	}
	
	@Override
	public void onDetach()
	{
		// TODO Auto-generated method stub
		super.onDetach();
		controller_.onFragmentDetach();
		controller_ = null;
	}
	
	@Override
	public void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		controller_.onFragmentStart();
	}
	
	@Override
	public void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		controller_.onFragmentStop();
	}
	
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		allowStartActivity_ = true;
		controller_.onFragmentResume();
	}
	
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		controller_.onFragmentPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		controller_.onFragmentSaveInstanceState(outState);
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
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		controller_.onFragmentConfigurationChanged(newConfig);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		controller_.onFragmentPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		controller_.onFragmentCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		if (controller_.onFragmentOptionsItemSelected(item))
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
		controller_.onFragmentOptionsMenuClosed(menu);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		controller_.onActivityResult(requestCode, resultCode, data);
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
