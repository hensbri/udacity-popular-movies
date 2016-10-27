package apps.hensbri.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "apps.hensbri.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES_LISTS        = "movie_lists";
    public static final String PATH_MOVIES              = "movies";
    public static final String PATH_MOVIE_WITH_FAVORITE = "vi_movie_with_favorite";
    public static final String PATH_REVIEWS             = "reviews";
    public static final String PATH_VIDEOS              = "videos";

    public static final class MovieListEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_LISTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_LISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES_LISTS;

        public static final String TABLE_NAME = "movie_lists";

        public static final String COLUMN_REQUEST_TYPE = "request_type";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildMovieListUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // URI for specific movie item in w/ request
        public static Uri buildMovieListWithRequestTypeUri(String requestType) {
            return CONTENT_URI.buildUpon()
                    .appendPath(requestType)
                    .build();
        }

        // Parse movie request_type from URI
        public static String getMovieRequestTypeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REQUEST_TYPE = "request_type";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH= "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_average";

        // URI for movie item using _ID
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // URI for specific movie item in w/ request + id
        public static Uri buildMovieWithRequestTypeUri(String requestType, long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        // Parse movie id from URI
        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL= "url";

        // URI for review item using movie_id
        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Parse movie id from URI
        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

        public static final String TABLE_NAME = "videos";

        /*
        "trailers":{
        "quicktime":[],
        "youtube":[{"name":"Trailer Hd","size":"HD","source":"WawU4ouldxU","type":"Trailer"},
                   {"name":"Trailer 2","size":"HQ","source":"K_tLp7T6U1c","type":"Trailer"},
                   {"name":"Peter Fonda On Hope And The Shawshank Redemption","size":"Standard","source":"9qqfMvKxBa0","type":"Featurette"}]}
         */

        public static final String COLUMN_ID       = "_ID";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME     = "name";
        public static final String COLUMN_SIZE     = "size";
        public static final String COLUMN_SOURCE   = "source";
        public static final String COLUMN_TYPE     = "type";

        // URI for review item using movie_id
        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Parse movie id from URI
        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class MovieWithFavoriteView implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_WITH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" + PATH_MOVIE_WITH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" + PATH_MOVIE_WITH_FAVORITE;

        public static final String TABLE_NAME = "vi_movie_with_favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH= "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieWithFavoriteViewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
