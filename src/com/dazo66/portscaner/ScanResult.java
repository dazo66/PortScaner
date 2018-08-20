package com.dazo66.portscaner;


   public class ScanResult {

        private final int port;
        private final boolean isOpen;

        public ScanResult(int portIn, boolean isOpenIn){
            port = portIn;
            isOpen = isOpenIn;
        }

        public int getPort() {
            return port;
        }

        public boolean isOpen() {
            return isOpen;
        }

}
