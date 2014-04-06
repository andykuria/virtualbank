/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ginterface;



/**
 *
 * @author Administrator
 */
public interface ihsmcmd {
    public void TranslatePIN(String msgID, String pinblk,String acc,String acq_zpk,String iss_zpk);
    public void TranslateZPK_ZMK2ZPK_LMK(String msgID,String zmk_lmk,String zpk_zmk);
    public void GenerateZPK(String msgID,String zmk_lmk);
    public void GenerateTAK(String msgID, String tmk_lmk);
    public void TranslateTAK_LMK2TAK_ZMK(String msgID, String zmk_lmk, String tak_lmk);
    public void TranslateTAK_ZMK2TAK_LMK(String msgID, String zmk_lmk, String tak_zmk);
    public void GenerateMAC(String msgID, String tak_lmk, String dataLen, String data);
    public void VerifyMAC(String msgID, String tak_lmk, String mac, String dataLen, String data);    
}
