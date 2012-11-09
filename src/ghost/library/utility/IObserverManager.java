package ghost.library.utility;


public interface IObserverManager {

	public void attachObserver(IObserver observer, IObserverTarget target);
	public void detachObserver(IObserver observer, IObserverTarget target);
	
}
