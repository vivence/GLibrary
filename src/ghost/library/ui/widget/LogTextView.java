package ghost.library.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

public class LogTextView extends TextView {

	public LogTextView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LogTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LogTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected MovementMethod getDefaultMovementMethod() 
	{
        return ScrollingMovementMethod.getInstance();
    }

    @Override
    public Editable getText() 
    {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) 
    {
        super.setText(text, BufferType.EDITABLE);
    }

}
