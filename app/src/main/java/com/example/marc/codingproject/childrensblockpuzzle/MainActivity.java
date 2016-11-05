package com.example.marc.codingproject.childrensblockpuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroupThemeChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioGroupThemeChoice = (RadioGroup) findViewById(R.id.radio);
    }

    public void startGame(View view) {
        int gameMode = -1;
        int checkedRadioButtonId = radioGroupThemeChoice.getCheckedRadioButtonId();
        switch (checkedRadioButtonId) {
            case R.id.radio_animals:
                gameMode = GameBoardActivity.ANIMALS_THEME;
                break;
            case R.id.radio_flowers:
                gameMode = GameBoardActivity.FLOWERS_THEME;
                break;
            case R.id.radio_vehicles:
                gameMode = GameBoardActivity.VEHICLES_THEME;
                break;
        }
        if (checkedRadioButtonId != -1) {
            Intent intent = new Intent(this, GameBoardActivity.class);
            intent.putExtra(GameBoardActivity.EXTRA_GAME_THEME, gameMode);
            startActivity(intent);
        }
    }

    public void goStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}
