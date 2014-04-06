/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.security.MessageDigest;

/**
 *
 * @author kt1
 */
public class securityLib {

    public static String getMD5(String data) {
        String result="";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(data.getBytes());
            result = toHex(digest);
        } catch (Exception ex) {
        }
        return result;
    }
    
    public static String[] getMD5_mpos(String data) {
        String[] result=new String[2];
        
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(data.getBytes());
            result[0] = toHex(digest).substring(0, 16);
            result[1] = toHex(digest).substring(16 );
        } catch (Exception ex) {
        }
        return result;
    }

    public static String toHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(a[i] & 0x0f, 16));
        }
        return sb.toString();
    }
}
