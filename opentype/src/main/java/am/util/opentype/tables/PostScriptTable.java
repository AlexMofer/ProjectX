package am.util.opentype.tables;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
            "parenleft", "parenright", "asterisk", "plus", "comma",
            "hyphen", "period", "slash", "zero", "one",
            "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "colon", "semicolon",
            "less", "equal", "greater", "question", "at",
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y",
            "Z", "bracketleft", "backslash", "bracketright", "asciicircum",
            "underscore", "grave", "a", "b", "c",
            "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w",
            "x", "y", "z", "braceleft", "bar",
            "braceright", "asciitilde", "Adieresis", "Aring", "Ccedilla",
            "Eacute", "Ntilde", "Odieresis", "Udieresis", "aacute",
            "agrave", "acircumflex", "adieresis", "atilde", "aring",
            "ccedilla", "eacute", "egrave", "ecircumflex", "edieresis",
            "iacute", "igrave", "icircumflex", "idieresis", "ntilde",
            "oacute", "ograve", "ocircumflex", "odieresis", "otilde",
            "uacute", "ugrave", "ucircumflex", "udieresis", "dagger",
            "degree", "cent", "sterling", "section", "bullet", "paragraph",
            "germandbls", "registered", "copyright", "trademark",
            "acute", "dieresis", "notequal", "AE", "Oslash",
            "infinity", "plusminus", "lessequal", "greaterequal", "yen",
            "mu", "partialdiff", "summation", "product", "pi",
            "integral", "ordfeminine", "ordmasculine", "Omega", "ae",
            "oslash", "questiondown", "exclamdown", "logicalnot", "radical",
            "florin", "approxequal", "Delta", "guillemotleft", "guillemotright",
            "ellipsis", "nonbreakingspace", "Agrave", "Atilde", "Otilde",
            "OE", "oe", "endash", "emdash", "quotedblleft",
            "quotedblright", "quoteleft", "quoteright", "divide", "lozenge",
            "ydieresis", "Ydieresis", "fraction", "currency", "guilsinglleft",
            "guilsinglright", "fi", "fl", "daggerdbl", "periodcentered",
            "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex", "Ecircumflex",
            "Aacute", "Edieresis", "Egrave", "Iacute", "Icircumflex",
            "Idieresis", "Igrave", "Oacute", "Ocircumflex", "apple",
            "Ograve", "Uacute", "Ucircumflex", "Ugrave", "dotlessi",
            "circumflex", "tilde", "macron", "breve", "dotaccent",
            "ring", "cedilla", "hungarumlaut", "ogonek", "caron",
            "Lslash", "lslash", "Scaron", "scaron", "Zcaron",
            "zcaron", "brokenbar", "Eth", "eth", "Yacute",
            "yacute", "Thorn", "thorn", "minus", "multiply",
            "onesuperior", "twosuperior", "threesuperior", "onehalf", "onequarter",
            "threequarters", "franc", "Gbreve", "gbreve", "Idotaccent",
            "Scedilla", "scedilla", "Cacute", "cacute", "Ccaron",
            "ccaron", "dcroat"
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
    //    private final ArrayList<String> mNames;
    private final ArrayList<byte[]> mNames;
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
        final ArrayList<byte[]> names;
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
                    final byte[] name = new byte[length];
                    reader.read(name, 0, length);
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
     * Glyph name
     *
     * @param glyphNameIndex Glyph name index.
     * @param charsetName    The name of a supported {@linkplain java.nio.charset.Charset
     *                       charset}
     * @return Glyph name.
     */
    public byte[] getGlyphNameBytes(int glyphNameIndex, String charsetName)
            throws UnsupportedEncodingException {
        if (glyphNameIndex >= 258) {
            return mNames.get(glyphNameIndex - 258);
        } else {
            return MAC_GLYPHS[glyphNameIndex].getBytes(charsetName);
        }
    }

    /**
     * Glyph name
     *
     * @param glyphNameIndex Glyph name index.
     * @param charsetName    The name of a supported {@linkplain java.nio.charset.Charset
     *                       charset}
     * @return Glyph name.
     */
    @SuppressWarnings("all")
    public String getGlyphName(int glyphNameIndex, String charsetName)
            throws UnsupportedEncodingException {
        if (glyphNameIndex >= 258) {
            return new String(mNames.get(glyphNameIndex - 258), charsetName);
        } else {
            return MAC_GLYPHS[glyphNameIndex];
        }
    }

    /**
     * Glyph name
     *
     * @param glyphNameIndex Glyph name index.
     * @return Glyph name.
     */
    public String getGlyphName(int glyphNameIndex)
            throws UnsupportedEncodingException {
        return getGlyphName(glyphNameIndex, FileOpenTypeReader.CHARSET_ISO_8859_15);
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
