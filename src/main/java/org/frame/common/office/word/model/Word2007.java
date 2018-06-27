package org.frame.common.office.word.model;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

@XmlRootElement
public class Word2007 {
	
	private String docName;
	
	private Object[] order;
	
	private List<XWPFParagraph> paragraphs;
	
	private List<XWPFPictureData> pictures;
	
	private List<XWPFTable> tables;
	
	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName.replace(".docx", "");
	}

	public Object[] getOrder() {
		return order;
	}

	public void setOrder(Object[] order) {
		this.order = order;
	}

	public List<XWPFParagraph> getParagraphs() {
		return paragraphs;
	}
	
	public List<String> getParagraphs(List<XWPFParagraph> paragraphs) {
		List<String> result = null;
		
		if (paragraphs != null) {
			result = new ArrayList<String>();
			for (XWPFParagraph paragraph : paragraphs) {
				result.add(paragraph.getText());
			}
		}
		
		return result;
	}
	
	public List<String> getParagraphsData() {
		return this.getParagraphs(this.getParagraphs());
	}

	public void setParagraphs(List<XWPFParagraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public List<XWPFPictureData> getPictures() {
		return pictures;
	}
	
	public List<byte[]> getPictures(List<XWPFPictureData> pictures) {
		List<byte[]> result = null;
		if (pictures != null) {
			result = new ArrayList<byte[]>();
			byte[] bytes;
			for (XWPFPictureData picture : pictures) {
				bytes = picture.getData();
				result.add(bytes);
			}
		}
		return result;
	}

	public List<byte[]> getPicturesData() {
		return this.getPictures(this.getPictures());
	}
	
	public void setPictures(List<XWPFPictureData> pictures) {
		this.pictures = pictures;
	}
	
	public List<XWPFTable> getTables() {
		return tables;
	}

	public List<String[][]> getTables(List<XWPFTable> tables) {
		List<String[][]> result = null;
		if (tables != null) {
			result = new ArrayList<String[][]>();
			for (XWPFTable table : tables) {
				if (table != null) {
					String[][] data = new String[table.getRows().size()][table.getRows().get(0).getTableCells().size()];
					
					for (int i = 0; i < table.getRows().size(); i++) {
			            XWPFTableRow row = table.getRows().get(i);
			            for (int j = 0; j < row.getTableCells().size(); j++) {
			                XWPFTableCell cell = row.getTableCells().get(j);
			                data[i][j] = cell.getText();
			            }
			        }
				}
			}
		}
        
		return result;
	}
	
	public List<String[][]> getTablesData() {
		return this.getTables(this.getTables());
	}
	
	public void setTables(List<XWPFTable> tables) {
		this.tables = tables;
	}
	
	public boolean savePictures(List<XWPFPictureData> pictures, File file) {
		boolean result = false;

		FileOutputStream fileOutputStream = null;
		try {
			if (file.isFile()) {
				file = file.getParentFile();
			} else {
				file.mkdirs();
			}

			if (pictures != null) {
				XWPFPictureData picture;
				for (int i = 0; i < pictures.size(); i++) {
					picture = (XWPFPictureData) pictures.get(i);
					fileOutputStream = new FileOutputStream(new File(file.getAbsolutePath() + "/" + this.getDocName() + "/" + picture.getFileName()));
					fileOutputStream.write(picture.getData());
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
