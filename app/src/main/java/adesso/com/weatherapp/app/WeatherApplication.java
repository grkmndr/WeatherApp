package adesso.com.weatherapp.app;

/**
 * Created by macapple on 08/06/2017.
 */
import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
