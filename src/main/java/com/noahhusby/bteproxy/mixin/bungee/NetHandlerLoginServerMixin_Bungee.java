package com.noahhusby.bteproxy.mixin.bungee;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.noahhusby.bteproxy.bridge.network.NetworkManagerBridge_Bungee;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(NetHandlerLoginServer.class)
public abstract class NetHandlerLoginServerMixin_Bungee {

    @Shadow @Final private MinecraftServer server;
    @Shadow @Final public NetworkManager networkManager;
    @Shadow private GameProfile loginGameProfile;

    @Inject(method = "processLoginStart",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/NetHandlerLoginServer;loginGameProfile:Lcom/mojang/authlib/GameProfile;",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void bungee$initUuid(final CallbackInfo ci) {
        if (true) {
            final UUID uuid;
            if (((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$getSpoofedUUID() != null) {
                uuid = ((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$getSpoofedUUID();
            } else {
                uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.loginGameProfile.getName()).getBytes(Charsets.UTF_8));
            }

            this.loginGameProfile = new GameProfile(uuid, this.loginGameProfile.getName());

            if (((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$getSpoofedProfile() != null) {
                for (final Property property : ((NetworkManagerBridge_Bungee) this.networkManager).bungeeBridge$getSpoofedProfile()) {
                    this.loginGameProfile.getProperties().put(property.getName(), property);
                }
            }
        }
    }
}
