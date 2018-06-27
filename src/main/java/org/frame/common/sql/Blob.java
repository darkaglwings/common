/**
 * LobUtil contains tools for java.sql.Blob and java.sql.Clob
 */
package org.frame.common.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Blob {

	/**
	 * write blob content to file
	 * 
	 * @param file file to be written
	 * @param blob blob content
	 * 
	 * @return  true write blob content to file success <br>
	 *         false write blob content to file failure(could be errors)
	 */
	public boolean blob2File(File file, java.sql.Blob blob) {
		return this.blob2File(file.getAbsolutePath(), blob);
	}
	
	/**
	 * write blob content to file
	 * 
	 * @param filePath file path of file to be written
	 * @param blob blob content
	 * 
	 * @return  true write blob content to file success <br>
	 *         false write blob content to file failure(could be errors)
	 */
	public boolean blob2File(String filePath, java.sql.Blob blob) {
		boolean result = false;
		
		InputStream inputStream = null;
		OutputStream fileOutputStream = null;
		
		try {
			if (blob != null) {
				inputStream = blob.getBinaryStream();
				File file = new File(filePath);
				fileOutputStream = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int length = 0;
				while ( (length = inputStream.read(b)) != -1) {
					fileOutputStream.write(b, 0, length);
				}
			}
			
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (Exception exception) {
				fileOutputStream = null;
				exception.printStackTrace();
			}
			
			try {
				inputStream.close();
			} catch (Exception exception) {
				inputStream = null;
				exception.printStackTrace();
			}
		}
		
		return result;
	}
	
}
