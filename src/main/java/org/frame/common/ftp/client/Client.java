/**
 * FTP Client contains client tools for FTP
 */
package org.frame.common.ftp.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Client {
	
	final static int DIRECTORY = 1;
	
	final static int FILE = 0;
	
	private FTPClient ftpClient;
	
	private String host;
	
	private int port;
	
	private String username;
	
	private String password;
	
	private boolean binaryTransfer = true;
	
	private boolean passiveMode = true;
    
    private String encoding  = "UTF-8";
    
    private int clientTimeout = -1;
	
	private boolean loggedin = false;
	
	public boolean getBinaryTransfer() {
		return this.binaryTransfer;
	}
	
	public void setBinaryTransfer(boolean binaryTransfer) {
		this.binaryTransfer = binaryTransfer;
		if (ftpClient != null) {
			try {
				if (binaryTransfer) ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				else ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getClientTimeout() {
		return clientTimeout;
	}

	public void setClientTimeout(int clientTimeout) {
		this.clientTimeout = clientTimeout;
		try {
			if (this.clientTimeout != -1) ftpClient.setSoTimeout(clientTimeout);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
		if (ftpClient != null) ftpClient.setControlEncoding(encoding);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public boolean getPassiveMode() {
		return this.passiveMode;
	}
	
	public void setPassiveMode(boolean passiveMode) {
		this.passiveMode = passiveMode;
		if (ftpClient != null) ftpClient.enterLocalPassiveMode();
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * constructor with anonymous and default encoding(UTF-8)
	 */
	public Client() {
		ftpClient = new FTPClient();
		this.setEncoding(encoding);
	}
	
	/**
	 * constructor with default port(21) and default encoding(UTF-8)
	 * 
	 * @param host host ip of ftp server
	 * @param username username for access ftp server
	 * @param password password for access ftp server
	 */
	public Client(String host, String username, String password) {
		this.host = host;
		this.port = 21;
		this.username = username;
		this.password = password;
		
		ftpClient = new FTPClient();
		this.setEncoding(encoding);
	}
	
	/**
	 * constructor with default encoding(UTF-8)
	 * 
	 * @param host host ip of ftp server
	 * @param port port of ftp server
	 * @param username username for access ftp server
	 * @param password password for access ftp server
	 */
	public Client(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		
		ftpClient = new FTPClient();
		this.setEncoding(encoding);
	}
	
	/**
	 * identify if binary transfer
	 * 
	 * @return  true transfer file with binary <br>
	 *         false transfer file with ascii
	 */
	public boolean isBinaryTransfer() {
		return binaryTransfer;
	}
	
	/**
	 * identify if connect to ftp server
	 * 
	 * @return  true connect to ftp server <br>
	 *         false not connect to ftp server
	 */
	public boolean isConnected() {
		return ftpClient != null && ftpClient.isConnected();
	}
	
	/**
	 * identify if login to ftp server
	 * 
	 * @return  true logged in ftp server <br>
	 *         false not logged in ftp server
	 */
	public boolean isLoggedin() {
		return this.loggedin;
	}
	
	/**
	 * identify if use passive mode
	 * 
	 * @return  true use passive mode <br>
	 *         false not use passive mode
	 */
	public boolean isPassiveMode() {
		return passiveMode;
	}
	
	/**
	 * change work directory in ftp server
	 * 
	 * @param path work directory changed to
	 * 
	 * @return string of current work directory
	 */
	public String cd(String path) {
		String result = null;
		if (this.login()) {
			try {
				ftpClient.changeWorkingDirectory(path);
				result = path;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * connect to ftp server
	 * 
	 * @return  true connected to ftp server <br>
	 *         false not connect to ftp server
	 */
	public boolean connect() {
		if (!this.isConnected()) {
			try {
				ftpClient.connect(host, port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return this.isConnected();
	}
	
	/**
	 * delete file or folder in server
	 * 
	 * @param path file path of file or folder to be deleted
	 * 
	 * @return  true delete success <br>
	 *         false delete failure(could be errors)
	 */
	public boolean delete(String path) {
		boolean result = false;
		
		if (this.login()) {
			try {
				FTPFile[] ftpFile = this.file(path);
				for (int i = 0; i < ftpFile.length; i++) {
					if (".".equals(ftpFile[i].getName()) || "..".equals(ftpFile[i].getName())) {
						continue;
					} else {
						if (ftpFile[i].getType() == 1) {
							this.delete(path + "/" + ftpFile[i].getName());
						} else if (ftpFile[i].getType() == 0) {
							ftpClient.deleteFile(path + "/" + ftpFile[i].getName());
						} else {
							System.err.println("unknown file type: " + ftpFile[i].getType());
						}
					}
				}
				ftpClient.removeDirectory(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * disconnect ftp server
	 * 
	 * @return  true connected to ftp server <br>
	 *         false not connect to ftp server
	 */
	public boolean disconnect() {
		try {
			if (ftpClient != null) ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this.isConnected();
	}
	
	/**
	 * download file or folder from ftp server
	 * 
	 * @param remote file path of file or folder to be downloaded
	 * @param local path of local folder to be saved remote file or folder
	 * 
	 * @return  true download success <br>
	 *         false download failure(could be errors)
	 */
	public boolean download(String remote, String local) {
		return this.download(remote, new File(local));
	}

	/**
	 * download file or folder from ftp server
	 * 
	 * @param remote file path of file or folder to be downloaded
	 * @param local local folder to be saved with remote file or folder
	 * 
	 * @return  true download success <br>
	 *         false download failure(could be errors)
	 */
	public boolean download(String remote, File local) {
		boolean result = false;

		int remoteType;

		FTPFile[] ftpFile = this.file(remote);
		if (ftpFile.length > 0) {
			if (ftpFile[0].getType() == 0) {
				remoteType = Client.FILE;
			} else {
				remoteType = Client.DIRECTORY;
			}
				
			handler(remote, local, remoteType);
		}
			
		return result;
	}
	
	
	
	/**
	 * list files and folders in root path
	 * 
	 * @return ftp file[] files in root path
	 */
	public FTPFile[] file() {
		return this.file("/");
	}
	
	/**
	 * list files and folders in specific path
	 * 
	 * @param path file path of folder in ftp server to be listed
	 * 
	 * @return ftp file[] files in specfic path
	 */
	public FTPFile[] file(String path) {
		FTPFile[] ftpFile = null;
		try {
			if (this.login()) {
				ftpFile = ftpClient.listFiles(path);
			}
		} catch (Exception e) {
			System.err.println("ftp client can not operator files.");
			e.printStackTrace();
		}
		
		return ftpFile;
	}
	
	/**
	 * to logged in ftp server
	 * 
	 * @return  true logged in ftp server <br>
	 *         false not logged in ftp server
	 */
	public boolean login() {
		try {
			if (this.connect()) {
				if (!this.isLoggedin()) {
					ftpClient.login(username, password);
					this.loggedin = true;
				}
			} else {
				System.err.println("ftp client can not access the ftp server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.isLoggedin();
	}
	
	/**
	 * to log out from ftp server
	 * 
	 * @return  true logged out ftp server <br>
	 *         false not logged out ftp server
	 */
	public boolean logout() {
		if (this.isLoggedin()) {
			try {
				if (ftpClient != null) loggedin = ftpClient.logout();
				else this.loggedin = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return !this.isLoggedin();
	}
	
	/**
	 * create folder in ftp server
	 * 
	 * @param path file path of folder to be created
	 * 
	 * @return  true create folder success <br>
	 *         false create folder failure
	 */
	public boolean mkdir(String path) {
		boolean result = false;
		
		if (this.login()) {
			try {
				path = path.replace("\\", "/");
				String[] paths = path.split("/");
				path = "";
				for (int i = 0; i < paths.length; i++) {
					path += "/" + paths[i];
					ftpClient.makeDirectory(path);
				}
				
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * upload file or folder to ftp server
	 * 
	 * @param remote file path of folder to be saved in ftp server
	 * @param local file path of local file or folder to be uploaded
	 * 
	 * @return  true upload success <br>
	 *         false upload failure(could be errors)
	 */
	public boolean upload(String remote, String local) {
		return this.upload(remote, new File(local));
	}
	
	/**
	 * upload file or folder to ftp server
	 * 
	 * @param remote file path of folder to be saved in ftp server
	 * @param local local file or folder to be uploaded
	 * 
	 * @return  true upload success <br>
	 *         false upload failure(could be errors)
	 */
	public boolean upload(String remote, File local) {
		boolean result = false;
		
		if (local.exists()) {
			remote = remote.replace("\\", "/");
			if (remote.endsWith("/")) remote = remote.substring(0, remote.length() - 1);
			FTPFile[] ftpFile = this.file(remote);
			if (ftpFile.length == 0) this.mkdir(remote);
			else if (ftpFile.length == 1) {
				if (ftpFile[0].getType() == 0) {
					remote = remote.substring(0, remote.lastIndexOf("/"));
				}
			}
			
			if (local.isDirectory()) {
				this.mkdir(remote + "/" + local.getName());
				File[] files = local.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) this.upload(remote + "/" + local.getName(), files[i]);
						else if (files[i].isFile()) this.write(remote + "/" + local.getName() + "/" + files[i].getName(), files[i]);
					}
				}
			} else if (local.isFile()) {
				this.write(remote + "/" + local.getName(), local);
			} else {
				System.err.println("unknown file type: " + local.getName());
			}
		}
		
		return result;
	}
	
	
	/**
	 * handle download operation
	 * 
	 * @param remote file path of file or folder to be downloaded
	 * @param local local folder to be saved with remote file or folder
	 * @param type download a file or a folder up to Client.DIRECTORY or Client.FILE
	 * 
	 * @return  true download success <br>
	 *         false download failure(could be errors)
	 */
	private boolean handler(String remote, File local, int type) {
		boolean result = false;
		if (this.login()) {
			try {
				File file;
				remote = remote.replace("\\", "/");
				if (remote.endsWith("/")) remote = remote.substring(0, remote.length() - 1);
				
				FTPFile[] ftpFile = this.file(remote);
				for (int i = 0; i < ftpFile.length; i++) {
					if (".".equals(ftpFile[i].getName()) || "..".equals(ftpFile[i].getName())) {
						continue;
					} else {
						if (ftpFile[i].getType() == 1) {
							file = this.location(local, remote + "/" + ftpFile[i].getName(), Client.DIRECTORY);
							this.handler(remote + "/" + ftpFile[i].getName(), local, Client.DIRECTORY);
						} else if (ftpFile[i].getType() == 0) {
							if (type == Client.FILE) {
								file = this.location(local, ftpFile[i].getName(), Client.FILE);
								this.read(remote, file);
							} else {
								file = this.location(local, remote + "/" + ftpFile[i].getName(), Client.FILE);
								this.read(remote + "/" + ftpFile[i].getName(), file);
							}
						} else {
							System.err.println("unknown file type: " + ftpFile[i].getType());
						}
					}
				}
				
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * create local file or folder to be saved download files or folders
	 * 
	 * @param file local file or folder to be saved download files or folders
	 * @param name remote file path of file or folder to be downloaded from ftp server
	 * 
	 * @return local file or folder to be saved download files or folders
	 */
	private File location(File file, String name, int type) {
		try {
			if (!name.startsWith("/")) name = "/" + name;
			if (!file.exists()) {
				file.mkdirs();
				file = new File(file.getAbsolutePath().replace("\\", "/") + name);
				if (type == Client.DIRECTORY) {
					file.mkdirs();
				} else if (type == Client.FILE) {
					file.createNewFile();
				}
			} else {
				if (file.isDirectory()) {
					file = new File(file.getAbsolutePath().replace("\\", "/") + name);
					if (type == Client.DIRECTORY) {
						file.mkdirs();
					} else if (type == Client.FILE) {
						file.createNewFile();
					}
				} else if (file.isFile()) {
					//file = new File(file.getParent().replace("\\", "/") + name);
				} else {
					System.err.println("unkonwn file type.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
	
	/**
	 * do read remote file content to local file
	 * 
	 * @param remote file path of remote file to be read
	 * @param local local file to be saved remote file content
	 * 
	 * @return  true read success and completed <br>
	 *         false read failure(could be errors)
	 */
	private boolean read(String remote, File local) {
		return this.read(remote, local.getAbsolutePath());
	}
	
	/**
	 * do read remote file content to local file
	 * 
	 * @param remote file path of remote file to be read
	 * @param local file path of local file to be saved remote file content
	 * 
	 * @return  true read success and completed <br>
	 *         false read failure(could be errors)
	 */
	private boolean read(String remote, String local) {
		boolean result = false;
		if (this.login()) {
			try {
				File file = new File(local);
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileOutputStream fos = new FileOutputStream(local);
				ftpClient.retrieveFile(remote, fos);
				fos.close();

				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * do write remote file content to local file
	 * 
	 * @param remote file path of remote file to be saved with local file content
	 * @param local local file to be written
	 * 
	 * @return  true write success and completed <br>
	 *         false write failure(could be errors)
	 */
	private boolean write(String remote, File local) {
		return this.write(remote, local.getAbsolutePath());
	}
	
	/**
	 * do write remote file content to local file
	 * 
	 * @param remote file path of remote file to be saved with local file content
	 * @param local file path of local file to be written
	 * 
	 * @return  true write success and completed <br>
	 *         false write failure(could be errors)
	 */
	private boolean write(String remote, String local) {
		boolean result = false;
		if (this.login()) {
			try {
				FileInputStream fis = new FileInputStream(local);
			    ftpClient.storeFile(remote, fis);
			    fis.close();
			    
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/*public static void main(String[] args) {
		String host = "127.0.0.1";
		String username = "ftp";
		String password = "0p-0p-0p-";
		
		//new Client(host, username, password).file("/a.txt");
		//new Client(host, username, password).read("/input/a.txt", "d:/b.txt");
		//new Client(host, username, password).download("/input", "d:/");
		//new Client(host, username, password).write("/a.txt", "d:\\input\\a\\b.txt");
		//new Client(host, username, password).mkdir("/b/c/d");
		//new Client(host, username, password).upload("/", "d:\\啊啊");
		//new Client(host, username, password).delete("/input");
	}*/
	
}
