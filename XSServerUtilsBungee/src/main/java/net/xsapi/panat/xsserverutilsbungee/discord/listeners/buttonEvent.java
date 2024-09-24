package net.xsapi.panat.xsserverutilsbungee.discord.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyHandler;

import java.awt.*;

public class buttonEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        User user = e.getUser();

        if(e.getButton().getId().equalsIgnoreCase("verify_step_button")) {
            e.deferEdit().queue();

            EmbedBuilder verificationEmbed = new EmbedBuilder();

            String verifyUsername = verifyHandler.getUserFromVerify(user.getId());

            if(!verifyUsername.isEmpty()) {
                verificationEmbed.setTitle("Discord ของคุณได้รับการยืนยันไปแล้ว");
                verificationEmbed.setColor(Color.RED);
                verificationEmbed.setDescription(":x: ดิสคอร์ดของคุณยืนยันไปกับไอดี ***" + verifyUsername + "*** เรียบร้อยแล้ว");
                e.getHook().sendMessageEmbeds(verificationEmbed.build()).setEphemeral(true).queue();
                return;
            }

            verificationEmbed.setTitle("**Siamcraft Verification**");
            verificationEmbed.setColor(Color.ORANGE);
            verificationEmbed.addField("ขั้นตอนที่ 1","เข้าร่วมเซิฟเวอร์ play.siamcraft.net",false);
            verificationEmbed.addField("ขั้นตอนที่ 2","พิมพ์คำสั่ง /verify " + user.getId(),false);
            verificationEmbed.addField("ขั้นตอนที่ 3","คลิก ที่ปุ่มกรอกเลขยืนยัน",false);
            verificationEmbed.addField("ขั้นตอนที่ 4","นำเลข ***6*** หลักจากในเซิฟเวอร์มาทำการยืนยัน",false);
            verificationEmbed.setImage("https://i.imgur.com/gj9ln2P.png");
            net.dv8tion.jda.api.interactions.components.buttons.Button verifyCodeButton = Button.success("verify_code_button","\uD83D\uDFE2 กรอกเลขยืนยัน");
            e.getHook().sendMessageEmbeds(verificationEmbed.build()).setActionRow(verifyCodeButton).setEphemeral(true).queue();
            /*
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessageEmbeds(verificationEmbed.build()).setActionRow(verifyCodeButton).queue();
            });*/

            verifyHandler.getDiscordUsername().put(user.getId(),user.getName());
        } else if(e.getButton().getId().equalsIgnoreCase("verify_code_button")) {

            EmbedBuilder verificationEmbed = new EmbedBuilder();
            if(!verifyHandler.getVerifyDiscordQueue().containsKey(user.getId())) {
                e.deferEdit().queue();
                verificationEmbed.setTitle("**Siamcraft Verification**");
                verificationEmbed.setColor(Color.RED);
                verificationEmbed.setDescription(":x: กรุณาใช้คำสั่ง `/verify " + user.getId() + "` ในเซิฟเวอร์ก่อน");
                e.getHook().sendMessageEmbeds(verificationEmbed.build()).setEphemeral(true).queue();
                return;
            }

            TextInput codeInput = TextInput.create("verification_code", "6-digits code", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(6)
                    .setPlaceholder("XXXXXX")
                    .build();
            Modal modal = Modal.create("verification_modal", "Siamcraft Verification")
                    .addActionRow(codeInput)
                    .build();

            e.getInteraction().replyModal(modal).queue();
        }
    }
}
