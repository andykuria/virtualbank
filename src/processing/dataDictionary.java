/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author minhdbh
 * @param <T>
 */
public class dataDictionary<T> {

    ConcurrentHashMap<Integer, T> dic;

    public dataDictionary() {
        dic = new ConcurrentHashMap<>();
    }

    public void add(Integer key, T newval) {
        dic.put(key, newval);
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

}
