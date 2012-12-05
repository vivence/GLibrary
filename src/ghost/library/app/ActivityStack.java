package ghost.library.app;

import java.util.Stack;

/**
 * bug:
 * getRootActivity() couldn't return activity recycled by system.
 * @author vivence
 *
 */
public class ActivityStack {
	
	private android.app.Activity currentActivity_;
	private Stack<android.app.Activity> activities_;

	public ActivityStack()
	{
		// TODO Auto-generated constructor stub
	}
	
	public android.app.Activity getCurrentActivity()
	{
		return currentActivity_;
	}
	
	@Deprecated
	public android.app.Activity getRootActivity()
	{
		if (null != activities_ && !activities_.empty())
		{
			return activities_.firstElement();
		}
		return null;
	}
	
	public void onActivityCreate(android.app.Activity activity)
	{
		if (null != activity)
		{
			if (null == activities_)
			{
				activities_ = new Stack<android.app.Activity>();
			}
			activities_.push(activity);
			currentActivity_ = activity;
		}
	}
	
	public void onActivityDestroy(android.app.Activity activity)
	{
		if (null != activity && null != activities_)
		{
			activities_.remove(activity);
		}
	}
	
	public void onActivityRestart(android.app.Activity activity)
	{
		if (null != activity)
		{
			currentActivity_ = activity;
		}
	}
	
	public void onActivityResume(android.app.Activity activity)
	{
		if (null != activity)
		{
			currentActivity_ = activity;
		}
	}
	
	public void onActivityStop(android.app.Activity activity)
	{
		if (currentActivity_ == activity)
		{
			currentActivity_ = null;
		}
	}

}
