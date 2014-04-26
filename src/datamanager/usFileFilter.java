/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author minhdbh
 */
public class usFileFilter implements FileFilter {

    private String[] fileExtensionsLst = new String[]{"xml"};

    public usFileFilter() {

    }

    public usFileFilter(String fileExtensionsStr) {
        fileExtensionsLst = fileExtensionsStr.split(",");
    }

    public usFileFilter(String[] fileExtensionsArray) {

        fileExtensionsLst = fileExtensionsArray;
    }

    @Override
    public boolean accept(File file) {
        for (String extension : fileExtensionsLst) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
