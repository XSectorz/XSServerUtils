package net.xsapi.panat.xsserverutilsclient.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.handler.XSHandler;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class XSPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "XSServerUtilsClient";
    }

    @Override
    public @NotNull String getAuthor() {
        return "PanatXsectorZ";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if(params.startsWith("online_")) {
            String onlineGroup = params.replace("online_","");
            if(XSHandler.getOnlineListServerGroup().containsKey(onlineGroup)) {
                return String.valueOf(XSHandler.getOnlineListServerGroup().get(onlineGroup));
            } else {
                return "ไม่สามารถเชื่อมต่อได้";
            }
        }
            return null;
    }
}
