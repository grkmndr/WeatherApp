package adesso.com.weatherapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import adesso.com.weatherapp.R;
import adesso.com.weatherapp.models.WeatherModel;
import adesso.com.weatherapp.realm.RealmController;
import io.realm.Realm;

/**
 * Created by macapple on 07/06/2017.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private WeatherModel[] mWeatherData;

    final private ForecastAdapterOnClickHandler mClickHandler;

    private Realm realm;

    public interface ForecastAdapterOnClickHandler {
        void onClick(WeatherModel weatherForDay);
    }


    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mCityTextView;
        public final TextView mTemperatureTextView;

        public final ImageView mWeatherIconImageView;

        public ForecastAdapterViewHolder(View view) {

            super(view);

            mCityTextView = (TextView) view.findViewById(R.id.tv_city_name);
            mTemperatureTextView = (TextView) view.findViewById(R.id.tv_temperature);

            mWeatherIconImageView = (ImageView) view.findViewById(R.id.iv_weather_icon);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            WeatherModel weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {

        //realm = RealmController.getInstance().getRealm();

        WeatherModel weatherForThisDay = mWeatherData[position];

        holder.mCityTextView.setText(weatherForThisDay.getCityName());
        holder.mTemperatureTextView.setText(weatherForThisDay.getTemperature());

        /*try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(weatherForThisDay.getWeatherIconUrl().toString()).getContent());
            holder.mWeatherIconImageView.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null) return 0;
        return mWeatherData.length;
    }

    public void setWeatherData(WeatherModel[] WeatherData) {
        mWeatherData = WeatherData;
        notifyDataSetChanged();
    }
}
