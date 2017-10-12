package model;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Helper.UnsignedHelper;

import java.net.DatagramPacket;

/**
 *
 * @author Epulapp
 */
public class ACK {
    private byte[] opcode = {0, 4};
    private byte[] blockNr;
    private int blockNrInt;


    public ACK(byte b, byte b1)
    {
        blockNr = new byte[]{b, b1};
        //System.out.println("Packet nr == " + b + "," + b1);
        blockNrInt = UnsignedHelper.twoBytesToInt(blockNr);
    }

    /**
     * Returns what block nr that was received with the ack
     * @return
     */
    public int getAckNr()
    {
        return blockNrInt;
    }

    public String toString()
    {
        return "model.ACK, nr: " + blockNrInt;
    }

    public byte[] returnAckAsBytes(){

        byte[] out = new byte[4];
        out[0] = opcode[0];
        out[1] = opcode[1];
        out[2] = blockNr[0];
        out[3] = blockNr[1];
        return out;
    }
    
    public static ACK getAck(DatagramPacket recvDatagramPacket) throws IllegalStateException
    {
        byte[] data = recvDatagramPacket.getData();

        if (data[0] != 0 && data[1] != 4)
        {
            ErrorPacket error = ErrorPacket.getErrorPacket(recvDatagramPacket);
            throw new IllegalStateException(ErrorPacket.errorList[error.codeError]+" "+error.getMessage());
        }
        return new ACK(data[2], data[3]);
    }
}
