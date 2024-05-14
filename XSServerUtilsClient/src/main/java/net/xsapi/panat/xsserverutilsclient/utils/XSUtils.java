package net.xsapi.panat.xsserverutilsclient.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Objects;

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

    /*public static String decodeTextFromConfig(String section) {
        String text = Objects.requireNonNull(messagesConfig.getConfig().getString(section));
        text = text.replace("%prefix%", Objects.requireNonNull(messagesConfig.getConfig().getString("prefix")));
        Component parsedMessage = MiniMessage.builder().build().deserialize(text);
        String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage);
        return legacy.replace('&', '§');
    }*/

    public static String decodeTextNotReplace(String str) {
        Component parsedMessage = MiniMessage.builder().build().deserialize(str);
        return LegacyComponentSerializer.legacyAmpersand().serialize(parsedMessage);
    }

}
