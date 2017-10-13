import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by Epulapp on 12/10/2017.
 */
public class Client {
    public static String recevoirFichier(String ip, String port, String destination, String fileName) throws SocketException, IOException {
        String message = "Fichier récupéré avec succès";

        DatagramSocket basicSocket = new DatagramSocket();
        basicSocket.setSoTimeout(10000); //on indique 10s d'attente maximum du socket

        //création de la request RRQ
        byte code = 1;
        byte[] RRQRequest = createRRQRequest(code, fileName, "octet");
        int length = RRQRequest.length;
        int intPort = Integer.parseInt(port);

        basicSocket.send(CreateDTGPacket(RRQRequest, length, ip, intPort));

        // création d'un DTGPacket vide pour récéptionner
        byte[] emptyByte = new byte[516];
        DatagramPacket rcDp = CreateDTGPacket(emptyByte, emptyByte.length, ip, intPort);

        basicSocket.receive(rcDp);

        try {

            byte[] packetNb = getPackNb(rcDp);
            byte[] ack1 = new byte[]{(byte) 0, (byte) 1};
            byte[] rawData = rcDp.getData();

            if(rawData[1] == (byte)5){
                message = getErrorPacket(rcDp);
                return message;
            }

            if (Arrays.equals(ack1, packetNb)) {

                int oldPort = intPort;
                intPort = rcDp.getPort();
                byte[] buffer = getData(rawData);
                int sizePacket = rcDp.getLength();

                //On ouvre le fichier
                FileOutputStream out = new FileOutputStream(destination + "\\" + fileName);
                out.write(buffer);


                while (sizePacket >= 516) {
                    int tentatives = 0;
                    boolean timeout = false;

                    do {
                        try {
                            basicSocket.send(CreateDTGPacket(createAcknowledge(getPackNb(rcDp)), 4, ip, intPort));

                            rcDp = CreateDTGPacket(new byte[516], 516, ip, oldPort);
                            basicSocket.receive(rcDp);


                        } catch (SocketTimeoutException e) {
                            timeout = true;
                            tentatives++;

                            if (tentatives >= 5) {
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

                    rawData = rcDp.getData();

                    if(rawData[1] == (byte)5){
                        message = getErrorPacket(rcDp);
                        return message;
                    }

                    buffer = getData(rawData);
                    sizePacket = rcDp.getLength();
                    out.write(buffer);
                }

                basicSocket.send(CreateDTGPacket(createAcknowledge(getPackNb(rcDp)), 4, ip, intPort));

                out.close();
            }
        } catch (Exception e) {
            message = e.getMessage();
        }finally {
            return message;
        }
    }


    //Creation d'un datagramPacket
    protected static DatagramPacket CreateDTGPacket(byte[] buf, int packetSize, String adresse, int port) throws UnknownHostException
    {
        return new DatagramPacket(buf, packetSize , InetAddress.getByName(adresse), port);
    }

    //
    public static byte[] createAcknowledge(byte[] packetNb) {
        byte zeroByte = 0;

        int wrqByteLength = 4;
        byte[] wrqByteArray = new byte[wrqByteLength];

        int position = 0;
        wrqByteArray[position] = zeroByte;
        position++;
        wrqByteArray[position] = (byte)4; //Aknowledge = type 4
        position++;
        wrqByteArray[position] = packetNb[0];
        position++;
        wrqByteArray[position] = packetNb[1];

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


    public static byte[] getPackNb(DatagramPacket dtp){
        byte[] data = dtp.getData();

        return new byte[]{data[2], data[3]};
    }

    public static byte[] getData(byte[] data){

        byte[] datarcv = new byte[data.length-4];
        for(int i = 0 ; i<data.length-4;i++){ datarcv[i] = data[i+4];}

        return datarcv;
    }

    public static final String[] errorList = {
            "Non défini, voir le message d'erreur",
            "Fichier non trouvé",
            "Disque plein ou dépassement de l'espace alloué" ,
            "Opération TFTP illégale" ,
            "Transfert ID inconnu",
            "Le fichier existe déjà",
            "Utilisateur inconnu" };

    public static String getErrorPacket(DatagramPacket recvDatagramPacket)
    {
        byte[] data = recvDatagramPacket.getData();
        byte errorCode = data[3];

        return errorList[(int)errorCode];
    }
}
