package cmpt276.as2.parentapp.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import cmpt276.as2.parentapp.R;

//timer button= start and stop button
public class TimeoutActivity extends AppCompatActivity {
    private static final String INITIAL_TIME = "Initial Time";
    private static final String DURATION_SETTING = "Duration Settings";
    private static final String DURATION_CHOICE = "Duration Choice";


    Button timerButton;
    Button resetButton;
    Button optionButton;
    TextView timeout;
    boolean timerIsRunning;
    int initialTime;
    int timeLeft;
    CountDownTimer timer;

    public static Intent makeIntent(Context context, int initialTime){
        Intent intent = new Intent(context, TimeoutActivity.class);
        intent.putExtra(INITIAL_TIME,initialTime);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        timerButton = findViewById(R.id.StartStopButton);
        resetButton = findViewById(R.id.ResetButton);
        optionButton=findViewById(R.id.OptionButton);
        timerButton.setText("START");
        getDuration();
        timerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!timerIsRunning) {
                    timerButton.setText("STOP");
                    timerIsRunning=true;
                    startTimer();

                }
                else{
                    timerButton.setText("START");
                    timerIsRunning=false;
                    stopTimer();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeLeft=initialTime;
            }
        });

        if(!timerIsRunning) {
            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = TimeoutOptionActivity.makeIntent(TimeoutActivity.this);
                    startActivity(i);
                }
            });
        }
        else{
            Toast.makeText(this,"A timer is running",Toast.LENGTH_SHORT);
        }

    }




    private void stopTimer() {
        timer.cancel();
    }

    private void startTimer() {
        timer= new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                updateTimer();
                timeLeft--;
            }

            @Override
            public void onFinish() {
                //notification()
            }
        }.start();
    }

    private void updateTimer() {
        int minute=timeLeft/60;
        int second=timeLeft%60;
        String timerText=minute+":"+second;
        timeout.setText(timerText);
    }
    public void getDuration(){
        initialTime = TimeoutOptionActivity.getDuration(this)*60;

    }

}
