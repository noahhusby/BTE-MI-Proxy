package com.noahhusby.bteproxy.bridge.network;

import com.mojang.authlib.properties.Property;

import java.net.SocketAddress;
import java.util.UUID;

public interface NetworkManagerBridge_Bungee {
    void bungeeBridge$setRemoteAddress(SocketAddress socketAddress);

    UUID bungeeBridge$getSpoofedUUID();

    void bungeeBridge$setSpoofedUUID(UUID uuid);

    Property[] bungeeBridge$getSpoofedProfile();

    void bungeeBridge$setSpoofedProfile(Property[] profile);
}
