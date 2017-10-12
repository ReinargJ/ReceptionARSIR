import Helper.UnsignedHelper;
import model.DataPacket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by Epulapp on 12/10/2017.
 */
public class Client {
    public static String recevoirFichier(String ip, String port, String destination, String fileName) throws SocketException, IOException {
        String message="";

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

        try {
            DataPacket responsedata = DataPacket.getDataPacket(rcDp);

            if (responsedata.getPacketNr() == 1) {
                int oldPort = intPort;
                intPort = rcDp.getPort();
                int packetNb = 0;
                byte[] buffer = responsedata.getData();
                int sizePacket = rcDp.getLength() - 4;

                //On ouvre le fichier
                FileOutputStream out = new FileOutputStream(destination+"\\"+fileName);
                out.write(buffer);

                packetNb++;

                while (sizePacket >= 512) {
                    int tentatives = 0;
                    boolean timeout = true;

                    do {
                        try {
                            basicSocket.send(CreateDTGPacket(createAcknowledge(packetNb), 4, ip, intPort));

                            rcDp = CreateDTGPacket(new byte[516], 516, ip, intPort);
                            basicSocket.receive(rcDp);

                        } catch (SocketTimeoutException e) {
                            timeout = true;
                            tentatives++;

                            if (tentatives >= 1000) {
                                throw e;
                            }
                        } catch (Exception e) {
                            throw e;
                        } finally {
                            if (timeout == false) {
                                break;
                            }
                        }
                    } while (timeout == true);

                    DataPacket dtpk = DataPacket.getDataPacket(rcDp);

                    if (dtpk.getPacketNr() == packetNb + 1) {
                        packetNb++;

                        buffer = dtpk.getData();
                        sizePacket = rcDp.getLength() - 4;
                        out.write(buffer);
                    }

                }

                out.write(buffer);
                basicSocket.send(CreateDTGPacket(createAcknowledge(packetNb), 4, ip, intPort));

                out.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return message;
    }


    //Creation d'un datagramPacket
    protected static DatagramPacket CreateDTGPacket(byte[] buf, int packetSize, String adresse, int port) throws UnknownHostException
    {
        return new DatagramPacket(buf, packetSize , InetAddress.getByName(adresse), port);
    }

    //
    public static byte[] createAcknowledge(int packetNb) {
        byte zeroByte = 0;
        int wrqByteLength = 4;
        byte[] wrqByteArray = new byte[wrqByteLength];
        byte[] numpacket  = UnsignedHelper.intTo2UnsignedBytes(packetNb);
        int position = 0;
        wrqByteArray[position] = zeroByte;
        position++;
        wrqByteArray[position] = 4; //Aknowledge = type 4
        position++;
        wrqByteArray[position] = numpacket[0];
        position++;
        wrqByteArray[position] = numpacket[1];

        System.out.println(Arrays.toString(wrqByteArray));
        return wrqByteArray;
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
