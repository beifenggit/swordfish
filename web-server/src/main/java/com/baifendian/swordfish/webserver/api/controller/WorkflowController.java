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
package com.baifendian.swordfish.webserver.api.controller;

/**
 * 工作流管理的服务入口
 */

import com.baifendian.swordfish.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/projects/{projectName}/workflows")
public class WorkflowController {

  private static Logger logger = LoggerFactory.getLogger(WorkflowController.class.getName());

  /**
   * 创建工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.POST})
  public void createWorkflow(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "proxyUser") String proxyUser,
                             @RequestParam(value = "queue") String queue,
                             @RequestParam(value = "data") String data,
                             @RequestParam(value = "file") String file,
                             HttpServletResponse response) {


  }

  /**
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.PUT})
  public void modifyWorkflow(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "proxyUser") String proxyUser,
                             @RequestParam(value = "queue") String queue,
                             @RequestParam(value = "data") String data,
                             @RequestParam(value = "file") String file,
                             HttpServletResponse response) {


  }

  @RequestMapping(value = "/{name}", method = {RequestMethod.DELETE})
  public void modifyWorkflow(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             HttpServletResponse response) {


  }

  @RequestMapping(value = "", method = {RequestMethod.GET})
  public void queryWorkflow(@RequestAttribute(value = "session.user") User operator,
                            @PathVariable String projectName,
                            HttpServletResponse response) {


  }

  @RequestMapping(value = "{name}", method = {RequestMethod.GET})
  public void queryWorkflowDetail(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable String projectName,
                                  @PathVariable String name,
                                  HttpServletResponse response) {


  }

  @RequestMapping(value = "{name}/file", method = {RequestMethod.GET})
  public void downloadWorkflowDetail(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable String projectName,
                                  @PathVariable String name,
                                  HttpServletResponse response) {


  }
}