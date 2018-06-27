/**
 * certificate for security
 * 
 * generate a keystore: keytool -genkey -keystore chinajavaworld.keystore -alias chinajavaworld
 * generate a certificate: keytool -export -keystore chinajavaworld.keystore -alias chinajavaworld -file chinajavaworld.cer
 * list alias in a keystore: keytool -list -v -alias chinajavaworld -keystore cacerts
 * import certificate to keysotre: keytool -import -alias chinajavaworld -file chinajavaworld.cer -keystore cacerts -truestcacerts
 * delete alias in keystore: keytool -delete -alias chinajavaworld -keystore cacerts
 * 
 * cacerts path: %JAVA_HOME%/jre/lib/secureiy/cacerts
 * cacerts default password: changeit
 */
package org.frame.common.security;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.frame.common.constant.ICommonConstant;
import org.frame.common.network.NetState;
import org.frame.common.util.Properties;

public class Certificate {
	
	private final String type = new Coder().decode(new Coder().base64Decoder("Pj8n"));
	
	
	/**
	 * judge this software is authorized or not
	 * 
	 * @return  true authorized<br>
	 *         false not authorized
	 */
	public boolean authorization() {
		boolean result = false;
		
		try {
			Coder coder = new Coder();
			
			String alias, keystore, password;
			
			if (Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties") != null) {
				Properties properties = new Properties(ICommonConstant.DEFAULT_CONFIG_PROPERTIES);
				
				alias = properties.read("a");
				keystore = properties.read("c");
				password = properties.read("p");
			} else {
				alias = null;
				keystore = null;
				password = null;
			}
			
			if (keystore != null && !"".equals(keystore) && !"null".equals(keystore)) {
				keystore = keystore.substring(1);
				keystore = coder.decode(coder.base64Decoder(keystore));
			} else {
				keystore = System.getProperty("java.home").replace("\\", "/") + coder.decode(coder.base64Decoder("WxgdFlsHERcBBh0ADVsXFRcRBgAH"));
			}
			
			if (password == null || "".equals(password) || "null".equals(password)) {
				password = "PFxwVGhMRHQA=";
			}
			
			password = password.substring(1);
			password = coder.decode(coder.base64Decoder(password));
			
			if (alias == null || "".equals(alias) || "null".equals(alias)) {
				alias = "ABBkd";
			}
			
			alias = alias.substring(1);
			alias = coder.decode(coder.base64Decoder(alias));
			
			FileInputStream fileInputStream = new FileInputStream(keystore);
			KeyStore keyStore = KeyStore.getInstance(type);
			keyStore.load(fileInputStream, password.toCharArray());

			X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
			if (certificate != null) {
				Date TimeNow = new Date();
				certificate.checkValidity(TimeNow);
				
				String subject = certificate.getSubjectDN().toString().toLowerCase();
				String[] mac = new NetState().mac("");
				for (String address : mac) {
					if (subject.contains(address.toLowerCase())) {
						result = true;
						break;
					}
				}
			}
		} catch (CertificateExpiredException e) {
			System.err.println("certificate is overtime.");
			System.exit(0);
		} catch (CertificateNotYetValidException e) {
			System.err.println("certificate is not in effect yet.");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("authorized error.");
			System.exit(0);
		}
		
		return result;
	}
	
	public void verifyCert() {
		try {
			//前提：将证书库中的一条证书导出到证书文件(我写的例子里证书文件叫TC.cer)
			//从证书文件TC.cer里读取证书信息||| 
			/*CertificateFactory cf = CertificateFactory.getInstance("X.509");
			FileInputStream in = new FileInputStream("C:/TC.cer");
			//将文件以文件流的形式读入证书类Certificate中
			Certificate c = cf.generateCertificate(in);
			System.err.println("转换成String后的证书信息："+c.toString());*/
			//或者不用上面代码的方法，直接从证书库中读取证书信息，和上面的结果一摸一样
			
			String pass = "changeit";
			String cert = "C:/Program Files/Java/jdk1.7.0_03/jre/lib/security/cacerts";
			String alias = "chinajavaworld"; //alias为条目的别名
			
			FileInputStream fileInputStream = new FileInputStream(cert);
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(fileInputStream, pass.toCharArray());
			java.security.cert.Certificate c = keyStore.getCertificate(alias);

			//获取获取X509Certificate类型的对象，这是证书类获取Certificate的子类，实现了更多方法
			X509Certificate t=(X509Certificate) c;
			//从信息中提取需要信息
			System.out.println("版本号:"+t.getVersion());
			System.out.println("序列号:"+t.getSerialNumber().toString(16));
			System.out.println("主体名："+t.getSubjectDN());
			System.out.println("签发者："+t.getIssuerDN());
			System.out.println("有效期："+t.getNotBefore());
			System.out.println("签名算法："+t.getSigAlgName());
			//byte[] sig = t.getSignature();//签名值
			PublicKey pk = t.getPublicKey();
			byte[] pkenc = pk.getEncoded();
			System.out.println("公钥：");
			for(int i=0;i<pkenc.length;i++) {
				System.out.print(pkenc[i]+",");
			}
			System.err.println();
			//证书的日期有效性检查，颁发的证书都有一个有效性的日期区间
			Date TimeNow=new Date();
			t.checkValidity(TimeNow);
			System.out.println("证书的日期有效性检查:有效的证书日期！");
			//验证证书签名的有效性，通过数字证书认证中心(CA)机构颁布给客户的CA证书，比如：caroot.crt文件
			//我手里没有CA颁给我的证书，所以下面代码执行不了
			FileInputStream in3=new FileInputStream("d:\\temp\\chinajavaworld.cer");
			//获取CA证书
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			java.security.cert.Certificate cac = cf.generateCertificate(in3);
			//获取CA的公钥
			PublicKey pbk = cac.getPublicKey();
			//c为本地证书，也就是待检验的证书，用CA的公钥校验数字证书c的有效性
			c.verify(pbk);
		} catch(CertificateExpiredException e) {//证书的日期有效性检查:过期
			System.err.println("证书的日期有效性检查:过期");
		} catch(CertificateNotYetValidException e) { //证书的日期有效性检查:尚未生效
			System.err.println("证书的日期有效性检查:尚未生效");
		} catch (CertificateException ce) {
			ce.printStackTrace();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//读出证书方法
	public int readNormal(String certName) {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509"); //载入证书类型
			InputStream is = new FileInputStream(certName);//输入文件
			X509Certificate x509certificate =(X509Certificate) certFactory.generateCertificate(is);//解释输入文件
			/*打印一系列信息*/
			System.out.println( "类型: "+x509certificate.getType());//类型,此处为X.509
			System.out.println( "版本: "+x509certificate.getVersion());//版本
			System.out.println( "标题: "+x509certificate.getSubjectDN().getName());//标题
			System.out.println( "得到开始的有效日期: "+x509certificate.getNotBefore().toString());//得到开始的有效日期
			System.out.println( "得到截止的日期: "+x509certificate.getNotAfter().toString());//得到截止的日期
			System.out.println( "得到序列号: "+x509certificate.getSerialNumber().toString(16));//得到序列号
			System.out.println( "得到发行者名: "+x509certificate.getIssuerDN().getName());//得到发行者名
			System.out.println( "得到签名算法: "+x509certificate.getSigAlgName());//得到签名算法
			System.out.println( "得到公钥算法: "+x509certificate.getPublicKey().getAlgorithm());//得到公钥算法

			is.close();//关闭流
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	//以字符串形式读出证书中得所有信息
	public void readBin(String certName) {
		try {
			InputStream is = new FileInputStream(certName);
			DataInputStream dis = new DataInputStream(is);
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			byte[] bytes = new byte[dis.available()];
			dis.readFully(bytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			while(bais.available()>0) {
				X509Certificate cert = (X509Certificate) certFactory.generateCertificate(bais);

				System.out.println(cert.toString());
			}
			is.close();
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 使用自签证书实例
	 */
	public void crypt(byte[] cipherText, String file) {
		try {
			//生成公钥
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56);
			Key key = keyGen.generateKey();

			//生成DES的Cipher
			Cipher cdes =Cipher.getInstance("DES");
			cdes.init(Cipher.ENCRYPT_MODE, key);
			byte[] ct = cdes.doFinal(cipherText);

			//加密后的文件写回磁盘
			FileOutputStream out = new FileOutputStream(file);
			out.write(ct);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 把消息发送给CA进行消息签名,或者说生成数字证书
	 */
	public byte[] signature(byte[] sigText, String file, String pswd, String keyStore, String alias) {
		byte[] result = null;
		char[] kpass;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");

			//keyStore默认为名字为.keyStore得隐藏文件,在用户的主目录下
			BufferedInputStream ksbufin = new BufferedInputStream(new FileInputStream(keyStore));

			//访问keyStore的密码
			kpass = new char[pswd.length()];
			for(int i = 0; i <pswd.length(); i++) {
				kpass[i]= pswd.charAt(i);
			};
			ks.load(ksbufin,kpass);

			//取得CA得私钥来进行数字签名
			PrivateKey priv =(PrivateKey)ks.getKey(alias, kpass);
			Signature rsa = Signature.getInstance("SHA1withDSA");
			rsa.initSign(priv);
			rsa.update(sigText);
			byte[] sig = rsa.sign();
			System.out.println("sig is done");
			
			FileOutputStream out = new FileOutputStream(file);
			out.write(sig);
			out.close();
			
			result = sig;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 接收消息对数字证书进行验证
	 */
	public boolean verifySignature(byte[] updateData, byte[] sigedText, String certName) {
		boolean result = false;
		
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			FileInputStream fin = new FileInputStream(certName);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(fin);

			//通过自签证书获得公钥
			PublicKey pub = cert.getPublicKey();
			System.out.println(pub.toString());
			
			Signature rsa = Signature.getInstance("SHA1withDSA");
			rsa.initVerify(pub);
			rsa.update(updateData);
			//验证
			boolean verifies = rsa.verify(sigedText);
			System.out.println("verified "+ verifies);
			if (verifies) {
				System.out.println("Verify is done!");
			} else {
				System.out.println("verify is not successful");
			}
			
			result = verifies;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		Certificate c = new Certificate();
		c.authorization();
	}
	
}
