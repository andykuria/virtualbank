/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import datamanager.fieldType;
import java.util.LinkedList;
import java.util.List;
import lib.CommonLib;
import lib.DateUtils;

/**
 *
 * @author minhdbh
 */
public class fieldPattern {

    String name;
    fieldType type;
    String fieldValue;
    List<String> subField;
    String nodeValue;

    public fieldPattern() {
        name = "";
        type = fieldType.UNKNOWN;
        fieldValue = "";
        nodeValue = "";
        subField = new LinkedList<>();
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public fieldType getType() {
        return type;
    }

    public void setType(fieldType type) {
        this.type = type;
    }

    public String getValue() {
        return fieldValue;
    }

    public void setValue(String value) {
        this.fieldValue = value;
    }

    private void parsing(String patternValue) {
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
                        case 12://AM
                            type = fieldType.AUTO_AMMOUNT;
                            break;
                        case 71812: //'HSM'
                            type = fieldType.AUTO_HSM;
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

}
