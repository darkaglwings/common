/**
 * PDF contains tools for adobe reader
 */
package org.frame.common.office.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.frame.common.path.Path;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDF {
	
	private static String TEXT = "text";

	private static String PICTURE = "picture";
	
	private String password;
	
	/**
	 * constructor
	 */
	public PDF() {
		
	}
	
	/**
	 * constructor for encrypted pdf
	 * 
	 * @param password pdf password
	 */
	public PDF(String password) {
		this.password = password;
	}
	
	/**
	 * read pdf content to map
	 * 
	 * @param path file path of pdf file
	 * 
	 * @return map of pdf content
	 */
	public Map<String, Object> read(String path) {
		return this.read(new File(path));
	}
	
	/**
	 * read pdf content to map
	 * 
	 * @param file pdf file
	 * 
	 * @return map of pdf content
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> read(File file) {
		Map<String, Object> result = null;
		
		if (file.isFile() && file.getName().endsWith(".pdf")) {
			result = new HashMap<String, Object>();
			PDDocument pdDocument= null;
			try {
				pdDocument = PDDocument.load(file);
				if (pdDocument.isEncrypted() && password != null) pdDocument.decrypt(password);
				
				PDFTextStripper stripper = new PDFTextStripper();
				result.put(PDF.TEXT, stripper.getText(pdDocument));
				
				PDDocumentCatalog pdDocumentCatalog = pdDocument.getDocumentCatalog();
				List<?> pages = pdDocumentCatalog.getAllPages();
				PDPage page;
				
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);;
				for (int i = 0; i < pages.size(); i++) {
					page = (PDPage) pages.get(i);
					if(page != null) {
						Map<String, PDXObjectImage> images = page.findResources().getImages();
	                  
	                    if(images != null) {
	                    	List<byte[]> pictures = new ArrayList<byte[]>();
	                        Iterator<String> iter = images.keySet().iterator();
	                        while(iter.hasNext()) {
	                        	byteArrayOutputStream.flush();
	                            PDXObjectImage image = (PDXObjectImage) images.get(iter.next());
	                            image.write2OutputStream(byteArrayOutputStream);
	                            pictures.add(byteArrayOutputStream.toByteArray());
	                        }
	                        
	                        result.put(PDF.PICTURE, pictures);
	                    }
	                }
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (pdDocument != null) pdDocument.close();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * replace pdf content with map
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param path file path of pdf file
	 * 
	 * @return true replace success <br>
	 *         false replace failure(could be errors)
	 */
	public boolean replace(Map<String, String> map, String path) {
		return this.replace(map, new File(path));
	}
	
	/**
	 * replace pdf content with map
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param file pdf file
	 * 
	 * @return true replace success <br>
	 *         false replace failure(could be errors)
	 */
	@SuppressWarnings("unchecked")
	public boolean replace(Map<String, String> map, File file) {
		boolean result = true;
		
		if (file.isFile() && file.getName().endsWith(".pdf")) {
			if (map != null) {
				PDDocument pdDocument= null;
				try {
					pdDocument = PDDocument.load(file);
					if (pdDocument.isEncrypted() && password != null) pdDocument.decrypt(password);
					Map<String, Object> origin = this.read(file);
					String content = (String) origin.get(PDF.TEXT);
					List<byte[]> pictures = (List<byte[]>) origin.get(PDF.PICTURE);
					Object[] object = new Object[pictures.size() + 1];
					String key;
					if (content != null) {
						for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
							key = (String) iterator.next();
							content = content.replaceAll(key, map.get(key));
						}
					}
					
					object[0] = content;
					for (int i = 1; i < object.length; i++) {
						object[i] = pictures.get(i - 1);
					}
					
					result = this.write(object, file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * write data to pdf
	 * 
	 * @param content object[] data to write
	 * @param path file path of pdf file to be written
	 * 
	 * @return true replace success <br>
	 *         false replace failure(could be errors)
	 */
	public boolean write(Object[] content, String path) {
		return this.write(content, new File(path));
	}
	
	/**
	 * write data to pdf
	 * 
	 * @param content object[] data to write
	 * @param file pdf file to be written
	 * 
	 * @return true replace success <br>
	 *         false replace failure(could be errors)
	 */
	public boolean write(Object[] content, File file) {
		boolean result = false;

		Document document = null;
		FileOutputStream fileOutputStream = null;

		try {
			if (file.isDirectory()) {
				file = new File(file.getAbsolutePath() + "/new Adobe Reader.pdf");
			} else if (file.isFile()) {
				if (file.getName().endsWith(".pdf")) {

				} else {
					file = new File(file.getParent() + "/new Adobe Reader.pdf");
				}
			} else {
				if (file.getName().endsWith(".pdf")) {
					file.createNewFile();
				} else {
					file.mkdirs();
					file = new File(file.getAbsolutePath() + "/new Adobe Reader.pdf");
				}
			}

			if (content != null) {
				fileOutputStream = new FileOutputStream(file);

				document = new Document();
				PdfWriter.getInstance(document, fileOutputStream);
				document.open();

				Paragraph paragraph = null;
				PdfPTable table;
				Image image;
				String[][] data;
				int column;
				for (int i = 0; i < content.length; i++) {
					if (content[i] != null) {
						if (content[i] instanceof StringBuffer) {
							paragraph = new Paragraph();
							paragraph.setFont(this.getZNFont(11, true));
							paragraph.add(((StringBuffer) content[i]).toString());
							paragraph.setAlignment(Paragraph.ALIGN_CENTER);
							document.add(paragraph);
						} else if (content[i] instanceof String) {
							paragraph = new Paragraph();
							paragraph.setFont(this.getZNFont());
							paragraph.add((String) content[i]);
							document.add(paragraph);
						} else if (content[i] instanceof String[][]) {
							data = (String[][]) content[i];
							if (data.length > 0)
								column = data[0].length;
							else
								column = 0;
							table = new PdfPTable(column);
							for(int j = 0; j < data.length; j++) {
								for (int k = 0; k < data[j].length; k++) {
									paragraph = new Paragraph();
									paragraph.setFont(this.getZNFont());
									paragraph.add(data[j][k]);
									table.addCell(paragraph);
								}
								table.completeRow();
							}

							document.add(table);
						} else if (content[i] instanceof byte[]) {
							image = Image.getInstance((byte[]) content[i]);
							image.setAlignment(Image.ALIGN_CENTER);
							image.scaleToFit(480, 320);
							document.add(image);  
						}
					}
				}

				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (document != null && document.isOpen()) document.close();
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (Exception exception) {
				fileOutputStream = null;
				exception.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * to get default Chinese font
	 * 
	 * @return default font
	 */
	protected Font getZNFont() {
		return this.getZNFont(9, false);
	}
	
	/**
	 * to get Chinese font
	 * 
	 * @param nfontsize font size
	 * @param isBold  true use bold characters <br>
	 *               false use normal characters
	 *               
	 * @return specific font
	 */
	protected Font getZNFont(int nfontsize, boolean isBold) {
		BaseFont bfChinese;
		Font fontChinese = null;
		try {
			bfChinese = BaseFont.createFont(new Path().resource("com/frame/common/resource/font") + "/simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			if (isBold) {
				fontChinese = new Font(bfChinese, nfontsize, Font.BOLD);
			} else {
				fontChinese = new Font(bfChinese, nfontsize, Font.NORMAL);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fontChinese;
	}
	
}
