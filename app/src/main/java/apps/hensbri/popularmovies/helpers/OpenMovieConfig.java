package apps.hensbri.popularmovies.helpers;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hensbri on 8/22/16.
 */
public class OpenMovieConfig {
    public final static String LOG_TAG = OpenMovieConfig.class.getSimpleName();

    public final static String MD_BASE_URL           = "http://api.themoviedb.org/3/movie/";
    public final static String MD_APPID_PARAM        = "api_key";
    public final static String OPEN_MOVIE_DB_APPID   = "<OPEN_MOVIE_API_KEY>";
    public final static String MD_REVIEWS_PARAM      = "reviews";
    public final static String MD_TRAILERS_PARAM     = "trailers";
    public final static String MD_BASE_IMG_URL       = "http://image.tmdb.org/t/p/";

    public final static String OM_RESULTS      = "results";
    public final static String OM_MOVIE_ID     = "id";
    public final static String OM_TITLE        = "title";
    public final static String OM_POSTER_PATH  = "poster_path";
    public final static String OM_OVERVIEW     = "overview";
    public final static String OM_RELEASE_DATE = "release_date";
    public final static String OM_POPULARITY   = "popularity";
    public final static String OM_VOTE_AVG     = "vote_average";

    // Reviews
    public final static String OM_REVIEW_ID      = "id";
    public final static String OM_REVIEW_AUTHOR  = "author";
    public final static String OM_REVIEW_CONTENT = "content";
    public final static String OM_REVIEW_URL     = "url";

    // Trailers
    public final static String OM_TRAILERS_YOUTUBE = "youtube";
    public final static String OM_TRAILERS_NAME    = "name";
    public final static String OM_TRAILERS_SIZE    = "size";
    public final static String OM_TRAILERS_SOURCE  = "source";
    public final static String OM_TRAILERS_TYPE    = "type";

    public final static int AVAILABLE_SORT_TYPES_SIZES = 2;
    public final static String AVAILABLE_SORT_TYPES[];
    static {
        AVAILABLE_SORT_TYPES = new String[AVAILABLE_SORT_TYPES_SIZES];
        AVAILABLE_SORT_TYPES[0] = "top_rated";
        AVAILABLE_SORT_TYPES[1] = "popular";
    }

    public static String getAvailableSortTypes(int i) {
        return AVAILABLE_SORT_TYPES[i];
    }

    public final static int POSTER_SIZES_COUNT = 7;
    public final static String POSTER_SIZES[];
    static {
        POSTER_SIZES = new String[POSTER_SIZES_COUNT];
        POSTER_SIZES[0] = "w92";
        POSTER_SIZES[1] = "w154";
        POSTER_SIZES[2] = "w185";
        POSTER_SIZES[3] = "w342";
        POSTER_SIZES[4] = "w500";
        POSTER_SIZES[5] = "w780";
        POSTER_SIZES[6] = "original";
    }

    public static String buildMovieListURI(String request_type) {
        Uri builtUri = Uri.parse(OpenMovieConfig.MD_BASE_URL).buildUpon()
                .appendPath(request_type)
                .appendQueryParameter(
                        OpenMovieConfig.MD_APPID_PARAM,
                        OpenMovieConfig.OPEN_MOVIE_DB_APPID)
                .build();
        return builtUri.toString();
    }

    public static String buildReviewsURI(Long movie_id) {
        Uri builtUri = Uri.parse(OpenMovieConfig.MD_BASE_URL).buildUpon()
                .appendPath(Long.toString(movie_id))
                .appendPath(MD_REVIEWS_PARAM)
                .appendQueryParameter(
                        OpenMovieConfig.MD_APPID_PARAM,
                        OpenMovieConfig.OPEN_MOVIE_DB_APPID)
                .build();
        return builtUri.toString();
    }

    public static String buildTrailersURI(Long movie_id) {
        Uri builtUri = Uri.parse(OpenMovieConfig.MD_BASE_URL).buildUpon()
                .appendPath(Long.toString(movie_id))
                .appendPath(MD_TRAILERS_PARAM)
                .appendQueryParameter(
                        OpenMovieConfig.MD_APPID_PARAM,
                        OpenMovieConfig.OPEN_MOVIE_DB_APPID)
                .build();
        return builtUri.toString();
    }

    public static String buildPosterURIString(String poster_uri, String poster_size) {
        Uri builtUri = Uri.parse(OpenMovieConfig.MD_BASE_IMG_URL)
                .buildUpon()
                .appendPath(poster_size)
                .appendEncodedPath(poster_uri)
                .build();
        return builtUri.toString();
    }

    @Nullable
    public static String getMovieDbJsonStr(String uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(uri);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            return buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage(), e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}