package com.rapid.searchengine;

import jakarta.servlet.http.HttpServletRequest;

import java.net.*;
import java.util.Enumeration;

public class Utilities {
    public static boolean isSameNetwork(HttpServletRequest request) {
        try {
            // Get the client IP address
            String clientIp = request.getRemoteAddr();
            InetAddress clientAddress = InetAddress.getByName(clientIp);
            // Get the server IP addresses
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        // Check if the client IP address and the server IP address are in the same subnet
                        if (isInSameSubnet(clientAddress, address)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isInSameSubnet(InetAddress clientAddress, InetAddress serverAddress) {
        byte[] client = clientAddress.getAddress();
        byte[] server = serverAddress.getAddress();
        // Compare the first three bytes (the network part) of the IP addresses
        return client[0] == server[0] && client[1] == server[1] && client[2] == server[2];
    }
}
