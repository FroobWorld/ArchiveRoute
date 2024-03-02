package com.froobworld.archiveroute.data;

import com.froobworld.archiveroute.ArchiveRoute;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ExileEnforcer {
    private final ArchiveRoute archiveRoute;
    private final ExileManager exileManager;

    public ExileEnforcer(ArchiveRoute archiveRoute, ExileManager exileManager) {
        this.archiveRoute = archiveRoute;
        this.exileManager = exileManager;
    }

    @Subscribe
    public void onPostConnect(PlayerChooseInitialServerEvent event) {
        if (exileManager.checkExile(event.getPlayer())) {
            RegisteredServer archiveServer = archiveRoute.getServer().getServer("archive").orElse(null);
            if (event.getInitialServer().isEmpty() || event.getInitialServer().get().equals(archiveServer)) {
                return;
            }
            archiveRoute.getLogger().info("Attempting to redirect exiled player " + event.getPlayer().getUsername() + ".");
            if (archiveServer != null) {
                event.setInitialServer(archiveServer);
                event.getPlayer().sendMessage(Component.text("The main server is currently unable to accept new players. Feel free to explore the archives, and try again later.", NamedTextColor.RED));
            } else {
                event.getPlayer().disconnect(Component.text("The server is currently unable to accept new players, please try again later.", NamedTextColor.RED));
            }
        }
    }

    @Subscribe
    public void onServerChange(ServerPreConnectEvent event) {
        if (exileManager.checkExile(event.getPlayer())) {
            RegisteredServer archiveServer = archiveRoute.getServer().getServer("archive").orElse(null);
            if (event.getOriginalServer().equals(archiveServer)) {
                return;
            }
            archiveRoute.getLogger().info("Attempting to redirect exiled player " + event.getPlayer().getUsername() + ".");
            if (archiveServer != null) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(archiveServer));
                event.getPlayer().sendMessage(Component.text("The main server is currently unable to accept new players. Feel free to explore the archives, and try again later.", NamedTextColor.RED));
            } else {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }
    }

}
