/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import cfg.cfgNode;
import cfg.cfgParser;
import cfg.nodeType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lib.CommonLib;

/**
 *
 * @author netone
 */
public class messagePatternLoader {

    String directoryPath;
    cfgParser[] cfgFiles;

    public static List<String> getFilesInFolder(String folderPath) {
        List<String> rs = new ArrayList<>();
        usFileFilter xmlFilter = new usFileFilter();
        for (final File fileEntry : new File(folderPath).listFiles(xmlFilter)) {
            if (!fileEntry.isDirectory()) {
                rs.add(fileEntry.getName());
            }
        }
        return rs;
    }

    public messagePatternLoader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public messagePatternLoader() {
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public void init() {
        List<String> files = getFilesInFolder(directoryPath);
        cfgFiles = new cfgParser[files.size()];
        for (int i = 0; i < files.size(); i++) {
            cfgFiles[i] = new cfgParser(directoryPath + "/" + files.get(i));
        }
    }

    public int size() {
        return cfgFiles != null ? cfgFiles.length : 0;
    }

    public cfgParser get(int index) {
        return cfgFiles != null ? cfgFiles[index] : null;
    }

    public List<cfgNode> getNodeNotInScope(String exceptNodeList, String scope) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {
            cfgNode header = cfgFiles[i].getXmlNode("header");
            if (header != null) {
                if (header.getValue("SCOPE").toUpperCase().equals(scope.toUpperCase())) {
                    rs.addAll(cfgFiles[i].getReverseNodeType(exceptNodeList));
                }
            }
        }
        return rs;
    }

    public List<cfgNode> getNodeInScope(nodeType nt, String scope) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {
            cfgNode header = cfgFiles[i].getXmlNode("header");
            if (header != null) {
                if (header.getValue("SCOPE").toUpperCase().equals(scope.toUpperCase())) {
                    rs.addAll(cfgFiles[i].getNodeType(nt));
                }
            }
        }
        return rs;
    }

    public List<cfgNode> getNodeByDes(nodeType nt, String pDes) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {
            for (cfgNode tmpnode : cfgFiles[i].getNodeType(nt)) {
                if (tmpnode.getNodeAtt("des").toUpperCase().equals(pDes.toUpperCase())) {
                    rs.addAll(cfgFiles[i].getNodeType(nt));
                }
            }

        }
        return rs;
    }

    public List<String> getAllExceptTransactionType(String exceptNodeList, String scope) {
        List<String> rs = new LinkedList<>();
        List<cfgNode> allNodes = getNodeNotInScope(exceptNodeList, scope);
        if (allNodes.size() > 0) {
            for (int i = 0; i < allNodes.size(); i++) {

                rs.add(allNodes.get(i).getNodeID() + "(" + allNodes.get(i).getFileName() + ")");

            }
        }
        return rs;
    }

    public List<String> getAllTransactionType(nodeType nt, String pInst, String scope, boolean isAllinScope) {
        List<String> rs = new LinkedList<>();
        List<cfgNode> allNodes = new LinkedList<>();
        Set<String> rsInSet=new LinkedHashSet();
        switch (nt) {
            case SCENARIO:
                allNodes = getNodeInScope(nt, scope);
                break;
            default:
                if (isAllinScope) {
                    allNodes = getNodeInScope(nt, scope);
                } else {
                    allNodes = getNodeByDes(nt, pInst);
                }
        }
        //
        if (allNodes.size() > 0) {
            for (int i = 0; i < allNodes.size(); i++) {

                //rs.add(allNodes.get(i).getNodeID() + "(" + allNodes.get(i).getFileName() + ")");
                rsInSet.add(allNodes.get(i).getNodeID() + "(" + allNodes.get(i).getFileName() + ")");

            }
        }
        rs.addAll(rsInSet);
        
        return rs;
    }

    public List<String> getAllCards(String scope) {
        //Use only for PARRTEN XML
        List<String> rs = new LinkedList<>();
        Set<String> rsInSet=new LinkedHashSet();
        for (int i = 0; i < size(); i++) {
            cfgNode header = cfgFiles[i].getXmlNode("header");
            if (header.getValue("SCOPE").toUpperCase().equals(scope.toUpperCase())) {
                String[] cardMarks = header.getValue("ACQ_CARDS").split(",");
                for (int icardmark = 0; icardmark < cardMarks.length; icardmark++) {
                    String[] cardlist = header.getValue(cardMarks[icardmark]).split(",");
                    rsInSet.addAll(Arrays.asList(cardlist));
                    //rs.addAll(Arrays.asList(cardlist));
                }
            }
        }
        rs.addAll(rsInSet);
        return rs;
    }
    
    private boolean checkValue(List<String> dataDic, String checkVal)
    {
        for(String testVal:dataDic)
        {
            if (testVal.toUpperCase().equals(checkVal.toUpperCase()))
            {
                return false;
            }
                    
        }
        return false;
    }

    public cfgNode getTempNode(String xmlFile, String nodeName) {
        for (int i = 0; i < size(); i++) {
            cfgParser testFile = cfgFiles[i];
            if (testFile.getFileName().toUpperCase().equals(xmlFile.toUpperCase())) {
                if (testFile.getXmlNode(nodeName) != null) {
                    return testFile.getXmlNode(nodeName);
                }
            }
        }
        return null;
    }

    public List<cfgNode> getCardData(String cardPan, String pinstCode) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {
            switch (cfgFiles[i].getXmlType()) {
                case PATTRENDATA:
                case CARD:
                    cfgNode header = cfgFiles[i].getXmlNode("header");

                    if (header != null) {
                        if (header.getValue("INSTITUTIONCODE").toUpperCase().equals(pinstCode.toUpperCase())) {
                            rs.add(cfgFiles[i].getXmlNode(cardPan));
                        }
                    }
                    break;
                case SCENARIO:
                    break;

                default:
            }

        }
        return rs;
    }

    public List<cfgNode> getNode(String pNode) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {

            rs.add(cfgFiles[i].getXmlNode(pNode));
        }
        return rs;
    }

    public List<cfgNode> getNodeByType(nodeType pType) {
        List<cfgNode> rs = new LinkedList<>();
        for (int i = 0; i < size(); i++) {

            rs.addAll(cfgFiles[i].getNodeType(pType));
        }
        return rs;
    }

    public cfgParser getFileParserByName(String fileName) {
        for (int i = 0; i < size(); i++) {
            cfgParser testFile = cfgFiles[i];
            if (testFile.getFileName().toUpperCase().equals(fileName.toUpperCase())) {
                return testFile;

            }
        }
        return null;
    }

    public void reloadCfg() {
        if (cfgFiles != null) {
            for (int i = 0; i < cfgFiles.length; i++) {
                cfgFiles[i].reLoadCFG();
            }
        }
    }

    public cfgParser[] toArray() {
        return cfgFiles;
    }

}
