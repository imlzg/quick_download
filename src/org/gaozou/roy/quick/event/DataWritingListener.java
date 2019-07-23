package org.gaozou.roy.quick.event;

import java.util.EventListener;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public interface DataWritingListener extends EventListener {
    void dataWritten(DataWritingEvent dwe);
}
