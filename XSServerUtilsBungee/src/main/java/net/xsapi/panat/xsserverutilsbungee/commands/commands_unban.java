package net.xsapi.panat.xsserverutilsbungee.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.util.HashSet;
import java.util.Set;

public class commands_unban extends Command implements TabExecutor {

    public commands_unban() {
        super("xsunban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            LuckPerms api = LuckPermsProvider.get();
            User user = api.getPlayerAdapter(ProxiedPlayer.class).getUser(player);

            if(!XSUtils.hasPermission(user,"xsutils.unban")) {
                player.sendMessage(XSUtils.decodeTextFromConfig("no_permission"));
                return;
            }

            if(args.length == 1) {
                String target = args[0];
                if(!XSHandler.getBanList().containsKey(target)) {
                    player.sendMessage(XSUtils.decodeTextFromConfig("unban_fail"));
                    return;
                }

                XSBanplayers xsBanplayers = XSHandler.getBanList().get(target);

                XSHandler.getBanList().remove(target);
                XSDatabaseHandler.deleteFromDatabase(xsBanplayers.getIdRef());
                player.sendMessage(XSUtils.decodeTextFromConfig("unban_success").replace("%player_name%",args[0]));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> match = new HashSet<>();
        if (args.length == 1) {
            match.addAll(XSHandler.getBanList().keySet());
        }
        return match;
    }
}
