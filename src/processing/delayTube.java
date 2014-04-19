/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import iso8583.IsoMessage;
import iss.showLogEnum;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import lib.CommonLib;
import lib.DateTimeEnum;
import lib.DateUtils;

/**
 *
 * @author netone
 */
public class delayTube {

    SortedMap<Long, IsoMessage> delayMessage;
    Date baseTime;

    public delayTube() {
        delayMessage = new TreeMap();
        baseTime = DateUtils.getDate();
    }

    public void addMessage(IsoMessage msg, int delaytime) {
        long nextseq = 0;
        if (delayMessage.size() <= 0) {
            baseTime = DateUtils.getDate();
            nextseq = getSeqKey(delaytime);
        } else {
            nextseq = getSeqKey(delaytime + DateUtils.DateDiff(DateTimeEnum.MILISECOND, baseTime, DateUtils.getDate())); 
        }
        delayMessage.put(nextseq, msg);
        CommonLib.PrintScreen(null, String.format("Seq Tube ID: %d and msg= %s",nextseq, msg.getTraceInfo()), showLogEnum.DEFAULT);
    }

    private synchronized long getSeqKey(long delaytime) {
        long rs = delaytime;
        while (delayMessage.containsKey(rs)) {
            rs++;
        }

        return rs;
    }

    public List<IsoMessage> getMessages(Date peekTime) {
        List<IsoMessage> rs = new LinkedList();
        long timeGap = DateUtils.DateDiff(DateTimeEnum.MILISECOND, baseTime, peekTime);
      //  System.out.println(String.format("Delay tube process: basedtime = %s; peekTime=%s, gap=%d", DateUtils.getDateInFormat( baseTime,"mm:ss:SSS"),DateUtils.getDateInFormat( peekTime,"mm:ss:SSS"),timeGap ));
        long lowestTime = get1stKey();
        while ((lowestTime <= timeGap) && (lowestTime >= 0)) {
            IsoMessage timepassMsg = delayMessage.get(lowestTime);
            delayMessage.remove(lowestTime);
            rs.add(timepassMsg);
            lowestTime = get1stKey();
        }
        return rs;
    }

    private synchronized long get1stKey() {
        if (delayMessage.size() >= 1) {
            return delayMessage.firstKey();
        } else {
            return -1;
        }
    }

}
