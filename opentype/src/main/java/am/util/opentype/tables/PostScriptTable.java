package am.util.opentype.tables;

import java.io.IOException;
import java.util.ArrayList;

import am.util.opentype.FileOpenTypeReader;
import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * PostScript Table
 * This table contains additional information needed to use TrueType or OpenType™ fonts
 * on PostScript printers. This includes data for the FontInfo dictionary entry and
 * the PostScript names of all the glyphs. For more information about PostScript names,
 * see the Adobe Glyph List Specification at https://github.com/adobe-type-tools/agl-specification .
 * <p>
 * Versions 1.0, 2.0, and 2.5 refer to TrueType fonts and OpenType fonts with TrueType data.
 * OpenType fonts with TrueType data may also use Version 3.0.
 * OpenType fonts with CFF data use Version 3.0 only.
 */
@SuppressWarnings("unused")
public class PostScriptTable {

    private static final String[] MAC_GLYPHS = {
            ".notdef", ".null", "nonmarkingreturn", "space", "exclam", "quotedbl",
            "numbersign", "dollar", "percent", "ampersand", "quotesingle",
            "11\tparenleft",
            "12\tparenright",
            "13\tasterisk",
            "14\tplus",
            "15\tcomma",
            "16\thyphen",
            "17\tperiod",
            "18\tslash",
            "19\tzero",
            "20\tone",
            "21\ttwo",
            "22\tthree",
            "23\tfour",
            "24\tfive",
            "25\tsix",
            "26\tseven",
            "27\teight",
            "28\tnine",
            "29\tcolon",
            "30\tsemicolon",
            "31\tless",
            "32\tequal",
            "33\tgreater",
            "34\tquestion",
            "35\tat",
            "36\tA",
            "37\tB",
            "38\tC",
            "39\tD",
            "40\tE",
            "41\tF",
            "42\tG",
            "43\tH",
            "44\tI",
            "45\tJ",
            "46\tK",
            "47\tL",
            "48\tM",
            "49\tN",
            "50\tO",
            "51\tP",
            "52\tQ",
            "53\tR",
            "54\tS",
            "55\tT",
            "56\tU",
            "57\tV",
            "58\tW",
            "59\tX",
            "60\tY",
            "61\tZ",
            "62\tbracketleft",
            "63\tbackslash",
            "64\tbracketright",
            "65\tasciicircum",
            "66\tunderscore",
            "67\tgrave",
            "68\ta",
            "69\tb",
            "70\tc",
            "71\td",
            "72\te",
            "73\tf",
            "74\tg",
            "75\th",
            "76\ti",
            "77\tj",
            "78\tk",
            "79\tl",
            "80\tm",
            "81\tn",
            "82\to",
            "83\tp",
            "84\tq",
            "85\tr",
            "86\ts",
            "87\tt",
            "88\tu",
            "89\tv",
            "90\tw",
            "91\tx",
            "92\ty",
            "93\tz",
            "94\tbraceleft",
            "95\tbar",
            "96\tbraceright",
            "97\tasciitilde",
            "98\tAdieresis",
            "99\tAring",
            "100\tCcedilla",
            "101\tEacute",
            "102\tNtilde",
            "103\tOdieresis",
            "104\tUdieresis",
            "105\taacute",
            "106\tagrave",
            "107\tacircumflex",
            "108\tadieresis",
            "109\tatilde",
            "110\taring",
            "111\tccedilla",
            "112\teacute",
            "113\tegrave",
            "114\tecircumflex",
            "115\tedieresis",
            "116\tiacute",
            "117\tigrave",
            "118\ticircumflex",
            "119\tidieresis",
            "120\tntilde",
            "121\toacute",
            "122\tograve",
            "123\tocircumflex",
            "124\todieresis",
            "125\totilde",
            "126\tuacute",
            "127\tugrave",
            "128\tucircumflex",
            "129\tudieresis",
            "130\tdagger",
            "131\tdegree",
            "132\tcent",
            "133\tsterling",
            "134\tsection",
            "135\tbullet",
            "136\tparagraph",
            "137\tgermandbls",
            "138\tregistered",
            "139\tcopyright",
            "140\ttrademark",
            "141\tacute",
            "142\tdieresis",
            "143\tnotequal",
            "144\tAE",
            "145\tOslash",
            "146\tinfinity",
            "147\tplusminus",
            "148\tlessequal",
            "149\tgreaterequal",
            "150\tyen",
            "151\tmu",
            "152\tpartialdiff",
            "153\tsummation",
            "154\tproduct",
            "155\tpi",
            "156\tintegral",
            "157\tordfeminine",
            "158\tordmasculine",
            "159\tOmega",
            "160\tae",
            "161\toslash",
            "162\tquestiondown",
            "163\texclamdown",
            "164\tlogicalnot",
            "165\tradical",
            "166\tflorin",
            "167\tapproxequal",
            "168\tDelta",
            "169\tguillemotleft",
            "170\tguillemotright",
            "171\tellipsis",
            "172\tnonbreakingspace",
            "173\tAgrave",
            "174\tAtilde",
            "175\tOtilde",
            "176\tOE",
            "177\toe",
            "178\tendash",
            "179\temdash",
            "180\tquotedblleft",
            "181\tquotedblright",
            "182\tquoteleft",
            "183\tquoteright",
            "184\tdivide",
            "185\tlozenge",
            "186\tydieresis",
            "187\tYdieresis",
            "188\tfraction",
            "189\tcurrency",
            "190\tguilsinglleft",
            "191\tguilsinglright",
            "192\tfi",
            "193\tfl",
            "194\tdaggerdbl",
            "195\tperiodcentered",
            "196\tquotesinglbase",
            "197\tquotedblbase",
            "198\tperthousand",
            "199\tAcircumflex",
            "200\tEcircumflex",
            "201\tAacute",
            "202\tEdieresis",
            "203\tEgrave",
            "204\tIacute",
            "205\tIcircumflex",
            "206\tIdieresis",
            "207\tIgrave",
            "208\tOacute",
            "209\tOcircumflex",
            "210\tapple",
            "211\tOgrave",
            "212\tUacute",
            "213\tUcircumflex",
            "214\tUgrave",
            "215\tdotlessi",
            "216\tcircumflex",
            "217\ttilde",
            "218\tmacron",
            "219\tbreve",
            "220\tdotaccent",
            "221\tring",
            "222\tcedilla",
            "223\thungarumlaut",
            "224\togonek",
            "225\tcaron",
            "226\tLslash",
            "227\tlslash",
            "228\tScaron",
            "229\tscaron",
            "230\tZcaron",
            "231\tzcaron",
            "232\tbrokenbar",
            "233\tEth",
            "234\teth",
            "235\tYacute",
            "236\tyacute",
            "237\tThorn",
            "238\tthorn",
            "239\tminus",
            "240\tmultiply",
            "241\tonesuperior",
            "242\ttwosuperior",
            "243\tthreesuperior",
            "244\tonehalf",
            "245\tonequarter",
            "246\tthreequarters",
            "247\tfranc",
            "248\tGbreve",
            "249\tgbreve",
            "250\tIdotaccent",
            "251\tScedilla",
            "252\tscedilla",
            "253\tCacute",
            "254\tcacute",
            "255\tCcaron",
            "256\tccaron",
            "257\tdcroat"
    };

    private final float mVersion;
    private final float mItalicAngle;
    private final int mUnderlinePosition;
    private final int mUnderlineThickness;
    private final int mIsFixedPitch;
    private final int mMinMemType42;
    private final int mMaxMemType42;
    private final int mMinMemType1;
    private final int mMaxMemType1;
    private final int mNumGlyphs;
    private final int[] mGlyphNameIndex;
    private final ArrayList<String> mNames;
    private final int[] mOffset;

    public PostScriptTable(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_POST)
            throw new IOException();
        reader.seek(record.getOffset());
        final float version = reader.readFixed();
        final float italicAngle = reader.readFixed();
        final int underlinePosition = reader.readShort();
        final int underlineThickness = reader.readShort();
        final int isFixedPitch = reader.readUnsignedInt();
        final int minMemType42 = reader.readUnsignedInt();
        final int maxMemType42 = reader.readUnsignedInt();
        final int minMemType1 = reader.readUnsignedInt();
        final int maxMemType1 = reader.readUnsignedInt();
        final int numGlyphs;
        final int[] glyphNameIndex;
        final ArrayList<String> names;
        final int[] offset;
        if (version == 1.0f) {
            // This version is used in order to supply PostScript glyph names when the font file
            // contains exactly the 258 glyphs in the standard Macintosh TrueType font file
            // (see 'post' Format 1 in Apple’s specification
            // (https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6post.html)
            // for a list of the 258 Macintosh
            // glyph names), and the font does not otherwise supply glyph names.
            // As a result, the glyph names are taken from the system
            // with no storage required by the font.
            numGlyphs = -1;
            glyphNameIndex = null;
            names = null;
            offset = null;
        } else if (version == 2.0f) {
            // This is the version required in order to supply PostScript glyph names for fonts
            // which do not supply them elsewhere. A version 2.0 'post' table can be used in fonts
            // with TrueType or CFF version 2 outlines.
            numGlyphs = reader.readUnsignedShort();
            glyphNameIndex = new int[numGlyphs];
            int numberNewGlyphs = 0;
            for (int i = 0; i < numGlyphs; i++) {
                final int index = reader.readUnsignedShort();
                glyphNameIndex[i] = index;
                if (index >= 258) {
                    numberNewGlyphs++;
                }
            }
            if (numberNewGlyphs > 0) {
                names = new ArrayList<>();
                for (int i = 0; i < numberNewGlyphs; i++) {
                    final int length = reader.readUnsignedByte();
                    names.add(reader.readString(length, FileOpenTypeReader.CHARSET_ISO_8859_15));
                }
            } else {
                names = null;
            }
            offset = null;
        } else if (version == 2.5f) {
            // This version of the 'post' table has been deprecated as of OpenType
            // Specification v1.3.
            // This version provides a space-saving table for TrueType-based fonts which contain
            // a pure subset of, or a simple reordering of, the standard Macintosh glyph set.
            numGlyphs = reader.readUnsignedShort();
            glyphNameIndex = null;
            names = null;
            offset = new int[numGlyphs];
            for (int i = 0; i < numGlyphs; i++) {
                offset[i] = reader.read();
            }
        } else if (version == 3.0f) {
            // This version makes it possible to create a font that is not burdened with
            // a large 'post' table set of glyph names. A version 3.0 'post' table can be used by
            // OpenType fonts with TrueType or CFF (version 1 or 2) data.
            // This version specifies that no PostScript name information is provided for
            // the glyphs in this font file. The printing behavior of this version on
            // PostScript printers is unspecified, except that it should not result in
            // a fatal or unrecoverable error. Some drivers may print nothing;
            // other drivers may attempt to print using a default naming scheme.
            // Windows makes use of the italic angle value in the 'post' table but does not
            // actually require any glyph names to be stored as Pascal strings.
            numGlyphs = -1;
            glyphNameIndex = null;
            names = null;
            offset = null;
        } else {
            // Apple has defined a version 4.0 for use with Apple Advanced Typography (AAT),
            // which is described in their documentation.
            numGlyphs = -1;
            glyphNameIndex = null;
            names = null;
            offset = null;
        }
        mVersion = version;
        mItalicAngle = italicAngle;
        mUnderlinePosition = underlinePosition;
        mUnderlineThickness = underlineThickness;
        mIsFixedPitch = isFixedPitch;
        mMinMemType42 = minMemType42;
        mMaxMemType42 = maxMemType42;
        mMinMemType1 = minMemType1;
        mMaxMemType1 = maxMemType1;
        mNumGlyphs = numGlyphs;
        mGlyphNameIndex = glyphNameIndex;
        mNames = names;
        mOffset = offset;
    }

    /**
     * version 1.0 is 0x00010000
     * version 2.0 is 0x00020000
     * version 2.5 is 0x00025000 (deprecated)
     * version 3.0 is 0x00030000
     * version 4.0 is 0x00040000
     *
     * @return Version.
     */
    public float getVersion() {
        return mVersion;
    }

    /**
     * Italic angle in counter-clockwise degrees from the vertical. Zero for upright text,
     * negative for text that leans to the right (forward).
     *
     * @return Italic angle.
     */
    public float getItalicAngle() {
        return mItalicAngle;
    }

    /**
     * This is the suggested distance of the top of the underline from the baseline
     * (negative values indicate below baseline).
     * The PostScript definition of this FontInfo dictionary key
     * (the y coordinate of the center of the stroke) is not used for historical reasons.
     * The value of the PostScript key may be calculated by subtracting half
     * the underlineThickness from the value of this field.
     *
     * @return Underline position.
     */
    public int getUnderlinePosition() {
        return mUnderlinePosition;
    }

    /**
     * Suggested values for the underline thickness. In general, the underline thickness
     * should match the thickness of the underscore character (U+005F LOW LINE),
     * and should also match the strikeout thickness, which is specified in the OS/2 table.
     *
     * @return Underline thickness.
     */
    public int getUnderlineThickness() {
        return mUnderlineThickness;
    }

    /**
     * Set to 0 if the font is proportionally spaced, non-zero if the font is
     * not proportionally spaced (i.e. monospaced).
     *
     * @return Is fixed pitch or not.
     */
    public int isFixedPitch() {
        return mIsFixedPitch;
    }

    /**
     * Minimum memory usage when an OpenType font is downloaded.
     *
     * @return Minimum memory usage.
     */
    public int getMinMemType42() {
        return mMinMemType42;
    }

    /**
     * Maximum memory usage when an OpenType font is downloaded.
     *
     * @return Maximum memory usage.
     */
    public int getMaxMemType42() {
        return mMaxMemType42;
    }

    /**
     * Minimum memory usage when an OpenType font is downloaded as a Type 1 font.
     *
     * @return Minimum memory usage.
     */
    public int getMinMemType1() {
        return mMinMemType1;
    }

    /**
     * Maximum memory usage when an OpenType font is downloaded as a Type 1 font.
     *
     * @return Maximum memory usage.
     */
    public int getMaxMemType1() {
        return mMaxMemType1;
    }

    /**
     * Number of glyphs (this should be the same as numGlyphs in 'maxp' table).
     * (Version 2.0 or 2.5)
     *
     * @return Number of glyphs.
     */
    public int getNumGlyphs() {
        return mNumGlyphs;
    }

    /**
     * This is not an offset, but is the ordinal number of the glyph in 'post' string tables.
     * (Version 2.0 only)
     *
     * @return Glyph name index.
     */
    public int[] getGlyphNameIndex() {
        return mGlyphNameIndex;
    }

    /**
     * Glyph names
     *
     * @param glyphNameIndex Glyph name index.
     * @return Glyph names.
     */
    public String getGlyphName(int glyphNameIndex) {
        if (glyphNameIndex >= 258) {
            return mNames.get(glyphNameIndex - 258);
        } else {
            return MAC_GLYPHS[glyphNameIndex];
        }
    }

    /**
     * Difference between graphic index and standard order of glyph.
     * (Version 2.5 only)
     *
     * @return Offset that maps the glyph index used in this font into the standard glyph index.
     */
    public int[] getOffset() {
        return mOffset;
    }
}
