package org.frame.common.io;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class File extends java.io.File {

	private static final long serialVersionUID = -9110018217555238692L;

	public File(String pathname) {
		super(pathname);
	}
	
	public File(URI uri) {
		super(uri);
	}
	
	public File(java.io.File parent, String child) {
		super(parent, child);
	}
	
	public File(String parent, String child) {
		super(parent, child);
	}

	/**
	 * write data in byte[] to file
	 * 
	 * @param bytes byte[] of data
	 * @param file file to be written
	 * 
	 * @return  true write data to file success <br>
	 *         false write data to file failure(could be errors)
	 */
	public boolean byte2file(byte[] bytes) {
		boolean result = false;
		
		FileOutputStream fileOutputStream = null;
		
		try {
			File file;
			if (!this.exists()) {
				this.createNewFile();
				file = this;
			} else {
				if (this.isDirectory()) {
					file = new File(this.getAbsolutePath() + "/new image.jpg");
				} else if (this.isFile()) {
					file = this;
				} else {
					file = null;
					System.err.println("write error: " + this.getAbsolutePath() + " neither a file nor a directory.");
				}
			}
			
			if (file != null) {
				fileOutputStream = new FileOutputStream(this);
				fileOutputStream.write(bytes);
				
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (Exception exception) {
				fileOutputStream = null;
				exception.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * delete all files and folders in the folder of file path
	 * 
	 * @param file folder to be cleaned
	 * 
	 * @return  true clean success <br>
	 *         false clean failure(could be errors)
	 */
	public boolean clean() {
		boolean result = false;

		try{
			if (this.exists()) {
				if (this.listFiles() != null) {
					for (java.io.File file : this.listFiles()) {
						((File) file).delete();
					}
				}
			}

			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * copy file or folder to another folder
	 * 
	 * @param src file path of file or folder to be copied
	 * @param dest file path of folder to be copied to
	 * 
	 * @return  true copy success <br>
	 *         false copy failure(could be errors)
	 */
	public boolean copy(String target) {
		return this.copy(new File(target));
	}
	
	/**
	 * copy file or folder to another folder
	 * 
	 * @param src file or folder to be copied
	 * @param dest folder to be copied to
	 * 
	 * @return  true copy success <br>
	 *         false copy failure(could be errors)
	 */
	public boolean copy(File target) {
		boolean result = false;

		try{
			if (this.exists()) {
				if (this.isDirectory()) {
					this.copyFile(target);
					target = new File(target.getAbsolutePath().replace("\\", "/") + "/" + this.getName());
					if (this.listFiles() != null) {
						for (java.io.File file : this.listFiles()) {
							if (file.isDirectory()) ((File) file).copy(target);
							else if (file.isFile()) ((File) file).copyFile(target);
						}
					}
				} else if (this.isFile()) {
					this.copyFile(target);
				}
			}
			
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	public boolean createNewFile() throws IOException {
		boolean result = false;
		
		if (!this.exists()) {
			super.getParentFile().mkdirs();
			result = super.createNewFile();
		}
		
		return result;
	}
	
	/**
	 * delete file or folder
	 * @param file file or folder to be deleted
	 * 
	 * @return  true delete success <br>
	 *         false delete failure(could be errors)
	 */
	@Override
	public boolean delete() {
		boolean result = false;
		try{
			if (this.exists()) {
				if (this.isDirectory()) {
					if (this.listFiles() != null) {
						for (java.io.File file : this.listFiles()) {
							if (file.isDirectory()) ((File) file).delete();
							else if (file.isFile()) super.delete();
						}
					}
					
					super.delete();
				} else if (this.isFile()) super.delete();
			}
			
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * execute a file or open a folder
	 * 
	 * @param file file or folder to be opened
	 * 
	 * @return  true execute success <br>
	 *         false execute failure(could be errors)
	 */
	public boolean execute() {
		boolean result = false;
		
		if (this.exists()) {
			try {
				Desktop.getDesktop().open(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			result = true;
		} else {
			System.out.println("file not found: " + this.getAbsolutePath());
		}
		
		return result;
	}
	
	/**
	 * to get file name from a file path string
	 * 
	 * @param file file to be gotten name
	 * 
	 * @return string of file name
	 */
	public String getAbsoluteName() {
		int index = this.getName().indexOf(".");
		if (index == -1)
			return this.getName();
		else
			return this.getName().substring(0, index);
	}
	
	/**
	 * read file content to byte[]
	 * 
	 * @param file file to be read
	 * 
	 * @return byte[] of file content
	 */
	public byte[] file2byte() {
		byte[] result = null;
		
		if (this.isFile()) {
			BufferedInputStream bufferedInputStream = null;
			ByteArrayOutputStream byteArrayOutputStream = null;
			
			try {
				bufferedInputStream = new BufferedInputStream(new FileInputStream(this));
				byteArrayOutputStream = new ByteArrayOutputStream(1024);

				byte[] buffer = new byte[1024];
				int size = 0;
				while ((size = bufferedInputStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, size);
				}
				bufferedInputStream.close();

				result = byteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bufferedInputStream != null) bufferedInputStream.close();
					if (byteArrayOutputStream != null) byteArrayOutputStream.close();
				} catch (Exception exception) {
					bufferedInputStream = null;
					byteArrayOutputStream = null;
					exception.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * assert a file is a child of the other directory
	 * 
	 * @param child path of the file to be judged
	 * @param parent parent directory
	 * 
	 * @return  true file is a child of the parent directory
	 *         false file is not a child of the parent directory
	 */
	public boolean isParent(String child) {
		return this.isParent(new File(child));
	}
	
	/**
	 * assert a file is a child of the other directory
	 * 
	 * @param child file to be judged
	 * @param parent parent directory
	 * 
	 * @return  true file is a child of the parent directory
	 *         false file is not a child of the parent directory
	 */
	public boolean isParent(File child) {
		boolean result = false;
		
		if (child != null && this != null) {
			if (child.getParentFile() == null) {
				result = false;
			} else {
				if (child.getParentFile().getAbsolutePath().equals(this.getAbsolutePath())) {
					result = true;
				} else {
					result = isParent((File) child.getParentFile());
				}
			}
		}
		
		return result;
	}
	
	/**
	 * move file or folder to another folder
	 * 
	 * @param src file path of file or folder to be moved
	 * @param dest file path of folder to be moved to
	 * 
	 * @return  true move success <br>
	 *         false move failure(could be errors)
	 */
	public boolean move(String dest) {
		return this.move(new File(dest));
	}
	
	/**
	 * move file or folder to another folder
	 * 
	 * @param src file or folder to be moved
	 * @param dest folder to be moved to
	 * 
	 * @return  true move success <br>
	 *         false move failure(could be errors)
	 */
	public boolean move(File dest) {
		boolean result = false;

		try{
			if (this.exists()) {
				if (!dest.exists()) dest.mkdirs();

				if (dest.isDirectory()) dest = new File(dest.getAbsolutePath().replace("\\", "/") + "/" + this.getName());
				else if (dest.isFile()) dest = new File(dest.getParent().replace("\\", "/") + "/" + this.getName());

				if (dest.exists()) {
					System.err.println("directory is exist");
					//this.delete(dest);
				}
				
				this.renameTo(dest);
			}

			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * rename a file or a folder to a new file name
	 * 
	 * @param src file path of file or folder to be renamed
	 * @param dest file path of file or folder to be renamed to
	 * 
	 * @return  true rename success <br>
	 *         false rename failure(could be errors)
	 */
	public boolean rename(String dest) {
		return this.rename(new File(dest));
	}
	
	/**
	 * rename a file or a folder to a new file name
	 * 
	 * @param src file or folder to be renamed
	 * @param dest file or folder to be renamed to
	 * 
	 * @return  true rename success <br>
	 *         false rename failure(could be errors)
	 */
	public boolean rename(File dest) {
		boolean result = false;

		try{
			if (this.exists()) {
				if (this.isFile() && dest.isDirectory()) System.err.println("a file can not rename to a directory");;
				if (this.isDirectory() && dest.isFile()) System.err.println("a directory can not rename to a file");
				
				this.renameTo(dest);
			}

			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * do copy operation from a file to a new file
	 * 
	 * @param src file to be copied
	 * @param dest file to be copied to
	 * 
	 * @return  true copy success <br>
	 *         false copy failure(could be errors)
	 */
	private boolean copyFile(File dest) {
		boolean result = false;

		try {
			if (this.exists()) {
				if (!dest.exists()) dest.mkdirs();
				
				if (this.isDirectory() && dest.isDirectory()) {
					dest = new File(dest.getAbsolutePath().replace("\\", "/") + "/" + this.getName());
					dest.mkdirs();
					
					return true;
				}
				
				if (this.isFile() && dest.isDirectory()) {
					dest = new File(dest.getAbsolutePath().replace("\\", "/") + "/" + this.getName());
					dest.createNewFile();
				}
				
				if (this.isDirectory() && dest.isFile()) {
					dest = new File(dest.getParent().replace("\\", "/") + "/" + this.getName());
					dest.mkdirs();
					
					return true;
				}
				
				if (this.isFile() && dest.isFile()) {
					if (dest.exists()) {
						dest = new File(dest.getParent().replace("\\", "/") + "/" + this.getName());
						dest.createNewFile();
					}
					
					if (this.getName().equals(dest.getName())) {
						//targetFile.deleteOnExit();
						//targetFile = new File(targetFile.getAbsolutePath());
					}
				}
				
				BufferedInputStream inBuff = null;
				BufferedOutputStream outBuff = null;
				
				try{
					inBuff = new BufferedInputStream(new FileInputStream(this));
					outBuff = new BufferedOutputStream(new FileOutputStream(dest));

					byte[] b = new byte[1024 * 5];
					int length;
					while ((length = inBuff.read(b)) != -1) {
						outBuff.write(b, 0, length);
					}
					
					outBuff.flush();
					
					result = true;
				} catch(Exception e) {
					e.printStackTrace();
				}finally{
					try {
						if (inBuff != null) inBuff.close();
						if (outBuff != null) outBuff.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	/*public static void main(String[] args) {
		//System.out.println(new FileUtil().fileName("d:\\test.txt"));
		//new FileUtil().move("d:\\test", "d:\\test2");
		//new FileUtil().copy("d:\\test", "d:\\test2");
		
		//new FileUtil().clean("d:\\test2\\test");
		//new FileUtil().delete("d:\\test2\\test");
		//new FileUtil().execute(new File("d:\\test.bat"));
		
		System.out.println(new FileUtil().isParent("D:\\test\\a\\b", "D:\\"));
	}*/
	
	
}
