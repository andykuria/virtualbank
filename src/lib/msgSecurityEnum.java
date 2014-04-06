/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

/**
 *
 * @author minhdbh
 */
public enum msgSecurityEnum {

    READY,
    IN_NEED_GEN_PIN,
    IN_NEED_GEN_PIN_ZPK,
    IN_NEED_OF_PIN,
    IN_NEED_OF_MACVER,
    IN_NEED_OF_MACGEN,
    IN_NEED_OF_MACVER_MD5,
    IN_NEED_OF_MACGEN_MD5,
    NET_ZPK_GENERATE_ZMK,
    NET_ZPK_TRASLATE_ZMK_LMK,
    NET_TAK_GENERATE_ZMK,
    NET_TAK_TRANSLATE_LMK_ZMK,
    NET_TAK_TRANSLATE_ZMK_LMK,
    MAKE_RESPONSE

}
