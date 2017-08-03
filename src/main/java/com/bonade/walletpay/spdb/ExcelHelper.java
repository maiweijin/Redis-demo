package com.bonade.walletpay.spdb;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import com.bonade.walletpay.spdb.ExcelObject.ExcelType;

public interface ExcelHelper<T> {
	public void addColumn(String title, String fieldName);

	public void addColumn(String title, Function<T, String> function);

	public void createExcel(File file, List<T> data);

	public void createExcel(OutputStream out, List<T> data, ExcelType excelType);
}
