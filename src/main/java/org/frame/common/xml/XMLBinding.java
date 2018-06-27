/**
 * XMLBinding contains tools for xml
 */
package org.frame.common.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLBinding {

	private String encoding = "UTF-8";
	
	/**
	 * constructor with default encoding(UTF-8)
	 */
	public XMLBinding() {
		
	}
	
	/**
	 * constructor with specific encoding
	 * 
	 * @param encoding string of specific encoding
	 */
	public XMLBinding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * convert java beans to fragments of xml
	 * 
	 * @param data object[] of java beans to be converted
	 * 
	 * @return string of fragments of xml
	 */
	public String bean2fragment(Object[] data) {
		String result = null;
		try{
			StringWriter stringWriter = new StringWriter();
			for (int i = 0; i < data.length; i++) {
				JAXBContext context = JAXBContext.newInstance(data[i].getClass());
				Marshaller marshaller = context.createMarshaller();

				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

				marshaller.marshal(data[i], stringWriter);
			}
			result = stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * convert java bean to xml
	 * 
	 * @param object java bean to be converted
	 * 
	 * @return string of xml
	 */
	public String bean2xml(Object object) {
		String result = null;
		try{
	        JAXBContext context = JAXBContext.newInstance(object.getClass());
	        Marshaller marshaller = context.createMarshaller();
			
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
	        
	        StringWriter stringWriter = new StringWriter();
	        marshaller.marshal(object, stringWriter);
	        
	        result = stringWriter.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return result;
	}
	
	/**
	 * convert java beans to xml with specific root element
	 * 
	 * @param root string of specific root element content
	 * @param data java beans to be converted
	 * 
	 * @return string of xml
	 */
	public String bean2xml(String root, Object[] data) {
		String result = null;
		try{
			StringWriter stringWriter = new StringWriter();
			for (int i = 0; i < data.length; i++) {
				JAXBContext context = JAXBContext.newInstance(data[i].getClass());
				Marshaller marshaller = context.createMarshaller();

				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

				marshaller.marshal(data[i], stringWriter);
			}
			result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + root + ">" + stringWriter.toString() + "</" + root + ">";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * convert java bean to xml file
	 * 
	 * @param object java bean to be converted
	 * @param file xml file after converted
	 * 
	 * @return  true convert success <br>
	 *         false convert failure(could be errors)
	 */
	public boolean bean2xml(Object object, File file) {
		return this.xml2file(file.getAbsolutePath(), this.bean2xml(object));
	}
	
	/**
	 * convert java bean to xml file
	 * 
	 * @param object java bean to be converted
	 * @param path file path of xml file after converted
	 * 
	 * @return  true convert success <br>
	 *         false convert failure(could be errors)
	 */
	public boolean bean2xml(Object object, String path) {
		return this.xml2file(path, this.bean2xml(object));
	}
	
	/**
	 * convert xml to java bean
	 * 
	 * @param clazz class of java bean after converted
	 * @param content string of xml content to be converted
	 * 
	 * @return instance of java bean
	 */
	public Object xmlContent2bean(Class<?> clazz, String content) {
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			byteArrayInputStream = new ByteArrayInputStream(content.getBytes(encoding));
			XMLStreamReader reader = inputFactory.createXMLStreamReader(byteArrayInputStream);
			return unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (byteArrayInputStream != null) byteArrayInputStream.close();
			} catch (Exception e) {
				byteArrayInputStream = null;
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * convert xml to java bean
	 * 
	 * @param clazz class of java bean after converted
	 * @param file xml file to be converted
	 * 
	 * @return instance of java bean
	 */
	public Object xml2bean(Class<?> clazz, File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return unmarshaller.unmarshal(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * convert xml to java bean
	 * 
	 * @param clazz class of java bean after converted
	 * @param path file path of xml file to be converted
	 * 
	 * @return instance of java bean
	 */
	public Object xml2bean(Class<?> clazz, String path) {
		return this.xml2bean(clazz, new File(path));
	}
	
	/**
	 * convert xml file to org.dom4j.Document
	 * 
	 * @param file xml file to be converted
	 * 
	 * @return instance of org.dom4j.Document
	 */
	public Document xml2document(File file) {
		try {
			SAXReader saxReader = new SAXReader();
			return saxReader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * convert xml content to org.dom4j.Document
	 * 
	 * @param xml string of xml content
	 * 
	 * @return instance of org.dom4j.Document
	 */
	public Document xml2document(String xml) {
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * save xml as file
	 * 
	 * @param file xml file to be saved
	 * @param xml string of xml content
	 * 
	 * @return  true save success <br>
	 *         false save failure(could be errors)
	 */
	public boolean xml2file(File file, String xml) {
		try {
			Document document = DocumentHelper.parseText(xml);
			return this.xml2file(file, document);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * save xml as file
	 * 
	 * @param path file path of xml file to be saved
	 * @param xml string of xml content
	 * 
	 * @return  true save success <br>
	 *         false save failure(could be errors)
	 */
	public boolean xml2file(String path, String xml) {
		return this.xml2file(new File(path), xml);
	}

	/**
	 * save xml as file
	 * 
	 * @param file xml file to be saved
	 * @param document instance of org.dom4j.Document(actual is xml)
	 * 
	 * @return  true save success <br>
	 *         false save failure(could be errors)
	 */
	public boolean xml2file(File file, Document document) {
		boolean result = false;

		XMLWriter xmlWriter = null;
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			OutputFormat outFormat = OutputFormat.createPrettyPrint();
			outFormat.setEncoding(encoding);
			outFormat.setTrimText(false);
			xmlWriter= new XMLWriter(new FileOutputStream(file), outFormat);
			xmlWriter.write(document);

			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(xmlWriter!=null) xmlWriter.close();
			} catch (IOException e) {
				xmlWriter = null;
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * save xml as file
	 * 
	 * @param path file path of file to be saved
	 * @param document instance of org.dom4j.Document(actual is xml)
	 * 
	 * @return  true save success <br>
	 *         false save failure(could be errors)
	 */
	public boolean xml2file(String path, Document document) {
		return this.xml2file(new File(path), document);
	}
	
	/**
	 * read xml file content to string
	 * 
	 * @param file xml file to be read
	 * 
	 * @return string of xml file content
	 */
	public String xml2string(File file) {
		Document document = this.xml2document(file);
		if (document != null) {
			return document.asXML();
		} else {
			return null;
		}
	}
	
	/**
	 * read xml file content to string
	 * 
	 * @param path file path of xml file to be read
	 * 
	 * @return string of xml file content
	 */
	public String xml2string(String path) {
		return this.xml2string(new File(path));
	}
	
	public static void main(String[] args) {
		XMLBinding xmlBinding = new XMLBinding();
		String path = "d:/b.xml";
		String content = xmlBinding.xml2string(path);
        System.out.println(xmlBinding.xml2document(content));
       /* new FileUtil().delete(path);
        System.out.println(new File(path).exists());*/
	}
	
}
