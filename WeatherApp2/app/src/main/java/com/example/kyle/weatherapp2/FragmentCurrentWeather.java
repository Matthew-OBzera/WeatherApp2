package com.example.kyle.weatherapp2;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FragmentCurrentWeather extends Fragment implements Downloader.DownloadListener<JSONObject> {

    private Button goButton;
    private EditText zipcodeText;
    private TextView currentTimeText, conditionText, tempText, dewPointText, relHumidityText, pressureText, visibilityText,
            windspeedText, gustsText;
    private RadioGroup radGrp;

    private ImageView image;

    private double temperature, dewPoint, humidity, pressure, visibility, windspeed, gusts;
    private String windDirection, timeStamp, conditions;

    private boolean go = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate((R.layout.currentweather_fragment), container, false);
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        setHasOptionsMenu(true);
        getTextView(view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        if (v != null) {
            goButton = (Button) v.findViewById(R.id.goButton);
        }


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("got here2");
                Context context = getActivity();
                String zipcode = zipcodeText.getText().toString();
                getLocation(zipcode);

            }
        });


        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int id) {
                if (go) {
                    setText(id);
                }
            }
        });
    }

    private void setText(int id) {
        String timeStampStr, conditionsStr, tempStr, dewStr, presStr, visStr, windStr, gustStr, humidStr;
        switch (id) {
            case R.id.imperial:
                System.out.println("got here3");
                timeStampStr = timeStamp;
                conditionsStr = conditions;
                tempStr = String.valueOf((int) temperature) + "° F";
                dewStr = String.valueOf((int) dewPoint) + "° F";
                humidStr = String.valueOf((int) humidity) + "%";
                presStr = String.valueOf((int) pressure) + " in";
                visStr = String.valueOf((int) visibility) + " mi";
                windStr = windDirection + " @ "
                        + String.valueOf((int) windspeed) + " mph";
                if (Double.isNaN(gusts)) {
                    gustStr = "NA";
                } else {
                    gustStr = String.valueOf((int) (gusts)) + " mph";
                }
                break;
            case R.id.metric:
                timeStampStr = timeStamp;
                conditionsStr = conditions;
                tempStr = String.valueOf((int) ((temperature - 32) * (5.0 / 9.0))) + "° C";
                dewStr = String.valueOf((int) ((dewPoint - 32) * (5.0 / 9.0))) + "° C";
                humidStr = String.valueOf((int) humidity) + "%";
                presStr = String.valueOf((int) (pressure * 2.54)) + " cm";
                visStr = String.valueOf((int) (visibility * 1.6093)) + " km";
                windStr = windDirection + " @ "
                        + String.valueOf((int) (windspeed * 1.6093)) + " km/h";
                if (Double.isNaN(gusts)) {
                    gustStr = "NA";
                } else {
                    gustStr = String.valueOf((int) (gusts * 1.6093)) + " km/h";
                }

                break;
            default:
                timeStampStr = "";
                conditionsStr = "";
                tempStr = "";
                dewStr = "";
                humidStr = "";
                presStr = "";
                visStr = "";
                windStr = "";
                gustStr = "";
                break;
        }
        currentTimeText.setText(timeStampStr);
        conditionText.setText(conditionsStr);
        tempText.setText(tempStr);
        dewPointText.setText(dewStr);
        relHumidityText.setText(humidStr);
        pressureText.setText(presStr);
        visibilityText.setText(visStr);
        windspeedText.setText(windStr);
        gustsText.setText(gustStr);
    }

    private void getTextView(View view) {

        zipcodeText = (EditText) view.findViewById(R.id.zipCode);
        currentTimeText = (TextView) view.findViewById(R.id.timeStampVal);
        conditionText = (TextView) view.findViewById(R.id.conditionsVal);
        tempText = (TextView) view.findViewById(R.id.temperatureVal);
        dewPointText = (TextView) view.findViewById(R.id.dewPointVal);
        relHumidityText = (TextView) view.findViewById(R.id.relativeHumidityVal);
        pressureText = (TextView) view.findViewById(R.id.pressureVal);
        visibilityText = (TextView) view.findViewById(R.id.visibilityVal);
        windspeedText = (TextView) view.findViewById(R.id.windSpeedVal);
        gustsText = (TextView) view.findViewById(R.id.gustsVal);
        radGrp = (RadioGroup) view.findViewById(R.id.radioGroup);
        image = (ImageView) view.findViewById(R.id.imageView);

    }

    private void setInfo(WeatherInfo info, Context context) {

        temperature = info.current.temperature;
        dewPoint = info.current.dewPoint;
        humidity = info.current.humidity;
        pressure = info.current.pressure;
        visibility = info.current.visibility;
        windspeed = info.current.windSpeed;
        gusts = info.current.gusts;

        windDirection = info.current.windDirectionStr();
        timeStamp = info.current.timestamp;
        conditions = info.current.summary;

        go = true;
        setText(radGrp.getCheckedRadioButtonId());


        Resources res = context.getResources();
        String mDrawableName = "img" + zipcodeText.getText();
        int resID = res.getIdentifier(mDrawableName, "drawable", getActivity().getPackageName());
        image.setImageResource(resID);
    }


    public void getLocation(String zipcode) {
        Downloader<JSONObject> downloadInfo = new Downloader<>(this);
        downloadInfo.execute("http://craiginsdev.com/zipcodes/findzip.php?zip=" + zipcode);
    }

    @Override
    public JSONObject parseResponse(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JSONObject jsonObject = new JSONObject(reader.readLine());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handleResult(JSONObject result) {
        WeatherInfoIO.WeatherListener weatherDownloaded = new WeatherInfoIO.WeatherListener() {
            @Override
            public void handleResult(WeatherInfo result) {
                if (result != null) {
                    setInfo(result, getActivity());
//                    if(!recentZipCodes.contains(currentZip)){
//                        recentZipCodes.addFirst(currentZip);
//                        if (recentZipCodes.size() > 5){
//                            recentZipCodes.removeLast();
//                        }
//                    }
                } else {
                    setText(-1);
                    image.setImageResource(0);
                    go = false;
                }
            }
        };
        try {
            String latitude = result.getString("latitude");
            String longitude = result.getString("longitude");
            WeatherInfoIO.loadFromUrl("http://forecast.weather.gov/MapClick.php?lat="
                            + latitude +
                            "&lon="
                            + longitude +
                            "&unit=0&lg=english&FcstType=dwml",
                    weatherDownloaded);

        } catch (JSONException e) {

        }
    }
}

