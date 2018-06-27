/**
 * Word contains tools for MicroSoft Office Word
 */
package org.frame.common.office.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.frame.common.office.word.model.Word2003;
import org.frame.common.office.word.model.Word2007;
import org.frame.common.office.word.model.XWPFDoc;


public class Word {
	
	private String encoding = "UTF-8";
	
	/**
	 * constructor with default encoding(GB2312)
	 */
	public Word() {
		this.encoding = "GB2312";
	}
	
	/**
	 * constructor with specific encoding
	 * 
	 * @param encoding string of specific encoding
	 */
	public Word(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * read word to object
	 * 
	 * @param path file path of word file
	 * 
	 * @return object Word2003 or Word2007 depends on word file
	 */
	public Object read(String path) {
		return this.read(new File(path));
	}
	
	/**
	 * read word to object
	 * 
	 * @param file word file
	 * 
	 * @return object Word2003 or Word2007 depends on word file
	 */
	public Object read(File file) {
		if (file.getName().endsWith(".doc"))
			return this.read2003Word(file);
		else if (file.getName().endsWith(".docx"))
			return this.read2007Word(file);
		else {
			System.err.println("unsupported file type: " + file.getName());
			return null;
		}
	}
	
	/**
	 * do read word2003 to object
	 * 
	 * @param file word2003 file
	 * 
	 * @return instance of Word2003
	 */
	private Word2003 read2003Word(File file) {
		FileInputStream fileInputStream = null;
		Word2003 word2003 = null;
		Object[] order;
		List<Paragraph> paragraphs;
		List<Picture> pictures;
		List<Table> tables;
		boolean isTable = false;
		try {
			if (file.isFile()) {
				word2003 = new Word2003();
				paragraphs = new ArrayList<Paragraph>();
				pictures = new ArrayList<Picture>();
				tables = new ArrayList<Table>();
				
				fileInputStream = new FileInputStream(file);
				HWPFDocument doc = new HWPFDocument(fileInputStream);
				
				TableIterator tableIterator = new TableIterator(doc.getRange());
				while (tableIterator.hasNext()) {
		            tables.add(tableIterator.next());
				}
				
				for (int i = 0; i < doc.getRange().numParagraphs(); i++) {
					if (tables != null) {
						isTable = false;
						for (Table table : tables) {
							if (doc.getRange().getParagraph(i).getStartOffset() >= table.getStartOffset() && doc.getRange().getParagraph(i).getEndOffset() <= table.getEndOffset()) {
								isTable = true;
								break;
							}
						}
						
						if (!isTable)
							paragraphs.add(doc.getRange().getParagraph(i));
					}
				}
				
				for (Picture picture : doc.getPicturesTable().getAllPictures()) {
					pictures.add(picture);
				}
				
				word2003.setParagraphs(paragraphs);
				word2003.setPictures(pictures);
				word2003.setTables(tables);
				
				order = new Object[paragraphs.size() + pictures.size() + tables.size()];
				Paragraph paragraph;
				Picture picture;
				Table table;
				BigInteger min;
				Object object;
				for (int i = 0; i < order.length; i++) {
					if (paragraphs.size() > 0) 
						paragraph = paragraphs.get(0);
					else 
						paragraph = null;
					
					if (pictures.size() > 0) 
						picture = pictures.get(0);
					else 
						picture = null;
					
					if (tables.size() > 0) 
						table = tables.get(0);
					else 
						table = null;
					
					if (paragraph == null && table == null) {
						min = BigInteger.valueOf(-1);
						object = null;
					} else if (paragraph == null && table != null) {
						min = BigInteger.valueOf(table.getStartOffset());
						object = table;
					} else if (paragraph != null && table == null) {
						min = BigInteger.valueOf(paragraph.getStartOffset());
						object = paragraph;
					} else if (paragraph != null && table != null) {
						object = paragraph.getStartOffset() < table.getStartOffset() ? paragraph : table;
						min = BigInteger.valueOf(paragraph.getStartOffset() < table.getStartOffset() ? paragraph.getStartOffset() : table.getStartOffset());
					} else {
						min = BigInteger.valueOf(-1);
						object = null;
					}
					
					if (object == null && picture == null) {
						min = BigInteger.valueOf(-1);
						object = null;
					} else if (object == null && picture != null) {
						min = BigInteger.valueOf(picture.getStartOffset());
						object = picture;
					} else if (object != null && picture == null) {
						
					} else if (object != null && picture != null) {
						object = min.doubleValue() <= picture.getStartOffset() ? object : picture;
						min = BigInteger.valueOf(min.doubleValue() <= picture.getStartOffset() ? min.longValue() : picture.getStartOffset());
					} else {
						min = BigInteger.valueOf(-1);
						object = null;
					}
					
					order[i] = object;
					if (object instanceof Paragraph) {
						paragraphs.remove(0);
					} else if (object instanceof Picture) {
						pictures.remove(0);
					} else if (object instanceof Table) {
						tables.remove(0);
					}
				}

				word2003.setOrder(order);
				word2003.setDocName(file.getName());
			} else {
				System.err.println("unsupported file type: " + file.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) fileInputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
				fileInputStream = null;
			}
			
		}
		
		return word2003;
	}
	
	/**
	 * do read word2007 to object
	 * 
	 * @param file word2007 file
	 * 
	 * @return instance of Word2007
	 */
	private Word2007 read2007Word(File file) {
		Word2007 word2007 = null;
		Object[] order;
		FileInputStream fileInputStream = null;
		try {
			if (file.isFile()) {
				word2007 = new Word2007();
				
				fileInputStream = new FileInputStream(file);
				XWPFDocument doc = new XWPFDocument(fileInputStream);
				
				List<XWPFParagraph> paragraphs = doc.getParagraphs();
				List<XWPFPictureData> pictures = doc.getAllPictures();
				List<XWPFTable> tables = doc.getTables();
				
				order = new Object[paragraphs.size() + tables.size() + pictures.size()];
				
				for (XWPFParagraph paragraph : paragraphs) {
					order[doc.getPosOfParagraph(paragraph)] = paragraph;
				}
				
				for (XWPFTable table : tables) {
					order[doc.getPosOfTable(table)] = table;
				}
				
				for (XWPFPictureData picture : pictures) {
					for (int i = 0; i < order.length; i++) {
						if (order[i] instanceof XWPFParagraph && "".equals(((XWPFParagraph) order[i]).getText())) {
							order[i] = picture;
							break;
						}
					}
				}
				
				word2007.setParagraphs(doc.getParagraphs());
				word2007.setTables(doc.getTables());
				word2007.setPictures(doc.getAllPictures());
				word2007.setOrder(order);
				word2007.setDocName(file.getName());
			} else {
				System.err.println("unsupported file type: " + file.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) fileInputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
				fileInputStream = null;
			}
			
		}
		
		return word2007;
	}

	/**
	 * replace content in word file
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param path file path of word file
	 * 
	 * @return  true replace success <br>
	 *         false replace failure(could be errors)
	 */
	public boolean replace(Map<String, String> map, String path) {
		return this.replace(map, new File(path));
	}
	
	/**
	 * replace content in word file
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param file word file
	 * 
	 * @return  true replace success <br>
	 *         false replace failure(could be errors)
	 */
	public boolean replace(Map<String, String> map, File file) {
		if (file.getName().endsWith(".doc")) 
			return this.replace2003Word(map, file);
		else if (file.getName().endsWith(".docx")) 
			return this.replace2007Word(map, file);
		else 
			System.err.println("unsupport file type. file: " + file.getName());
		return false;
	}
	
	/**
	 * do replace content in word2003 file
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param file word file
	 * 
	 * @return  true replace success <br>
	 *         false replace failure(could be errors)
	 */
	private boolean replace2003Word(Map<String, String> map, File file) {
		boolean result = false;
		
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			if (file != null && file.exists()) {
				if (map != null) {
					fileInputStream = new FileInputStream(file);
					HWPFDocument doc = new HWPFDocument(fileInputStream);
					
					String key;
					Range range = doc.getRange();
					for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
						key = (String) iterator.next();
						range.replaceText(key, map.get(key));
					}
					
					fileOutputStream = new FileOutputStream(file);
					doc.write(fileOutputStream);
				} else {
					System.err.println("replacing message not found.");
				}
			} else {
				System.err.println("file not found. file: " + file.getName());
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) fileInputStream.close();
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (Exception exception) {
				fileInputStream = null;
				fileOutputStream = null;
				exception.printStackTrace();
			}
			
		}
		
		return result;
	}
	
	/**
	 * do replace content in word2007 file
	 * 
	 * @param map map of data for replace <br>
	 *              key data to be replaced <br>
	 *            value data to replaced <br>
	 * @param file word file
	 * 
	 * @return  true replace success <br>
	 *         false replace failure(could be errors)
	 */
	private boolean replace2007Word(Map<String, String> map, File file) {
		boolean result = false;

		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			if (file != null && file.exists()) {
				if (map != null) {
					fileInputStream = new FileInputStream(file);
					XWPFDocument doc = new XWPFDocument(fileInputStream);

					String key;
					String value;
					for (XWPFParagraph paragraph : doc.getParagraphs()) {
						for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
							key = (String) iterator.next();
							if (paragraph.getText().contains(key)) {
								value = paragraph.getParagraphText().replaceAll(key, map.get(key));
								for (int i = 0; i < paragraph.getRuns().size(); i++) {
									paragraph.removeRun(i);
								}
								paragraph.createRun().setText(value);
							}
						}
					}

					for (XWPFTable table : doc.getTables()) {
						for (XWPFTableRow row : table.getRows()) {
							for (XWPFTableCell cell : row.getTableCells()) {
								for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
									key = (String) iterator.next();
									if (cell.getText().contains(key)) {
										value = cell.getText();
										cell.removeParagraph(0);
										cell.setText(value.replaceAll(key, map.get(key)));
									}
								}
							}
						}
					}

					fileOutputStream = new FileOutputStream(file);
					doc.write(fileOutputStream);
				} else {
					System.err.println("replacing message not found.");
				}
			} else {
				System.err.println("file not found. file: " + file.getName());
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) fileInputStream.close();
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (Exception exception) {
				fileInputStream = null;
				fileOutputStream = null;
				exception.printStackTrace();
			}

		}

		return result;
	}
	
	/**
	 * write data to word file
	 * 
	 * @param content object[] data to write
	 * @param path file path of word file to be written
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(Object[] content, String path) {
		return this.write(content, new File(path));
	}
	
	/**
	 * write data to word file
	 * 
	 * @param content object[] data to write
	 * @param file word file to be written
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(Object[] content, File file) {
		if (file.getName().endsWith(".doc")) 
			return this.write2003Word(content, file);
		else if (file.getName().endsWith(".docx")) 
			return this.write2007Word(content, file);
		else 
			System.err.println("unsupport file type. file: " + file.getName());
		return false;
	}
	
	/**
	 * do write data to word2003 file
	 * 
	 * @param content object[] data to write
	 * @param file word2003 file to be written
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2003Word(Object[] content, File file) {
		boolean result = false;

		FileOutputStream fileOutputStream = null;

		try {
			if (file.isDirectory()) {
				file = new File(file.getAbsolutePath() + "/new document.doc");
			} else if (file.isFile()) {
				if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {

				} else {
					file = new File(file.getParent() + "/new document.doc");
				}
			} else {
				if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
					file.createNewFile();
				} else {
					file.mkdirs();
					file = new File(file.getAbsolutePath() + "/new document.doc");
				}
			}
			
			if (content != null) {
				fileOutputStream = new FileOutputStream(file);
				
				if (content != null) {
					for (int i = 0; i < content.length; i++) {
						if (content[i] != null) {
							if (content[i] instanceof String) {
								fileOutputStream.write(((String) content[i]).getBytes(this.encoding));
							} else if (content[i] instanceof String[][]) {
								//handle table data into document
							} else if (content[i] instanceof byte[]) {
								//handle picture data into document
								//fileOutputStream.write((byte[]) content[i]);
							}
						}
					}
				}
			}
			result = true;
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
	 * do write data to word2007 file
	 * 
	 * @param content object[] data to write
	 * @param file word2007 file to be written
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2007Word(Object[] content, File file) {
		boolean result = false;

		FileOutputStream fileOutputStream = null;

		try {
			if (file.isDirectory()) {
				file = new File(file.getAbsolutePath() + "/new document.docx");
			} else if (file.isFile()) {
				if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {

				} else {
					file = new File(file.getParent() + "/new document.docx");
				}
			} else {
				if (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
					file.createNewFile();
				} else {
					file.mkdirs();
					file = new File(file.getAbsolutePath() + "/new document.docx");
				}
			}
			
			if (content != null) {
				XWPFDoc doc = new XWPFDoc();
				fileOutputStream = new FileOutputStream(file);

				XWPFParagraph paragraph;
				XWPFRun run;
				XWPFTable table;
				XWPFTableRow row;
				XWPFTableCell cell;
				String[][] data;
				int picId = 0;
				for (int i = 0; i < content.length; i++) {
					if (content[i] != null) {
						if (content[i] instanceof StringBuffer) {
							paragraph = doc.createParagraph();
							run = paragraph.createRun();
							run.setText(((StringBuffer) content[i]).toString());
							run.setBold(true);
							run.setFontSize(11);
							paragraph.setAlignment(ParagraphAlignment.CENTER);
						} else if (content[i] instanceof String) {
							paragraph = doc.createParagraph();
							run = paragraph.createRun();
							run.setText((String) content[i]);
						} else if (content[i] instanceof String[][]) {
							data = (String[][]) content[i];
							table = doc.createTable();
							for (int j = 0; j < data.length; j++) {
								row = table.createRow();
								for (int k = 0; k < data[j].length; k++) {
									cell = row.createCell();
									cell.setText(data[j][k]);
								}
							}
						} else if (content[i] instanceof byte[]) {
							//Here has a bug in poi3.8, picture has added in document, but it can be displayed in doc.
							doc.addPictureData((byte[]) content[i], XWPFDocument.PICTURE_TYPE_JPEG);
							doc.createPicture(picId++, 640, 480);
						} else if (content[i] instanceof InputStream) {
							//Here has a bug in poi3.8, picture has added in document, but it can be displayed in doc.
							doc.addPictureData((InputStream) content[i], XWPFDocument.PICTURE_TYPE_JPEG);
							doc.createPicture(picId++, 640, 480);
						}
					}
				}
				doc.write(fileOutputStream);
			}
			result = true;
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
	
}
