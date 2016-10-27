package apps.hensbri.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.data.MovieContract.MovieEntry;
import apps.hensbri.popularmovies.data.MovieContract.MovieListEntry;
import apps.hensbri.popularmovies.data.MovieContract.MovieWithFavoriteView;
import apps.hensbri.popularmovies.data.MovieContract.ReviewEntry;
import apps.hensbri.popularmovies.data.MovieContract.VideoEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 17;

    public static final String DATABASE_NAME = "movies.db";

    private String mFavoritesStr;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mFavoritesStr = context.getString(R.string.movie_favorites_key);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                    MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                    MovieEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL, " +
                    MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                    MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                    "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") " +
                    "ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_LISTS_TABLE =
                "CREATE TABLE " + MovieListEntry.TABLE_NAME + " (" +
                        MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieListEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        MovieListEntry.COLUMN_REQUEST_TYPE + " TEXT NOT NULL," +
                        "UNIQUE (" + MovieListEntry.COLUMN_MOVIE_ID + ", " +
                        MovieListEntry.COLUMN_REQUEST_TYPE + ") " +
                        "ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                        ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL," +
                        ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                        ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                        ReviewEntry.COLUMN_URL + " TEXT NOT NULL," +
                        "UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") " +
                        "ON CONFLICT REPLACE, " +
                        "FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ") " +
                        "ON DELETE CASCADE);";

        final String SQL_CREATE_VIDEOS_TABLE =
                "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                        VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        VideoEntry.COLUMN_SIZE + " TEXT NOT NULL," +
                        VideoEntry.COLUMN_SOURCE + " TEXT NOT NULL," +
                        VideoEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                        "FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ") " +
                        "ON DELETE CASCADE);";

        final String SQL_CREATE_MOVIE_FAVORITE_VIEW =
                "CREATE VIEW " + MovieContract.MovieWithFavoriteView.TABLE_NAME + " AS " +
                        " SELECT " +
                        "m." + MovieWithFavoriteView._ID + "," +
                        "m." + MovieWithFavoriteView.COLUMN_MOVIE_ID + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_TITLE + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_POSTER_PATH + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_OVERVIEW + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_RELEASE_DATE + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_POPULARITY + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_VOTE_AVG + ", " +
                        "m." + MovieWithFavoriteView.COLUMN_VOTE_AVG + ", " +
                        "f." + MovieEntry.COLUMN_MOVIE_ID + " IS NOT NULL AS " +
                        MovieWithFavoriteView.COLUMN_IS_FAVORITE +
                        " FROM " + MovieEntry.TABLE_NAME + " m LEFT JOIN " +
                            MovieListEntry.TABLE_NAME + " f " +
                        " ON ( " +
                            "m." + MovieEntry.COLUMN_MOVIE_ID + "=" + "f." + MovieListEntry.COLUMN_MOVIE_ID +
                        " AND f." + MovieListEntry.COLUMN_REQUEST_TYPE +
                            " = '" + mFavoritesStr + "');";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_LISTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITE_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS "  + MovieWithFavoriteView.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}