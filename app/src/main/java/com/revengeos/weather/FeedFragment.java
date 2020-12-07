package com.revengeos.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.revengeos.weather.util.LocaleUtils;
import com.revengeos.weathericons.WeatherIconsHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rjsv.expconslayout.ExpandableConstraintLayout;

public class FeedFragment extends Fragment {

    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "a9a5a8c0a12e5b11ae2fc673c8edf0c2";

    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final int permissionsRequestCode = 420;

    private TextView currentTemp;
    private TextView currentTempEnd;
    private TextView currentLocation;
    private TextView currentLocationEnd;
    private ImageView currentIcon;

    private ExpandableConstraintLayout currentMoreDataLayout;
    private View currentTouchLayer;

    private Location mLocation;

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            getCurrentData();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case permissionsRequestCode: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                        String locationProvider = locationManager.getBestProvider(criteria, true);
                        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestSingleUpdate(locationProvider, locationListener, null);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        currentTemp = v.findViewById(R.id.current_temperature);
        currentTempEnd = v.findViewById(R.id.current_temperature_end);
        currentLocation = v.findViewById(R.id.current_location);
        currentLocationEnd = v.findViewById(R.id.current_location_end);
        currentIcon = v.findViewById(R.id.current_icon);

        currentMoreDataLayout = v.findViewById(R.id.current_more);
        currentMoreDataLayout.collapse();
        currentMoreDataLayout.setAnimationDuration(700);

        currentTouchLayer = v.findViewById(R.id.current_touch_layer);
        currentTouchLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMoreDataLayout.toggle();
            }
        });

        requestPermissions(permissions, permissionsRequestCode);

        return v;
    }

    private void getCurrentData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(Double.toString(mLocation.getLatitude()), Double.toString(mLocation.getLongitude()), AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String temperature = LocaleUtils.Companion.getFormattedTemperature(weatherResponse.main.temp);

                    currentTemp.setText(temperature);
                    currentTempEnd.setText(temperature);
                    currentLocation.setText(weatherResponse.name);
                    currentLocationEnd.setText(weatherResponse.name);

                    int state = WeatherIconsHelper.Companion.mapConditionIconToCode(weatherResponse.weather.get(0).id,
                            weatherResponse.sys.sunrise, weatherResponse.sys.sunset);
                    currentIcon.setImageDrawable(getResources().getDrawable(WeatherIconsHelper.Companion.getDrawable(state, getContext())));

                    String stringBuilder = "Country: " +
                            weatherResponse.sys.country +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main.temp +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main.temp_min +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main.temp_max +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main.humidity +
                            "\n" +
                            "Sunrise: " +
                            weatherResponse.sys.sunrise +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main.pressure;

                    //weatherData.setText(stringBuilder);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                //weatherData.setText(t.getMessage());
            }
        });
    }
}