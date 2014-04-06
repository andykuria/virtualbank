/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptionshandle;

/**
 *
 * @author minhdbh
 */
public class bicsexception extends Exception {
    private String verrMessage="";
    private bicserrcode verrCode=bicserrcode.DEFAULT;

    public bicsexception()
    {
        super();
    }

    public bicsexception(String perrMsg)
    {
        super(perrMsg);
        verrMessage=perrMsg;
    }

    public bicsexception(String perrMsg,bicserrcode perrCode)
    {
        super(perrMsg);
        verrMessage=perrMsg;
        verrCode=perrCode;
    }

    public String getErrMessage()
    {
        return verrMessage;
    }
    
    public bicserrcode getErrCode()
    {
        return verrCode;
    }

    public String getNotify()
    {
        return "Error code: "+verrCode+"  \tMessage: "+verrMessage;
    }

}
