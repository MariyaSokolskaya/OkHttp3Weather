package com.example.okhttp3weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.okhttp3weather.data.WeatherData;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText cityText;
    AppCompatButton loadButton;
    TextView tempText, tempLikeText, descrText, humidityText, speedText, sunriseText;
    OkHttpClient okHttpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityText = findViewById(R.id.city);
        loadButton = findViewById(R.id.load);
        tempText = findViewById(R.id.temp);
        tempLikeText = findViewById(R.id.temp_like);
        descrText = findViewById(R.id.decription);
        humidityText = findViewById(R.id.huminity);
        speedText = findViewById(R.id.speed);
        sunriseText = findViewById(R.id.sunrise);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityText.getText().toString();
                okHttpClient = new OkHttpClient();
                HttpUrl.Builder urlBuilder =
                        HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather")
                                .newBuilder();
                urlBuilder.addQueryParameter("q", city);
                urlBuilder.addQueryParameter("appid", "8a84eb1741e330c3b10588dc09920ed7");
                urlBuilder.addQueryParameter("units", "metric");
                urlBuilder.addQueryParameter("lang", "ru");
                String url = urlBuilder.build().toString();
                Request request = new Request.Builder().url(url).build();
                okHttpClient.newCall(request).enqueue(new CallRequest());

            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String jsonStr = (String) msg.obj;
            if (msg.arg1 != -1){
                Gson gson = new Gson();
                WeatherData weatherData = gson.fromJson(jsonStr, WeatherData.class);
                showInfo(weatherData);
            }else{
                descrText.setText(jsonStr);
            }
        }
    };
    void showInfo(WeatherData weatherData){
        String info = Double.toString(weatherData.main.temp);
        tempText.setText(info);
        info = Double.toString(weatherData.main.feelsLike);
        tempLikeText.setText(info);
        info = Integer.toString(weatherData.main.humidity);
        humidityText.setText(info);
        descrText.setText(weatherData.weather[0].description);
        info = Double.toString(weatherData.wind.speed);
        speedText.setText(info);
        Date date = new Date();
        date.setTime(weatherData.sys.sunrise);
        sunriseText.setText(date.toString());
    }
    class CallRequest implements Callback{
        String result = "";
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            result = "Ошибка соединения";
            Log.i("Weather failure", result);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (response.isSuccessful() && response.code() == 200){
                String jsonStr = response.body().string();
                Message message = new Message();
                message.obj = jsonStr;
                handler.sendMessage(message);
            }else{
                String res = "Произошла ошибка: " + response.code();
                Message message = new Message();
                message.obj = res;
                message.arg1 = -1;
                handler.sendMessage(message);
            }
        }
    }
}