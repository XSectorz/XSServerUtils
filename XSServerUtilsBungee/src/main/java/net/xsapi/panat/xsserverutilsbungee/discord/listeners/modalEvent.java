package net.xsapi.panat.xsserverutilsbungee.discord.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyHandler;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyUser;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;

import java.awt.*;
import java.util.UUID;

public class modalEvent extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equals("verification_modal")) {
            e.deferReply(true).queue();
            String verificationCode = e.getValue("verification_code").getAsString();

            User user = e.getUser();
            EmbedBuilder verificationEmbed = new EmbedBuilder();

            if(System.currentTimeMillis() - verifyHandler.getVerifyDiscordQueue().get(user.getId()).getVerifyTime() > 0L) { //timeout for verify
                verificationEmbed.setTitle("**Siamcraft Verification**");
                verificationEmbed.setColor(Color.RED);
                verificationEmbed.setDescription(":x: หมดเวลาการยืนยันตัวตนกรุณาลองใหม่อีกครั้ง");
                e.getHook().sendMessageEmbeds(verificationEmbed.build()).setEphemeral(true).queue();
                return;
            }

            if(!verifyHandler.getVerifyDiscordQueue().get(user.getId()).getSecretDigit().equalsIgnoreCase(verificationCode)) { //verify code not match
                verificationEmbed.setTitle("**Siamcraft Verification**");
                verificationEmbed.setColor(Color.RED);
                verificationEmbed.setDescription(":x: รหัสยืนยันไม่ถูกต้องกรุณาลองใหม่อีกครั้ง");
                e.getHook().sendMessageEmbeds(verificationEmbed.build()).setEphemeral(true).queue();
                return;
            }

            Guild guild = e.getGuild();

            verificationEmbed.setTitle("**Siamcraft Verification**");
            verificationEmbed.setColor(Color.GREEN);
            verificationEmbed.setDescription(":white_check_mark: คุณทำการยืนยันตัวตนสำเร็จเรียบร้อยแล้ว");
            e.getHook().sendMessageEmbeds(verificationEmbed.build()).setEphemeral(true).queue();

            String playerUUID = verifyHandler.getVerifyDiscordQueue().get(user.getId()).getCurrentUserUUID();
            String playerName = verifyHandler.getVerifyDiscordQueue().get(user.getId()).getCurrentUserName();
            XSDatabaseHandler.updateVerifyUser(user.getId(),playerUUID);
            verifyUser verifyUser = new verifyUser(playerName,user.getId(),System.currentTimeMillis());
            verifyHandler.getVerifyUser().put(UUID.fromString(playerUUID),verifyUser);

            if(guild != null) {
                Member member = guild.retrieveMember(user).complete();

                Role role = guild.getRoleById(mainConfig.getConfig().getString("discordVerifyRole"));
                if(role != null && member != null) {
                    guild.addRoleToMember(member,role).queue(
                            success -> {
                                core.getPlugin().getLogger().info("Added role to : " + user.getId());
                            },
                            error -> {
                                core.getPlugin().getLogger().info("Failed to add role to : " + user.getId());
                            }
                    );
                }
            }
        }
    }
}
