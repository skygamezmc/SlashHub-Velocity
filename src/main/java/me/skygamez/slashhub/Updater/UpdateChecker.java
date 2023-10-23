package me.skygamez.slashhub.Updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.skygamez.slashhub.SlashHub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private URL url;
    private String newVersion;
    private SlashHub localPlugin;
    private BufferedReader apiReader;

    public UpdateChecker(SlashHub plugin,  int Resource) throws MalformedURLException {
        url = new URL("https://api.spiget.org/v2/resources/" + Resource + "/versions/latest");
        localPlugin = plugin;
    }

    public boolean isUpdateRequired() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader apiReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = apiReader.readLine()) != null) {
                response.append(line);
            }

            // Parse the JSON response
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            newVersion = jsonResponse.get("name").getAsString();
        } finally {
            connection.disconnect();
        }

        return !localPlugin.Version.equals(newVersion);
    }
}
