package com.bonade.walletpay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.bonade.walletpay.spdb.ExcelHelper;
import com.bonade.walletpay.spdb.ExcelObject;

public class ExcelTest {
	public static void main(String[] args) throws FileNotFoundException {
		ExcelHelper<TestObject> create = new ExcelObject<>();
		TestObject t = new TestObject();
		List<TestObject> string = new ArrayList<>();
		string.add(t);
		t.setAge(10);
		t.setName("ming");
		TestObject model = create.getModel(t);
		create.addColumn("姓名", model.getName());
		create.addColumn("年齡", model.getAge());
		create.createExcel(new File("e://test.xlsx"), string);
	}
}
