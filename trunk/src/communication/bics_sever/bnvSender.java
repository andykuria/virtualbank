/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.bics_sever;
/**
 *
 * @author minhdbh
 */

import globalutils.LineModeEnum;
import iss.showLogEnum;
import java.io.*;
import java.net.*;
import lib.CommonLib;
import unisim201401.systemLoader;

public class bnvSender
{
    //private Vector mMessageQueue = new Vector();
    //private ServerDispatcher mServerDispatcher;
    //private ClientInfo mClientInfo;
    private DataOutputStream mOut;
     private systemLoader systemGlobal;

    public void setSystemGlobal(systemLoader systemGlobal) {
        this.systemGlobal = systemGlobal;
    }
    public bnvSender(ClientInfo aClientInfo)//, ServerDispatcher aServerDispatcher)
    throws IOException
    {
       // mClientInfo = aClientInfo;
        //mServerDispatcher = aServerDispatcher;
        Socket socket = aClientInfo.mSocket;
        mOut = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Sends given message to the client's socket.
     */
    public synchronized void sendMessageToClient(byte[] aMessage)
    {
        try
        {
            mOut.write(aMessage);
            mOut.flush();
            //--GlobalObject.log_vnbc_sock_send.addData("SENT: "+CommonLib.getIntStringOfArray(aMessage));
            //--CommonLib.PrintScreen("ShowRawOutGoingVNBCMessage", "VNBC SENT (OK): "+CommonLib.getIntStringOfArray(aMessage));
        }
        catch (Exception ex)
        {
            //--GlobalObject.log_system_action.addData("VNBC Sent (NOT OK): "+CommonLib.getIntStringOfArray(aMessage));
            //* Warning Added */
            //--GlobalObject.warning_log_file.addData("VNBC\tVNBC\t01\tCouldn't send data to VNBC, Check the connection!");
            //--CommonLib.PrintScreen("ShowRawOutGoingVNBCMessage", "VNBC Sent (not OK): "+CommonLib.getIntStringOfArray(aMessage));
        }        
    }

    public synchronized void sendMessageToClient(byte[] aMessage,LineModeEnum pLineMode, String pIndentifyCode)
    {
        try
        {
            switch (pLineMode)
            {
                case ASCII:
                    mOut.write(aMessage);
                    mOut.flush();
                    CommonLib.PrintScreen(systemGlobal, pIndentifyCode+" send: ("+ String.valueOf(pLineMode)+") "+CommonLib.getHumanFormatFromByte(aMessage),showLogEnum.SIMPLEMODE);
                    break;
                case EBCDIC:
                    mOut.write(aMessage);
                    mOut.flush();
                    CommonLib.PrintScreen(systemGlobal, pIndentifyCode+" send: (HUMAN) "+CommonLib.asHex(aMessage),showLogEnum.SIMPLEMODE);
                    CommonLib.PrintScreen(systemGlobal, pIndentifyCode+" send: "+CommonLib.getIntStringOfArray(aMessage),showLogEnum.SIMPLEMODE);
                    //CommonLib.PrintScreen(pIndentifyCode+" send: ("+ String.valueOf(pLineMode)+") "+CommonLib.getHumanFormatFromByte(CommonLib.convertASCII_to_ByteEBCDIC(new String(aMessage))));
                    break;
                default:                        
                    mOut.write(aMessage);
                    mOut.flush();
                    CommonLib.PrintScreen(systemGlobal, pIndentifyCode+" send: (NULL MODE) "+CommonLib.getHumanFormatFromByte(aMessage),showLogEnum.SIMPLEMODE);
            }            
            mOut.flush();
            //--GlobalObject.log_vnbc_sock_send.addData("SENT: "+CommonLib.getIntStringOfArray(aMessage));
            //--CommonLib.PrintScreen("ShowRawOutGoingVNBCMessage", "VNBC SENT (OK): "+CommonLib.getIntStringOfArray(aMessage));
        }
        catch (Exception ex)
        {
            //--GlobalObject.log_system_action.addData("VNBC Sent (NOT OK): "+CommonLib.getIntStringOfArray(aMessage));
            //* Warning Added */
            //--GlobalObject.warning_log_file.addData("VNBC\tVNBC\t01\tCouldn't send data to VNBC, Check the connection!");
            //--CommonLib.PrintScreen("ShowRawOutGoingVNBCMessage", "VNBC Sent (not OK): "+CommonLib.getIntStringOfArray(aMessage));
        }
    }
    
}