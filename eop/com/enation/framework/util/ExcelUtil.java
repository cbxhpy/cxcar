package com.enation.framework.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * excel工具类
 * 
 * @author kingapex 2010-7-27上午09:55:59
 */
public class ExcelUtil {

	private Workbook wb;
	private POIFSFileSystem fs = null;
	private CellStyle cs;

	public ExcelUtil() {
		wb = new HSSFWorkbook();
	}
	
	public void openModal(InputStream in) {
		try {
			fs = new POIFSFileSystem(in);
			wb = new HSSFWorkbook(fs);
			cs = wb.createCellStyle();
			cs.setBorderLeft(CellStyle.BORDER_THIN);
	        cs.setBorderRight(CellStyle.BORDER_THIN);
	        cs.setBorderTop(CellStyle.BORDER_THIN);
	        cs.setBorderBottom(CellStyle.BORDER_THIN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean openModal(String modlePath) {
		InputStream In = null;
		FileInputStream File_in = null;

		try {
			fs = new POIFSFileSystem(new FileInputStream(modlePath));
			wb = new HSSFWorkbook(fs);
			return true;
		} catch (IOException e) {
			//System.out.println("------read ::" + modlePath + "-----ERROR!---");
			e.printStackTrace(System.err);
			return false;
		}
	}

	public void writeToFile(String targetFile) {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(targetFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			wb.write(fileOut);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			wb = null;
			try {
				fileOut.flush();
				fileOut.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public void setCellMoney(int numRow, int numCol) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				DataFormat format = wb.createDataFormat();
				Row row = aSheet.getRow((short) numRow);
				Cell csCell = row.getCell((short) numCol);
				CellStyle style = wb.createCellStyle();
				style.setDataFormat(format.getFormat("0.00"));
				style.setBorderBottom(CellStyle.BORDER_THIN);
				style.setBottomBorderColor(HSSFColor.BLACK.index);
				style.setBorderLeft(CellStyle.BORDER_THIN);
				style.setLeftBorderColor(HSSFColor.BLACK.index);
				style.setBorderRight(CellStyle.BORDER_THIN);
				style.setRightBorderColor(HSSFColor.BLACK.index);
				style.setBorderTop(CellStyle.BORDER_THIN);
				style.setTopBorderColor(HSSFColor.BLACK.index);

				csCell.setCellStyle(style);

			}
		} catch (Exception e) {
			//System.out.println("insertDataToExcel" + e);
		}
	}

	public void writeStringToCell(int numRow, int numCol, String strval) {
		try {
			strval = StringUtil.isEmpty(strval) ? "" : strval;
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.getRow((short) numRow);
				if (row == null)
					row = aSheet.createRow(numRow);

				Cell csCell = row.getCell((short) numCol);
				if (csCell == null)
					csCell = row.createCell(numCol);
				// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
		        
				csCell.setCellValue(strval);
				csCell.setCellStyle(cs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// //System.out.println("insertDataToExcel" + e);

		}
	}
	
	public void writeStringToCellAndSheet(int numRow, int numCol, String strval, int sheet) {
		try {
			strval = StringUtil.isEmpty(strval) ? "" : strval;
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(sheet);
				Row row = aSheet.getRow((short) numRow);
				if (row == null)
					row = aSheet.createRow(numRow);

				Cell csCell = row.getCell((short) numCol);
				if (csCell == null)
					csCell = row.createCell(numCol);
				// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				CellStyle cs = wb.createCellStyle();
				cs.setBorderLeft(CellStyle.BORDER_THIN);
		        cs.setBorderRight(CellStyle.BORDER_THIN);
		        cs.setBorderTop(CellStyle.BORDER_THIN);
		        cs.setBorderBottom(CellStyle.BORDER_THIN);
		        
				csCell.setCellValue(strval);
				csCell.setCellStyle(cs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// //System.out.println("insertDataToExcel" + e);

		}
	}
	
	/**
	 * 合并单元格
	 * 
	 * @param cellLine
	 *            要合并的列
	 * @param startRow
	 *            要合并列的开始行
	 * @param endRow
	 *            要合并列的结束行
	 */
	public void addMergedRegion(int cellLine,
			int startRow, int endRow) {
		
		if (wb.getSheetAt(0) != null) {
			Sheet aSheet = wb.getSheetAt(0);
			CellStyle style = wb.createCellStyle(); // 样式对象

			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			// 获取第一行的数据,以便后面进行比较
			String s_will = aSheet.getRow(startRow).getCell(cellLine)
					.getStringCellValue();

			int count = 0;
			for (int i = startRow + 1; i <= endRow; i++) {
				String s_current = aSheet.getRow(i).getCell(cellLine)
						.getStringCellValue();
				if (s_will.equals(s_current)) {
					startRow = i;
					count++;
				} else {
					if (count > 0) {
						aSheet.addMergedRegion(new CellRangeAddress(
								startRow - count, startRow, cellLine, cellLine));
						Row row = aSheet.getRow(startRow - count);
						String cellValueTemp = row.getCell(cellLine)
								.getStringCellValue();
						Cell cell = row.createCell(cellLine);
						cell.setCellValue(cellValueTemp); // 跨单元格显示的数据
						cell.setCellStyle(style); // 样式
						count = 0;
					}
					s_will = s_current;
				}
				// 由于上面循环中合并的单元放在有下一次相同单元格的时候做的，所以最后如果几行有相同单元格则要运行下面的合并单元格。
				if (i == endRow && count > 0) {
					aSheet.addMergedRegion(new CellRangeAddress(endRow - count,
							endRow, cellLine, cellLine));
					String cellValueTemp = aSheet.getRow(endRow - count)
							.getCell(cellLine).getStringCellValue();
					Row row = aSheet.getRow(endRow - count);
					Cell cell = row.createCell(cellLine);
					cell.setCellValue(cellValueTemp); // 跨单元格显示的数据
					cell.setCellStyle(style); // 样式
				}
			}
		}

		
	}
	
	/**
	 * 设置单元格合并
	 * @param a 起始单元格行
	 * @param b 起始单元格列
	 * @param c 终止单元格行
	 * @param d 终止单元格列
	 */
	public void mergeCell(int a,int b,int c,int d) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				CellRangeAddress cellRange = new CellRangeAddress(a, (short)b, c, (short)d);
				aSheet.addMergedRegion(cellRange);
				
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void insertStringToCell(int numRow, int numCol, String strval) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.createRow((short) numRow);
				Cell csCell = row.createCell((short) numCol);
				// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(strval);

			}
		} catch (Exception e) {

			//System.out.println("insertDataToExcel" + e);

		}
	}

	public void writeFormula(int numRow, int numCol, String formula) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.getRow((short) numRow);
				Cell csCell = row.getCell((short) numCol);
				csCell.setCellFormula(formula);
			}
		} catch (Exception e) {
			//System.out.println("insertDataToExcel" + e);
		}
	}

	public void insertFormula(int numRow, int numCol, String formula) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.createRow((short) numRow);
				Cell csCell = row.createCell((short) numCol);
				csCell.setCellFormula(formula);
			}
		} catch (Exception e) {
			//System.out.println("insertDataToExcel" + e);
		}
	}

	public void writeNumToCell(int numRow, int numCol, Double num) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.getRow((short) numRow);
				Cell csCell = row.getCell((short) numCol);
				csCell.setCellValue(num.doubleValue());
			}
		} catch (Exception e) {

			//System.out.println("insertDataToExcel" + e);

		}
	}

	public void insertNumToCell(int numRow, int numCol, Double num) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.createRow((short) numRow);
				Cell csCell = row.createCell((short) numCol);
				csCell.setCellValue(num.doubleValue());
			}
		} catch (Exception e) {
			//System.out.println("insertDataToExcel" + e);
		}
	}

	public void replaceDataToCell(int numRow, int numCol, String temstr, String strval) {
		try {
			if (wb.getSheetAt(0) != null) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.getRow((short) numRow);
				Cell csCell = row.getCell((short) numCol);
				String temp = "";
				temp = csCell.getStringCellValue();
				temp = temp.replaceAll(temstr, strval);
				csCell.setCellValue(temp);
			}
		} catch (Exception e) {

			//System.out.println("insertDataToExcel" + e);

		}
	}

	public void insertDataToExcel(int numRow, Object[] object) {
		try {
			if (null != wb.getSheetAt(0)) {
				Sheet aSheet = wb.getSheetAt(0);
				Row row = aSheet.getRow((short) numRow);

				if (row == null)
					row = aSheet.createRow((short) numRow);

				for (int i = 0; i < object.length; i++) {
					Cell csCell = row.createCell((short) i);

					CellStyle style = wb.createCellStyle();
					style.setBorderBottom(CellStyle.BORDER_THIN);
					style.setBottomBorderColor(HSSFColor.BLACK.index);
					style.setBorderLeft(CellStyle.BORDER_THIN);
					style.setLeftBorderColor(HSSFColor.BLACK.index);
					style.setBorderRight(CellStyle.BORDER_THIN);
					style.setRightBorderColor(HSSFColor.BLACK.index);
					style.setBorderTop(CellStyle.BORDER_THIN);
					style.setTopBorderColor(HSSFColor.BLACK.index);

					csCell.setCellStyle(style);
					if (object[i] != null)
						csCell.setCellValue(object[i].toString());
					else
						csCell.setCellValue("0");
				}
			}
		} catch (Exception e) {
			//System.out.println("insertDataToExcel" + e);
		}
	}

	public int getExcelLastCellNum() {
		int count = 0;
		if (null != wb.getSheetAt(0)) {
			Sheet aSheet = wb.getSheetAt(0);
			Row aRow = aSheet.getRow(0);
			count = aRow.getLastCellNum();
		}
		return count;
	}

	public String getOutputPath(String str) {
		String temp = "";
		if (str.lastIndexOf("/") != -1) {
			temp = str.substring(0, str.lastIndexOf("/"));
		} else if (str.lastIndexOf("\\") != -1) {
			temp = str.substring(0, str.lastIndexOf("\\"));
		} else if (str.lastIndexOf("\\\\") != -1) {
			temp = str.substring(0, str.lastIndexOf("\\\\"));
		} else {
			temp = str;
		}
		return temp;
	}

	public Row getRow(int sheetIndex, int rowIndex) {
		Sheet sheet = wb.getSheetAt(sheetIndex);
		return sheet.getRow(rowIndex);
	}

	public Sheet getSheet(int sheetIndex) {
		return wb.getSheetAt(sheetIndex);
	}

	public Sheet getSheet(String sheetName) {
		return wb.getSheet(sheetName);
	}
	
	public static Object getCellValue(Cell cell) {
		if (cell == null)
			return null;
		int celltype = cell.getCellType();

		if (celltype == Cell.CELL_TYPE_NUMERIC)
			return cell.getNumericCellValue();

		if (celltype == Cell.CELL_TYPE_STRING) {
			String value = cell.getStringCellValue();
			if ("null".equals(value)) {
				value = "";
			}
			if (!StringUtil.isEmpty(value)) {
				value = value.replaceAll(" ", "");
				value = value.replaceAll("  ", "");
			}
			return value;
		}

		if (celltype == Cell.CELL_TYPE_FORMULA) {
			String value = cell.getStringCellValue();
			if ("null".equals(value)) {
				value = "";
			}
			return value;
		}

		if (celltype == Cell.CELL_TYPE_BLANK)
			return "";

		if (celltype == Cell.CELL_TYPE_ERROR)
			return "";

		return "";
	}

}