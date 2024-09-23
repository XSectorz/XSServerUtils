package net.xsapi.panat.xsserverutilsbungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.scp.scpSessions;
import net.xsapi.panat.xsserverutilsbungee.scp.scpUsers;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.util.Date;

public class joinEvent implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if(XSHandler.getBanList().containsKey(p.getName())) {

            XSBanplayers xsBanplayers = XSHandler.getBanList().get(p.getName());
            if(new Date().getTime()- xsBanplayers.getEnd_date() < 0 || xsBanplayers.getEnd_date() == -1) {
                p.disconnect(XSUtils.sentKickedAnnouce(xsBanplayers));
            }
        }

        if(XSHandler.getScpUsers().containsKey(p.getName())) {
            scpUsers scpUsers = XSHandler.getScpUsers().get(p.getName());

            core.getPlugin().getLogger().info("CONTAIN " + p.getName());

            core.getPlugin().getLogger().info("IS ONLINE? " + scpUsers.isOnline());
            core.getPlugin().getLogger().info(p.getAddress().getAddress().getHostAddress());
            if(!scpUsers.isOnline()) {

                if(XSHandler.getScpUserSessions().containsKey(p.getName())) {
                    core.getPlugin().getLogger().info("Has session");

                    scpSessions scpSession =  XSHandler.getScpUserSessions().get(p.getName());

                    String userAddr = p.getAddress().getAddress().getHostAddress();

                    if(!userAddr.equalsIgnoreCase(scpSession.getIpAddr())) {
                        core.getPlugin().getLogger().info("Ip not equal " + scpSession.getIpAddr() + " : " + userAddr + " remove session");
                        XSHandler.getScpUserSessions().remove(p.getName());
                        p.disconnect(XSUtils.sentKickSCP());
                        return;
                    }

                    if(System.currentTimeMillis() - scpSession.getLogoutTime() > 3600000L) {
                        core.getPlugin().getLogger().info("Session timeout");
                        p.disconnect(XSUtils.sentKickSCP());
                        return;
                    }
                } else {
                    core.getPlugin().getLogger().info("Not session");
                    p.disconnect(XSUtils.sentKickSCP());
                    return;
                }

            }
            core.getPlugin().getLogger().info("Pass check!");
            scpUsers.setIsOnline(true);

            ServerInfo target = ProxyServer.getInstance().getServerInfo(scpUsers.getServer());

            if(target != null) {
                //core.getPlugin().getLogger().info("sent..... to " + target);
                //p.connect(target);
            }
        }


    }

}
