package ghost.library.ui.widget;

public interface ITextView {
	public void setText(CharSequence text);
	public void setTextSize(int px);
	public void setTextColor(int color);
	public void setShadowLayer(float radius, float dx, float dy, int color);
}
