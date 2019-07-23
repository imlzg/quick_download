package org.gaozou.roy.quick.event;

import org.gaozou.roy.quick.download.Work;

import java.io.Serializable;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class StatusEvent implements Serializable {
    public static final int ERROR = 0;
    public static final int STOPPED = 1;
    public static final int CONNECTING = 2;
    public static final int IN_PROGRESS = 5;
    public static final int PAUSED = 7;
    public static final int COMPLETED = 10;

    Work source = null;
    int oldStatus = STOPPED;
    int newStatus = STOPPED;

    /** Creates a new instance of StatusEvent */
    public StatusEvent(Work source, int oldStatus, int newStatus) {
        this.source = source;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Work getSource() {
        return source;
    }

    public int getOldStatus() {
        return oldStatus;
    }

    public int getNewStatus() {
        return newStatus;
    }
}