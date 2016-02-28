package com.seakernel.stardroid.legacy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seakernel.stardroid.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * This class represents the game over screen.  It allows the user to review their game stats, play
 *  the game again, or return back to the main screen.
 *
 * Created by Calvin on 12/13/13.
 */
public class GameOverActivity extends Activity
{
    private String playerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create the main layout for the screen
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundResource(R.drawable.black_hole);
        setContentView(mainLayout);

        // Create a text view to display to the user the game is over
        TextView gameOverText = new TextView(this);
        gameOverText.setText("Game Over");
        gameOverText.setTextColor(Color.WHITE);
        gameOverText.setTextSize(30f);
        gameOverText.setGravity(Gravity.CENTER);
        mainLayout.addView(gameOverText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Create a layout to store all the information about the players game
        LinearLayout playerStatsLayout = new LinearLayout(this);
        playerStatsLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.addView(playerStatsLayout);

        // Get the information about the players game
        Bundle extras = getIntent().getExtras();

        // Create a variable to hold the player score for comparing later
        int playerScore = 0;

        if (extras == null)
            finish();
        else
        {
            // Pull the information about the players game from the bundle
            playerName = extras.getString("PlayerName");
            playerScore = extras.getInt("EnemiesKilled");
            int shipSpeed = extras.getInt("ShipSpeed");
            int firingRate = extras.getInt("FiringRate");

            // Display the number of enemies killed
            TextView enemiesKilledText = new TextView(this);
            enemiesKilledText.setText("Enemies Killed = " + playerScore);
            enemiesKilledText.setTextColor(Color.WHITE);
            playerStatsLayout.addView(enemiesKilledText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            // Display the final speed of the ship
            TextView shipSpeedText = new TextView(this);
            shipSpeedText.setText("Ship Speed = " + shipSpeed);
            shipSpeedText.setTextColor(Color.WHITE);
            playerStatsLayout.addView(shipSpeedText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            // Display the final firing rate
            TextView firingRateText = new TextView(this);
            firingRateText.setText("Firing Rate = " + firingRate);
            firingRateText.setTextColor(Color.WHITE);
            playerStatsLayout.addView(firingRateText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        }

        // Create a layout to contain the buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        mainLayout.addView(buttonLayout);

        // Create a button to play the game again
        Button playAgainButton = new Button(this);
        playAgainButton.setText("Play Again");
        playAgainButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("PlayerName", playerName);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        buttonLayout.addView(playAgainButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Create a button to return to the main screen
        Button mainScreenButton = new Button(this);
        mainScreenButton.setText("Title Screen");
        mainScreenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        buttonLayout.addView(mainScreenButton, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Determine if the top scores need to be rewritten
        ArrayList<String> playerList = readPlayerNames();
        ArrayList<Integer> scores = readPlayerScores()  ;
        if (newHighScore(playerName, playerScore, playerList, scores))
            savePlayerInfo(playerList, scores);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * This method will take in the current player and the previous top players to determine
     *  if a new high score has been made.
     *
     * @param playerName The name of the current player
     * @param score The score of the current player
     * @param playerList The previous top players names
     * @param scoreList The previous top players scores
     * @return if the top 5 scores have changed
     */
    private boolean newHighScore(String playerName, int score, ArrayList<String> playerList, ArrayList<Integer> scoreList)
    {
        // Check to see if less than five people are already in the top scores
        if (playerList.size() < 5)
        {
            // find the correct position to put the new score
            for (int i = 0; i <= playerList.size(); i++)
            {
                // if we've exhausted the list or the score is greater than the current score, add the player
                if (scoreList.size() == i || scoreList.get(i) < score)
                {
                    playerList.add(i, playerName);
                    scoreList.add(i, score);
                    return true;
                }
            }
        }
        else
            // loop through each player to determine where the new score should be added
            for (int player = 0; player < playerList.size(); player++)
            {
                // Check if the current player has a score higher than someone in the list
                if (scoreList.get(player) < score)
                {
                    // Add the player at the correct position
                    playerList.add(player, playerName);
                    scoreList.add(player, score);

                    // remove the lowest player
                    playerList.remove(5);
                    scoreList.remove(5);

                    return true;
                }
            }
        return false;
    }

    /**
     * A method for reading the names of players from memory
     * @return An array containing the scores for every saved player
     */
    private ArrayList<String> readPlayerNames()
    {
        ArrayList<String> playerList = new ArrayList<String>();

        try
        {
            FileInputStream is = openFileInput("players.dat");
            ObjectInputStream reader = new ObjectInputStream(is);
            playerList = (ArrayList<String>) reader.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return playerList;
    }

    /**
     * A method for reading the scores from memory
     *
     * @return An array containing the scores for every saved player
     */
    private ArrayList<Integer> readPlayerScores()
    {
        ArrayList<Integer> scoreList = new ArrayList<Integer>();

        try
        {
            FileInputStream is = openFileInput("scores.dat");
            ObjectInputStream reader = new ObjectInputStream(is);
            scoreList = (ArrayList<Integer>) reader.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return scoreList;
    }

    /**
     * A method for saving player info to memory
     *
     * @param playerList The players names to save
     * @param scores The corresponding scores to each player
     */
    private void savePlayerInfo(ArrayList<String> playerList, ArrayList<Integer> scores)
    {
        try
        {
            FileOutputStream os = openFileOutput("players.dat", MODE_PRIVATE);
            ObjectOutputStream output = new ObjectOutputStream(os);
            output.writeObject(playerList);

            os = openFileOutput("scores.dat", MODE_PRIVATE);
            output = new ObjectOutputStream(os);
            output.writeObject(scores);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
