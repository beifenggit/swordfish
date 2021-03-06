package com.baifendian.swordfish.common.job.struct.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgreDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(PostgreDatasource.class.getName());

  private String address;

  private String database;

  private String user;

  private String password;

  private String other;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * 获取 url
   */
  public String getJdbcUrl() {
    String jdbcUrl = address;
    if (jdbcUrl.lastIndexOf("/") != (jdbcUrl.length() - 1)) {
      jdbcUrl += "/";
    }

    jdbcUrl += database;

    if (StringUtils.isNotEmpty(other)) {
      jdbcUrl += "?" + other;
    }

    return jdbcUrl;
  }

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      Class.forName("org.postgresql.Driver");
      con = DriverManager.getConnection(getJdbcUrl(), this.user, this.password);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Postgre datasource try conn close conn error", e);
          throw e;
        }
      }
    }

  }
}
