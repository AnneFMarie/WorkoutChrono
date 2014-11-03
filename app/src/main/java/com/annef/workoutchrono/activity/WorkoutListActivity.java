package com.annef.workoutchrono.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.annef.workoutchrono.R;
import com.annef.workoutchrono.dao.WorkoutDAO;
import com.annef.workoutchrono.model.Workout;

public class WorkoutListActivity extends Activity {

    // Workout workout;
    private ListView liste;

    private OnItemClickListener onWorkoutClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // On lance l'activite de modification d'un exercice
            Intent secondeActivite = new Intent(WorkoutListActivity.this,
                    WorkoutActivity.class);

            Cursor cursor = (Cursor) liste.getItemAtPosition(position);
            Workout workout = new Workout(cursor.getInt(0), cursor.getString(1));
            Log.d("com.annef.workoutchrono", "click on workout name = "+cursor.getString(1)+" n°" + position
                    + " id = " + cursor.getInt(0));
            if (null != workout) {
                secondeActivite.putExtra("com.annef.workoutchrono.workout",
                        workout.getId());
                // Puis on lance l'intent !
                startActivityForResult(secondeActivite, 0);
            }

        }
    };

    private WorkoutDAO db;

    private SimpleCursorAdapter dbAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(this, "Workout Sauvegardé", Toast.LENGTH_SHORT).show();

        Log.d("annefworkout", "on workoutActivity Result");

        dbAdapter.notifyDataSetChanged();
        dbAdapter.swapCursor(db.getReadAllWorkoutCursor());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_workout);

        db = new WorkoutDAO(this);

        liste = (ListView) findViewById(R.id.workoutList);

        dbAdapter = db.getWorkoutAdapter(this);
        liste.setAdapter(dbAdapter);
        liste.setOnItemClickListener(onWorkoutClickListener);

        if (dbAdapter.isEmpty()){
            Toast.makeText(this, "Use + to add a workout", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workoutlist_menu, menu);
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
        } else if (id == R.id.action_add_workout){
            // Add empty exo to workout
            Workout workout = Workout.getEmpty();

            workout.setId(db.create(workout));

            dbAdapter.notifyDataSetChanged();
            dbAdapter.swapCursor(db.getReadAllWorkoutCursor());

            // Launch Workout Edition
            Intent secondeActivite = new Intent(WorkoutListActivity.this,
                    WorkoutActivity.class);

            secondeActivite.putExtra("com.annef.workoutchrono.workout",
                     workout.getId());
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
