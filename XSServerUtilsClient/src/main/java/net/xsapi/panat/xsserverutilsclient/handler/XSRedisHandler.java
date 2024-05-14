package net.xsapi.panat.xsserverutilsclient.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.xsapi.panat.xsserverutilsclient.config.mainConfig;
import net.xsapi.panat.xsserverutilsclient.core;
import net.xsapi.panat.xsserverutilsclient.objects.XSMuteplayers;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class XSRedisHandler {

    public static String redisHost;
    public static int redisPort;
    public static String redisPass;
    public static ArrayList<Thread> threads = new ArrayList<>();
    public static String getHostRedis() {
        return redisHost;
    }
    public static String getRedisPass() {
        return redisPass;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static String getHostPrefix() {
        return "xsserverutils:channel_bungeecord";
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
                            //core.getPlugin().getLogger().info("XSServerUtils Threads : Is interrupt");
                            return;
                        }

                        if(channel.equalsIgnoreCase(XSHandler.getSubChannel())) {

                            String args = message.split("<SPLIT>")[0];
                           // Bukkit.broadcastMessage("GET " + message);
                            if(args.equalsIgnoreCase("MUTE")) {
                                String muteObject = message.split("<SPLIT>")[1];
                                String target = message.split("<SPLIT>")[2];
                                Gson gson = new Gson();
                                XSMuteplayers xsMuteplayers = gson.fromJson(muteObject, XSMuteplayers.class);
                                XSHandler.getMuteList().put(target,xsMuteplayers);
                               // Bukkit.broadcastMessage("MUTE " + target);
                            } else if(args.equalsIgnoreCase("REQUEST_DATA_ACK")) {

                                String data = message.split("<SPLIT>")[1];
                                Gson gson = new Gson();
                                Type type = new TypeToken<HashMap<String, XSMuteplayers>>(){}.getType();
                                HashMap<String, XSMuteplayers> mutelistFromJson = gson.fromJson(data, type);
                                XSHandler.setMutelist(mutelistFromJson);
                                //Bukkit.broadcastMessage("Load data " + mutelistFromJson);
                            } else if(args.equalsIgnoreCase("UN_MUTE")) {
                                String player = message.split("<SPLIT>")[1];
                                XSHandler.getMuteList().remove(player);

                            }
                            //XSRedisHandler.sendRedisMessage(XSRedisHandler.getHostPrefix(),"test ack");
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
