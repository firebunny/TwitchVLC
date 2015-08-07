/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitchvlc;

import java.io.Serializable;

/**
 *
 * @author Greg
 */
public class SerialStream implements Serializable, Comparable {
    public String name;
    public boolean notify;
    public SerialStream(String na, boolean no) {
        name=na;
        notify=no;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof SerialStream) {
            return name.compareTo(((SerialStream)o).name);
        }
        return name.compareTo(o.toString());
    }
}
