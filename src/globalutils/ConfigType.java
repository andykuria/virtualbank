/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package globalutils;

/**
 *
 * @author minhdbh
 */
public class ConfigType {
    private String configKey;
    private Object configValue;

    public ConfigType()
    {
        configKey="";
    }

    public String getConfigKey() {
        return configKey;
    }

    public Object getConfigValue() {
        return configValue;
    }

    public void setConfigKey(String pconfigKey) {
        this.configKey = pconfigKey;
    }

    public void setConfigValue(Object pconfigValue) {
        this.configValue = pconfigValue;
    }


}
