package org.gaozou.roy.quick;

import org.gaozou.roy.quick.dialog.AboutDialog;
import org.gaozou.roy.quick.dialog.AddDialog;
import org.gaozou.roy.quick.download.Download;
import org.gaozou.roy.quick.download.DownloadThread;
import org.gaozou.roy.quick.download.ThreadStatus;
import org.gaozou.roy.quick.event.DataWritingEvent;
import org.gaozou.roy.quick.event.DataWritingListener;
import org.gaozou.roy.quick.event.StatusEvent;
import org.gaozou.roy.quick.event.StatusListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class Quicker extends JFrame {
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuItem addMenuItem;
    private javax.swing.JMenuItem startMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane downloadPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem pauseMenuItem;
    private javax.swing.JLabel rFileLabel;
    private javax.swing.JMenu transferMenu;
    private javax.swing.JTable downloadTable;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lFileLabel;
    private javax.swing.JScrollPane threadPane;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem cancelMenuItem;
    private javax.swing.JTable threadTable;


    NumberFormat nf = NumberFormat.getInstance();

    /** Download status listener. It updates download progress and status
     *  in screen.
     */
    private class SL implements StatusListener {
        public void statusChanged(StatusEvent se) {
            Download dt = (Download) se.getSource();

            downloadTable.setValueAt( ThreadStatus.getStatus( se.getNewStatus() ), dt.getDownloadPriority(), 1);

            if ( se.getNewStatus() == StatusEvent.STOPPED || se.getNewStatus()==StatusEvent.COMPLETED )
                --running;

            if ( se.getNewStatus() == StatusEvent.COMPLETED )
                pauseMenuItem.setEnabled( false );
        }
    }

    /** Thread status listener. It updates thread progress and status
     *  in screen.
     */
    private class TSL implements StatusListener {
        public void statusChanged(StatusEvent se) {
            DownloadThread dt = (DownloadThread) se.getSource();

            if ( se.getNewStatus()==StatusEvent.ERROR ) {
                System.out.println("Restarting... "+dt.getName());
                try {
                    dt.getParentDownload().resumeThread( dt.getID() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            threadTable.setValueAt( ThreadStatus.getStatus( dt.getStatus() ), dt.getID(), 1);
        }
    }

    private class DWL implements DataWritingListener {
        public void dataWritten(DataWritingEvent dwe) {

            Download d = (Download) dwe.getSource();
            long b = d.getBytesCompleted();
            long s = d.getSize();

            downloadTable.setValueAt((int) (b*100/s), d.getDownloadPriority(), 1);
            downloadTable.setValueAt( b+"/"+s, d.getDownloadPriority(), 2);
        }
    }

    private class TDWL implements DataWritingListener {
        public void dataWritten(DataWritingEvent dwe) {

            DownloadThread dt = (DownloadThread) dwe.getSource();

            long tb = dt.getBytesCompleted();
            long ts = dt.getSize();

            threadTable.setValueAt((int) (tb*100/ts), dt.getID(), 1);
            threadTable.setValueAt( tb+"/"+ts, dt.getID(), 2);
        }
    }

    /*
     *  Download monitor that updates every second some status
     *  information, elapsed time and transfer rate in screen.
     */
    private class DownloadMonitor extends TimerTask {

        int c = 0;
        boolean run = true;

        public void run() {
            for (Download aDa : da) {
                if (aDa.getStatus() >= ThreadStatus.IN_PROGRESS.getID() && aDa.getStatus() != ThreadStatus.PAUSED.getID()) {
                    long b = aDa.getBytesCompleted();
                    //long s = aDa.getSize();

                    int t = aDa.getElapsedTime();
                    int h = t / 3600;
                    int m = (t - h * 3600) / 60;
                    int sc = t - h * 3600 - m * 60;

                    //if ( da[r].getStatus()==ThreadStatus.IN_PROGRESS.getID() )
                    //downloadTable.setValueAt( new Integer((int) (b*100/s)), da[r].getDownloadPriority(), 1);

                    //downloadTable.setValueAt( b+"/"+s, da[r].getDownloadPriority(), 2);
                    downloadTable.setValueAt((t == 0 ? "0" : nf.format(b / (t * 1000.0))) + " KB/s", aDa.getDownloadPriority(), 3);
                    downloadTable.setValueAt(h + ":" + m + ":" + sc, aDa.getDownloadPriority(), 4);
                }
            }
/*
            if ( tsi>-1 ) {
                DownloadThread[] dt = da[tsi].getThreads();

                for (int t=0; t<dt.length; ++t)
                    if ( dt[t].getStatus()>=ThreadStatus.IN_PROGRESS.getID() ) {
                        long tb = dt[t].getBytesCompleted();
                        long ts = dt[t].getSize();

                        if ( dt[t].getStatus()==ThreadStatus.IN_PROGRESS.getID() )
                            threadTable.setValueAt( new Integer((int) (tb*100/ts)), t, 1);

                        threadTable.setValueAt( tb+"/"+ts, t, 2);
                    }
            }
*/
        }
    }

    SL dsl = new SL();
    TSL tsl = new TSL();

    DWL dwl = new DWL();
    TDWL tdwl = new TDWL();

    DownloadMonitor dm = new DownloadMonitor();

    Download[] da = new Download[0];

    Timer t = new Timer();

    int running = 0;

    int lsi = -1, tsi = -1;

    /** Creates new form GDownloader */
    public Quicker() {
        initComponents();
        initMyComponents();

       setBounds(100, 100, 600, 400);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        toolBar = new javax.swing.JToolBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        downloadPane = new javax.swing.JScrollPane();
        downloadTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        threadPane = new javax.swing.JScrollPane();
        threadTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        rFileLabel = new javax.swing.JLabel();
        lFileLabel = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        transferMenu = new javax.swing.JMenu();
        startMenuItem = new javax.swing.JMenuItem();
        pauseMenuItem = new javax.swing.JMenuItem();
        cancelMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setTitle("Quick");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        toolBar.setFloatable(false);
        toolBar.setMaximumSize(new java.awt.Dimension(18, 30));
        toolBar.setMinimumSize(new java.awt.Dimension(18, 30));
        toolBar.setPreferredSize(new java.awt.Dimension(18, 30));
        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerSize(2);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(350, 354));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(400, 354));
        downloadPane.setBackground(new java.awt.Color(255, 255, 255));
        downloadPane.setMinimumSize(new java.awt.Dimension(350, 150));
        downloadPane.setPreferredSize(new java.awt.Dimension(400, 300));
        downloadPane.getViewport().setBackground( new java.awt.Color(255, 255, 255) );
        downloadTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        downloadTable.setIntercellSpacing(new java.awt.Dimension(0, 2));
        downloadTable.setShowGrid( false );
        downloadTable.setSelectionMode( javax.swing.ListSelectionModel.SINGLE_SELECTION );

        javax.swing.ListSelectionModel rowSM = downloadTable.getSelectionModel();
        rowSM.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                javax.swing.ListSelectionModel lsm =
                (javax.swing.ListSelectionModel) e.getSource();
                if ( lsm.isSelectionEmpty() )
                onDownloadSelectionChange(-1);
                else {
                    int idx = lsm.getMinSelectionIndex();

                    onDownloadSelectionChange(idx);
                }
            }
        });

        downloadPane.setViewportView(downloadTable);

        jSplitPane1.setLeftComponent(downloadPane);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(65534, 250));
        jPanel1.setMinimumSize(new java.awt.Dimension(350, 150));
        jPanel1.setPreferredSize(new java.awt.Dimension(350, 150));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel6.setMaximumSize(new java.awt.Dimension(250, 250));
        jPanel6.setMinimumSize(new java.awt.Dimension(250, 150));
        jPanel6.setPreferredSize(new java.awt.Dimension(250, 150));
        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("Thread Information:");
        jPanel6.add(jLabel4, java.awt.BorderLayout.NORTH);

        threadPane.setBackground(new java.awt.Color(255, 255, 255));
        threadPane.setMaximumSize(new java.awt.Dimension(32767, 250));
        threadPane.setMinimumSize(new java.awt.Dimension(300, 150));
        threadPane.setPreferredSize(new java.awt.Dimension(250, 150));
        threadPane.getViewport().setBackground( new java.awt.Color(255, 255, 255) );
        threadTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        threadTable.setIntercellSpacing(new java.awt.Dimension(0, 2));
        threadTable.setRowSelectionAllowed(false);
        threadTable.setShowGrid( false );
        threadPane.setViewportView(threadTable);

        jPanel6.add(threadPane, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel6);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 250));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 150));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 150));
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.GridLayout(3, 1));

        jPanel4.setMaximumSize(new java.awt.Dimension(90, 175));
        jPanel4.setMinimumSize(new java.awt.Dimension(90, 10));
        jPanel4.setPreferredSize(new java.awt.Dimension(90, 10));
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText(" Remote file:");
        jPanel4.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText(" Local file:");
        jPanel4.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setText(" Size:");
        jPanel4.add(jLabel3);

        jPanel3.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel5.setLayout(new java.awt.GridLayout(3, 1));

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 175));
        jPanel5.setMinimumSize(new java.awt.Dimension(150, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(150, 10));
        rFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        jPanel5.add(rFileLabel);

        lFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        jPanel5.add(lFileLabel);

        sizeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        jPanel5.add(sizeLabel);

        jPanel3.add(jPanel5, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("General", jPanel3);

        jPanel2.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel2);

        jSplitPane1.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        fileMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        addMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        addMenuItem.setText("Add...");
        addMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(addMenuItem);
        fileMenu.add(jSeparator1);
        exitMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        transferMenu.setText("Transfer");
        transferMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        startMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        startMenuItem.setText("Start");
        startMenuItem.setEnabled(false);
        startMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMenuItemActionPerformed(evt);
            }
        });

        transferMenu.add(startMenuItem);
        pauseMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        pauseMenuItem.setText("Pause");
        pauseMenuItem.setEnabled(false);
        pauseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseMenuItemActionPerformed(evt);
            }
        });

        transferMenu.add(pauseMenuItem);
        cancelMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelMenuItem.setText("Cancel");
        cancelMenuItem.setEnabled(false);
        transferMenu.add(cancelMenuItem);
        menuBar.add(transferMenu);
        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Dialog", 0, 12));
        aboutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        aboutMenuItem.setText("About...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        pack();
    }

    private void aboutMenuItemActionPerformed(ActionEvent evt) {
        new AboutDialog(this, true).setVisible(true);
    }

    private void pauseMenuItemActionPerformed(ActionEvent evt) {
        int idx = downloadTable.getSelectedRow();

        try {
            da[idx].pauseDownload();

            startMenuItem.setEnabled( true );
            pauseMenuItem.setEnabled( false );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMenuItemActionPerformed(ActionEvent evt) {
        int idx = downloadTable.getSelectedRow();

        try {
            if ( da[idx].getStatus()==ThreadStatus.STOPPED.getID() ) {
                new Thread(da[idx]).start();

                ++running;

                if ( running==1 )
                    t.scheduleAtFixedRate( new DownloadMonitor(), 0, 1000);
            }

            if ( da[idx].getStatus()==ThreadStatus.PAUSED.getID() ) {
                da[idx].resumeDownload();
            }

            startMenuItem.setEnabled( false );
            pauseMenuItem.setEnabled( true );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMenuItemActionPerformed(ActionEvent evt) {
        AddDialog ad = new AddDialog(this, true);
        ad.setVisible(true);
    }

    private void exitMenuItemActionPerformed(ActionEvent evt) {
        System.exit(0);
    }


    private void exitForm(WindowEvent evt) {
        System.exit(0);
    }

    private void initMyComponents() {
        resetDownloadsTable();
        resetThreadsTable();

        nf.setMaximumFractionDigits(2);
    }

    private void resetDownloadsTable() {
        DefaultTableModel dtm = new DefaultTableModel( new String[]{"Location", "Status", "Transfer", "KB/s", "Elapsed time"}, 0) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        downloadTable.setModel( dtm );

        downloadTable.getColumnModel().getColumn(0).setMinWidth(150);
        downloadTable.getColumnModel().getColumn(1).setMinWidth(100);
        downloadTable.getColumnModel().getColumn(1).setWidth(100);
        downloadTable.getColumnModel().getColumn(1).setCellRenderer(new ProgressBarCellRenderer());
        downloadTable.getColumnModel().getColumn(2).setMinWidth(60);
        downloadTable.getColumnModel().getColumn(2).setCellRenderer(new TextCellRenderer(javax.swing.JLabel.RIGHT));
        downloadTable.getColumnModel().getColumn(3).setMinWidth(80);
        downloadTable.getColumnModel().getColumn(3).setMaxWidth(80);
        downloadTable.getColumnModel().getColumn(3).setCellRenderer(new TextCellRenderer(javax.swing.JLabel.RIGHT));
        downloadTable.getColumnModel().getColumn(4).setMinWidth(100);
        downloadTable.getColumnModel().getColumn(4).setMaxWidth(100);
        downloadTable.getColumnModel().getColumn(4).setCellRenderer(new TextCellRenderer(javax.swing.JLabel.RIGHT));
    }

    private void resetThreadsTable() {
        DefaultTableModel dtm = new DefaultTableModel( new String[]{"Id", "Status", "Bytes"}, 0) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        threadTable.setModel(dtm);

        threadTable.getColumnModel().getColumn(0).setMinWidth(35);
        threadTable.getColumnModel().getColumn(0).setMaxWidth(35);
        threadTable.getColumnModel().getColumn(0).setCellRenderer(new TextCellRenderer(javax.swing.JLabel.CENTER));
        threadTable.getColumnModel().getColumn(1).setMinWidth(90);
        threadTable.getColumnModel().getColumn(1).setMaxWidth(90);
        threadTable.getColumnModel().getColumn(1).setCellRenderer(new ProgressBarCellRenderer());
        threadTable.getColumnModel().getColumn(2).setMinWidth(60);
        threadTable.getColumnModel().getColumn(2).setCellRenderer(new TextCellRenderer(javax.swing.JLabel.RIGHT));
    }

    private void loadDownloads() {

        resetDownloadsTable();

        javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel) downloadTable.getModel();

        for (int y=0; y<da.length; ++y) {
            da[y].setDownloadPriority(y);
            dtm.addRow(new String[]{da[y].getURL().toString(), ""+ThreadStatus.getStatus( da[y].getStatus() ), da[y].getBytesCompleted()+"/"+da[y].getSize(), ""});
        }

        downloadTable.setModel( dtm );
    }

    private void loadDownloadThreads(DownloadThread[] dt) {

        resetThreadsTable();

        javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel) threadTable.getModel();

        for (int y=0; y<dt.length; ++y) {
            dt[y].addStatusListener( tsl );
            dt[y].addDataWritingListener( tdwl );
            dtm.addRow(new String[] {""+y, ""+ThreadStatus.getStatus( dt[y].getStatus() ), dt[y].getBytesCompleted()+"/"+dt[y].getSize()});
        }

        threadTable.setModel( dtm );
    }

    public void addDownload(Download d) {

        Download[] dab = new Download[da.length+1];

        System.arraycopy(da, 0, dab, 0, da.length);

        d.addStatusListener( dsl );
        d.addDataWritingListener( dwl );
        d.setID( da.length );
        dab[dab.length-1] = d;

        da = dab;

        loadDownloads();
    }

    public void onDownloadSelectionChange(int idx) {

        lsi = tsi;
        tsi = idx;

        if ( idx<0 ) {
            startMenuItem.setEnabled( false );
            pauseMenuItem.setEnabled( false );
        }
        else {

            //Show download information

            rFileLabel.setText( da[idx].getURL().toString() );
            lFileLabel.setText( da[idx].getLocalFile().getAbsolutePath() );
            sizeLabel.setText( da[idx].getSize()+" bytes" );

            //Update status related stuff

            int s = da[idx].getStatus();

            switch (s) {
                case StatusEvent.PAUSED:
                case StatusEvent.STOPPED: {
                    startMenuItem.setEnabled( true );
                    pauseMenuItem.setEnabled( false );

                    break;
                }

                case StatusEvent.COMPLETED: {
                    startMenuItem.setEnabled( false );
                    pauseMenuItem.setEnabled( false );

                    break;
                }

                default: {
                    System.out.println(s);

                    startMenuItem.setEnabled( false );
                    pauseMenuItem.setEnabled( true );
                }
            }

            //Show thread information

            DownloadThread[] dt = da[idx].getThreads();

            loadDownloadThreads(dt);

            //Remove listeners
            if ( lsi != -1 ) {
                dt = da[lsi].getThreads();

                for (DownloadThread aDt : dt) {
                    aDt.removeStatusListener(tsl);
                    aDt.removeDataWritingListener(tdwl);
                }
            }
        }
    }

//    private Download findDownload(int id) {
//        for (Download aDa : da) {
//            if (aDa.getID() == id)
//                return aDa;
//        }
//
//        return null;
//    }


    public static void main(String args[]) {
        new Quicker().setVisible(true);
    }

}
