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

/* $Id$ */

package org.apache.fop.fonts.truetype;


/**
 * Represents table names as found in a TrueType font's Table Directory.
 * TrueType fonts may have custom tables so we cannot use an enum.
 */
public final class OFTableName {

    /**
     * PCL 5 data.
     */
    public static final OFTableName PCLT = new OFTableName("PCLT");

    /**
     * Character to glyph mapping.
     */
    public static final OFTableName CMAP = new OFTableName("cmap");

    /**
     * Glyph data.
     */
    public static final OFTableName GLYF = new OFTableName("glyf");

    /**
     * Horizontal metrics.
     */
    public static final OFTableName HMTX = new OFTableName("hmtx");

    /**
     * Kerning.
     */
    public static final OFTableName KERN = new OFTableName("kern");

    /**
     * Index to location.
     */
    public static final OFTableName LOCA = new OFTableName("loca");

    private final String name;

    private OFTableName(String name) {
        this.name = name;
    }

    /**
     * Returns an instance of this class corresponding to the given string representation.
     *
     * @param tableName table name as in the Table Directory
     * @return TTFTableName
     */
    public static OFTableName getValue(String tableName) {
        if (tableName != null) {
            return new OFTableName(tableName);
        }
        throw new IllegalArgumentException("A TrueType font table name must not be null");
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OFTableName)) {
            return false;
        }
        OFTableName to = (OFTableName) o;
        return this.name.equals(to.getName());
    }

    @Override
    public String toString() {
        return name;
    }

}
