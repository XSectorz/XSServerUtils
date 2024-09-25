package net.xsapi.panat.xsserverutilsbungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;

public class serverConnectEvent implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectedEvent e) {

        if(e.getServer().getInfo().getName().equalsIgnoreCase("Lobby/01") || e.getServer().getInfo().getName().equalsIgnoreCase("Lobby/02")) {
            ProxiedPlayer player = e.getPlayer();
            XSDatabaseHandler.insertIntoDatabaseUser(player);
        }

    }
}
