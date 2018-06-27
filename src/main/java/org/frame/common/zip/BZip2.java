package org.frame.common.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class BZip2 {
  
    public byte[] compress(byte[] data) {
    	byte[] output = null;
    	
    	try {
    		ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
            compress(bais, baos);
      
            output = baos.toByteArray();
      
            baos.flush();
            baos.close();
      
            bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
  
        return output;  
    }  
  
    public boolean compress(File file) {
        return compress(file, false);
    }
  
    public boolean compress(File file, boolean delete) {
    	boolean result = false;
    	
    	try {
    		FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(file.getPath() + ".bz2");
            
            compress(fis, fos);
      
            fis.close();
            fos.flush();
            fos.close();
      
            if (delete) {
                file.delete();
            }
            
            result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return result;
    }  
  
    public boolean compress(InputStream is, OutputStream os) {
    	boolean result = false;
    	
    	try {
    		BZip2CompressorOutputStream gos = new BZip2CompressorOutputStream(os);
    		  
            int count;
            byte data[] = new byte[1024];
            while ((count = is.read(data, 0, 1024)) != -1) {
                gos.write(data, 0, count);
            }
      
            gos.finish();
      
            gos.flush();
            gos.close();
            
            result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
    }  

    public boolean compress(String path) {
       return compress(path, false);
    }
    
    public boolean compress(String path, boolean delete) {
       return compress(new File(path), delete);
    }
    
    public byte[] extract(byte[] data) {
    	byte[] result = null;
    	
    	try {
    		ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
            extract(bais, baos);
      
            result = baos.toByteArray();
      
            baos.flush();
            baos.close();

            bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
  
        return result;
    }
    
    public boolean extract(File file) {  
        return extract(file, true);  
    }
    
    public boolean extract(File file, boolean delete) {
    	boolean result = false;
    	
    	try {
    		FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(file.getPath().replace(".bz2", ""));
            extract(fis, fos);
            fis.close();
            fos.flush();
            fos.close();
      
            if (delete) {
                file.delete();
            }
            
            result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return result;
    }
    
    public boolean extract(InputStream is, OutputStream os) {
    	boolean result = false;
    	
    	try {
    		BZip2CompressorInputStream gis = new BZip2CompressorInputStream(is);
    		
            int count;
            byte data[] = new byte[1024];
            while ((count = gis.read(data, 0, 1024)) != -1) {
                os.write(data, 0, count);
            }
      
            gis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
    }
    
    public boolean extract(String path) {
        return extract(path, false);
    }

    public boolean extract(String path, boolean delete) {
        return extract(new File(path), delete);
    }
}
