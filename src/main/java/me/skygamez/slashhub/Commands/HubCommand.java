package me.skygamez.slashhub.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.skygamez.slashhub.SlashHub;
import me.skygamez.slashhub.Utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HubCommand implements SimpleCommand {
    private final ProxyServer server;
    public SlashHub slashHub;

    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component parsed;

    private final MessageFormatter messageFormatter = new MessageFormatter();
    boolean requirePermission;

    public HubCommand(SlashHub slashHub, ProxyServer server, boolean requirePermission) {
        this.server = server;
        this.slashHub = slashHub;
        this.requirePermission = requirePermission;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            if (!slashHub.CannotExecuteOnConsole.isEmpty()) {
                parsed = messageFormatter.Format(miniMessage, slashHub.CannotExecuteOnConsole);
                source.sendMessage(parsed);
            }
            return;
        }

        Player player = (Player) source;

        if (slashHub.BlockedServers != null) {
            if (slashHub.BlockedServers.contains(player.getCurrentServer().get().getServerInfo().getName()) && !player.hasPermission("slashhub.bypass")) {
                if (!slashHub.ServerDisabled.isEmpty()) {
                    parsed = messageFormatter.Format(miniMessage, slashHub.ServerDisabled);
                    player.sendMessage(parsed);
                }
                return;
            }
        }


        Random rand = new Random();
        String RandomServer = slashHub.TargetServers.get(rand.nextInt(slashHub.TargetServers.size()));

        if (!server.getServer(RandomServer).isPresent()) {
            if (!slashHub.ServerNotFound.isEmpty()) {
                parsed = messageFormatter.Format(miniMessage, slashHub.ServerNotFound);
                player.sendMessage(parsed);
            }
            System.out.println("[SlashHub] Player " + player.getUsername() + " has attempted to connect to server " + RandomServer + " However this server was not found in your Velocity Config");

            return;
        }

        if (slashHub.TargetServers.contains(player.getCurrentServer().get().getServerInfo().getName())) {
            if (!slashHub.AlreadyOnServer.isEmpty()) {
                parsed = messageFormatter.Format(miniMessage, slashHub.AlreadyOnServer);
                source.sendMessage(parsed);
            }
            return;
        }

        Optional<RegisteredServer> TargetServer = server.getServer(RandomServer);
        if (!slashHub.ConnectingMessage.isEmpty()) {
            parsed = messageFormatter.Format(miniMessage, slashHub.ConnectingMessage);
            source.sendMessage(parsed);
        }
        player.createConnectionRequest(TargetServer.get()).fireAndForget();
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        if (requirePermission) {
            return invocation.source().hasPermission("slashhub.use");
        }
        return true;
    }
}
