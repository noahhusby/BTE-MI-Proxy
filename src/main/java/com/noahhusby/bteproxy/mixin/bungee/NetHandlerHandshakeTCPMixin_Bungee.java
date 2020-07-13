package com.noahhusby.bteproxy.mixin.bungee;

import com.mojang.authlib.properties.Property;
import com.noahhusby.bteproxy.bridge.network.NetworkManagerBridge_Bungee;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NetHandlerHandshakeTCP.class)
public abstract class NetHandlerHandshakeTCPMixin_Bungee {

    @Redirect(method = "processHandshake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;handleServerHandshake(Lnet/minecraft/network/handshake/client/C00Handshake;Lnet/minecraft/network/NetworkManager;)Z",
                    ordinal = 0,
                    remap = false))
    private boolean bungee$redirectFmlCheck(final FMLCommonHandler handler, final C00Handshake packetIn, final NetworkManager networkManager) {
        // Don't bother if the player is not allowed to log in.
        if (handler.shouldAllowPlayerLogins() && packetIn.getRequestedState() == EnumConnectionState.LOGIN) {
            final Property[] pr = ((NetworkManagerBridge_Bungee) networkManager).bungeeBridge$getSpoofedProfile();
            if (pr != null) {
                for (final Property p : pr) {
                    if ("forgeClient".equalsIgnoreCase(p.getName()) && "true".equalsIgnoreCase(p.getValue())) {
                        // Manually tell the system that we're a FML client.
                        networkManager.channel().attr(NetworkRegistry.FML_MARKER).set(true);
                        return true;
                    }
                }
            }
        }

        networkManager.channel().attr(NetworkRegistry.FML_MARKER).set(true);
        return true;

      //  return handler.handleServerHandshake(packetIn, networkManager);
    }
}