package apps.hensbri.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import apps.hensbri.popularmovies.data.MovieContract;
import apps.hensbri.popularmovies.helpers.OpenMovieConfig;

/**
 * Created by hensbri on 11/7/16.
 */
public class FetchReviewsTask extends AsyncTask<Long, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context mContext;

    public FetchReviewsTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Long... params) {

        try {
            if (params.length == 0) {
                return null;
            }
            Long movieId = params[0];
            if ( movieId == null ) {
                return null;
            }
            String jsonStr = OpenMovieConfig.getMovieDbJsonStr(
                    OpenMovieConfig.buildReviewsURI(movieId));
            updateReviewsFromJson(jsonStr, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private void updateReviewsFromJson(String reviewsJsonStr, Long movieId)
            throws JSONException {
        JSONObject movieJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewArray = movieJson.getJSONArray(OpenMovieConfig.OM_RESULTS);
        Vector<ContentValues> cvReviewList = new Vector<>(reviewArray.length());

        for (int i = 0; i < reviewArray.length(); i++) {

            JSONObject reviewObj = reviewArray.getJSONObject(i);

            String reviewId = reviewObj.getString(OpenMovieConfig.OM_REVIEW_ID);
            String author = reviewObj.getString(OpenMovieConfig.OM_REVIEW_AUTHOR);
            String content = reviewObj.getString(OpenMovieConfig.OM_REVIEW_CONTENT);
            String url = reviewObj.getString(OpenMovieConfig.OM_REVIEW_URL);

            ContentValues cv = new ContentValues();

            cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
            cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            cv.put(MovieContract.ReviewEntry.COLUMN_URL, url);

            cvReviewList.add(cv);
        }

        int inserted = 0;
        // add to database
        if (cvReviewList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvReviewList.size()];
            cvReviewList.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
        }
    }
}
