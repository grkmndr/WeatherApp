package adesso.com.weatherapp.models;

import android.graphics.drawable.Icon;

import java.net.URL;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macapple on 07/06/2017.
 */

public class WeatherModel extends RealmObject {

    @PrimaryKey
    private int id;

    private String cityName = "";
    private String lat = "";
    private String lon = "";
    private String temperature = "";
    private String humidity = "";
    private String windSpeed = "";

    private String weatherIconUrl;

    private Date date;

    private String day;

    public WeatherModel() {}

    public WeatherModel(String cityName) {
        this.setCityName(cityName);
    }

    public WeatherModel(String lat, String lon) {
        this.setLat(lat);
        this.setLon(lon);
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getTemperature() {
        return temperature+"°";
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeatherIconUrl() {
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(String weatherIconUrl) {
        this.weatherIconUrl = weatherIconUrl;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
