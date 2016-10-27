package apps.hensbri.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import apps.hensbri.popularmovies.R;
import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.OpenMovieConfig;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {

        try {
            // First delete all non-favorite movies
            int deleted = getContext().getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                            " NOT IN (SELECT " + MovieContract.MovieListEntry.COLUMN_MOVIE_ID +
                            " FROM " + MovieContract.MovieWithFavoriteView.TABLE_NAME +
                            " WHERE " + MovieContract.MovieWithFavoriteView.COLUMN_IS_FAVORITE + " != 1)",
                    null);

            // Get Popular and Top Rated Movie IDs (details will be pulled next)
            for (int i = 0; i < OpenMovieConfig.AVAILABLE_SORT_TYPES_SIZES; ++i) {
                String requestType = OpenMovieConfig.getAvailableSortTypes(i);
                String jsonStr = OpenMovieConfig.getMovieDbJsonStr(OpenMovieConfig.buildMovieListURI(requestType));
                updateMovieListsFromJson(jsonStr, requestType);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void updateMovieListsFromJson(String movieJsonStr,
                                          String requestType)
            throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OpenMovieConfig.OM_RESULTS);
        Vector<ContentValues> cVMovieList = new Vector<>(movieArray.length());
        Vector<ContentValues> cvMovies = new Vector<>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieObj = movieArray.getJSONObject(i);

            String movie_id = movieObj.getString(OpenMovieConfig.OM_MOVIE_ID);

            ContentValues movieListValues = new ContentValues();

            movieListValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
            movieListValues.put(MovieContract.MovieEntry.COLUMN_REQUEST_TYPE, requestType);

            cVMovieList.add(movieListValues);

            String title = movieObj.getString(OpenMovieConfig.OM_TITLE);
            String poster_path = movieObj.getString(OpenMovieConfig.OM_POSTER_PATH);
            String overview = movieObj.getString(OpenMovieConfig.OM_OVERVIEW);
            String release_date = movieObj.getString(OpenMovieConfig.OM_RELEASE_DATE);
            float popularity = (float) movieObj.getDouble(OpenMovieConfig.OM_POPULARITY);
            float vote_avg = (float) movieObj.getDouble(OpenMovieConfig.OM_VOTE_AVG);

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, vote_avg);

            cvMovies.add(movieValues);
        }

        int inserted = 0;
        // add to database
        if (cVMovieList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVMovieList.size()];
            cVMovieList.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieListEntry.CONTENT_URI, cvArray);
        }

        inserted = 0;
        // add to database
        if (cvMovies.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvMovies.size()];
            cvMovies.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
