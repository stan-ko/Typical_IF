package typical_if.android.util;

/**
 * Created by ADMIN on 26.03.2015.
 */
public class StoppableThread extends Thread {

    public boolean isStopped;

    public StoppableThread(Runnable runnable) {
        super(runnable);
    }

    public void stopThread() {
        isStopped = true;
        interrupt();
    }
}
