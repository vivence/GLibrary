package ghost.library.data;

import ghost.library.concurrent.Task;

public interface IList {
	public boolean isFetchingNew();
	public boolean fetchNew(Task.IObserver taskObserver);
	public boolean isFetchingOld();
	public boolean fetchOld(Task.IObserver taskObserver);
	public boolean hasMore();
	public int getSize();
	public boolean isEmpty();
	public void clear();
}
