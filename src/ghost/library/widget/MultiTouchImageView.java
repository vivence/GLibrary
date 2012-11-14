package ghost.library.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class MultiTouchImageView 
	extends ImageView 
	implements 
		GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener,
		ScaleGestureDetector.OnScaleGestureListener
{
	public static final float ZOOM_MIN_FACTOR = 1;
	public static final float ZOOM_MAX_FACTOR = 2;
	public static final float OVERFLOW_SCALE_FACTOR = 0.3f;
	public static final float DEFAULT_MIN_SCALE = 1.0f*ZOOM_MIN_FACTOR;
	public static final float DEFAULT_MAX_SCALE = 1.0f*ZOOM_MAX_FACTOR;

	private Bitmap bmp_ = null;
	private RectF bmpRect_ = null;
	private float optimalScale_ = 1.0f;
	private float minScale_ = DEFAULT_MIN_SCALE;
	private float minOverflowScale_ = minScale_*(1-OVERFLOW_SCALE_FACTOR);
	private float maxScale_ = DEFAULT_MAX_SCALE;
	private float maxOverflowScale_ = maxScale_*(1+OVERFLOW_SCALE_FACTOR);
	private float currentScale_ = 1.0f;
	
	private float startScale_ = 0f;
	private float endScale_ = 0f;
	private PointF scaleCenterPoint_;
	private RectF startRect_;
	private RectF endRect_;
	
	public MultiTouchImageView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MultiTouchImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MultiTouchImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	protected void init(Context context)
	{
		final GestureDetector detector = new GestureDetector(context, this);
		detector.setOnDoubleTapListener(this);
		detector.setIsLongpressEnabled(false);
		
		final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
		
		setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				boolean b1 = detector.onTouchEvent(event);
				boolean b2 = scaleDetector.onTouchEvent(event);
				return b1 || b2;
			}
		});
		
		setScaleType(ScaleType.MATRIX);
		
		Drawable drawable = getDrawable();
		if (null != drawable && drawable instanceof BitmapDrawable)
		{
			reset(((BitmapDrawable)drawable).getBitmap());
		}
	}
	
	public void reset(Bitmap bmp)
	{
		// stop animation
		// stop fling
		bmp_ = bmp;
		if (null != bmp_)
		{
			bmpRect_ = null;
			updateOptimalScale();
			restore();
		}
		else
		{
			bmpRect_ = null;
			optimalScale_ = 1.0f;
			minScale_ = DEFAULT_MIN_SCALE;
			minOverflowScale_ = minScale_*(1-OVERFLOW_SCALE_FACTOR);
			maxScale_ = DEFAULT_MAX_SCALE;
			maxOverflowScale_ = maxScale_*(1+OVERFLOW_SCALE_FACTOR);
			currentScale_ = 1.0f;
			
			startScale_ = 0f;
			endScale_ = 0f;
			scaleCenterPoint_ = null;
			startRect_ = null;
			endRect_ = null;
		}
	}
	
	public void replaceImageBitmap(Bitmap bmp)
	{
		if (null != bmp && null != bmp_)
		{
			float scaleAdjust = (float)bmp_.getWidth()/(float)bmp.getWidth();
			
			RectF rect = bmpRect_;
			RectF bmpRect = new RectF(0.0f, 0.0f, bmp.getWidth(), bmp.getHeight());
			float scale = rect.width()/bmpRect.width();
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			matrix.mapRect(bmpRect);
			float dx = rect.left - bmpRect.left;
			float dy = rect.top - bmpRect.top;
			matrix.postTranslate(dx, dy);
			super.setImageBitmap(bmp);
			setImageMatrix(matrix);
			
			updateOptimalScale();
			
			currentScale_ = scale;
			
			startScale_ *= scaleAdjust;
			endScale_ *= scaleAdjust;
		}
		else
		{
			setImageBitmap(bmp);
		}
	}
	
	public static float calcOptimalScale(
			int contentWidth, int contentHeight, 
			int containerWidth, int containerHeight)
	{
		float scaleX = (float)containerWidth/(float)contentWidth;
		float scaleY = (float)containerHeight/(float)contentHeight;
		return Math.min(scaleX, scaleY);
	}
	
	public float calcCenterTranslateOffsetX()
	{
		return calcCenterTranslateOffsetX(this, bmpRect_);
	}
	
	public float calcCenterTranslateOffsetY()
	{
		return calcCenterTranslateOffsetY(this, bmpRect_);
	}
	
	public static float calcCenterTranslateOffsetX(View view, RectF rect)
	{
		return (view.getWidth()-(int)rect.width())/2.0f-rect.left;
	}
	
	public static float calcCenterTranslateOffsetY(View view, RectF rect)
	{
		return (view.getHeight()-(int)rect.height())/2.0f-rect.top;
	}
	
	public void updateOptimalScale()
	{
		optimalScale_ = calcOptimalScale(
				bmp_.getWidth(), bmp_.getHeight(), 
				getWidth(), getHeight());
		minScale_ = Math.min(DEFAULT_MIN_SCALE, optimalScale_);
		minOverflowScale_ = minScale_*(1-OVERFLOW_SCALE_FACTOR);
		maxScale_ = Math.max(DEFAULT_MAX_SCALE, optimalScale_);
		maxOverflowScale_ = maxScale_*(1+OVERFLOW_SCALE_FACTOR);
	}
	
	public void restore()
	{
		Matrix matrix = new Matrix();
		matrix.postScale(optimalScale_, optimalScale_);
		setImageMatrix(matrix);
		
		postTranslate(calcCenterTranslateOffsetX(), calcCenterTranslateOffsetY());
		
		currentScale_ = optimalScale_;
	}
	
	public void postScale(float scale)
	{
		postScale(scale, null);
	}
	
	public void postScale(float scale, PointF centerPoint)
	{
		float nextScale = currentScale_ * scale;
		if (0 > Float.compare(nextScale, minOverflowScale_))
		{
			scale = minOverflowScale_ / currentScale_;
			nextScale = currentScale_ * scale;
		}
		if (0 < Float.compare(nextScale, maxOverflowScale_))
		{
			scale = maxOverflowScale_ / currentScale_;
			nextScale = currentScale_ * scale;
		}
		
		if (0 > Float.compare(currentScale_, optimalScale_))
		{
			centerPoint = new PointF(getWidth()/2, getHeight()/2);
		}
		
		Matrix matrix = new Matrix(getImageMatrix());
		if (null != centerPoint)
		{
			matrix.postScale(scale, scale, centerPoint.x, centerPoint.y);
		}
		else
		{
			matrix.postScale(scale, scale);
		}
		setImageMatrix(matrix);
		currentScale_ = nextScale;
	}
	
	public void postTranslate(float dx, float dy)
	{
		Matrix matrix = new Matrix(getImageMatrix());
		matrix.postTranslate(dx, dy);
		setImageMatrix(matrix);
	}
	
	@Override
	public void setScaleType(ScaleType scaleType)
	{
		// TODO Auto-generated method stub
		super.setScaleType(ScaleType.MATRIX);
	}
	
	@Override
	public void setImageMatrix(Matrix matrix)
	{
		// TODO Auto-generated method stub
		if (null != bmpRect_)
		{
			bmpRect_.set(0, 0, bmp_.getWidth(), bmp_.getHeight());
			matrix.mapRect(bmpRect_);
		}
		else if (null != bmp_)
		{
			bmpRect_ = new RectF(0, 0, bmp_.getWidth(), bmp_.getHeight());
			matrix.mapRect(bmpRect_);
		}
		super.setImageMatrix(matrix);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm)
	{
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		reset(bm);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable)
	{
		// TODO Auto-generated method stub
		super.setImageDrawable(drawable);
		if (null != drawable && drawable instanceof BitmapDrawable)
		{
			reset(((BitmapDrawable)drawable).getBitmap());
		}
	}
	
	@Override
	public void setImageResource(int resId)
	{
		// TODO Auto-generated method stub
		super.setImageResource(resId);
		Drawable drawable = getDrawable();
		if (null != drawable && drawable instanceof BitmapDrawable)
		{
			reset(((BitmapDrawable)drawable).getBitmap());
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		updateOptimalScale();
		if ((0 < w && 0 < h) && (0 == oldw || 0 == oldh))
		{
//			if (initedMatrix_)
//			{
//				animateRestore();
//			}
//			else
			{
				restore();
			}
		}
	}

	// scale
	
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector)
	{
		// TODO Auto-generated method stub
		// adjust
	}
	
	// click
	
	@Override
	public boolean onDown(MotionEvent e)
	{
		// TODO Auto-generated method stub
		
		// stop animation
		// stop fling
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	// move

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
