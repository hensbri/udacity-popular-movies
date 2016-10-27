package apps.hensbri.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * https://developer.android.com/reference/android/content/UriMatcher.html
 */
public class MovieProvider extends ContentProvider {
    private final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_REQUEST = 101;

    static final int REVIEWS = 200;
    static final int REVIEWS_WITH_ID = 201;
    static final int MOVIE_REVIEWS = 202;

    static final int VIDEOS = 300;
    static final int VIDEOS_WITH_ID = 301;
    static final int MOVIE_VIDEOS = 302;

    static final int MOVIE_LISTS = 400;

    static final int MOVIE_WITH_FAVORITE_BY_ID = 500;

    //movies.movie_id = ?
    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sMovieFavoriteMovieIdSelection =
            MovieContract.MovieWithFavoriteView.TABLE_NAME + "." +
                    MovieContract.MovieWithFavoriteView.COLUMN_MOVIE_ID + " = ? ";

    //reviews.movie_id = ?
    private static final String sReviewSelection =
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    //videos.movie_id = ?
    private static final String sVideoSelection =
            MovieContract.VideoEntry.TABLE_NAME + "." + MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final SQLiteQueryBuilder sMovieByRequestQueryBuilder;

    static{
        sMovieByRequestQueryBuilder = new SQLiteQueryBuilder();
        sMovieByRequestQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieListEntry.TABLE_NAME +
                        " ON (" + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieListEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieListEntry.TABLE_NAME +
                        "." + MovieContract.MovieListEntry.COLUMN_MOVIE_ID + ")");
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES_LISTS, MOVIE_LISTS);
        matcher.addURI(authority, MovieContract.PATH_MOVIES_LISTS + "/*", MOVIES_WITH_REQUEST);

        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_FAVORITE_BY_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_VIDEOS + "/#", VIDEOS_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_WITH_REQUEST:
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
            case REVIEWS_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case VIDEOS:
            case VIDEOS_WITH_ID:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_LISTS:
                return MovieContract.MovieListEntry.CONTENT_TYPE;
            case MOVIE_WITH_FAVORITE_BY_ID:
                return MovieContract.MovieWithFavoriteView.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie_lists/*"
            case MOVIES_WITH_REQUEST:
            {
                String requestType = MovieContract.MovieListEntry.getMovieRequestTypeFromUri(uri);
                sortOrder = ( requestType == "popular" ) ?
                        MovieContract.MovieEntry.COLUMN_POPULARITY :
                        MovieContract.MovieEntry.COLUMN_VOTE_AVG;
                sortOrder += " DESC";
                retCursor = sMovieByRequestQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        MovieContract.MovieListEntry.COLUMN_REQUEST_TYPE + " = ?",
                        new String[]{requestType},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "movies/#"
            case MOVIE_WITH_FAVORITE_BY_ID: {
                long movieId = MovieContract.MovieWithFavoriteView.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieWithFavoriteView.TABLE_NAME,
                        projection,
                        sMovieFavoriteMovieIdSelection,
                        new String[]{Long.toString(movieId)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "movies"
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "reviews/#"
            case REVIEWS_WITH_ID: {
                long movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        sReviewSelection,
                        new String[]{Long.toString(movieId)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "videos/#"
            case VIDEOS_WITH_ID: {
                long movieId = MovieContract.VideoEntry.getMovieIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        sVideoSelection,
                        new String[]{Long.toString(movieId)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "movie_lists"
            case MOVIE_LISTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_LISTS: {
                long _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieListEntry.buildMovieListUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case VIDEOS: {
                rowsDeleted = db.delete(MovieContract.VideoEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            case MOVIE_LISTS: {
                rowsDeleted = db.delete(MovieContract.MovieListEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_LISTS:
                rowsUpdated = db.update(MovieContract.MovieListEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEOS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIE_LISTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
