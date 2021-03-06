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
package com.baifendian.swordfish.common.job.struct.node;

public class JobType {

  public static final String HQL = "HQL"; // in fact, "HQL" is not accurate, "SQL" is more accuracy
  public static final String MR = "MR";
  public static final String SHELL = "SHELL";
  public static final String SPARK = "SPARK";
  public static final String STORM = "STORM";
  public static final String VIRTUAL = "VIRTUAL";
  public static final String SPARK_STREAMING = "SPARK_STREAMING"; // 长任务类型
  public static final String IMPEXP = "IMPEXP";

  /**
   * 判断是否是长任务
   */
  public static boolean isLongJob(String job) {
    switch (job) {
      case SPARK_STREAMING:
        return true;
      case STORM:
        return true;
      case HQL:
      case MR:
      case SHELL:
      case SPARK:
      case VIRTUAL:
        return false;
      default:
        throw new IllegalArgumentException("job not valid");
    }
  }
}
