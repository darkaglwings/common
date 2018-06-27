/**
 * LobUtil contains tools for java.sql.Blob and java.sql.Clob
 */
package org.frame.common.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.SQLException;

public class Clob {

	/**
	 * write clob content to file
	 * @param file file to be written
	 * @param clob clob content
	 * 
	 * @return  true write clob content to file success <br>
	 *         false write clob content to file failure(could be errors)
	 */
	public boolean clob2File(File file, java.sql.Clob clob) {
		return this.clob2File(file.getAbsolutePath(), clob);
	}
	
	/**
	 * write clob content to file
	 * @param filePath file path of file to be written
	 * @param clob clob content
	 * 
	 * @return  true write clob content to file success <br>
	 *         false write clob content to file failure(could be errors)
	 */
	public boolean clob2File(String filePath, java.sql.Clob clob) {
		boolean result = false;
		Reader reader = null;
		BufferedReader bufferedReader = null;
		FileOutputStream fileOutputStream = null;
		PrintStream printStream = null;
		try {
			if (clob != null) {

				String content = "";
				reader = clob.getCharacterStream();
				bufferedReader = new BufferedReader(reader);
				String line = bufferedReader.readLine();
				while (line!=null) {
					content += line;
					line = bufferedReader.readLine();
				}        

				fileOutputStream = new FileOutputStream(filePath);
				printStream = new PrintStream(fileOutputStream);
				printStream.println(content);
				
			}
			
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (printStream != null) printStream.close();
			
			try {
				reader.close();
			} catch (Exception exception) {
				reader = null;
				exception.printStackTrace();
			}
			
			try {
				fileOutputStream.close();
			} catch (Exception exception) {
				fileOutputStream = null;
				exception.printStackTrace();
			}
			
			try {
				bufferedReader.close();
			} catch (Exception exception) {
				bufferedReader = null;
				exception.printStackTrace();
			}
		}
		
		return result;
	}

	/**
	 * read clob content to string
	 * 
	 * @param clob clob content
	 * 
	 * @return string string of clob content
	 */
	public String clob2String(java.sql.Clob clob) {
		String result = null;
		if (clob != null) {
			try {
				Reader reader = clob.getCharacterStream();

				char[] doc = new char[(int) clob.length()];
				reader.read(doc);
				reader.close();

				result = new String(doc);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
}
