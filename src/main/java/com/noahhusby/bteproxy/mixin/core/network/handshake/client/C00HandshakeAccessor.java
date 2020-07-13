package com.noahhusby.bteproxy.mixin.core.network.handshake.client;

import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(C00Handshake.class)
public interface C00HandshakeAccessor {

    @Accessor("ip")  String accessor$getIp();

    @Accessor("ip") void accessor$setIp(String ip);

    @Accessor("port") int accessor$getPort();
}
