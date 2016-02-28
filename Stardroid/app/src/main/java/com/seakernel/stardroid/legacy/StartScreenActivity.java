package com.seakernel.stardroid.legacy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seakernel.stardroid.R;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * This class represents the start screen that will let the user put in a name and start the game.
 *  After multiple games have been played, the user will be shown the top 5 high scores.
 * Created by Calvin on 12/9/13.
 */
public class StartScreenActivity extends Activity
{
    LinearLayout mainLayout = null;
    LinearLayout playerLayout = null;
    EditText playerNameText = null;
    Intent gameIntent = null;
    int GAME_ACTIVITY = 1;
    int GAME_OVER_ACTIVITY = 2;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        gameIntent = new Intent(this, MainGameActivity.class);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundResource(R.drawable.nebula);
        setContentView(mainLayout);

        TextView gameNameText = new TextView(this);
        gameNameText.setText("Stardroid");
        gameNameText.setTextColor(Color.WHITE);
        gameNameText.setTextSize(30f);
        mainLayout.addView(gameNameText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout newPlayerInfoLayout = new LinearLayout(this);
        newPlayerInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(newPlayerInfoLayout);

        Button startButton = new Button(this);
        startButton.setText("Play");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                gameIntent.putExtra("PlayerName", playerNameText.getText().toString());
                startActivityForResult(gameIntent, GAME_ACTIVITY);
            }
        });
        newPlayerInfoLayout.addView(startButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        playerNameText = new EditText(this);
        playerNameText.setText("Cadet 1");
        newPlayerInfoLayout.addView(playerNameText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        playerLayout = new LinearLayout(this);
        playerLayout.setOrientation(LinearLayout.VERTICAL);
        playerLayout.setGravity(Gravity.CENTER);

        readPlayerInfo(playerLayout);
        mainLayout.addView(playerLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == GAME_ACTIVITY)
            {
                Intent gameOverIntent = new Intent(this, GameOverActivity.class);
                gameOverIntent.putExtras(data.getExtras());
                startActivityForResult(gameOverIntent, GAME_OVER_ACTIVITY);
            }
            else if (requestCode == GAME_OVER_ACTIVITY)
            {
                gameIntent.putExtra("PlayerName", playerNameText.getText().toString());
                startActivityForResult(gameIntent, GAME_ACTIVITY);
            }
        }
        else if (resultCode == RESULT_CANCELED && requestCode == GAME_OVER_ACTIVITY)
        {
            mainLayout.removeView(playerLayout);

            playerLayout = new LinearLayout(this);
            playerLayout.setOrientation(LinearLayout.VERTICAL);
            playerLayout.setGravity(Gravity.CENTER);

            readPlayerInfo(playerLayout);
            mainLayout.addView(playerLayout);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    private void readPlayerInfo(LinearLayout layout)
    {
        try
        {
            FileInputStream is = openFileInput("players.dat");
            ObjectInputStream reader = new ObjectInputStream(is);
            ArrayList<String> players = (ArrayList<String>)reader.readObject();

            is = openFileInput("scores.dat");
            reader = new ObjectInputStream(is);
            ArrayList<Integer> scores = (ArrayList<Integer>)reader.readObject();

            for (int i = 0; i < players.size(); i++)
            {
                TextView playerText = new TextView(this);
                playerText.setText(players.get(i) + ": " + scores.get(i));
                playerText.setTextColor(Color.WHITE);
                layout.addView(playerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
