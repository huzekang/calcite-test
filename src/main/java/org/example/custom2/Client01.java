package org.example.custom2;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @program: calcite-test
 * @author: huzekang
 * @create: 2020-02-28 09:49
 **/
public class Client01 {
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("org.apache.calcite.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:calcite:lex=JAVA");
		CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
		SchemaPlus rootSchema = calciteConnection.getRootSchema();
		// HrSchema中包含两个表
		rootSchema.add("hr", new ReflectiveSchema(new HrSchema()));
		// 设置默认schema
		calciteConnection.setSchema("hr");
		final Statement statement = calciteConnection.createStatement();
		final ResultSet resultSet = statement.executeQuery("select d.deptno,min(e.empid) from hr.emps as e join hr.depts as d on e.deptno = d.deptno where  e.deptno =10 group by d.deptno having  count(*) >1");

		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		int lineIndex = 1;
		while (resultSet.next()) {
			for (int i = 1; i < columnCount + 1; i++) {
				final Object value = resultSet.getObject(i);
				System.out.println(String.format("\t 列序号 -》%s,值-》%s,类型-》%s", i, value, metaData.getColumnTypeName(i)));
			}
			lineIndex += 1;
		}
		resultSet.close();
		statement.close();
		connection.close();
	}
}
