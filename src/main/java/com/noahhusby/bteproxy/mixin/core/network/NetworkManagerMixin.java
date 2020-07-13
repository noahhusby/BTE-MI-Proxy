package com.noahhusby.bteproxy.mixin.core.network;

import com.noahhusby.bteproxy.bridge.network.NetworkManagerBridge;
import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalAddress;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

@SuppressWarnings("rawtypes")
@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin extends SimpleChannelInboundHandler implements NetworkManagerBridge {

    @Shadow
    private Channel channel;

    @Shadow public abstract SocketAddress getRemoteAddress();

    @Nullable
    private InetSocketAddress impl$virtualHost;

    @Override
    public InetSocketAddress bridge$getAddress() {
        final SocketAddress remoteAddress = getRemoteAddress();
        if (remoteAddress instanceof LocalAddress) { // Single player
            return InetSocketAddress.createUnresolved("127.0.0.1", 0);
        }
        return (InetSocketAddress) remoteAddress;
    }

    @Override
    public InetSocketAddress bridge$getVirtualHost() {
        if (this.impl$virtualHost != null) {
            return this.impl$virtualHost;
        }
        final SocketAddress local = this.channel.localAddress();
        if (local instanceof LocalAddress) {
            return InetSocketAddress.createUnresolved("127.0.0.1", 0);
        }
        return (InetSocketAddress) local;
    }

    @Override
    public void bridge$setVirtualHost(final String host, final int port) {
        try {
            this.impl$virtualHost = new InetSocketAddress(InetAddress.getByAddress(host,
                    ((InetSocketAddress) this.channel.localAddress()).getAddress().getAddress()), port);
        } catch (UnknownHostException e) {
            this.impl$virtualHost = InetSocketAddress.createUnresolved(host, port);
        }
    }
}
