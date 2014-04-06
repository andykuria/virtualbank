/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ginterface;

import cfg.cfgNode;

import globalutils.ConnectionMode;
import globalutils.LineModeEnum;


import iso8583.HeaderProcessing;

/**
 *
 * @author minhdbh
 */
public interface iqueryactionable {

    public cfgNode getInstConfig();

    public void setInstConfig(cfgNode pinstConfig);

    public HeaderProcessing getHeaderInfo();

    public LineModeEnum getLineMode();

    public ConnectionMode getConnMode();

    public String getInsCode();

    public String getInsScope();
}
