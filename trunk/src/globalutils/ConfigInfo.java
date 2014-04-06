/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package globalutils;

import iss.showLogEnum;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
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

/**
 *
 * @author Administrator
 */
public class ConfigInfo {
    //private String cfgName="";

    private HashMap<String, HashMap> paras;
    private String xmlFileCfg = "";

    public ConfigInfo() {
        //cfgName="GATEWAY-SYSTEM-config";
        paras = new HashMap<String, HashMap>();
    }

    public ConfigInfo(String istName) {
        //cfgName=istName.toUpperCase();
        paras = new HashMap<String, HashMap>();
        LoadCfgFromFile(xmlFileCfg);
    }

    private void LoadCfgFromFile(String xmlCfgFile) {
        xmlFileCfg = xmlCfgFile;
        if (paras != null) {
            paras.clear();
        }
        paras = new HashMap<String, HashMap>();
        try {
            File file = new File(xmlCfgFile);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            CommonLib.PrintScreen(null, "Read Configuration from XML File: Root element " + doc.getDocumentElement().getNodeName(), showLogEnum.DEFAULT);
            NodeList nodeLst = doc.getElementsByTagName("organization");
            CommonLib.PrintScreen(null, "Load information of all organization", showLogEnum.DEFAULT);
            Element elem = null;
            for (int s = 0; s < nodeLst.getLength(); s++) {
                //Node fstNode = nodeLst.item(s);
                elem = (Element) nodeLst.item(s);
                HashMap orgDetails = new HashMap();
                String orgName = "";
                orgName = elem.getAttribute("name").toUpperCase();

                NodeList filedParas = elem.getElementsByTagName("field");
                for (int j = 0; j < filedParas.getLength(); j++) {
                    Element fp = (Element) filedParas.item(j);
                    orgDetails.put(fp.getAttribute("name").toUpperCase(), fp.getTextContent());
                }
                paras.put(orgName, orgDetails);
                //return;

            }
        } catch (Exception e) {
            CommonLib.PrintScreen(null, "XML parsing error: " + xmlCfgFile, showLogEnum.DEFAULT);
            e.printStackTrace();
        }
    }

    public void ParseConfig(String xmlCfgFile) {
        xmlFileCfg = xmlCfgFile;
        LoadCfgFromFile(xmlCfgFile);
    }

    public void reLoadCFG() {
        LoadCfgFromFile(xmlFileCfg);
    }

    public OrgDetails getOrgDetails(String orgName) {
        OrgDetails rs;
        if (paras.containsKey(orgName.toUpperCase())) {
            rs = new OrgDetails(paras.get(orgName.toUpperCase()), orgName.toUpperCase());
            return rs;
        }
        return null;
    }

    public HashMap getOrg(String orgName) {

        if (paras.containsKey(orgName.toUpperCase())) {

            return paras.get(orgName.toUpperCase());
        }
        return null;
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

    /**
     * return value in HashMap object by Key
     * @return
     */
    public String getValue(String orgName, String key) {
        //OrgDetails oD=getOrgDetails(orgName);
        try {

            if (paras.containsKey(orgName.toUpperCase())) {
                HashMap<String, String> orgDetail = paras.get(orgName.toUpperCase());
                if (orgDetail.containsKey(key.toUpperCase())) {
                    return orgDetail.get(key.toUpperCase());
                }
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }
        return "";
    }

    /**
     * return value in HashMap object by Key
     * @return
     */
    public int getIntValue(String orgName, String key) {
        try {
            if (paras.containsKey(orgName.toUpperCase())) {
                HashMap<String, String> orgDetail = paras.get(orgName.toUpperCase());
                if (orgDetail.containsKey(key.toUpperCase())) {
                    return Integer.parseInt(orgDetail.get(key.toUpperCase()));
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
        return 0;
    }

    public void setValue(String orgName, String key, String value) {
        HashMap oD;
        if (paras.containsKey(orgName.toUpperCase())) {
            oD = paras.get(orgName.toUpperCase());
            if (oD.containsKey(key.toUpperCase())) {
                oD.remove(key.toUpperCase());
            }
            oD.put(key.toUpperCase(), value);
            saveOrg(orgName.toUpperCase(), oD);
        }
    }

    public void saveCfg() {

        saveCfg(xmlFileCfg);
    }

    public HashMap getThis() {
        return paras;
    }

    public void saveOrg(String orgName, HashMap od) {
        if (paras.containsKey(orgName)) {
            paras.remove(orgName);
        }
        paras.put(orgName, od);
    }

    public void saveOrg(OrgDetails oD) {
        if (paras.containsKey(oD.getName())) {
            paras.remove(oD.getName());
        }
        paras.put(oD.getName(), oD.getThis());
    }

    public HashMap[] toArray() {
        HashMap[] arrRs = new HashMap[paras.size()];
        int i = 0;
        Iterator itr = paras.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();

            arrRs[i] = new HashMap();
            arrRs[i] = (HashMap) entry.getValue();
            i++;
        }
        return arrRs;
    }

    public OrgDetails[] toOrgsArray() {
        OrgDetails[] arrRs = new OrgDetails[paras.size()];
        int i = 0;
        Iterator itr = paras.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();

            arrRs[i] = new OrgDetails((HashMap) entry.getValue(), (String) entry.getKey());
            i++;
        }
        return arrRs;
    }

    public OrgDetails[] toOrgsArray(String filterParas, String value) {
        filterParas = filterParas.toUpperCase();
        value = value.toUpperCase();
        OrgDetails[] arrRs = new OrgDetails[totalOrgByParas(filterParas, value)];
        int i = 0;
        Iterator itr = paras.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            if (((HashMap) entry.getValue()).containsKey(filterParas)) {
                if (((HashMap) entry.getValue()).get(filterParas).toString().toUpperCase().equals(value)) {
                    arrRs[i] = new OrgDetails((HashMap) entry.getValue(), (String) entry.getKey());
                    i++;
                }
            }

        }
        return arrRs;
    }

    private int totalOrgByParas(String filterParas, String value) {
        int totalorgs = 0;
        filterParas = filterParas.toUpperCase();
        value = value.toUpperCase();
        Iterator itr = paras.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();

            if (((HashMap) entry.getValue()).containsKey(filterParas)) {
                if (((HashMap) entry.getValue()).get(filterParas).toString().toUpperCase().equals(value)) {
                    totalorgs++;
                }
            }

        }

        return totalorgs;
    }

    public String getAllCfgInfo() {
        String result = "";
        try {
            result = String.format("\t----- %s -----\n\r", xmlFileCfg);
            Iterator itp = paras.keySet().iterator();
            while (itp.hasNext()) {
                String parentNode = (String) itp.next();
                result += String.format("\t<%s>\n\r", parentNode);
                HashMap childMMap = paras.get(parentNode);
                Iterator itc = childMMap.keySet().iterator();
                while (itc.hasNext()) {
                    String childNode = (String) itc.next();
                    result += String.format("\t\t<%s>%s</>\n\r", childNode, childMMap.get(childNode));
                }
                result += String.format("\t</%s>\n\r", parentNode);
            }
            result += "\t----- END CFG -----\n\r";
        } catch (Exception ex) {
        }
        return result;
    }
}
