package ghost.library.network;

import ghost.library.utility.IObserverTarget;
import ghost.library.utility.Log;
import ghost.library.utility.ObserverTargetImpl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusReceiver extends BroadcastReceiver implements IObserverTarget {
	
	public static final String LOG_TAG = "network_status";

	public static enum StatusChange{
		OFF_TO_ON,
		ON_TO_OFF,
		NO_CHANGE
	}
	
	public static interface IObserver extends ghost.library.utility.IObserver{
		public void onStatusChanged(StatusChange mobileChange, StatusChange wifiChange);
	}
	
	private ObserverTargetImpl observerTargetImpl_;
	
	private boolean mobileNetworkConnected_ = false;
	private boolean wifiNetworkConnected_ = false;
	
	private boolean opened_ = false;
	
	public void open(Context context)
	{
		if (!opened_)
		{
			opened_ = true;
			IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			context.registerReceiver(this, filter);
			testStatus(context);
		}
	}
	
	public void close(Context context)
	{
		if (opened_)
		{
			opened_ = false;
			context.unregisterReceiver(this);
		}
	}
	
	public boolean isMobileConnected()
	{
		return mobileNetworkConnected_;
	}
	
	public boolean isWifiConnected()
	{
		return wifiNetworkConnected_;
	}
	
	public boolean isNetworkValid()
	{
		return isMobileConnected() || isWifiConnected();
	}
	
	public void testStatus(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		boolean oldMobileNetworkConnected = mobileNetworkConnected_;
		mobileNetworkConnected_ = mobileNetworkInfo.isConnected();
		boolean oldWifiNetworkConnected = wifiNetworkConnected_;
		wifiNetworkConnected_ = wifiNetworkInfo.isConnected();
		
		if (null != observerTargetImpl_ && observerTargetImpl_.hasObserver())
		{
			StatusChange mobileChange = StatusChange.NO_CHANGE;
			if (oldMobileNetworkConnected != mobileNetworkConnected_)
			{
				mobileChange = mobileNetworkConnected_ ? StatusChange.OFF_TO_ON : StatusChange.ON_TO_OFF;
			}
			StatusChange wifieChange = StatusChange.NO_CHANGE;
			if (oldWifiNetworkConnected != wifiNetworkConnected_)
			{
				wifieChange = wifiNetworkConnected_ ? StatusChange.OFF_TO_ON : StatusChange.ON_TO_OFF;
			}
			
			if (StatusChange.NO_CHANGE != mobileChange || StatusChange.NO_CHANGE != wifieChange)
			{
				Log.d(LOG_TAG, "network status changed on thread("+Thread.currentThread().getName()+") mobile: "+(isMobileConnected() ? "on" : "off")+"\t wifi: "+(isWifiConnected() ? "on" : "off"));
				notifyObservers(
						"onStatusChanged", 
						new NotifyMethodParam(StatusChange.class, mobileChange),
						new NotifyMethodParam(StatusChange.class, wifieChange));
			}
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		testStatus(context);
	}

	@Override
	public final void addObserver(ghost.library.utility.IObserver observer)
	{
		if (null == observerTargetImpl_)
		{
			observerTargetImpl_ = new ObserverTargetImpl();
		}
		observerTargetImpl_.addObserver(observer);
	}
	
	@Override
	public final void removeObserver(ghost.library.utility.IObserver observer)
	{
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.removeObserver(observer);
		}
	}
	
	@Override
	public final void clearObserver()
	{
		// TODO Auto-generated method stub
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.clearObserver();
		}
	}
	
	@Override
	public void notifyObservers(String methodName,
			NotifyMethodParam... parameters)
	{
		// TODO Auto-generated method stub
		if (null != observerTargetImpl_)
		{
			observerTargetImpl_.notifyObservers(methodName, parameters);
		}
	}

}
