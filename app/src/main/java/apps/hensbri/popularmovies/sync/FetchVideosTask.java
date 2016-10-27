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

public class FetchVideosTask extends AsyncTask<Long, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context mContext;

    public FetchVideosTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Long... params) {

        try {
            if (params.length == 0) {
                return null;
            }
            Long movieId = params[0];
            if (movieId == null) {
                return null;
            }
            String jsonStr = OpenMovieConfig.getMovieDbJsonStr(
                    OpenMovieConfig.buildTrailersURI(movieId));
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
        JSONArray trailersArray = movieJson.getJSONArray(OpenMovieConfig.OM_TRAILERS_YOUTUBE);
        Vector<ContentValues> cvTrailersList = new Vector<>(trailersArray.length());

        for (int i = 0; i < trailersArray.length(); i++) {

            JSONObject obj = trailersArray.getJSONObject(i);

            String tName   = obj.getString(OpenMovieConfig.OM_TRAILERS_NAME);
            String tSize   = obj.getString(OpenMovieConfig.OM_TRAILERS_SIZE);
            String tSource = obj.getString(OpenMovieConfig.OM_TRAILERS_SOURCE);
            String tType   = obj.getString(OpenMovieConfig.OM_TRAILERS_TYPE);

            ContentValues cv = new ContentValues();

            cv.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.VideoEntry.COLUMN_NAME,     tName);
            cv.put(MovieContract.VideoEntry.COLUMN_SIZE,     tSize);
            cv.put(MovieContract.VideoEntry.COLUMN_SOURCE,   tSource);
            cv.put(MovieContract.VideoEntry.COLUMN_TYPE,     tType);

            cvTrailersList.add(cv);
        }

        int inserted = 0;
        // add to database
        if (cvTrailersList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvTrailersList.size()];
            cvTrailersList.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
        }
    }
}