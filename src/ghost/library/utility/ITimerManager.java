package ghost.library.utility;

public interface ITimerManager {

	public void attachReceiver(long millis, ITimerReceiver receiver);
	public void detachReceiver(long millis, ITimerReceiver receiver);
	public void clearTimer(long millis);
	public void clearAllTimers();
	
}
