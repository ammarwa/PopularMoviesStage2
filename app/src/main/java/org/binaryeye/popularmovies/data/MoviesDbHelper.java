package org.binaryeye.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ammar on 0030 30 Mar 17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                        MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MoviesEntry.COLUMN_ADULT + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_GENRE_IDS + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " VARCHAR (50) NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_POPULARITY + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_POSTER_PATH + " VARCHAR (50) NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_TITLE + " VARCHAR (50) NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT + " VARCHAR (50), " +
                        MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " VARCHAR (50) NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " VARCHAR (50) NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_VIDEO + " VARCHAR (50), " +
                        " UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
