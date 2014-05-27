/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import datamanager.fieldParser;
import datamanager.fieldType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lib.CommonLib;

/**
 *
 * @author netone
 */
public class cfgNode {

    cfgType inCFGFileType = cfgType.SYSTEM;
    LinkedHashMap<String, String> nodeAtts;
    LinkedHashMap<String, String> nodeValues;
    String nodeID;
    String fullPathXML;
    nodeType nt = nodeType.NOT_DEFINE;

    public cfgNode() {
        nodeAtts = new LinkedHashMap<>();
        nodeValues = new LinkedHashMap<>();
    }

    public cfgType getCfgType() {
        return inCFGFileType;
    }

    public void setCfgType(cfgType nodeType) {
        this.inCFGFileType = nodeType;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public Map<String, String> getNodeAtts() {
        return nodeAtts;
    }

    public void setNodeAtts(LinkedHashMap<String, String> nodeAtts) {
        this.nodeAtts = nodeAtts;
    }

    public LinkedHashMap<String, String> getNodeValues() {
        return nodeValues;
    }

    public LinkedHashMap<String, String> getNodeValues(String filterPattern) {
        LinkedHashMap<String, String> nodeFilter = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : nodeValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String[] valueItems = value.split("::");
            if (valueItems.length > 1) {
                if (valueItems[0].toUpperCase().indexOf(filterPattern.toUpperCase()) == 0) {
                    nodeFilter.put(key, value);
                }
            }
            // do what you have to do here
            // In your case, an other loop.
        }
        return nodeFilter;
    }

    public LinkedHashMap<String, fieldParser> getFieldPatternFromNode() {
        LinkedHashMap<String, fieldParser> nodePattern = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : nodeValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            fieldParser valueItems = new fieldParser(value);
            nodePattern.put(key, valueItems);
            // do what you have to do here
            // In your case, an other loop.
        }
        return nodePattern;
    }

    public LinkedHashMap<String, fieldParser> getFieldPatternFromNode(Vector<fieldType> fieldFilter) {
        LinkedHashMap<String, fieldParser> nodePattern = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : nodeValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            fieldParser valueItems = new fieldParser(value);
            if (fieldFilter.contains(valueItems.getType())) {
                nodePattern.put(key, valueItems);
            }
            // do what you have to do here
            // In your case, an other loop.
        }
        return nodePattern;
    }

    public void setNodeValues(LinkedHashMap<String, String> nodeValues) {
        this.nodeValues = nodeValues;
    }
    
    public void setValue(String keyField, String value)
    {
        this.nodeValues.put(keyField.toUpperCase(), value);
    }
    

    public void addAttItem(String attKey, String attValues) {
        nodeAtts.put(attKey, attValues);
        if (attKey.toUpperCase().equals("TYPE")) {
            nt = nodeType.valueOf(attValues.toUpperCase());
        }
    }

    public void addValue(String aKey, String aValues) {
        nodeValues.put(aKey, aValues);
    }

    public String getNodeAtt(String keyAtrr) {
        if (nodeAtts != null) {
            return nodeAtts.get(keyAtrr);
        } else {
            return "";
        }
    }

    public boolean checkItem(String keyField) {
        return nodeValues == null ? false : nodeValues.containsKey(keyField.toUpperCase());
    }

    public boolean checkAtt(String keyAtt) {
        return nodeAtts == null ? false : nodeAtts.containsKey(keyAtt.toUpperCase());
    }

    /**
     * return value in HashMap object by Key
     *
     * @return
     */
    public String getValue(String orgName) {
        //OrgDetails oD=getOrgDetails(orgName);
        try {

            if (nodeValues.containsKey(orgName.toUpperCase())) {
                return nodeValues.get(orgName.toUpperCase());

            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }

    }

    public void changeItem(String keyField, String fieldValue) {
        if (checkItem(keyField)) {
            nodeValues.remove(keyField);
            nodeValues.put(keyField, fieldValue);
        }
    }

    /**
     * return value in HashMap object by Key
     *
     * @return
     */
    public int getIntValue(String orgName) {
        try {
            if (nodeValues.containsKey(orgName.toUpperCase())) {
                return Integer.parseInt(nodeValues.get(orgName.toUpperCase()));

            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public void clear() {
        if (nodeAtts != null) {
            nodeAtts.clear();
        }
        if (nodeValues != null) {
            nodeValues.clear();
        }

    }

    public List<String> getFieldKeys() {
        List<String> fieldKeys = new LinkedList<>();
        if (nodeValues != null) {
            fieldKeys.addAll(nodeValues.keySet());
        }
        return fieldKeys;
    }

    public void setCfgPath(String fullPath) {
        this.fullPathXML = fullPath;
    }

    public String getFileName() {
        String[] allPaths = fullPathXML.split("/");
        return allPaths[allPaths.length - 1];
    }

    public nodeType getNodeType() {
        return nt;
    }

    public int getSize() {
        return nodeValues == null ? 0 : nodeValues.size();
    }

    public boolean checkBinaryField(int pindexoffield) {
        if (!getValue("BINARY_FIELDS").equals("")) {
            String[] fieldsBinaryStr = getValue("BINARY_FIELDS").split(" ");
            int[] fieldsBinary = new int[fieldsBinaryStr.length];
            for (int iB = 0; iB < fieldsBinaryStr.length; iB++) {
                fieldsBinary[iB] = CommonLib.valueOf(fieldsBinaryStr[iB]);
            }
            for (int i = 0; i < fieldsBinary.length; i++) {
                if (fieldsBinary[i] == pindexoffield) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        String rs = "";
        rs = String.format("<%s %s> ", nodeID, nodeAtts.toString());
        for (Map.Entry<String, String> entry : nodeValues.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            rs += String.format("\n\r\t<%s>%s</>", key, value);
        }

        return rs;
    }
}
