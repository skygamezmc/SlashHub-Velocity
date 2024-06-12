package me.skygamez.slashhub.Commands;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import me.skygamez.slashhub.SlashHub;
import me.skygamez.slashhub.Utils.MessageFormatter;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SlashHubCommand implements SimpleCommand {

    MiniMessage miniMessage = MiniMessage.miniMessage();
    private final MessageFormatter messageFormatter = new MessageFormatter();

    public SlashHub slashHub;
    private Path folder;

    public SlashHubCommand (SlashHub slashHub, @DataDirectory final Path folder) {
        this.slashHub = slashHub;
        this.folder = folder;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (invocation.arguments().length == 0) {
            source.sendMessage(messageFormatter.Format(miniMessage, "&b------------ &9SlashHub &b------------\n" +
                    "\n" +
                    "&8 * &9Version 1.7 &7- &9 by SkyGameZ\n" +
                    "\n" +
                    "&8 * &9Subcommands:\n" +
                    "&7    - &a/slashhub reload\n" +
                    "\n" +
                    "&b----------------------------------"));
            return;
        }

        switch (invocation.arguments()[0]) {
            case "reload":
                if ((!source.hasPermission("slashhub.admin.reload") || !source.hasPermission("slashhub.reload") || !source.hasPermission("slashhub.admin.*")) && source instanceof Player) {
                    source.sendMessage(messageFormatter.Format(miniMessage, "&cYou do not have permission to run this command!"));
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
                slashHub.RequireUsePermission = toml.getBoolean("RequireUsePermission");
                slashHub.BlockedServers = toml.getList("DisabledServers");

                source.sendMessage(messageFormatter.Format(miniMessage, slashHub.ReloadedPlugin));
                return;
        }
    }

    private Toml config(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        return new Toml().read(file);
    }


    @Override
    public boolean hasPermission(final Invocation invocation) {
        return (invocation.source().hasPermission("slashhub.admin"));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        List<String> argsList = new ArrayList<>();
        if (invocation.arguments().length == 0) {
            argsList.clear();
            argsList.add("reload");
        }
        return CompletableFuture.completedFuture(argsList);
    }
}
