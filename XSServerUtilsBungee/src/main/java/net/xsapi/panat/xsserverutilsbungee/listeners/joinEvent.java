package net.xsapi.panat.xsserverutilsbungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
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

            if(!scpUsers.isOnline()) {
                p.disconnect(XSUtils.sentKickSCP());
            }
            ServerInfo target = ProxyServer.getInstance().getServerInfo(scpUsers.getServer());

            if(target != null) {
                p.connect(target);
            }
        }


    }

}
