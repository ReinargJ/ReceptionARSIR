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
public class DataPacket {
    
    private byte[] opcode = {0, 4};
    private byte[] blockNr;
    private int blockNrInt;
    private byte[] data;
    
    public DataPacket(byte b, byte b1, byte[] dataArray)
    {
        blockNr = new byte[]{b, b1};
        data = dataArray;
        blockNrInt = UnsignedHelper.twoBytesToInt(blockNr);
    }
    
    public int getPacketNr()
    {
        return blockNrInt;
    }
    
    public static DataPacket getDataPacket(DatagramPacket recvDatagramPacket) throws IllegalStateException
    {
        byte[] data = recvDatagramPacket.getData();

        if (data[0] != 0 && data[1] != 3)
        {
            ErrorPacket error = ErrorPacket.getErrorPacket(recvDatagramPacket);
            throw new IllegalStateException(ErrorPacket.errorList[error.codeError]+" "+error.getMessage());
        }
        byte[] datarcv = new byte[data.length-4];
        for(int i = 0 ; i<data.length-4;i++){ datarcv[i] = data[i+4];}
        return new DataPacket(data[2], data[3],datarcv );
    }
    
    public byte[] getData()
    {
        return data;
    }
}
