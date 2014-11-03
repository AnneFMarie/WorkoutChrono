package com.annef.workoutchrono.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by LAHI8322 on 08/09/2014.
 */
public class Workout {
    //implements Parcelable {

        String name;
        List<Exercise> sequence;
        long id;
/*
        public static final Parcelable.Creator<Workout> CREATOR = new Parcelable.Creator<Workout>(){
            @Override
            public Workout createFromParcel(Parcel source)
            {
                return new Workout(source);
            }

            @Override
            public Workout[] newArray(int size)
            {
                return new Workout[size];
            }
        };

        public Workout(Parcel in){
            id = in.readLong();
            name = in.readString();

            sequence = new ArrayList<Exercise>();
            in.readTypedList(sequence, Exercise.CREATOR);
        }
    */
        public Workout(String name, ArrayList<Exercise> sequence){
            this.name = name;
            sequence = sequence;
        }


        public Workout(String name) {
            this.name = name;
        }

        public Workout(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public void add(Exercise exo) {
            if (null == sequence){
                this.sequence = new ArrayList<Exercise>();
            }

            sequence.add(exo);

        }

        public int getSequenceLength() {
            if (null == sequence){
                return 0;
            }
            return sequence.size();

        }

        public List<Exercise> getExerciseSequence() {
            return sequence;
        }

        public Exercise getExerciseByName(String name) {

            for (Iterator<Exercise> iterator = sequence.iterator(); iterator.hasNext();) {
                Exercise exo = (Exercise) iterator.next();
                if (exo.name.equals(name)){
                    return exo;
                }
            }
            return null;
        }

        public ArrayList<Parcelable> getParcelableSequence() {
            ArrayList<Parcelable> ret = new ArrayList<Parcelable>();
            for (Iterator<Exercise> iterator = sequence.iterator(); iterator.hasNext();) {
                ret.add((Parcelable) iterator.next());
            }
            return ret;
        }

        public void replace(Exercise exo) {
            for (Iterator<Exercise> iterator = sequence.iterator(); iterator.hasNext();) {
                Exercise currentExo = (Exercise) iterator.next();
                if (exo.name.equals(currentExo.name)){
                    iterator.remove();
                    sequence.add(exo);
                }
            }

        }


        public List<String> getExercisesLabels() {
            List<String> labels = new ArrayList<String>();

            for (Iterator iterator = sequence.iterator(); iterator
                    .hasNext();) {
                Exercise exercise = (Exercise) iterator.next();
                labels.add(exercise.getName());
            }
            return labels;
        }
/*
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeTypedList(sequence);
        }
*/
        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }

        public static Workout getEmpty() {
            Workout workout = new Workout("");
            return workout;
        }

        public void setId(long create) {
            id = create;

        }

        public void setName(String text) {
            name = text;

        }

        public void setExerciseList(List<Exercise> exercisesList) {
            // TODO Auto-generated method stub
            sequence = exercisesList;
        }



}
