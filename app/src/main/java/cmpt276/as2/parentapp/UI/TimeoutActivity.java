package cmpt276.as2.parentapp.UI;

import static cmpt276.as2.parentapp.model.TimerNotification.TIMER_CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import cmpt276.as2.parentapp.R;
import cmpt276.as2.parentapp.model.NotificationReceiver;
import cmpt276.as2.parentapp.model.RingtonePlayService;

//timer button= start and stop button
public class TimeoutActivity extends AppCompatActivity {
    private static final String TIMER_SITUATION = "timer situation";
    private static final String BACKGROUND_TITLE = "beach";
    private static final int BACKGROUND_SIZE = 7;
    public static MediaPlayer beach_sound;

    ImageView timerAnimation;
    Button timerButton;
    Button resetButton;
    Button optionButton;
    TextView timeoutText;
    TableLayout animationLayout;
    ImageSwitcher background;
    boolean timerIsRunning;
    int initialTime;
    int timeLeft;
    int currentBackground;
    CountDownTimer timer;
    CountDownTimer BackgroundTimer;
//    boolean timerIsPaused;

    int backgroundList[];
    int counter = 0;

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, TimeoutActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setUpMusic();

        initialTime = getDuration();
        timeLeft = initialTime;
        setContentView(R.layout.activity_timeout);

        timerButton = findViewById(R.id.StartStopButton);
        resetButton = findViewById(R.id.ResetButton);
        optionButton = findViewById(R.id.OptionButton);

        timeoutText = findViewById(R.id.timeoutText);
        timeoutText.setTextSize(40);
        timerButton.setText("START");

        backgroundList = new int[9];
        fillBackgroundList();

        timerAnimation = new ImageView(this);
        timerAnimation.setImageResource(R.drawable.round);
        timerAnimation.setAlpha(0.5F);

        background = (ImageSwitcher) findViewById(R.id.background);
        background = findViewById(R.id.background);
        background.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_XY);
                return myView;
            }
        });
        changeBackground(0);

        animationLayout = findViewById(R.id.animationLayout);
        animationLayout.addView(timerAnimation);

        currentBackground=-1;

        timerButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                if (!timerIsRunning) {
                    startTimer();
                    timerIsRunning = true;
                    timerButton.setText("STOP");
                    optionButton.setAlpha(0);

                } else {
                    stopTimer();
                    timerIsRunning = false;
                    timerButton.setText("START");
                    optionButton.setAlpha(1);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer!=null){
                    timer.cancel();
                    timeLeft = initialTime;
                    timerIsRunning = false;
                    optionButton.setAlpha(1);
                    timerButton.setText("START");
                    updateTimer(timeLeft);
                    rotate(0);
                    changeBackground(0);
                }


            }
        });

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = TimeoutOptionActivity.makeIntent(TimeoutActivity.this);
                startActivity(i);

            }
        });


    }

    private void changeBackground(int i) {

        if(i!=currentBackground){

            background.setImageResource(backgroundList[i]);
            Animation in = AnimationUtils.loadAnimation(TimeoutActivity.this, android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(TimeoutActivity.this, android.R.anim.fade_out);
            background.setInAnimation(in);
            background.setOutAnimation(out);
        }
        currentBackground=i;


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getDuration() != initialTime) {
            initialTime = getDuration();
            timeLeft = initialTime;
            updateTimer(timeLeft);
        } else {
            updateTimer(timeLeft);
        }

    }




    private void stopTimer() {
//        timerIsPaused=true;
        timerIsRunning=false;
        beach_sound.pause();
        timer.cancel();
    }

    private void setUpMusic(){
        beach_sound = MediaPlayer.create(TimeoutActivity.this,R.raw.beach_sound);


    }

    private void startTimer() {
//        timerIsPaused=false;
        beach_sound.start();
        beach_sound.setLooping(true);

        changeBackground((initialTime - timeLeft) * 8 / initialTime);
        int x= timeLeft;
        timer = new CountDownTimer(x * 1000, 1000) {
            public void onTick(long secondsLeft) {
                timeLeft = ((int) secondsLeft) / 1000;
                updateTimer((int) secondsLeft / 1000);
                float angle = timeLeft * 360 / initialTime;
                rotate(angle);
                timerAnimation.setMaxWidth(animationLayout.getWidth());
                timerAnimation.setMaxHeight(animationLayout.getHeight());
                changeBackground((initialTime - timeLeft) * 8 / initialTime);


            }

            public void onFinish() {
                timeoutText.setText("done!");
                timerIsRunning = false;
                timerButton.setText("START");
                optionButton.setAlpha(1);
                timeLeft = initialTime;
                sendTimerNotification();
            }
        };
        timer.start();
    }


    public void fillBackgroundList() {
        backgroundList[0] = R.drawable.beach1;
        backgroundList[1] = R.drawable.beach2;
        backgroundList[2] = R.drawable.beach3;
        backgroundList[3] = R.drawable.beach4;
        backgroundList[4] = R.drawable.beach5;
        backgroundList[5] = R.drawable.beach6;
        backgroundList[6] = R.drawable.beach7;
        backgroundList[7] = R.drawable.beach8;
        backgroundList[8] = R.drawable.beach9;
    }


    private void rotate(float angle) {
        timerAnimation.setRotation(angle * -1);
    }


    private void updateTimer(int l) {
        int minute = l / 60;
        int second = l % 60;
        timeoutText.setText(getString(R.string.timerTextFormat, minute, second));
    }

    public int getDuration() {
        int newTime = TimeoutOptionActivity.getDuration(this);
        return newTime;
    }

    public void sendTimerNotification() {
        beach_sound.pause();
        String title = getString(R.string.timerNotificationTitle);
        String message = getString(R.string.timerNotificationDescription);
        String actionButtonText = getString(R.string.notificationActionButtonText);
        int notificationID = 1;

        Intent notificationReceiverBroadcast = new Intent(this, NotificationReceiver.class);
        notificationReceiverBroadcast.putExtra("notification_id", notificationID);
        PendingIntent actionButtonIntent = PendingIntent.getBroadcast(this, 0,
                notificationReceiverBroadcast, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_timer_24)
                .setContentTitle(title)
                .setColor(Color.GREEN)
                .setContentText(message)
                .addAction(R.drawable.ic_sharp_clear_24, actionButtonText, actionButtonIntent)
                .setFullScreenIntent(actionButtonIntent, true)
                .setDeleteIntent(actionButtonIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, builder.build());

        Intent stopAlarmIntent = new Intent(TimeoutActivity.this, RingtonePlayService.class);
        TimeoutActivity.this.stopService(stopAlarmIntent);
        Intent startAlarmIntent = new Intent(TimeoutActivity.this, RingtonePlayService.class);
        TimeoutActivity.this.startService(startAlarmIntent);
    }

////https://gist.github.com/codinginflow/61e9cec706e7fe94b0ca3fffc0253bf2
    @Override
    protected void onStop() {
        super.onStop();

        beach_sound.pause();
        SharedPreferences prefs = getSharedPreferences(TIMER_SITUATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long endTime = System.currentTimeMillis() + timeLeft * 1000;
        editor.putInt("timeLeft", timeLeft);
        editor.putBoolean("timerIsRunning", timerIsRunning);
        editor.putInt("initialTime", initialTime);
        editor.putLong("endTime", endTime);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpMusic();
        SharedPreferences prefs = getSharedPreferences(TIMER_SITUATION, MODE_PRIVATE);

        timerIsRunning = prefs.getBoolean("timerIsRunning", false);

        if (timerIsRunning) {
            initialTime=prefs.getInt("initialTime",0);
            timerButton.setText("STOP");
            long endTime = prefs.getLong("endTime", 0);
            timeLeft=(int)(endTime -  System.currentTimeMillis())/1000;
            if(timeLeft<0){
                timerIsRunning=false;
                timerButton.setText("START");
                timeLeft=initialTime;
                updateTimer(timeLeft);
                if(timer!=null){
                    timer.cancel();
                    updateTimer(0);
                }

            }
            else{
                startTimer();

            }
        }
        else{
            if(getDuration() != initialTime) {
                initialTime = getDuration();
                timeLeft = initialTime;
                updateTimer(timeLeft);
            } else {
                timeLeft=prefs.getInt("timeLeft",4);
                updateTimer(timeLeft);
            }


        }
    }
//

}
