package me.skygamez.slashhub.Commands;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import me.skygamez.slashhub.SlashHub;
import me.skygamez.slashhub.Utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.nio.file.Path;

public class ReloadCommand implements SimpleCommand {
    private Path folder;
    public SlashHub slashHub;

    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component parsed;

    private final MessageFormatter messageFormatter = new MessageFormatter();

    public ReloadCommand (SlashHub slashHub, @DataDirectory final Path folder) {
        this.folder = folder;
        this.slashHub = slashHub;
    }


    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            Toml toml = config(folder);
            slashHub.TargetServers = toml.getList("TargetServers");
            slashHub.ConnectingMessage = toml.getString("ConnectingMessage");
            slashHub.AlreadyOnServer = toml.getString("AlreadyOnHubMessage");
            slashHub.CannotExecuteOnConsole = toml.getString("CannotExecuteOnConsoleMessage");
            slashHub.ServerNotFound = toml.getString("ServerNotFound");
            slashHub.NoPermission = toml.getString("NoPermission");
            slashHub.ReloadedPlugin = toml.getString("ReloadedPlugin");

            parsed = messageFormatter.Format(miniMessage, slashHub.ReloadedPlugin);
            source.sendMessage(parsed);
            return;
        }

        Player player = (Player) source;


        if (!player.hasPermission("slashhub.reload")) {
            parsed = messageFormatter.Format(miniMessage, slashHub.NoPermission);
            player.sendMessage(parsed);
            return;
        }

        Toml toml = config(folder);
        slashHub.TargetServers = toml.getList("TargetServers");
        slashHub.ConnectingMessage = toml.getString("ConnectingMessage");
        slashHub.AlreadyOnServer = toml.getString("AlreadyOnHubMessage");
        slashHub.CannotExecuteOnConsole = toml.getString("CannotExecuteOnConsoleMessage");
        slashHub.ServerNotFound = toml.getString("ServerNotFound");
        slashHub.NoPermission = toml.getString("NoPermission");
        slashHub.ReloadedPlugin = toml.getString("ReloadedPlugin");

        parsed = messageFormatter.Format(miniMessage,slashHub.ReloadedPlugin);
        player.sendMessage(parsed);
    }

    private Toml config(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        return new Toml().read(file);
    }
}
