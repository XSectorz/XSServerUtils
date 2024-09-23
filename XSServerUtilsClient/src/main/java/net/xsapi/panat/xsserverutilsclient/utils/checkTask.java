package net.xsapi.panat.xsserverutilsclient.utils;

import net.xsapi.panat.xsserverutilsclient.config.messageConfig;
import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.handler.XSHandler;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class checkTask {

    public checkTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(core.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.isOp() || p.hasPermission("*")) {
                        if(!XSHandler.getScpUsers().containsKey(p.getName())) {
                            String kickMsg = "";

                            Bukkit.getLogger().info("CHECK " + XSHandler.getScpUsers());


                            for(String s : messageConfig.getConfig().getStringList("SCPKicked")) {
                                kickMsg += XSUtils.decodeText(s) + "\n";
                            }

                            p.kickPlayer(kickMsg);
                            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(),"[SCP] Checked found some exploits",null,"SCP v2.0");
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

}
