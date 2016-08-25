package apps.hensbri.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import apps.hensbri.popularmovies.data.OpenMovieConfig;
import apps.hensbri.popularmovies.data.ParcelableMovie;

/**
 * Fragment to provide main grid view for Popular Movies
 * and option to change view by
 */
public class MainFragment extends Fragment {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private ArrayAdapter<ParcelableMovie> mMoviesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        mMoviesAdapter =
                new ArrayAdapter<ParcelableMovie>(
                        getActivity(),
                        R.layout.main_movie_grid_item,
                        R.id.grid_item_movie_imageview,
                        new ArrayList<ParcelableMovie>()) {

                    public View getView(int position, View convertView, ViewGroup container) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(
                                    getContext()).inflate(
                                    R.layout.main_movie_grid_item,
                                    container,
                                    false);
                        }

                        ParcelableMovie movie = mMoviesAdapter.getItem(position);

                        ImageView moviePoster = (ImageView) convertView.findViewById(
                                R.id.grid_item_movie_imageview);

                        Picasso.with(getContext())
                                .load(movie.getPosterURLString(getString(R.string.movie_poster_grid_size)))
                                .fit()
                                .centerInside()
                                .into(moviePoster);

                        return convertView;
                    }
                };

        // Used http://www.broculos.net/2013/09/how-to-change-spinner-text-size-color.html#.V76HT5MrLBI
        // as an example for setting spinner style
        // Setup view by spinner
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.movie_selector_labels, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.movie_selector);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateMovies(getRequestType(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Setup gridView
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ParcelableMovie movie = mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("movie", movie);
                startActivity(intent);
            }
        });

        updateMovies(getRequestType(spinner.getSelectedItemPosition()));

        return rootView;
    }

    private void showMessage(String t) {
        Toast.makeText(getContext(), t, Toast.LENGTH_SHORT).show();
    }

    private String getRequestType(int position) {
        return OpenMovieConfig.getRequestType(position);
    }

    public void updateMovies(String request_type) {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(request_type);
    }

    // Source: Used Sunshine lesson as guide
    public class FetchMoviesTask extends AsyncTask<String, Void, ParcelableMovie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ParcelableMovie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OpenMovieConfig.OM_RESULTS);

            ParcelableMovie[] movies = new ParcelableMovie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieObj = movieArray.getJSONObject(i);

                String poster = movieObj.getString(OpenMovieConfig.OM_POSTER);
                String title = movieObj.getString(OpenMovieConfig.OM_TITLE);
                String overview = movieObj.getString(OpenMovieConfig.OM_OVERVIEW);
                float rating = (float) movieObj.getDouble(OpenMovieConfig.OM_VOTE_AVG);
                String release_date = movieObj.getString(OpenMovieConfig.OM_RELEASE_DATE);
                movies[i] = new ParcelableMovie(title,
                        overview,
                        poster,
                        rating,
                        release_date);
            }

            return movies;
        }

        @Override
        protected ParcelableMovie[] doInBackground(String... params) {

            //First param is request_type option
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                URL url = new URL(OpenMovieConfig.buildMovieURI(params[0]));

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
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                showMessage(e.getMessage());
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
                        showMessage(e.getMessage());
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                showMessage(e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ParcelableMovie[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for (ParcelableMovie m : result) {
                    mMoviesAdapter.add(m);
                }
            }
        }
    }
}