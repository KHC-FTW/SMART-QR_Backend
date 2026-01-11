package com.khchan744.smart_qr_backend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class Utils {

    public static byte[] computePaymentToken(byte[] uidBytes,
                                             byte[] hashedPw,
                                             byte[] paBytes,
                                             byte[] mfBytes)
    {
        byte[] totalByteData = new byte[uidBytes.length + hashedPw.length + paBytes.length + mfBytes.length];
        System.arraycopy(uidBytes, 0, totalByteData, 0, uidBytes.length);
        System.arraycopy(hashedPw, 0, totalByteData, uidBytes.length, hashedPw.length);
        System.arraycopy(paBytes, 0, totalByteData, uidBytes.length + hashedPw.length, paBytes.length);
        System.arraycopy(mfBytes, 0, totalByteData, uidBytes.length + hashedPw.length + paBytes.length, mfBytes.length);
        return sha256Hash(totalByteData);
    }

    public static byte[] sha256Hash(byte[] data) {
        try{
            return MessageDigest.getInstance("SHA-256").digest(data);
        }catch(NoSuchAlgorithmException ignore){}
            return null;
    }

    public static byte[] paymentAmountToBytes(String amount) {
        String[] paymentAmountParts = splitAmountParts(amount);
        return paymentAmountToBytes(paymentAmountParts[0], paymentAmountParts[1]);
    }

    public static byte[] paymentAmountToBytes(String integer, String fraction){
        if (integer.length() > 4 || fraction.length() > 1){
            // integer must be 0 - 9999; fraction must be 0 - 9
            throw new IllegalArgumentException("Invalid payment amount");
        }
        if(fraction.isBlank()) fraction = "0";
        byte[] result = new byte[3]; // 2 bytes for integer, 1 byte for fraction
        byte[] integerBytes = oneDecimalToTwoBytes(Integer.parseInt(integer));
        result[0] = integerBytes[0];
        result[1] = integerBytes[1];
        result[2] = (byte)(Integer.parseInt(fraction) & 0xF);
        return result;
    }

    public static byte[] oneDecimalToTwoBytes(int decimal){
        byte[] result = new byte[2];
        if (decimal < 0 || decimal > 0xFFFF) {
            throw new IllegalArgumentException("Decimal number: " + decimal + " out of range.");
        }
        result[0] = (byte) ((decimal >> 8) & 0xFF);  // most significant byte
        result[1] = (byte) (decimal & 0xFF); // least significant byte
        return result;
    }

    public static String[] splitAmountParts(String amount) {
        int dotIdx = amount.indexOf('.');
        if (dotIdx < 0) return new String[]{amount, ""};
        String integer = amount.substring(0, dotIdx);
        String fraction = (dotIdx + 1 < amount.length()) ? amount.substring(dotIdx + 1) : "";
        return new String[]{integer, fraction};
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64Decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    private Utils() {}
}
