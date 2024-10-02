package net.xsapi.panat.xsserverutilsbungee.commands;

import net.md_5.bungee.api.ProxyServer;
import net.xsapi.panat.xsserverutilsbungee.core;

public class commandsLoader {

    public commandsLoader() {
        ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_ban());
        ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_unban());
        ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_mute());
        ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_unmute());
        //ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_hub());
        ProxyServer.getInstance().getPluginManager().registerCommand(core.getPlugin(), new commands_verify());
    }

}
