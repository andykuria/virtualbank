/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import cfg.cfgNode;
import cfg.cfgParser;
import java.util.AbstractQueue;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;
import lib.msgSecurityEnum;
import lib.secObjInfo;

/**
 *
 * @author minhdbh
 */
public class routingtable {

    cfgParser cfgRoutingTable;

    public routingtable(cfgParser cfgRoutingTable) {
        this.cfgRoutingTable = cfgRoutingTable;
    }

    public String getRoutingByPort(String sZone) {
        if (cfgRoutingTable != null) {
            cfgNode routePort = cfgRoutingTable.getXmlNode("PORT");
            return routePort.getValue(sZone);
            /*List<String> instCode = cfgRoutingTable.getNodesKey();
             for (int iInst = 0; iInst < instCode.size(); iInst++) {
             cfgNode insNode = cfgRoutingTable.getXmlNode(instCode.get(iInst));
             List<String> zoneList = insNode.getFieldKeys();
             for (int iPan = 0; iPan < zoneList.size(); iPan++) {
             if (zoneList.get(iPan).toUpperCase().equals(sZone.toUpperCase())) {
             return instCode.get(iInst);
             }
             }
             }*/

        } else {
            return "";
        }

    }

    public String getRoutingByPan(String pan) {
        if (cfgRoutingTable != null) {
            cfgNode routePort = cfgRoutingTable.getXmlNode("PAN");
            List<String> panRouteList = routePort.getFieldKeys();
            for (int iPan = 0; iPan < panRouteList.size(); iPan++) {
                if (pan.indexOf(panRouteList.get(iPan).toUpperCase()) == 0) {
                    return routePort.getValue(panRouteList.get(iPan));
                }
            }

            /*List<String> instCode = cfgRoutingTable.getNodesKey();
             for (int iInst = 0; iInst < instCode.size(); iInst++) {
             cfgNode insNode = cfgRoutingTable.getXmlNode(instCode.get(iInst));
             List<String> zoneList = insNode.getFieldKeys();
             for (int iPan = 0; iPan < zoneList.size(); iPan++) {
             if (pan.indexOf(insNode.getValue(zoneList.get(iPan)).toUpperCase()) == 0) {
             return instCode.get(iInst);
             }
             }
             }*/
        } else {
            return "";
        }
        return "";
    }

    public Queue<secObjInfo> getSecurityQueue(String dZone) {
        Queue<secObjInfo> rs = new ConcurrentLinkedQueue<>();
        if (cfgRoutingTable != null) {
            cfgNode routingNode = cfgRoutingTable.getXmlNode(dZone);
            String[] secList = routingNode.getNodeAtts().get("secList").split(",");
            if (secList.length > 0) {
                for (int i = 0; i < secList.length; i++) {
                    secObjInfo newSecRequest = new secObjInfo(msgSecurityEnum.valueOf(secList[i]));
                    newSecRequest.setHsmCommnadID(CommonLib.getHSMCommandID());
                    newSecRequest.setdZone(dZone);
                    rs.add(newSecRequest);
                }
            }
        }
        return rs;
    }

    public static Queue<secObjInfo> getSecurityQueueFromStr(String strSecList) {
        Queue<secObjInfo> rs = new ConcurrentLinkedQueue<>();
        String[] secList = strSecList.split(",");
        if (secList.length > 0) {
            for (int i = 0; i < secList.length; i++) {
                secObjInfo newSecRequest = new secObjInfo(msgSecurityEnum.valueOf(secList[i]));
                newSecRequest.setHsmCommnadID(CommonLib.getHSMCommandID());

                rs.add(newSecRequest);
            }
        }

        return rs;
    }

    public static Queue<secObjInfo> getSecurityQueueFromStr(String strSecList, String dZone) {
        Queue<secObjInfo> rs = new ConcurrentLinkedQueue<>();
        String[] secList = strSecList.split(",");
        if (secList.length > 0) {
            for (int i = 0; i < secList.length; i++) {
                secObjInfo newSecRequest = new secObjInfo(msgSecurityEnum.valueOf(secList[i]));
                newSecRequest.setHsmCommnadID(CommonLib.getHSMCommandID());
                newSecRequest.setdZone(dZone);
                rs.add(newSecRequest);
            }
        }

        return rs;
    }
}
