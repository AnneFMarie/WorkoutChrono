package com.annef.workoutchrono.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.annef.workoutchrono.R;
import com.annef.workoutchrono.model.Exercise;
import com.annef.workoutchrono.model.ExerciseType;
import com.annef.workoutchrono.model.Workout;

public class WorkoutDAO extends DAOUtils {

    SQLiteDatabase db;

    public WorkoutDAO(Context pContext) {

        super(pContext);

        db = this.open();
    }


    public long create(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(NAME, workout.getName());

        // Inserting Row
        long id = db.insert(TABLE_WORKOUT, null, values);
        return id;
    }

    public void delete(Workout workout) {
        db.delete(TABLE_WORKOUT, ID + " = ?",
                new String[] { String.valueOf(workout.getId()) });

        List<Exercise> idList = getExercisesList(workout);

        for (int i = 0; i < idList.size(); i++) {
            db.delete(TABLE_EXERCISE, ID + " = ?",
                    new String[] { String.valueOf(idList.get(i).getId()) });

        }
    }

    public Workout findWorkoutByName(String name) {

       // SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_WORKOUT
                + " where name = ?", new String[] { name });

        Workout workout = new Workout(cursor.getInt(0), cursor.getString(1));

        return workout;
    }

    public List<Exercise> getExercisesList(Workout workout) {
      //  SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_EXERCISE
                        + " where workout = ?",
                new String[] { String.valueOf(workout.getId()) });
        List<Exercise> results = new ArrayList<Exercise>();

        while (cursor.moveToNext()) {
            results.add(new Exercise(cursor.getString(1), getType(cursor.getString(2)), cursor.getString(3), cursor
                    .getInt(4), cursor.getInt(5), cursor.getInt(6), cursor
                    .getLong(7), cursor.getLong(0), cursor.getLong(8), cursor.getString(9)));
        }

        return results;
    }

    public int update(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(NAME, workout.getName());

        // updating row
        Log.d("com.annef.workoutchrono", "update workout " + workout.getName()
                + " id = " + workout.getId());

        int update = db.update(TABLE_WORKOUT, values,
                ID + " = " + workout.getId(), null);
        return update;
    }

    public Workout read(long id) {
        //SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORKOUT, new String[] { ID, NAME }, ID
                        + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        Log.d("annef", "read workout id="+id);
        Log.d("annef", "found "+cursor.getCount()+" workout");

        if (cursor != null) {
            cursor.moveToFirst();

            Workout workout = new Workout(id, cursor.getString(1));
            Cursor exerciseCursor = getReadAllExerciseCursor(workout);
            if (exerciseCursor != null && exerciseCursor.getCount() > 0) {
                exerciseCursor.moveToFirst();

                List<Exercise> liste = new ArrayList<Exercise>();
                do {
                    Exercise exo = getExerciseFromCursor(exerciseCursor);
                    liste.add(exo);
                } while (exerciseCursor.moveToNext()) ;
                workout.setExerciseList(liste);
            }

            return workout;
        }
        else
           return null;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_WORKOUT);
        db.execSQL(CREATE_EXERCISE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);

        // Create tables again
        onCreate(db);
    }

    public List<Workout> getAllWorkout() {
        //SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_EXERCISE, null);
        if (cursor != null) {
            cursor.moveToFirst();

            List<Workout> liste = new ArrayList<Workout>();
            while (cursor.moveToNext()) {

                Workout workout = new Workout(cursor.getInt(0),
                        cursor.getString(1));
                liste.add(workout);
            }
            return liste;
        }
        return null;
    }

    public SimpleCursorAdapter getWorkoutAdapter(Context context) {

        Cursor cursor = getReadAllWorkoutCursor();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
                R.layout.workout_row, cursor, new String[] { "name" },
                new int[] { R.id.WorkoutRow_name });

        return adapter;
    }

    public SimpleCursorAdapter getExerciseAdapter(Workout workout, Context context) {

        Cursor cursor = getReadAllExerciseCursor(workout);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,
                R.layout.exercise_row, cursor, new String[] { "name" },
                new int[] { R.id.ExerciseRow_name});

        SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView textView = (TextView) view;
                if (cursor.getString(2).equals("Serie")){
                    textView.setText(cursor.getString(1)+" - "+cursor.getInt(5)+"x"+cursor.getInt(4));
                }
                else {
                    textView.setText(cursor.getString(1)+" - "+cursor.getInt(4)+"sec");
                }
                return true;

            }
        };
        adapter.setViewBinder(binder);

        return adapter;
    }

    public Exercise getExerciseFromCursor(Cursor cursor) {
        return new Exercise(cursor.getString(1),
                getType(cursor.getString(2)),
                cursor.getString(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getLong(7),
                cursor.getLong(0),
                cursor.getLong(8),
                cursor.getString(9));
    }

    private ExerciseType getType(String value) {
        ExerciseType type;
        try {
            type = ExerciseType.valueOf(value);
            return type;
        }
        catch (IllegalArgumentException e){
            return ExerciseType.Serie;
        }
    }

    public long create(Exercise exercise) {

        ContentValues values = new ContentValues();

        values.put(NAME, exercise.getName());
        values.put(TYPE, exercise.getType().toString());
        values.put(DESCRIPTION, exercise.getLabel());
        values.put(REPEAT, String.valueOf(exercise.getRepeat()));
        values.put(SERIE, String.valueOf(exercise.getNbSerie()));
        values.put(REST, String.valueOf(exercise.getRest()));
        values.put(WORKOUT, exercise.getWorkoutId());
        values.put(RANK, exercise.getRank());
        values.put(PATH, exercise.getPathToPic());
        long exerciseId = db.insert(TABLE_EXERCISE, null, values);

        return exerciseId;
    }

    public int update(Exercise exercise) {

        ContentValues values = new ContentValues();

        Log.d("annef.workout", "updating exercise ");

        values.clear();
        values.put(NAME, exercise.getName());
        values.put(TYPE, exercise.getType().toString());
        values.put(DESCRIPTION, exercise.getLabel());
        values.put(REPEAT, String.valueOf(exercise.getRepeat()));
        values.put(SERIE, String.valueOf(exercise.getNbSerie()));
        values.put(REST, String.valueOf(exercise.getRest()));
        values.put(PATH, exercise.getPathToPic());
        values.put(WORKOUT, exercise.getWorkoutId());

        // updating row
        Log.d("com.annef.workoutchrono", "update workout " + exercise.getName()
                + " id = " + exercise.getId());

        // updating row

        return db.update(TABLE_EXERCISE, values, ID + " = ?",
                new String[] { String.valueOf(exercise.getId()) });
    }

    public Cursor getReadAllExerciseCursor(Workout workout) {

        Cursor cursor = db
                .rawQuery(
                        "Select id as _id, name, type, description, repeat, serie, rest, workout, rank, "+PATH+" from "
                                + TABLE_EXERCISE + " where workout = ?",
                        new String[] { String.valueOf(workout.getId()) });
        return cursor;

    }

    public Cursor getReadAllWorkoutCursor() {

        Cursor cursor = db.rawQuery("Select id as _id, name from "
                + TABLE_WORKOUT, null);
        //close();
        return cursor;
    }

    public void delete(Exercise exo) {
        db.delete(TABLE_EXERCISE, ID + " = ?",
                new String[] { String.valueOf(exo.getId()) });
    }

    public long getNextRankForWorkout(long id) {
        Cursor cursor = db.rawQuery("Select MAX(" + RANK + ") from "
                        + TABLE_EXERCISE + " where " + WORKOUT + " = ?",
                new String[] { String.valueOf(id) });
        if (!cursor.moveToFirst()) {
            return 0;
        }
        return cursor.getLong(0);
    }

    public Exercise readExo(long id) {

            Cursor cursor = db.query(TABLE_EXERCISE, new String[] { ID, NAME, TYPE, DESCRIPTION, REPEAT, SERIE,
                    REST, WORKOUT, RANK, PATH }, ID
                            + "=?", new String[] { String.valueOf(id) }, null, null, null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();

                Exercise exo = getExerciseFromCursor(cursor);

                return exo;
            }
        else
                return null;

    }
}
