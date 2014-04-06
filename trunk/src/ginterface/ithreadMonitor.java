/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;

import globalutils.threadTypeEnum;

/**
 *
 * @author minhdbh
 */
public interface ithreadMonitor {
    public threadTypeEnum getThreadType();
    public int getInstIndex();

    public void close();

    public boolean getStatus();


}
