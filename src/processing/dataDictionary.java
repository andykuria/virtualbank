/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import hsm.pinInfo;
import iso8583.IsoMessage;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author minhdbh
 * @param <T>
 */
public class dataDictionary<T> {

    LinkedHashMap<Integer, T> dic;

    public dataDictionary() {
        dic = new LinkedHashMap<>();
    }

    public void add(Integer key, T newval) {
        try {
            dic.put(key, newval);
        } catch (Exception ex) {

        }
    }

    public T get(Integer key) {
        return dic.get(key);
    }

    public T peek(Integer key) {
        T rs = dic.get(key);
        if (rs != null) {
            remove(key);
        }
        return rs;
    }

    public void remove(Integer key) {
        dic.remove(key);
    }

    public void replace(Integer key, T newval) {
        try {
            dic.remove(key);
        } catch (Exception ex) {

        }
        add(key, newval);

    }

    public Integer find1stKeyByValue(Integer hashValue) {
        Integer rs = 0;

        for (Map.Entry<Integer, T> entry : dic.entrySet()) {
            if (entry.getValue().hashCode() == hashValue) {
                return entry.getKey();
            }
        }
        return rs;

    }

    public List<String> getMsgInfo() {
        List<String> rs = new LinkedList<>();
        for (Map.Entry<Integer, T> entry : dic.entrySet()) {
            IsoMessage revMsg=(IsoMessage)entry.getValue();
            rs.add( entry.getKey()+"|" + revMsg.getDesInterfaceCode()+"|"+revMsg.getField(2)+"|"+revMsg.getField(11) +"|"+revMsg.getField(13));
            
        }
        return rs;
    }

}
