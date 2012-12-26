package ghost.library.utility;


public interface IObserverTarget {
	
	public static class NotifyMethodParam{
		public Class<?> paramClass;
		public Object param;
		
		public NotifyMethodParam(Class<?> Object, Object param)
		{
			// TODO Auto-generated constructor stub
			this.paramClass = Object;
			this.param = param;
		}
	}
	
	public void addObserver(IObserver observer);
	public void removeObserver(IObserver observer);
	public void clearObservers();
	public void notifyObservers(Class<? extends IObserver> observerInterface, String methodName, NotifyMethodParam... parameters);
}
