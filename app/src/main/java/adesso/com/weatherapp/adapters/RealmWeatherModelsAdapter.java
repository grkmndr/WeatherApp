package adesso.com.weatherapp.adapters;

import android.content.Context;

import adesso.com.weatherapp.models.WeatherModel;
import io.realm.RealmResults;

/**
 * Created by macapple on 08/06/2017.
 */

public class RealmWeatherModelsAdapter extends RealmModelAdapter<WeatherModel> {

    public RealmWeatherModelsAdapter(Context context, RealmResults<WeatherModel> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
