/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfg;

import iss.showLogEnum;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lib.CommonLib;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import unisim201401.systemLoader;

/**
 *
 * @author minhdbh
 */
public class cfgParser {

    private HashMap<String, cfgNode> xmlNode;
    private cfgType xmlType;
    private String xmlFileCfg = "";
    private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }
    
    

    public cfgParser() {
        xmlNode = new HashMap<>();
    }

    public cfgParser(String xmlFilePath) {
        xmlNode = new HashMap<>();
        this.xmlFileCfg = xmlFilePath;
        LoadCfgFromFile(xmlFileCfg);
    }

    private void LoadCfgFromFile(String xmlCfgFile) {
        xmlFileCfg = xmlCfgFile;
        if (xmlNode != null) {
            xmlNode.clear();
        }

        try {
            File file = new File(xmlCfgFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            CommonLib.PrintScreen(systemGlobal, "Read Configuration from XML File: Root element " + doc.getDocumentElement().getNodeName(),showLogEnum.DETAILMODE);

            NodeList nodeLst = doc.getElementsByTagName("UNI_SIM");
            CommonLib.PrintScreen(systemGlobal, "Load information of root cfg " + xmlCfgFile, showLogEnum.DETAILMODE);

            Element elem = null;
            for (int s = 0; s < nodeLst.getLength(); s++) {
                elem = (Element) nodeLst.item(s);
                xmlType = cfgType.valueOf(elem.getAttribute("type").toUpperCase());
                switch (xmlType) {
                    case SYSTEM:
                    case BANK:
                    case CARD:
                    case PATTRENDATA:
                    case ISO8583FORMAT:
                    case ROUTING_TABLE:
                    case ISS_RESPONSE:
                    case SCENARIO:
                        NodeList orNode = elem.getElementsByTagName("organization");
                        CommonLib.PrintScreen(systemGlobal, "Load information of all organization " + xmlCfgFile, showLogEnum.DETAILMODE);
                        for (int iOr = 0; iOr < orNode.getLength(); iOr++) {
                            Element orElement = (Element) orNode.item(iOr);
                            cfgNode orInfo = pareNodefromElement(orElement);
                            orInfo.setNodeID(orInfo.getNodeAtt("name"));
                            orInfo.setCfgPath(xmlFileCfg);
                            xmlNode.put(orInfo.getNodeAtt("name"), orInfo);
                        }
                        break;
                }
            }

        } catch (Exception e) {
            CommonLib.PrintScreen(systemGlobal, "XML parsing error: " + xmlCfgFile, showLogEnum.DETAILMODE);
            e.printStackTrace();
        }
    }

    private cfgNode pareNodefromElement(Element e) {
        cfgNode rsParsing = new cfgNode();
        for (int iA = 0; iA < e.getAttributes().getLength(); iA++) {
            rsParsing.addAttItem(e.getAttributes().item(iA).getNodeName(), e.getAttributes().item(iA).getNodeValue());
        }

        NodeList fieldList = e.getElementsByTagName("field");
        for (int j = 0; j < fieldList.getLength(); j++) {
            Element fp = (Element) fieldList.item(j);
            rsParsing.addValue(fp.getAttribute("name").toUpperCase(), fp.getTextContent());
        }
        rsParsing.setCfgType(xmlType);
        return rsParsing;
    }

    public cfgType getXmlType() {
        return xmlType;
    }

    public void ParseConfig(String xmlCfgFile) {
        xmlFileCfg = xmlCfgFile;
        LoadCfgFromFile(xmlCfgFile);
    }

    public void reLoadCFG() {
        LoadCfgFromFile(xmlFileCfg);
    }

    public List<cfgNode> getReverseNodeType(String nodeTypeList) {
        List<cfgNode> rs = new ArrayList<>();
        String[] allKeyNode = (String[]) xmlNode.keySet().toArray(new String[0]);
        for (int i = 0; i < allKeyNode.length; i++) {
            if (nodeTypeList.toUpperCase().indexOf(String.valueOf(xmlNode.get(allKeyNode[i]).getNodeType() ) ) < 0) {
                rs.add(xmlNode.get(allKeyNode[i]));
            }
        }

        return rs;
    }
    
    public List<cfgNode> getNodeType(nodeType nt) {
        List<cfgNode> rs = new ArrayList<>();
        String[] allKeyNode = (String[]) xmlNode.keySet().toArray(new String[0]);
        for (int i = 0; i < allKeyNode.length; i++) {
            if (nt==xmlNode.get(allKeyNode[i]).getNodeType() )  {
                rs.add(xmlNode.get(allKeyNode[i]));
            }
        }

        return rs;
    } 

    public cfgNode getXmlNode(String orgName) {
        cfgNode rs;
        if (xmlNode.containsKey(orgName.toUpperCase())) {
            rs = xmlNode.get(orgName.toUpperCase());
            return rs;
        }
        return null;
    }

    public String getValue(String orgName, String key) {
        //OrgDetails oD=getOrgDetails(orgName);
        try {
            cfgNode anode = getXmlNode(orgName);
            if (anode != null) {
                return anode.getValue(key);
            } else {
                return "";
            }

        } catch (Exception ex) {
            return "";
        }

    }

    public int getIntValue(String orgName, String key) {
        //OrgDetails oD=getOrgDetails(orgName);
        try {
            cfgNode anode = getXmlNode(orgName);
            if (anode != null) {
                return anode.getIntValue(key);
            } else {
                return 0;
            }

        } catch (Exception ex) {
            return 0;
        }
    }

    public void setValue(String orgName, String key, String value) {
        cfgNode anode;
        if (xmlNode.containsKey(orgName.toUpperCase())) {
            anode = getXmlNode(orgName);
            if (anode.checkItem(key)) {
                anode.changeItem(key, value);
            }
            xmlNode.put(anode.getNodeID(), anode);

        }
    }

    public void saveCfg(String xmlCfgFile) {
        try {
            File file = new File(xmlCfgFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("organization");
            Element elem = null;
            for (int s = 0; s < nodeLst.getLength(); s++) {
                //Node fstNode = nodeLst.item(s);
                elem = (Element) nodeLst.item(s);

                NodeList filedParas = elem.getElementsByTagName("field");
                for (int j = 0; j < filedParas.getLength(); j++) {
                    Element fp = (Element) filedParas.item(j);
                    fp.setTextContent(getValue(elem.getAttribute("name").toUpperCase(), fp.getAttribute("name").toUpperCase()));
                }
            }

            createXMLFile(doc, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createXMLFile(Document doc, File file) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            // write to file
            // Create file if it does not exist
            boolean success = file.createNewFile();
            if (success) {
                // File did not exist and was created
                boolean append = false;
                FileWriter fw = new FileWriter(file, append);
                fw.write(xmlString);
                fw.close();
            } else {
                // File already exists

                boolean append = false;
                FileWriter fw = new FileWriter(file, append);
                fw.write(xmlString);//appends the string to the file
                fw.close();
            }

        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public List<String> getNodesKey() {
        List<String> nodeKeys = new ArrayList<>();
        if (xmlNode != null) {
            nodeKeys.addAll(xmlNode.keySet());
        }
        return nodeKeys;
    }

    public String getABsolutePath()
    {
        return xmlFileCfg;
    }
    public String getFileName() {
        String[] allPaths = xmlFileCfg.split("/");
        return allPaths[allPaths.length - 1];
    }

}
