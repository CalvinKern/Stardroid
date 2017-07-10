package com.seakernel.stardroid;

import android.content.Context;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 2/27/16.
 */
public class Utilities {
    public static List<PlayerRecord> readPlayerInfo(Context context) {
        try {
            FileInputStream is = context.openFileInput("players.dat");
            ObjectInputStream reader = new ObjectInputStream(is);
            ArrayList<String> players = (ArrayList<String>)reader.readObject();

            is = context.openFileInput("scores.dat");
            reader = new ObjectInputStream(is);
            ArrayList<Integer> scores = (ArrayList<Integer>)reader.readObject();

            return getPlayerInfoList(players, scores);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Known issue -- 07/09/2017
        }

        return null;
    }

    private static List<PlayerRecord> getPlayerInfoList(List<String> players, List<Integer> scores) {
        // If either the players or the scores are null, return null
        if (players == null || scores == null) {
            return null;
        }

        int minSize = Math.min(players.size(), scores.size());
        // Create the list of records
        List<PlayerRecord> records = new ArrayList<>(minSize);
        PlayerRecord newRecord;
        for (int i = 0; i < minSize; i++) {
            newRecord = new PlayerRecord(players.get(i), scores.get(i));
            records.add(newRecord);
        }

        return records;
    }

    public static class PlayerRecord implements Serializable {
        private String mName;
        private int mScore;

        public PlayerRecord(String name, int score) {
            mName = name;
            mScore = score;
        }

        @Override
        public String toString() {
            return String.format("%s: %d", mName, mScore);
        }
    }
}
