/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: TTFTableName.java 1357883 2012-07-05 20:29:53Z gadams $ */

package org.apache.fop;

/**
 * Represents table names as found in a TrueType font's Table Directory. TrueType fonts may have
 * custom tables so we cannot use an enum.
 */
@SuppressWarnings("all")
final class TTFTableName {

    /**
     * The first table in a TrueType font file containing metadata about other tables.
     */
    public static final TTFTableName TABLE_DIRECTORY = new TTFTableName("tableDirectory");

    /**
     * Naming table.
     */
    public static final TTFTableName NAME = new TTFTableName("name");
    private final String name;

    private TTFTableName(String name) {
        this.name = name;
    }

    /**
     * Returns an instance of this class corresponding to the given string representation.
     *
     * @param tableName table name as in the Table Directory
     * @return TTFTableName
     */
    public static TTFTableName getValue(String tableName) {
        if (tableName != null) {
            return new TTFTableName(tableName);
        }
        throw new IllegalArgumentException("A TrueType font table name must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TTFTableName)) {
            return false;
        }
        TTFTableName to = (TTFTableName) o;
        return name.equals(to.getName());
    }

    /**
     * Returns the name of the table as it should be in the Directory Table.
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
