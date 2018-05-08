package com.teamtreehouse.publicdataanalyzer.model;

import java.text.DecimalFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Country {

  @Id
  @Column(columnDefinition="VARCHAR(3)")
  private String code;

  @Column(columnDefinition="VARCHAR(32)")
  private String name;


  @Column(columnDefinition = "DECIMAL", precision = 11, scale = 8)
  private Double internetUsers;

  @Column(columnDefinition = "DECIMAL", precision = 11, scale = 8)
  private Double adultLiteracyRate;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getInternetUsers() {
    return internetUsers;
  }

  public void setInternetUsers(Double internetUsers) {
    this.internetUsers = internetUsers;
  }

  public Double getAdultLiteracyRate() {
    return adultLiteracyRate;
  }

  public void setAdultLiteracyRate(Double adultLiteracyRate) {
    this.adultLiteracyRate = adultLiteracyRate;
  }

  public Country(CountryBuilder countryBuilder) {
    this.code = countryBuilder.code;
    this.name = countryBuilder.name;
    this.internetUsers = countryBuilder.internetUsers;
    this.adultLiteracyRate = countryBuilder.adultLiteracy;
  }

  public Country() {
  }

  @Override
  public String toString() {
    return "Country{" +
        "code = " + code  +
        ", name = " + name  +
        ", internetUsers = " + internetUsers  +
        ", adultLiteracyRate = " + adultLiteracyRate +
        '}';
  }

  public static class CountryBuilder{

    private String code;
    private String name;
    private Double internetUsers;
    private Double adultLiteracy;

    public CountryBuilder(String code, String name) {
      this.code = code;
      this.name = name;
    }

    public CountryBuilder withInternetUser(Double internetUsers){
      this.internetUsers = internetUsers;
      return this;
    }

    public CountryBuilder withAdultLiteracy(Double adultLiteracy){
      this.adultLiteracy = adultLiteracy;
      return this;
    }

    public Country build(){
      return new Country(this);
    }
  }
}
