package org.frame.common.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip {
  
    public static byte[] compress(byte[] data) throws Exception {  
        ByteArrayInputStream bais = new ByteArrayInputStream(data);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
  
        compress(bais, baos);  
  
        byte[] output = baos.toByteArray();  
  
        baos.flush();  
        baos.close();  
  
        bais.close();  
  
        return output;  
    }  
  
    public static void compress(File file) throws Exception {  
        compress(file, true);  
    }  

    public static void compress(File file, boolean delete) throws Exception {  
        FileInputStream fis = new FileInputStream(file);  
        FileOutputStream fos = new FileOutputStream(file.getPath() + ".gz");  
  
        compress(fis, fos);  
  
        fis.close();  
        fos.flush();  
        fos.close();  
  
        if (delete) {  
            file.delete();  
        }  
    }  
  
    public static void compress(InputStream is, OutputStream os)  
            throws Exception {  
  
        GZIPOutputStream gos = new GZIPOutputStream(os);  
  
        int count;  
        byte data[] = new byte[1024];  
        while ((count = is.read(data, 0, 1024)) != -1) {  
            gos.write(data, 0, count);  
        }  
  
        gos.finish();  
  
        gos.flush();  
        gos.close();  
    }  
  
    public static void compress(String path) throws Exception {  
        compress(path, true);  
    }  
  
    public static void compress(String path, boolean delete) throws Exception {  
        File file = new File(path);  
        compress(file, delete);  
    }  
  
    public static byte[] extract(byte[] data) throws Exception {  
        ByteArrayInputStream bais = new ByteArrayInputStream(data);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
  
        extract(bais, baos);  
  
        data = baos.toByteArray();  
  
        baos.flush();  
        baos.close();  
  
        bais.close();  
  
        return data;  
    }  
  
    public static void extract(File file) throws Exception {  
        extract(file, true);  
    }  
  
    public static void extract(File file, boolean delete) throws Exception {  
        FileInputStream fis = new FileInputStream(file);  
        FileOutputStream fos = new FileOutputStream(file.getPath().replace(".gz", ""));  
        extract(fis, fos);  
        fis.close();  
        fos.flush();  
        fos.close();  
  
        if (delete) {  
            file.delete();  
        }  
    }  
  
    public static void extract(InputStream is, OutputStream os) throws Exception {  
  
        GZIPInputStream gis = new GZIPInputStream(is);  
  
        int count;  
        byte data[] = new byte[1024];  
        while ((count = gis.read(data, 0, 1024)) != -1) {  
            os.write(data, 0, count);  
        }
  
        gis.close();  
    }
  
    public static void extract(String path) throws Exception {  
        extract(path, true);  
    }  
  
    public static void extract(String path, boolean delete) throws Exception {  
        File file = new File(path);  
        extract(file, delete);  
    }
    
}
