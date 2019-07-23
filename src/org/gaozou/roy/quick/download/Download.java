package org.gaozou.roy.quick.download;

import org.gaozou.roy.quick.event.DataWritingEvent;
import org.gaozou.roy.quick.event.DataWritingListener;
import org.gaozou.roy.quick.event.StatusEvent;
import org.gaozou.roy.quick.event.StatusListener;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class Download implements Work, Runnable{
    private class SL implements StatusListener {
        public void statusChanged(StatusEvent se) {
            DownloadThread dt = (DownloadThread) se.getSource();

            if ( se.getNewStatus()==se.COMPLETED )
                --remaining;

            if ( remaining==0 ) {
                if ( sl!=null)
                    sl.statusChanged( new StatusEvent( d, getStatus(), StatusEvent.COMPLETED ) );

                status = StatusEvent.COMPLETED;
            }
        }
    }

    private class DWL implements DataWritingListener {
        public void dataWritten(DataWritingEvent dwe) {
            completed += dwe.getLength();

            if ( dwl!=null )
                dwl.dataWritten( new DataWritingEvent(d, dwe.getPosition(), dwe.getLength()) );
        }
    }

    int id = -1, p = 0;

    int threads = 1, remaining = 0;

    URL u = null;
    File f = null;
    DownloadThread[] dt = new DownloadThread[0];
    Download d = this;

    Range[] range = new Range[0];

    long size = 0, completed = 0;

    long startTime = 0, endTime = 0;

    int status = StatusEvent.STOPPED;

    StatusListener sl = null;
    SL tsl = new SL();
    DWL tdwl = new DWL();

    DataWritingListener dwl = null;

    /** Creates a new instance of DownloadThreadManager */
    public Download(URL u, File f) {
        this.u = u;
        this.f = f;

        try {
            URLConnection uc = u.openConnection();
            uc.connect();
            size = uc.getContentLength();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        return id;
    }

    public int getDownloadPriority() {
        return p;
    }

    public int getElapsedTime() {
        if ( status<ThreadStatus.COMPLETED.getID() )
            endTime = System.currentTimeMillis();

        return (int) (endTime-startTime)/1000;
    }

    public URL getURL() {
        return u;
    }

    public File getLocalFile() {
        return f;
    }

    public long getSize() {
        return size;
    }

    public long getBytesCompleted() {
        return completed;
    }

    public int getThreadNumber() {
        return threads;
    }

    public DownloadThread[] getThreads() {
        return dt;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setDownloadPriority(int p) {
        this.p = p;
    }

    public void setThreadNumber(int threads) {

        if ( threads>0 ) {
            this.threads = threads;

            prepareThreads();
        }
    }

    public int getStatus() {
        return status;
    }

    public void addStatusListener(StatusListener sl) {
        this.sl = sl;
    }

    public void addDataWritingListener(DataWritingListener dwl) {
        this.dwl = dwl;
    }

    public void run() {

        try {
            download();
        }
        catch (Exception e) {

//MANEJAR LA EXCEPTION
            e.printStackTrace();
        }
    }

    private void prepareThreads() {

        long fl = size/threads;

        range = new Range[threads];
        dt = new DownloadThread[threads];

        try {
            for (int x=0; x<threads; ++x) {
                if ( x==threads-1 )
                    range[x] = new Range( x*fl, size );
                else
                    range[x] = new Range( x*fl, x*fl+fl-1 );

                dt[x] = new DownloadThread(this, f, range[x]);
                dt[x].setID(x);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartThread(int id) throws Exception {

        if ( id>=threads || id<0 )
            return;

        completed -= dt[id].getBytesCompleted();
        StatusListener[] sla = dt[id].getStatusListeners();
        DataWritingListener[] dwla = dt[id].getDataWritingListeners();

        dt[id] = null;

        dt[id] = new DownloadThread(this, f, range[id]);
        dt[id].setID( id );

        for (int z=0; z<sla.length; ++z)
            dt[id].addStatusListener( sla[z] );
        for (int z=0; z<sla.length; ++z)
            dt[id].addDataWritingListener( dwla[z] );
        new Thread(dt[id]).start();
    }

    public void resumeThread(int id) throws Exception {

        //Check if it's a valid thread id
        if ( id>=threads || id<0 )
            return;

        long c = dt[id].getBytesCompleted();
        StatusListener[] sla = dt[id].getStatusListeners();
        DataWritingListener[] dwla = dt[id].getDataWritingListeners();

        //Bytes to rollback
        int rollback = 10;

        //Check if completed bytes is less than the number of bytes to rollback
        if ( c<rollback )
            rollback = (int) c;

        completed -= rollback;
//System.out.println("[Thread "+id+"] Old Range: "+range[id]);
//System.out.println("[Thread "+id+"] Completed: "+c);
        range[id] = range[id].getSubRange(range[id].getStart()+c-rollback);
//System.out.println("[Thread "+id+"] New Range: "+range[id]);
        //Destroy thread
        dt[id] = null;

        dt[id] = new DownloadThread(this, f, range[id]);
        dt[id].setID( id );

        for (int z=0; z<sla.length; ++z)
            dt[id].addStatusListener( sla[z] );
        for (int z=0; z<sla.length; ++z)
            dt[id].addDataWritingListener( dwla[z] );
        new Thread(dt[id]).start();
    }

    public void download() throws Exception {

        startTime = System.currentTimeMillis();

        status = StatusEvent.CONNECTING;

        if ( sl!=null)
            sl.statusChanged( new StatusEvent(this, StatusEvent.STOPPED, StatusEvent.CONNECTING ) );

        if ( size>-1 ) {
            status = StatusEvent.IN_PROGRESS;

            if ( sl!=null)
                sl.statusChanged( new StatusEvent(this, StatusEvent.CONNECTING, StatusEvent.IN_PROGRESS ) );

            for (int x=0; x<threads; ++x) {
                dt[x].addStatusListener( tsl );
                dt[x].addDataWritingListener( tdwl );
                new Thread(dt[x]).start();
                ++remaining;
            }
        }


//System.out.println("Process took: "+((System.currentTimeMillis()-ini)/1000)+" secs");
    }

    public void pauseDownload() {
        for (int x=0; x<threads; ++x)
            dt[x].stopDownload();

        if ( sl!=null)
            sl.statusChanged( new StatusEvent( d, getStatus(), ThreadStatus.PAUSED.getID() ) );

        status = ThreadStatus.PAUSED.getID();
    }

    public void resumeDownload() throws Exception {
        for (int x=0; x<threads; ++x)
            if ( dt[x].getStatus()!= ThreadStatus.COMPLETED.getID() )
                resumeThread( x );

        if ( sl!=null)
            sl.statusChanged( new StatusEvent( d, getStatus(), ThreadStatus.RESUMING.getID() ) );

        status = ThreadStatus.RESUMING.getID();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //Download d = new Download(new URL("http://unc.dl.sourceforge.net/sourceforge/gaim/gaim-0.61-1.i386.rpm"));
        //Download d = new Download(new URL("http://www.miatech.net/swf/intro.swf"));
        Download d = new Download(new URL("http://unc.dl.sourceforge.net/sourceforge/jboss/JBoss-2.4.11.zip"), new File("JBoss-2.4.11.zip"));
        d.setThreadNumber(20);
        //d.download();
    }

}
