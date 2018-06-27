package org.frame.common.office.word.model;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;


@XmlRootElement
public class Word2003 {
	
	private String docName;
	
	private Object[] order;
	
	private List<Paragraph> paragraphs;
	
	private List<Picture> pictures;
	
	private List<Table> tables;

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName.replace(".doc", "");
	}
	
	public Object[] getOrder() {
		return order;
	}

	public void setOrder(Object[] order) {
		this.order = order;
	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}
	
	public List<String> getParagraphs(List<Paragraph> paragraphs) {
		List<String> result = null;
		
		if (paragraphs != null) {
			result = new ArrayList<String>();
			for (Paragraph paragraph : paragraphs) {
				result.add(paragraph.text());
			}
		}
		
		return result;
	}
	
	public List<String> getParagraphsData() {
		return this.getParagraphs(this.getParagraphs());
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = new ArrayList<Paragraph>();
		for (Paragraph paragraph : paragraphs) {
			this.paragraphs.add(paragraph);
		}
	}

	public List<Picture> getPictures() {
		return pictures;
	}
	
	public List<byte[]> getPictures(List<Picture> pictures) {
		List<byte[]> result = null;
		if (pictures != null) {
			result = new ArrayList<byte[]>();
			byte[] bytes;
			for (Picture picture : pictures) {
				bytes = picture.getContent();
				result.add(bytes);
			}
		}
		return result;
	}

	public List<byte[]> getPicturesData() {
		return this.getPictures(this.getPictures());
	}
	
	public void setPictures(List<Picture> pictures) {
		this.pictures = new ArrayList<Picture>();
		for (Picture picture : pictures) {
			this.pictures.add(picture);
		}
	}

	public List<Table> getTables() {
		return tables;
	}
	
	public List<String[][]> getTables(List<Table> tables) {
		List<String[][]> result = null;
		if (tables != null) {
			result = new ArrayList<String[][]>();
			for (Table table : tables) {
				if (table != null) {
					String[][] data = new String[table.numRows()][table.getRow(0).numCells()];

					for (int i = 0; i < table.numRows(); i++) {
						TableRow row = table.getRow(i);
						for (int j = 0; j < row.numCells(); j++) {
							TableCell cell = row.getCell(j);
							for (int k = 0; k < cell.numParagraphs(); k++) {
								Paragraph paragraph = cell.getParagraph(k);
								data[i][j] = paragraph.text();
							}
						}
					}

					result.add(data);
				}
			}
		}
		
		return result;
	}
	
	public List<String[][]> getTablesData() {
		return this.getTables(this.getTables());
	}

	public void setTables(List<Table> tables) {
		this.tables = new ArrayList<Table>();
		for (Table table : tables) {
			this.tables.add(table);
		}
	}
	
	public boolean savePictures(List<Picture> pictures, File file) {
		boolean result = false;

		FileOutputStream fileOutputStream = null;
		try {
			if (file.isFile()) {
				file = file.getParentFile();
			} else {
				file.mkdirs();
			}

			if (pictures != null) {
				int index;
				String name = "";
				Picture picture;
				for (int i = 0; i < pictures.size(); i++) {
					picture = (Picture) pictures.get(i);
					index = picture.getMimeType().indexOf("/");
					if (index != -1) {
						name = picture.getMimeType().substring(index + 1, picture.getMimeType().length());
					}
					fileOutputStream = new FileOutputStream(new File(file.getAbsolutePath() + "/" + this.getDocName() + "/" + i + "." + name));
					picture.writeImageContent(fileOutputStream);
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
	
}
