/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iso8583;

/**
 *
 * @author minhdbh
 */
public enum IsoMessageType {

    DEFAULT,
    SYSTEM_SIM_SEND,
    REQUEST,
    RESPONSE,
    NETWORK_REQUEST,
    NETWORK_RESPONSE,
    AUTO_REPLY,
    DROP

}
