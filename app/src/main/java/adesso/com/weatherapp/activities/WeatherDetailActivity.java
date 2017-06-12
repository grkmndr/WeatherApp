package adesso.com.weatherapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import adesso.com.weatherapp.R;
import adesso.com.weatherapp.models.WeatherModel;
import adesso.com.weatherapp.utilities.NetworkUtils;
import adesso.com.weatherapp.utilities.OpenWeatherJsonUtils;

public class WeatherDetailActivity extends AppCompatActivity {

    private TextView mDetailCityName;
    private TextView mDetailCurrentDay;
    private TextView mDetailTemperature;
    private TextView mDetailHumidity;
    private TextView mDetailRain;
    private TextView mDetailWind;
    private ImageView mDetailWeatherIcon;

    private Double lat;
    private Double lon;

    private String mWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        mDetailCityName = (TextView) findViewById(R.id.tv_detail_city_name);
        mDetailCurrentDay = (TextView) findViewById(R.id.tv_detail_current_day);
        mDetailTemperature = (TextView) findViewById(R.id.tv_detail_temperature);
        mDetailHumidity = (TextView) findViewById(R.id.tv_detail_humidity);
        mDetailRain = (TextView) findViewById(R.id.tv_detail_rain);
        mDetailWind = (TextView) findViewById(R.id.tv_detail_wind);
        mDetailWeatherIcon = (ImageView) findViewById(R.id.iv_detail_weather_icon);


        Intent intent = getIntent();
        if (intent.hasExtra("DETAIL_EXTRA_LAT") && intent.hasExtra("DETAIL_EXTRA_LON")){
            lat = intent.getDoubleExtra("DETAIL_EXTRA_LAT", 0.0);
            lon = intent.getDoubleExtra("DETAIL_EXTRA_LON", 0.0);
        }

        new JsonTask().execute();
    }

    private class JsonTask extends AsyncTask<Void, WeatherModel, WeatherModel[]> {

        ProgressDialog pd;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(WeatherDetailActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected WeatherModel[] doInBackground(Void... voids) {

            URL requestUrl = NetworkUtils.buildCurrentLocationUrl(lat.toString(), lon.toString(), "forecast/daily");

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(requestUrl);

                WeatherModel[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getFiveDayForecastFromJSON(WeatherDetailActivity.this, jsonWeatherResponse);

                WeatherModel[] fiveDayForecastJSON = new WeatherModel[5];
                for (int i = 0; i < 5; i++) {
                    fiveDayForecastJSON[i] = simpleJsonWeatherData[i];
                }
                return fiveDayForecastJSON;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherModel[] result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            mDetailCityName.setText(result[0].getCityName());
            mDetailCurrentDay.setText(result[0].getDay());
            mDetailTemperature.setText(result[0].getTemperature());
            mDetailHumidity.setText(result[0].getHumidity());
            //mDetailRain.setText();
            mDetailWind.setText(result[0].getWindSpeed());

            new DownloadImageTask(mDetailWeatherIcon).execute("https://openweathermap.org/img/w/" + result[0].getWeatherIconUrl() + ".png");

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
