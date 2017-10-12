import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javafx.scene.control.Label;

/**
 * Created by Epulapp on 06/10/2017.
 */
public class ReceiveFile {
    public int receiveFile(String fileName, String path, String serverAddr) {
        File f = new File(fileName + path);
        if (f.exists()) {
            return -1;
        }
        if (f.isDirectory()) {
            return -2;
        }

        //reception(fileName, serverAddr);

        return 0;
    }

    public static int ReceiveFile(String fileToReceive, Label lbError) throws SocketException, IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        byte[] rrqrequest = RequestFactory.createRQRequest(RequestFactory.OP_RRQ, fileToReceive, "octet");
        int length = rrqrequest.length;
        socket.send(CreateDP(rrqrequest, length));

        DatagramPacket rcDp = CreateDP(new byte[516]);

        socket.receive(rcDp);

        try {
            DataPacket responsedata = DataPacket.getDataPacket(rcDp);

            if (responsedata.getPacketNr() == 1) {
                int ancienport = port;
                port = rcDp.getPort();
                int numpacket = 0;
                byte[] buffer = responsedata.getData();
                int taillepacket = rcDp.getLength() - 4;
                FileOutputStream out = new FileOutputStream(fileToReceive);
                out.write(buffer);
                numpacket++;
                while (taillepacket >= 512) {
                    int tentatives = 0;
                    boolean timeout = false;
                    do {
                        try {
                            socket.send(CreateDP(RequestFactory.createAckRequest(numpacket), 4));

                            rcDp = CreateDP(new byte[516], 516);
                            socket.receive(rcDp);

                        } catch (SocketTimeoutException e) {
                            timeout = true;
                            tentatives++;

                            if (tentatives >= SendFile.NB_TENTATIVES) {
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

                    if (dtpk.getPacketNr() == numpacket + 1) {
                        numpacket++;
                        buffer = dtpk.getData();
                        taillepacket = rcDp.getLength() - 4;
                        out.write(buffer);
                    }

                }

                out.write(buffer);
                socket.send(CreateDP(RequestFactory.createAckRequest(numpacket), 4));
                port = ancienport;
                out.close();
            }
        } catch (IllegalStateException ex) {
            lbError.setText(ex.getMessage());
            return 1;
        }
        return 0;

    }
