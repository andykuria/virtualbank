/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import lib.CommonLib;
import lib.DateUtils;

/**
 *
 * @author netone
 */
public class fieldParser {

    String patternValue;
    String fieldValue;
    fieldType type;

    public fieldParser(String patternValue) {
        this.patternValue = patternValue;
        parsing();
    }

    public void setPatternValue(String patternValue) {
        this.patternValue = patternValue;
        parsing();
    }

    private void parsing() {
        String[] fieldParts = patternValue.split("::");
        if (fieldParts.length >= 1) {

            //ASCII table 'M' = 77 'N'=78
            int prefixCode = CommonLib.getStringCode(fieldParts[0]);
            int prefixType = 0;
            switch (prefixCode) {
                case 1213: //'MN' (77-65)*100+78-65
                    prefixType = CommonLib.getStringCode(fieldParts[1]);

                    switch (prefixType) {
                        case 813: //'IN'
                            type = fieldType.MANUAL;
                            fieldValue = fieldParts[2];
                            break;
                        case 71812: //'HSM'
                            type = fieldType.MANUAL_HSM;
                            fieldValue = fieldParts[2];
                            break;
                        case 18007: //'BIN'
                            type = fieldType.BIH;
                            break;
                    }

                    break;
                case 19: //'AT'
                    prefixType = CommonLib.getStringCode(fieldParts[1]);

                    switch (prefixType) {
                        case 112: //'BM'
                            type = fieldType.AUTO_BITMAP;
                            break;
                        case 150013://'PAN'
                            type = fieldType.AUTO_PAN;
                            break;
                        case 319://DT'
                            type = fieldType.AUTO_DATETIME;
                            break;
                        case 1917: //'TR'-trace
                            type = fieldType.AUTO_TRACE;
                            break;
                        case 19: //'T'-Time
                            type = fieldType.AUTO_TIME;
                            break;
                        case 3://'D'-Date
                            type = fieldType.AUTO_DATE;
                            break;
                        case 1902://'TC'
                            type = fieldType.AUTO_TRACK2;
                            break;
                        case 180416://'SEQ' - de37
                            type = fieldType.AUTO_SEQ37;
                            break;

                        case 1900: //'TA'
                            type = fieldType.TMP_ACC;
                            fieldValue = fieldParts[2];
                            break;
                        case 1702://RC
                            type = fieldType.AUTO_39;

                            break;
                        case 251510://ZPK
                            type = fieldType.AUTO_ZPK;
                            break;
                        case 71812: //'HSM'
                            type = fieldType.AUTO_HSM;
                            break;
                        case 12://AM
                            type = fieldType.AUTO_AMMOUNT;
                            fieldValue = fieldParts[2];
                            break;

                    }

                    break;
                case 1417://'OR'
                    type = fieldType.AUTO_ORIGINAL;

                    break;
                default:
                    type = fieldType.FIXXED_VALUE;
                    if (fieldParts[0].indexOf("[BIH]") >= 0) {
                        type = fieldType.FIXXED_VALUE_BIN;
                        fieldValue = fieldParts[0].substring(5);
                    }
                    fieldValue = fieldParts[0];

            }

        } else {
            type = fieldType.FIXXED_VALUE;
            fieldValue = fieldParts[0];

        }
    }

    public String getFieldValue() {
        String rs = "";
        switch (type) {
            case AUTO_DATE:
                rs = DateUtils.getCurrentDateIST();
                break;
            case AUTO_TRACE:
                rs = CommonLib.getSystemTrace();
                break;
            case AUTO_DATETIME:
                rs = DateUtils.getCurrentDateTime();
                break;
            case AUTO_TIME:
                rs = DateUtils.getTime();
                break;
            case AUTO_SEQ37:
                rs = CommonLib.getSystemTrace() + '0' + DateUtils.getCurrentDateIST();
                break;
            case MANUAL:
                rs = fieldValue;
                break;
            case MANUAL_HSM:
                rs = fieldValue;
                break;
            case FIXXED_VALUE:
            default:
                rs = fieldValue;

        }
        return rs;
    }

    public fieldType getType() {
        return type;
    }
}
