package ghost.library.ui.widget;

import ghost.library.R;
import ghost.library.utility.ViewUtils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class AlphabetIndexerView extends View implements ITextView {
	
	public static final int MAX_LOOP_COUNT = 10;
	public static final int MIN_ITEM_COUNT = 25;
	public static final int OVERLAY_SHOW_DURATION = 1000*1; // ms
	
	private String alphabet_;
	private TextPaint textPaint_;
	private float fontHeight_;
	
	private float fontMaxSize_;
	
	private ListView listView_;
	private AlphabetIndexer indexer_;
	
	private View overlay_;
	private Runnable hideOverlay_ = new Runnable() {
		
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			AlphabetIndexerView.this.hideOverlay();
		}
	};
	
	private boolean paintParamsChanged_ = false;

	public AlphabetIndexerView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public AlphabetIndexerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
		initCustomAttrs(context, attrs, 0);
	}

	public AlphabetIndexerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
		initCustomAttrs(context, attrs, defStyle);
	}
	
	protected void init()
	{
		final Resources res = getResources();
//        final CompatibilityInfo compat = res.getCompatibilityInfo();

        textPaint_ = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint_.density = res.getDisplayMetrics().density;
        textPaint_.setTextAlign(Align.CENTER);
        
        FontMetrics fm = textPaint_.getFontMetrics();
        fontHeight_ = fm.bottom-fm.top;
	}
	
	protected void initCustomAttrs(Context context, AttributeSet attrs, int defStyle) 
	{
		ViewUtils.applyTextViewAttributes(this, context, attrs, defStyle);
	}
	
	@Override
	public void setTextSize(int size)
	{
		Context context = getContext();
        Resources resources = null;

        if (null == context)
        {
        	resources = Resources.getSystem();
        }
        else
        {
        	resources = context.getResources();
        }

        fontMaxSize_ = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, resources.getDisplayMetrics());
        textPaint_.setTextSize(fontMaxSize_);
        
        paintParamsChanged_ = true;
	}
	
	@Override
	public void setTextColor(int color)
	{
		textPaint_.setColor(color);
	}

	@Override
	public void setText(CharSequence text)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShadowLayer(float radius, float dx, float dy, int color)
	{
		// TODO Auto-generated method stub
		textPaint_.setShadowLayer(radius, dx, dy, color);
	}
	
	protected void updatePaintParams()
	{
		if (TextUtils.isEmpty(alphabet_))
		{
			return;
		}
		int height = getHeight();
		if (0 >= height)
		{
			return;
		}
		FontMetrics fm = textPaint_.getFontMetrics();
        fontHeight_ = fm.bottom-fm.top;
		int partHeight = height / alphabet_.length();
		int loopCount = 0;
		if (partHeight < fontHeight_)
		{
			do
			{
				float textSize = textPaint_.getTextSize()-1;
				if (!(0 < Float.compare(textSize, 0.0f)))
				{
					break;
				}
				textPaint_.setTextSize(textSize);
				fm = textPaint_.getFontMetrics();
		        fontHeight_ = fm.bottom-fm.top;
		        ++loopCount;
			}
			while (partHeight < fontHeight_ && loopCount < MAX_LOOP_COUNT);
		}
		else
		{
			do
			{
				float textSize = textPaint_.getTextSize()+1;
				if (0 > Float.compare(fontMaxSize_, textSize))
				{
					break;
				}
				textPaint_.setTextSize(textSize);
				fm = textPaint_.getFontMetrics();
		        fontHeight_ = fm.bottom-fm.top;
		        ++loopCount;
			}
			while (partHeight > fontHeight_ && loopCount < MAX_LOOP_COUNT);
		}
	}
	
	public void setListView(ListView listView)
	{
		listView_  = listView;
		if (listView_.getCount() > MIN_ITEM_COUNT)
		{
			setVisibility(View.VISIBLE);
			paintParamsChanged_ = true;
		}
		else
		{
			setVisibility(View.GONE);
		}
	}
	
	public void setIndexer(AlphabetIndexer indexer)
	{
		indexer_ = indexer;
	}
	
	public void setAlphabet(String alphabet)
	{
		alphabet_ = alphabet;
		if (!TextUtils.isEmpty(alphabet_) 
				&& 1 < alphabet.length()
				&& null != listView_ && listView_.getCount() > MIN_ITEM_COUNT)
		{
			setVisibility(View.VISIBLE);
			paintParamsChanged_ = true;
		}
		else
		{
			setVisibility(View.GONE);
		}
	}
	
	protected View createOverlayView()
	{
		LayoutInflater inflate = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	return inflate.inflate(R.layout.index_overlay, null);
	}
	protected TextView findOverlayTextView(View overlayView)
	{
		return (TextView)overlayView;
	}
	protected WindowManager.LayoutParams createOverlayLayoutParams()
	{
		int overlaySize = getResources().getDimensionPixelSize(R.dimen.index_over_size);
		return new WindowManager.LayoutParams(
    			overlaySize, overlaySize,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
	}
	
	private void showOverlay(String text)
	{
		if (null == overlay_)
		{
			overlay_ = createOverlayView();
			TextView overTextView = findOverlayTextView(overlay_);
			overTextView.setText(text);
        	
        	WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        	windowManager.addView(overlay_, createOverlayLayoutParams());
		}
		else
		{
			TextView overTextView = findOverlayTextView(overlay_);
			overTextView.setText(text);
		}
	}
	
	private void hideOverlay()
	{
		if (null != overlay_)
		{
			WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
			windowManager.removeView(overlay_);
			overlay_ = null;
		}
	}
	
	@Override
	protected void onDetachedFromWindow()
	{
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		hideOverlay();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if (oldh != h)
		{
			paintParamsChanged_ = true;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if (!TextUtils.isEmpty(alphabet_))
		{
			int height = getHeight();
			if (0 < height)
			{
				if (paintParamsChanged_)
				{
					updatePaintParams();
				}
				int partHeight = height / alphabet_.length();
				FontMetrics fm = textPaint_.getFontMetrics();
				float baseX = getWidth()/2;
				float baseY = partHeight - (partHeight - fontHeight_)/2 - fm.bottom; 
				char[] chars = alphabet_.toCharArray();
				for (char c : chars)
				{
					canvas.drawText(String.valueOf(c), baseX, baseY, textPaint_);
					baseY += partHeight;
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		boolean ret = super.onTouchEvent(event);
		
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (null != listView_ && null != indexer_)
			{
				int height = getHeight();
				int partHeight = height / alphabet_.length();
				if (0 < partHeight)
				{
					int section = (int)(event.getY() / partHeight);
					if (0 <= section && alphabet_.length() > section)
					{
//						int currentSection = -1;
//						int currentPosition = listView_.getFirstVisiblePosition();
//						if (0 <= currentPosition)
//						{
//							currentSection = indexAdapter_.getSectionForPosition(currentPosition);
//						}
//						if (currentSection != section)
						{
							int position = indexer_.getPositionForSection(section);
							listView_.setSelection(position+listView_.getHeaderViewsCount());
						}
						if (alphabet_.length() > section)
						{
							showOverlay(String.valueOf(alphabet_.charAt(section)));
							removeCallbacks(hideOverlay_);
							postDelayed(hideOverlay_, OVERLAY_SHOW_DURATION);
						}
					}
				}
			}
			break;
		default:
			break;
		}	
		
		return ret;
	}

}
