package adesso.com.weatherapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.DetectedActivity;

import java.io.InputStream;

import adesso.com.weatherapp.R;
import adesso.com.weatherapp.activities.MainActivity;
import adesso.com.weatherapp.activities.WeatherDetailActivity;
import adesso.com.weatherapp.models.WeatherModel;
import adesso.com.weatherapp.realm.RealmController;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by macapple on 07/06/2017.
 */

//public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
public class ForecastAdapter extends RealmRecyclerViewAdapter<WeatherModel> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public ForecastAdapter(Context context) {

        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        // get the article
        final WeatherModel weatherModel = getItem(position);
        // cast the generic view holder to our specific one
        final ForecastViewHolder holder = (ForecastViewHolder) viewHolder;

        // set the title and the snippet
        holder.textCityName.setText(weatherModel.getCityName());
        holder.textTemperature.setText(weatherModel.getTemperature());

        //holder.imageWeatherIcon.setImageIcon(weatherModel.getWeatherIconUrl());
        if (weatherModel.getWeatherIconUrl() != null) {
            new DownloadImageTask(holder.imageWeatherIcon).execute("https://openweathermap.org/img/w/" + weatherModel.getWeatherIconUrl() + ".png");
        }
        // load the background image
        /*if (book.getImageUrl() != null) {
            Glide.with(context)
                    .load(book.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageBackground);
        }*/

        //remove single match from realm
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View content = inflater.inflate(R.layout.alert_dialog, null);
                final TextView errorMessage = (TextView) content.findViewById(R.id.delete_alert_text);
                errorMessage.setText(R.string.delete_alert_message);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<WeatherModel> results = realm.where(WeatherModel.class).findAll();

                                // Get the book title to show it in toast message
                                WeatherModel w = results.get(position);
                                String cityName = w.getCityName();

                                // All changes to data must happen in a transaction
                                realm.beginTransaction();

                                // remove single match
                                results.remove(position);
                                realm.commitTransaction();

                                notifyDataSetChanged();

                                Toast.makeText(context, cityName + " is removed from the bookmarks", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });

        //open single match from realm
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent showDetailActivityIntent = new Intent(context, WeatherDetailActivity.class);

                showDetailActivityIntent.putExtra("DETAIL_EXTRA_LAT", Double.parseDouble(weatherModel.getLat()));
                showDetailActivityIntent.putExtra("DETAIL_EXTRA_LON", Double.parseDouble(weatherModel.getLon()));

                context.startActivity(showDetailActivityIntent);
            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {

        public TextView textTemperature;
        public TextView textCityName;
        public ImageView imageWeatherIcon;

        public ForecastViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            textCityName = (TextView) itemView.findViewById(R.id.tv_city_name);
            textTemperature = (TextView) itemView.findViewById(R.id.tv_temperature);
            imageWeatherIcon = (ImageView) itemView.findViewById(R.id.iv_weather_icon);
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
