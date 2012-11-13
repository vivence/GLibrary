package ghost.library.utility;


public class Log {
	private Log(){}
	
	public static final int LOG_LEVEL_VERBOSE = 1;
	public static final int LOG_LEVEL_DEBUG = 2;
	public static final int LOG_LEVEL_INFO = 3;
	public static final int LOG_LEVEL_WARN = 4;
	public static final int LOG_LEVEL_ERROR = 5;
	public static final int LOG_LEVEL_FETAL = 5;
	
	public static int LOG_LEVEL = LOG_LEVEL_VERBOSE;
	
	public static int v(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_VERBOSE) 
		{
			return android.util.Log.v(tag, msg);
		}
		return 0;
	}
	
	public static int v(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_VERBOSE) 
		{
			return android.util.Log.v(tag, msg, tr);
		}
		return 0;
	}
	
	public static int d(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_DEBUG) 
		{
			return android.util.Log.d(tag, msg);
		}
		return 0;
	}
	
	public static int d(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_DEBUG) 
		{
			return android.util.Log.d(tag, msg, tr);
		}
		return 0;
	}
	
	public static int i(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_INFO) 
		{
			return android.util.Log.i(tag, msg);
		}
		return 0;
	}
	
	public static int i(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_INFO) 
		{
			return android.util.Log.i(tag, msg, tr);
		}
		return 0;
	}
	
	public static int w(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_WARN) 
		{
			return android.util.Log.w(tag, msg);
		}
		return 0;
	}
	
	public static int w(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_WARN) 
		{
			return android.util.Log.w(tag, msg, tr);
		}
		return 0;
	}
	
	public static int w(String tag, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_WARN) 
		{
			return android.util.Log.w(tag, tr);
		}
		return 0;
	}
	
	public static int e(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_ERROR) 
		{
			return android.util.Log.e(tag, msg);
		}
		return 0;
	}
	
	public static int e(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_ERROR) 
		{
			return android.util.Log.e(tag, msg, tr);
		}
		return 0;
	}
	
	public static int wtf(String tag, String msg) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_FETAL) 
		{
			return android.util.Log.wtf(tag, msg);
		}
		return 0;
	}
	
	public static int wtf(String tag, String msg, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_FETAL) 
		{
			return android.util.Log.wtf(tag, msg, tr);
		}
		return 0;
	}
	
	public static int wtf(String tag, Throwable tr) 
	{
		if (LOG_LEVEL <= LOG_LEVEL_FETAL) 
		{
			return android.util.Log.wtf(tag, tr);
		}
		return 0;
	}
}
