package net.xsapi.panat.xsserverutilsclient.listeners;

import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.handler.XSHandler;
import net.xsapi.panat.xsserverutilsclient.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsclient.utils.XSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;

public class playerChatEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        if(XSHandler.getMuteList().containsKey(e.getPlayer().getName()) ) {
            Player target = e.getPlayer();

            XSMuteplayers xsMuteplayers = XSHandler.getMuteList().get(target.getName());

            if(new Date().getTime()- xsMuteplayers.getEnd_date() < 0 || xsMuteplayers.getEnd_date() == -1) {
                String timeLeft = "ถาวร";

                core.getPlugin().getLogger().info("MUTE....." + target.getName());

                if(xsMuteplayers.getEnd_date() != -1) {
                    timeLeft = XSUtils.convertDiffTime(System.currentTimeMillis(),(long) xsMuteplayers.getEnd_date());
                }

                target.sendMessage(XSUtils.decodeText("<color:#FFE300><bold>XSUtils</bold> <dark_gray>▸ <white>คุณไม่สามารถส่งข้อความได้เป็นเวลา <red>%time_left%"
                        .replace("%time_left%",timeLeft)));
                e.setCancelled(true);
            }
        }

    }
}
