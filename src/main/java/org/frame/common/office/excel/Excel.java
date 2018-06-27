/**
 * Excel contains tools for MicroSoft Office Excel
 */
package org.frame.common.office.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	
	public static final String EXCEL_2003 = "excel_2003";
	
	public static final String EXCEL_2007 = "excel_2007";
	
	private String excel;
	
	/**
	 * constructor with default excel type(excel2007)
	 */
	public Excel() {
		this.excel = EXCEL_2007;
	}
	
	/**
	 * constructor with specific excel type
	 * 
	 * @param excel specific excel type(Excel.EXCEL_2003 or Excel.EXCEL_2007) <br>
	 *              if excel type neither Excel.EXCEL_2003 nor Excel.EXCEL_2007 then use default excel type
	 */
	public Excel(String excel) {
		this.excel = excel;
		
		if (!EXCEL_2003.equals(this.excel) && !EXCEL_2007.equals(this.excel))
			this.excel = EXCEL_2007;
	}
	
	/**
	 * read excel content(first sheet)
	 * 
	 * @param path file path of excel file to be read
	 * 
	 * @return object[][] data of excel file content
	 */
	public Object[][] read(String path) {
		return this.read(new File(path));
	}
	
	/**
	 * read excel content
	 * 
	 * @param path file path of excel file to be read
	 * @param sheet number of excel sheet(start with 0)
	 * 
	 * @return object[][] data of excel file content
	 */
	public Object[][] read(String path, int sheet) {
		return this.read(new File(path), sheet);
	}
	
	/**
	 * read excel content(first sheet)
	 * 
	 * @param file excel file to be read
	 * 
	 * @return object[][] data of excel file content
	 */
	public Object[][] read(File file) {
		return this.read(file, 0);
	}
	
	/**
	 * read excel content
	 * 
	 * @param file excel file to be read
	 * @param sheet index of excel sheet(start with 0)
	 * 
	 * @return object[][] data of excel file content
	 */
	public Object[][] read(File file, int sheet) {
		if (file.exists()) {
			String fileName = file.getName();
			String fileType = "";
			if (fileName.lastIndexOf(".") != -1) {
				fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			} else {
				System.err.println("unknown file type.");
			}
			
			if("xls".equals(fileType)) {
				return this.format(read2003Excel(file));
			} else if("xlsx".equals(fileType)) {
				return this.format(read2007Excel(file));
			} else {
				System.err.println("unknown file type: " + fileType);
			}
		} else {
			System.err.println("file not found. file: " + file.getName());
		}
		
		return null;
	}
	
	/**
	 * do read excel2003 content(first sheet)
	 * 
	 * @param file excel file to be read
	 * 
	 * @return object[] data(every element of object[] is a object[] contains data of one row in excel)
	 */
	private Object[] read2003Excel(File file) {
		return this.read2003Excel(file, 0);
	}
	
	/**
	 * do read excel2003 content(first sheet)
	 * 
	 * @param file excel file to be read
	 * @param index index of excel sheet(start with 0)
	 * 
	 * @return object[] data(every element of object[] is a object[] contains data of one row in excel)
	 */
	private Object[] read2003Excel(File file, int index) {
		Object[] result = null;
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = workbook.getSheetAt(index);
			result = new Object[sheet.getPhysicalNumberOfRows()];
			Object value = null;
			HSSFRow row = null;
			HSSFCell cell = null;
			for(int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				if (row != null) {
					Object[] data = new Object[row.getLastCellNum()];
					for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
						cell = row.getCell(j);
						if (cell != null) {
							DecimalFormat df = new DecimalFormat("0");
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							DecimalFormat nf = new DecimalFormat("0.00");
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if ("@".equals(cell.getCellStyle().getDataFormatString())) {
									value = df.format(cell.getNumericCellValue());
								} else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
									value = nf.format(cell.getNumericCellValue());
								} else {
									value = simpleDateFormat.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
								}
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								value = cell.getBooleanCellValue();
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = "";
								break;
							default:  
								value = cell.toString();
							}

							data[j] = value;
						} else {
							data[j] = null;
							continue;
						}
						   
					}
					result[i] = data;
				} else {
					result[i] = null;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) workbook.close();
			} catch (IOException e) {
				workbook = null;
				e.printStackTrace();
			}
		}
		
		return result;  
	}
	
	/**
	 * do read excel2007 content(first sheet)
	 * 
	 * @param file excel file to be read
	 * 
	 * @return object[] data(every element of object[] is a object[] contains data of one row in excel)
	 */
	private Object[] read2007Excel(File file) {
		return this.read2007Excel(file, 0);
	}
	
	/**
	 * do read excel2007 content(first sheet)
	 * 
	 * @param file excel file to be read
	 * @param index index of excel sheet(start with 0)
	 * 
	 * @return object[] data(every element of object[] is a object[] contains data of one row in excel)
	 */
	private Object[] read2007Excel(File file, int index) {
		Object[] result = null;
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(file));
			XSSFSheet sheet = workbook.getSheetAt(index);
			result = new Object[sheet.getPhysicalNumberOfRows()];
			Object value = null;
			XSSFRow row = null;
			XSSFCell cell = null;
			for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				if (row != null) {
					Object[] data = new Object[row.getLastCellNum()];
					for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
						cell = row.getCell(j);
						if (cell != null) {
							DecimalFormat df = new DecimalFormat("0");
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							DecimalFormat nf = new DecimalFormat("0.00");
							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case XSSFCell.CELL_TYPE_NUMERIC:
								if("@".equals(cell.getCellStyle().getDataFormatString())) {
									value = df.format(cell.getNumericCellValue());
								} else if("General".equals(cell.getCellStyle().getDataFormatString())) {
									value = nf.format(cell.getNumericCellValue());
								} else {
									value = simpleDateFormat.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
								}
								break;
							case XSSFCell.CELL_TYPE_BOOLEAN:
								value = cell.getBooleanCellValue();
								break;
							case XSSFCell.CELL_TYPE_BLANK:
								value = "";
								break;
							default:
								value = cell.toString();
							}
							
							data[j] = value;
						} else {
							data[j] = null;
							continue;
						}
					}
					result[i] = data;
				} else {
					result[i] = null;
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) workbook.close();
			} catch (IOException e) {
				workbook = null;
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * write data to default excel file(first sheet)
	 * 
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(Object[][] data) {
		if (EXCEL_2003.equals(this.excel))
			return this.write2003Excel(data);
		else if (EXCEL_2007.equals(this.excel))
			return this.write2007Excel(data);
		else
			return true;
	}
	
	/**
	 * write data to excel file(first sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(String path, Object[][] data) {
		return this.write(path, 0, data);
	}
	
	/**
	 * write data to excel file
	 * 
	 * @param file excel file to be written
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(File file, Object[][] data) {
		return this.write(file, 0, data);
	}
	
	/**
	 * write data to excel file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(String path, int sheet, Object[][] data) {
		return this.write(path, sheet, "sheet" + sheet, data);
	}
	
	/**
	 * write data to excel file(specific sheet)
	 * 
	 * @param file excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(File file, int sheet, Object[][] data) {
		return this.write(file, sheet, "sheet" + sheet, data);
	}
	
	/**
	 * write data to excel file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param sheetName name of specific sheet
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(String path, int sheet, String sheetName, Object[][] data) {
		if (path.endsWith(".xls"))
			return this.write2003Excel(path, sheet, sheetName, data);
		else if (path.endsWith(".xlsx"))
			return this.write2007Excel(path, sheet, sheetName, data);
		else
			return this.write2003Excel(path + "/new excel.xls", sheet, sheetName, data);
	}
	
	/**
	 * write data to excel file(specific sheet)
	 * 
	 * @param file excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param sheetName name of specific sheet
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	public boolean write(File file, int sheet, String sheetName, Object[][] data) {
		return this.write(file.getAbsolutePath(), sheet, "sheet" + sheet, data);
	}
	
	/**
	 * do write data to excel2003 file(first sheet)
	 * 
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2003Excel(Object[][] data) {
		return this.write2003Excel("new excel.xls", data);
	}
	
	/**
	 * do write data to excel2003 file(first sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2003Excel(String path, Object[][] data) {
		return this.write2003Excel(path, 0, data);
	}
	
	/**
	 * do write data to excel2003 file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2003Excel(String path, int sheet, Object[][] data) {
		return this.write2003Excel(path, sheet, "sheet" + sheet, data);
	}
	
	/**
	 * do write data to excel2003 file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param sheetName name of specific sheet
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2003Excel(String path, int sheet, String sheetName, Object[][] data) {
		boolean result = false;
		
		HSSFWorkbook hssfWorkbook = null;
		if (data != null) {
			try {
				hssfWorkbook = new HSSFWorkbook();
				
				HSSFSheet hssfSheet = hssfWorkbook.createSheet();
				
				hssfWorkbook.setSheetName(sheet, sheetName);
				
				HSSFRow hssfRow = null;
				HSSFCell hssfCell = null;
				
				for (int i = 0; i < data.length; i++) {
					hssfRow = hssfSheet.createRow(i);
					
					for (int j = 0; j < data[i].length; j++) {
						try {
							if (data[i][j] == null) data[i][j] = "";

							hssfCell = hssfRow.createCell(j);

							if (data[i][j] instanceof Boolean)
								hssfCell.setCellValue((Boolean) data[i][j]);
							else if (data[i][j] instanceof Calendar)
								hssfCell.setCellValue((Calendar) data[i][j]);
							else if (data[i][j] instanceof Date) {
								HSSFCellStyle hssfCellStyle = hssfWorkbook.createCellStyle();
								hssfCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
								hssfCell.setCellValue((Date) data[i][j]);
								hssfCell.setCellStyle(hssfCellStyle);
							} else if (data[i][j] instanceof Double)
								hssfCell.setCellValue((Double) data[i][j]);
							else if (data[i][j] instanceof RichTextString)
								hssfCell.setCellValue((RichTextString) data[i][j]);
							else if (data[i][j] instanceof String)
								hssfCell.setCellValue((String) data[i][j]);
							else
								hssfCell.setCellValue(String.valueOf(data[i][j]));
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						
					}
				}
				
				FileOutputStream fileOutputStream = new FileOutputStream(path);
				hssfWorkbook.write(fileOutputStream);
				fileOutputStream.close();
				
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (hssfWorkbook != null) hssfWorkbook.close();
				} catch (IOException e) {
					hssfWorkbook = null;
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * do write data to excel2007 file(first sheet)
	 * 
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2007Excel(Object[][] data) {
		return this.write2007Excel("new excel.xlsx", data);
	}
	
	/**
	 * do write data to excel2007 file(first sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2007Excel(String path, Object[][] data) {
		return this.write2007Excel(path, 0, data);
	}
	
	/**
	 * do write data to excel2007 file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2007Excel(String path, int sheet, Object[][] data) {
		return this.write2007Excel(path, sheet, "sheet" + sheet, data);
	}
	
	/**
	 * do write data to excel2007 file(specific sheet)
	 * 
	 * @param path file path of excel file to be written
	 * @param sheet index of excel sheet(start with 0)
	 * @param sheetName name of specific sheet
	 * @param data object[][] data to write
	 * 
	 * @return  true write success <br>
	 *         false write failure(could be errors)
	 */
	private boolean write2007Excel(String path, int sheet, String sheetName, Object[][] data) {
		boolean result = false;
		
		XSSFWorkbook xssfWorkbook = null;
		if (data != null) {
			try {
				xssfWorkbook = new XSSFWorkbook();
				
				XSSFSheet xssfSheet = xssfWorkbook.createSheet();
				
				xssfWorkbook.setSheetName(sheet, sheetName);
				
				XSSFRow xssfRow = null;
				XSSFCell xssfCell = null;
				
				for (int i = 0; i < data.length; i++) {
					xssfRow = xssfSheet.createRow(i);
					
					for (int j = 0; j < data[i].length; j++) {
						try {
							if (data[i][j] == null) data[i][j] = "";

							xssfCell = xssfRow.createCell(j);

							if (data[i][j] instanceof Boolean)
								xssfCell.setCellValue((Boolean) data[i][j]);
							else if (data[i][j] instanceof Calendar)
								xssfCell.setCellValue((Calendar) data[i][j]);
							else if (data[i][j] instanceof Date) {
								XSSFCellStyle xssfCellStyle = xssfWorkbook.createCellStyle();
								xssfCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
								xssfCell.setCellValue((Date) data[i][j]);
								xssfCell.setCellStyle(xssfCellStyle);
							} else if (data[i][j] instanceof Double)
								xssfCell.setCellValue((Double) data[i][j]);
							else if (data[i][j] instanceof RichTextString)
								xssfCell.setCellValue((RichTextString) data[i][j]);
							else if (data[i][j] instanceof String)
								xssfCell.setCellValue((String) data[i][j]);
							else
								xssfCell.setCellValue(String.valueOf(data[i][j]));
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						
					}
				}
				
				FileOutputStream fileOutputStream = new FileOutputStream(path);
				xssfWorkbook.write(fileOutputStream);
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (xssfWorkbook != null) xssfWorkbook.close();
				} catch (IOException e) {
					xssfWorkbook = null;
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * format result of read excel, make object[] to object[][]
	 * 
	 * @param data result of read excel
	 * 
	 * @return object[][] formatted data from result
	 */
	private Object[][] format(Object[] data) {
		Object[][] result;
		int width = 0;
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null) {
					width = width > ((Object[]) data[i]).length ? width : ((Object[]) data[i]).length;
				}
			}
			
			result = new Object[data.length][width];
			
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null) {
					for (int j = 0; j < width; j++) {
						if (j < ((Object[]) data[i]).length)
							result[i][j] = ((Object[]) data[i])[j];
						else
							result[i][j] = null;
					}
				} else {
					result[i] = null;
				}
			}
		} else {
			result = null;
		}
		return result;
	}
	
}
