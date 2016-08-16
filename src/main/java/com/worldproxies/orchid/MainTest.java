package com.worldproxies.orchid;

import com.worldproxies.orchid.worldproxy.IsoProxyFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainTest {

    public static void main(String[] args) throws UnknownHostException {
        IsoProxyFactory.getProxy("US");
        InetAddress addr = InetAddress.getByName("204.85.191.30");
        String host = addr.getHostName();
        System.out.println(host);
        System.out.println(IsoProxyFactory.getIsoPorts());
    }

}
