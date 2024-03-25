package com.example.urban_management_app;

// necessary imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// simple class that checks a .txt file of bad words
public class BadWordsFilter {

    private List<String> badWords;

    public BadWordsFilter(InputStream inputStream) {
        badWords = new ArrayList<>();
        loadSwearWords(inputStream);
    }

    private void loadSwearWords(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsSwearWord(String input) {
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (badWords.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

