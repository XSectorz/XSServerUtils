package net.xsapi.panat.xsserverutilsbungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.scp.scpSessions;

public class onLeave implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {

        ProxiedPlayer p = e.getPlayer();

        if(XSHandler.getScpUsers().containsKey(p.getName()) && p.getServer()!= null) {
            int timeDiff = (int) ((System.currentTimeMillis() -  XSHandler.getScpUsers().get(p.getName()).getCurrentTime())/1000);

            if(timeDiff < 100000) {
                XSDatabaseHandler.updateSCPUsersLogout(p.getName(), XSHandler.getScpUsers().get(p.getName()).getOnlineTime()+timeDiff,
                        p.getServer().getInfo().getName());
            }

            scpSessions scpSessions = new scpSessions(p.getAddress().getAddress().getHostAddress(),System.currentTimeMillis());

            if(XSHandler.getScpUsers().get(p.getName()).isOnline()) {
                core.getPlugin().getLogger().info("Online added session! " + p.getName());
                XSHandler.getScpUserSessions().put(p.getName(),scpSessions);
            } else {
                core.getPlugin().getLogger().info("Offline not add session! " + p.getName());
            }
            XSHandler.getScpUsers().get(p.getName()).setIsOnline(false);
        }

    }
}
