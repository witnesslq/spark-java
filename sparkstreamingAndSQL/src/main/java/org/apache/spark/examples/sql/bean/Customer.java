package org.apache.spark.examples.sql.bean;

public class Customer {
  private int customer_id;
  private String name;
  private String  city;
  private String  state;
  private String  zip_code;
  public int getCustomer_id() {
    return customer_id;
  }
  public void setCustomer_id(int customer_id) {
    this.customer_id = customer_id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }
  public String getZip_code() {
    return zip_code;
  }
  public void setZip_code(String zip_code) {
    this.zip_code = zip_code;
  }
}
