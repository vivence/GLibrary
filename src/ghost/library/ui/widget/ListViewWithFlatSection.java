package ghost.library.ui.widget;

import ghost.library.ui.adapter.SectionsAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewWithFlatSection extends ListView implements OnScrollListener {
	
//	public static final int FLAG_SECTION_BACKGROUND_ALPHA = 220;
	
	private SectionsAdapter adapter_;
	private SectionsAdapter.SectionState flatSectionItem_;
	private Bitmap flatSectionBmp_;
	private float flatSectionTop_;
	private Paint paint_;

	public ListViewWithFlatSection(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ListViewWithFlatSection(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ListViewWithFlatSection(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	protected void init(Context context)
	{
		paint_ = new Paint();
//		paint_.setAlpha(FLAG_SECTION_BACKGROUND_ALPHA);
		
		setOnScrollListener(this);
	}
	
	protected boolean switchFlatSection(SectionsAdapter.SectionState item)
	{
		if (flatSectionItem_ == item)
		{
			return false;
		}
		if (null != flatSectionItem_)
		{
			flatSectionItem_.setSectionViewVisiable(true);
		}
		flatSectionItem_ = item;
		if (null != flatSectionItem_)
		{
			flatSectionItem_.setSectionViewVisiable(false);
			flatSectionBmp_ = flatSectionItem_.getSectionViewDrawCache();
		}
		else
		{
			flatSectionBmp_ = null;
		}
		return true;
	}
	
	protected void refreshFlatSection(int firstVisibleItem)
	{
		if (null != adapter_ && 1 < adapter_.getSectionsCount())
		{
			int section = 0;
			int position = 0;
			SectionsAdapter.SectionState sectionState = null;
			
			if (0 == firstVisibleItem)
			{
				View childView = getChildAt(0);
				if (0 != childView.getTop())
				{
					int headerViewCount = getHeaderViewsCount();
					if (getChildAt(headerViewCount).getTop() < 0)
					{
						sectionState = adapter_.getSectionState(0);
					}
				}
			}
			else
			{
				firstVisibleItem -= getHeaderViewsCount();
				Pair<Integer, Integer> sectionAndPosition = adapter_.positionToSectionPosition(firstVisibleItem);
				section = sectionAndPosition.first;
				position = sectionAndPosition.second;
				if (0 == sectionAndPosition.second)
				{
					View childView = getChildAt(0);
					if (0 != childView.getTop())
					{
						sectionState = adapter_.getSectionState(sectionAndPosition.first);
					}
				}
				else
				{
					sectionState = adapter_.getSectionState(sectionAndPosition.first);
				}
			}
			switchFlatSection(sectionState);
			
			if (null != flatSectionBmp_)
			{
				int maxBottom = Integer.MAX_VALUE;
				if (adapter_.getSectionsCount()-1 > section)
				{
					int nextSectionViewIndex = adapter_.getCount(section)-position+1;
					if (firstVisibleItem < getHeaderViewsCount())
					{
						nextSectionViewIndex += getHeaderViewsCount();
					}
					if (0 <= nextSectionViewIndex && getChildCount() > nextSectionViewIndex)
					{
						maxBottom = getChildAt(nextSectionViewIndex).getTop();
					}
				}
				if (maxBottom < flatSectionBmp_.getHeight())
				{
					flatSectionTop_ = maxBottom - flatSectionBmp_.getHeight();
				}
				else
				{
					flatSectionTop_ = 0;
				}
			}
		}
	}
	
	@Override
	public void setAdapter(ListAdapter adapter)
	{
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		if (adapter instanceof SectionsAdapter)
		{
			adapter_ = (SectionsAdapter)adapter;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		// TODO Auto-generated method stub
		refreshFlatSection(firstVisibleItem);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		if (null != flatSectionBmp_)
		{
			canvas.drawBitmap(flatSectionBmp_, 0, flatSectionTop_, paint_);	
		}
	}

}
