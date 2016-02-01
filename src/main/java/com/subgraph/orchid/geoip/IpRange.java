package com.subgraph.orchid.geoip;

public class IpRange {

    // ip address consists of 4 parts (each 8 bits), 4 is maximum of SUBNET_PARTS value!
    // if SUBNET_PARTS is 4, we got bucket to integer.max_value
    private final static long SUBNET_PARTS = 2;

    final long startIp;
    final long endIp;
    final String location;

    IpRange(final String startIp, final String endIp, final String location) {
        this.startIp = ipToLong(startIp);
        this.endIp = ipToLong(endIp);
        this.location = location;
    }

    IpRange(final long startIp, final long endIp, final String location) {
        this.startIp = startIp;
        this.endIp = endIp;
        this.location = location;
    }

    boolean contains(final String ipAddress) {
        long ip = ipToLong(ipAddress);
        return contains(ip);
    }

    boolean contains(final long ipAddress) {
        return ipAddress <= endIp && ipAddress >= startIp;
    }

    public static long calculateSubnet(final String ipAddress) {
        return parseLongFromIp(ipAddress.split("\\."), SUBNET_PARTS);
    }

    public static long calculateSubnet(final long ipAddress) {
        return ipAddress >>> (SUBNET_PARTS * 8);
    }

    public static long parseLongFromIp(final String[] ipAddress, final long parts) {
        long result = 0;
        for (int i = 0; i < parts; i++) {
            long ip = Long.parseLong(ipAddress[i]);
            result |= ip << ((parts - 1 - i) * 8);
        }
        return result;
    }

    public static long ipToLong(final String ipAddress) {
        final String[] ipAddressInArray = ipAddress.split("\\.");
        return parseLongFromIp(ipAddressInArray, ipAddressInArray.length);
    }
}

