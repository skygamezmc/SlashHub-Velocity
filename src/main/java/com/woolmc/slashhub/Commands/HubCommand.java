package com.woolmc.slashhub.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.woolmc.slashhub.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HubCommand implements SimpleCommand {
    private final ProxyServer server;

    public HubCommand(ProxyServer server) {

        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(Component.text(Main.CannotExecuteOnConsole.replace('&', '§')));
            return;
        }

        Player player = (Player) source;

        if (!player.hasPermission("slashhub.use")) {
            player.sendMessage(Component.text(Main.NoPermission.replace('&', '§')));
            return;
        }

        Random rand = new Random();
        List<String> TargetServers = Main.TargetServers;
        String RandomServer = TargetServers.get(rand.nextInt(TargetServers.size()));

        if (!server.getServer(RandomServer).isPresent()) {
            player.sendMessage(Component.text(Main.ServerNotFound));
            System.out.println("[SlashHub] Player " + player.getUsername() + " has attempted to connect to server " + RandomServer + " However this server was not found in your Velocity Config");
            return;
        }

        if (TargetServers.contains(player.getCurrentServer().toString())) {
            player.sendMessage(Component.text(Main.AlreadyOnServer.replace('&', '§')));
        }
        Optional<RegisteredServer> TargetServer = server.getServer(RandomServer);
        source.sendMessage(Component.text(Main.ConnectingMessage.replace('&', '§')));
        player.createConnectionRequest(TargetServer.get()).fireAndForget();
    }
}
