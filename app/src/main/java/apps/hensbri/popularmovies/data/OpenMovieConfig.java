package apps.hensbri.popularmovies.data;

import android.net.Uri;
import android.util.Log;

/**
 * Created by hensbri on 8/22/16.
 */
public class OpenMovieConfig {
    private static final String LOG_TAG = ParcelableMovie.class.getSimpleName();

    public final static String MD_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String MD_APPID_PARAM = "api_key";
    public final static String MD_BASE_IMG_URL = "http://image.tmdb.org/t/p/";
    public final static String OPEN_MOVIE_DB_APPID = "<ENTER APPID HERE>";

    public final static String OM_RESULTS      = "results";
    public final static String OM_POSTER       = "poster_path";
    public final static String OM_OVERVIEW     = "overview";
    public final static String OM_TITLE        = "original_title";
    public final static String OM_VOTE_AVG     = "vote_average";
    public final static String OM_RELEASE_DATE = "release_date";

    public final static int REQUEST_TYPES_COUNT = 2;
    public final static String REQUEST_TYPES[];
    static {
        REQUEST_TYPES = new String[REQUEST_TYPES_COUNT];
        REQUEST_TYPES[0] = "popular";
        REQUEST_TYPES[1] = "top_rated";
    }

    public static String getRequestType(int idx) {
        return REQUEST_TYPES[idx];
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

    public static String buildMovieURI(String request_type)
    {
        Uri builtUri = Uri.parse(OpenMovieConfig.MD_BASE_URL).buildUpon()
                .appendPath(request_type)
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
}