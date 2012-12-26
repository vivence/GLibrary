package ghost.library.ui;

import ghost.library.concurrent.Task;
import ghost.library.concurrent.Task.IObserver;
import ghost.library.concurrent.Task.State;
import android.os.Handler;

public abstract class TaskObserver extends ObserverHelper implements IObserver {
	
	private Handler uiHandler_;

	public TaskObserver(Handler uiHandler)
	{
		// TODO Auto-generated constructor stub
		uiHandler_ = uiHandler;
	}

	@Override
	public final void onStateChanged(final Task task, final State oldState, final State newState)
	{
		// TODO Auto-generated method stub
		uiHandler_.post(new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				onStateChangedOnUIThread(task, oldState, newState);
			}
		});
	}
	protected abstract void onStateChangedOnUIThread(Task task, State oldState, State newState);

	@Override
	public final void onProgress(final Task task, final int current, final int max)
	{
		// TODO Auto-generated method stub
		uiHandler_.post(new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				doOnProgressOnUIThread(task, current, max);
			}
		});
	}
	protected abstract void doOnProgressOnUIThread(Task task, int current, int max);

}
