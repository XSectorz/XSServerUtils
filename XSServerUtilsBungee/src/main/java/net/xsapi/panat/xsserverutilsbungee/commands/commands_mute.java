package net.xsapi.panat.xsserverutilsbungee.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSRedisHandler;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class commands_mute extends Command implements TabExecutor {

    public commands_mute() {
        super("xsmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            LuckPerms api = LuckPermsProvider.get();
            User user = api.getPlayerAdapter(ProxiedPlayer.class).getUser(player);

            if(!XSUtils.hasPermission(user,"xsutils.mute")) {
                player.sendMessage(XSUtils.decodeTextFromConfig("no_permission"));
                return;
            }
            String uuid;
            String target;
            String reason = "";
            String timer = "";
            long timeElapsed = 0L;
            if(args.length >= 1) {
                target = args[0];
                if(XSHandler.getMuteList().containsKey(target)) {
                    XSMuteplayers xsMuteplayers = XSHandler.getMuteList().get(target);
                    if(System.currentTimeMillis() - xsMuteplayers.getEnd_date() < 0L || xsMuteplayers.getEnd_date() == -1) { //time not end
                        player.sendMessage(XSUtils.decodeTextFromConfig("mute_already").replace("%player_name%",args[0]));
                        return;
                    }
                }

                String result = XSDatabaseHandler.getUserIdFromUsername(target);
                int idRef = Integer.parseInt(result.split(":")[0]);

                if(idRef == -1) {
                    player.sendMessage(XSUtils.decodeTextFromConfig("player_does_not_exists"));
                    return;
                }

                uuid = result.split(":")[1];
                if(args.length == 1) {
                    XSDatabaseHandler.insertIntoDatabaseBan(idRef,XSUtils.decodeTextFromConfig("banned_default_reason"),new Date().getTime(),-1,player.getName());
                } else  {

                    timer = args[1]; //format [d]d:[h]h:[m]m:[s]s
                    String[] timeFormat = timer.split(":");

                    if(!timer.equalsIgnoreCase("-1")) {
                        for(String timeSplit : timeFormat) {
                            if(timeSplit.endsWith("d")) {
                                timeElapsed = timeElapsed + Integer.parseInt(timeSplit.replace("d","")) * 86400L * 1000L;
                            } else if(timeSplit.endsWith("h")) {
                                timeElapsed = timeElapsed + Integer.parseInt(timeSplit.replace("h","")) * 3600L * 1000L;
                            } else if (timeSplit.endsWith("m")) {
                                timeElapsed = timeElapsed + Integer.parseInt(timeSplit.replace("m","")) * 60L * 1000L;
                            } else if (timeSplit.endsWith("s")) {
                                timeElapsed = timeElapsed + Integer.parseInt(timeSplit.replace("s", "")) * 1000L;
                            }
                        }
                    }


                    if(args.length == 2) {
                        XSDatabaseHandler.insertIntoDatabaseBan(idRef,XSUtils.decodeTextFromConfig("banned_default_reason"),new Date().getTime(),new Date().getTime()+timeElapsed,player.getName());
                    } else { //more than 2 arguments
                        for(int i = 2 ; i < args.length ; i++) {
                            reason += (args[i] + " ");
                        }
                        XSDatabaseHandler.insertIntoDatabaseBan(idRef,reason,new Date().getTime(),new Date().getTime()+timeElapsed,player.getName());
                    }

                }

                XSRedisHandler.sendRedisMessage(XSRedisHandler.getClientPrefix()+"lobby/01","test");
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> match = new HashSet<>();
        if (args.length == 1) {
            for(ProxiedPlayer online : core.getPlugin().getProxy().getPlayers()) {
                match.add(online.getName());
            }
        } else if(args.length == 2) {
            match.add("<reason>");
        }else if(args.length == 3) {
            match.add("<time>");
        }
        return match;
    }
}
