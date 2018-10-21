/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.util.opentype.tables;

import am.util.opentype.TableRecord;

/**
 * 基础表
 */
public class BaseTable {

    private final TableRecord mRecord;

    public BaseTable(TableRecord record) {
        mRecord = record;
    }

    /**
     * 获取表记录
     *
     * @return 表记录
     */
    @SuppressWarnings("unused")
    public TableRecord getTableRecord() {
        return mRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTable table = (BaseTable) o;
        return Objects.equals(mRecord, table.mRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mRecord);
    }

    @Override
    public String toString() {
        return "BaseTable{" +
                "record=" + String.valueOf(mRecord) +
                '}';
    }
}
