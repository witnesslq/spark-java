package org.apache.spark.examples.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.examples.sql.bean.Customer;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;


public class SQLTest {
  public static void main(String[] args) {
//    reflact2DF();
//    type2DF();
    json2DF();
  }
  /**
   * 通过反射的方式创建模式
   */
  public static void reflact2DF(){
    JavaSparkContext sc = new JavaSparkContext("local", "JavaWordCount",
        System.getenv("SPARK_HOME"), JavaSparkContext.jarOfClass(SQLTest.class));
    SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
    
    /**
      100, John Smith, Austin, TX, 78727
      200, Joe Johnson, Dallas, TX, 75201
      300, Bob Jones, Houston, TX, 77028
      400, Andy Davis, San Antonio, TX, 78227
      500, James Williams, Austin, TX, 78727
     */
    
    JavaRDD<Customer> customers = sc.textFile("hdfs://192.168.10.161:8020/liuxing/customers.txt")
        .map(new Function<String, Customer>() {
          private static final long serialVersionUID = 1L;
          public Customer call(String line) throws Exception {
            String[] parts = line.split(",");
            Customer customer = new Customer();
            customer.setCustomer_id(Integer.parseInt(parts[0]));
            customer.setName(parts[1]);
            customer.setCity(parts[2]);
            customer.setState(parts[3]);
            customer.setZip_code(parts[4]);
            return customer;
          }
        });

    // Apply a schema to an RDD of JavaBeans and register it as a table.
    DataFrame df = sqlContext.createDataFrame(customers, Customer.class);
    df.registerTempTable("customers");
    df.show();
    df.printSchema();
    
    df.select("name").show();
    
    df.filter(df.col("customer_id").equalTo(500)).show();
    
    df.groupBy("zip_code").count().show();
  }
  /**
   * 使用新的数据类型类StructType，StringType和StructField指定模式
   */
  public static void type2DF(){
    JavaSparkContext sc = new JavaSparkContext("local", "JavaWordCount",
        System.getenv("SPARK_HOME"), JavaSparkContext.jarOfClass(SQLTest.class));
    SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

    String schemaString = "customer_id name city state zip_code";
    List<StructField> fields = new ArrayList<StructField>();
    for (String fieldName: schemaString.split(" ")) {
      fields.add(DataTypes.createStructField(fieldName, DataTypes.StringType, true));
    }
    StructType schema = DataTypes.createStructType(fields);

    JavaRDD<Row> customers = sc.textFile("hdfs://192.168.10.161:8020/liuxing/customers.txt")
        .map(new Function<String, Row>() {
          private static final long serialVersionUID = 1L;
          public Row call(String line) throws Exception {
            String[] parts = line.split(",");
            return RowFactory.create(parts[0], parts[1],parts[2],parts[3],parts[4]);
          }
        });

    // Apply a schema to an RDD of JavaBeans and register it as a table.
    DataFrame df = sqlContext.createDataFrame(customers, schema);
    df.registerTempTable("customers");
    sqlContext.sql("SELECT name,zip_code FROM customers ORDER BY zip_code").show();
  }
  /**
   * 读取json的
   */
  public static void json2DF() {
    JavaSparkContext sc = new JavaSparkContext("local", "JavaWordCount",
        System.getenv("SPARK_HOME"), JavaSparkContext.jarOfClass(SQLTest.class));
    SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
    
    /**
        {"name":"Michael"}
        {"name":"Andy", "age":30}
        {"name":"Justin", "age":19}
     * 
     */
    
    DataFrame df = sqlContext.read().json("hdfs://192.168.10.161:8020/liuxing/customers.json");
    
    df.show();
    
    df.printSchema();
    
//    df.select("name").write().save("hdfs://192.168.10.161:8020/liuxing/customers_age.json");
    df.select("name").show();
    
    df.select(df.col("name"),df.col("age").plus(23)).show();
    
    df.filter(df.col("age").gt(21)).show();
    
    df.groupBy(df.col("age")).count().show();
    
    sqlContext.sql("SELECT * FROM customers.json union `hdfs://192.168.10.161:8020/liuxing/customers.json`").show();
  }
}
