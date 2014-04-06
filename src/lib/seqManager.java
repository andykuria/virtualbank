/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.util.Random;
import sun.security.util.BigInt;

/**
 *
 * @author netone
 */
public class seqManager {

    private int seq6;
    private int seq12;

    public seqManager() {
        makeNewRand();

    }

    private void makeNewRand() {
        Random rand = new Random();
        seq6 = rand.nextInt(999999);
        seq12 = rand.nextInt(9999999);
    }

    public int getSeq6() {
        makeNewRand();
        return seq6;
    }

    public int getSeq12() {
        makeNewRand();
        return seq12;
    }

    public String getSeq6Str() {
        return CommonLib.formatIntToString(getSeq6(), 6);
    }

    public String getSeq11Str() {
        return CommonLib.formatIntToString(getSeq12(), 12);
    }

}
