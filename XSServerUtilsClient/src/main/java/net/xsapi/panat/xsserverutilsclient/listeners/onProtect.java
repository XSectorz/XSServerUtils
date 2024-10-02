package net.xsapi.panat.xsserverutilsclient.listeners;

import net.xsapi.panat.xsserverutilsclient.config.mainConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class onProtect implements Listener {

    @EventHandler
    public void onProtect(PlayerInteractEvent e) {

        if(mainConfig.getConfig().getString("configuration.server").equalsIgnoreCase("Auth/01")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void obBreak(BlockBreakEvent e) {

        if(mainConfig.getConfig().getString("configuration.server").equalsIgnoreCase("Auth/01")) {
            e.setCancelled(true);
        }
    }
}
