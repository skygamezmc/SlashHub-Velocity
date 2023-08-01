package me.skygamez.slashhub.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.skygamez.slashhub.SlashHub;
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

    public HubCommand(SlashHub slashHub, ProxyServer server) {
        this.server = server;
        this.slashHub = slashHub;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            parsed = miniMessage.deserialize(slashHub.CannotExecuteOnConsole);
            source.sendMessage(parsed);
            //source.sendMessage(Component.text(Main.CannotExecuteOnConsole.replace('&', '§')));
            return;
        }

        Player player = (Player) source;

        if (!player.hasPermission("slashhub.use")) {
            parsed = miniMessage.deserialize(slashHub.NoPermission);
            player.sendMessage(parsed);
            return;
        }

        Random rand = new Random();
        List<String> TargetServers = slashHub.TargetServers;
        String RandomServer = TargetServers.get(rand.nextInt(TargetServers.size()));

        if (!server.getServer(RandomServer).isPresent()) {
            parsed = miniMessage.deserialize(slashHub.ServerNotFound);
            player.sendMessage(parsed);
            System.out.println("[SlashHub] Player " + player.getUsername() + " has attempted to connect to server " + RandomServer + " However this server was not found in your Velocity Config");

            return;
        }

        if (server.getServer(RandomServer).get().getPlayersConnected().contains(player)) {
            parsed = miniMessage.deserialize(slashHub.AlreadyOnServer);
            source.sendMessage(parsed);
            return;
        }

        Optional<RegisteredServer> TargetServer = server.getServer(RandomServer);
        parsed = miniMessage.deserialize(slashHub.ConnectingMessage);
        source.sendMessage(parsed);
        player.createConnectionRequest(TargetServer.get()).fireAndForget();
    }
}
