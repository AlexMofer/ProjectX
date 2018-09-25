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

import android.graphics.Rect;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public abstract class OpenFont {

    private static final String[] MAC_GLYPH_ORDERING = {
            /* 0x000 */
            ".notdef", ".null", "nonmarkingreturn", "space",
            "exclam", "quotedbl", "numbersign", "dollar",
            "percent", "ampersand", "quotesingle", "parenleft",
            "parenright", "asterisk", "plus", "comma",
            /* 0x010 */
            "hyphen", "period", "slash", "zero",
            "one", "two", "three", "four",
            "five", "six", "seven", "eight",
            "nine", "colon", "semicolon", "less",
            /* 0x020 */
            "equal", "greater", "question", "at",
            "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L",
            /* 0x030 */
            "M", "N", "O", "P",
            "Q", "R", "S", "T",
            "U", "V", "W", "X",
            "Y", "Z", "bracketleft", "backslash",
            /* 0x040 */
            "bracketright", "asciicircum", "underscore", "grave",
            "a", "b", "c", "d",
            "e", "f", "g", "h",
            "i", "j", "k", "l",
            /* 0x050 */
            "m", "n", "o", "p",
            "q", "r", "s", "t",
            "u", "v", "w", "x",
            "y", "z", "braceleft", "bar",
            /* 0x060 */
            "braceright", "asciitilde", "Adieresis", "Aring",
            "Ccedilla", "Eacute", "Ntilde", "Odieresis",
            "Udieresis", "aacute", "agrave", "acircumflex",
            "adieresis", "atilde", "aring", "ccedilla",
            /* 0x070 */
            "eacute", "egrave", "ecircumflex", "edieresis",
            "iacute", "igrave", "icircumflex", "idieresis",
            "ntilde", "oacute", "ograve", "ocircumflex",
            "odieresis", "otilde", "uacute", "ugrave",
            /* 0x080 */
            "ucircumflex", "udieresis", "dagger", "degree",
            "cent", "sterling", "section", "bullet",
            "paragraph", "germandbls", "registered", "copyright",
            "trademark", "acute", "dieresis", "notequal",
            /* 0x090 */
            "AE", "Oslash", "infinity", "plusminus",
            "lessequal", "greaterequal", "yen", "mu",
            "partialdiff", "summation", "product", "pi",
            "integral", "ordfeminine", "ordmasculine", "Omega",
            /* 0x0A0 */
            "ae", "oslash", "questiondown", "exclamdown",
            "logicalnot", "radical", "florin", "approxequal",
            "Delta", "guillemotleft", "guillemotright", "ellipsis",
            "nonbreakingspace", "Agrave", "Atilde", "Otilde",
            /* 0x0B0 */
            "OE", "oe", "endash", "emdash",
            "quotedblleft", "quotedblright", "quoteleft", "quoteright",
            "divide", "lozenge", "ydieresis", "Ydieresis",
            "fraction", "currency", "guilsinglleft", "guilsinglright",
            /* 0x0C0 */
            "fi", "fl", "daggerdbl", "periodcentered",
            "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex",
            "Ecircumflex", "Aacute", "Edieresis", "Egrave",
            "Iacute", "Icircumflex", "Idieresis", "Igrave",
            /* 0x0D0 */
            "Oacute", "Ocircumflex", "apple", "Ograve",
            "Uacute", "Ucircumflex", "Ugrave", "dotlessi",
            "circumflex", "tilde", "macron", "breve",
            "dotaccent", "ring", "cedilla", "hungarumlaut",
            /* 0x0E0 */
            "ogonek", "caron", "Lslash", "lslash",
            "Scaron", "scaron", "Zcaron", "zcaron",
            "brokenbar", "Eth", "eth", "Yacute",
            "yacute", "Thorn", "thorn", "minus",
            /* 0x0F0 */
            "multiply", "onesuperior", "twosuperior", "threesuperior",
            "onehalf", "onequarter", "threequarters", "franc",
            "Gbreve", "gbreve", "Idotaccent", "Scedilla",
            "scedilla", "Cacute", "cacute", "Ccaron",
            /* 0x100 */
            "ccaron", "dcroat"
    };
    private static final String ENCODING = "WinAnsiEncoding";    // Default encoding
    private static final short FIRST_CHAR = 0;
    protected final Set<String> familyNames = new HashSet<String>();
    // internal mapping of glyph indexes to unicode indexes
    // used for quick mappings in this class
    private final Map<Integer, Integer> glyphToUnicodeMap = new HashMap<Integer, Integer>();
    private final Map<Integer, Integer> unicodeToGlyphMap = new HashMap<Integer, Integer>();
    /**
     * The FontFileReader used to read this TrueType font.
     */
    protected FontFileReader fontFile;
    protected boolean useKerning;
    /**
     * Table directory
     */
    protected Map<OFTableName, OFDirTabEntry> dirTabs;
    protected List<UnicodeMapping> unicodeMappings;
    protected int nhmtx;                               // Number of horizontal metrics
    protected int locaFormat;
    /**
     * Offset to last loca
     */
    protected long lastLoca;
    protected int numberOfGlyphs; // Number of glyphs in font (read from "maxp" table)
    /**
     * Contains glyph data
     */
    protected OFMtxEntry[] mtxTab;                  // Contains glyph data
    protected String postScriptName = "";
    protected String fullName = "";
    protected String embedFontName = "";
    protected String notice = "";
    protected String subFamilyName = "";
    protected boolean cid = true;
    protected int usWeightClass;
    private boolean isEmbeddable = true;
    private boolean hasSerifs = true;
    private Map<Integer, Map<Integer, Integer>> kerningTab; // for CIDs
    private Map<Integer, Map<Integer, Integer>> ansiKerningTab; // For winAnsiEncoding
    private List<CMapSegment> cmaps;
    private int upem;                                // unitsPerEm from "head" table
    private PostScriptVersion postScriptVersion;
    private long italicAngle;
    private long isFixedPitch;
    private int fontBBox1;
    private int fontBBox2;
    private int fontBBox3;
    private int fontBBox4;
    private int capHeight;
    private int os2CapHeight;
    private int underlinePosition;
    private int underlineThickness;
    private int strikeoutPosition;
    private int strikeoutThickness;
    private int xHeight;
    private int os2xHeight;
    //Effective ascender/descender
    private int ascender;
    private int descender;
    //Ascender/descender from hhea table
    private int hheaAscender;
    private int hheaDescender;
    //Ascender/descender from OS/2 table
    private int os2Ascender;
    private int os2Descender;
    //    private Map<Integer, List<Integer>> ansiIndex;
    private short lastChar;
    private int[] ansiWidth;
    private boolean isCFF;

    public OpenFont() {
        this(true);
    }

    /**
     * Constructor
     *
     * @param useKerning true if kerning data should be loaded
     */
    public OpenFont(boolean useKerning) {
        this.useKerning = useKerning;
    }

    /**
     * Obtain directory table entry.
     *
     * @param name (tag) of entry
     * @return a directory table entry or null if none found
     */
    public OFDirTabEntry getDirectoryEntry(OFTableName name) {
        return dirTabs.get(name);
    }

    /**
     * Position inputstream to position indicated
     * in the dirtab offset + offset
     *
     * @param in        font file reader
     * @param tableName (tag) of table
     * @param offset    from start of table
     * @return true if seek succeeded
     * @throws IOException if I/O exception occurs during seek
     */
    public boolean seekTab(FontFileReader in, OFTableName tableName,
                           long offset) throws IOException {
        OFDirTabEntry dt = dirTabs.get(tableName);
        if (dt == null) {
            return false;
        } else {
            in.seek(dt.getOffset() + offset);
        }
        return true;
    }

    /**
     * Convert from truetype unit to pdf unit based on the
     * unitsPerEm field in the "head" table
     *
     * @param n truetype unit
     * @return pdf unit
     */
    public int convertTTFUnit2PDFUnit(int n) {
        int ret;
        if (n < 0) {
            long rest1 = n % upem;
            long storrest = 1000 * rest1;
            long ledd2 = (storrest != 0 ? rest1 / storrest : 0);
            ret = -((-1000 * n) / upem - (int) ledd2);
        } else {
            ret = (n / upem) * 1000 + ((n % upem) * 1000) / upem;
        }

        return ret;
    }

    /**
     * Read the cmap table,
     * return false if the table is not present or only unsupported
     * tables are present. Currently only unicode cmaps are supported.
     * Set the unicodeIndex in the TTFMtxEntries and fills in the
     * cmaps vector.
     *
     * @see <a href="https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6cmap.html">
     * TrueType-Reference-Manual
     * </a>
     */
    protected boolean readCMAP() {

        return true;
    }

    private boolean readUnicodeCmap(long cmapUniOffset, int encodingID) {
        return true;
    }

    private boolean isInPrivateUseArea(int start, int end) {
        return (isInPrivateUseArea(start) || isInPrivateUseArea(end));
    }

    private boolean isInPrivateUseArea(int unicode) {
        return (unicode >= 0xE000 && unicode <= 0xF8FF);
    }

    /**
     * @return mmtx data
     */
    public List<OFMtxEntry> getMtx() {
        return Collections.unmodifiableList(Arrays.asList(mtxTab));
    }

    /**
     * Reads the font using a FontFileReader.
     *
     * @param in The FontFileReader to use
     * @throws IOException In case of an I/O problem
     */
    public void readFont(FontFileReader in, String header) throws IOException {
        readFont(in, header, null);
    }

    /**
     * initialize the ansiWidths array (for winAnsiEncoding)
     * and fill with the missingwidth
     */
    protected void initAnsiWidths() {
        ansiWidth = new int[256];
        for (int i = 0; i < 256; i++) {
            ansiWidth[i] = mtxTab[0].getWx();
        }
    }

    /**
     * Print first char/last char
     */
    /* not used
    private void printMaxMin() {
        int min = 255;
        int max = 0;
        for (int i = 0; i < mtxTab.length; i++) {
            if (mtxTab[i].getIndex() < min) {
                min = mtxTab[i].getIndex();
            }
            if (mtxTab[i].getIndex() > max) {
                max = mtxTab[i].getIndex();
            }
        }
        log.info("Min: " + min);
        log.info("Max: " + max);
    }
    */

    /**
     * Read the font data.
     * If the fontfile is a TrueType Collection (.ttc file)
     * the name of the font to read data for must be supplied,
     * else the name is ignored.
     *
     * @param in   The FontFileReader to use
     * @param name The name of the font
     * @return boolean Returns true if the font is valid
     * @throws IOException In case of an I/O problem
     */
    public boolean readFont(FontFileReader in, String header, String name) throws IOException {
        initializeFont(in);
        /*
         * Check if TrueType collection, and that the name
         * exists in the collection
         */
        if (!checkTTC(header, name)) {
            if (name == null) {
                throw new IllegalArgumentException(
                        "For TrueType collection you must specify which font "
                                + "to select (-ttcname)");
            } else {
                throw new IOException(
                        "Name does not exist in the TrueType collection: " + name);
            }
        }

        readDirTabs();
        readFontHeader();
        getNumGlyphs();
        readHorizontalHeader();
        readHorizontalMetrics();
        initAnsiWidths();
        readPostScript();
        readOS2();
        determineAscDesc();

        readName();
        boolean pcltFound = readPCLT();
        // Read cmap table and fill in ansiwidths
        boolean valid = readCMAP();
        if (!valid) {
            return false;
        }

        // Create cmaps for bfentries
        createCMaps();
        updateBBoxAndOffset();

        if (useKerning) {
            readKerning();
        }
        guessVerticalMetricsFromGlyphBBox();
        return true;
    }

    protected abstract void updateBBoxAndOffset() throws IOException;

    protected abstract void readName() throws IOException;

    protected abstract void initializeFont(FontFileReader in) throws IOException;

    protected void createCMaps() {
        cmaps = new ArrayList<CMapSegment>();
        int unicodeStart;
        int glyphStart;
        int unicodeEnd;
        if (unicodeMappings.isEmpty()) {
            return;
        }
        Iterator<UnicodeMapping> e = unicodeMappings.iterator();
        UnicodeMapping um = e.next();
        UnicodeMapping lastMapping = um;

        unicodeStart = um.getUnicodeIndex();
        glyphStart = um.getGlyphIndex();

        while (e.hasNext()) {
            um = e.next();
            if (((lastMapping.getUnicodeIndex() + 1) != um.getUnicodeIndex())
                    || ((lastMapping.getGlyphIndex() + 1) != um.getGlyphIndex())) {
                unicodeEnd = lastMapping.getUnicodeIndex();
                cmaps.add(new CMapSegment(unicodeStart, unicodeEnd, glyphStart));
                unicodeStart = um.getUnicodeIndex();
                glyphStart = um.getGlyphIndex();
            }
            lastMapping = um;
        }

        unicodeEnd = lastMapping.getUnicodeIndex();
        cmaps.add(new CMapSegment(unicodeStart, unicodeEnd, glyphStart));
    }

    /**
     * Returns the PostScript name of the font.
     *
     * @return String The PostScript name
     */
    public String getPostScriptName() {
        if (TextUtils.isEmpty(postScriptName)) {
            final String fullName = getFullName();
            if (fullName == null)
                return null;
            final int length = fullName.length();
            final StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                final char ch = fullName.charAt(i);
                if (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\t') {
                    builder.append(ch);
                }
            }
            return builder.toString();
        } else {
            return postScriptName;
        }
    }

    PostScriptVersion getPostScriptVersion() {
        return postScriptVersion;
    }

    /**
     * Returns the font family names of the font.
     *
     * @return Set The family names (a Set of Strings)
     */
    public Set<String> getFamilyNames() {
        return familyNames;
    }

    /**
     * Returns the font sub family name of the font.
     *
     * @return String The sub family name
     */
    public String getSubFamilyName() {
        return subFamilyName;
    }

    /**
     * Returns the full name of the font.
     *
     * @return String The full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the name of the character set used.
     *
     * @return String The caracter set
     */
    public String getCharSetName() {
        return ENCODING;
    }

    /**
     * Returns the CapHeight attribute of the font.
     *
     * @return int The CapHeight
     */
    public int getCapHeight() {
        return convertTTFUnit2PDFUnit(capHeight);
    }

    /**
     * Returns the XHeight attribute of the font.
     *
     * @return int The XHeight
     */
    public int getXHeight() {
        return convertTTFUnit2PDFUnit(xHeight);
    }

    /**
     * Returns the number of bytes necessary to pad the currentPosition so that a table begins
     * on a 4-byte boundary.
     *
     * @param currentPosition the position to pad.
     * @return int the number of bytes to pad.
     */
    protected int getPadSize(int currentPosition) {
        int padSize = 4 - (currentPosition % 4);
        return padSize < 4 ? padSize : 0;
    }

    /**
     * Returns the Flags attribute of the font.
     *
     * @return int The Flags
     */
    public int getFlags() {
        int flags = 32;    // Use Adobe Standard charset
        if (italicAngle != 0) {
            flags |= 64;
        }
        if (isFixedPitch != 0) {
            flags |= 2;
        }
        if (hasSerifs) {
            flags |= 1;
        }
        return flags;
    }

    /**
     * Returns the weight class of this font. Valid values are 100, 200....,800, 900.
     *
     * @return the weight class value (or 0 if there was no OS/2 table in the font)
     */
    public int getWeightClass() {
        return this.usWeightClass;
    }

    /**
     * Returns the StemV attribute of the font.
     *
     * @return String The StemV
     */
    public String getStemV() {
        return "0";
    }

    /**
     * Returns the ItalicAngle attribute of the font.
     *
     * @return String The ItalicAngle
     */
    public String getItalicAngle() {
        String ia = Short.toString((short) (italicAngle / 0x10000));

        // This is the correct italic angle, however only int italic
        // angles are supported at the moment so this is commented out.
        /*
         * if ((italicAngle % 0x10000) > 0 )
         * ia=ia+(comma+Short.toString((short)((short)((italicAngle % 0x10000)*1000)/0x10000)));
         */
        return ia;
    }

    /**
     * @return int[] The font bbox
     */
    public int[] getFontBBox() {
        final int[] fbb = new int[4];
        fbb[0] = convertTTFUnit2PDFUnit(fontBBox1);
        fbb[1] = convertTTFUnit2PDFUnit(fontBBox2);
        fbb[2] = convertTTFUnit2PDFUnit(fontBBox3);
        fbb[3] = convertTTFUnit2PDFUnit(fontBBox4);

        return fbb;
    }

    /**
     * Returns the original bounding box values from the HEAD table
     *
     * @return An array of bounding box values
     */
    public int[] getBBoxRaw() {
        int[] bbox = {fontBBox1, fontBBox2, fontBBox3, fontBBox4};
        return bbox;
    }

    /**
     * Returns the LowerCaseAscent attribute of the font.
     *
     * @return int The LowerCaseAscent
     */
    public int getLowerCaseAscent() {
        return convertTTFUnit2PDFUnit(ascender);
    }

    /**
     * Returns the LowerCaseDescent attribute of the font.
     *
     * @return int The LowerCaseDescent
     */
    public int getLowerCaseDescent() {
        return convertTTFUnit2PDFUnit(descender);
    }

    /**
     * Returns the index of the last character, but this is for WinAnsiEncoding
     * only, so the last char is &lt; 256.
     *
     * @return short Index of the last character (&lt;256)
     */
    public short getLastChar() {
        return lastChar;
    }

    /**
     * Returns the index of the first character.
     *
     * @return short Index of the first character
     */
    public short getFirstChar() {
        return FIRST_CHAR;
    }

    /**
     * Returns an array of character widths.
     *
     * @return int[] The character widths
     */
    public int[] getWidths() {
        int[] wx = new int[mtxTab.length];
        for (int i = 0; i < wx.length; i++) {
            wx[i] = convertTTFUnit2PDFUnit(mtxTab[i].getWx());
        }
        return wx;
    }

    public Rect[] getBoundingBoxes() {
        Rect[] boundingBoxes = new Rect[mtxTab.length];
        for (int i = 0; i < boundingBoxes.length; i++) {
            int[] boundingBox = mtxTab[i].getBoundingBox();
            boundingBoxes[i] = new Rect(
                    convertTTFUnit2PDFUnit(boundingBox[0]),
                    convertTTFUnit2PDFUnit(boundingBox[1]),
                    convertTTFUnit2PDFUnit(boundingBox[2]),
                    convertTTFUnit2PDFUnit(boundingBox[3]));
        }
        return boundingBoxes;
    }

    /**
     * Returns an array (xMin, yMin, xMax, yMax) for a glyph.
     *
     * @param glyphIndex the index of the glyph
     * @return int[] Array defining bounding box.
     */
    public int[] getBBox(int glyphIndex) {
        int[] bbox = new int[4];
        if (glyphIndex < mtxTab.length) {
            int[] bboxInTTFUnits = mtxTab[glyphIndex].getBoundingBox();
            for (int i = 0; i < 4; i++) {
                bbox[i] = convertTTFUnit2PDFUnit(bboxInTTFUnits[i]);
            }
        }
        return bbox;
    }

    /**
     * Returns the width of a given character.
     *
     * @param idx Index of the character
     * @return int Standard width
     */
    public int getCharWidth(int idx) {
        return convertTTFUnit2PDFUnit(ansiWidth[idx]);
    }

    /**
     * Returns the width of a given character in raw units
     *
     * @param idx Index of the character
     * @return int Width in it's raw form stored in the font
     */
    public int getCharWidthRaw(int idx) {
        if (ansiWidth != null) {
            return ansiWidth[idx];
        }
        return -1;
    }

    /**
     * Returns the kerning table.
     *
     * @return Map The kerning table
     */
    public Map<Integer, Map<Integer, Integer>> getKerning() {
        return kerningTab;
    }

    /**
     * Returns the ANSI kerning table.
     *
     * @return Map The ANSI kerning table
     */
    public Map<Integer, Map<Integer, Integer>> getAnsiKerning() {
        return ansiKerningTab;
    }

    public int getUnderlinePosition() {
        return convertTTFUnit2PDFUnit(underlinePosition);
    }

    public int getUnderlineThickness() {
        return convertTTFUnit2PDFUnit(underlineThickness);
    }

    public int getStrikeoutPosition() {
        return convertTTFUnit2PDFUnit(strikeoutPosition);
    }

    public int getStrikeoutThickness() {
        return convertTTFUnit2PDFUnit(strikeoutThickness);
    }

    /**
     * Indicates if the font may be embedded.
     *
     * @return boolean True if it may be embedded
     */
    public boolean isEmbeddable() {
        return isEmbeddable;
    }

    /**
     * Indicates whether or not the font is an OpenType
     * CFF font (rather than a TrueType font).
     *
     * @return true if the font is in OpenType CFF format.
     */
    public boolean isCFF() {
        return this.isCFF;
    }

    /**
     * Read Table Directory from the current position in the
     * FontFileReader and fill the global HashMap dirTabs
     * with the table name (String) as key and a TTFDirTabEntry
     * as value.
     *
     * @throws IOException in case of an I/O problem
     */
    protected void readDirTabs() throws IOException {
        int sfntVersion = fontFile.readInt(); // TTF_FIXED_SIZE (4 bytes)
        switch (sfntVersion) {
            case 0x10000:
                // sfnt version: OpenType 1.0
                break;
            case 0x4F54544F: //"OTTO"
                this.isCFF = true;
                // sfnt version: OpenType with CFF data
                break;
            case 0x74727565: //"true"
                // sfnt version: Apple TrueType
                break;
            case 0x74797031: //"typ1"
                // sfnt version: Apple Type 1 housed in sfnt wrapper
                break;
            default:
                // Unknown sfnt version
                break;
        }
        int ntabs = fontFile.readUnsignedShort();
        fontFile.skip(6);    // 3xTTF_USHORT_SIZE

        dirTabs = new HashMap<OFTableName, OFDirTabEntry>();
        OFDirTabEntry[] pd = new OFDirTabEntry[ntabs];

        for (int i = 0; i < ntabs; i++) {
            pd[i] = new OFDirTabEntry();
            String tableName = pd[i].read(fontFile);
            dirTabs.put(OFTableName.getValue(tableName), pd[i]);
        }
    }

    /**
     * Read the "head" table, this reads the bounding box and
     * sets the upem (unitsPerEM) variable
     *
     * @throws IOException in case of an I/O problem
     */
    protected void readFontHeader() {
    }

    /**
     * Read the number of glyphs from the "maxp" table
     *
     * @throws IOException in case of an I/O problem
     */
    protected void getNumGlyphs() {
    }

    /**
     * Read the "hhea" table to find the ascender and descender and
     * size of "hmtx" table, as a fixed size font might have only
     * one width.
     *
     * @throws IOException in case of an I/O problem
     */
    protected void readHorizontalHeader() {
    }

    /**
     * Read "hmtx" table and put the horizontal metrics
     * in the mtxTab array. If the number of metrics is less
     * than the number of glyphs (eg fixed size fonts), extend
     * the mtxTab array and fill in the missing widths
     *
     * @throws IOException in case of an I/O problem
     */
    protected void readHorizontalMetrics() {
    }

    /**
     * Read the "post" table
     * containing the PostScript names of the glyphs.
     */
    protected void readPostScript() throws IOException {
        int postFormat = fontFile.readInt();
        italicAngle = fontFile.readInt();
        underlinePosition = fontFile.readShort();
        underlineThickness = fontFile.readShort();
        isFixedPitch = fontFile.readInt();

        //Skip memory usage values
        fontFile.skip(4 * 4);

        switch (postFormat) {
            case 0x00010000:
                //PostScript format 1
                postScriptVersion = PostScriptVersion.V1;
                for (int i = 0; i < MAC_GLYPH_ORDERING.length; i++) {
                    mtxTab[i].setName(MAC_GLYPH_ORDERING[i]);
                }
                break;
            case 0x00020000:
                //PostScript format 2
                postScriptVersion = PostScriptVersion.V2;
                int numGlyphStrings = 257;

                // Read Number of Glyphs
                int l = fontFile.readUnsignedShort();

                // Read indexes
                for (int i = 0; i < l; i++) {
                    mtxTab[i].setIndex(fontFile.readUnsignedShort());

                    if (mtxTab[i].getIndex() > numGlyphStrings) {
                        numGlyphStrings = mtxTab[i].getIndex();
                    }
                }

                // firstChar=minIndex;
                String[] psGlyphsBuffer = new String[numGlyphStrings - 257];
                for (int i = 0; i < psGlyphsBuffer.length; i++) {
                    psGlyphsBuffer[i] = fontFile.readString(fontFile.readUnsignedByte());
                }

                //Set glyph names
                for (int i = 0; i < l; i++) {
                    if (mtxTab[i].getIndex() < MAC_GLYPH_ORDERING.length) {
                        mtxTab[i].setName(MAC_GLYPH_ORDERING[mtxTab[i].getIndex()]);
                    } else {
                        if (!mtxTab[i].isIndexReserved()) {
                            int k = mtxTab[i].getIndex() - MAC_GLYPH_ORDERING.length;

                            mtxTab[i].setName(psGlyphsBuffer[k]);
                        }
                    }
                }

                break;
            case 0x00030000:
                // PostScript format 3 contains no glyph names
                postScriptVersion = PostScriptVersion.V3;
                break;
            default:
                // Unknown PostScript format
                postScriptVersion = PostScriptVersion.UNKNOWN;
        }
    }

    /**
     * Read the "OS/2" table
     */
    protected void readOS2() {
    }

    /**
     * Read the "PCLT" table to find xHeight and capHeight.
     *
     * @throws IOException In case of a I/O problem
     */
    protected boolean readPCLT() throws IOException {
        return false;
    }

    /**
     * Determines the right source for the ascender and descender values. The problem here is
     * that the interpretation of these values is not the same for every font. There doesn't seem
     * to be a uniform definition of an ascender and a descender. In some fonts
     * the hhea values are defined after the Apple interpretation, but not in every font. The
     * same problem is in the OS/2 table. FOP needs the ascender and descender to determine the
     * baseline so we need values which add up more or less to the "em box". However, due to
     * accent modifiers a character can grow beyond the em box.
     */
    protected void determineAscDesc() {
        int hheaBoxHeight = hheaAscender - hheaDescender;
        int os2BoxHeight = os2Ascender - os2Descender;
        if (os2Ascender > 0 && os2BoxHeight <= upem) {
            ascender = os2Ascender;
            descender = os2Descender;
        } else if (hheaAscender > 0 && hheaBoxHeight <= upem) {
            ascender = hheaAscender;
            descender = hheaDescender;
        } else {
            if (os2Ascender > 0) {
                //Fall back to info from OS/2 if possible
                ascender = os2Ascender;
                descender = os2Descender;
            } else {
                ascender = hheaAscender;
                descender = hheaDescender;
            }
        }
    }

    protected void guessVerticalMetricsFromGlyphBBox() {
        // Approximate capHeight from height of "H"
        // It's most unlikely that a font misses the PCLT table
        // This also assumes that postscriptnames exists ("H")
        // Should look it up in the cmap (that wouldn't help
        // for charsets without H anyway...)
        // Same for xHeight with the letter "x"
        int localCapHeight = 0;
        int localXHeight = 0;
        int localAscender = 0;
        int localDescender = 0;
        for (OFMtxEntry aMtxTab : mtxTab) {
            if ("H".equals(aMtxTab.getName())) {
                localCapHeight = aMtxTab.getBoundingBox()[3];
            } else if ("x".equals(aMtxTab.getName())) {
                localXHeight = aMtxTab.getBoundingBox()[3];
            } else if ("d".equals(aMtxTab.getName())) {
                localAscender = aMtxTab.getBoundingBox()[3];
            } else if ("p".equals(aMtxTab.getName())) {
                localDescender = aMtxTab.getBoundingBox()[1];
            } else {
                // OpenType Fonts with a version 3.0 "post" table don't have glyph names.
                // Use Unicode indices instead.
                List unicodeIndex = aMtxTab.getUnicodeIndex();
                if (unicodeIndex.size() > 0) {
                    //Only the first index is used
                    char ch = (char) ((Integer) unicodeIndex.get(0)).intValue();
                    if (ch == 'H') {
                        localCapHeight = aMtxTab.getBoundingBox()[3];
                    } else if (ch == 'x') {
                        localXHeight = aMtxTab.getBoundingBox()[3];
                    } else if (ch == 'd') {
                        localAscender = aMtxTab.getBoundingBox()[3];
                    } else if (ch == 'p') {
                        localDescender = aMtxTab.getBoundingBox()[1];
                    }
                }
            }
        }
        if (ascender - descender > upem) {
            ascender = localAscender;
            descender = localDescender;
        }

        if (capHeight == 0) {
            capHeight = localCapHeight;
            if (capHeight == 0) {
                capHeight = os2CapHeight;
            }
        }
        if (xHeight == 0) {
            xHeight = localXHeight;
            if (xHeight == 0) {
                xHeight = os2xHeight;
            }
        }
    }

    /**
     * Read the kerning table, create a table for both CIDs and
     * winAnsiEncoding.
     *
     * @throws IOException In case of a I/O problem
     */
    protected void readKerning() throws IOException {
        // Read kerning
        kerningTab = new HashMap<Integer, Map<Integer, Integer>>();
        ansiKerningTab = new HashMap<Integer, Map<Integer, Integer>>();
        OFDirTabEntry dirTab = dirTabs.get(OFTableName.KERN);
        if (dirTab != null) {
            seekTab(fontFile, OFTableName.KERN, 2);
            for (int n = fontFile.readUnsignedShort(); n > 0; n--) {
                fontFile.skip(2 * 2);
                int k = fontFile.readUnsignedShort();
                if (!((k & 1) != 0) || (k & 2) != 0 || (k & 4) != 0) {
                    return;
                }
                if ((k >> 8) != 0) {
                    continue;
                }

                k = fontFile.readUnsignedShort();
                fontFile.skip(3 * 2);
                while (k-- > 0) {
                    int i = fontFile.readUnsignedShort();
                    int j = fontFile.readUnsignedShort();
                    int kpx = fontFile.readShort();
                    if (kpx != 0) {
                        // CID kerning table entry, using unicode indexes
                        final Integer iObj = glyphToUnicode(i);
                        final Integer u2 = glyphToUnicode(j);
                        if (iObj == null) {
                            // happens for many fonts (Ubuntu font set),
                            // stray entries in the kerning table??
                        } else if (u2 == null) {
                        } else {
                            Map<Integer, Integer> adjTab = kerningTab.get(iObj);
                            if (adjTab == null) {
                                adjTab = new HashMap<Integer, Integer>();
                            }
                            adjTab.put(u2, convertTTFUnit2PDFUnit(kpx));
                            kerningTab.put(iObj, adjTab);
                        }
                    }
                }
            }

            // Create winAnsiEncoded kerning table from kerningTab
            // (could probably be simplified, for now we remap back to CID indexes and
            // then to winAnsi)

            for (Map.Entry<Integer, Map<Integer, Integer>> e1 : kerningTab.entrySet()) {
                Integer unicodeKey1 = e1.getKey();
                Integer cidKey1 = unicodeToGlyph(unicodeKey1);
                Map<Integer, Integer> akpx = new HashMap<Integer, Integer>();
                Map<Integer, Integer> ckpx = e1.getValue();

                for (Map.Entry<Integer, Integer> e : ckpx.entrySet()) {
                    Integer unicodeKey2 = e.getKey();
                    Integer cidKey2 = unicodeToGlyph(unicodeKey2);
                    Integer kern = e.getValue();

                    for (Object o : mtxTab[cidKey2].getUnicodeIndex()) {
                        Integer unicodeKey = (Integer) o;
                        Integer[] ansiKeys = unicodeToWinAnsi(unicodeKey);
                        for (Integer ansiKey : ansiKeys) {
                            akpx.put(ansiKey, kern);
                        }
                    }
                }

                if (akpx.size() > 0) {
                    for (Object o : mtxTab[cidKey1].getUnicodeIndex()) {
                        Integer unicodeKey = (Integer) o;
                        Integer[] ansiKeys = unicodeToWinAnsi(unicodeKey);
                        for (Integer ansiKey : ansiKeys) {
                            ansiKerningTab.put(ansiKey, akpx);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns this font's character to glyph mapping.
     *
     * @return the font's cmap
     */
    public List<CMapSegment> getCMaps() {
        return cmaps;
    }

    /**
     * Check if this is a TrueType collection and that the given
     * name exists in the collection.
     * If it does, set offset in fontfile to the beginning of
     * the Table Directory for that font.
     *
     * @param name The name to check
     * @return True if not collection or font name present, false otherwise
     * @throws IOException In case of an I/O problem
     */
    protected final boolean checkTTC(String tag, String name) throws IOException {
        if ("ttcf".equals(tag)) {
            // This is a TrueType Collection
            fontFile.skip(4);

            // Read directory offsets
            int numDirectories = fontFile.readInt();
            // int numDirectories=in.readUnsignedShort();
            long[] dirOffsets = new long[numDirectories];
            for (int i = 0; i < numDirectories; i++) {
                dirOffsets[i] = fontFile.readInt();
            }

            // Read all the directories and name tables to check
            // If the font exists - this is a bit ugly, but...
            boolean found = false;

            // Iterate through all name tables even if font
            // Is found, just to show all the names
            long dirTabOffset = 0;
            for (int i = 0; (i < numDirectories); i++) {
                fontFile.seek(dirOffsets[i]);
                readDirTabs();

                readName();

                if (fullName.equals(name)) {
                    found = true;
                    dirTabOffset = dirOffsets[i];
                }

                // Reset names
                notice = "";
                fullName = "";
                familyNames.clear();
                postScriptName = "";
                subFamilyName = "";
            }

            fontFile.seek(dirTabOffset);
            return found;
        } else {
            fontFile.seek(0);
            return true;
        }
    }

    /**
     * Return TTC font names
     *
     * @param in FontFileReader to read from
     * @return True if not collection or font name present, false otherwise
     * @throws IOException In case of an I/O problem
     */
    public final List<String> getTTCNames(FontFileReader in) throws IOException {
        this.fontFile = in;

        List<String> fontNames = new ArrayList<>();
        in.seek(0);
        String tag = in.readString(4);

        if ("ttcf".equals(tag)) {
            // This is a TrueType Collection
            in.skip(4);

            // Read directory offsets
            int numDirectories = in.readInt();
            long[] dirOffsets = new long[numDirectories];
            for (int i = 0; i < numDirectories; i++) {
                dirOffsets[i] = in.readInt();
            }

            for (int i = 0; (i < numDirectories); i++) {
                in.seek(dirOffsets[i]);
                readDirTabs();

                readName();

                fontNames.add(fullName);

                // Reset names
                notice = "";
                fullName = "";
                familyNames.clear();
                postScriptName = "";
                subFamilyName = "";
            }

            in.seek(0);
            return fontNames;
        } else {
            return null;
        }
    }

    /*
     * Helper classes, they are not very efficient, but that really
     * doesn't matter...
     */
    private Integer[] unicodeToWinAnsi(int unicode) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 32; i < 256; i++) {
            if (unicode == i) {
                ret.add(i);
            }
        }
        return ret.toArray(new Integer[ret.size()]);
    }

    /**
     * Dumps a few informational values to System.out.
     */
    public void printStuff() {
        System.out.println("Font name:   " + postScriptName);
        System.out.println("Full name:   " + fullName);
        System.out.println("Family name: " + familyNames);
        System.out.println("Subfamily name: " + subFamilyName);
        System.out.println("Notice:      " + notice);
        System.out.println("xHeight:     " + convertTTFUnit2PDFUnit(xHeight));
        System.out.println("capheight:   " + convertTTFUnit2PDFUnit(capHeight));

        int italic = (int) (italicAngle >> 16);
        System.out.println("Italic:      " + italic);
        System.out.print("ItalicAngle: " + (short) (italicAngle / 0x10000));
        if ((italicAngle % 0x10000) > 0) {
            System.out.print("."
                    + (short) ((italicAngle % 0x10000) * 1000)
                    / 0x10000);
        }
        System.out.println();
        System.out.println("Weight:      " + usWeightClass);
        System.out.println("Ascender:    " + convertTTFUnit2PDFUnit(ascender));
        System.out.println("Descender:   " + convertTTFUnit2PDFUnit(descender));
        System.out.println("FontBBox:    [" + convertTTFUnit2PDFUnit(fontBBox1)
                + " " + convertTTFUnit2PDFUnit(fontBBox2) + " "
                + convertTTFUnit2PDFUnit(fontBBox3) + " "
                + convertTTFUnit2PDFUnit(fontBBox4) + "]");
    }

    /**
     * Map a glyph index to the corresponding unicode code point
     *
     * @param glyphIndex
     * @return unicode code point
     */
    private Integer glyphToUnicode(int glyphIndex) {
        return glyphToUnicodeMap.get(glyphIndex);
    }

    /**
     * Map a unicode code point to the corresponding glyph index
     *
     * @param unicodeIndex unicode code point
     * @return glyph index
     */
    private Integer unicodeToGlyph(int unicodeIndex) throws IOException {
        final Integer result
                = unicodeToGlyphMap.get(unicodeIndex);
        if (result == null) {
            throw new IOException(
                    "Glyph index not found for unicode value " + unicodeIndex);
        }
        return result;
    }

    String getGlyphName(int glyphIndex) {
        return mtxTab[glyphIndex].getName();
    }

    public String getEmbedFontName() {
        return embedFontName;
    }

    public String getCopyrightNotice() {
        return notice;
    }

    /**
     * Version of the PostScript table (post) contained in this font.
     */
    public enum PostScriptVersion {
        /**
         * PostScript table version 1.0.
         */
        V1,
        /**
         * PostScript table version 2.0.
         */
        V2,
        /**
         * PostScript table version 3.0.
         */
        V3,
        /**
         * Unknown version of the PostScript table.
         */
        UNKNOWN
    }

    /**
     * Key-value helper class.
     */
    static final class UnicodeMapping implements Comparable {

        private final int unicodeIndex;
        private final int glyphIndex;

        UnicodeMapping(OpenFont font, int glyphIndex, int unicodeIndex) {
            this.unicodeIndex = unicodeIndex;
            this.glyphIndex = glyphIndex;
            font.glyphToUnicodeMap.put(glyphIndex, unicodeIndex);
            font.unicodeToGlyphMap.put(unicodeIndex, glyphIndex);
        }

        /**
         * Returns the glyphIndex.
         *
         * @return the glyph index
         */
        public int getGlyphIndex() {
            return glyphIndex;
        }

        /**
         * Returns the unicodeIndex.
         *
         * @return the Unicode index
         */
        public int getUnicodeIndex() {
            return unicodeIndex;
        }


        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            int hc = unicodeIndex;
            hc = 19 * hc + (hc ^ glyphIndex);
            return hc;
        }

        /**
         * {@inheritDoc}
         */
        public boolean equals(Object o) {
            if (o instanceof UnicodeMapping) {
                UnicodeMapping m = (UnicodeMapping) o;
                if (unicodeIndex != m.unicodeIndex) {
                    return false;
                } else {
                    return (glyphIndex == m.glyphIndex);
                }
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        public int compareTo(Object o) {
            if (o instanceof UnicodeMapping) {
                UnicodeMapping m = (UnicodeMapping) o;
                if (unicodeIndex > m.unicodeIndex) {
                    return 1;
                } else if (unicodeIndex < m.unicodeIndex) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return -1;
            }
        }
    }
}
