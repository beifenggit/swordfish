package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.mysql.enums.DbType;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

/**
 * @auth: ronghua.yu
 * @time: 16/8/9
 * @desc:
 * @modify by qifeng.dai(smile8) on 2016.11.08
 */
public class DataSource {
  private Integer id;
  private Integer ownerId;
  private String ownerName;
  private Integer projectId;
  private String name;
  private String desc;
  private DbType type; // db 类型
  private String params; // 参数信息
  private DataSourceDbBase paramObj; // 参数信息, object 形式
  private int createTime;
  private int modifyTime;

  public DataSource() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public DbType getType() {
    return type;
  }

  public void setType(DbType type) {
    this.type = type;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public int getCreateTime() {
    return createTime;
  }

  public void setCreateTime(int createTime) {
    this.createTime = createTime;
  }

  public int getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(int modifyTime) {
    this.modifyTime = modifyTime;
  }

  public DataSourceDbBase getParamObj() throws Exception {
    if (paramObj == null && StringUtils.isNotEmpty(params)) {
      switch (type) {
        case MONGODB:
          paramObj = JsonUtil.parseObject(params, DataSourceMongo.class);
          break;
        case MYSQL:
          paramObj = JsonUtil.parseObject(params, DataSourceMysql.class);
          break;
        case HBASE11:
          paramObj = JsonUtil.parseObject(params, DataSourceHbase.class);
          break;
        case REDIS:
          paramObj = JsonUtil.parseObject(params, DataSourceRedis.class);
        default:
          throw new Exception("类型不支持");
      }
    }

    return paramObj;
  }

  @Override
  public String toString() {
    return "DataSource{" +
        "id=" + id +
        ", ownerId=" + ownerId +
        ", ownerName='" + ownerName + '\'' +
        ", projectId=" + projectId +
        ", name='" + name + '\'' +
        ", desc='" + desc + '\'' +
        ", type=" + type +
        ", params='" + params + '\'' +
        ", createTime=" + createTime +
        ", modifyTime=" + modifyTime +
        '}';
  }
}
