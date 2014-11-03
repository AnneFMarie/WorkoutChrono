package com.annef.workoutchrono.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DAOUtils extends SQLiteOpenHelper {
    // Nous sommes à la première version de la base
    // Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 8;
    // Le nom du fichier qui représente ma base
    protected final static String NOM = "database.db";

    public static final String TABLE_WORKOUT = "workout";
    public static final String TABLE_EXERCISE = "exercise";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String REPEAT = "repeat";
    public static final String SERIE = "serie";
    public static final String REST = "rest";
    public static final String RANK = "rank";
    public static final String PATH = "pathToPic";
    public static final String WORKOUT = "workout";

    public static final String CREATE_WORKOUT = "CREATE TABLE " + TABLE_WORKOUT
            + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
            + " TEXT );";

    public static final String DROP_TABLE_WOROUT = "DROP TABLE IF EXISTS "
            + TABLE_WORKOUT + ";";

    public static final String CREATE_EXERCISE = "CREATE TABLE "
            + TABLE_EXERCISE + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + TYPE
            + " TEXT, " + DESCRIPTION + " TEXT, " + REPEAT + " INT, " + SERIE + " INT, "+ REST
            + " INT, " + WORKOUT + " LONG, " + RANK + " LONG, " + PATH + " TEXT);";

    public static final String DROP_TABLE_EXERCISE = "DROP TABLE IF EXISTS "
            + TABLE_EXERCISE + ";";

    protected SQLiteDatabase mDb = null;

    public DAOUtils(Context pContext) {
        super(pContext, NOM, null, VERSION);
        // DatabaseHandler(pContext, NOM, null, VERSION);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase
        // s'en charge
        mDb = this.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }


}
