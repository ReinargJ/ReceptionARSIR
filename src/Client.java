import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.SocketException;
/**
 * Created by Epulapp on 12/10/2017.
 */
public class Client {
    public static String recevoirFichier(String ip, String port, String destination, String fileName) throws SocketException, IOException {
        DatagramSocket basicSocket = new DatagramSocket();
        basicSocket.setSoTimeout(10000); //on indique 10s d'attente maximum du socket

        //création de la request RRQ
        byte code = 1;
        byte[] RRQRequest = createRRQRequest(code, fileName, "octet");
        int length = RRQRequest.length;
        int intPort = Integer.parseInt(port);
        basicSocket.send(CreateDTGPacket(RRQRequest, length, ip, intPort));

        // création d'un DTGPacket vide pour récéptionner
        byte[] emptyByte = new byte[512];
        DatagramPacket rcDp = CreateDTGPacket(emptyByte, emptyByte.length, ip, intPort );

        basicSocket.receive(rcDp);

        return "LA";
    }

    protected static DatagramPacket CreateDTGPacket(byte[] buf, int packetSize, String adresse, int port) throws UnknownHostException
    {
        return new DatagramPacket(buf, packetSize , InetAddress.getByName(adresse), port);
    }

    protected static boolean isLastPacket(DatagramPacket datagramPacket) {
        if (datagramPacket.getLength() < 512)
            return true;
        else
            return false;
    }

    public static byte[] createRRQRequest(byte opCode, String fileName, String mode) {
        byte zero = 0;
        int rrqByteLength = 2 + fileName.length() + 1 + mode.length() + 1;
        byte[] rrByteArray = new byte[rrqByteLength];

        int position = 0;
        rrByteArray[position] = zero;
        position++;
        rrByteArray[position] = opCode;
        position++;
        for (int i = 0; i < fileName.length(); i++) {
            rrByteArray[position] = (byte) fileName.charAt(i);
            position++;
        }
        rrByteArray[position] = zero;
        position++;
        for (int i = 0; i < mode.length(); i++) {
            rrByteArray[position] = (byte) mode.charAt(i);
            position++;
        }
        rrByteArray[position] = zero;
        return rrByteArray;
    }


}