package com;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpUtil {
    private static List<String> allInet4Address() {
        List<String> values = new ArrayList<String>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                String name = netInterface.getName();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (null == ip || !(ip instanceof Inet4Address)) {
                        continue;
                    }
                    values.add(ip.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return values;
    }

    /**
     * @return
     */
    private static List<String> allEthInet4Address() {
        List<String> values = new ArrayList<String>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                String name = netInterface.getName();
                // windows,linux:eth , ios:en
                if (!name.startsWith("eth") && !name.startsWith("en") && !name.startsWith("bond")) {
                    continue;
                }
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (null == ip || !(ip instanceof Inet4Address)) {
                        continue;
                    }
                    values.add(ip.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return values;
    }

    public static String getLocalIP() {
        String str = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        if (intf.getName().equals("eth0")) {
                            return inetAddress.getHostAddress().toString();
                        }
                        str = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
        }
        return str;
    }

    public static String getExternalIp() {
        List<String> list = IpUtil.allEthInet4Address();
        for (String s : list) {
            if (!internalIp(s.getBytes()))
                return s;
        }
        return "";
    }

    private static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }
}

