package com.gmail.absolutevanillahelp.quicktap;

/**
 * Created by derekzhang on 8/10/15.
 */
public class TapDecider extends Thread {

    private final QuickTapActivity quickTapActivity;
    private volatile boolean canTap;

    public TapDecider(QuickTapActivity quickTapActivity) {

        this.quickTapActivity = quickTapActivity;
        canTap = true;
    }

    public boolean canTap() {

        return canTap;
    }

    @Override
    public void run() {

        try {
            while (!isInterrupted()) {

                Thread.sleep(((long) (Math.random() * 31)) * 100 + 1000);

                canTap = false;
                quickTapActivity.playCanTapSound(false);
                quickTapActivity.setTouchViewText("DON'T TAP");

                Thread.sleep(((long) (Math.random() * 51)) * 10 + 500);

                canTap = true;
                quickTapActivity.playCanTapSound(true);
                quickTapActivity.setTouchViewText("TAP");
            }
        }
        catch (InterruptedException ignore) {
        }
    }
}
