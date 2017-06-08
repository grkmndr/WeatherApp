package adesso.com.weatherapp.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Locale;

import adesso.com.weatherapp.models.WeatherModel;

/**
 * Created by macapple on 07/06/2017.
 */

public class OpenWeatherJsonUtils {

    public static WeatherModel[] getWeatherModelsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String OWM_LIST = "list";

        /* All temperatures are children of the "temp" object */
        final String OWM_TEMPERATURE = "temp";

        /* Max temperature for the day */
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        WeatherModel[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        String cityName = forecastJson.getJSONObject("city").getString("name");

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new WeatherModel[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = WeatherDateUtils.getUTCDateFromLocal(localDate);
        long startDay = WeatherDateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String temperature;

            /* These are the values that will be collected */
            long dateTimeMillis;

            /* Get the JSON object representing the day */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            dateTimeMillis = startDay + WeatherDateUtils.DAY_IN_MILLIS * i;
            date = WeatherDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            //temperature = Double.toString(dayForecast.getJSONObject("main").getDouble("temp"));
            temperature = String.format(Locale.ENGLISH, "%.0f", dayForecast.getJSONObject("main").getDouble("temp"));

            parsedWeatherData[i] = new WeatherModel(cityName);
            parsedWeatherData[i].setDay(date);
            parsedWeatherData[i].setTemperature(temperature);

        }

        return parsedWeatherData;
    }
}
