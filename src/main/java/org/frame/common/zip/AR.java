package org.frame.common.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.io.IOUtils;

public class AR {
	
	private ArArchiveOutputStream aaos;
	
	public boolean compress(File[] src) {
    	if (src != null && src.length > 0)
    		return compress(src, src[0].getAbsolutePath().replace("\\", "/"));
    	else {
    		System.err.println("no file fonund in this action.");
    		return false;
    	}
    		
    }
	
	public boolean compress(String[] src) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
       return compress(file);
    }
    
    public boolean compress(File[] src, String dest) {
    	return compress(src, new File(dest));
    }
    
    public boolean compress(String[] src, File dest) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
    	return compress(file, dest);
    }
    
    public boolean compress(String[] src, String dest) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
    	return compress(file, new File(dest));
    }
	
	public boolean compress(File[] src, File dest) {
    	boolean result = false;
    	
    	try {
    		if (src != null && src.length > 0) {
        		String zipName = "";
        		int index = src[0].getName().lastIndexOf(".");
        		if (index != -1) {
        			zipName = src[0].getName().substring(0, src[0].getName().lastIndexOf(".")) + ".ar";
        		} else {
        			zipName = src[0].getName() + ".ar";
        		}
        		
        		
        		if (!dest.exists()) {
            		if (dest.getName().indexOf(".") == -1) {
            			dest.mkdirs();
            			dest = new File(dest.getAbsolutePath() +  "/" + zipName);
            			dest.createNewFile();
            		} else {
            			if (!dest.getParentFile().exists()) {
            				dest.getParentFile().mkdirs();
            				dest.createNewFile();
            			} else
            				dest.createNewFile();
            		}
            	} else {
            		if (dest.isDirectory()) {
            			dest = new File(dest.getAbsolutePath() + "/" + zipName);
            			dest.createNewFile();
            		}
            	}
        		
        		CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(dest), new CRC32());
        		aaos = new ArArchiveOutputStream(cos);
        		
        		this.compress(src, dest, "");
        		
        		if (aaos != null) {
            		aaos.close();
            	}
        	}
    		
    		result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    private boolean compress(File[] src, File dest, String entryPath) {
    	boolean result = false;
    	
    	try {
    		for (int i = 0; i < src.length; i++) {
    			if (src[i] != null) {
    				if (src[i].isDirectory()) {
        				ArArchiveEntry entry = new ArArchiveEntry(src[i], entryPath + src[i].getName() + "/");
        				aaos.putArchiveEntry(entry);

        				if (src[i].listFiles() != null) {
        					for (File file : src[i].listFiles()) {
            					this.compress(new File[]{file}, dest, entryPath + src[i].getName() + "/");
            				}
        				}
        			} else if (src[i].isFile()) {
        				FileInputStream fileInputStream = new FileInputStream(src[i]);
        				
        				ArArchiveEntry entry = new ArArchiveEntry(src[i], entryPath + src[i].getName() + "/");
        				aaos.putArchiveEntry(entry);

        				IOUtils.copy(fileInputStream, aaos);
        				
        				aaos.closeArchiveEntry();
        				fileInputStream.close();
        			} else
        				System.err.println("file not exist: " + src[i].getName());
    			} else {
    				System.err.println("file is null.");
    			}
    		}
    		
    		result = true;
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }

	public boolean extract(String src) {
    	return extract(new File(src));  
    }  
  
    public boolean extract(File src) {
    	return extract(src, src.getParent());
    }
  
    public boolean extract(String src, String dest) {
        return this.extract(new File(src), dest);
    }
    
    public boolean extract(File src, String dest) {
        return extract(src, new File(dest));  
  
    }
    
    public boolean extract(String src, File dest) {
        return this.extract(new File(src), dest);
    }
	
	public boolean extract(File src, File dest) {
    	boolean result = false;
    	try {
    		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(src), new CRC32());
    		ArArchiveInputStream aais = new ArArchiveInputStream(cis);

            if (dest.isFile())
        		dest = dest.getParentFile();
        	else
        		dest.mkdirs();
    		
            ArchiveEntry entry;
            File file;
            while ((entry = aais.getNextEntry()) != null) {
            	if (dest.isFile())
            		dest = dest.getParentFile();
            	
            	if (!dest.exists()) {
            		dest.mkdirs();
            	}
            	
                file = new File(dest.getPath() + "/" + entry.getName());
      
                //if (entry.isDirectory()) {//entry can not identify directory, it's a bug in apache commons compress. waiting for update...
                if (entry.getName().indexOf(".") == -1) {
                	file.mkdirs();
                } else {
                	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
          		  
                    int count;
                    byte data[] = new byte[1024];
                    while ((count = aais.read(data, 0, 1024)) != -1) {
                        bos.write(data, 0, count);
                    }
              
                    bos.close();
                }  
      
            }
            
            aais.close();
            
            result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
         
		return result;
    }
	
	/*public static void main(String[] args) {
    	//File[] file = new File[]{new File("d:\\test"), new File("d:\\temp")};
    	//File[] file = new File[]{new File("d:\\test")};
    	//System.out.println(file[0].isFile());
    	//new AR().compress(file, new File("d:\\a.ar"));
    	new AR().extract("d:\\a.ar");
	}*/
}