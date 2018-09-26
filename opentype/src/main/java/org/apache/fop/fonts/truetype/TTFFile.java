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

import java.io.IOException;

/**
 * Reads a TrueType file or a TrueType Collection.
 * The TrueType spec can be found at the Microsoft.
 * Typography site: http://www.microsoft.com/truetype/
 */
@SuppressWarnings("all")
public class TTFFile extends OpenFont {

    public TTFFile() {
        this(true);
    }

    /**
     * Constructor
     *
     * @param useKerning true if kerning data should be loaded
     */
    public TTFFile(boolean useKerning) {
        super(useKerning);
    }

    /**
     * Read the "name" table.
     *
     * @throws IOException In case of a I/O problem
     */
    protected void readName() {
    }

    /**
     * Read the "glyf" table to find the bounding boxes.
     *
     * @throws IOException In case of a I/O problem
     */
    private void readGlyf() throws IOException {
    }

    @Override
    protected void updateBBoxAndOffset() throws IOException {
        readIndexToLocation();
        readGlyf();
    }

    /**
     * Read the "loca" table.
     *
     * @throws IOException In case of a I/O problem
     */
    protected final void readIndexToLocation() {
    }

    /**
     * Gets the last location of the glyf table
     *
     * @return The last location as a long
     */
    public long getLastGlyfLocation() {
        return lastLoca;
    }

    @Override
    protected void initializeFont(FontFileReader in) {
        fontFile = in;
    }
}
