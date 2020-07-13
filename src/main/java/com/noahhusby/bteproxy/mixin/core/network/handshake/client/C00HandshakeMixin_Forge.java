package com.noahhusby.bteproxy.mixin.core.network.handshake.client;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;

@Mixin(C00Handshake.class)
public abstract class C00HandshakeMixin_Forge {

    @Shadow private int protocolVersion;
    @Shadow public String ip;
    @Shadow public int port;
    @Shadow private EnumConnectionState requestedState;
    @Shadow(remap = false) private boolean hasFMLMarker = false;

    /**
     * @author bloodmc, dualspiral
     *
     * Forge strips out its FML marker when clients connect. This causes
     * BungeeCord's IP forwarding data to be stripped. In order to
     * workaround the issue, we will only strip data if the FML marker
     * is found and wasn't already in the extra data.
     */
    @Inject(method = "readPacketData(Lnet/minecraft/network/PacketBuffer;)V", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void forgeImpl$FixBungeeData(final PacketBuffer buf, final CallbackInfo callbackInfo) throws IOException {

        // Sponge start
        this.protocolVersion = buf.readVarInt();
            this.ip = buf.readString(Short.MAX_VALUE);
            final String[] split = this.ip.split("\0\\|", 2);
            this.ip = split[0];
            // If we have extra data, check to see if it is telling us we have a
            // FML marker
            if (split.length == 2) {
                this.hasFMLMarker = split[1].contains("\0FML\0");
            }


        // Check for FML marker and strip if found, but only if it wasn't
        // already in the extra data.
        if (!this.hasFMLMarker) {
            this.hasFMLMarker = this.ip.contains("\0FML\0");
            if (this.hasFMLMarker) {
                this.ip = this.ip.split("\0")[0];
            }
        }

        this.port = buf.readUnsignedShort();
        this.requestedState = EnumConnectionState.getById(buf.readVarInt());
        // Sponge end

        callbackInfo.cancel();
    }
}
