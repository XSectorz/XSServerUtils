package net.xsapi.panat.xsserverutilsbungee.handler;

import com.google.gson.Gson;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.core;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;

public class XSRedisHandler {

    public static String redisHost;
    public static int redisPort;
    public static String redisPass;
    public static ArrayList<Thread> threads = new ArrayList<>();

    public static String getClientPrefix(){
        return "xsserverutils:channel_client_";
    }

    public static String getHostRedis() {
        return redisHost;
    }
    public static String getRedisPass() {
        return redisPass;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static void redisConnection() {
        redisHost = mainConfig.getConfig().getString("redis.host");
        redisPort = mainConfig.getConfig().getInt("redis.port");
        redisPass = mainConfig.getConfig().getString("redis.password");

        try {
            Jedis jedis = new Jedis(getHostRedis(), getRedisPort());
            if(!getRedisPass().isEmpty()) {
                jedis.auth(getRedisPass());
            }
            jedis.close();
            core.getPlugin().getLogger().info("Redis Server : Connected");
        } catch (Exception e) {
            core.getPlugin().getLogger().info("Redis Server : Not Connected");
            e.printStackTrace();
        }
    }

    public static void subscribeToChannelAsync(String channelName) {
        Thread thread = new Thread(() -> {
            try (Jedis jedis = new Jedis(getHostRedis(), getRedisPort(), 0)) {
                if (!getRedisPass().isEmpty()) {
                    jedis.auth(getRedisPass());
                }
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (Thread.currentThread().isInterrupted()) {
                           // core.getPlugin().getLogger().info("XSServerUtils Threads : Is interrupt");
                            return;
                        }

                        if(channel.equalsIgnoreCase(XSHandler.getSubChannel())) {
                           // core.getPlugin().getLogger().info("Req data received");
                            String args = message.split("<SPLIT>")[0];
                            if(args.equalsIgnoreCase("REQUEST_DATA")) {
                                String server = message.split("<SPLIT>")[1];
                              //  core.getPlugin().getLogger().info("sub " + getClientPrefix()+server);
                                Gson gson = new Gson();
                                String muteJson = gson.toJson(XSHandler.getMuteList());
                                sendRedisMessage(getClientPrefix()+server,"REQUEST_DATA_ACK<SPLIT>"+muteJson);

                                String scpJSON = gson.toJson(XSHandler.getScpUsers());
                                sendRedisMessage(XSRedisHandler.getClientPrefix() + server,"UPDATE_SCP_USER<SPLIT>" + scpJSON);
                            }
                        }
                    }
                };
                jedis.subscribe(jedisPubSub, channelName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        threads.add(thread);
    }

    public static void sendRedisMessage(String CHName, String message) {

        new Thread(() -> {
            try (Jedis jedis = new Jedis(getHostRedis(), getRedisPort())) {
                if(!getRedisPass().isEmpty()) {
                    jedis.auth(getRedisPass());
                }
                jedis.publish(CHName, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void destroyThreads() {
        for(Thread thread : threads) {
            thread.interrupt();
        }
    }

}
