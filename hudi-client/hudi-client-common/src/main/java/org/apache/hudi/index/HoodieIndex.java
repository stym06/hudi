/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.index;

import org.apache.hudi.ApiMaturityLevel;
import org.apache.hudi.PublicAPIClass;
import org.apache.hudi.PublicAPIMethod;
import org.apache.hudi.client.common.HoodieEngineContext;
import org.apache.hudi.common.model.FileSlice;
import org.apache.hudi.common.model.HoodieKey;
import org.apache.hudi.common.model.HoodieRecordPayload;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.hudi.exception.HoodieIndexException;
import org.apache.hudi.table.HoodieTable;

import java.io.Serializable;

/**
 * Base class for different types of indexes to determine the mapping from uuid.
 *
 * @param <T> Sub type of HoodieRecordPayload
 * @param <I> Type of inputs
 * @param <K> Type of keys
 * @param <O> Type of outputs
 */
@PublicAPIClass(maturity = ApiMaturityLevel.EVOLVING)
public abstract class HoodieIndex<T extends HoodieRecordPayload, I, K, O> implements Serializable {

  protected final HoodieWriteConfig config;

  protected HoodieIndex(HoodieWriteConfig config) {
    this.config = config;
  }

  /**
   * Looks up the index and tags each incoming record with a location of a file that contains the row (if it is actually
   * present).
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  public abstract I tagLocation(I records, HoodieEngineContext context,
                                HoodieTable<T, I, K, O> hoodieTable) throws HoodieIndexException;

  /**
   * Extracts the location of written records, and updates the index.
   * <p>
   * TODO(vc): We may need to propagate the record as well in a WriteStatus class
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  public abstract O updateLocation(O writeStatusRDD, HoodieEngineContext context,
                                   HoodieTable<T, I, K, O> hoodieTable) throws HoodieIndexException;

  /**
   * Rollback the effects of the commit made at instantTime.
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  public abstract boolean rollbackCommit(String instantTime);

  /**
   * An index is `global` if {@link HoodieKey} to fileID mapping, does not depend on the `partitionPath`. Such an
   * implementation is able to obtain the same mapping, for two hoodie keys with same `recordKey` but different
   * `partitionPath`
   *
   * @return whether or not, the index implementation is global in nature
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  public abstract boolean isGlobal();

  /**
   * This is used by storage to determine, if its safe to send inserts, straight to the log, i.e having a
   * {@link FileSlice}, with no data file.
   *
   * @return Returns true/false depending on whether the impl has this capability
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.EVOLVING)
  public abstract boolean canIndexLogFiles();

  /**
   * An index is "implicit" with respect to storage, if just writing new data to a file slice, updates the index as
   * well. This is used by storage, to save memory footprint in certain cases.
   */
  @PublicAPIMethod(maturity = ApiMaturityLevel.STABLE)
  public abstract boolean isImplicitWithStorage();

  /**
   * Each index type should implement it's own logic to release any resources acquired during the process.
   */
  public void close() {
  }

  public enum IndexType {
    HBASE, INMEMORY, BLOOM, GLOBAL_BLOOM, SIMPLE, GLOBAL_SIMPLE
  }
}
