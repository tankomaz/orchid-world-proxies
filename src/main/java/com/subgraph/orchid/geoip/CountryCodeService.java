package com.subgraph.orchid.geoip;

import com.subgraph.orchid.data.IPv4Address;

public class CountryCodeService {

	private final static CountryCodeService DEFAULT_INSTANCE = new CountryCodeService();
	public static CountryCodeService getInstance() {
		return DEFAULT_INSTANCE;
	}

    private MaxMindDB maxMindDB;
    private Ip2LocationDB ip2LocationDB;
    private DBIP dbip;

	public CountryCodeService() {
		this.maxMindDB = MaxMindDB.geoData();
        this.ip2LocationDB = Ip2LocationDB.geoData();
        this.dbip = DBIP.geoData();
	}
	
	public String getCountryCodeForAddress(IPv4Address address) {
        String maxMindIso = maxMindDB.getCountryCodeForAddress(address);
        String ip2locationIso = ip2LocationDB.lookup(address.toString());
        String dbIpIso = dbip.lookup(address.toString());
        if (maxMindIso != null && maxMindIso.equalsIgnoreCase(ip2locationIso) && maxMindIso.equalsIgnoreCase(dbIpIso)) {
            return maxMindIso;
        } else {
            return "-UNKNOWN-";
        }
	}

}
