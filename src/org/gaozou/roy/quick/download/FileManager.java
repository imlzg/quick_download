package org.gaozou.roy.quick.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class FileManager {
    static HashMap hm = new HashMap();

    /** Creates a new instance of FileManager */
    public FileManager() {
    }

    public RandomAccessFile getFileFor(File f) throws IOException {

        RandomAccessFile raf = (RandomAccessFile) hm.get( f.getAbsolutePath() );

        if ( raf==null ) {
            if ( !f.exists() )
                f.createNewFile();

            try {
                raf = new RandomAccessFile( f, "rwd" );
            }
            catch (FileNotFoundException fnfe) {
            }
        }

        return raf;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }

}
