package com.woolmc.slashhub.Updater;

import com.woolmc.slashhub.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
    private URL url;
    private String newVersion;
    private Main localPlugin;
    private BufferedReader apiReader;

    public UpdateChecker(Main plugin) throws MalformedURLException {
        url = new URL("https://api.spigotmc.org/legacy/update.php?resource=101114");
        localPlugin = plugin;
    }

    public boolean updateRequired() throws IOException {
        URLConnection con = url.openConnection();
        apiReader = (new BufferedReader(new InputStreamReader(con.getInputStream())));
        newVersion = apiReader.readLine();
        apiReader.close();
        return !localPlugin.Version.equals(newVersion);
    }
}
