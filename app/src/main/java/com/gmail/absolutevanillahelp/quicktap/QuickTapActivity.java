package com.gmail.absolutevanillahelp.quicktap;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.media.*;
import android.os.*;
import android.widget.*;

import com.gmail.absolutevanillahelp.quicktap.util.TimeConverter;

/**
 * Created by derekzhang on 8/10/15.
 */
public class QuickTapActivity extends Activity {

    public final static String TAG_PLAYER_1_NAME = "player_1_name";
    public final static String TAG_PLAYER_2_NAME = "player_2_name";
    public final static String TAG_TIME_LIMIT = "time_limit";

    private final static int MAX_PROGRESS = 10000;

    private VerticalProgressBar battleBar;

    private TextView player1TimeText;
    private TextView player2TimeText;

    private TextView player1TouchView;
    private TextView player2TouchView;

    private TapDecider tapDecider;

    private HandlerThread tapHandlerThread;
    private Handler tapHandler;

    private ReadySetGo readySetGo;

    private ResetableCountdownTimer countdownTimer;

    private volatile boolean timerFinished;

    private int progress;

    private AlertDialog menuDialog;

    private boolean onFirstRun;

    private long player1Score;
    private long player2Score;

    private SoundPool soundPool;
    private int[] soundIDs;
    private volatile boolean stopSoundLoaded;
    private volatile boolean tapSoundLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.switap_activity);

        //locks the activity in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //sets up the battle bar
        battleBar = findViewById(R.id.battle_bar);
        battleBar.setMax(MAX_PROGRESS);

        //initiates variable responsible for tracking progress
        progress = MAX_PROGRESS / 2;

        //progress is half because players have to start even
        battleBar.setProgress(progress);

        Intent intent = getIntent();

        //sets up the player name views for identifying who is who
        ((TextView) findViewById(R.id.player1_name_label)).setText(intent.getStringExtra(TAG_PLAYER_1_NAME));
        ((TextView) findViewById(R.id.player2_name_label)).setText(intent.getStringExtra(TAG_PLAYER_2_NAME));

        //sets up time label that is primarily used to display time remaining in game
        player1TimeText = findViewById(R.id.player1_time_label);
        player2TimeText = findViewById(R.id.player2_time_label);

        //sets up the touch views that players use
        player1TouchView = findViewById(R.id.player1_touch_view);
        player2TouchView = findViewById(R.id.player2_touch_view);

        //disable user interaction to touch views at first
        toggleTouchView(false);

        //instantiates the tap decider thread
        tapDecider = new TapDecider(this);

        createTapHandler();

        player1TouchView.setOnClickListener(v -> {

            if (tapHandler != null) {

                final boolean canTap = tapDecider.canTap();
                tapHandler.post(() -> {

                    long original = player1Score;
                    if (canTap) {

                        player1Score++;
                    }
                    else {

                        for (int i = 0; player1Score > 1 && i < 2; i++) {

                            player1Score--;
                        }
                    }

                    if (player1Score != original) {

                        updateBattleBar();
                    }
                });
            }
        });
        player2TouchView.setOnClickListener(v -> {

            if (tapHandler != null) {

                final boolean canTap = tapDecider.canTap();
                tapHandler.post(() -> {

                    long original = player2Score;
                    if (canTap) {

                        player2Score++;
                    }
                    else {

                        for (int i = 0; player2Score > 1 && i < 2; i++) {

                            player2Score--;
                        }
                    }
                    if (player2Score != original) {

                        updateBattleBar();
                    }
                });
            }
        });

        countdownTimer = new ResetableCountdownTimer(intent.getLongExtra(TAG_TIME_LIMIT, 30 * 1000), 1000);

        menuDialog = new AlertDialog.Builder(QuickTapActivity.this)
                .setTitle("QUICKTAP GAME PAUSED")
                .setItems(R.array.menu_options, (dialog, which) -> {

                    if (which == 0) {

                        setTouchViewText("TAP");
                        createTapHandler();
                        tapDecider = new TapDecider(QuickTapActivity.this);
                        countdownTimer = countdownTimer.reset();
                        readySetGo = new ReadySetGo();
                        readySetGo.start();
                    } else if (which == 1) {

                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .create();

        findViewById(R.id.menu_button).setOnClickListener(v -> onBackPressed());

        onFirstRun = true;

        player1Score = 1;
        player2Score = 1;

        stopSoundLoaded = false;
        tapSoundLoaded = false;
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!onFirstRun) {

            menuDialog.show();
        }

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {

            if (status == 0 && soundIDs != null && soundIDs.length >= 2) {

                if (sampleId == soundIDs[0]) {

                    stopSoundLoaded = true;
                } else if (sampleId == soundIDs[1]) {

                    tapSoundLoaded = true;
                }
            }
        });
        soundIDs = new int[]{soundPool.load(this, R.raw.stop, 1),
                soundPool.load(this, R.raw.tap, 1)};
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (onFirstRun) {

            if (hasFocus) {

                readySetGo = new ReadySetGo();
                readySetGo.start();
            }
            onFirstRun = false;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (menuDialog.isShowing()) {

            menuDialog.dismiss();
        }

        terminateProtocol();

        stopSoundLoaded = false;
        tapSoundLoaded = false;
        soundPool.release();
    }

    private void createTapHandler() {

        tapHandlerThread = new HandlerThread("TapHandlerThread");
        tapHandlerThread.start();

        tapHandler = new Handler(tapHandlerThread.getLooper());
    }

    private void terminateProtocol() {

        tapHandler = null;
        tapHandlerThread.quit();
        tapDecider.interrupt();
        readySetGo.cancel();
        countdownTimer.cancel();
    }

    @Override
    public void onBackPressed() {

        runOnUiThread(() -> toggleTouchView(false));
        terminateProtocol();
        menuDialog.show();
    }

    public void playCanTapSound(boolean canTap) {

        if (canTap) {

            if (tapSoundLoaded) {

                soundPool.play(soundIDs[1], 1, 1, 0, 0, 1);
            }
        }
        else if (stopSoundLoaded) {

            soundPool.play(soundIDs[0], 1, 1, 0, 0, 1);
        }
    }

    public void setTouchViewText(final String text) {

        runOnUiThread(() -> {

            player1TouchView.setText(text);
            player2TouchView.setText(text);
        });
    }

    private void setTimeDisplay(String time) {

        player1TimeText.setText(time);
        player2TimeText.setText(time);
    }

    private void toggleTouchView(boolean clickable) {

        player1TouchView.setEnabled(clickable);
        player2TimeText.setEnabled(clickable);
    }

    private void updateBattleBar() {

        long combined = player1Score + player2Score;
        if (combined == Long.MAX_VALUE || combined < 2) {

            player1Score = progress == 0 ? 1 : progress;
            player2Score = MAX_PROGRESS - progress == 0 ? 1 : MAX_PROGRESS - progress;
        }
        else {

            progress = (int) ((double) player1Score / (combined) * 10000);
            final int progress = this.progress;
            runOnUiThread(() -> battleBar.setProgress(progress));
        }
    }

    @Override
    public void finish() {

        if (timerFinished) {
            
            terminateProtocol();
            if (battleBar.getProgress() == MAX_PROGRESS / 2) {

                finisher("It's a tie!");
            } else {

                String winner;
                if (battleBar.getProgress() < MAX_PROGRESS / 2) {

                    winner = getIntent().getStringExtra(TAG_PLAYER_2_NAME);
                } else {

                    winner = getIntent().getStringExtra(TAG_PLAYER_1_NAME);
                }

                finisher(winner + " won!");
            }
        }
        else {

            super.finish();
        }
    }

    private void finisher(String message) {

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton("Ok", (dialog, which) -> {

                    dialog.dismiss();
                    QuickTapActivity.super.finish();
                })
                .setCancelable(false)
                .show();
    }

    private class ResetableCountdownTimer extends CountDownTimer {

        private long totalTimeLeft;
        private final long interval;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ResetableCountdownTimer(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);
            totalTimeLeft = millisInFuture;
            interval = countDownInterval;
            timerFinished = false;
        }

        private ResetableCountdownTimer(ResetableCountdownTimer resetableCountdownTimer) {

            super(resetableCountdownTimer.totalTimeLeft, resetableCountdownTimer.interval);
            totalTimeLeft = resetableCountdownTimer.totalTimeLeft;
            interval = resetableCountdownTimer.interval;
            timerFinished = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {

            totalTimeLeft = millisUntilFinished;

            String str = TimeConverter.toString(millisUntilFinished);
            final String time = str.substring(0, 2) + ":" + str.substring(2,4);
            runOnUiThread(() -> setTimeDisplay(time));
        }

        @Override
        public void onFinish() {

            timerFinished = true;

            totalTimeLeft = 0;

            runOnUiThread(() -> {

                setTimeDisplay("00:00");
                toggleTouchView(false);
                finish();
            });
        }

        public ResetableCountdownTimer reset() {

            return new ResetableCountdownTimer(this);
        }
    }

    private class ReadySetGo extends CountDownTimer {

        private int counter;

        public ReadySetGo() {

            super(3100, 1000);
            counter = 0;
        }

        @Override
        public void onTick(long millisUntilFinished) {

            final String message;
            if (counter == 0) {

                message = "READY";
            }
            else if (counter == 1) {

                message = "SET...";
            }
            else if (counter == 2) {

                message = "GO!";
            }
            else {

                message = "";
            }

            runOnUiThread(() -> setTimeDisplay(message));

            counter++;
        }

        @Override
        public void onFinish() {

            tapDecider.start();
            countdownTimer.start();

            runOnUiThread(() -> {

                toggleTouchView(true);
                playCanTapSound(tapDecider.canTap());
            });

            counter = 0;
        }
    }
}