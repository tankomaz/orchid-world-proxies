import com.subgraph.orchid.geoip.Ip2LocationDB;
import com.subgraph.orchid.worldproxy.IsoProxyFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class MainClass {

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            Proxy proxy1 = IsoProxyFactory.getProxy("US");
        } catch (Throwable t) {
            System.err.println(">" + t.getMessage());

        }
        Thread.sleep(12000);

        System.out.println(">" + IsoProxyFactory.getIsoPorts());

        System.out.println("STAAAAART");
        new Thread(new Runnnerr(1)).start();
        new Thread(new Runnnerr(2)).start();
        new Thread(new Runnnerr(3)).start();
        new Thread(new Runnnerr(4)).start();
        new Thread(new Runnnerr(5)).start();
        new Thread(new Runnnerr(6)).start();
        Thread.sleep(1000000);
    }

    private static class Runnnerr implements Runnable {
        private int threadId;

        public Runnnerr(int threadId) {
            this.threadId = threadId;
        }
            static AtomicInteger i = new AtomicInteger(0);

        @Override
        public void run() {
            while (true) {
                System.out.println("Thread["+threadId+"]");
                try {
                    Proxy proxyUS = IsoProxyFactory.getProxy("US");
                    String ipUS = getMyIpUS(proxyUS);
                    String targetIsoUS = Ip2LocationDB.geoData().lookup(ipUS);
                    if (!"US".equals(targetIsoUS)) {
                        System.out.println("Blya....wait US (but:" + targetIsoUS + ") ip:" + ipUS + "double:" + doubleCheck(proxyUS));
                    }
                    Proxy proxyCa = IsoProxyFactory.getProxy("CA");
                    String ipCA = getMyIpCA(proxyCa);
                    String targetIsoCA = Ip2LocationDB.geoData().lookup(ipCA);
                    if (!"CA".equals(targetIsoCA)) {
                        System.out.println("Blya....wait CA (but:" + targetIsoCA + ") ip:" + ipCA + "double:" + doubleCheck(proxyCa));
                    }
                    Proxy proxyFR = IsoProxyFactory.getProxy("FR");
                    String ipFR = getMyIpFR(proxyFR);
                    String targetIsoFR = Ip2LocationDB.geoData().lookup(ipFR);
                    if (!"FR".equals(targetIsoFR)) {
                        System.out.println("Blya....wait FR (but:" + targetIsoFR + ") ip:" + ipFR + "double:" + doubleCheck(proxyFR));
                    }
                    Proxy proxyUA = IsoProxyFactory.getProxy("UA");
                    String ipUA = getMyIpUA(proxyUA);
                    String targetIsoUA = Ip2LocationDB.geoData().lookup(ipUA);
                    if (!"UA".equals(targetIsoUA)) {
                        System.out.println("Blya....wait UA (but:" + targetIsoUA + ") ip:" + ipUA + "double:" + doubleCheck(proxyUA));
                    }
                } catch (Throwable t) {
                    System.err.println(">>err>" + t.getMessage());
                } finally {
                }
                if (i.getAndIncrement() % 100 == 0) {
                    System.out.println("I am working..." + new Date());
                }
            }
        }
        private String getMyIpUS(Proxy proxy) throws IOException {
            URL url = new URL("http://checkip.amazonaws.com/");
            URLConnection connection = url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return br.readLine();
        }

        private String getMyIpCA(Proxy proxy) throws IOException {
//            URL url = new URL("http://checkip.amazonaws.com/");
            URL url = new URL("http://ipinfo.io/ip");
            URLConnection connection = url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return br.readLine();
        }

        private String getMyIpFR(Proxy proxy) throws IOException {
//            URL url = new URL("http://checkip.amazonaws.com/");
            URL url = new URL("http://ipinfo.io/ip");
            URLConnection connection = url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return br.readLine();
        }
        private String getMyIpUA(Proxy proxy) throws IOException {
//            URL url = new URL("http://checkip.amazonaws.com/");
            URL url = new URL("http://ipinfo.io/ip");
            URLConnection connection = url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return br.readLine();
        }

        private String doubleCheck(Proxy proxy) throws IOException {
//            URL url = new URL("http://ipinfo.io/ip");
            URL url = new URL("http://checkip.amazonaws.com/");
            URLConnection connection = url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return br.readLine();
        }
    }

}
