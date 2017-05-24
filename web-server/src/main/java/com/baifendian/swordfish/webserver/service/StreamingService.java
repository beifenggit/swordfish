/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.webserver.service;

import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.StreamingJobMapper;
import com.baifendian.swordfish.dao.mapper.StreamingResultMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.StreamingJob;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.model.flow.Property;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.dto.StreamingResultDto;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.webserver.utils.ParamVerify.*;

@Service
public class StreamingService {

  private static Logger logger = LoggerFactory.getLogger(StreamingService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private StreamingJobMapper streamingJobMapper;

  @Autowired
  private StreamingResultMapper streamingResultMapper;

  @Autowired
  private ProjectService projectService;

  /**
   * 插入一个任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob createStreamingJob(User operator,
                                         String projectName,
                                         String name,
                                         String desc,
                                         String type,
                                         String parameter,
                                         String userDefParams,
                                         String extras) {

    // 校验变量
    verifyStreamingName(name);
    verifyDesc(desc);
    verifyExtras(extras);

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // project 是否存在写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 对节点进行解析
    if (!flowNodeParamCheck(parameter, type)) {
      logger.error("StreamingJob parameter:{} invalid", parameter);
      throw new ParameterException("StreamingJob parameter: \"{0}\" invalid", parameter);
    }

    // 对自定义参数进行解析
    try {
      JsonUtil.parseObjectList(userDefParams, Property.class);
    } catch (Exception e) {
      logger.error("StreamingJob user define parameters:{} invalid", userDefParams);
      throw new ParameterException("StreamingJob user define parameters: \"{0}\" invalid", userDefParams);
    }

    StreamingJob streamingJob = new StreamingJob();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      streamingJob.setOwnerId(operator.getId());
      streamingJob.setOwner(operator.getName());
      streamingJob.setProjectId(project.getId());
      streamingJob.setProjectName(projectName);
      streamingJob.setName(name);
      streamingJob.setCreateTime(now);
      streamingJob.setModifyTime(now);
      streamingJob.setDesc(desc);
      streamingJob.setType(type);
      streamingJob.setParameter(parameter);
      streamingJob.setUserDefinedParams(userDefParams);
      streamingJob.setExtras(extras);
    } catch (Exception e) {
      logger.error("Str set value error", e);
      throw new BadRequestException("Project flow set value error", e);
    }

    try {
      streamingJobMapper.insertAndGetId(streamingJob);
    } catch (DuplicateKeyException e) {
      logger.error("StreamingJob has exist, can't create again.", e);
      throw new ServerErrorException("StreamingJob has exist, can't create again.");
    } catch (Exception e) {
      logger.error("StreamingJob create has error", e);
      throw new ServerErrorException("StreamingJob create has error", e);
    }

    return streamingJob;
  }


  /**
   * 更新并插入一条任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob putStreamingJob(User operator,
                                      String projectName,
                                      String name,
                                      String desc,
                                      String type,
                                      String parameter,
                                      String userDefParams,
                                      String extras) {
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      return createStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams, extras);
    }

    return patchStreamingJob(operator, projectName, name, desc, parameter, userDefParams, extras);
  }

  /**
   * 修改一条流任务信息
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob patchStreamingJob(User operator,
                                        String projectName,
                                        String name,
                                        String desc,
                                        String parameter,
                                        String userDefParams,
                                        String extras) {

    verifyDesc(desc);
    verifyExtras(extras);

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streaming {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streaming \"{0}\" in project \"{1}\"", name, project.getName());
    }

    Date now = new Date();

    // 对节点进行解析
    if (StringUtils.isNotEmpty(parameter)) {
      if (!flowNodeParamCheck(parameter, streamingJob.getType())) {
        logger.error("StreamingJob parameter:{} invalid", parameter);
        throw new ParameterException("StreamingJob parameter: \"{0}\" invalid", parameter);
      }

      streamingJob.setParameter(parameter);
    }

    // 对自定义参数进行解析
    if (StringUtils.isNotEmpty(userDefParams)) {
      try {
        JsonUtil.parseObjectList(userDefParams, Property.class);
      } catch (Exception e) {
        logger.error("StreamingJob user define parameters:{} invalid", userDefParams);
        throw new ParameterException("StreamingJob user define parameters: \"{0}\" invalid", userDefParams);
      }

      streamingJob.setUserDefinedParams(userDefParams);
    }

    if (StringUtils.isNotEmpty(extras)) {
      streamingJob.setExtras(extras);
    }

    if (StringUtils.isNotEmpty(desc)) {
      streamingJob.setDesc(desc);
    }

    streamingJob.setModifyTime(now);
    streamingJob.setOwnerId(operator.getId());
    streamingJob.setOwner(operator.getName());

    try {
      streamingJobMapper.updateStreamingJob(streamingJob);
    } catch (Exception e) {
      logger.error("Streaming modify has error", e);
      throw new ServerErrorException("Streaming modify has error", e);
    }

    return streamingJob;
  }

  /**
   * 删除一个工作流
   *
   * @param operator
   * @param projectName
   * @param name
   */
  public void deleteStreamingJob(User operator, String projectName, String name) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 应该有项目写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streamingJob job {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streamingJob job \"{0}\" in project \"{1}\"", name, project.getName());
    }

    // 删除工作流
    streamingJobMapper.deleteById(streamingJob.getId());
  }

  /**
   * 执行一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param proxyUser
   * @param queue
   * @return
   */
  public ExecutorIdDto executeStreamingJob(User operator, String projectName, String name, String proxyUser, String queue) {
    return null;
  }

  /**
   * 删除一个流任务
   *
   * @param operator
   * @param execId
   */
  public void killStreamingJob(User operator, int execId) {

  }

  /**
   * 查询项目下所有流任务
   *
   * @param operator
   * @param projectName
   * @return
   */
  public List<StreamingResultDto> queryProjectStreamingJobAndResult(User operator, String projectName) {
    return null;
  }

  /**
   * 查询具体某个流任务的详情
   *
   * @param operator
   * @param execId
   * @return
   */
  public List<StreamingResultDto> queryStreamingJobAndResult(User operator, int execId) {
    return null;
  }

  /**
   * 查询日志信息
   *
   * @param operator
   * @param jobId
   * @param from
   * @param size
   * @return
   */
  public LogResult getStreamingJobLog(User operator, String jobId, int from, int size) {
//    ExecutionNode executionNode = streaming_result.selectExecNodeByJobId(jobId);
//
//    if (executionNode == null) {
//      logger.error("job id does not exist: {}", jobId);
//      throw new NotFoundException("Not found jobId \"{0}\"", jobId);
//    }
//
//    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(executionNode.getExecId());
//
//    if (executionFlow == null) {
//      logger.error("exec flow does not exist: {}", executionNode.getExecId());
//      throw new NotFoundException("Not found execId \"{0}\"", executionNode.getExecId());
//    }
//
//    Project project = projectMapper.queryByName(executionFlow.getProjectName());
//
//    if (project == null) {
//      logger.error("Project does not exist: {}", executionFlow.getProjectName());
//      throw new NotFoundException("Not found project \"{0}\"", executionFlow.getProjectName());
//    }
//
//    // 必须有 project 执行权限
//    if (!projectService.hasExecPerm(operator.getId(), project)) {
//      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
//      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
//    }
//
//    return logHelper.getLog(from, size, jobId);
    return null;
  }
}