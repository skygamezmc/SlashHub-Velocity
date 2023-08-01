package me.skygamez.slashhub;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.skygamez.slashhub.Commands.HubCommand;
import me.skygamez.slashhub.Commands.ReloadCommand;
import me.skygamez.slashhub.Metrics.Metrics;
import me.skygamez.slashhub.Updater.UpdateChecker;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Plugin(
        id = "slashhub",
        name = "SlashHub",
        version = "1.0",
        description = "A Velocity port for SlashHub",
        authors = {"skygamez"}
)
public class SlashHub {
    private ProxyServer server;
    private Logger logger;

    public Path folder = null;

    public String Version = "1.4.2";

    public List<String> TargetServers;
    public List<String> CommandAliases;
    public String ConnectingMessage;
    public String AlreadyOnServer;
    public String CannotExecuteOnConsole;
    public String ServerNotFound;
    public String NoPermission;
    public String ReloadedPlugin;

    private Metrics.Factory metricsFactory;

    //Function for loading config
    public Toml loadConfig(Path path) {
        File folder = path.toFile();


        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = SlashHub.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        return new Toml().read(file);
    }

    public void LoadConfigVars() {
        Toml toml = loadConfig(folder);
        TargetServers = toml.getList("TargetServers");
        CommandAliases = toml.getList("CommandAliases");
        ConnectingMessage = toml.getString("ConnectingMessage");
        AlreadyOnServer = toml.getString("AlreadyOnHubMessage");
        CannotExecuteOnConsole = toml.getString("CannotExecuteOnConsoleMessage");
        ServerNotFound = toml.getString("ServerNotFound");
        NoPermission = toml.getString("NoPermission");
        ReloadedPlugin = toml.getString("ReloadedPlugin");
    }

    @Inject
    public void SlashHub(ProxyServer server, Logger logger, Metrics.Factory metricsFactory, CommandManager commandManager, @DataDirectory final Path folder) throws IOException {
        this.server = server;
        this.logger = logger;
        this.folder = folder;

        this.metricsFactory = metricsFactory;

        Toml toml = loadConfig(folder);
        if (toml == null) {
            logger.warn("Failed to load config.toml. Shutting down.");
            return;
        }

        LoadConfigVars();

        try {
            UpdateChecker updateChecker = new UpdateChecker(this);
            if (updateChecker.updateRequired()) {
                logger.info("§b----------------------------");
                logger.info("");
                logger.info("§7  * §9SlashHub by SkyGameZ §7*");
                logger.info("§7   * §9Velocity edition §7*");
                logger.info("§7   * §9Update available! §7*");
                logger.info("");
                logger.info("§b----------------------------");
            } else {
                logger.info("§b----------------------------");
                logger.info("");
                logger.info("§7  * §9SlashHub by SkyGameZ §7*");
                logger.info("§7   * §9Velocity edition §7*");
                logger.info("§7    * §9Version 1.4.2 §7*");
                logger.info("");
                logger.info("§b----------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }










    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();

        CommandMeta HubMeta = commandManager.metaBuilder("hub").aliases(CommandAliases.toArray(new String[0])).build();
        commandManager.register(HubMeta, new HubCommand(this ,this.server));

        CommandMeta ReloadMeta = commandManager.metaBuilder("hubreload").build();
        commandManager.register(ReloadMeta, new ReloadCommand(this, folder));

        int pluginId = 18816;
        Metrics metrics = metricsFactory.make(this, pluginId);
    }
}
