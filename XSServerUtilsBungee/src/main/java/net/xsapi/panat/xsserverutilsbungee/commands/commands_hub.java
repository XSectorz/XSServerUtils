package net.xsapi.panat.xsserverutilsbungee.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.util.HashSet;
import java.util.Set;

public class commands_hub extends Command {

    public commands_hub() {
        super("hub","","lobby");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo serverInfo = core.getPlugin().getProxy().getServerInfo(mainConfig.getConfig().getString("lobby"));
            player.connect(serverInfo);
        }
    }

}
