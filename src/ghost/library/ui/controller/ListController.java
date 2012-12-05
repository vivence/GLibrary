package ghost.library.ui.controller;

import ghost.library.concurrent.Task;
import ghost.library.concurrent.Task.State;
import ghost.library.data.IList;
import ghost.library.ui.ObserverHelper;
import ghost.library.utility.ViewUtils;
import junit.framework.Assert;
import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ListController extends ObserverHelper implements IListController, OnScrollListener, Task.IObserver
{
	private IList dataList_;
	private ListView listView_;
	private View listEndView_;
	private BaseAdapter adapter_;

	public ListController(IList dataList, ListView listView)
	{
		// TODO Auto-generated constructor stub
		Assert.assertNotNull(listView);
		listView_ = listView;
		listView_.setOnScrollListener(this);
		// add footer
		listEndView_ = createListEndViewView();
		listView_.addFooterView(createListEndViewView(), null, true);
		getListEndView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				ListController.this.fetchOld();
			}
		});
		ViewUtils.hideProgressView(getListEndView(), null);
		
		setDataList(dataList);
	}
	
	protected abstract View createListEndViewView();
	
	public void setDataList(IList dataList)
	{
		if (null != dataList_ && dataList_ == dataList)
		{
			return;
		}
		dataList_ = dataList;
		adapter_ = createAdapter(dataList);
		listView_.setAdapter(adapter_);
	}
	
	public IList getDataList()
	{
		return dataList_;
	}
	
	public BaseAdapter getAdapter()
	{
		return adapter_;
	}
	
	public ListView getListView()
	{
		return listView_;
	}
	
	public View getListEndView()
	{
		return listEndView_;
	}
	
	protected void onTaskBegin(Task task)
	{
		
	}

	protected void onTaskProgress(Task task, int current, int max)
	{
		
	}
	
	protected abstract boolean isTaskSucceed(Task task);
	protected abstract void doInForeground(Runnable runnable);
	
	protected void onTaskSucceedInBackground(final Task task)
	{
		doInForeground(new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				ListController.this.onTaskSucceedInForeground(task);
			}
		});
	}
	
	protected void onTaskSucceedInForeground(Task task)
	{
		if (!hasMore())
		{
			ViewUtils.hideProgressView(getListEndView(), createNoMoreView());
		}
		else
		{
			ViewUtils.showProgressViewClickTip(getListEndView());
		}
	}
	
	protected void onTaskFailedInBackground(final Task task)
	{
		doInForeground(new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				ListController.this.onTaskFailedInForeground(task);
			}
		});
	}
	
	protected void onTaskFailedInForeground(Task task)
	{
		ViewUtils.showProgressViewClickTip(getListEndView());
	}
	
	protected abstract BaseAdapter createAdapter(IList dataList);
	protected abstract View createNoMoreView();
	
	@Override
	public boolean fetchNew()
	{
		if (null == dataList_)
		{
			return false;
		}
		if (dataList_.fetchNew(this))
		{
			if (0 >= adapter_.getCount())
			{
				ViewUtils.showProgressView(getListEndView());
			}
			return true;
		}
		return false;
	}
	@Override
	public boolean fetchOld()
	{
		if (null == dataList_)
		{
			return false;
		}
		if (0 >= adapter_.getCount())
		{
			return fetchNew();
		}
		if (dataList_.fetchOld(this))
		{
			ViewUtils.showProgressView(getListEndView());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasMore()
	{
		// TODO Auto-generated method stub
		if (null == dataList_)
		{
			return false;
		}
		return dataList_.hasMore();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		// TODO Auto-generated method stub
		switch (scrollState)
		{
		case SCROLL_STATE_TOUCH_SCROLL:
			if (hasMore() && !dataList_.isFetchingNew() && !dataList_.isFetchingOld())
			{
				ViewUtils.showProgressViewUpToLoadTip(getListEndView());
			}
			break;
		case SCROLL_STATE_FLING:
			break;
		case SCROLL_STATE_IDLE:
			if (hasMore() && view.getLastVisiblePosition() >= view.getCount()-3)
			{
				fetchOld();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStateChanged(Task task, State oldState, State newState)
	{
		// TODO Auto-generated method stub
		switch (newState)
		{
		case EXECUTING:
			ListController.this.onTaskBegin(task);
			break;
		case FINISHED:
			if (!(task.isCanceled() || task.isAborted()) && ListController.this.isTaskSucceed(task))
			{
				ListController.this.onTaskSucceedInBackground(task);
			}
			else 
			{
				ListController.this.onTaskFailedInBackground(task);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgress(Task task, int current, int max)
	{
		// TODO Auto-generated method stub
		ListController.this.onTaskProgress(task, current, max);
	}

	@Override
	protected Context getAttachedContext()
	{
		// TODO Auto-generated method stub
		return listView_.getContext();
	}
	
}
