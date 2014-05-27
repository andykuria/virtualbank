/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logs;

import iss.showLogEnum;
import lib.DateUtils;
import java.io.FileWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lib.CommonLib;

/**
 *
 * @author Administrator
 */
public class DataLogObject {

    private Queue objListValue;
    private String headerFileName = "";
    private String socketName = "";
    private String name = "";
    //fileName = headerFileName + "_" + socketName + "_" + name

    public DataLogObject() {
        objListValue = new ConcurrentLinkedQueue();
    }

    public DataLogObject(String headerFname, String interCode, String _name) {
        objListValue = new ConcurrentLinkedQueue();
        headerFileName = headerFname;
        socketName = interCode;
        name = _name;
    }

    public void setFileName(String headerFname, String interCode, String _name) {
        headerFileName = headerFname;
        socketName = interCode;
        name = _name;
    }

    public synchronized void addData(String value) {
        if (objListValue != null) {
            objListValue.add("[" + DateUtils.getCurrentDateTime() + "]" + " \t" + value + "\n");
        }

    }

    public synchronized void saveToDisk() {
        if (!objListValue.isEmpty()) {
            String filePath = LogUtils.getCurrentDirectory() + "/logs/" + headerFileName + "_" + socketName + "_" + name;
            LogUtils.createDirectory(LogUtils.getCurrentDirectory() + "/logs");

            try {
                FileWriter fwrite = new FileWriter(filePath, true);
                while (!objListValue.isEmpty()) {
                    fwrite.write(objListValue.peek().toString());
                    objListValue.remove();
                }
                fwrite.close();
            } catch (Exception ex) {
                CommonLib.PrintScreen(null, "Error in write file data to " + headerFileName + "_" + socketName + "_" + name + " on harddisk", showLogEnum.DETAILMODE);
            }
        }
    }

}
