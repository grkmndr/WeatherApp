package adesso.com.weatherapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import adesso.com.weatherapp.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Double lat;
    private Double lon;
    private Marker mMarker;

    private Button mButtonCancel;
    private Button mButtonAddBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        mButtonCancel = (Button) findViewById(R.id.btn_cancel);
        mButtonAddBookmark =(Button) findViewById(R.id.btn_add_bookmark);

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_LAT") && intent.hasExtra("EXTRA_LON")){
            lat = intent.getDoubleExtra("EXTRA_LAT", 0.0);
            lon = intent.getDoubleExtra("EXTRA_LON", 0.0);
        }

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                googleMap.clear();
                mMarker = googleMap.addMarker(new MarkerOptions().position(point)
                .draggable(true));

                Log.d("System out", "onMapClick..."+mMarker.getPosition().latitude+"..."+mMarker.getPosition().longitude);
            }
        });


        LatLng currentLocation = new LatLng(lat, lon);
        mMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation)
                .draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

    }

    public void onClickAddBookmarkButton(View v) {
        Intent intent = new Intent();
        intent.putExtra("EXTRA_BOOKMARK_LAT", mMarker.getPosition().latitude);
        intent.putExtra("EXTRA_BOOKMARK_LON", mMarker.getPosition().longitude);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancelButton(View v) {
        onBackPressed();
    }
}
