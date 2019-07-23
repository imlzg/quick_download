package org.gaozou.roy.quick.event;

import org.gaozou.roy.quick.download.Work;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class DataWritingEvent implements java.io.Serializable {
    Work source = null;
    long pos = -1;
    int length = -1;

    /** Creates a new instance of DataWritingEvent */
    public DataWritingEvent(Work source, long pos, int length) {
        this.source = source;
        this.pos = pos;
        this.length = length;
    }

    public Work getSource() {
        return source;
    }

    public long getPosition() {
        return pos;
    }

    public int getLength() {
        return length;
    }
}