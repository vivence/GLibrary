package ghost.library.utility;

public class Date extends java.util.Date {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5538421764844896259L;

	public Date()
	{
		// TODO Auto-generated constructor stub
	}
	
	public Date(long milliseconds) 
	{
		super(milliseconds);
		// TODO Auto-generated constructor stub
	}

	public Date(int seconds) 
	{
		super((long) seconds * TimeUtils.MILLIS_PER_SECOND);
	}

	@Deprecated
	public Date(String string) 
	{
		super(string);
		// TODO Auto-generated constructor stub
	}

	@Deprecated
	public Date(int year, int month, int day) 
	{
		super(year, month, day);
		// TODO Auto-generated constructor stub
	}

	@Deprecated
	public Date(int year, int month, int day, int hour, int minute) 
	{
		super(year, month, day, hour, minute);
		// TODO Auto-generated constructor stub
	}

	@Deprecated
	public Date(int year, int month, int day, int hour, int minute, int second) 
	{
		super(year, month, day, hour, minute, second);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns this Date as a second value. The value is the number of seconds
	 * since Jan. 1, 1970, midnight GMT.
	 * 
	 * @return the number of seconds since Jan. 1, 1970, midnight GMT.
	 */
	public int getTimeSeconds() 
	{
		return (int) (getTime() / TimeUtils.MILLIS_PER_SECOND);
	}

	/**
	 * Sets this Date to the specified second value. The value is the number of
	 * seconds since Jan. 1, 1970 GMT.
	 * 
	 * @param seconds
	 *            the number of seconds since Jan. 1, 1970 GMT.
	 */
	public void setTime(int seconds) 
	{
		// TODO Auto-generated method stub
		super.setTime(seconds * TimeUtils.MILLIS_PER_SECOND);
	}

}
