package ghost.library.ui.widget;

import android.view.ViewGroup;

public interface IFullScreenView {
	public void open(ViewGroup faceView, Runnable runnableAfterOpened);
	public void close(ViewGroup faceView, Runnable runnableAfterClosed);
}
