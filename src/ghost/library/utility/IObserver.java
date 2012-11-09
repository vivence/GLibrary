package ghost.library.utility;


public interface IObserver {
	public void onAttachTarget(IObserverTarget target);
	public void onDetachTarget(IObserverTarget target);
}
