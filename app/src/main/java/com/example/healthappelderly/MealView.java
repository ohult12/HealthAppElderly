package com.example.healthappelderly;

import android.os.Bundle;
import android.widget.TextView;
import java.util.Date;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class MealView extends AppCompatActivity {
    TextView tvDate, tvMealTime, tvMealInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_viewer);

        tvDate = findViewById(R.id.tvDate);
        tvMealTime = findViewById(R.id.tvNextMealTime);
        tvMealInfo = findViewById(R.id.tvMealInfo);

        SimpleDateFormat sdf = new SimpleDateFormat("'Date/n' yyyy-MM-dd");

        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);

    }

}
