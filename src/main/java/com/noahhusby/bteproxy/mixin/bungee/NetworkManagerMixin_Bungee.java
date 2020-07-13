package com.noahhusby.bteproxy.mixin.bungee;

import com.mojang.authlib.properties.Property;
import com.noahhusby.bteproxy.bridge.network.NetworkManagerBridge_Bungee;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;
import java.util.UUID;

@SuppressWarnings("rawtypes")
@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin_Bungee extends SimpleChannelInboundHandler implements NetworkManagerBridge_Bungee {

    @Shadow private SocketAddress socketAddress;

    private UUID impl$spoofedUUID;
    private Property[] impl$spoofedProfile;

    @Override
    public void bungeeBridge$setRemoteAddress(final SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public UUID bungeeBridge$getSpoofedUUID() {
        return this.impl$spoofedUUID;
    }

    @Override
    public void bungeeBridge$setSpoofedUUID(final UUID uuid) {
        this.impl$spoofedUUID = uuid;
    }

    @Override
    public Property[] bungeeBridge$getSpoofedProfile() {
        return this.impl$spoofedProfile;
    }

    @Override
    public void bungeeBridge$setSpoofedProfile(final Property[] profile) {
        this.impl$spoofedProfile = profile;
    }
}
