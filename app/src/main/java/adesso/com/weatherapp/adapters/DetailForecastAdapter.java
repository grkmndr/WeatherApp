package adesso.com.weatherapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import adesso.com.weatherapp.R;
import adesso.com.weatherapp.models.WeatherModel;

/**
 * Created by macapple on 12/06/2017.
 */

public class DetailForecastAdapter extends RecyclerView.Adapter<DetailForecastAdapter.DetailForecastViewHolder> {

    private WeatherModel[] mWeatherModels;

    public DetailForecastAdapter(WeatherModel[] weatherModels) {
        mWeatherModels = weatherModels;
    }

    @Override
    public DetailForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_detail_list_item, parent, false);
        return new DetailForecastViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(DetailForecastViewHolder holder, int position) {

    }

    public int getItemCount() {
        return mWeatherModels.length;
    }

    public static class DetailForecastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mItemWeatherIcon;
        private TextView mItemTemperature;
        private TextView mItemHumidity;
        private TextView mItemRain;
        private TextView mItemWind;

        private static final String PHOTO_KEY = "PHOTO";

        //4
        public DetailForecastViewHolder(View v) {
            super(v);

            mItemWeatherIcon = (ImageView) v.findViewById(R.id.iv_detail_item_weather_icon);
            mItemTemperature = (TextView) v.findViewById(R.id.tv_detail_item_temperature);
            mItemHumidity = (TextView) v.findViewById(R.id.tv_detail_item_humidity);
            mItemRain = (TextView) v.findViewById(R.id.tv_detail_item_rain);
            mItemWind = (TextView) v.findViewById(R.id.tv_detail_item_wind);
            v.setOnClickListener(this);
        }

        //5
        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }
    }
}
