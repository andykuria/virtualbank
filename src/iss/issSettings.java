/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iss;

/**
 *
 * @author minhdbh
 */
public class issSettings {
    IssValue rc;
    IssValue auth;
    private boolean requireMac=false;
    private boolean delayResponse=false;
    private int delayTime=0;

    public issSettings() {
        this.rc=new IssValue(issEnum.AUTO);
        this.auth=new IssValue(issEnum.NONE);
        requireMac=false;
        delayResponse=false;
        delayTime=0;
    }

    public IssValue getRc() {
        return rc;
    }

    public void setRc(IssValue rc) {
        this.rc = rc;
    }

    public IssValue getAuth() {
        return auth;
    }

    public void setAuth(IssValue auth) {
        this.auth = auth;
    }

    public boolean isRequireMac() {
        return requireMac;
    }

    public void setRequireMac(boolean requireMac) {
        this.requireMac = requireMac;
    }

    public boolean isDelayResponse() {
        return delayResponse;
    }

    public void setDelayResponse(boolean delayResponse) {
        this.delayResponse = delayResponse;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
    
    
}
