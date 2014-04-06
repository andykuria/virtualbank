/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import cfg.cfgNode;
import unisim201401.systemLoader;

/**
 *
 * @author netone
 */
public class keyManager {

    public static String getInZPK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            switch (instCFG.getIntValue("TYPE")) {
                case 01://IST Switch v1.4 tyoe
                    return instCFG.getValue("ZPK");
                case 10: //IST Switch v1.5 tyoe
                    return instCFG.getValue("ZPK");
                case 02:  //UPI
                    return instCFG.getValue("ZPK");
                case 03: //KFTC
                    return instCFG.getValue("ZPK_IN");
            }
        }
        return rs;
    }

    public static String getOutZPK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            switch (instCFG.getIntValue("TYPE")) {
                case 01://IST Switch v1.4 tyoe
                    return instCFG.getValue("ZPK");
                case 10: //IST Switch v1.5 tyoe
                    return instCFG.getValue("ZPK");
                case 02:  //UPI
                    return instCFG.getValue("ZPK");
                case 03: //KFTC
                    return instCFG.getValue("ZPK_IN");
            }
        }
        return rs;
    }

    public static String getInMAK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            switch (instCFG.getIntValue("TYPE")) {
                case 01://IST Switch v1.4 tyoe
                    return instCFG.getValue("TAK");
                case 10: //IST Switch v1.5 tyoe
                    return instCFG.getValue("TAK");
                case 02:  //UPI
                    return instCFG.getValue("TAK");
                case 03: //KFTC
                    return instCFG.getValue("TAK_IN");
            }
        }
        return rs;
    }

    public static String getOutMAK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            switch (instCFG.getIntValue("TYPE")) {
                case 01://IST Switch v1.4 tyoe
                    return instCFG.getValue("TAK");
                case 10: //IST Switch v1.5 tyoe
                    return instCFG.getValue("TAK");
                case 02:  //UPI
                    return instCFG.getValue("TAK");
                case 03: //KFTC
                    return instCFG.getValue("TAK_IN");
            }
        }
        return rs;
    }

    public static String getZMK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            return instCFG.getValue("ZMK");
        } else {
            return "";
        }
    }

    public static String getZPK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            return instCFG.getValue("ZPK");
        } else {
            return "";
        }
    }
    
    public static String getTAK(String sZone, systemLoader systemGlobalInfo) {
        String rs = "";
        cfgNode instCFG = systemGlobalInfo.getInstitutionDataConfig(sZone);
        if (instCFG != null) {
            return instCFG.getValue("TAK");
        } else {
            return "";
        }
    }

    public static int getInstitutionType(systemLoader systemGlobalInfo, String instCode) {
        try {
            return systemGlobalInfo.getInstitutionDataConfig(instCode).getIntValue(instCode);
        } catch (Exception ex) {
            return 0;
        }

    }
}
