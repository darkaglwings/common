/**
 * StringUtil contains tools for java.lang.String
 */
package org.frame.common.lang;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class StringHelper {

	public String binaryString2hexString(String bString) {
		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
			return null;
		StringBuffer tmp = new StringBuffer();
		int iTmp = 0;
		for (int i = 0; i < bString.length(); i += 4) {
			iTmp = 0;
			for (int j = 0; j < 4; j++) {
				iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
			}
			tmp.append(Integer.toHexString(iTmp));
		}
		return tmp.toString();
	}
	
	public String byte2Hex(byte[] b) {
	    String result = "";
	    
	    for (int i = 0; i < b.length; i++) {
	    	String hex = Integer.toHexString(b[i] & 0xFF);
	    	if (hex.length() == 1) {
	    		hex = '0' + hex;
	    	}
	    	result += hex.toUpperCase();
	    }
	    
	    return result;
	}
	
	public int byte2Int(byte[] b) {

        int s = 0;

        int s0 = b[0] & 0xff;// 最低位

        int s1 = b[1] & 0xff;

        int s2 = b[2] & 0xff;

        int s3 = b[3] & 0xff;

        s3 <<= 24;

        s2 <<= 16;

        s1 <<= 8;

        s = s0 | s1 | s2 | s3;

        return s;

    }
	
	/**
	 * compare with two version strings
	 * 
	 * @param arg0 the first string to be compared
	 * @param arg1 the second string to be compared
	 * 
	 * @return 1: arg0 > arg1
	 *         0: arg0 = arg1
	 *        -1: arg0 < arg1
	 *      null: errors occurred
	 */
	public Integer compare(String arg1, String arg2, String separator) {
		Integer result = null;
		
		String from = arg1;
		String to = arg2;
		
		if (".".equals(separator)) {
			from = from.replace(".", "_");
			to = to.replace(".", "_");
			separator = "_";
		}
		
		if (from != null && to != null && separator != null) {
			String[] froms = from.split(separator);
			String[] tos = to.split(separator);
			
			int length = froms.length < tos.length ? froms.length : tos.length;
			Long left = null, right = null;
			for (int i = 0; i < length; i++) {
				try {
					left = Long.parseLong(froms[i]);
				} catch (NumberFormatException e) {
					System.err.println(arg1 + " error, " + froms[i] + " is not a number.");
					result = null;
					break;
				}
				
				try {
					right = Long.parseLong(tos[i]);
				} catch (NumberFormatException e) {
					System.err.println(arg2 + " error, " + tos[i] + " is not a number.");
					result = null;
					break;
				}
				
				if (left != null && right != null) {
					if (left > right) {
						result = 1;
						break;
					} else if (left < right) {
						result = -1;
						break;
					} else if (left.longValue() == right.longValue()){
						result = 0;
						continue;
					} else {
						System.err.println(left + ", " + right + "can not be compared.");
					}
				}
			}
			
			if (result != null && result == 0) {
				if (froms.length > tos.length) {
					result = 1;
				} else if (froms.length < tos.length) {
					result = -1;
				} else if (froms.length == tos.length) {
					result = 0;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * read context from file to string
	 * 
	 * @param filePath file path of file to be read
	 * 
	 * @return string contains file content
	 */
	public String file2String(String filePath) {
		if (filePath != null) {
			File file = new File(filePath);
			return file2String(file);
		}
		
		return "";
	}
	
	/**
	 * read context from file to string
	 * 
	 * @param file file to be read
	 * 
	 * @return string contains file content
	 */
	public String file2String(File file) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			String content = "";
			while (content != null) {
				content = bufferedReader.readLine();

				if (content == null) {
					break;
				}

				stringBuilder.append(content.trim());
			}

			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
	
	public char[] getChars (byte[] bytes) {
		Charset cs = Charset.forName ("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate (bytes.length);
		bb.put (bytes);
		bb.flip ();
		CharBuffer cb = cs.decode (bb);

		return cb.array();
	}
	
	public String hex2Byte(String octetString) {
		try {
			String[] temps = octetString.substring(0, octetString.lastIndexOf(":")).split(":");
			byte[] bs = new byte[temps.length];
			for (int i = 0; i < temps.length; i++)
				bs[i] = (byte) Integer.parseInt(temps[i], 16);

			return new String(bs, "GB2312");
		} catch (Exception e) {
			return null;
		}
	}
	
	public String hex2Oct(String data) {
		String result = null;
		try {
			result = String.valueOf(Integer.parseInt(data, 16));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * read context from input stream to file
	 * 
	 * @param inputStream input stream to be read
	 * 
	 * @return string contains input stream content
	 */
	public String inputStream2String(InputStream inputStream) {
		return this.inputStream2String(inputStream, "UTF-8");
	}
	
	/**
	 * read context from input stream to file
	 * 
	 * @param inputStream input stream to be read
	 * 
	 * @return string contains input stream content
	 */
	public String inputStream2String(InputStream inputStream, String encoding) {
		String result = null;
	    BufferedReader in = null;
	    StringBuffer buffer = new StringBuffer();
	    String line = "";
	    try {
	    	in = new BufferedReader(new InputStreamReader(inputStream, encoding));
	    	while ((line = in.readLine()) != null) {
	    		buffer.append(line).append("\n");
	  	    }
	    	
	    	result = new String(buffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    
	    return result;
	}
	
	/**
	 * assert string is null or not
	 * 
	 * @param string string be judged
	 * 
	 * @return  true string is null <br>
	 *         false string is not null
	 */
	public static boolean isAbusolteNull(String string) {
		return string == null;
	}

	/**
	 * assert string is null("", "null", null) or not
	 * 
	 * @param string string be judged
	 * 
	 * @return  true string is null("", "null", null) <br>
	 *         false string is not null("", "null", null)
	 */
	public static boolean isNull(String string) {
		return string == null || "".equals(string) || "null".equals(string);
	}
	
	public static boolean isNumeric(String str){
		for (int i = str.length(); --i >= 0;){   
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	public String join(Object[] object) {
		return this.join(object, ",");
	}
	
	public String join(Object[] object, String separator) {
		String result = "";
		for (int i = 0; i < object.length; i++) {
			if (i < object.length - 1) {
				result += String.valueOf(object[i]) + separator;
			} else {
				result += String.valueOf(object[i]);
			}
		}
		
		return result;
	}
	
	public String oct2Hex(String data) {
		String result = null;
		try {
			result = String.valueOf(Integer.toHexString(Integer.parseInt(data)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * write string to file
	 * 
	 * @param filePath file path of file to be written
	 * @param string string of content
	 * 
	 * @return  true write string to file success <br>
	 *         false write string to file failure(could be errors)
	 */
	public boolean string2File(String filePath, String string) {
		return this.string2File(new File(filePath), string);
	}
	
	/**
	 * write string to file
	 * 
	 * @param file file to be written
	 * @param string string of content
	 * 
	 * @return  true write string to file success <br>
	 *         false write string to file failure(could be errors)
	 */
	public boolean string2File(File file, String string) {
		if (string != null) {
			Writer writer = null;
			File parent = new File(file.getParent());
			if (!parent.exists()) {
				parent.mkdirs();
			}  
			try {  
				writer = new OutputStreamWriter(new FileOutputStream(file));
				writer.write(string);
			} catch(Exception e) {
				e.printStackTrace();  
				return false;
			} finally {
				try {
					if (writer!=null) {  
						writer.close();
					}
				} catch(Exception ex) {
					ex.printStackTrace();  
				}
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * write string to input stream
	 * 
	 * @param string string of content
	 * 
	 * @return input steam to be written
	 */
	public InputStream String2InputStream(String string) {
		if (string != null) {
			ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
		    return stream;
		} else {
			return null;
		}
	}
	
}
