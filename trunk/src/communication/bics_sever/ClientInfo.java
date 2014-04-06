/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package communication.bics_sever;
import java.net.Socket;

/**
 *
 * @author minhdbh
 */
public class ClientInfo
{
    public Socket mSocket = null;
    public bnvListener mClientListener = null;
    public bnvSender mClientSender = null;
    
    public void close()
    {
        try
        {
            mSocket.close();
        }
        catch (Exception ex)
        {
            
        }
    }

}