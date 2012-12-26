package ghost.library.ui.controller;

public interface IListController {
	public boolean fetchNew();
	public boolean fetchOld();
	public boolean hasMore();
	public boolean isEmpty();
	public void clear();
}
