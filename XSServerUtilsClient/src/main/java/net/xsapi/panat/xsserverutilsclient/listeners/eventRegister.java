package net.xsapi.panat.xsserverutilsclient.listeners;

import net.xsapi.panat.xsserverutilsclient.core;
import org.bukkit.Bukkit;

public class eventRegister {

    public eventRegister() {

        Bukkit.getPluginManager().registerEvents(new playerChatEvent(), core.getPlugin());

    }
}
