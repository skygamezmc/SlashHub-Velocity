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

    public HubCommand(SlashHub slashHub, ProxyServer server) {
        this.server = server;
        this.slashHub = slashHub;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            parsed = messageFormatter.Format(miniMessage, slashHub.CannotExecuteOnConsole);
            source.sendMessage(parsed);
            //source.sendMessage(Component.text(Main.CannotExecuteOnConsole.replace('&', '§')));
            return;
        }

        Player player = (Player) source;

        if (!player.hasPermission("slashhub.use")) {
            parsed = messageFormatter.Format(miniMessage, slashHub.NoPermission);
            player.sendMessage(parsed);
            return;
        }

        if (slashHub.BlockedServers != null) {
            if (slashHub.BlockedServers.contains(player.getCurrentServer().get().getServerInfo().getName()) && !player.hasPermission("slashhub.bypass")) {
                parsed = messageFormatter.Format(miniMessage, slashHub.ServerDisabled);
                player.sendMessage(parsed);
                return;
            }
        }


        Random rand = new Random();
        String RandomServer = slashHub.TargetServers.get(rand.nextInt(slashHub.TargetServers.size()));

        if (!server.getServer(RandomServer).isPresent()) {
            parsed = messageFormatter.Format(miniMessage, slashHub.ServerNotFound);
            player.sendMessage(parsed);
            System.out.println("[SlashHub] Player " + player.getUsername() + " has attempted to connect to server " + RandomServer + " However this server was not found in your Velocity Config");

            return;
        }

        if (slashHub.TargetServers.contains(player.getCurrentServer().get().getServerInfo().getName())) {
            parsed = messageFormatter.Format(miniMessage, slashHub.AlreadyOnServer);
            source.sendMessage(parsed);
            return;
        }

        Optional<RegisteredServer> TargetServer = server.getServer(RandomServer);
        parsed = messageFormatter.Format(miniMessage, slashHub.ConnectingMessage);
        source.sendMessage(parsed);
        player.createConnectionRequest(TargetServer.get()).fireAndForget();
    }
}
