package net.xsapi.panat.xsserverutilsbungee.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSRedisHandler;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.util.HashSet;
import java.util.Set;

public class commands_unmute extends Command implements TabExecutor {

    public commands_unmute() {
        super("xsunmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            LuckPerms api = LuckPermsProvider.get();
            User user = api.getPlayerAdapter(ProxiedPlayer.class).getUser(player);

            if(!XSUtils.hasPermission(user,"xsutils.unmute")) {
                player.sendMessage(XSUtils.decodeTextFromConfig("no_permission"));
                return;
            }

            if(args.length == 1) {
                String target = args[0];
                if(!XSHandler.getMuteList().containsKey(target)) {
                    player.sendMessage(XSUtils.decodeTextFromConfig("unmute_fail"));
                    return;
                }

                XSMuteplayers xsMuteplayers = XSHandler.getMuteList().get(target);


                XSHandler.getMuteList().remove(target);
                XSDatabaseHandler.deleteMutePlayerFromDatabase(xsMuteplayers.getIdRef());
                for(String serverGroup : mainConfig.getConfig().getSection("configuration.serverList").getKeys()) {
                    for(String server : mainConfig.getConfig().getStringList("configuration.serverList." + serverGroup)) {
                        XSRedisHandler.sendRedisMessage(XSRedisHandler.getClientPrefix() + server,"UN_MUTE<SPLIT>" + target);
                    }
                }
                player.sendMessage(XSUtils.decodeTextFromConfig("unmute_success").replace("%player_name%",args[0]));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> match = new HashSet<>();
        if (args.length == 1) {
            match.addAll(XSHandler.getMuteList().keySet());
        }
        return match;
    }
}
