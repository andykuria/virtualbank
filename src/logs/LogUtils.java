/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package logs;

import java.io.File;

/**
 *
 * @author Administrator
 */
public class LogUtils {

    public static  String getCurrentDirectory()
    {
        return System.getProperty("user.dir");
    }

    public static boolean isFileExist(String filePath)
    {
        java.io.File fileToCheck = new File(filePath);
        return fileToCheck.exists();
    }

    public static boolean isDirectory(String path)
    {
        java.io.File pathToCheck = new File(path);
        return pathToCheck.isDirectory();
    }

    public static void createDirectory(String path)
    {
        if (!isFileExist(path))
        {
            if (!isDirectory(path))
            {
                new File(path).mkdir();
            }
        }
    }

}
