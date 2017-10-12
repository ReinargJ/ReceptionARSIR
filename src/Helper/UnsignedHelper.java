package Helper;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Epulapp
 */
public class UnsignedHelper {
        /**
     * This method receives an signed int and converts it to the equivalent
     * unsigned byte values of the two least significant bytes of the int.
     * @param val an signed integer value 0 <= val <= 2^16 - 1
     * @return byte[] containing the unsigned equivalent of the entered
     * value of the integer to the method. The least significant bits in
     * index 1 and the most significant bits in index 0.
     * */
    public synchronized static byte[] intTo2UnsignedBytes(int val)
    {
        int upperBound = (int) Math.round(Math.pow(2,16) - 1);
        int lowerBound = 0;
        if (val < lowerBound || val > upperBound)
        {
            throw new IllegalArgumentException("Argument has to be 0 <= val <= (2^16-1)");
        }
        int no256s = val / 256;
        byte[] out = new byte[2];
        out[0] = (byte) no256s;
        out[1] = (byte) (val % 256);
        return out;
    }

    /**
     * Returns the signed integer value of an 16-bit binary number
     * @param inbytes the 16-bit unsigned value
     * @return the same value expressed as an 32-bit signed integer
     */
    public synchronized static int twoBytesToInt(byte[] inbytes)
    {
        int first = Byte.toUnsignedInt(inbytes[0]);
        int second = Byte.toUnsignedInt(inbytes[1]);
        int result = first * 256 + second;
        return result;
    }
}
