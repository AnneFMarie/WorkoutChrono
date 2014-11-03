package com.annef.tests.model;


import com.annef.workoutchrono.model.Exercise;
import com.annef.workoutchrono.model.ExerciseType;
import com.annef.workoutchrono.model.Workout;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAHI8322 on 27/10/2014.
 */
public class WorkoutTest extends TestCase {

        Workout workout;
    String name = "name";
    long id = 123;
    Exercise exo1;
    Exercise exo2;
    Exercise exo3;

    ArrayList<Exercise> exoSequence;


    public void setUp() {

        exo1 = new Exercise("exo1", ExerciseType.Duree, "", 12, 3, 180, id, 11, 0, "");
        exo2 = new Exercise("exo2", ExerciseType.Duree, "", 12, 3, 180, id, 11, 0, "");
        exo3 = new Exercise("exo3", ExerciseType.Duree, "", 12, 3, 180, id, 11, 0, "");
        exoSequence = new ArrayList<Exercise>();
        exoSequence.add(exo1);
        exoSequence.add(exo2);
    }

    public void testCreateName() {
        workout = new Workout(name);

        Assert.assertEquals(workout.getName(), name);

    }

    public void testCreateNameId() {

        workout = new Workout(id, name);

        assertEquals (workout.getName(),(name));
        assertEquals (workout.getId(), id);

    }

    public void testCreateSeq(){
        workout = new Workout(name, exoSequence);

        assertEquals(workout.getName(), name);
        assertEquals(workout.getSequenceLength(), 2);
    }

    public void testAddExo() {
        workout = new Workout(name, exoSequence);
        workout.add(exo3);
        assertTrue(workout.getName().equals(name));
        assertTrue(workout.getSequenceLength() == 3);
    }

        public void testGetSequenceLength() {

            workout = new Workout(name, exoSequence);
            workout.add(exo3);
            assertTrue(workout.getSequenceLength() == 2);
            assertTrue(workout.getName().equals(name));
            assertTrue(workout.getSequenceLength() == 3);
        }

        public void testGetExerciseSequence() {
            workout = new Workout(name, exoSequence);
            List<Exercise> exoList = workout.getExerciseSequence();
            assertTrue(exoList.size() == 2);
        }

        public void testGetExerciseByName() {
            workout = new Workout(name, exoSequence);
            Exercise exoA = workout.getExerciseByName(exo1.getName());

            assertTrue (exoA.getName().equals(exo1.getName()));
            assertTrue (exoA.getRest() == (exo1.getRest()));
            assertTrue (exoA.getRepeat() == exo1.getRepeat());
        }


        public void  testGetExercisesLabels() {
            workout = new Workout(name, exoSequence);
            List<String> labels = workout.getExercisesLabels();

            assertTrue (labels.size() == 2);
            assertEquals(labels.get(0), exo1.getName());
            assertEquals(labels.get(1), exo2.getName());
        }

        public void testGetName() {
            workout = new Workout(name);
            assertTrue (workout.getName().equals(name));
        }

        public void testGetId() {
            workout = new Workout(id, name);
            assertTrue (workout.getId() == id);
        }

        public void testGetEmpty() {
            workout = Workout.getEmpty();
            assertTrue (workout.getName().equals(""));

        }

        public void testSetId() {

            workout = new Workout(id, name);
            workout.setId(234);

            assertTrue (workout.getId() == 234);

        }

        public void testSetName() {
            workout = new Workout(id, name);
            workout.setName("newName");

            assertTrue (workout.getName().equals("newName"));

        }

        public void testSetExerciseList() {

            workout = new Workout(id, name);
            workout.setExerciseList(exoSequence);

            assertTrue(workout.getSequenceLength() == 2);
        }


}
