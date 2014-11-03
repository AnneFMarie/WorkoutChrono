package com.annef.workoutchrono.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class Exercise {
    //implements Parcelable {

    String name;
    ExerciseType type;
    String description;
    int repeat;
    int nbSerie;
    int rest;
    long workoutId;
    long id;
    long rank;
    String pathToPic;

    public Exercise(String name, ExerciseType type, String description, int repeat, int nbSerie, int rest, long workoutId, long id, long rank, String pathToPic){
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.repeat = repeat;
        this.nbSerie = nbSerie;
        this.rest = rest;
        this.workoutId = workoutId;
        this.rank = rank;
        this.pathToPic = pathToPic;
    }


    public String getLabel() {
        return description+" "+repeat+"x";
    }

    public String getName() {
        return name;
    }

    public ExerciseType getType() {
        return type;
    }

    public int getRepeat() {
        return repeat;
    }

    public int getNbSerie(){
        return nbSerie;
    }

    public int getRest() {
        return rest;
    }

    public long getWorkoutId(){
        return workoutId;

    }

    public long getId(){
        return id;

    }



    public static Exercise create(long workoutId, long rank) {

        String name1 = "";
        ExerciseType type1 = ExerciseType.Duree;
        String description1 = "";
        return new Exercise(name1, type1, description1, 0, 0, 0, workoutId, 0, rank, "");
    }

    public void setId(long create) {
        id = create;

    }

    public Exercise clone(){
        return new Exercise(this.name, this.type, this.description, this.repeat,
                this.nbSerie, this.rest, 0, 0, this.rank, this.pathToPic);
    }

    public void setName(String text) {
        this.name = text;
    }

    public void setType(String type) {
        //Log.d("annef.workout", "setting type :"+typeLabel.getText());
        if (type.equals("Duree")){
            this.type = ExerciseType.Duree;
        }
        if (type.equals("Serie")){
            this.type = ExerciseType.Serie;
        }
        if (type.equals("Repos")){
            this.type = ExerciseType.Repos;
        }

    }

    public void setRepeat(int i) {
        this.repeat = i;

    }

    public void setNbSerie(int i){
        nbSerie = i;
    }

    public void setRepos(int i) {
        this.rest = i;// TODO Auto-generated method stub

    }

    public long getRank() {
        return this.rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public String getPathToPic() {
        return pathToPic;
    }

    public void setPathToPic(String pathToPic) {
        this.pathToPic = pathToPic;
    }
}


