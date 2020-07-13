package com.noahhusby.bteproxy.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import com.noahhusby.bteproxy.bridge.network.NetworkManagerBridge_Bungee;
import com.noahhusby.bteproxy.mixin.core.network.handshake.client.C00HandshakeAccessor;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.Arrays;

@Mixin(NetHandlerHandshakeTCP.class)
public abstract class NetHandlerHandshakeTCPMixin_Common_Bungee {

    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private NetworkManager networkManager;

    @Inject(method = "processHandshake", at = @At(value = "HEAD"), cancellable = true)
    private void bungee$patchHandshake(final C00Handshake packetIn, final CallbackInfo ci) {
        if (packetIn.getRequestedState().equals(EnumConnectionState.LOGIN)) {
            final String[] split = ((C00HandshakeAccessor) packetIn).accessor$getIp().split("\00\\|", 2)[0].split("\00"); // ignore any extra data

            if (split.length == 3 || split.length == 4) {
                ((C00HandshakeAccessor) packetIn).accessor$setIp(split[0]);
                ((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$setRemoteAddress(new InetSocketAddress(split[1],
                        ((InetSocketAddress) this.networkManager.getRemoteAddress()).getPort()));
                ((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));

                if (split.length == 4) {
                    ((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$setSpoofedProfile(gson.fromJson(split[3], Property[].class));
                }
            } else {
                final TextComponentString chatcomponenttext =
                        new TextComponentString("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                this.networkManager.sendPacket(new SPacketDisconnect(chatcomponenttext));
                this.networkManager.closeChannel(chatcomponenttext);
            }
        }
    }
}