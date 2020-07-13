package com.noahhusby.bteproxy.bridge.network;

import java.net.InetSocketAddress;

public interface NetworkManagerBridge {

    InetSocketAddress bridge$getAddress();

    InetSocketAddress bridge$getVirtualHost();

    void bridge$setVirtualHost(String host, int port);

    void bridge$setVersion(int version);
}
