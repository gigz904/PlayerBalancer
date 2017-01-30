package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.faucet.ServerListPing;
import me.jaimemartz.faucet.StatusResponse;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;

public enum PingTacticType {
    CUSTOM {
        ServerListPing utility = new ServerListPing();

        @Override
        public void ping(ServerInfo server, Callback<ServerStatus> callback, LobbyBalancer plugin) {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                try {
                    StatusResponse response = utility.ping(server.getAddress());
                    callback.done(new ServerStatus(
                            response.getDescription().toLegacyText(),
                            response.getPlayers().getOnline(),
                            response.getPlayers().getMax()),
                            null);
                } catch (IOException e) {
                    callback.done(null, e);
                }
            });
        }
    },

    GENERIC {
        @Override
        public void ping(ServerInfo server, Callback<ServerStatus> callback, LobbyBalancer plugin) {
            try {
                server.ping((ping, throwable) -> {
                    if (ping != null) {
                        callback.done(new ServerStatus(
                                ping.getDescription(),
                                ping.getPlayers().getOnline(),
                                ping.getPlayers().getMax()
                        ), throwable);
                    } else {
                        callback.done(null, throwable);
                    }
                });
            } catch (Exception e) {
                callback.done(null, e);
            }
        }
    };

    public abstract void ping(ServerInfo server, Callback<ServerStatus> callback, LobbyBalancer plugin);
}
