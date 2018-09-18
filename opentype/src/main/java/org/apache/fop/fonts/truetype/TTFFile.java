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
        OFDirTabEntry dirTab = dirTabs.get(OFTableName.GLYF);
        if (dirTab == null) {
            // TODO
            // glyf table not found, cannot continue
            return;
//            throw new IOException("glyf table not found, cannot continue");
        }
        for (int i = 0; i < (numberOfGlyphs - 1); i++) {
            if (mtxTab[i].getOffset() != mtxTab[i + 1].getOffset()) {
                fontFile.seek(dirTab.getOffset() + mtxTab[i].getOffset());
                fontFile.skip(2);
                final int[] bbox = {
                        fontFile.readShort(),
                        fontFile.readShort(),
                        fontFile.readShort(),
                        fontFile.readShort()};
                mtxTab[i].setBoundingBox(bbox);
            } else {
                mtxTab[i].setBoundingBox(mtxTab[0].getBoundingBox());
            }
        }

        long n = (dirTabs.get(OFTableName.GLYF)).getOffset();
        for (int i = 0; i < numberOfGlyphs; i++) {
            if ((i + 1) >= mtxTab.length
                    || mtxTab[i].getOffset() != mtxTab[i + 1].getOffset()) {
                fontFile.seek(n + mtxTab[i].getOffset());
                fontFile.skip(2);
                final int[] bbox = {
                        fontFile.readShort(),
                        fontFile.readShort(),
                        fontFile.readShort(),
                        fontFile.readShort()};
                mtxTab[i].setBoundingBox(bbox);
            } else {
                /**@todo Verify that this is correct, looks like a copy/paste bug (jm)*/
                final int bbox0 = mtxTab[0].getBoundingBox()[0];
                final int[] bbox = {bbox0, bbox0, bbox0, bbox0};
                mtxTab[i].setBoundingBox(bbox);
                /* Original code
                mtxTab[i].bbox[0] = mtxTab[0].bbox[0];
                mtxTab[i].bbox[1] = mtxTab[0].bbox[0];
                mtxTab[i].bbox[2] = mtxTab[0].bbox[0];
                mtxTab[i].bbox[3] = mtxTab[0].bbox[0]; */
            }
        }
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
    protected final void readIndexToLocation()
            throws IOException {
        if (!seekTab(fontFile, OFTableName.LOCA, 0)) {
            // TODO
            // 'loca' table not found, happens when the font file doesn't contain TrueType outlines.
            // (trying to read an OpenType CFF font maybe?)
            return;
//            throw new IOException("'loca' table not found, happens when the font file doesn't"
//                    + " contain TrueType outlines (trying to read an OpenType CFF font maybe?)");
        }
        for (int i = 0; i < numberOfGlyphs; i++) {
            mtxTab[i].setOffset(locaFormat == 1 ? fontFile.readInt()
                    : (fontFile.readUnsignedShort() << 1));
        }
        lastLoca = (locaFormat == 1 ? fontFile.readInt()
                : (fontFile.readUnsignedShort() << 1));
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
