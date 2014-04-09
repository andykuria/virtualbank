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
import java.util.LinkedList;
import java.util.List;
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
        for (final File fileEntry : new File(folderPath).listFiles()) {
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
        List<cfgNode> rs = new ArrayList<>();
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
        List<cfgNode> rs = new ArrayList<>();
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

    public List<String> getAllExceptTransactionType(String exceptNodeList, String scope) {
        List<String> rs = new ArrayList<>();
        List<cfgNode> allNodes = getNodeNotInScope(exceptNodeList, scope);
        if (allNodes.size() > 0) {
            for (int i = 0; i < allNodes.size(); i++) {

                rs.add(allNodes.get(i).getNodeID() + "(" + allNodes.get(i).getFileName() + ")");

            }
        }
        return rs;
    }

    public List<String> getAllTransactionType(nodeType nt, String scope) {
        List<String> rs = new ArrayList<>();
        List<cfgNode> allNodes = getNodeInScope(nt, scope);
        if (allNodes.size() > 0) {
            for (int i = 0; i < allNodes.size(); i++) {

                rs.add(allNodes.get(i).getNodeID() + "(" + allNodes.get(i).getFileName() + ")");

            }
        }
        return rs;
    }

    public List<String> getAllCards(String scope) {
        //Use only for PARRTEN XML
        List<String> rs = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            cfgNode header = cfgFiles[i].getXmlNode("header");
            if (header.getValue("SCOPE").toUpperCase().equals(scope.toUpperCase())) {
                String[] cardMarks = header.getValue("ACQ_CARDS").split(",");
                for (int icardmark = 0; icardmark < cardMarks.length; icardmark++) {
                    String[] cardlist = header.getValue(cardMarks[icardmark]).split(",");
                    rs.addAll(Arrays.asList(cardlist));
                }
            }
        }
        return rs;
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
        List<cfgNode> rs = new ArrayList<>();
        for (int i = 0; i < size(); i++) {

            rs.add(cfgFiles[i].getXmlNode(pNode));
        }
        return rs;
    }

    public List<cfgNode> getNodeByType(nodeType pType) {
        List<cfgNode> rs = new ArrayList<>();
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
}
