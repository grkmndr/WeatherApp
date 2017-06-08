package adesso.com.weatherapp.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import adesso.com.weatherapp.models.WeatherModel;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by macapple on 08/06/2017.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(WeatherModel.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<WeatherModel> getWeatherModels() {

        return realm.where(WeatherModel.class).findAll();
    }

    //query a single item with the given id
    public WeatherModel getWeatherModel(String id) {

        return realm.where(WeatherModel.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasWeatherModels() {

        return !realm.allObjects(WeatherModel.class).isEmpty();
    }

    //query example
    public RealmResults<WeatherModel> queryedWeatherModels() {

        return realm.where(WeatherModel.class)
                .contains("cityName", "Antalya")
                .findAll();

    }
}
