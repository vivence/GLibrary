package ghost.library.data;

import ghost.library.concurrent.Task;

public interface IList {
	public boolean fetchNew(Task.IObserver taskObserver);
	public boolean fetchOld(Task.IObserver taskObserver);
	public boolean hasMore();
	public boolean isFetchingNew();
	public boolean isFetchingOld();
}
