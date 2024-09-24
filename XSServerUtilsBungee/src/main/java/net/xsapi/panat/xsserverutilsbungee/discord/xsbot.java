package net.xsapi.panat.xsserverutilsbungee.discord;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.discord.listeners.buttonEvent;
import net.xsapi.panat.xsserverutilsbungee.discord.listeners.modalEvent;
import net.xsapi.panat.xsserverutilsbungee.discord.listeners.msgRecievedEvent;

import javax.security.auth.login.LoginException;

public class xsbot {

    private final ShardManager shardManager;

    public xsbot() throws LoginException {
        String token = mainConfig.getConfig().getString("discordToken");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.setActivity(Activity.playing("play.Siamcraft.net"));
        shardManager = builder.build();

        shardManager.addEventListener(new msgRecievedEvent());
        shardManager.addEventListener(new buttonEvent());
        shardManager.addEventListener(new modalEvent());
    }

    public ShardManager getShardManager() {
        return shardManager;
    }
}
