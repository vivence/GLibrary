package ghost.library.ui.adapter;

import ghost.library.R;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SectionsAdapter extends BaseAdapter {
	
	public static class SectionState{
		private boolean visiable_ = true;
		private View sectionView_;
		private Bitmap drawCache_;
		
		public boolean sectionViewVisiable()
		{
			return visiable_;
		}
		
		private void applyState()
		{
			if (null != sectionView_)
			{
				if (visiable_)
				{
					sectionView_.setVisibility(View.VISIBLE);
				}
				else
				{
					if (null == drawCache_)
					{
						sectionView_.setDrawingCacheEnabled(true);
						sectionView_.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
						Bitmap bmp = sectionView_.getDrawingCache();
						if (null != bmp)
						{
							drawCache_ = bmp.copy(Bitmap.Config.ARGB_8888, true);
						}
						sectionView_.setDrawingCacheEnabled(false);
					}
					sectionView_.setVisibility(View.INVISIBLE);
				}
			}
		}
		
		public void setSectionViewVisiable(boolean visiable)
		{
			visiable_ = visiable;
			applyState();
		}
		
		public View getSectionView()
		{
			return sectionView_;
		}
		
		public void setSectionView(View sectionView)
		{
			if (null != sectionView_)
			{
				sectionView_.setTag(R.id.tag_key_section_state, null);
			}
			sectionView_ = sectionView;
			if (null != sectionView_)
			{
				Object tag = sectionView_.getTag(R.id.tag_key_section_state);
				if (null != tag && tag instanceof SectionState)
				{
					((SectionState)tag).setSectionView(null);
				}
				sectionView_.setTag(R.id.tag_key_section_state, this);
				if (null == drawCache_)
				{
					sectionView_.setDrawingCacheEnabled(true);
					sectionView_.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
					Bitmap bmp = sectionView_.getDrawingCache();
					if (null != bmp)
					{
						drawCache_ = bmp.copy(Bitmap.Config.ARGB_8888, true);
					}
					sectionView_.setDrawingCacheEnabled(false);
				}
				applyState();
			}
		}
		
		public Bitmap getSectionViewDrawCache()
		{
			return drawCache_;
		}
	}
	
	/**
	 * 
	 * @param position the global position
	 * @return [section, position]
	 */
	public Pair<Integer, Integer> positionToSectionPosition(int position)
	{
		int section = 0;
		int sectionsCount = getSectionsCount();
		if (1 < sectionsCount)
		{
			for (int i = 0; i < sectionsCount; i++)
			{
				int count = getCount(i);
				if (position > count)
				{
					position -= count+1; // include section
				}
				else
				{
					section = i;
					break;
				}
			}
		}
		return new Pair<Integer, Integer>(section, position);
	}
	
	@Override
	public final int getCount()
	{
		// TODO Auto-generated method stub
		int sectionsCount = getSectionsCount();
		if (1 < sectionsCount)
		{
			int count = 0;
			for (int i = 0; i < sectionsCount; i++)
			{
				count += getCount(i);
			}
			count += sectionsCount;
			return count;
		}
		else
		{
			return getOriginalCount();
		}
	}
	public abstract int getSectionsCount();
	public abstract int getCount(int section);
	public abstract int getOriginalCount();

	@Override
	public final Object getItem(int position)
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			Pair<Integer, Integer> sectionAndPosition = positionToSectionPosition(position);
			if (0 == sectionAndPosition.second)
			{
				return getSectionItem(sectionAndPosition.first);
			}
			else
			{
				return getItem(sectionAndPosition.first, sectionAndPosition.second-1); // exclude section
			}
		}
		else
		{
			return getOriginalItem(position);
		}
	}
	public Object getSectionItem(int section)
	{
		return null;
	}
	public SectionState getSectionState(int section)
	{
		return null;
	}
	public abstract Object getItem(int section, int position);
	public abstract Object getOriginalItem(int position);

	@Override
	public final long getItemId(int position)
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			Pair<Integer, Integer> sectionAndPosition = positionToSectionPosition(position);
			if (0 == sectionAndPosition.second)
			{
				return getSectionItemId(sectionAndPosition.first);
			}
			else
			{
				return getItemId(sectionAndPosition.first, sectionAndPosition.second-1); // exclude section
			}
		}
		else
		{
			return getOriginalItemId(position);
		}
	}
	public long getSectionItemId(int section)
	{
		return section;
	}
	public abstract long getItemId(int section, int position);
	public abstract long getOriginalItemId(int position);
	
	@Override
	public final View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			Pair<Integer, Integer> sectionAndPosition = positionToSectionPosition(position);
			if (0 == sectionAndPosition.second)
			{
				View sectionView = getSectionView(sectionAndPosition.first, convertView, parent);
				SectionState sectionState = getSectionState(sectionAndPosition.first);
				if (null != sectionState)
				{
					sectionState.setSectionView(sectionView);
				}
				return sectionView;
			}
			else
			{
				return getView(sectionAndPosition.first, sectionAndPosition.second-1, convertView, parent); // exclude section
			}
		}
		else
		{
			return getOriginalView(position, convertView, parent);
		}
	}
	public abstract View getSectionView(int section, View convertView, ViewGroup parent);
	public abstract View getView(int section, int position, View convertView, ViewGroup parent);
	public abstract View getOriginalView(int position, View convertView, ViewGroup parent);
	
	@Override
	public final int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			return getOriginalViewTypeCount() + 1;
		}
		else
		{
			return getOriginalViewTypeCount();
		}
	}
	public int getOriginalViewTypeCount()
	{
		return 1;
	}
	
	@Override
	public final int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			Pair<Integer, Integer> sectionAndPosition = positionToSectionPosition(position);
			if (0 == sectionAndPosition.second)
			{
				return getSectionItemViewType(sectionAndPosition.first);
			}
			else
			{
				return getItemViewType(sectionAndPosition.first, sectionAndPosition.second-1); // exclude section
			}
		}
		else
		{
			return getOriginalItemViewType(position);
		}
	}
	public int getSectionItemViewType(int section)
	{
		return getViewTypeCount()-1;
	}
	public int getItemViewType(int section, int position)
	{
		return 0;
	}
	public int getOriginalItemViewType(int position)
	{
		return 0;
	}
	
	@Override
	public final boolean isEnabled(int position)
	{
		// TODO Auto-generated method stub
		int sectionCount = getSectionsCount();
		if (1 < sectionCount)
		{
			Pair<Integer, Integer> sectionAndPosition = positionToSectionPosition(position);
			if (0 == sectionAndPosition.second)
			{
				return isSectionEnable(sectionAndPosition.first);
			}
			else
			{
				return isEnable(sectionAndPosition.first, sectionAndPosition.second-1); // exclude section
			}
		}
		else
		{
			return isOriginalEnable(position);
		}
	}
	public boolean isSectionEnable(int section)
	{
		return false;
	}
	public boolean isEnable(int section, int position)
	{
		return true;
	}
	public boolean isOriginalEnable(int position)
	{
		return true;
	}

}
