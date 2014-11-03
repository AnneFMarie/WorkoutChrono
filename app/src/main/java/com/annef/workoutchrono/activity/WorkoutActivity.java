package com.annef.workoutchrono.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.annef.workoutchrono.R;
import com.annef.workoutchrono.dao.WorkoutDAO;
import com.annef.workoutchrono.model.Exercise;
import com.annef.workoutchrono.model.Workout;

public class WorkoutActivity extends Activity {

    Workout workout;
    private ListView liste;

  /*  private AdapterView.OnItemLongClickListener onExerciseLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            showDialog(position);
            return false;
        }
    });;
*/
    private OnItemClickListener onExerciseClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // On rajoute un extra
            Cursor cursor = (Cursor) liste.getItemAtPosition(position);

            Log.d("annef.workout", "cursor rows = " + cursor.getCount());
            Log.d("annef.workout", "cursor column = " + cursor.getColumnName(0));
            Exercise exo = db.getExerciseFromCursor(cursor);

            if (null != exo) {
                // On lance l'activite de modification d'un exercice
                Intent secondeActivite = new Intent(WorkoutActivity.this,
                        ExerciseActivity.class);
                Log.d("annef",
                        "Ready to start Exo activity with exo id="
                                + exo.getId());
                secondeActivite.putExtra("com.annef.workoutchrono.exercise",
                        (Parcelable) exo);
                // Puis on lance l'intent !
                startActivityForResult(secondeActivite, 0);
            }

        }
    };


    private WorkoutDAO db;
    private TextView nameTextView;
    private SimpleCursorAdapter dbAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(this, "Exercice Sauvegardé", Toast.LENGTH_SHORT).show();

        dbAdapter.notifyDataSetChanged();
        dbAdapter.swapCursor(db.getReadAllExerciseCursor(workout));

    }

    @Override
    public void onBackPressed() {
        workout.setName(nameTextView.getText().toString());

        db.update(workout);
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        db = new WorkoutDAO(this);

        workout = getIntent().getExtras().getParcelable(
                "com.annef.workoutchrono.workout");
        Log.d("com.annef.workoutchrono",
                "Strating WorkoutActivity with workout " + workout.getName()
                        + " id= " + workout.getId());
        workout.setExerciseList(db.getExercisesList(workout));

        liste = (ListView) findViewById(R.id.exerciseList);
        dbAdapter = db.getExerciseAdapter(workout, this);
        liste.setAdapter(dbAdapter);
        liste.setOnItemClickListener(onExerciseClickListener);

        nameTextView = (TextView) findViewById(R.id.workoutName);
        nameTextView.setText(workout.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_delete) {
            return true;
        }
        if (id == R.id.action_launch) {
            // On lance l'activite de modification d'un exercice
            Intent launchActivite = new Intent(WorkoutActivity.this,
                    ChronoActivity.class);

            // On rajoute un extra
            launchActivite.putExtra("com.annef.workoutchrono.workout",
                    (Parcelable) workout);
            // launchActivite.putExtra("com.annef.workoutchrono.sequence",
            // workout.getParcelableSequence());
            // Puis on lance l'intent !
            startActivity(launchActivite);
        }
        if (id == R.id.action_add_exercise) {

            // Add empty exo to workout

            Log.d("annef.workout",
                    "adding exercie for dworkout id=" + workout.getId());
            long rank = db.getNextRankForWokrout(workout.getId());
            Exercise exo = Exercise.create(workout.getId(), rank);
            exo.setId(db.create(exo));
            workout.add(exo);

            // dbAdapter.notifyDataSetChanged();
            // dbAdapter.swapCursor(db.getReadAllExerciseCursor(workout));

            // On lance l'activite de modification d'un exercice
            Intent secondeActivite = new Intent(WorkoutActivity.this,
                    ExerciseActivity.class);
            secondeActivite.putExtra("com.annef.workoutchrono.exercise",
                    (Parcelable) exo);
            // Puis on lance l'intent !
            startActivityForResult(secondeActivite, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.workout_fragment,
                    container, false);
            return rootView;
        }
    }

}
