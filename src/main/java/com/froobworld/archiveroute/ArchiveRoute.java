package com.froobworld.archiveroute;

import com.froobworld.archiveroute.data.ExileManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "archive-route", name = "ArchiveRoute", version = "0.1.0-SNAPSHOT",
        url = "https://froobworld.com", description = "Re-route players to the archive server during lockdown.", authors = {"froobynooby"})
public class ArchiveRoute {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public ArchiveRoute(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        new ExileManager(this);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }
}