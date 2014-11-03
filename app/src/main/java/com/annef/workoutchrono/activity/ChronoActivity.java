package com.annef.workoutchrono.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.annef.workoutchrono.R;
import com.annef.workoutchrono.dao.WorkoutDAO;
import com.annef.workoutchrono.model.Exercise;
import com.annef.workoutchrono.model.ExerciseType;
import com.annef.workoutchrono.model.Workout;

public class ChronoActivity extends Activity {

    int currentposition;
    Exercise currentExo;
    TextView exerciseName;
    TextView timerLabel;
    TextView pathToPic;
    ImageView pic;
    Button next;
    Button cancel;
    List<Exercise> workoutSequence;

    private Button pause;
    MyCountdownTimer countdownTimer;

    private SoundPool soundPool;
    private int soundID;

    private boolean loaded;

    final Context context = this;

    AlertDialog.Builder alertDialogBuilder;

    private OnClickListener nextOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            currentposition++;
            Log.d("annef.workout", "current position = " + currentposition);
            Log.d("annef.wokrout", "workoutSequence.size() = "
                    + workoutSequence.size());
            if (currentposition == workoutSequence.size()) {
                alertDialogBuilder
                        .setMessage("Workout Completed ! Well Done !");
                alertDialogBuilder.setPositiveButton("Finish",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent result = new Intent();
                                setResult(RESULT_OK, result);
                                finish();

                            }
                        });
                alertDialogBuilder.setNegativeButton("Restart",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                currentposition = 0;
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                if (currentExo.getType() != ExerciseType.Serie){
                    countdownTimer.cancel();
                }
                currentExo = workoutSequence.get(currentposition);
                updateActivity();
            }

        }
    };
    private OnClickListener pauseOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (countdownTimer.isPaused()) {

                countdownTimer.resume();
                pause.setText(R.string.pause);
            } else {
                countdownTimer.pause();
                pause.setText(R.string.resume);
            }

        }
    };

    private OnClickListener cancelOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            alertDialogBuilder
                    .setMessage("Etes vous sure de vouloir arreter votre entrainement ?");
            alertDialogBuilder.setPositiveButton("Oui",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent result = new Intent();
                            setResult(RESULT_OK, result);
                            finish();

                        }
                    });
            alertDialogBuilder.setNegativeButton("Non",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    };


    private WorkoutDAO db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = new WorkoutDAO(this);

        exerciseName = (TextView) findViewById(R.id.chronolabel);
        timerLabel = (TextView) findViewById(R.id.timer);
        //testPurposeOnly
       //pathToPic = (TextView) findViewById(R.id.path);
        pic = (ImageView) findViewById(R.id.picture_chrono);

        next = (Button) findViewById(R.id.btn_skip);
        next.setOnClickListener(nextOnClickListener);

        cancel = (Button) findViewById(R.id.btn_stop);
        cancel.setOnClickListener(cancelOnClickListener);

        pause = (Button) findViewById(R.id.btn_pause);
        pause.setOnClickListener(pauseOnClickListener);

        alertDialogBuilder = new AlertDialog.Builder(context);

        long workoutId = getIntent().getExtras().getLong(
                "com.annef.workoutchrono.workout");

        Workout workout = db.read(workoutId);

        // Sound
        // Set the hardware buttons to control the music
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(this, R.raw.bip, 1);

        countdownTimer = new MyCountdownTimer();

        currentposition = 0;
        if (workout.getSequenceLength() == 0){

            Toast.makeText(this, "Cette séance est vide", Toast.LENGTH_SHORT).show();
            Intent result = new Intent();
            setResult(RESULT_OK, result);
            finish();
        }
        else {
            workoutSequence = buildWorkoutSequence(workout.getExerciseSequence());
            currentExo = workoutSequence.get(currentposition);
            updateActivity();
        }
    }

    private List<Exercise> buildWorkoutSequence(List<Exercise> exerciseSequence) {

        Log.d("annef", "building sequence");
        List<Exercise> list = new ArrayList<Exercise>();
        for (Exercise exercise : exerciseSequence) {
            Exercise rest = new Exercise("repos", ExerciseType.Repos,
                    "rest between series", exercise.getRest(), 1, 0,
                    exercise.getWorkoutId(), 0, 0, exercise.getPathToPic());
            for (int i = 0; i < exercise.getNbSerie(); i++) {
                Exercise exoToAdd = exercise.clone();
                exoToAdd.setName(exercise.getName() + " - " + (i+1) + " of " + exercise.getNbSerie());
                list.add(exoToAdd);

                Log.d("annef", "add exo "+exoToAdd.getName());
                if (exercise.getRest() != 0 && i+1 < exercise.getNbSerie()) {
                    list.add(rest);
                    Log.d("annef", "add exo "+rest.getName());
                }
            }


        }
        return list;

    }

    private void updateActivity() {
        Log.d("annef.workout", "updating activity position = "
                + currentposition);

        exerciseName.setText(currentExo.getName());
        // test purpose only
        //pathToPic.setText(currentExo.getPathToPic());
        if (!"".equals(currentExo.getPathToPic())) {
            pic.setImageURI(Uri.parse(new File(currentExo.getPathToPic()).toString()));
        }
        Log.d("annef.workout", "current Exo is "
                + currentExo.getName());

        timerLabel.setText("");
        // Pre timer
        // countdownTimer.start(3);

        if (currentExo.getType() == ExerciseType.Serie) {
            timerLabel.setText(currentExo.getRepeat() + " repets");
        } else if (currentExo.getType() == ExerciseType.Duree) {
            countdownTimer.start(currentExo.getRepeat(), true);
        } else { // repos
            countdownTimer.start(currentExo.getRepeat(), false);
        }

    }

    public class My321CountDownTimer {

    }

    public class MyCountdownTimer {
        CountDownTimer timer;
        private long remainingTime;
        private boolean isPaused;

        public MyCountdownTimer() {
            isPaused = true;
        }

        public boolean isPaused() {
            return isPaused;
        }

        public long getRemainingTime() {
            return remainingTime;
        }

        public void cancel() {
            timer.cancel();
        }

        public void pause() {
            timer.cancel();
            isPaused = true;
            Log.d("annef.workout", "pause !");
        }

        public void resume() {
            isPaused = false;
            start(remainingTime / 1000, false);
        }

        public void start(long time, boolean preTimer) {
            isPaused = false;

            timer = new CountDownTimer(time * 1000, 1) {

                @Override
                public void onTick(long millisUntilFinished) {
                    onTimerTick(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    onTimerFinish();
                }
            };

            if (preTimer) {
                Log.d("annef.workout", "Strating countdown 3 2 1...!");
                CountDownTimer my321Timer = new CountDownTimer(3000, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        on321TimerTick(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        timer.start();
                    }

                };
                timerLabel.setText("3");
                my321Timer.start();
            } else {

                timer.start();
            }
        }

        public void onTimerFinish() {
            // ding dong
            // ringtone & ring manager
            next.callOnClick();
        }

        public void onTimerTick(long millisUntilFinished) {
            remainingTime = millisUntilFinished;
            timerLabel.setText(toMMSS(millisUntilFinished / 1000));
        }

        public void on321TimerTick(long millisUntilFinished) {
            remainingTime = millisUntilFinished;

            if (!timerLabel.getText().equals("") && millisUntilFinished / 1000 != Integer.parseInt(""+timerLabel.getText())) {
                // biiip
                bip();
            }

            timerLabel.setText("" + millisUntilFinished / 1000);

        }

        private void bip() {

            Log.d("annef.workout", "Will Play sound");
            // Getting the user sound settings
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float actualVolume = (float) audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            // Is the sound loaded already?
            if (loaded) {
                soundPool.play(soundID, volume, volume, 1, 0, 1f);
                Log.d("annef.workout", "Played sound");
            }

        }

        private CharSequence toMMSS(long time) {
            String minute = "" + time / 60;
            String second = "" + time % 60;
            return minute + ":" + second;
        }
    }

}
