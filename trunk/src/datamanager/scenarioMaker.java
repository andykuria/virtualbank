/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datamanager;

import cfg.cfgNode;
import cfg.cfgParser;
import iso8583.IsoMessage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author netone
 */
public class scenarioMaker {
    List<IsoMessage> listOfMsg=new LinkedList<>();
    cfgParser scenarioPattern;
    Map<Integer, String> pinValue=new ConcurrentHashMap<>();
    String desInstitution;
    
    public static IsoMessage genMsgFromParretn(cfgNode cfgPattern, scriptParser sP, String desInst) {
        IsoMessage rs=new IsoMessage();
        rs.setSourceInterfaceCode("SIMUI");
        rs.setDesInterfaceCode(desInst );
        
        return rs;
            
    }
}
