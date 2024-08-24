package net.xsapi.panat.xsserverutilsbungee.websocket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.handler.XSDatabaseHandler;
import net.xsapi.panat.xsserverutilsbungee.handler.XSHandler;
import net.xsapi.panat.xsserverutilsbungee.scp.scpUsers;
import net.xsapi.panat.xsserverutilsbungee.utils.XSUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class scpWebSocket extends WebSocketClient {

    public scpWebSocket(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server");
        send("Hello this is from client");
    }

    @Override
    public void onMessage(String message) {

        String action = message.split("<SPLIT>")[0];
        String clientName = message.split("<SPLIT>")[1];

        if(action.equalsIgnoreCase("REGISTER_NEW_2FA")) {
            String hashSecret = message.split("<SPLIT>")[2];
            XSDatabaseHandler.updateSecretSCPUsers(clientName,hashSecret);
            XSHandler.getScpUsers().get(clientName).setIsOnline(true);
            scpUsers scpUsers = XSHandler.getScpUsers().get(clientName);
            scpUsers.setCurrentTime(System.currentTimeMillis());
        } else if(action.equalsIgnoreCase("DISCONNECTED")) {
            if(XSHandler.getScpUsers().containsKey(clientName)) {
                XSHandler.getScpUsers().get(clientName).setIsOnline(false);
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(clientName);
                if(player != null) {
                    player.disconnect(XSUtils.sentKickSCP());

                    int timeDiff = (int) ((System.currentTimeMillis() -  XSHandler.getScpUsers().get(clientName).getCurrentTime())/1000);

                    XSDatabaseHandler.updateSCPUsersLogout(clientName, XSHandler.getScpUsers().get(clientName).getOnlineTime()+timeDiff,
                            player.getServer().getInfo().getName());
                }

            }
        } else if(action.equalsIgnoreCase("LOGIN")) {
            if(XSHandler.getScpUsers().containsKey(clientName)) {
                XSHandler.getScpUsers().get(clientName).setIsOnline(true);
                scpUsers scpUsers = XSHandler.getScpUsers().get(clientName);
                scpUsers.setCurrentTime(System.currentTimeMillis());
            }
        } else if(action.equalsIgnoreCase("CREATE_ACCOUNT")) {
            if(!XSHandler.getScpUsers().containsKey(clientName)) {
                String group = message.split("<SPLIT>")[2];
                core.getPlugin().getLogger().info("[SCP] Added user " + clientName + "!");
                XSHandler.getScpUsers().put(clientName,new scpUsers(clientName,"pre-auth",0,group));
            } else {
                core.getPlugin().getLogger().info("[SCP] user " + clientName + " already exists");
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
