package org.example;

import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlMoniker;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorNamespace;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.example.custom.Client;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @describer:执行sql语句，实现sql校验功能
 */
public class ValidateSample {
    /**
     * 解析流程
     *      1.首先生成SQL解析器SqlParser.Config,
     *      SqlParser.Config中存在获取解析工厂类SqlParser.Config#parserFactory()方法,
     *      可以在SqlParser.configBuilder()配置类中设置解析工厂
     *
     *      2.SqlParserImplFactory解析工厂中调用getParser方法获取解析器
     *
     *      3.SqlAbstractParserImpl抽象解析器,JavaCC中生成的解析器的父类,Calcite中默认的解析类名为SqlParserImpl
     *
     * SqlParserImpl中,有静态字段FACTORY,主要是实现SqlParserImplFactory,并创建解析器
     * SqlParser调用create方法,从SqlParser.Config中获取工厂SqlParserImplFactory,并创建解析器
     * 调用SqlParser#parseQuery方法,解析SQL,最终调用SqlAbstractParserImpl(默认实现类SqlParserImpl)的parseSqlStmtEof或者parseSqlExpressionEof方法,获取解析后的抽象语法树SqlNode
     */
    public static void main(String[] args) throws SqlParseException, SQLException, UnsupportedEncodingException {
        URL url = Client.class.getResource("/model2.json");
        String str = URLDecoder.decode(url.toString(), "UTF-8");
        Properties info = new Properties();
        info.put("model", str.replace("file:", ""));
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteServerStatement statement = connection.createStatement().unwrap(CalciteServerStatement.class);
        CalcitePrepare.Context prepareContext = statement.createPrepareContext();

        // 解析配置 - mysql设置
         SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
         // 创建解析器
         SqlParser parser = SqlParser.create("", mysqlConfig);
         // Sql语句
//        String sql = "SELECT s.STORE_NAME as saleName,f.STORE_COST as storeCost,f.STORE_SALES,p.PRODUCT_NAME,c.FNAME,c.LNAME  FROM sales_fact_sample f LEFT JOIN product p ON f.PRODUCT_ID = p.PRODUCT_ID LEFT JOIN customer c ON f.CUSTOMER_ID = c.CUSTOMER_ID LEFT JOIN store s ON f.STORE_ID = s.STORE_ID";
        String sql = "SELECT STORE_ID as saleName  FROM sales_fact_sample f ";
         // 解析sql
         SqlNode sqlNode = parser.parseQuery(sql);
         // 还原某个方言的SQL
         System.out.println("\n方言的SQL ============= \n"+sqlNode.toSqlString(MysqlSqlDialect.DEFAULT));

         // sql validate（会先通过Catalog读取获取相应的metadata和namespace）
         SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
         CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
                 prepareContext.getRootSchema(),
                 prepareContext.getDefaultSchemaPath(),
                 factory,
                 new CalciteConnectionConfigImpl(new Properties()));
        // 校验（包括对表名，字段名，函数名，字段类型的校验。）
         SqlValidator validator = SqlValidatorUtil.newValidator(
                 SqlStdOperatorTable.instance(),
                 calciteCatalogReader,
                 factory,
                 SqlConformanceEnum.DEFAULT );
         // 校验后的SqlNode
         SqlNode validateSqlNode = validator.validate(sqlNode);
         SqlValidatorScope selectScope = validator.getSelectScope((SqlSelect) validateSqlNode);
         SqlValidatorNamespace namespace = validator.getNamespace(sqlNode);
         System.out.println("\nvalidateSqlNode ============= \n"+validateSqlNode);
         List<SqlMoniker> sqlMonikerList = new ArrayList<>();
         selectScope.findAllColumnNames(sqlMonikerList);
         System.out.println("\nselectScope ============= \n"+selectScope);
         for (SqlMoniker sqlMoniker : sqlMonikerList) {
            System.out.println(sqlMoniker.id()+"--->"+sqlMoniker);
         }
         System.out.println("\nnamespace ============= \n"+namespace);
         System.out.println(namespace.fieldExists("nameCC"));

    }

}