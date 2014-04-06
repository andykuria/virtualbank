/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iss;

/**
 *
 * @author minhdbh
 */
public class IssValue {
    issEnum type;
    String valueForFix="";

    public IssValue(issEnum type) {
        this.type = type;
    }

    public IssValue() {
        this.type= issEnum.AUTO;
        valueForFix="00";
    }

    public String getValueForFix() {
        return valueForFix;
    }

    
    public void setValueForFix(String valueForFix) {
        this.valueForFix = valueForFix;
    }

    public issEnum getType() {
        return type;
    }

    public void setType(issEnum type) {
        this.type = type;
    }
    
    
    
}
