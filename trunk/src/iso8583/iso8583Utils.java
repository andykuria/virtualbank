/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

import cfg.cfgNode;
import exceptionshandle.bicsexception;
import lib.CommonLib;

/**
 *
 * @author kt1
 */
public class iso8583Utils {

    /**
     * Ham chuyen doi tu Byte dang mot mang cua cac fields
     * Thanh phan dau vao gom ca MTI + Bitmap + Data Element
     * Khong bao gom do dai cua thong diep (4 byte)
     * @param value
     * @return
     */
    public static String[] ParseIsoMessage(byte[] value, cfgNode isoCfg) throws bicsexception {
        String[] fields = new String[129];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = "";
        }

        try {
            //Chu y: bao gom ca MTI + Bitmap + DataElement
            //do dai cua String tra ve phai la 129 do no bao gom ca MTI + Bitmap
            //vi tri 0 la MTI
            //vi tri 1 la Bitmap
            //Vi tri 2 la PAN
            //Cac vi tri sau tuong tu nhu ISO 8583
            //int[] fieldsLength = new int[129];

            //String strMessage = new String(value);

            String strPrimaryBitmap = "";
            String strBitmap = "";
            String strMTI = new String(CommonLib.copyByteArrayFromArray(value, 0, 4));
            String strBitmapBinary = "";
            String strPrimaryBitmapBinary = "";
            boolean b_haveSecondaryBitmap = false;
            //String strDataElement = "";
            byte[] dataElements = new byte[0];


            if (isoCfg.checkBinaryField(1))//kiem tra loai bipmap Byte hay hexa
            {

                strPrimaryBitmap = CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 4, 8));

                strPrimaryBitmapBinary = CommonLib.getBinaryBitmapFromHexa(strPrimaryBitmap);
                if (Character.toString(strPrimaryBitmapBinary.charAt(0)).equals("0")) {
                    strBitmap = strPrimaryBitmap;
                    strBitmapBinary = strPrimaryBitmapBinary;
                    b_haveSecondaryBitmap = false;
                    //strDataElement = strMessage.substring(12);
                    dataElements = CommonLib.copyByteArrayFromArray(value, 12);
                } else if (Character.toString(strPrimaryBitmapBinary.charAt(0)).equals("1")) {
                    strBitmap = CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 4, 16));
                    b_haveSecondaryBitmap = true;
                    strBitmapBinary = CommonLib.getBinaryBitmapFromHexa(CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 4, 8))) + CommonLib.getBinaryBitmapFromHexa(CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 12, 8)));
                    dataElements = CommonLib.copyByteArrayFromArray(value, 20);
                }
            } else {
                strPrimaryBitmap = new String(CommonLib.copyByteArrayFromArray(value, 4, 16));
                strPrimaryBitmapBinary = CommonLib.getBinaryBitmapFromHexa(strPrimaryBitmap);

                if (Character.toString(strPrimaryBitmapBinary.charAt(0)).equals("0")) {
                    strBitmap = strPrimaryBitmap;
                    strBitmapBinary = strPrimaryBitmapBinary;
                    b_haveSecondaryBitmap = false;
                    dataElements = CommonLib.copyByteArrayFromArray(value, 20);
                } else if (Character.toString(strPrimaryBitmapBinary.charAt(0)).equals("1")) {
                    strBitmap = new String(CommonLib.copyByteArrayFromArray(value, 4, 32));
                    b_haveSecondaryBitmap = true;
                    strBitmapBinary = CommonLib.getBinaryBitmapFromHexa(new String(CommonLib.copyByteArrayFromArray(value, 4, 16))) + CommonLib.getBinaryBitmapFromHexa(new String(CommonLib.copyByteArrayFromArray(value, 20, 16)));
                    dataElements = CommonLib.copyByteArrayFromArray(value, 36);
                }
            }
            //Lay do dai cua tung truong trong thong diep, phuc vu cho viec phan tich
            //ConfigIsoMessage cf = new ConfigIsoMessage();
            //fieldsLength = isoCfg.getFieldsLength();

            //Phan tich tung truong trong message
            fields[0] = strMTI;

            //Add by Minhdbh 10.11.2010
            //if (!strMTI.substring(0,1).equals("0")) throw new bicsexception("Message is not OK");

            fields[1] = strBitmap;

            byte[] restDataElement = CommonLib.copyByteArrayFromArray(dataElements, 0);

            if (b_haveSecondaryBitmap == true) {
                //Xu ly khi co ca Secondary Bitmap
                for (int i = 1; i < 128; i++) {
                    if (Character.toString(strBitmapBinary.charAt(i)).equals("1")) {
                        if (isoCfg.getIntValue(String.valueOf( i + 1)) == 99) {

                            int lengthfield = Integer.parseInt(new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)));
                            fields[i + 1] = new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)) + isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 2, lengthfield));
                            //fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 2, lengthfield)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2 + lengthfield));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 2 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement, 2 + lengthfield);
                        } else if (isoCfg.getIntValue(String.valueOf(i + 1)) == 999) {
                            int lengthfield = Integer.parseInt(new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)));
                            fields[i + 1] = new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)) + isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 3, lengthfield));
                            /*if (i == 47) {
                            
                            fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 5)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 5, lengthfield-2)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3 + lengthfield));
                            } else {
                            fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 3, lengthfield)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3 + lengthfield));
                            }
                             */


                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 3 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement, 3 + lengthfield);
                        } else {
                            fields[i + 1] = isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 0, isoCfg.getIntValue(String.valueOf(i + 1))));
                            //fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 0, fieldsLength[i + 1])) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, fieldsLength[i + 1]));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < isoCfg.getIntValue(String.valueOf(i + 1))) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + "\n" + getArrayString(fields));
                            }
                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement, isoCfg.getIntValue(String.valueOf(i + 1)));

                        }
                    }
                }
            } else {
                //Xu ly khi ko co Secondary Bitmap
                for (int i = 1; i < 64; i++) {
                    if (Character.toString(strBitmapBinary.charAt(i)).equals("1")) {
                        if (isoCfg.getIntValue(String.valueOf( i + 1)) == 99) {

                            int lengthfield = Integer.parseInt(new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)));
                            fields[i + 1] = new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)) + isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 2, lengthfield));
                            //fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 2, lengthfield)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 2 + lengthfield));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 2 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement, 2 + lengthfield);
                        } else if (isoCfg.getIntValue(String.valueOf( i + 1)) == 999) {
                            int lengthfield = Integer.parseInt(new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)));
                            fields[i + 1] = new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)) + isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 3, lengthfield));
                            /*if (i == 47) {
                            
                            fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 5)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 5, lengthfield-2)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3 + lengthfield));
                            } else {
                            fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3)) + CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 3, lengthfield)) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, 3 + lengthfield));
                            }
                             */


                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 3 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement, 3 + lengthfield);
                        } else {
                            fields[i + 1] = isolib.getIsoAscii(i + 1, isoCfg, CommonLib.copyByteArrayFromArray(restDataElement, 0, isoCfg.getIntValue(String.valueOf(i + 1))));
                            //fields[i + 1] = isoCfg.checkBinaryField(i + 1) ? CommonLib.toHexString(CommonLib.copyByteArrayFromArray(restDataElement, 0, fieldsLength[i + 1])) : new String(CommonLib.copyByteArrayFromArray(restDataElement, 0, fieldsLength[i + 1]));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < isoCfg.getIntValue(String.valueOf( i + 1))) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + "\n" + getArrayString(fields));
                            }
                            restDataElement = CommonLib.copyByteArrayFromArray(restDataElement,isoCfg.getIntValue(String.valueOf( i + 1)));

                        }
                    }
                }
            }

            //Add by Minhdbh 10.11.2010
            if (restDataElement.length > 0) {
                throw new bicsexception("Message is not OK; still remain data after parsing!!!" + "\n" + getArrayString(fields));
            }
            return fields;
        } catch (Exception ex) {
            throw new bicsexception("Message is not OK; Error on parsing message " + ex.getMessage());
        }
    }

    /**
     * Ham chuyen doi tu Byte dang mot mang cua cac fields cua IST
     * Thanh phan dau vao gom ca MTI + Bitmap + Data Element
     * Khong bao gom do dai cua thong diep (4 byte)
     * @param value
     * @return
     */
    public static String[] ParseIsoMessageEBCDIC(byte[] value, cfgNode isoCfg) throws bicsexception {
        //Chu y: bao gom ca MTI + Bitmap + DataElement
        //do dai cua String tra ve phai la 129 do no bao gom ca MTI + Bitmap
        //vi tri 0 la MTI
        //vi tri 1 la Bitmap
        //Vi tri 2 la PAN
        //Cac vi tri sau tuong tu nhu ISO 8583
        String[] fields = new String[129];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = "";
        }
        try {
           // int[] fieldsLength = new int[129];

            //String strMessageIST = new String(value);
            String strPrimaryBitmap = "";
            String strBitmap = "";
            String strMTI = CommonLib.convertEBCDIC_to_StringASCII(CommonLib.copyByteArrayFromArray(value, 0, 4));
            //System.out.println("Gia tri cua MTI la : " + strMTI);

            String strBitmapBinary = "";
            String strPrimaryBitmapBinary = "";
            boolean b_haveSecondaryBitmap = false;
            String strDataElement = "";
            byte firstBitmap = value[4];
            if (Math.abs((int) firstBitmap >> 8) == 1) {
                b_haveSecondaryBitmap = true;
            }
            strPrimaryBitmap = CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 4, 8));


            byte[] bitmapRaw = new byte[8];
            System.arraycopy(value, 4, bitmapRaw, 0, 8);
            strPrimaryBitmap = CommonLib.toHexString(bitmapRaw);




            //System.out.println("Gia tri cua PrimaryBitmap cua IST la: " + strPrimaryBitmap);
            strPrimaryBitmapBinary = CommonLib.getBinaryBitmapFromHexa(strPrimaryBitmap);
            //System.out.println("Gia tri cua Primary Bitmap Binary la : " + strPrimaryBitmapBinary);
            if (!b_haveSecondaryBitmap) {
                strBitmap = strPrimaryBitmap;
                //System.out.println("Gia tri cua Bitmap la : " + strBitmap);
                strBitmapBinary = strPrimaryBitmapBinary;
                //System.out.println("Gia tri cua Bitmap Binary la : " + strBitmapBinary);
                strDataElement = CommonLib.convertEBCDIC_to_StringASCII(CommonLib.copyByteArrayFromArray(value, 12));
                //System.out.println("Gia tri cua Data Element la : " + strDataElement);
            } else if (Character.toString(strPrimaryBitmapBinary.charAt(0)).equals("1")) {
                strBitmap = CommonLib.toHexString(CommonLib.copyByteArrayFromArray(value, 4, 16));
                //System.out.println("Gia tri cua Bitmap la : " + strBitmap);
                b_haveSecondaryBitmap = true;
                strBitmapBinary = CommonLib.getBinaryBitmapFromHexa(strBitmap.substring(0, 16)) + CommonLib.getBinaryBitmapFromHexa(strBitmap.substring(16, 32));
                //System.out.println("Gia tri cua Bitmap Binary la : " + strBitmapBinary);
                strDataElement = CommonLib.convertEBCDIC_to_StringASCII(CommonLib.copyByteArrayFromArray(value, 20));
                //System.out.println("Gia tri cua Data Element la : " + strDataElement);
            }

           // fieldsLength = isoCfg.getFieldsLength();

            //Phan tich tung truong trong message
            fields[0] = strMTI;

            //Add by Minhdbh 10.11.2010
            //if (!strMTI.substring(0,1).equals("0")) throw new bicsexception("Message is not OK");

            fields[1] = strBitmap;

            String restDataElement = strDataElement;

            if (b_haveSecondaryBitmap == true) {
                //Xu ly khi co ca Secondary Bitmap
                for (int i = 1; i < 128; i++) {
                    if (Character.toString(strBitmapBinary.charAt(i)).equals("1")) {
                        if (isoCfg.getIntValue(String.valueOf(i + 1)) == 99) {
                            int lengthfield = Integer.parseInt(restDataElement.substring(0, 2));
                            fields[i + 1] = restDataElement.substring(0, 2 + lengthfield);

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 2 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(2 + lengthfield);
                        } else if (isoCfg.getIntValue(String.valueOf(i + 1)) == 999) {
                            int lengthfield = Integer.parseInt(restDataElement.substring(0, 3));
                            fields[i + 1] = restDataElement.substring(0, 3 + lengthfield);

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 3 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(3 + lengthfield);
                        } else {
                            fields[i + 1] = restDataElement.substring(0, isoCfg.getIntValue(String.valueOf(i + 1)));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < isoCfg.getIntValue(String.valueOf(i + 1))) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(isoCfg.getIntValue(String.valueOf(i + 1)));
                        }
                    }
                }
            } else {
                //Xu ly khi ko co Secondary Bitmap
                for (int i = 1; i < 64; i++) {
                    if (Character.toString(strBitmapBinary.charAt(i)).equals("1")) {
                        if (isoCfg.getIntValue(String.valueOf(i + 1)) == 99) {
                            int lengthfield = Integer.parseInt(restDataElement.substring(0, 2));
                            fields[i + 1] = restDataElement.substring(0, 2 + lengthfield);

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 2 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(2 + lengthfield);
                        } else if (isoCfg.getIntValue(String.valueOf(i + 1))== 999) {
                            int lengthfield = Integer.parseInt(restDataElement.substring(0, 3));
                            fields[i + 1] = restDataElement.substring(0, 3 + lengthfield);

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < 3 + lengthfield) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + " (LLLVAR)" + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(3 + lengthfield);
                        } else {
                            fields[i + 1] = restDataElement.substring(0, isoCfg.getIntValue(String.valueOf(i + 1)));

                            //Added by Minhdbh 10.11.2010
                            if (fields[i + 1].equals("") || fields[i + 1].length() < isoCfg.getIntValue(String.valueOf(i + 1))) {
                                throw new bicsexception("Message is not OK at " + String.valueOf(i + 1) + "\n" + getArrayString(fields));
                            }

                            restDataElement = restDataElement.substring(isoCfg.getIntValue(String.valueOf(i + 1)));
                        }
                    }
                }
            }
            //Add by Minhdbh 10.11.2010
            if (!restDataElement.equals("")) {
                throw new bicsexception("Message is not OK; still remain data after parsing!!!" + "\n" + getArrayString(fields));
            }
            return fields;
        } catch (Exception ex) {
            throw new bicsexception("Message is not OK; Error on parsing message " + ex.getMessage());
        }
    }

    public static String getArrayString(String[] value) {
        String fullMessagePrint = "";
        //fullMessagePrint+="\n\tFULL message: "+getAsciiMessage()+"\n";
        for (int i = 0; i < value.length; i++) {
            if (value[i].equals("") == false) {
                fullMessagePrint += "\n\t" + "F" + String.valueOf(i) + ": " + value[i];
            }
        }
        return fullMessagePrint;
    }
}
