package net.xsapi.panat.xsserverutilsbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.xsapi.panat.xsserverutilsbungee.config.messagesConfig;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyDiscordQueue;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyHandler;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;

import java.awt.*;

public class commands_verify extends Command  {

    public commands_verify() {
        super("verify");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if(verifyHandler.getVerifyUser().containsKey(player.getUniqueId())) {
                player.sendMessage(XSUtils.decodeTextFromConfig("verify_user_already"));
                return;
            }

            if(args.length == 1) {
                String discordID = args[0];

                String verifyUsername = verifyHandler.getUserFromVerify(discordID);

                if(!verifyUsername.isEmpty()) {
                    player.sendMessage(XSUtils.decodeTextFromConfig("verify_discord_already"));
                    return;
                }

                if(!verifyHandler.getDiscordUsername().containsKey(discordID)) {
                    player.sendMessage(XSUtils.decodeTextFromConfig("verify_not_contain"));
                    return;
                }

                String discordName = verifyHandler.getDiscordUsername().get(discordID);
                String digitsCode = XSUtils.generateSixDigitCode();

                if(verifyHandler.getVerifyDiscordQueue().containsKey(discordID)) {

                    if(System.currentTimeMillis() - verifyHandler.getVerifyDiscordQueue().get(discordID).getVerifyTime() <= 0L) {
                        player.sendMessage(XSUtils.decodeTextFromConfig("verify_is_waiting"));
                        return;
                    }

                }

                for(String s : messagesConfig.getConfig().getStringList("sentVerify_annouce")) {
                    String decodeText = XSUtils.decodeText(s);
                    decodeText = decodeText.replace("%discord_name%",discordName);
                    decodeText = decodeText.replace("%discord_verify_code%",digitsCode);
                    player.sendMessage(decodeText);
                }

                verifyDiscordQueue verifyDiscordQueue = new verifyDiscordQueue(discordName,System.currentTimeMillis()+10000L,digitsCode,player.getUniqueId().toString(),player.getName());
                verifyHandler.getVerifyDiscordQueue().put(discordID,verifyDiscordQueue);
            }

        }
    }
}
