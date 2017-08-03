package com.bonade.walletpay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.bonade.walletpay.spdb.ExcelHelper;
import com.bonade.walletpay.spdb.ExcelObject;

public class ExcelTest {
	public static void main(String[] args) throws FileNotFoundException {
		ExcelHelper<String> create = new ExcelObject<>();
		List<String> string = new ArrayList<>();
		string.add("qwe");
		string.add("asd");
		string.add("zxc");
		create.addColumn("列1", (str) -> str + 1);
		create.addColumn("列2", (str) -> str + 2);
		create.createExcel(new File("e://test.xls"), string);
	}
}
