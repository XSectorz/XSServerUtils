package net.xsapi.panat.xsserverutilsbungee.discord.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.event.EventHandler;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;


public class msgRecievedEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getAuthor().isBot() || e.isWebhookMessage()) {
            return;
        }

        String[] args = e.getMessage().getContentRaw().split(" ");
        if(args[0].equalsIgnoreCase("!link")) {
            core.getPlugin().getLogger().info("someone send message");
            e.getChannel().sendMessage("test sent" + e.getChannel().getName()).queue();

            EmbedBuilder verification = new EmbedBuilder();

            verification.setTitle("**Siamcraft Verification**");
            verification.setColor(Color.ORANGE);
            verification.addField("หัวข้อ1", "Joined play.siamcraft.net\nบรรทัด2\nบรรทัด3", false);
            verification.setImage("https://i.imgur.com/gj9ln2P.png");
            verification.setFooter("siamcraft copy right 2024");

            Button verifyStepButton = Button.success("verify_step_button","ยืนยันตัวตน");

            e.getChannel().sendMessageEmbeds(verification.build()).setActionRow(verifyStepButton).queue();
            return;
        }
    }

}
