package ghost.library.utility;

public class TimeUtils extends android.support.v4.util.TimeUtils {
	
	private TimeUtils(){}
	
	public static final int MILLIS_PER_SECOND = 1000;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int MINUTE_PER_HOUR = 60;
	public static final int HOURS_PER_DAY = 24;
	
	public static final int SECONDS_PER_HOUR = MINUTE_PER_HOUR*SECONDS_PER_MINUTE;
	public static final int SECONDS_PER_DAY = HOURS_PER_DAY*MINUTE_PER_HOUR*SECONDS_PER_MINUTE;

	public static int getCurrentTimeSeconds()
	{
		return (int)(getCurrentTimeMillis()/MILLIS_PER_SECOND);
	}
	
	public static long getCurrentTimeMillis()
	{
		return System.currentTimeMillis();
	}
	
	public static int getDurationSeconds(int startSeconds)
	{
		return getCurrentTimeSeconds()-startSeconds;
	}
	
}
