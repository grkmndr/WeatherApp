package adesso.com.weatherapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import adesso.com.weatherapp.R;
import adesso.com.weatherapp.adapters.ForecastAdapter;
import adesso.com.weatherapp.adapters.RealmWeatherModelsAdapter;
import adesso.com.weatherapp.models.WeatherModel;
import adesso.com.weatherapp.realm.RealmController;
import adesso.com.weatherapp.utilities.NetworkUtils;
import adesso.com.weatherapp.utilities.OpenWeatherJsonUtils;
import io.realm.Realm;

//import adesso.com.weatherapp.app.Prefs;
import io.realm.RealmResults;

public class MainActivity extends
        AppCompatActivity implements
        //ForecastAdapter.ForecastAdapterOnClickHandler,
        //LoaderCallbacks<WeatherModel[]>,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Realm realm;

    private View mForecastView;

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;
    private TextView mCurrentCityName;
    private TextView mCurrentCityTemperature;

    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;
    private static final int CURRENT_LOCATION_WEATHER_LOADER_ID = 1;

    private Double lat;
    private Double lon;
    private Double mNewBookmarkLat;
    private Double mNewBookmarkLon;
    private boolean mBookmarkAdded = false;
    private LocationManager locationManager;


    private String provider;

    private LoaderCallbacks<WeatherModel[]> forecastLoaderListener
            = new LoaderCallbacks<WeatherModel[]>() {
        @Override
        public Loader<WeatherModel[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<WeatherModel[]>(MainActivity.this) {

                /* This String array will hold and help cache our weather data */
                WeatherModel[] mWeatherData = null;

                /**
                 * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
                 */
                @Override
                protected void onStartLoading() {
                    if (mWeatherData != null) {
                        deliverResult(mWeatherData);
                    } else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                /**
                 * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                 * from OpenWeatherMap in the background.
                 *
                 * @return Weather data from OpenWeatherMap as an array of Strings.
                 *         null if an error occurs
                 */
                @Override
                public WeatherModel[] loadInBackground() {

                    String locationQuery = "Antalya";

                    URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery, "forecast");

                    try {
                        String jsonWeatherResponse = NetworkUtils
                                .getResponseFromHttpUrl(weatherRequestUrl);

                        WeatherModel[] simpleJsonWeatherData = OpenWeatherJsonUtils
                                .getWeatherModelsFromJson(MainActivity.this, jsonWeatherResponse);

                        return simpleJsonWeatherData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                /**
                 * Sends the result of the load to the registered listener.
                 *
                 * @param data The result of the load
                 */
                public void deliverResult(WeatherModel[] data) {
                    mWeatherData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<WeatherModel[]> loader, WeatherModel[] data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            //mForecastAdapter.setWeatherData(data);

            if (null == data) {
                showErrorMessage();
            } else {
                showWeatherDataView();
            }
        }

        @Override
        public void onLoaderReset(Loader<WeatherModel[]> loader) {

        }
    };

    private LoaderCallbacks<WeatherModel> currentLocationWeatherLoaderListener
            = new LoaderCallbacks<WeatherModel>() {
        @Override
        public Loader<WeatherModel> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<WeatherModel>(MainActivity.this) {

                /* This String array will hold and help cache our weather data */
                private WeatherModel mCurrentLocationWeatherData = null;

                /**
                 * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
                 */
                @Override
                protected void onStartLoading() {
                    if (mCurrentLocationWeatherData != null) {
                        deliverResult(mCurrentLocationWeatherData);
                    } else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                /**
                 * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                 * from OpenWeatherMap in the background.
                 *
                 * @return Weather data from OpenWeatherMap.
                 *         null if an error occurs
                 */
                @Override
                public WeatherModel loadInBackground() {
                    URL requestUrl;

                    if (!mBookmarkAdded) {
                        requestUrl = NetworkUtils.buildCurrentLocationUrl(lat.toString(), lon.toString(), "forecast");
                    } else {
                        requestUrl = NetworkUtils.buildCurrentLocationUrl(mNewBookmarkLat.toString(), mNewBookmarkLon.toString(), "forecast");
                    }
                    try {
                        String jsonWeatherResponse = NetworkUtils
                                .getResponseFromHttpUrl(requestUrl);

                        WeatherModel[] simpleJsonWeatherData = OpenWeatherJsonUtils
                                .getWeatherModelsFromJson(MainActivity.this, jsonWeatherResponse);

                        return simpleJsonWeatherData[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                /**
                 * Sends the result of the load to the registered listener.
                 *
                 * @param data The result of the load
                 */
                public void deliverResult(WeatherModel data) {
                    mCurrentLocationWeatherData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<WeatherModel> loader, WeatherModel data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null) {
                Log.d(TAG, "BOOKMARK CITY: " + data.getCityName());

                if (!mBookmarkAdded) {
                    mCurrentCityName.setText(data.getCityName());
                    mCurrentCityTemperature.setText(data.getTemperature());
                } else {
                    data.setId((int)System.currentTimeMillis());
                    data.setLat(mNewBookmarkLat.toString());
                    data.setLon(mNewBookmarkLon.toString());
                    realm.beginTransaction();
                    realm.copyToRealm(data);
                    realm.commitTransaction();
                    mForecastAdapter.notifyDataSetChanged();
                    mForecastAdapter.notify();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<WeatherModel> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.realm = RealmController.with(this).getRealm();

        //deleteAllRealmData();
        //setRealmData();

        mRecyclerView = (RecyclerView) findViewById(R.id.bookmark_recycler_list);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mCurrentCityName = (TextView) findViewById(R.id.tv_current_city_name);
        mCurrentCityTemperature = (TextView) findViewById(R.id.tv_current_city_temperature);

        setRealmAdapter(RealmController.with(this).getWeatherModels());

        //LoaderCallbacks<WeatherModel[]> callback = MainActivity.this;

        mBookmarkAdded = false;
        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, forecastLoaderListener);
        getSupportLoaderManager().initLoader(CURRENT_LOCATION_WEATHER_LOADER_ID, null, currentLocationWeatherLoaderListener);

        fetchLocation();

        FloatingActionButton bookmarkFab = (FloatingActionButton) findViewById(R.id.fab_add_bookmark);
        bookmarkFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent showMapActivityIntent = new Intent(MainActivity.this, MapActivity.class);

                showMapActivityIntent.putExtra("EXTRA_LAT", lat);
                showMapActivityIntent.putExtra("EXTRA_LON", lon);

                /*
                 * Once the Intent has been created, we can use Activity's method, "startActivity"
                 * to start the ChildActivity.
                 */
                startActivityForResult(showMapActivityIntent, 1);
            }
        });

    }

    private void invalidateData() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            invalidateData();
            mBookmarkAdded = false;
            getSupportLoaderManager().restartLoader(CURRENT_LOCATION_WEATHER_LOADER_ID, null, currentLocationWeatherLoaderListener);
            mForecastAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public void fetchLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        Log.d(TAG, "LOCATION: " + lat + " " + lon);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mBookmarkAdded = true;

                Double tempLat = lat;
                Double tempLon = lon;

                mNewBookmarkLat = data.getDoubleExtra("EXTRA_BOOKMARK_LAT", tempLat);
                mNewBookmarkLon = data.getDoubleExtra("EXTRA_BOOKMARK_LON", tempLon);

                Log.d(TAG, "BOOKMARK LOC: " + mNewBookmarkLat + ", " + mNewBookmarkLon);

                getSupportLoaderManager().restartLoader(CURRENT_LOCATION_WEATHER_LOADER_ID, null, currentLocationWeatherLoaderListener);

            }
        }
    }

    public void setRealmAdapter(RealmResults<WeatherModel> weatherModels) {

        RealmWeatherModelsAdapter realmAdapter = new RealmWeatherModelsAdapter(this.getApplicationContext(), weatherModels, true);
        // Set the data and tell the RecyclerView to draw
        mForecastAdapter.setRealmAdapter(realmAdapter);
        mForecastAdapter.notifyDataSetChanged();
    }

    private void setRealmData() {

        ArrayList<WeatherModel> weatherModels = new ArrayList<>();

        WeatherModel weatherModel = new WeatherModel();
        weatherModel.setId(1 + (int)System.currentTimeMillis());
        weatherModel.setCityName("Antalya");
        weatherModels.add(weatherModel);

        for (WeatherModel w : weatherModels) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(w);
            realm.commitTransaction();
        }
    }

    private void deleteAllRealmData() {
        
        RealmResults<WeatherModel> results = realm.where(WeatherModel.class).findAll();

        realm.beginTransaction();
        results.clear();
        realm.commitTransaction();

    }
}
