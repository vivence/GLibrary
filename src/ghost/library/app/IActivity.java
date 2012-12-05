package ghost.library.app;

import android.view.View;

public interface IActivity {
	public void initActionBar();
	public Runnable getRunnableAfterSplashOpened();
	public void openSplash();
	public void closeSplash(Runnable runnableAfterSplashClosed);
	public int getStatusBarHeight();
	public View getFaceView();
	public View getFullScreenView();
	public boolean openFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewOpened);
	public boolean closeFullScreenView(View fullScreenView, final Runnable runnableAfterFullScreenViewClosed);
}
