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
public class ErrorPacket {
     private byte[] opcode = {0, 5};
    public byte[] codeEr;
    private String messageStr;
    private byte[] message;
    public int codeError;
    
   public static final String[] errorList = {
       "Non défini, voir le message d'erreur",
       "Fichier non trouvé",
       "Disque plein ou dépassement de l'espace alloué" ,
       "Opération TFTP illégale" ,
       "Transfert ID inconnu",
       "Le fichier existe déjà",
       "Utilisateur inconnu" };

    
    public ErrorPacket(byte b, byte b1, byte[] msg)
    {
        codeEr = new byte[]{b, b1};
        message = msg;
        messageStr = msg.toString();
        codeError  = UnsignedHelper.twoBytesToInt(codeEr);
    }
    
    public int getCodeError()
    {
        return codeError;
    }
    
    public static ErrorPacket getErrorPacket(DatagramPacket recvDatagramPacket) throws IllegalStateException
    {
        byte[] data = recvDatagramPacket.getData();

        if (data[0] != 0 && data[1] != 5)
        {
          
            throw new IllegalStateException("");
            
        }
        
        byte[] datarcv = new byte[data.length-4];
        
        for(int i = 0 ; i<data.length-4;i++)
        {
            
            datarcv[i] = data[i+4];
            
            if(data[i+4]==0) break;
            
        }
        return new ErrorPacket(data[2], data[3],datarcv );
    }
    
    public String getMessage()
    {
        return messageStr ;
    }
}
