package com.woolmc.slashhub.Commands;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.InvocableCommand;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.woolmc.slashhub.Main;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.nio.file.Path;

public class ReloadCommand implements SimpleCommand {

    private Path folder;

    public ReloadCommand (@DataDirectory final Path folder) {
        this.folder = folder;
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            Toml toml = config(folder);
            Main.TargetServers = toml.getList("TargetServers");
            Main.ConnectingMessage = toml.getString("ConnectingMessage");
            Main.AlreadyOnServer = toml.getString("AlreadyOnHubMessage");
            Main.CannotExecuteOnConsole = toml.getString("CannotExecuteOnConsoleMessage");
            Main.ServerNotFound = toml.getString("ServerNotFound");
            Main.NoPermission = toml.getString("NoPermission");
            Main.ReloadedPlugin = toml.getString("ReloadedPlugin");

            source.sendMessage(Component.text(Main.ReloadedPlugin.replace('&', '§')));
            return;
        }

        Player player = (Player) source;


        if (!player.hasPermission("slashhub.reload")) {
            player.sendMessage(Component.text(Main.NoPermission.replace('&', '§')));
            return;
        }

        Toml toml = config(folder);
        Main.TargetServers = toml.getList("TargetServers");
        Main.ConnectingMessage = toml.getString("ConnectingMessage");
        Main.AlreadyOnServer = toml.getString("AlreadyOnHubMessage");
        Main.CannotExecuteOnConsole = toml.getString("CannotExecuteOnConsoleMessage");
        Main.ServerNotFound = toml.getString("ServerNotFound");
        Main.NoPermission = toml.getString("NoPermission");
        Main.ReloadedPlugin = toml.getString("ReloadedPlugin");

        player.sendMessage(Component.text(Main.ReloadedPlugin.replace('&', '§')));
    }

    private Toml config(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        return new Toml().read(file);
    }
}
