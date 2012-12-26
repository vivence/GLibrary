package ghost.library.ui.widget;

import ghost.library.utility.Log;

import java.util.Set;
import java.util.TreeSet;

import android.widget.SectionIndexer;

public abstract class AlphabetIndexer implements SectionIndexer {
	
	public static final String LOG_TAG = "alphabet_indexer";

	private android.widget.AlphabetIndexer indexer_;
	private AlphabetIndexerView indexerView_;
	
	public abstract int getItemCount();
	public abstract String getIndexName(int position);
	
	public AlphabetIndexer(AlphabetIndexerView indexerView)
	{
		// TODO Auto-generated constructor stub
		indexerView_ = indexerView;
		indexerView_.setIndexer(this);
	}

	protected android.widget.AlphabetIndexer getRawIndexer()
	{
		return indexer_;
	}
	
	protected void updateAlphabetIndexer() 
	{
		Set<String> addedAlpha = new TreeSet<String>();
		StringBuffer sb = new StringBuffer();
		int itemCount = getItemCount();
		for (int i = 0; i < itemCount; ++i) 
		{
			String alpha = getIndexName(i);
			if (!addedAlpha.contains(alpha)) 
			{
				addedAlpha.add(alpha);
				sb.append(alpha);
			}
		}

		indexer_ = new android.widget.AlphabetIndexer(new AlphabetIndexCursor(this), 0, sb.toString());
		indexerView_.setAlphabet(sb.toString());
	}

	// section
	@Override
	public Object[] getSections() 
	{
		// TODO Auto-generated method stub
		android.widget.AlphabetIndexer indexer = getRawIndexer();
		if (null != indexer) 
		{
			return indexer.getSections();
		}
		return null;
	}

	@Override
	public int getPositionForSection(int section) 
	{
		// TODO Auto-generated method stub
		android.widget.AlphabetIndexer indexer = getRawIndexer();
		if (null != indexer) 
		{
			int position = indexer.getPositionForSection(section);
			Log.d(LOG_TAG, "get position(" + position + ") for section("+ section + ")");
			return position;
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) 
	{
		// TODO Auto-generated method stub
		android.widget.AlphabetIndexer indexer = getRawIndexer();
		if (null != indexer) 
		{
			int section = indexer.getSectionForPosition(position);
			Log.d(LOG_TAG, "get section(" + section + ") for position("+ position + ")");
			return section;
		}
		return 0;
	}

}
