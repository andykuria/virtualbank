/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hsm;

import ginterface.iqueryactionable;

/**
 *
 * @author minhdbh
 */
public class hsmCommand {
    private iqueryactionable institutionData;

    public iqueryactionable getInstitutionData() {
        return institutionData;
    }

    public void setInstitutionData(iqueryactionable pinstitutionData) {
        this.institutionData = pinstitutionData;
    }

    public hsmCommand()
    {

    }

    public String TranslatePIN(String msgID, String pinblk,String acc)
    {
        //return hsmLib.TranslatePIN(msgID, pinblk, acc,institutionData.getInstConfig().getValue(acc, acc) , pinblk)
        return"";
    }

}
