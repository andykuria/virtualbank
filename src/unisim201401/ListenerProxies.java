/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unisim201401;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author minhdbh
 */
public class ListenerProxies {

    private static final Class<?>[] INTERFACES = {ActionListener.class};

    public static ActionListener actionListener(final Object target,
            String method) {
        final Method proxied = method(target, method);
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                ActionEvent event = (ActionEvent) args[0];
                return proxied.invoke(target, event);
            }
        };
        return (ActionListener) Proxy.newProxyInstance(target.getClass()
                .getClassLoader(), INTERFACES, handler);
    }

    private static Method method(Object target, String method) {
        try {
            return target.getClass().getMethod(method, ActionEvent.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
