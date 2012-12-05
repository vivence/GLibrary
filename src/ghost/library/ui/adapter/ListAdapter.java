package ghost.library.ui.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.widget.BaseAdapter;

public abstract class ListAdapter<T> extends BaseAdapter {
	
	protected List<T> list_;

	public ListAdapter(List<T> list)
	{
		// TODO Auto-generated constructor stub
		setList(list);
	}
	
	public void setList(List<T> list)
	{
		list_ = list;
	}
	
	/**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) 
    {
    	if (null == list_)
		{
    		list_ = new ArrayList<T>();
		}
    	list_.add(object);
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) 
    {
    	if (null == list_)
		{
    		list_ = new ArrayList<T>();
		}
    	list_.addAll(collection);
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T ... items) 
    {
    	if (null == list_)
		{
    		list_ = new ArrayList<T>();
		}
        Collections.addAll(list_, items);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void insert(T object, int index) 
    {
    	if (null == list_)
		{
    		list_ = new ArrayList<T>();
		}
    	list_.add(index, object);
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) 
    {
    	if (null != list_)
		{
    		list_.remove(object);
		}
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() 
    {
    	if (null != list_)
		{
    		list_.clear();
		}
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *        in this adapter.
     */
    public void sort(Comparator<? super T> comparator) 
    {
    	if (null != list_)
		{
        	Collections.sort(list_, comparator);
		}
    }

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return null != list_ ? list_.size() : 0;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null != list_ ? list_.get(position) : null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

}
