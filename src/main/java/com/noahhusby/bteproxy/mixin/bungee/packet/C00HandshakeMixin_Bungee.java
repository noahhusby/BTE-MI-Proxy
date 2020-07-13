package com.noahhusby.bteproxy.mixin.bungee.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(C00Handshake.class)
public abstract class C00HandshakeMixin_Bungee {

    @Shadow public String ip;

    @Redirect(method = "readPacketData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readString(I)Ljava/lang/String;"))
    private String bungee$patchReadStringForPortForwarding(final PacketBuffer buf, final int value) {
        return buf.readString(Short.MAX_VALUE);
    }

}
