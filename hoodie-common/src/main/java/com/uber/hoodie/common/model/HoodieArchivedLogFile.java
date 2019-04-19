/*
 *  Copyright (c) 2017 Uber Technologies, Inc. (hoodie-dev-group@uber.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.uber.hoodie.common.model;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

public class HoodieArchivedLogFile extends HoodieLogFile {

  public static final String ARCHIVE_EXTENSION = ".archive";

  public HoodieArchivedLogFile(FileStatus fileStatus) {
    super(fileStatus);
  }

  public HoodieArchivedLogFile(Path logPath) {
    super(logPath);
  }

  @Override
  public String toString() {
    return "HoodieArchivedLogFile {" + super.getPath() + '}';
  }
}