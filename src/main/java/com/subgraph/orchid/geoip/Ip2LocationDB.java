package com.subgraph.orchid.geoip;

import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.cursors.LongObjectCursor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Ip2LocationDB {

    private final static Logger logger = Logger.getLogger(Ip2LocationDB.class.getName());
    private final static String DATABASE_FILENAME = "IP2LOCATION-LITE-DB1.CSV";

    private static final Pattern filePattern = Pattern
            .compile("\"(\\d+)\",\"(\\d+)\",\"(.+)\",\"(.+)\"");


    private volatile LongObjectMap<ArrayList<IpRange>> geoTable;
    private volatile List<Country> countries = new ArrayList<>();

    public Ip2LocationDB() {
        fillData();
    }

    public void reload() {
        fillData();
    }

    public String lookup(final String ipAddress) {
        return lookupCountry(IpRange.calculateSubnet(ipAddress), IpRange.ipToLong(ipAddress));
    }

    public String lookup(final long ipAddress) {
        return lookupCountry(IpRange.calculateSubnet(ipAddress), ipAddress);
    }

    private String lookupCountry(final long subnet, final long ip) {
        String retval = null;
        List<com.subgraph.orchid.geoip.IpRange> l = geoTable.get(subnet);
        if (l != null) {
            for (IpRange range : l) {
                if (range.contains(ip)) {
                    retval = range.location;
                    break;
                }
            }
        }
        return retval;
    }

    public List<Country> getCountries() {
        return countries;
    }

    private synchronized void fillData() {
        LongObjectMap<ArrayList<IpRange>> map = new LongObjectHashMap<>();
        Set<Country> countriesSet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Ip2LocationDB.class.getResourceAsStream("/data/" + DATABASE_FILENAME)))) {
            String s;
            while ((s = br.readLine()) != null) {
                putGeoDataFromString(s, map, countriesSet);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can`t read geodata line", e);
        } catch (Exception e) {
            throw new IllegalStateException("Can`t load geodata", e);
        }
        // free not user memory
        for (LongObjectCursor<ArrayList<IpRange>> c : map) {
            c.value.trimToSize();
        }
        geoTable = map;
        countries = countriesSet.stream().sorted((a, b) -> a.getName().compareTo(b.getName()))
                .collect(Collectors.toList());
    }

    private void putGeoDataFromString(String geoString, LongObjectMap<ArrayList<IpRange>> map,
            Set<Country> countriesSet) {
        Matcher matcher = filePattern.matcher(geoString);
        if (matcher.find()) {
            long startIp = Long.parseLong(matcher.group(1));
            long endIp = Long.parseLong(matcher.group(2));

            String locationId = matcher.group(3);
            String locationName = matcher.group(4);

            countriesSet.add(new Country(locationId, locationName));

            ArrayList<IpRange> l = null;
            for (long i = IpRange.calculateSubnet(startIp); i <= IpRange.calculateSubnet(endIp); i++) {
                l = map.get(i);
                if (l == null) {
                    l = new ArrayList<>();
                    l.add(new IpRange(startIp, endIp, locationId));
                    map.put(i, l);
                } else {
                    l.add(new IpRange(startIp, endIp, locationId));
                }
            }
        }
    }

    public static Ip2LocationDB geoData() {
        return getInstance();
    }

    private static Ip2LocationDB getInstance() {
        return InstanceHolder.geodata;
    }

    private static class InstanceHolder {
        static Ip2LocationDB geodata = new Ip2LocationDB();

    }

}
