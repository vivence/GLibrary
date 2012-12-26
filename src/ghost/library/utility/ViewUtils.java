package ghost.library.utility;

import ghost.library.R;
import ghost.library.ui.widget.ITextView;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewUtils {

	private ViewUtils()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static void applyTextViewAttributes(ITextView view, Context context, AttributeSet attrs, int defStyle)
	{
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TextView, defStyle, 0);
		view.setText(a.getText(R.styleable.TextView_text));
		view.setTextSize(a.getDimensionPixelSize(R.styleable.TextView_text_size, 0));
		view.setTextColor(a.getColor(R.styleable.TextView_text_color, 0));
		view.setShadowLayer(
				a.getFloat(R.styleable.TextView_shadow_radius, 0.0f), 
				a.getFloat(R.styleable.TextView_shadow_dx, 0.0f), 
				a.getFloat(R.styleable.TextView_shadow_dy, 0.0f), 
				a.getColor(R.styleable.TextView_shadow_color, 0));
		a.recycle();
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
