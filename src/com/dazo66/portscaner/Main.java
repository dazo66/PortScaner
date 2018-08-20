package com.dazo66.portscaner;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Dazo66
 */
public class Main {

    private String host;
    private String ports;
    private int timeout;

    public Main(String hostIn, String portsIn) {
        this(hostIn,portsIn,200);
    }

    public Main(String hostIn, String portsIn, int timeoutIn){
        host = hostIn;
        ports = portsIn;
        timeout = timeoutIn;

        final List<Future<ScanResult>> futures = new ArrayList<>();
        final ExecutorService es = Executors.newFixedThreadPool(10);
        int[] ports = getPorts(this.ports);

        if (null != ports) {
            for (int i : ports) {
                try {
                    futures.add(portIsOpen(es, host, i, timeout));
                } catch (NumberFormatException e) {
                    System.out.println("NumberFormatException" + e.getMessage());
                }
            }
        } else {
            for (int port = 1; port <= 65535; port++) {
                futures.add(portIsOpen(es, host, port, timeout));
            }
        }
        es.shutdown();
        for (final Future<ScanResult> f : futures) {
            try {
                if (!f.get().isOpen()) {
                    System.out.println(f.get().getPort() + " is closed.");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private int[] getPorts(String rawPort) {
        int[] array = null;
        Integer integer = null;
        try {
            integer = Integer.valueOf(rawPort);
        }catch (NumberFormatException e) {
            //nothing to do
        }
        if (rawPort.contains(",")) {
            String[] strings = rawPort.split(",");
            array = new int[strings.length];
            int length = array.length;
            for (int i = 0; i < length; i++) {
                array[i] = Integer.valueOf(strings[i]);
            }

        }else if (rawPort.contains("-")){
            String[] temp = rawPort.split("-");
            int start = Integer.valueOf(temp[0]);
            int end = Integer.valueOf(temp[1]);
            array = new int[end - start + 1];
            for (int i = 0; i < end - start + 1 ; i++) {
                array[i] = start + i;
            }
        }
        if (integer != null) {
            return new int[]{integer};
        }
        return array;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            new Main(args[0], args[1]);
        }else if (args.length == 3){
            String s = args[2];
            new Main(args[0], args[1], Integer.valueOf(args[2]));
        }else {
            throw new IllegalArgumentException("IllegalArgumentException! args:" + Arrays.toString(args));
        }
    }

    static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(() -> {
            try {
                Thread.sleep(5);
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.close();
                return new ScanResult(port, true);
            } catch (Exception ex) {
                return new ScanResult(port, false);
            }
        });
    }



}

