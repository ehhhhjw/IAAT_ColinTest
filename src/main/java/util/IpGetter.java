package util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by homer on 16-12-8.
 */
public class IpGetter {
        /**
         * 单网卡名称
         */
        private static final String NETWORK_CARD = "eth0";

        /**
         * 绑定网卡名称
         */
        private static final String NETWORK_CARD_BAND = "bond0";

        private static final String OTHER = "enp1s0";

        public void main(String[] args) {
            print("the " + IpGetter.getLocalIP());
        }

        /**
         *
         * Description: 得到本机名<br>
         * @return
         * @see
         */
        public static String getLocalHostName()
        {
            try
            {
                InetAddress addr = InetAddress.getLocalHost();
                return addr.getHostName();
            }
            catch (Exception e)
            {
                return "";
            }
        }

        /**
         * Description: linux下获得本机IPv4 IP<br>
         * @return
         * @see
         */
        public static String getLocalIP()
        {
            String ip = "";
            try
            {
                Enumeration<NetworkInterface> e1 = (Enumeration<NetworkInterface>)NetworkInterface.getNetworkInterfaces();
                while (e1.hasMoreElements())
                {
                    NetworkInterface ni = e1.nextElement();
                    print(" the name is" + ni.getName());
                    //单网卡或者绑定双网卡
                    if ((NETWORK_CARD.equals(ni.getName()))
                            || (NETWORK_CARD_BAND.equals(ni.getName())
                            || (OTHER.equals(ni.getName()))))
                    {
                        Enumeration<InetAddress> e2 = ni.getInetAddresses();
                        while (e2.hasMoreElements())
                        {
                            InetAddress ia = e2.nextElement();
                            print(" ddddd the name is" + ni.getName() + " " + ia.getHostAddress());
                            if (ia instanceof Inet6Address)
                            {
                                continue;
                            }
                            ip = ia.getHostAddress();
                        }
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
            }
            catch (SocketException e)
            {
                print("SocketException");
            }
            return ip;
        }

    public static void test() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets))
                displayInterfaceInformation(netint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      public  static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {

            System.out.printf("Display name: %s\n", netint.getDisplayName());
            System.out.printf("Name: %s\n", netint.getName());
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                System.out.printf("InetAddress: %s\n", inetAddress);
            }
            System.out.printf("\n");
        }
  	private static void print(String msg) {
		String TAG = Thread.currentThread() .getStackTrace()[1].getClassName();
		SimpleDateFormat df=new SimpleDateFormat("MM-dd HH:mm:ss,SSS"); 
		Date date = new Date(System.currentTimeMillis());
		System.out.println(df.format(date) + " " + TAG + " - " + msg);
	}
}