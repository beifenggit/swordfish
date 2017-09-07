package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.SparkSqlService.Iface;
import com.baifendian.swordfish.rpc.UdfInfo;
import java.util.List;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlServiceImpl implements Iface {

  private static Logger logger = LoggerFactory.getLogger(SparkSqlServiceImpl.class);

  private RunnerManager runnerManager = new RunnerManager();

  @Override
  public RetInfo execEtl(String jobId, List<UdfInfo> udfs, List<String> sql, int remainTime)
      throws TException {
    logger.info("begin ");
    RetInfo retInfo = new RetInfo();
    long endTime = System.currentTimeMillis() + remainTime * 1000L;
    runnerManager.executeEtlSql(jobId, udfs, sql, endTime);
    retInfo.setStatus(0);
    return retInfo;
  }

  @Override
  public RetInfo execAdhoc(String jobId, List<UdfInfo> udfs, List<String> sql, int queryLimit,
      int remainTime) throws TException {
    RetInfo retInfo = new RetInfo();
    long endTime = System.currentTimeMillis() + remainTime * 1000L;
    runnerManager.executeAdhocSql(jobId, udfs, sql, endTime, queryLimit);
    retInfo.setStatus(0);
    return retInfo;
  }

  @Override
  public RetInfo cancelExecFlow(String jobId) throws TException {
    RetInfo retInfo = new RetInfo();
    retInfo.setStatus(0);

    runnerManager.cancelExecFlow(jobId);
    return retInfo;
  }

  @Override
  public AdhocResultInfo getAdhocResult(String jobId) throws TException {
    return runnerManager.getAdHocResult(jobId);
  }
}