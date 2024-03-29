package org.gaozou.roy.quick.dialog;

import org.gaozou.roy.quick.Quicker;
import org.gaozou.roy.quick.download.Download;
import org.gaozou.roy.quick.download.URLDetect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class AddDialog extends JDialog {
    private javax.swing.JSpinner threadsSpinner;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField saveAsField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField urlField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel statusTextLabel;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JButton addButton;

    /* Creates new form AddDialog */
    public AddDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initMyComponents();

        setBounds(100, 100, 500, 270);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        addButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        urlField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        statusTextLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        saveAsField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        getContentPane().setLayout(null);

        setTitle("Add file");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addButton.setText("Add");
        addButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        getRootPane().setDefaultButton( addButton );
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        getContentPane().add(addButton);
        addButton.setBounds(320, 210, 55, 25);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        getContentPane().add(cancelButton);
        cancelButton.setBounds(390, 210, 65, 25);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("URL:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 50, 40, 16);

        getContentPane().add(urlField);
        urlField.setBounds(100, 50, 360, 20);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText("Protocol:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 20, 60, 16);

        jComboBox1.setFont(new java.awt.Font("Dialog", 0, 12));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HTTP" }));
        getContentPane().add(jComboBox1);
        jComboBox1.setBounds(100, 20, 80, 20);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setText("Threads:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 90, 60, 16);

        statusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        statusLabel.setText("Status: ");
        statusLabel.setVisible( false );
        getContentPane().add(statusLabel);
        statusLabel.setBounds(30, 180, 41, 16);

        statusTextLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        statusTextLabel.setText(" ");
        getContentPane().add(statusTextLabel);
        statusTextLabel.setBounds(100, 180, 260, 16);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("Save As:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(30, 130, 60, 16);

        saveAsField.setEnabled(false);
        getContentPane().add(saveAsField);
        saveAsField.setBounds(100, 130, 280, 20);

        browseButton.setFont(new java.awt.Font("Dialog", 0, 12));
        browseButton.setText("Browse");
        browseButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        browseButton.setMaximumSize(new java.awt.Dimension(44, 20));
        browseButton.setMinimumSize(new java.awt.Dimension(44, 20));
        browseButton.setPreferredSize(new java.awt.Dimension(44, 20));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        getContentPane().add(browseButton);
        browseButton.setBounds(390, 130, 65, 25);

        pack();
    }//GEN-END:initComponents

    private void browseButtonActionPerformed(ActionEvent evt) {

        java.net.URL u = null;

        try {
            u = new java.net.URL((urlField.getText().startsWith("http://") ? "" : "http://") + urlField.getText());
        } catch (Exception e) {
        }

        String f = u!=null? u.getFile() : "";

        int idx = f.lastIndexOf('/');

        if ( idx>-1 )
            f = f.substring(idx+1);

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File(f));
        int r = fc.showSaveDialog(browseButton);

        if (r==fc.APPROVE_OPTION) {
            saveAsField.setText( fc.getSelectedFile().getAbsolutePath() );
        }
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        closeDialog(null);
    }

    private void addButtonActionPerformed(ActionEvent evt) {

        if ( urlField.getText().length()==0 || saveAsField.getText().length()==0 )
            return;

        try {
            statusLabel.setVisible( true );
            statusTextLabel.setText("Requesting remote file information...");

//            java.net.URL u = new java.net.URL((urlField.getText().startsWith("http://") ? "" : "http://") + urlField.getText());

            java.net.URL u = URLDetect.detect((urlField.getText().startsWith("http://") ? "" : "http://") + urlField.getText());

            Download d = new Download( u, new java.io.File(saveAsField.getText()) );
            d.setThreadNumber( ((javax.swing.SpinnerNumberModel) threadsSpinner.getModel()).getNumber().intValue() );

            ((Quicker) getOwner()).addDownload( d );

            statusTextLabel.setText("Done.");

            closeDialog(null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* Closes the dialog */
    private void closeDialog(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private void initMyComponents() {

        threadsSpinner = new javax.swing.JSpinner( new javax.swing.SpinnerNumberModel(1, 1, 50, 1) );
        getContentPane().add(threadsSpinner);
        threadsSpinner.setBounds(100, 90, 50, 20);
        threadsSpinner.setBorder(null);

        pack();
    }


    public static void main(String args[]) {
        new AddDialog(new javax.swing.JFrame(), true).setVisible(true);
    }

}
