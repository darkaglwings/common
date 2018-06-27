/**
 * Zip contains methods compress and extract files with zip
 */
package org.frame.common.zip;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipFile;

public class Zip {

	public static final String ZIP_MODE_ORIGIN = "origin";
	public static final String ZIP_MODE_APACHE = "apache";
	
	private final String mode = ZIP_MODE_APACHE;
	
	private Object zos;
	
	private String encoding;
	
	 public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * constructor with default encoding(GBK)
	 */
	public Zip() {
		this.encoding = "GBK";
	}
	
	/**
	 * constructor with specific encoding
	 * 
	 * @param encoding zip encoding
	 */
	public Zip(String encoding) {
		this.encoding = encoding;
	}
	
	/**
     * compress byte[]
     * 
     * @param data byte[] to be compressed
     * 
     * @return byte[] byte[] after compress
     */
	public byte[] compress(byte[] data) {
        byte[] output = new byte[0];
  
        Deflater compresser = new Deflater();
  
        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }
  
    /**
     * compress byte[] to output stream
     *  
     * @param data byte[] to be compressed
     * @param os output stream for compress
     */
    public void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);
  
        try {
            dos.write(data, 0, data.length);
            dos.finish();
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * extract byte[]
     * 
     * @param data byte[] to be extracted
     * 
     * @return byte[] byte[] after extract
     */
    public byte[] extract(byte[] data) {
        byte[] output = new byte[0];
  
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
  
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
  
        decompresser.end();
        return output;
    }
  
    /**
     * extract from input stream
     *
     * @param is input stream to be extracted
     * 
     * @return byte[] byte[] after extract
     */
    public byte[] extract(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
        try {
            int i = 1024;  
            byte[] buf = new byte[i];
  
            while ((i = iis.read(buf, 0, i)) > 0) {  
                o.write(buf, 0, i);
            }
  
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return o.toByteArray();
    }
    
    /**
     * compress files or folders to the same folder with first file
     * 
     * @param src file[] to be compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(File[] src) {
    	if (src != null && src.length > 0)
    		return compress(src, src[0].getAbsolutePath().replace("\\", String.valueOf("/")));
    	else {
    		System.err.println("no file fonund in this operation.");
    		return false;
    	}
    }
    
    /**
     * compress files or folders to the same folder with first file
     * 
     * @param src string[] of file path of files or folders to be compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(String[] src) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
       return compress(file);
    }
    
    /**
     * compress files or folders to specific zip file
     * 
     * @param src file[] of files or folders to be compressed
     * @param dest file path of specific zip file after compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(File[] src, String dest) {
    	return compress(src, new File(dest));
    }
    
    /**
     * compress files or folders to specific zip file
     * 
     * @param src string[] of file path of files or folders to be compressed
     * @param dest specific zip file after compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(String[] src, File dest) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
    	return compress(file, dest);
    }
    
    /**
     * compress files or folders to specific zip file
     * 
     * @param src string[] of file path of files or folders to be compressed
     * @param dest file path of specific zip file after compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(String[] src, String dest) {
    	File[] file = new File[src.length];
    	for (int i = 0; i < src.length; i++) {
			file[i] = new File(src[i]);
		}
    	return compress(file, new File(dest));
    }
    
    /**
     * compress files or folders to specific zip file
     * 
     * @param src file[] of files or folders to be compressed
     * @param dest specific zip file after compressed
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    public boolean compress(File[] src, File dest) {
    	boolean result = false;
    	
    	try {
    		if (src != null && src.length > 0) {
        		String zipName = "";
        		int index = src[0].getName().lastIndexOf(".");
        		if (index != -1) {
        			zipName = src[0].getName().substring(0, src[0].getName().lastIndexOf(".")) + ".zip";
        		} else {
        			zipName = src[0].getName() + ".zip";
        		}
        		
        		
        		if (!dest.exists()) {
            		if (dest.getName().indexOf(".") == -1) {
            			dest.mkdirs();
            			dest = new File(dest.getAbsolutePath() +  "/" + zipName);
            		} else {
            			if (!dest.getParentFile().exists()) {
            				dest.getParentFile().mkdirs();
            			}
            		}
            	} else {
            		if (dest.isDirectory()) {
            			dest = new File(dest.getAbsolutePath() + "/" + zipName);
            		}
            	}
        		
        		if (Zip.ZIP_MODE_APACHE.equals(this.mode)) {
        			zos = new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(dest));
        			((org.apache.tools.zip.ZipOutputStream) zos).setEncoding(this.encoding);
        		} else if (Zip.ZIP_MODE_ORIGIN.equals(this.mode)) {
        			zos = new java.util.zip.ZipOutputStream(new FileOutputStream(dest));
        		} else {
        			System.err.println("can not initlize ZipOutputStream.");
        			return false;
        		}
        		
        		this.zip(src, dest, "");
        		
        		if (zos != null) {
            		if (zos instanceof java.util.zip.ZipOutputStream) {
            			((java.util.zip.ZipOutputStream) zos).close();
        			} else if (zos instanceof org.apache.tools.zip.ZipOutputStream) {
            			((org.apache.tools.zip.ZipOutputStream) zos).close();
        			} else {
        				return true;
        			}
            	}
        	}
    		
    		result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    /**
     * extract zip file to the same folder
     * 
     * @param src file path of zip file to be extracted
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(String src) {
    	return extract(new File(src));
    }

    /**
     * extract zip file to the same folder
     * 
     * @param src zip file to be extracted
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(File src) {
    	return extract(src, src.getParent());
    }

    /**
     * extract zip file to specific folder
     * 
     * @param src file path of zip file to be extracted
     * @param dest file path of specific folder
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(String src, String dest) {
        return this.extract(new File(src), dest);
    }
    
    /**
     * extract zip file to specific folder
     * 
     * @param src zip file to be extracted
     * @param dest file path of specific folder
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(File src, String dest) {
        return extract(src, new File(dest));
  
    }
    
    /**
     * extract zip file to specific folder
     * 
     * @param src file path of zip file to be extracted
     * @param dest specific folder
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(String src, File dest) {
        return this.extract(new File(src), dest);
    }
    
    /**
     * extract zip file to specific folder
     * 
     * @param src zip file to be extracted
     * @param dest specific folder
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    public boolean extract(File src, File dest) {
    	boolean result = false;

    	if (Zip.ZIP_MODE_APACHE.equals(this.mode)) {
    		this.unzip(src, dest);
    	} else if (Zip.ZIP_MODE_ORIGIN.equals(this.mode)) {
    		try {
    			CheckedInputStream cis = new CheckedInputStream(new FileInputStream(src), new CRC32());
    			ZipInputStream zis = new ZipInputStream(cis);

    			if (dest.isFile()) {
    				dest = new File(dest.getParentFile().getAbsolutePath());
    			}

    			ZipEntry entry;
    			File file;
    			while ((entry = zis.getNextEntry()) != null) {
    				if (dest.isFile())
    					dest = dest.getParentFile();
    				else
    					dest.mkdirs();

    				file = new File(dest.getPath() + "/" + entry.getName());

    				if (entry.isDirectory()) {
    					file.mkdirs();
    					result = true;
    				} else {
    					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

    					int count;
    					byte data[] = new byte[1024];
    					while ((count = zis.read(data, 0, 1024)) != -1) {
    						bos.write(data, 0, count);
    					}

    					bos.close();
    				}  

    				zis.closeEntry();
    			}

    			zis.close();

    			result = true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		System.err.println("can not initlize ZipOutputStream.");
    		return false;
    	}

		return result;
    }
    
    /**
     * do extract operation
     * @param src zip file to be extracted
     * @param dest folder after extracted
     * 
     * @return  true extract success <br>
     *         false extract failure(could be errors)
     */
    private boolean unzip(File src, File dest) {
    	boolean result = false;

    	try {
    		ZipFile zipFile = new ZipFile(src, this.encoding);
    		Enumeration<org.apache.tools.zip.ZipEntry> enumeration = zipFile.getEntries();
    		org.apache.tools.zip.ZipEntry zipEntry = null;
    		byte[] buffer = new byte[1024];
    		int length = -1;
    		InputStream input = null;
    		BufferedOutputStream bos = null;
    		File file = null;

    		while (enumeration.hasMoreElements()) {
    			zipEntry = (org.apache.tools.zip.ZipEntry) enumeration.nextElement();
    			if (zipEntry.isDirectory()) {
    				file = new File(dest, zipEntry.getName());
    				if (!file.exists()) {
    					file.mkdir();
    				}
    				continue;
    			}

    			input = zipFile.getInputStream(zipEntry);
    			file = new File(dest, zipEntry.getName());
    			if (!file.getParentFile().exists()) {
    				file.getParentFile().mkdirs();
    			}
    			bos = new BufferedOutputStream(new FileOutputStream(file));

    			while (true) {
    				length = input.read(buffer);
    				if (length == -1)
    					break;
    				bos.write(buffer, 0, length);
    			}
    			bos.close();
    			input.close();
    		}
    		zipFile.close();

    		result = true;
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	return result;
    }
    
    /**
     * do compress operation
     * 
     * @param src files or folders to be extracted
     * @param dest file path of specific folder
     * @param entryPath entry path in zip file
     * 
     * @return  true compress success <br>
     *         false compress failure(could be errors)
     */
    private boolean zip(File[] src, File dest, String entryPath) {
    	boolean result = false;
    	
    	try {
    		for (int i = 0; i < src.length; i++) {
    			if (src[i].isDirectory()) {
    				String zipEntry = entryPath + src[i].getName() + "/";
    				if (zos instanceof java.util.zip.ZipOutputStream) {
    					((java.util.zip.ZipOutputStream) zos).putNextEntry(new java.util.zip.ZipEntry(zipEntry));
    					((java.util.zip.ZipOutputStream) zos).closeEntry();
            			
            			for (File file : src[i].listFiles()) {
            				this.zip(new File[]{file}, dest, zipEntry);
            			}
    				} else if (zos instanceof org.apache.tools.zip.ZipOutputStream) {
    					((org.apache.tools.zip.ZipOutputStream) zos).putNextEntry(new org.apache.tools.zip.ZipEntry(zipEntry));
    					((org.apache.tools.zip.ZipOutputStream) zos).closeEntry();
            			
            			this.zip(src[i].listFiles(), dest, zipEntry);
    				} else {
    					System.err.println("ZipOutputStream not found.");
    					return false;
    				}
    			} else if (src[i].isFile()) {
    				String zipEntry = entryPath + src[i].getName();
    				if (zos instanceof java.util.zip.ZipOutputStream) {
    					FileInputStream fileInputStream = new FileInputStream(src[i]);
    					((java.util.zip.ZipOutputStream) zos).putNextEntry(new java.util.zip.ZipEntry(zipEntry));

    					IOUtils.copy(fileInputStream, (java.util.zip.ZipOutputStream) zos);
    	    			
    	    			((java.util.zip.ZipOutputStream) zos).closeEntry();
    	    			fileInputStream.close();
    				} else if (zos instanceof org.apache.tools.zip.ZipOutputStream) {
    					FileInputStream fileInputStream = new FileInputStream(src[i]);
    					((org.apache.tools.zip.ZipOutputStream) zos).putNextEntry(new org.apache.tools.zip.ZipEntry(zipEntry));
    					
    	    			IOUtils.copy(fileInputStream, (org.apache.tools.zip.ZipOutputStream) zos);
    	    			
    	    			((org.apache.tools.zip.ZipOutputStream) zos).closeEntry();
    	    			fileInputStream.close();
    				} else {
    					System.err.println("ZipOutputStream not found.");
    					return false;
    				}
    			} else
    				System.err.println("file not exist: " + src[i].getName());
    		}
    		
    		result = true;
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
}
