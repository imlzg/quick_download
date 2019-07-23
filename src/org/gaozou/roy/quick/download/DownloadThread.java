package org.gaozou.roy.quick.download;

import org.gaozou.roy.quick.event.DataWritingEvent;
import org.gaozou.roy.quick.event.DataWritingListener;
import org.gaozou.roy.quick.event.StatusEvent;
import org.gaozou.roy.quick.event.StatusListener;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Author: george
 * Powered by GaoZou group.
 */
@SuppressWarnings("unchecked")
public class DownloadThread implements Work, Runnable{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Download d = null;

    URLConnection uc = null;
    RandomAccessFile raf = null;

    int id = -1;

    long from = 0;
    long to = 0;

    int c = 0;

    int status = StatusEvent.STOPPED;
    int lastStatus = ThreadStatus.STOPPED.getID();

    HashMap sl = new HashMap();
    HashMap dwl = new HashMap();

    long size = 0, completed = 0;

    boolean run = true;

    /* Creates a new instance of DownloadThread */
    public DownloadThread(Download d, File f) throws Exception {
        this(d, f, -1, -1);
    }

    public DownloadThread(Download d, File f, Range r) throws Exception {
        this(d, f, r.getStart(), r.getEnd());
    }

    public DownloadThread(Download d, File f, long from, long to) throws Exception {
        this.d = d;
        uc = d.getURL().openConnection();

        this.from = from;
        this.to = to;

        if ( from>=0 && to>=0 ) {
            if ( from>to ) {
                long x = from;

                from = to;
                to = x;
            }

            uc.setRequestProperty("Range", "bytes="+from+"-"+to);
            size = to-from+1;
        }

        raf = new FileManager().getFileFor( f );
    }

    public void addStatusListener(StatusListener sl) {
        this.sl.put( sl.toString(), sl );
    }

    public StatusListener[] getStatusListeners() {
//        return (StatusListener[]) sl.values().toArray(new StatusListener[0]);
        return (StatusListener[]) sl.values().toArray(new StatusListener[sl.values().size()]);
    }

    public void removeStatusListener(StatusListener sl) {
        this.sl.remove( sl.toString() );
    }

    public void addDataWritingListener(DataWritingListener dwl) {
        this.dwl.put( dwl.toString(), dwl);
    }

    public DataWritingListener[] getDataWritingListeners() {
//        return (DataWritingListener[]) dwl.values().toArray(new DataWritingListener[0]);
        return (DataWritingListener[]) dwl.values().toArray(new DataWritingListener[dwl.values().size()]);
    }

    public void removeDataWritingListener(DataWritingListener dwl) {
        this.dwl.remove( dwl.toString() );
    }

    public int getID() {
        return id;
    }

    public Download getParentDownload() {
        return d;
    }

    public long getSize() {
        return size;
    }

    public long getBytesCompleted() {
        return completed;
    }

    public int getStatus() {
        return status;
    }

    public void setID(int id) {
        this.id = id;

        setName("Thread "+id);
    }

    public void run() {

        InputStream in = null;

        try {
            run = true;

            changeStatusTo( ThreadStatus.CONNECTING );

            System.out.println("["+getName()+"] Connecting...");

            uc.connect();

            if  ( status==ThreadStatus.STOPPED.getID() )
                return;

            if ( from>-1 )
                raf.seek( from );

            size = uc.getContentLength();

            //int read = 1, readsf = 0;

            changeStatusTo( ThreadStatus.IN_PROGRESS );

            System.out.println("["+getName()+"] Downloading...");

            in = uc.getInputStream();

            int i;

            byte[] b = new byte[1024];

            //long s = System.currentTimeMillis();

            while ( (i=in.read(b))>-1 && run ) {
                raf.write(b, 0, i );

                completed += i;

                if ( dwl!=null)
                    notifyDataWriting( new DataWritingEvent(this, raf.getFilePointer(), i ) );
            }

            if ( !run )
                changeStatusTo( ThreadStatus.STOPPED );
            else {
                if ( completed<size )
                    throw new Exception("["+getName()+"] Connection lost. Incomplete download");

                changeStatusTo( ThreadStatus.COMPLETED );
            }
//System.out.println("["+getName()+"] Finishing at "+System.currentTimeMillis());
//System.out.println("Took "+((System.currentTimeMillis()-s)/1000)+" secs");

        } catch(Exception e) {
            e.printStackTrace( System.out );

            changeStatusTo( ThreadStatus.ERROR );
        } finally {
            try {
                if ( in!=null )
                    in.close();
            } catch (Exception e) {
            }

            try {
                if ( raf!=null )
                    raf.close();
            } catch (Exception e) {
            }
        }
    }

    public void stopDownload() {
        run = false;
    }

    public int getPercentageComplete() {
        return (int) ((long)(c*100)/(long)(to-from+1));
    }

    private void changeStatusTo(ThreadStatus ts) {

        lastStatus = getStatus();
        status = ts.getID();

        notifyStatusChange( new StatusEvent(this, lastStatus, status) );
    }

    private void notifyStatusChange(StatusEvent se) {
//        StatusListener[] sla = (StatusListener[]) sl.values().toArray(new StatusListener[0]);
        StatusListener[] sla = (StatusListener[]) sl.values().toArray(new StatusListener[sl.values().size()]);

        for (StatusListener aSla : sla) aSla.statusChanged(se);
    }

    private void notifyDataWriting(DataWritingEvent dwe) {
//        DataWritingListener[] dwla = (DataWritingListener[]) dwl.values().toArray(new DataWritingListener[0]);
        DataWritingListener[] dwla = (DataWritingListener[]) dwl.values().toArray(new DataWritingListener[dwl.values().size()]);

        for (DataWritingListener aDwla : dwla) aDwla.dataWritten(dwe);
    }


//    public static void main(String[] args) throws Exception{
//        DownloadThread dt = new DownloadThread(new Download(new URL("http://www.miatech.net/swf/intro.swf"), new File("intro.swf")), new File("intro.swf"), 0, 9200);
//        new Thread(dt).start();
//    }

}
