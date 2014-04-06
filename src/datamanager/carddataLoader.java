/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanager;

import cfg.cfgNode;
import java.util.List;

/**
 *
 * @author minhdbh
 */
public class carddataLoader {

    messagePatternLoader cards;
    String instCode;

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public void setCards(messagePatternLoader cards) {
        this.cards = cards;
    }

    public carddataLoader() {
    }

    public carddataLoader(messagePatternLoader cards) {
        this.cards = cards;
    }

    public cfgNode getCardInf(String pan) {
        List<cfgNode> cardfound = cards.getCardData(pan, instCode);
        if (cardfound.size() > 0) {
            return cardfound.get(0);
        }
        return null;
    }
}
