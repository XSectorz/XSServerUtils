package net.xsapi.panat.xsserverutilsbungee.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import net.xsapi.panat.xsserverutilsbungee.config.messagesConfig;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class XSUtils {

    public static String decodeText(String str) {
        Component parsedMessage = MiniMessage.builder().build().deserialize(str);
        String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage);
        return legacy.replace('&', '§');
    }

    public static String convertDiffTime(long current, long specific) {
        long difference = specific - current;

        long seconds = difference / 1000 % 60;
        long minutes = difference / (1000 * 60) % 60;
        long hours = difference / (1000 * 60 * 60) % 24;
        long days = difference / (1000 * 60 * 60 * 24);
        if(difference < 0) {
            return "หมดเวลาแล้ว";
        } else {
            if(days > 0) {
                return days + " วัน " + hours + " ชั่วโมง " + minutes + " นาที " + seconds + " วินาที";
            }
            if(hours > 0) {
                return hours + " ชั่วโมง " + minutes + " นาที " + seconds + " วินาที";
            }
            if(minutes > 0) {
                return minutes + " นาที " + seconds + " วินาที";
            }
            return seconds + " วินาที";
        }
    }

    public static String sentKickSCP() {
        String kicedMSG = "";
        for(String s : messagesConfig.getConfig().getStringList("scp_annouce")) {

            String replaceStr = XSUtils.decodeText(s);
            kicedMSG += replaceStr + "\n";
        }
        return kicedMSG;
    }

    public static String generateSixDigitCode() {
        int randomNumber = ThreadLocalRandom.current().nextInt(0, 1000000);
        String formattedNumber = String.format("%06d", randomNumber);

        return formattedNumber;
    }

    public static String sentKickedAnnouce(XSBanplayers xsBanplayers) {
        String kicedMSG = "";
        for(String s : messagesConfig.getConfig().getStringList("kicked_annouce")) {

            String replaceStr = XSUtils.decodeText(s);
            replaceStr = replaceStr.replace("%reason%",xsBanplayers.getReason());

            if(xsBanplayers.getEnd_date() == -1) {
                replaceStr = replaceStr.replace("%timer%",XSUtils.decodeTextFromConfig("banned_forever"));
            } else {
                replaceStr = replaceStr.replace("%timer%",XSUtils.convertDiffTime(System.currentTimeMillis(),(long)xsBanplayers.getEnd_date()));
            }

            kicedMSG += replaceStr + "\n";
        }
        return kicedMSG;
    }

    public static String decodeTextFromConfig(String section) {
        String text = Objects.requireNonNull(messagesConfig.getConfig().getString(section));
        text = text.replace("%prefix%", Objects.requireNonNull(messagesConfig.getConfig().getString("prefix")));
        Component parsedMessage = MiniMessage.builder().build().deserialize(text);
        String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage);
        return legacy.replace('&', '§');
    }

    public static String decodeTextNotReplace(String str) {
        Component parsedMessage = MiniMessage.builder().build().deserialize(str);
        return LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage);
    }

    public static boolean hasPermission(User user, String permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

}
