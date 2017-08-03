package com.bonade.walletpay.spdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.cglib.proxy.Enhancer;

public class ExcelObject<T> implements ExcelHelper<T> {

	enum ExcelType {
		Excel2003, Excel2010
	}
	
	public static final ExcelType Excel2003 = ExcelType.Excel2003;
	public static final ExcelType Excel2010 = ExcelType.Excel2010;

	private List<Column> list = new ArrayList<>();

	private ModelHandler modelHandler;
	
	public void addColumn(String title, Object method) {
		if(method == null){
			list.add(new NullColumn(title));
			return;
		}
		if(Enhancer.isEnhanced(method.getClass())){
			list.add(new ObjectColumn(title, method));
			return;
		}
		list.add(new ObjectColumn(title, method));
	}

	public void addColumn(String title, Function<T, String> function) {
		list.add(new FunctionColumn(title, function));
	}

	public void createExcel(File file, List<T> data) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!file.isFile()) {
			throw new RuntimeException("The file is a directory!");
		}
		String fileName = file.getName();
		try {
			if (fileName.endsWith(".xlsx")) {
				createExcel(new FileOutputStream(file), data, Excel2010);
			} else if (fileName.endsWith(".xls")) {
				createExcel(new FileOutputStream(file), data, Excel2003);
			} else {
				throw new RuntimeException("The file name error!");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createExcel(OutputStream out, List<T> data, ExcelType excelType) {
		Workbook workBook = null;
		switch (excelType) {
		case Excel2003:
			workBook = new HSSFWorkbook();
			break;
		case Excel2010:
			workBook = new XSSFWorkbook();
			break;
		default:
			workBook = new XSSFWorkbook();
			break;
		}
		Font font = workBook.createFont();
		font.setFontHeightInPoints((short) 18);
		font.setFontName("黑体");
		font.setBold(true);
		CellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setFont(font);
		titleStyle.setBorderBottom(BorderStyle.MEDIUM);
		titleStyle.setBorderLeft(BorderStyle.MEDIUM);
		titleStyle.setBorderRight(BorderStyle.MEDIUM);
		titleStyle.setBorderTop(BorderStyle.MEDIUM);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		Sheet sheet1 = workBook.createSheet("sheet1");
		Row createRow = sheet1.createRow(0);
		int col = 0;
		for (Column column : list) {
			Cell cell = createRow.createCell(col);
			cell.setCellValue(column.getTitle());
			cell.setCellStyle(titleStyle);
			col++;
		}
		int row = 1;
		for (T t : data) {
			col = 0;
			Row wbRow = sheet1.createRow(row);
			for (Column column : list) {
				Cell cell = wbRow.createCell(col);
				try {
					ObjectColumn strCol = (ObjectColumn) column;
					Object value = strCol.getValue();
					Method method = modelHandler.getMethod(value);
					Object invoke = method.invoke(t, modelHandler.getArgs(value));
					cell.setCellValue(invoke.toString());
				} catch (ClassCastException e) {
				} catch (IllegalAccessException e) {
				} catch (IllegalArgumentException e) {
				} catch (InvocationTargetException e) {
				}
				try {
					FunctionColumn funCol = (FunctionColumn) column;
					String result = funCol.getFunction().apply(t);
					cell.setCellValue(result==null?"":result);
				} catch (ClassCastException e) {
				}
				try {
					NullColumn nullCol = (NullColumn) column;
					cell.setCellValue("");
				} catch (ClassCastException e) {
				}
				col++;
			}
			row++;
		}
		try {
			workBook.write(out);
		} catch (IOException e) {
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public T getModel(T t) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(t.getClass());
		modelHandler = new ModelHandler();
		enhancer.setCallback(modelHandler);
		enhancer.setClassLoader(this.getClass().getClassLoader());
		return (T) enhancer.create();
	}
	
	abstract class Column {
		private String title;

		public Column(String title) {
			super();
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	class ObjectColumn extends Column {
		private Object value;

		public ObjectColumn(String title, Object value) {
			super(title);
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	class FunctionColumn extends Column {
		private Function<T, String> function;

		public FunctionColumn(String title, Function<T, String> function) {
			super(title);
			this.function = function;
		}

		public Function<T, String> getFunction() {
			return function;
		}

		public void setFunction(Function<T, String> function) {
			this.function = function;
		}
	}

	class NullColumn extends Column{
		public NullColumn(String title) {
			super(title);
		}
	}
}
