package ghost.library.utility;

import ghost.library.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewUtils {

	private ViewUtils()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static void hideProgressView(View progressView, View endView)
	{
		View view = progressView.findViewById(R.id.progress);
		if (null != view)
		{
			view.setVisibility(View.GONE);
		}
		view = progressView.findViewById(R.id.text);
		if (null != view)
		{
			view.setVisibility(View.GONE);
		} 
		if (null != endView && progressView instanceof ViewGroup)
		{
			endView.setId(R.id.list_end);
			((ViewGroup)progressView).addView(endView);
		}
		progressView.setClickable(false);
	}
	
	public static void showProgressView(View progressView)
	{
		if (progressView instanceof ViewGroup)
		{
			((ViewGroup) progressView).removeView(progressView.findViewById(R.id.list_end));
		}
		View view = progressView.findViewById(R.id.progress);
		if (null != view)
		{
			view.setVisibility(View.VISIBLE);
		}
		view = progressView.findViewById(R.id.text);
		if (null != view)
		{
			view.setVisibility(View.VISIBLE);
			if (view instanceof TextView)
			{
				((TextView) view).setText(R.string.loading);
			}
		} 
		progressView.setClickable(false);
	}
	
	public static void showProgressViewClickTip(View progressView)
	{
		if (progressView instanceof ViewGroup)
		{
			((ViewGroup) progressView).removeView(progressView.findViewById(R.id.list_end));
		}
		View view = progressView.findViewById(R.id.progress);
		if (null != view)
		{
			view.setVisibility(View.GONE);
		}
		view = progressView.findViewById(R.id.text);
		if (null != view)
		{
			view.setVisibility(View.VISIBLE);
			if (view instanceof TextView)
			{
				((TextView) view).setText(R.string.tap_to_load);
			}
		}
		progressView.setClickable(true);
	}
	
	public static void showProgressViewUpToLoadTip(View progressView)
	{
		if (progressView instanceof ViewGroup)
		{
			((ViewGroup) progressView).removeView(progressView.findViewById(R.id.list_end));
		}
		View view = progressView.findViewById(R.id.progress);
		if (null != view)
		{
			view.setVisibility(View.GONE);
		}
		view = progressView.findViewById(R.id.text);
		if (null != view)
		{
			view.setVisibility(View.VISIBLE);
			if (view instanceof TextView)
			{
				((TextView) view).setText(R.string.up_to_load);
			}
		}
		progressView.setClickable(true);
	}

}
