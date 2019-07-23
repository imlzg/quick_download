package org.gaozou.roy.quick.event;

import java.util.EventListener;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public interface StatusListener extends EventListener {
    void statusChanged(StatusEvent se);
}
