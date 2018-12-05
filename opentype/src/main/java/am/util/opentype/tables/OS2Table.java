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

import java.io.IOException;
import java.util.Arrays;

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * OS/2 and Windows Metrics Table
 * The OS/2 table consists of a set of metrics and other data that are required in OpenType fonts.
 * Six versions of the OS/2 table have been defined: versions 0 to 5.
 * All versions are supported, but use of version 4 or later is strongly recommended.
 */
@SuppressWarnings("unused")
public class OS2Table extends BaseTable {
    private final int mVersion;
    private final int mAvgCharWidth;
    private final int mWeightClass;
    private final int mWidthClass;
    private final int mType;
    private final int mSubscriptXSize;
    private final int mSubscriptYSize;
    private final int mSubscriptXOffset;
    private final int mSubscriptYOffset;
    private final int mSuperscriptXSize;
    private final int mSuperscriptYSize;
    private final int mSuperscriptXOffset;
    private final int mSuperscriptYOffset;
    private final int mStrikeoutSize;
    private final int mStrikeoutPosition;
    private final int mFamilyClass;
    private final int[] mPanose;
    private final int mUnicodeRange1;
    private final int mUnicodeRange2;
    private final int mUnicodeRange3;
    private final int mUnicodeRange4;
    private final int mVendID;
    private final int mSelection;
    private final int mFirstCharIndex;
    private final int mLastCharIndex;
    private final boolean mLegacy;
    private final int mTypoAscender;
    private final int mTypoDescender;
    private final int mTypoLineGap;
    private final int mWinAscent;
    private final int mWinDescent;
    private final int mCodePageRange1;
    private final int mCodePageRange2;
    private final int mHeight;
    private final int mCapHeight;
    private final int mDefaultChar;
    private final int mBreakChar;
    private final int mMaxContext;
    private final int mLowerOpticalPointSize;
    private final int mUpperOpticalPointSize;

    public OS2Table(OpenTypeReader reader, TableRecord record) throws IOException {
        super(record);
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_OS2)
            throw new IOException();
        reader.seek(record.getOffset());
        final int version = reader.readUnsignedShort();
        // Version 0
        final int xAvgCharWidth = reader.readShort();
        final int usWeightClass = reader.readUnsignedShort();
        final int usWidthClass = reader.readUnsignedShort();
        final int fsType = reader.readUnsignedShort();
        final int ySubscriptXSize = reader.readShort();
        final int ySubscriptYSize = reader.readShort();
        final int ySubscriptXOffset = reader.readShort();
        final int ySubscriptYOffset = reader.readShort();
        final int ySuperscriptXSize = reader.readShort();
        final int ySuperscriptYSize = reader.readShort();
        final int ySuperscriptXOffset = reader.readShort();
        final int ySuperscriptYOffset = reader.readShort();
        final int yStrikeoutSize = reader.readShort();
        final int yStrikeoutPosition = reader.readShort();
        final int sFamilyClass = reader.readShort();
        final int[] panose = new int[10];
        for (int i = 0; i < 10; i++) {
            panose[i] = reader.readUnsignedByte();
        }
        final int ulUnicodeRange1 = reader.readUnsignedInt();
        final int ulUnicodeRange2 = reader.readUnsignedInt();
        final int ulUnicodeRange3 = reader.readUnsignedInt();
        final int ulUnicodeRange4 = reader.readUnsignedInt();
        final int achVendID = reader.readInt();
        final int fsSelection = reader.readUnsignedShort();
        final int usFirstCharIndex = reader.readUnsignedShort();
        final int usLastCharIndex = reader.readUnsignedShort();
        final boolean legacy;
        final int sTypoAscender;
        final int sTypoDescender;
        final int sTypoLineGap;
        final int usWinAscent;
        final int usWinDescent;
        // Microsoft Version 0
        if (version <= 0 && record.getLength() == 68) {
            legacy = true;
            sTypoAscender = 0;
            sTypoDescender = 0;
            sTypoLineGap = 0;
            usWinAscent = -1;
            usWinDescent = -1;
        } else {
            legacy = false;
            sTypoAscender = reader.readShort();
            sTypoDescender = reader.readShort();
            sTypoLineGap = reader.readShort();
            usWinAscent = reader.readUnsignedShort();
            usWinDescent = reader.readUnsignedShort();
        }
        // Version 1
        final int ulCodePageRange1;
        final int ulCodePageRange2;
        if (version <= 0) {
            ulCodePageRange1 = -1;
            ulCodePageRange2 = -1;
        } else {
            ulCodePageRange1 = reader.readUnsignedInt();
            ulCodePageRange2 = reader.readUnsignedInt();
        }
        // Version 2/3/4
        final int sxHeight;
        final int sCapHeight;
        final int usDefaultChar;
        final int usBreakChar;
        final int usMaxContext;
        if (version <= 1) {
            sxHeight = 0;
            sCapHeight = 0;
            usDefaultChar = -1;
            usBreakChar = -1;
            usMaxContext = -1;
        } else {
            sxHeight = reader.readShort();
            sCapHeight = reader.readShort();
            usDefaultChar = reader.readUnsignedShort();
            usBreakChar = reader.readUnsignedShort();
            usMaxContext = reader.readUnsignedShort();
        }
        // Version 5
        final int usLowerOpticalPointSize;
        final int usUpperOpticalPointSize;
        if (version <= 4) {
            usLowerOpticalPointSize = -1;
            usUpperOpticalPointSize = -1;
        } else {
            usLowerOpticalPointSize = reader.readUnsignedShort();
            usUpperOpticalPointSize = reader.readUnsignedShort();
        }
        mVersion = version;
        mAvgCharWidth = xAvgCharWidth;
        mWeightClass = usWeightClass;
        mWidthClass = usWidthClass;
        mType = fsType;
        mSubscriptXSize = ySubscriptXSize;
        mSubscriptYSize = ySubscriptYSize;
        mSubscriptXOffset = ySubscriptXOffset;
        mSubscriptYOffset = ySubscriptYOffset;
        mSuperscriptXSize = ySuperscriptXSize;
        mSuperscriptYSize = ySuperscriptYSize;
        mSuperscriptXOffset = ySuperscriptXOffset;
        mSuperscriptYOffset = ySuperscriptYOffset;
        mStrikeoutSize = yStrikeoutSize;
        mStrikeoutPosition = yStrikeoutPosition;
        mFamilyClass = sFamilyClass;
        mPanose = panose;
        mUnicodeRange1 = ulUnicodeRange1;
        mUnicodeRange2 = ulUnicodeRange2;
        mUnicodeRange3 = ulUnicodeRange3;
        mUnicodeRange4 = ulUnicodeRange4;
        mVendID = achVendID;
        mSelection = fsSelection;
        mFirstCharIndex = usFirstCharIndex;
        mLastCharIndex = usLastCharIndex;
        mLegacy = legacy;
        mTypoAscender = sTypoAscender;
        mTypoDescender = sTypoDescender;
        mTypoLineGap = sTypoLineGap;
        mWinAscent = usWinAscent;
        mWinDescent = usWinDescent;
        mCodePageRange1 = ulCodePageRange1;
        mCodePageRange2 = ulCodePageRange2;
        mHeight = sxHeight;
        mCapHeight = sCapHeight;
        mDefaultChar = usDefaultChar;
        mBreakChar = usBreakChar;
        mMaxContext = usMaxContext;
        mLowerOpticalPointSize = usLowerOpticalPointSize;
        mUpperOpticalPointSize = usUpperOpticalPointSize;
    }

    /**
     * The version number for the OS/2 table: 0x0000 to 0x0005.
     * The version number allows for identification of the precise contents and layout for
     * the OS/2 table.
     *
     * @return OS/2 table version number.
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * The Average Character Width parameter specifies the arithmetic average of the escapement
     * (width) of all non-zero width glyphs in the font.
     * The value for xAvgCharWidth is calculated by obtaining the arithmetic average of
     * the width of all non-zero width glyphs in the font. Furthermore,
     * it is strongly recommended that implementers do not rely on this value
     * for computing layout for lines of text, especially for cases where complex scripts are used.
     *
     * @return Average weighted escapement.
     */
    public int getAvgCharWidth() {
        return mAvgCharWidth;
    }

    /**
     * Indicates the visual weight (degree of blackness or thickness of strokes) of
     * the characters in the font. Values from 1 to 1000 are valid.
     * usWeightClass values use the same scale as the 'wght' axis that is used in the 'fvar' table
     * of variable fonts and in the STAT table. While integer values from 1 to 1000 are supported,
     * some legacy platforms may have limitations on supported values. The following are
     * commonly-used values:
     * Value Description               C Definition (from windows.h)
     * 100   Thin                      FW_THIN
     * 200   Extra-light (Ultra-light) FW_EXTRALIGHT
     * 300   Light                     FW_LIGHT
     * 400   Normal (Regular)          FW_NORMAL
     * 500   Medium                    FW_MEDIUM
     * 600   Semi-bold (Demi-bold)     FW_SEMIBOLD
     * 700   Bold                      FW_BOLD
     * 800   Extra-bold (Ultra-bold)   FW_EXTRABOLD
     * 900   Black (Heavy)             FW_BLACK
     *
     * @return Weight class.
     */
    public int getWeightClass() {
        return mWeightClass;
    }

    /**
     * Indicates a relative change from the normal aspect ratio (width to height ratio)
     * as specified by a font designer for the glyphs in a font.
     * Although every glyph in a font may have a different numeric aspect ratio,
     * each glyph in a font of normal width is considered to have a relative aspect ratio of one.
     * When a new type style is created of a different width class (either by a font designer or
     * by some automated means) the relative aspect ratio of the characters in the new font
     * is some percentage greater or less than those same characters in the normal font — it
     * is this difference that this parameter specifies. The valid usWidthClass values are
     * shown in the following table. Note that the usWidthClass values are related to but distinct
     * from the scale for the 'wdth' axis that is used in the 'fvar' table of variable fonts and
     * in the STAT table. The “% of normal” column in the following table provides a mapping
     * from usWidthClass values 1 – 9 to 'wdth' values.
     * Value Description     C Definition           % of normal
     * 1     Ultra-condensed FWIDTH_ULTRA_CONDENSED 50
     * 2	 Extra-condensed FWIDTH_EXTRA_CONDENSED 62.5
     * 3     Condensed       FWIDTH_CONDENSED       75
     * 4     Semi-condensed  FWIDTH_SEMI_CONDENSED  87.5
     * 5     Medium (normal) FWIDTH_NORMAL          100
     * 6     Semi-expanded   FWIDTH_SEMI_EXPANDED   112.5
     * 7     Expanded        FWIDTH_EXPANDED        125
     * 8     Extra-expanded  FWIDTH_EXTRA_EXPANDED  150
     * 9     Ultra-expanded  FWIDTH_ULTRA_EXPANDED  200
     *
     * @return Width class.
     */
    public int getWidthClass() {
        return mWidthClass;
    }

    /**
     * Indicates font embedding licensing rights for the font.
     * The interpretation of flags is as follows:
     * Bit(s)    Mask     Description
     * 0 – 3     0x000F   Usage permissions. Valid fonts must set at most one of bits 1, 2 or 3;
     * _                  bit 0 is permanently reserved and must be zero. Valid values for this
     * _                  sub-field are 0, 2, 4 or 8. The meaning of these values is as follows:
     * _                  0: Installable embedding: the font may be embedded, and may be
     * _                  permanently installed for use on a remote systems, or for use
     * _                  by other users. The user of the remote system acquires the identical
     * _                  rights, obligations and licenses for that font as the original purchaser
     * _                  of the font, and is subject to the same end-user license agreement,
     * _                  copyright, design patent, and/or trademark as was the original purchaser.
     * _                  2: Restricted License embedding: the font must not be modified,
     * _                  embedded or exchanged in any manner without first obtaining explicit
     * _                  permission of the legal owner.
     * _                  4: Preview &amp; Print embedding: the font may be embedded, and may be
     * _                  temporarily loaded on other systems for purposes of viewing or printing
     * _                  the document. Documents containing Preview &amp; Print fonts must be opened
     * _                  “read-only”; no edits can be applied to the document.
     * _                  8: Editable embedding: the font may be embedded, and may be temporarily
     * _                  loaded on other systems. As with Preview &amp; Print embedding, documents
     * _                  containing Editable fonts may be opened for reading. In addition,
     * _                  editing is permitted, including ability to format new text using the
     * _                  embedded font, and changes may be saved.
     * 4 – 7              Reserved, must be zero.
     * 8         0x0100   No subsetting: When this bit is set, the font may not be subsetted
     * _                  prior to embedding. Other embedding restrictions specified in bits 0 to 3
     * _                  and bit 9 also apply.
     * 9         0x0200   Bitmap embedding only: When this bit is set, only bitmaps contained
     * _                  in the font may be embedded. No outline data may be embedded.
     * _                  If there are no bitmaps available in the font, then the font is
     * _                  considered unembeddable and the embedding services will fail.
     * _                  Other embedding restrictions specified in bits 0-3 and 8 also apply.
     * 10 – 15            Reserved, must be zero.
     * Embeddable fonts may be stored in a document. When a document with embedded fonts
     * is opened on a system that does not have the font installed (the remote system),
     * the embedded font may be loaded for temporary (and in some cases, permanent) use
     * on that system by an embedding-aware application. Embedding licensing rights are granted
     * by the vendor of the font.
     * Applications that implement support for font embedding must not embed fonts which are not
     * licensed to permit embedding. Also, when embedding a font into a document,
     * applications must not modify the embedding permissions and restrictions indicated
     * in this field. In addition, applications loading embedded fonts for temporary use
     * (Preview &amp; Print or Editable embedding) must delete the fonts when the document containing
     * the embedded font is closed.
     * Bits 0 to 3 (the embedding permissions sub-field) are mutually exclusive:
     * fonts should never have more than of these bits set. Note that, if two or more bits are set,
     * some applications could assume the least-restrictive permission indicated.
     * (See version differences for more discussion.) Caution: Font vendors are responsible
     * to set these bits correctly to obtain the desired application behaviors.
     * For Restricted License embedding to take effect, the Embedding permissions sub-field
     * must have the value 2 (that is, only bit 1 is set).
     * Note: Apple’s TrueType Reference Manual(https://developer.apple.com/fonts/TrueType-Reference
     * -Manual/RM06/Chap6OS2.html) specifies bit 1 of the fsType field, and only bit 1,
     * as having an assigned semantic. This originated from a pre-release draft specification
     * for the OS/2 table. However, the final specification for version 0 of the OS/2 table
     * defined bits 0 to 3. Also, some early font implementations mistakenly used the value 1
     * (bit 0 set), leading to problems of non-interoperability. For this reason, bit 0 was
     * specified as reserved in the final specification for version 0. Bit 0 is permanently
     * reserved, and its use is deprecated.
     * Versions 0 to 1: only bits 0 to 3 were assigned. Applications must ignore bits 4 to 15
     * when reading a version 0 or version 1 table.
     * Versions 0 to 2: The specification for versions 0 to 2 did not specify that bits 0 to 3
     * must be mutually exclusive. Rather, those specifications stated that,
     * in the event that more than one of bits 0 to 3 are set in a given font,
     * then the least-restrictive permission indicated take precedence. In particular,
     * some fonts using a version 0 to version 2 OS/2 table have both bit 2 and bit 3
     * set with the intent to indicate both preview/print and edit permissions. Applications are
     * permitted to use this behavior for fonts with a version 0 to version 2 OS/2 table.
     * Versions 3 and later: The specification for version 3, added in OpenType 1.4,
     * introduced the explicit requirement that bits 0 to 3 must be mutually exclusive.
     *
     * @return Type flags.
     */
    public int getType() {
        return mType;
    }

    /**
     * The recommended horizontal size in font design units for subscripts for this font.
     * If a font has two recommended sizes for subscripts, e.g., numerics and other,
     * the numeric sizes should be stressed. This size field maps to the em size of the font
     * being used for a subscript. The horizontal font size specifies a font designer’s
     * recommended horizontal size of subscript glyphs associated with this font.
     * If a font does not include all of the required subscript glyphs for an application,
     * and the application can substitute glyphs by scaling the glyphs of a font or by
     * substituting glyphs from another font, this parameter specifies the recommended
     * nominal width for those subscript glyphs.
     * For example, if the em for a font is 2048 units and ySubScriptXSize is set to 205,
     * then the horizontal size for a simulated subscript glyph would be 1/10th the size of
     * the normal glyph.
     *
     * @return Subscript horizontal font size.
     */
    public int getSubscriptXSize() {
        return mSubscriptXSize;
    }

    /**
     * The recommended vertical size in font design units for subscripts for this font.
     * If a font has two recommended sizes for subscripts, e.g. numerics and other,
     * the numeric sizes should be stressed. This size field maps to the em size of the font
     * being used for a subscript. The vertical font size specifies a font designer’s
     * recommendation for vertical size of subscript glyphs associated with this font.
     * If a font does not include all of the required subscript glyphs for an application,
     * and the application can substitute glyphs by scaling the glyphs in a font or by
     * substituting glyphs from another font, this parameter specifies the recommended
     * nominal height for those subscript glyphs.
     * For example, if the em for a font is 2048 units and ySubScriptYSize is set to 205,
     * then the vertical size for a simulated subscript glyph would be 1/10th the size of
     * the normal glyph.
     *
     * @return Subscript vertical font size.
     */
    public int getSubscriptYSize() {
        return mSubscriptYSize;
    }

    /**
     * The recommended horizontal offset in font design units for subscripts for this font.
     * The Subscript X Offset parameter specifies a font designer’s recommended horizontal
     * offset — from the glyph origin to the glyph origin of the subscript’s glyph — for
     * subscript glyphs associated with this font. If a font does not include all of the
     * required subscript glyphs for an application, and the application can substitute glyphs,
     * this parameter specifies the recommended horizontal position from the glyph escapement
     * point of the last glyph before the first subscript glyph. For upright glyphs,
     * this value is usually zero; however, if the glyphs of a font have an incline
     * (italic or slant), the reference point for subscript glyphs is usually adjusted to
     * compensate for the angle of incline.
     *
     * @return Subscript x offset.
     */
    public int getSubscriptXOffset() {
        return mSubscriptXOffset;
    }

    /**
     * The recommended vertical offset in font design units from the baseline for subscripts
     * for this font.
     * The Subscript Y Offset parameter specifies a font designer’s recommended vertical
     * offset from the glyph baseline to the glyph baseline for subscript glyphs associated
     * with this font. Values are expressed as a positive offset below the glyph baseline.
     * If a font does not include all of the required subscript glyphs for an application,
     * this parameter specifies the recommended vertical distance below the glyph baseline
     * for those subscript glyphs.
     *
     * @return Subscript y offset.
     */
    public int getSubscriptYOffset() {
        return mSubscriptYOffset;
    }

    /**
     * The recommended horizontal size in font design units for superscripts for this font.
     * If a font has two recommended sizes for superscripts, e.g., numerics and other,
     * the numeric sizes should be stressed. This size field maps to the em size of the font
     * being used for a superscript. The horizontal font size specifies a font designer’s
     * recommended horizontal size for superscript glyphs associated with this font.
     * If a font does not include all of the required superscript glyphs for an application,
     * and the application can substitute glyphs by scaling the glyphs of a font or by
     * substituting glyphs from another font, this parameter specifies the recommended
     * nominal width for those superscript glyphs.
     * For example, if the em for a font is 2048 units and ySuperScriptXSize is set to 205,
     * then the horizontal size for a simulated superscript glyph would be 1/10th the size of
     * the normal glyph.
     *
     * @return Superscript horizontal font size.
     */
    public int getSuperscriptXSize() {
        return mSuperscriptXSize;
    }

    /**
     * The recommended vertical size in font design units for superscripts for this font.
     * If a font has two recommended sizes for superscripts, e.g., numerics and other,
     * the numeric sizes should be stressed. This size field maps to the em size of the font
     * being used for a superscript. The vertical font size specifies a font designer’s
     * recommended vertical size for superscript glyphs associated with this font. If a font
     * does not include all of the required superscript glyphs for an application,
     * and the application can substitute glyphs by scaling the glyphs of a font or by
     * substituting glyphs from another font, this parameter specifies the recommended
     * nominal height for those superscript glyphs.
     * For example, if the em for a font is 2048 units and ySuperScriptYSize is set to 205,
     * then the vertical size for a simulated superscript glyph would be 1/10th the size of
     * the normal glyph.
     *
     * @return Superscript vertical font size.
     */
    public int getSuperscriptYSize() {
        return mSuperscriptYSize;
    }

    /**
     * The recommended horizontal offset in font design units for superscripts for this font.
     * The Superscript X Offset parameter specifies a font designer’s recommended horizontal
     * offset — from the glyph’s origin to the superscript glyph’s origin for the superscript
     * characters associated with this font. If a font does not include all of the required
     * superscript characters for an application, this parameter specifies the recommended
     * horizontal position from the escapement point of the character before the first
     * superscript character. For upright characters, this value is usually zero; however,
     * if the characters of a font have an incline (italic characters) the reference point
     * for superscript characters is usually adjusted to compensate for the angle of incline.
     *
     * @return Superscript x offset.
     */
    public int getSuperscriptXOffset() {
        return mSuperscriptXOffset;
    }

    /**
     * The recommended vertical offset in font design units from the baseline for superscripts
     * for this font.
     * The Superscript Y Offset parameter specifies a font designer’s recommended vertical
     * offset — from the glyph’s baseline to the superscript glyph’s baseline associated with
     * this font. Values for this parameter are expressed as a positive offset above the
     * character baseline. If a font does not include all of the required superscript characters
     * for an application, this parameter specifies the recommended vertical distance above
     * the character baseline for those superscript characters.
     *
     * @return Superscript y offset.
     */
    public int getSuperscriptYOffset() {
        return mSuperscriptYOffset;
    }

    /**
     * Thickness of the strikeout stroke in font design units.
     * This field should normally be the thickness of the em dash for the current font,
     * and should also match the underline thickness, which is specified in the 'post' table.
     *
     * @return Strikeout size.
     */
    public int getStrikeoutSize() {
        return mStrikeoutSize;
    }

    /**
     * The position of the top of the strikeout stroke relative to the baseline
     * in font design units.
     * Positive values represent distances above the baseline; negative values represent distances
     * below the baseline. Aligning the strikeout position with the em dash is suggested.
     * Note, however, that the strikeout position should not interfere with the recognition
     * of standard characters, and therefore should not line up with crossbars in the font.
     *
     * @return Strikeout position.
     */
    public int getStrikeoutPosition() {
        return mStrikeoutPosition;
    }

    /**
     * This parameter is a classification of font-family design.
     * The font class and font subclass are registered values assigned by IBM to each font family.
     * This parameter is intended for use in selecting an alternate font when the requested
     * font is not available. The font class is the most general and the font subclass
     * is the most specific. The high byte of this field contains the family class,
     * while the low byte contains the family subclass. More information about this field at
     * https://docs.microsoft.com/en-us/typography/opentype/spec/ibmfc.
     *
     * @return Font-family class and subclass.
     */
    public int getFamilyClass() {
        return mFamilyClass;
    }

    /**
     * Additional specifications are required for PANOSE to classify non-Latin character sets.
     * This 10-byte series of numbers is used to describe the visual characteristics of
     * a given typeface. These characteristics are then used to associate the font with
     * other fonts of similar appearance having different names.
     * The variables for each digit are listed below. The Panose values are fully described
     * in the PANOSE Classification Metrics Guide, currently owned by Monotype Imaging and
     * maintained at https://monotype.github.io/panose/.
     * The PANOSE definition contains ten bytes, each of which can have multiple possible values.
     * Note that the first byte is used for a high-level classification,
     * “Family Kind”, and that the interpretation of the remaining bytes is contingent on
     * the value of the first byte. For example, if the Family Kind value is 2 (Latin Text),
     * then the next byte specifies “Serif Style”; but if the Family Kind value is 3
     * (Latin Hand Written), then the next byte specifies “Tool Kind”. Some applications
     * might support only certain Family Kind values. The following table gives the interpretation
     * for the panose array when the Family Kind is Latin Text:
     * Type    Name
     * uint8   bFamilyType;
     * uint8   bSerifStyle;
     * uint8   bWeight;
     * uint8   bProportion;
     * uint8   bContrast;
     * uint8   bStrokeVariation;
     * uint8   bArmStyle;
     * uint8   bLetterform;
     * uint8   bMidline;
     * uint8   bXHeight;
     * Some applications may use the panose values for font selection, to select a font matching
     * certain parameters. For example, Proportion (for Family Kind = Latin Text) might be used
     * to determine if a font is monospaced; or Serif Style might be use to determine if a font
     * falls into generic serif or sans serif classes. Some applications will use Family Kind = 5
     * (Latin Symbol) to identify symbol fonts, which might affect font selection or
     * fallback behaviors. There are no requirements for how applications
     * should use the panose values.
     * In a variable font that uses OpenType Font Variation mechanisms, there is no way to
     * represent different PANOSE values for different instances supported by the font.
     * The PANOSE values can be set based on the default instance.
     * Early versions of this specification provided more details regarding PANOSE values.
     * However, the external specification cited above is the normative source and
     * should be referred to for such details.
     *
     * @param panose PANOSE classification number
     */
    public void getPanose(int[] panose) {
        if (panose == null)
            return;
        final int length = panose.length;
        for (int i = 0; i < length && i < 10; i++) {
            panose[i] = mPanose[i];
        }
    }

    /**
     * This field is used to specify the Unicode blocks or ranges encompassed by the font file
     * in 'cmap' subtables for platform 3, encoding ID 1 (Microsoft platform, Unicode BMP)
     * and platform 3, encoding ID 10 (Microsoft platform, Unicode full repertoire).
     * If a bit is set (1), then the Unicode ranges assigned to that bit are considered functional.
     * If the bit is clear (0), then the range is not considered functional.
     * Each of the bits is treated as an independent flag and the bits can be set in
     * any combination. The determination of “functional” is left up to the font designer,
     * although character set selection should attempt to be functional by ranges if
     * at all possible.
     * All reserved fields must be zero. Each uint32 is in Big-Endian form.
     * Bit       Unicode Range                             Block range     Notes
     * 0         Basic Latin                               0000-007F
     * 1         Latin-1 Supplement                        0080-00FF
     * 2         Latin Extended-A                          0100-017F
     * 3         Latin Extended-B                          0180-024F
     * 4         IPA Extensions                            0250-02AF
     * _         Phonetic Extensions                       1D00-1D7F       Added in OpenType 1.5 for OS/2 version 4.
     * _         Phonetic Extensions Supplement            1D80-1DBF       Added in OpenType 1.5 for OS/2 version 4.
     * 5         Spacing Modifier Letters                  02B0-02FF
     * _         Modifier Tone Letters                     A700-A71F       Added in OpenType 1.5 for OS/2 version 4.
     * 6         Combining Diacritical Marks               0300-036F
     * _         Combining Diacritical Marks Supplement    1DC0-1DFF       Added in OpenType 1.5 for OS/2 version 4.
     * 7         Greek and Coptic                          0370-03FF
     * 8         Coptic                                    2C80-2CFF       Added in OpenType 1.5 for OS/2 version 4. See below for other version differences.
     * 9         Cyrillic                                  0400-04FF
     * _         Cyrillic Supplement                       0500-052F       Added in OpenType 1.4 for OS/2 version 3.
     * _         Cyrillic Extended-A                       2DE0-2DFF       Added in OpenType 1.5 for OS/2 version 4.
     * _         Cyrillic Extended-B                       A640-A69F       Added in OpenType 1.5 for OS/2 version 4.
     * 10        Armenian                                  0530-058F
     * 11        Hebrew                                    0590-05FF
     * 12        Vai                                       A500-A63F       Added in OpenType 1.5 for OS/2 version 4. See below for other version differences.
     * 13        Arabic                                    0600-06FF
     * _         Arabic Supplement                         0750-077F       Added in OpenType 1.5 for OS/2 version 4.
     * 14        NKo                                       07C0-07FF       Added in OpenType 1.5 for OS/2 version 4. See below for other version differences.
     * 15        Devanagari                                0900-097F
     * 16        Bengali                                   0980-09FF
     * 17        Gurmukhi                                  0A00-0A7F
     * 18        Gujarati                                  0A80-0AFF
     * 19        Oriya                                     0B00-0B7F
     * 20        Tamil                                     0B80-0BFF
     * 21        Telugu                                    0C00-0C7F
     * 22        Kannada                                   0C80-0CFF
     * 23        Malayalam                                 0D00-0D7F
     * 24        Thai                                      0E00-0E7F
     * 25        Lao                                       0E80-0EFF
     * 26        Georgian                                  10A0-10FF
     * _         Georgian Supplement                       2D00-2D2F       Added in OpenType 1.5 for OS/2 version 4.
     * 27        Balinese                                  1B00-1B7F       Added in OpenType 1.5 for OS/2 version 4. See below for other version differences.
     * 28        Hangul Jamo                               1100-11FF
     * 29        Latin Extended Additional                 1E00-1EFF
     * _         Latin Extended-C                          2C60-2C7F       Added in OpenType 1.5 for OS/2 version 4.
     * _         Latin Extended-D                          A720-A7FF       Added in OpenType 1.5 for OS/2 version 4.
     * 30        Greek Extended                            1F00-1FFF
     * 31        General Punctuation                       2000-206F
     * _         Supplemental Punctuation                  2E00-2E7F       Added in OpenType 1.5 for OS/2 version 4.
     * 32        Superscripts And Subscripts               2070-209F
     * 33        Currency Symbols                          20A0-20CF
     * 34        Combining Diacritical Marks For Symbols   20D0-20FF
     * 35        Letterlike Symbols                        2100-214F
     * 36        Number Forms                              2150-218F
     * 37        Arrows                                    2190-21FF
     * _         Supplemental Arrows-A                     27F0-27FF       Added in OpenType 1.4 for OS/2 version 3.
     * _         Supplemental Arrows-B                     2900-297F       Added in OpenType 1.4 for OS/2 version 3.
     * _         Miscellaneous Symbols and Arrows          2B00-2BFF       Added in OpenType 1.5 for OS/2 version 4.
     * 38        Mathematical Operators                    2200-22FF
     * _         Supplemental Mathematical Operators       2A00-2AFF       Added in OpenType 1.4 for OS/2 version 3.
     * _         Miscellaneous Mathematical Symbols-A      27C0-27EF       Added in OpenType 1.4 for OS/2 version 3.
     * _         Miscellaneous Mathematical Symbols-B      2980-29FF       Added in OpenType 1.4 for OS/2 version 3.
     * 39        Miscellaneous Technical                   2300-23FF
     * 40        Control Pictures                          2400-243F
     * 41        Optical Character Recognition             2440-245F
     * 42        Enclosed Alphanumerics                    2460-24FF
     * 43        Box Drawing                               2500-257F
     * 44        Block Elements                            2580-259F
     * 45        Geometric Shapes                          25A0-25FF
     * 46        Miscellaneous Symbols                     2600-26FF
     * 47        Dingbats                                  2700-27BF
     * 48        CJK Symbols And Punctuation               3000-303F
     * 49        Hiragana                                  3040-309F
     * 50        Katakana                                  30A0-30FF
     * _         Katakana Phonetic Extensions              31F0-31FF       Added in OpenType 1.4 for OS/2 version 3.
     * 51        Bopomofo                                  3100-312F
     * _         Bopomofo Extended                         31A0-31BF       Added in OpenType 1.3, extending OS/2 version 2.
     * 52        Hangul Compatibility Jamo                 3130-318F
     * 53        Phags-pa                                  A840-A87F       Added in OpenType 1.5 for OS/2 version 4. See below for other version differences.
     * 54        Enclosed CJK Letters And Months           3200-32FF
     * 55        CJK Compatibility                         3300-33FF
     * 56        Hangul Syllables                          AC00-D7AF
     * 57        Non-Plane 0                               10000-10FFFF    Implies at least one character beyond the Basic Multilingual Plane. First assigned in OpenType 1.3 for OS/2 version 2.
     * 58        Phoenician                                10900-1091F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 59        CJK Unified Ideographs                    4E00-9FFF
     * _         CJK Radicals Supplement                   2E80-2EFF       Added in OpenType 1.3 for OS/2 version 2.
     * _         Kangxi Radicals                           2F00-2FDF       Added in OpenType 1.3 for OS/2 version 2.
     * _         Ideographic Description Characters        2FF0-2FFF       Added in OpenType 1.3 for OS/2 version 2.
     * _         CJK Unified Ideographs Extension A        3400-4DBF       Added in OpenType 1.3 for OS/2 version 2.
     * _         CJK Unified Ideographs Extension B        20000-2A6DF     Added in OpenType 1.4 for OS/2 version 3.
     * _         Kanbun                                    3190-319F       Added in OpenType 1.4 for OS/2 version 3.
     * 60        Private Use Area (plane 0)                E000-F8FF
     * 61        CJK Strokes                               31C0-31EF       Range added in OpenType 1.5 for OS/2 version 4.
     * _         CJK Compatibility Ideographs              F900-FAFF
     * _         CJK Compatibility Ideographs Supplement   2F800-2FA1F     Added in OpenType 1.4 for OS/2 version 3.
     * 62        Alphabetic Presentation Forms             FB00-FB4F
     * 63        Arabic Presentation Forms-A               FB50-FDFF
     * 64        Combining Half Marks                      FE20-FE2F
     * 65        Vertical Forms                            FE10-FE1F       Range added in OpenType 1.5 for OS/2 version 4.
     * _         CJK Compatibility Forms                   FE30-FE4F
     * 66        Small Form Variants                       FE50-FE6F
     * 67        Arabic Presentation Forms-B               FE70-FEFF
     * 68        Halfwidth And Fullwidth Forms             FF00-FFEF
     * 69        Specials                                  FFF0-FFFF
     * 70        Tibetan                                   0F00-0FFF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 71        Syriac                                    0700-074F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 72        Thaana                                    0780-07BF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 73        Sinhala                                   0D80-0DFF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 74        Myanmar                                   1000-109F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 75        Ethiopic                                  1200-137F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * _         Ethiopic Supplement                       1380-139F       Added in OpenType 1.5 for OS/2 version 4.
     * _         Ethiopic Extended                         2D80-2DDF       Added in OpenType 1.5 for OS/2 version 4.
     * 76        Cherokee                                  13A0-13FF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 77        Unified Canadian Aboriginal Syllabics     1400-167F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 78        Ogham                                     1680-169F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 79        Runic                                     16A0-16FF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 80        Khmer                                     1780-17FF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * _         Khmer Symbols                             19E0-19FF       Added in OpenType 1.5 for OS/2 version 4.
     * 81        Mongolian                                 1800-18AF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 82        Braille Patterns                          2800-28FF       First assigned in OpenType 1.3, extending OS/2 version 2.
     * 83        Yi Syllables                              A000-A48F       First assigned in OpenType 1.3, extending OS/2 version 2.
     * _         Yi Radicals                               A490-A4CF       Added in OpenType 1.3, extending OS/2 version 2.
     * 84        Tagalog                                   1700-171F       First assigned in OpenType 1.4 for OS/2 version 3.
     * _         Hanunoo                                   1720-173F       Added in OpenType 1.4 for OS/2 version 3.
     * _         Buhid                                     1740-175F       Added in OpenType 1.4 for OS/2 version 3.
     * _         Tagbanwa                                  1760-177F       Added in OpenType 1.4 for OS/2 version 3.
     * 85        Old Italic                                10300-1032F     First assigned in OpenType 1.4 for OS/2 version 3.
     * 86        Gothic                                    10330-1034F     First assigned in OpenType 1.4 for OS/2 version 3.
     * 87        Deseret                                   10400-1044F     First assigned in OpenType 1.4 for OS/2 version 3.
     * 88        Byzantine Musical Symbols                 1D000-1D0FF     First assigned in OpenType 1.4 for OS/2 version 3.
     * _         Musical Symbols                           1D100-1D1FF     Added in OpenType 1.4 for OS/2 version 3.
     * _         Ancient Greek Musical Notation            1D200-1D24F     Added in OpenType 1.5 for OS/2 version 4.
     * 89        Mathematical Alphanumeric Symbols         1D400-1D7FF     First assigned in OpenType 1.4 for OS/2 version 3.
     * 90        Private Use (plane 15)                    F0000-FFFFD     First assigned in OpenType 1.4 for OS/2 version 3.
     * _         Private Use (plane 16)                    100000-10FFFD   Added in OpenType 1.4 for OS/2 version 3.
     * 91        Variation Selectors                       FE00-FE0F       First assigned in OpenType 1.4 for OS/2 version 3.
     * _         Variation Selectors Supplement            E0100-E01EF     Added in OpenType 1.4 for OS/2 version 3.
     * 92        Tags                                      E0000-E007F     First assigned in OpenType 1.4 for OS/2 version 3.
     * 93        Limbu                                     1900-194F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 94        Tai Le                                    1950-197F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 95        New Tai Lue                               1980-19DF       First assigned in OpenType 1.5 for OS/2 version 4.
     * 96        Buginese                                  1A00-1A1F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 97        Glagolitic                                2C00-2C5F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 98        Tifinagh                                  2D30-2D7F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 99        Yijing Hexagram Symbols                   4DC0-4DFF       First assigned in OpenType 1.5 for OS/2 version 4.
     * 100       Syloti Nagri                              A800-A82F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 101       Linear B Syllabary                        10000-1007F     First assigned in OpenType 1.5 for OS/2 version 4.
     * _         Linear B Ideograms                        10080-100FF     Added in OpenType 1.5 for OS/2 version 4.
     * _         Aegean Numbers                            10100-1013F     Added in OpenType 1.5 for OS/2 version 4.
     * 102       Ancient Greek Numbers                     10140-1018F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 103       Ugaritic                                  10380-1039F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 104       Old Persian                               103A0-103DF     First assigned in OpenType 1.5 for OS/2 version 4.
     * 105       Shavian                                   10450-1047F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 106       Osmanya                                   10480-104AF     First assigned in OpenType 1.5 for OS/2 version 4.
     * 107       Cypriot Syllabary                         10800-1083F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 108       Kharoshthi                                10A00-10A5F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 109       Tai Xuan Jing Symbols                     1D300-1D35F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 110       Cuneiform                                 12000-123FF     First assigned in OpenType 1.5 for OS/2 version 4.
     * _         Cuneiform Numbers and Punctuation         12400-1247F     Added in OpenType 1.5 for OS/2 version 4.
     * 111       Counting Rod Numerals                     1D360-1D37F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 112       Sundanese                                 1B80-1BBF       First assigned in OpenType 1.5 for OS/2 version 4.
     * 113       Lepcha                                    1C00-1C4F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 114       Ol Chiki                                  1C50-1C7F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 115       Saurashtra                                A880-A8DF       First assigned in OpenType 1.5 for OS/2 version 4.
     * 116       Kayah Li                                  A900-A92F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 117       Rejang                                    A930-A95F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 118       Cham                                      AA00-AA5F       First assigned in OpenType 1.5 for OS/2 version 4.
     * 119       Ancient Symbols                           10190-101CF     First assigned in OpenType 1.5 for OS/2 version 4.
     * 120       Phaistos Disc                             101D0-101FF     First assigned in OpenType 1.5 for OS/2 version 4.
     * 121       Carian                                    102A0-102DF     First assigned in OpenType 1.5 for OS/2 version 4.
     * _         Lycian                                    10280-1029F     Added in OpenType 1.5 for OS/2 version 4.
     * _         Lydian                                    10920-1093F     Added in OpenType 1.5 for OS/2 version 4.
     * 122       Domino Tiles                              1F030-1F09F     First assigned in OpenType 1.5 for OS/2 version 4.
     * _         Mahjong Tiles                             1F000-1F02F     First assigned in OpenType 1.5 for OS/2 version 4.
     * 123-127   Reserved for process-internal usage
     * All available bits were exhausted as of Unicode 5.1. The bit assignements were last updated
     * for OS/2 version 4 in OpenType 1.5. There are many additional ranges supported in
     * the current version of Unicode that are not supported by these fields in the OS/2 table.
     * See the 'dlng' and 'slng' tags in the 'meta' table for an alternate mechanism to declare
     * what scripts or languages that a font can support or is designed for.
     * Different versions of the OS/2 table were created when different Unicode versions were
     * current, and the initial specification for a given version defined fewer bit assignments
     * than for later versions. Some applications may not support all assignments for fonts that
     * have earlier OS/2 versions.
     * All of the bit assignments listed above are valid for any version of the OS/2 table,
     * though OS/2 versions 1 and 2 were specified with some assignments that did not correspond
     * to well-defined Unicode ranges and that conflict with later assignments —
     * see the details below. If a font has a version 1 or version 2 OS/2 table with one of
     * these bits set, the obsolete assignment may be the intended interpretation.
     * Because these assignments do not correspond to well-defined ranges, however,
     * the implied character coverage is unclear.
     * Version 0:
     * When version 0 was first specified, no bit assignments were defined.
     * Some applications may ignore these fields in a version 0 OS/2 table.
     * Version 1:
     * Version 1 was first specified concurrent with Unicode 1.1,
     * and bit assigments were defined for bits 0 to 69 only. With fonts that have a
     * version 1 table, some applications might recognize only bits 0 to 69.
     * Also, version 1 was specified with some bit assignments that did not correspond to a
     * well-defined Unicode range:
     * Bit 8: “Greek Symbols and Coptic” (bit 7 was specified as “Basic Greek”)
     * Bit 12: “Hebrew Extended” (bit 11 was specified as “Basic Hebrew”)
     * Bit 14: “Arabic Extended” (bit 13 was specified as “Basic Arabic”)
     * Bit 27: “Georgian Extended” (bit 26 was specified as “Basic Georgian”)
     * These assignments were discontinued as of version 2.
     * In addition, versions 1 and 2 were defined with bit 53 specified as
     * “CJK Miscellaneous”, which also does not correspond to any well-defined Unicode range.
     * This assignment was discontinued as of version 3.
     * Version 2:
     * Version 2 was defined in OpenType 1.1, which was concurrent with Unicode 2.1.
     * At that time, bit assignments were defined for bits 0 to 69 only. Bit assignments
     * for version 2 were updated in OpenType 1.3, adding assignments for bits 70 to 83
     * corresponding to new blocks assigned in Unicode 2.0 and Unicode 3.0. With fonts
     * that have a version 2 table, some applications might recognize only those bits
     * assigned in OpenType 1.2 or OpenType 1.3.
     * Also, the specification for version 2 continued to use a problematic assignment
     * for bit 53 — see details for version 1. This assignment was discontinued as of version 3.
     * Version 3:
     * Version 3 was defined in OpenType 1.4 with assignments for bits 84 to 91
     * corresponding to additional ranges in Unicode 3.2. In addition,
     * some already-assigned bits were extended to cover additional Unicode ranges
     * for related characters; see details in the table above.
     * Version 4:
     * Version 4 was defined in OpenType 1.5 with assignments for bit 58 and bits 92 to 122
     * corresponding to additional ranges in Unicode 5.1. Also, bits 8, 12, 14, 27 and 53 were
     * re-assigned (see version 1 for previous assignments). In addition,
     * some already-assigned bits were extended to cover additional Unicode ranges for
     * related characters; see details in the table above.
     *
     * @return Unicode Character Range(Bits 0–31)
     */
    @SuppressWarnings("WeakerAccess")
    public int getUnicodeRange1() {
        return mUnicodeRange1;
    }

    /**
     * @return Unicode Character Range(Bits 32–63)
     * @see #getUnicodeRange1()
     */
    public int getUnicodeRange2() {
        return mUnicodeRange2;
    }

    /**
     * @return Unicode Character Range(Bits 64–95)
     * @see #getUnicodeRange1()
     */
    public int getUnicodeRange3() {
        return mUnicodeRange3;
    }

    /**
     * @return Unicode Character Range(Bits 96–127)
     * @see #getUnicodeRange1()
     */
    public int getUnicodeRange4() {
        return mUnicodeRange4;
    }

    /**
     * The four-character identifier for the vendor of the given type face.
     * This is not the royalty owner of the original artwork. This is the company responsible
     * for the marketing and distribution of the typeface that is being classified.
     * For example, there may be multiple vendors of ITC Zapf Dingbats, with some vendors
     * providing differentiating benefits in their fonts (more kern pairs, unregularized data,
     * hand hinted, etc.). This identifier will allow for the correct vendor’s type to be used
     * over another, possibly inferior, font file.
     * Microsoft maintains a registry of vendor IDs. Registered IDs must be unique to a
     * single vendor. Non-registered IDs can also be used, but are discouraged: vendors are
     * strongly encouraged to register an ID to ensure that there are no conflicts between
     * different vendors in use of a given ID, and that customers are able to find vendor
     * contact information for a given font. This field can also be left blank (set to null,
     * or a tag comprised of four space characters).
     * All vendor IDs use the Tag data type, which is equivalent to a four-character string
     * composed of a limited set of ASCII characters. For details regarding the Tag data type,
     * see Data Types at https://docs.microsoft.com/en-us/typography/opentype/spec/otff#data-types.
     * By convention, only registered tags should be comprised of only uppercase letters (or space).
     * For a list of registered Vendor IDs, or for details on registering a vendor ID or
     * updating vendor information, see Registered typography vendors at
     * http://www.microsoft.com/typography/links/vendorlist.aspx.
     *
     * @return Font Vendor Identification
     */
    public int getVendID() {
        return mVendID;
    }

    /**
     * Contains information concerning the nature of the font patterns, as follows:
     * Bit #   macStyle bit   C definition       Description
     * 0       bit 1          ITALIC             Font contains italic or oblique glyphs, otherwise they are upright.
     * 1                      UNDERSCORE         glyphs are underscored.
     * 2                      NEGATIVE           glyphs have their foreground and background reversed.
     * 3                      OUTLINED           Outline (hollow) glyphs, otherwise they are solid.
     * 4                      STRIKEOUT          glyphs are overstruck.
     * 5       bit 0          BOLD               glyphs are emboldened.
     * 6                      REGULAR            glyphs are in the standard weight/style for the font.
     * 7                      USE_TYPO_METRICS   If set, it is strongly recommended that applications use OS/2.sTypoAscender - OS/2.sTypoDescender + OS/2.sTypoLineGap as the default line spacing for this font.
     * 8                      WWS                The font has 'name' table strings consistent with a weight/width/slope family without requiring use of name IDs 21 and 22. (Please see more detailed description below.)
     * 9                      OBLIQUE            Font contains oblique glyphs.
     * 10–15                  &lt;reserved&gt;   Reserved; set to 0.
     * All undefined bits must be zero.
     * Bit 0: The setting of bits 0 must match the setting of bit 1 in the macStyle field of
     * the 'head' table.
     * Bits 1 – 4: Bits 1 – 4 are rarely used bits that indicate the font is primarily a
     * decorative or special purpose font.
     * Bit 5: The setting of bit 5 must match the settings of bit 0 in the macStyle
     * field of the 'head' table.
     * Bit 6: If bit 6 is set, then bits 0 and 5 must be clear, else the behavior is undefined.
     * Note that, if both bit 0 and bit 5 are clear, that does not give any indication as to
     * whether or not bit 6 will be clear. For example, Arial Light is not the regular style of
     * Arial and would have all bits cleared.
     * Bit 7: Bit 7 was defined in version 4. For new fonts, vendors are encouraged to use a
     * version 4 or later OS/2 table and to have bit 7 set.
     * If a font was created with an earlier version of the OS/2 table and is updated to the
     * current version of the OS/2 table, then setting bit 7 could create potential for reflow
     * of existing documents which use the fonts. To minimize such risk, the bit would be set
     * only if using the OS/2.usWin* metrics for line height would yield significantly inferior
     * results than using the OS/2.sTypo* values.
     * Bit 8: If bit 8 is set, then 'name' table strings for family and subfamily are provided
     * that are consistent with a weight/width/slope family model without requiring the use of
     * name IDs 21 or 22.
     * Many typographic families contains faces that differ only in one or more of the
     * attributes weight, width and slope. Even though a family might have a large number
     * of member faces, if the variations are in these attributes only, then family and
     * subfamily names provided in the 'name' table using IDs 1 and 2 or 16 and 17 will be
     * consistent with a weight/width/slope family model. In this case, bit 8 should be set,
     * and 'name' entries for name IDs 21 and 22 should not be included.
     * Some typographic families include faces that differ in attributes other than weight,
     * width or slope. For example, a family might include variations for “handwriting”,
     * “caption”, “display”, “optical size”, etc. In this case, some of the member faces may
     * differ from the Regular face only in weight, width or slope attributes, while other
     * members will differ in relation to other attributes. Fonts for those member faces that
     * differ from Regular only in weight, width or slope should have bit 8 set, and should not
     * use name ID 21 or 22. But the fonts for those member faces that differ from Regular
     * in terms of other attributes should not have bit 8 set, and they should use name IDs 21
     * and 22 to map these faces into a WWS-conformant family model.
     * Thus, if a font has a version 4 or later OS/2 table, bit 8 should be set if and only
     * if 'name' entries for IDs 16 and 17 are consistent with the WWS model and entries for
     * IDs 21 and 22 are not included. Conversely, if bit 8 is not set, that will be interpreted
     * to mean that the names provided by IDs 16 and 17 are not consistent with the WWS model
     * and that 'name' entries for IDs 21 and 22 are included.
     * In this context, “typographic family” is the Microsoft Unicode string for name ID 16,
     * if present, else the Microsoft Unicode string for name ID 1; “weight” is OS/2.usWeightClass;
     * “width” is OS/2.usWidthClass; “slope” is OS/2.fsSelection bit 0 (ITALIC) and
     * bit 9 (OBLIQUE).
     * Bit 9: If bit 9 is set, then this font is to be considered an “oblique” style by
     * processes which make a distinction between oblique and italic styles, such as
     * Cascading Style Sheets font matching. For example, a font created by algorithmically
     * slanting an upright face will set this bit.
     * If a font has a version 4 or later OS/2 table and this bit is not set, then this font
     * is not to be considered an “oblique” style. For example, a font that has a classic
     * italic design will not set this bit.
     * This bit, unlike the ITALIC bit (bit 0), is not related to style-linking in applications
     * that assume a four-member font-family model comprised of regular, italic, bold and
     * bold italic. It may be set or unset independently of the ITALIC bit. In most cases,
     * if OBLIQUE is set, then ITALIC will also be set, though this is not required.
     * Bit 15: Bit 15 is permanently reserved. It has been used in some legacy implementations
     * and may result in special behavior in some implementations. Use of this bit is deprecated.
     * Versions 0 to 3: Only bit 0 (italic) to bit 6 (regular) are assigned.
     * Bits 7 to 15 are reserved and must be set to 0. Applications should ignore bits 7
     * to 15 in a font that has a version 0 to version 3 OS/2 table.
     * Version 4 to 5: Bits 7 to 9 were defined in version 4 (OpenType 1.5).
     * Bits 10 to 15 are reserved and must be set to 0. Applications should ignore
     * bits 10 to 15 in a font that has a version 4 or version 5 OS/2 table.
     *
     * @return Font selection flags.
     */
    public int getSelection() {
        return mSelection;
    }

    /**
     * The minimum Unicode index (character code) in this font, according to the 'cmap' subtable
     * for platform ID 3 and platform- specific encoding ID 0 or 1. For most fonts supporting
     * Win-ANSI or other character sets, this value would be 0x0020. This field cannot represent
     * supplementary character values (codepoints greater than 0xFFFF). Fonts that
     * support supplementary characters should set the value in this field to 0xFFFF if
     * the minimum index value is a supplementary character.
     *
     * @return The minimum Unicode index.
     */
    public int getFirstCharIndex() {
        return mFirstCharIndex;
    }

    /**
     * The maximum Unicode index (character code) in this font, according to the 'cmap' subtable
     * for platform ID 3 and encoding ID 0 or 1. This value depends on which character sets
     * the font supports. This field cannot represent supplementary character values
     * (codepoints greater than 0xFFFF). Fonts that support supplementary characters should
     * set the value in this field to 0xFFFF.
     *
     * @return The maximum Unicode index.
     */
    public int getLastCharIndex() {
        return mLastCharIndex;
    }

    /**
     * Documentation for OS/2 version 0 in Apple’s TrueType Reference Manual stops at
     * the usLastCharIndex field and does not include the last five fields of the table as
     * it was defined by Microsoft. Some legacy TrueType fonts may have been built with a
     * shortened version 0 OS/2 table.
     *
     * @return legacy TrueType fonts.
     */
    public boolean isLegacy() {
        return mLegacy;
    }

    /**
     * The typographic ascender for this font. This field should be combined with the
     * sTypoDescender and sTypoLineGap values to determine default line spacing.
     * This field is similar to the ascender field in the 'hhea' table as well as to
     * the usWinAscent field in this table. However, legacy platform implementations
     * used those fields with platform-specific behaviors. As a result, those fields
     * are constrained by backward-compatibility requirements, and they do not ensure
     * consistent layout across implementations. The sTypoAscender, sTypoDescender and
     * sTypoLineGap fields are intended to allow applications to lay out documents in
     * a typographically-correct and portable fashion.
     * The USE_TYPO_METRICS flag (bit 7) of the fsSelection field is used to choose between
     * using sTypo* values or usWin* values for default line metrics. See fsSelection for
     * additional details.
     * It is not a general requirement that sTypoAscender - sTypoDescender be equal to
     * unitsPerEm. These values should be set to provide default line spacing appropriate
     * for the primary languages the font is designed to support.
     * For CJK (Chinese, Japanese, and Korean) fonts that are intended to be used for
     * vertical (as well as horizontal) layout, the required value for sTypoAscender
     * is that which describes the top of the ideographic em-box. For example,
     * if the ideographic em-box of the font extends from coordinates 0,-120 to 1000,880
     * (that is, a 1000 × 1000 box set 120 design units below the Latin baseline),
     * then the value of sTypoAscender must be set to 880. Failing to adhere to these
     * requirements will result in incorrect vertical layout.
     * Also see the Recommendations Section at
     * https://docs.microsoft.com/en-us/typography/opentype/spec/recom#tad for more on this field.
     *
     * @return The typographic ascender for this font.
     */
    public int getTypoAscender() {
        return mTypoAscender;
    }

    /**
     * The typographic descender for this font. This field should be combined with
     * the sTypoAscender and sTypoLineGap values to determine default line spacing.
     * This field is similar to the descender field in the 'hhea' table as well as to
     * the usWinDescent field in this table. However, legacy platform implementations
     * used those fields with platform-specific behaviors. As a result, those fields
     * are constrained by backward-compatibility requirements, and they do not ensure
     * consistent layout across implementations. The sTypoAscender, sTypoDescender
     * and sTypoLineGap fields are intended to allow applications to lay out documents
     * in a typographically-correct and portable fashion.
     * The USE_TYPO_METRICS flag (bit 7) of the fsSelection field is used to choose
     * between using sTypo* values or usWin* values for default line metrics.
     * See fsSelection for additional details.
     * It is not a general requirement that sTypoAscender - sTypoDescender be equal
     * to unitsPerEm. These values should be set to provide default line spacing
     * appropriate for the primary languages the font is designed to support.
     * For CJK (Chinese, Japanese, and Korean) fonts that are intended to
     * be used for vertical (as well as horizontal) layout, the required value
     * for sTypoDescender is that which describes the bottom of the ideographic em-box.
     * For example, if the ideographic em-box of the font extends from coordinates 0,-120
     * to 1000,880 (that is, a 1000 × 1000 box set 120 design units below the Latin baseline),
     * then the value of sTypoDescender must be set to -120. Failing to adhere to these
     * requirements will result in incorrect vertical layout.
     * Also see the Recommendations Section  at
     * https://docs.microsoft.com/en-us/typography/opentype/spec/recom#tad for more on this field.
     *
     * @return The typographic descender for this font.
     */
    public int getTypoDescender() {
        return mTypoDescender;
    }

    /**
     * The typographic line gap for this font. This field should be combined with
     * the sTypoAscender and sTypoDescender values to determine default line spacing.
     * This field is similar to the lineGap field in the 'hhea' table. However,
     * legacy platform implementations treat that field with platform-specific behaviors.
     * As a result, that field is constrained by backward-compatibility requirements,
     * and does not ensure consistent layout across implementations. The sTypoAscender,
     * sTypoDescender and sTypoLineGap fields are intended to allow applications to
     * lay out documents in a typographically-correct and portable fashion.
     * The USE_TYPO_METRICS flag (bit 7) of the fsSelection field is used to choose
     * between using sTypo* values or usWin* values for default line metrics.
     * See fsSelection for additional details.
     *
     * @return The typographic line gap for this font.
     */
    public int getTypoLineGap() {
        return mTypoLineGap;
    }

    /**
     * The “Windows ascender” metric. This should be used to specify the height above
     * the baseline for a clipping region.
     * This is similar to the sTypoAscender field, and also to the ascender field
     * in the 'hhea' table. There are important differences between these, however.
     * In the Windows GDI implementation, the usWinAscent and usWinDescent values
     * have been used to determine the size of the bitmap surface in the TrueType rasterizer.
     * Windows GDI will clip any portion of a TrueType glyph outline that appears above
     * the usWinAscent value. If any clipping is unacceptable, then the value should be
     * set greater than or equal to yMax.
     * Note: This pertains to the default position of glyphs, not their final position
     * in layout after data from the GPOS or 'kern' table has been applied. Also,
     * this clipping behavior also interacts with the VDMX table: if a VDMX table is
     * present and there is data for the current device aspect ratio and rasterization size,
     * then the VDMX data will supersede the usWinAscent and usWinDescent values.
     * Some legacy applications use the usWinAscent and usWinDescent values to
     * determine default line spacing. This is strongly discouraged. The sTypo* fields
     * should be used for this purpose.
     * Note that some applications use either the usWin* values or the sTypo* values
     * to determine default line spacing, depending on whether the USE_TYPO_METRICS flag (bit 7)
     * of the fsSelection field is set. This may be useful to provide compatibility
     * with legacy documents using older fonts, while also providing better
     * and more-portable layout using newer fonts. See fsSelection for additional details.
     * Applications that use the sTypo* fields for default line spacing
     * can use the usWin* values to determine the size of a clipping region.
     * Some applications use a clipping region for editing scenarios to determine
     * what portion of the display surface to re-draw when text is edited,
     * or how large a selection rectangle to draw when text is selected.
     * This is an appropriate use for the usWin* values.
     * Early versions of this specification suggested that the usWinAscent value
     * be computed as the yMax for all characters in the Windows “ANSI” character set.
     * For new fonts, the value should be determined based on the primary languages
     * the font is designed to support, and should take into consideration additional height
     * that may be required to accommodate tall glyphs or mark positioning.
     *
     * @return The “Windows ascender” metric.
     */
    public int getWinAscent() {
        return mWinAscent;
    }

    /**
     * The “Windows descender” metric. This should be used to specify the vertical extent below
     * the baseline for a clipping region.
     * This is similar to the sTypoDescender field, and also to the descender field
     * in the 'hhea' table. There are important differences between these, however.
     * Some of these differences are described below. In addition, the usWinDescent value
     * treats distances below the baseline as positive values; thus, usWinDescent
     * is usually a positive value, while sTypoDescender and hhea.descender are usually negative.
     * In the Windows GDI implementation, the usWinDescent and usWinAscent values
     * have been used to determine the size of the bitmap surface in the TrueType rasterizer.
     * Windows GDI will clip any portion of a TrueType glyph outline that appears
     * below (-1 × usWinDescent). If any clipping is unacceptable, then the value
     * should be set greater than or equal to (-yMin).
     * Note: This pertains to the default position of glyphs, not their final position
     * in layout after data from the GPOS or 'kern' table has been applied. Also,
     * this clipping behavior also interacts with the VDMX table: if a VDMX table
     * is present and there is data for the current device aspect ratio and rasterization size,
     * then the VDMX data will supersede the usWinAscent and usWinDescent values.
     * Some legacy applications use the usWinAscent and usWinDescent values
     * to determine default line spacing. This is strongly discouraged.
     * The sTypo* fields should be used for this purpose.
     * Note that some applications use either the usWin* values or the sTypo* values
     * to determine default line spacing, depending on whether the USE_TYPO_METRICS flag (bit 7)
     * of the fsSelection field is set. This may be useful to provide compatibility
     * with legacy documents using older fonts, while also providing better
     * and more-portable layout using newer fonts. See fsSelection for additional details.
     * Applications that use the sTypo* fields for default line spacing can use
     * the usWin* values to determine the size of a clipping region. Some applications
     * use a clipping region for editing scenarios to determine what portion
     * of the display surface to re-draw when text is edited, or how large a selection
     * rectangle to draw when text is selected. This is an appropriate use for the usWin* values.
     * Early versions of this specification suggested that the usWinDescent value be computed
     * as -yMin for all characters in the Windows “ANSI” character set. For new fonts,
     * the value should be determined based on the primary languages the font
     * is designed to support, and should take into consideration additional vertical
     * extent that may be required to accommodate glyphs with low descenders or mark positioning.
     *
     * @return The “Windows descender” metric.
     */
    public int getWinDescent() {
        return mWinDescent;
    }

    /**
     * This field is used to specify the code pages encompassed by the font file in
     * the 'cmap' subtable for platform 3, encoding ID 1 (Microsoft platform, Unicode BMP).
     * If the font file is encoding ID 0, then the Symbol Character Set bit should be set.
     * If a given bit is set (1), then the code page is considered functional.
     * If the bit is clear (0) then the code page is not considered functional.
     * Each of the bits is treated as an independent flag and the bits can be set
     * in any combination. The determination of “functional” is left up to the font designer,
     * although character set selection should attempt to be functional by code pages if
     * at all possible.
     * Symbol character sets have a special meaning. If the symbol bit (31) is set,
     * and the font file contains a 'cmap' subtable for platform of 3 and encoding ID of 1,
     * then all of the characters in the Unicode range 0xF000 - 0xF0FF (inclusive)
     * will be used to enumerate the symbol character set. If the bit is not set,
     * any characters present in that range will not be enumerated as a symbol character set.
     * All reserved fields must be zero. Each uint32 is in Big-Endian form.
     * Bit     Code Page   Description
     * 0       1252        Latin 1
     * 1       1250        Latin 2: Eastern Europe
     * 2       1251        Cyrillic
     * 3       1253        Greek
     * 4       1254        Turkish
     * 5       1255        Hebrew
     * 6       1256        Arabic
     * 7       1257        Windows Baltic
     * 8       1258        Vietnamese
     * 9–15                Reserved for Alternate ANSI
     * 16      874         Thai
     * 17      932         JIS/Japan
     * 18      936         Chinese: Simplified chars—PRC and Singapore
     * 19      949         Korean Wansung
     * 20      950         Chinese: Traditional chars—Taiwan and Hong Kong
     * 21      1361        Korean Johab
     * 22-28               Reserved for Alternate ANSI or OEM
     * 29                  Macintosh Character Set (US Roman)
     * 30                  OEM Character Set
     * 31                  Symbol Character Set
     * 32–47               Reserved for OEM
     * 48      869         IBM Greek
     * 49      866         MS-DOS Russian
     * 50      865         MS-DOS Nordic
     * 51      864         Arabic
     * 52      863         MS-DOS Canadian French
     * 53      862         Hebrew
     * 54      861         MS-DOS Icelandic
     * 55      860         MS-DOS Portuguese
     * 56      857         IBM Turkish
     * 57      855         IBM Cyrillic; primarily Russian
     * 58      852         Latin 2
     * 59      775         MS-DOS Baltic
     * 60      737         Greek; former 437 G
     * 61      708         Arabic; ASMO 708
     * 62      850         WE/Latin 1
     * 63      437         US
     * Version 0: These fields were not defined in version 0.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * additional data beyond the usWinDescent field should be ignored.
     * Version 1: Bit 8 was not assigned in version 1.
     * All other currently-assigned bits were defined in version 1.
     * Version 2 and later: All currently-assigned bits were defined in version 2.
     *
     * @return Code Page Character Range (Bits 0–31)
     */
    @SuppressWarnings("WeakerAccess")
    public int getCodePageRange1() {
        return mCodePageRange1;
    }

    /**
     * @return Code Page Character Range (Bits 32–63)
     * @see #getCodePageRange1()
     */
    public int getCodePageRange2() {
        return mCodePageRange2;
    }

    /**
     * This metric specifies the distance between the baseline and the approximate height
     * of non-ascending lowercase letters measured in FUnits. This value would normally
     * be specified by a type designer but in situations where that is not possible,
     * for example when a legacy font is being converted, the value may be set equal
     * to the top of the unscaled and unhinted glyph bounding box of the glyph encoded at U+0078
     * (LATIN SMALL LETTER X). If no glyph is encoded in this position the field should be set to 0.
     * This metric, if specified, can be used in font substitution: the xHeight value of
     * one font can be scaled to approximate the apparent size of another.
     * Version 0, version 1: This field was not defined in version 0 or version 1.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * or if the size of a version 1 OS/2 table extends beyond the code page range fields,
     * this additional data should be ignored.
     * Version 2 and later: This field was defined in version 2 of the OS/2 table.
     *
     * @return Height.
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * This metric specifies the distance between the baseline and the approximate height
     * of uppercase letters measured in FUnits. This value would normally be specified by
     * a type designer but in situations where that is not possible, for example when
     * a legacy font is being converted, the value may be set equal to the top of
     * the unscaled and unhinted glyph bounding box of the glyph encoded at U+0048
     * (LATIN CAPITAL LETTER H). If no glyph is encoded in this position the field
     * should be set to 0.
     * This metric, if specified, can be used in systems that specify type size by capital
     * height measured in millimeters. It can also be used as an alignment metric;
     * the top of a drop capital, for instance, can be aligned to the sCapHeight metric
     * of the first line of text.
     * Version 0, version 1: This field was not defined in version 0 or version 1.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * or if the size of a version 1 OS/2 table extends beyond the code page range fields,
     * this additional data should be ignored.
     * Version 2 and later: This field was defined in version 2 of the OS/2 table.
     *
     * @return Cap height.
     */
    public int getCapHeight() {
        return mCapHeight;
    }

    /**
     * This is the Unicode code point, in UTF-16 encoding,
     * of a character that can be used for a default glyph if a requested character is
     * not supported in the font. If the value of this field is zero,
     * glyph ID 0 is to be used for the default character. This field cannot represent
     * supplementary-plane character values (code points greater than 0xFFFF),
     * and so applications are strongly discouraged from using this field.
     * Version 0, version 1: This field was not defined in version 0 or version 1.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * or if the size of a version 1 OS/2 table extends beyond the code page range fields,
     * this additional data should be ignored.
     * Version 2 and later: This field was defined in version 2 of the OS/2 table.
     *
     * @return Default glyph.
     */
    public int getDefaultChar() {
        return mDefaultChar;
    }

    /**
     * This is the Unicode code point, in UTF-16 encoding, of a character that can be used
     * as a default break character. The break character is used to separate words
     * and justify text. Most fonts specify U+0020 SPACE as the break character.
     * This field cannot represent supplementary-plane character values
     * (code points greater than 0xFFFF) , and so applications are strongly discouraged
     * from using this field.
     * Version 0, version 1: This field was not defined in version 0 or version 1.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * or if the size of a version 1 OS/2 table extends beyond the code page range fields,
     * this additional data should be ignored.
     * Version 2 and later: This field was defined in version 2 of the OS/2 table.
     *
     * @return Break character.
     */
    public int getBreakChar() {
        return mBreakChar;
    }

    /**
     * The maximum length of a target glyph context for any feature in this font.
     * For example, a font which has only a pair kerning feature should set this field to 2.
     * If the font also has a ligature feature in which the glyph sequence 'f f i' is substituted
     * by the ligature 'ffi', then this field should be set to 3. This field could be useful
     * to sophisticated line-breaking engines in determining how far they should look ahead
     * to test whether something could change that effects the line breaking.
     * For chaining contextual lookups, the length of the string
     * (covered glyph) + (input sequence) + (lookahead sequence) should be considered.
     * Version 0, version 1: This field was not defined in version 0 or version 1.
     * If the size of a version 0 OS/2 table extends beyond the usWinDescent field,
     * or if the size of a version 1 OS/2 table extends beyond the code page range fields,
     * this additional data should be ignored.
     * Version 2 and later: This field was defined in version 2 of the OS/2 table.
     *
     * @return The maximum length of a target glyph context.
     */
    public int getMaxContext() {
        return mMaxContext;
    }

    /**
     * This field is used for fonts with multiple optical styles.
     * This value is the lower value of the size range for which this font has been designed.
     * The units for this field are TWIPs (one-twentieth of a point, or 1440 per inch).
     * The value is inclusive — meaning that that font was designed to work best
     * at this point size through, but not including, the point size indicated
     * by usUpperOpticalPointSize. When used with other optical-size-variant fonts
     * within a typographic family that also specify usLowerOpticalPointSize
     * and usUpperOpticalPointSize values, it would be expected that another font
     * has the usUpperOpticalPointSize field set to the same value as the value in this field,
     * unless this font is designed for the lowest size range among the fonts in the family.
     * The smallest font in an optical-size set should set this value to 0.
     * When working across multiple optical-size-variant fonts within a typographic family,
     * there should be no intentional gaps or overlaps in the ranges.
     * The usLowerOpticalPointSize value must be less than usUpperOpticalPointSize.
     * The maximum valid value is 0xFFFE.
     * For fonts that were not designed for multiple optical-size variants,
     * this field should be set to 0 (zero), and usUpperOpticalPointSize should be set to 0xFFFF.
     * Note: Use of this field has been superseded by the STAT table.
     * See Recommendations Section at
     * https://docs.microsoft.com/en-us/typography/opentype/spec/recom#OptSize for more information.
     * Versions 0 – 4: This field was not defined in versions 0 – 4. If the size of an OS/2 table
     * extends beyond the last field defined for the given version,
     * this additional data should be ignored.
     * Version 5: This field was defined in version 5 of the OS/2 table.
     *
     * @return Lower value of the size range.
     */
    public int getLowerOpticalPointSize() {
        return mLowerOpticalPointSize;
    }

    /**
     * This field is used for fonts with multiple optical styles.
     * This value is the upper value of the size range for which this font has been designed.
     * The units for this field are TWIPs (one-twentieth of a point, or 1440 per inch).
     * The value is exclusive — meaning that that font was designed to work best below
     * this point size down to the usLowerOpticalPointSize threshold. When used
     * with other optical-size-variant fonts within a typographic family that also specify
     * usLowerOpticalPointSize and usUpperOpticalPointSize values,
     * it would be expected that another font has the usLowerOpticalPointSize field set
     * to the same value as the value in this field, unless this font is designed for
     * the highest size range among the fonts in the family. The largest font
     * in an optical-size set should set this value to 0xFFFF, which is interpreted as infinity.
     * When working across multiple optical-size-variant fonts within a typographic family,
     * there should be no intentional gaps or overlaps left in the ranges.
     * The usUpperOpticalPointSize value must be greater than usLowerOpticalPointSize.
     * The minimum valid value for this field is 2 (two). The largest possible inclusive
     * point size represented by this field is 3276.65 points; any higher values would be
     * represented as infinity.
     * For fonts that were not designed for multiple optical-size variants,
     * this field should be set to 0xFFFF, and usLowerOpticalPointSize should be set to 0 (zero).
     * Note: Use of this field has been superseded by the STAT table.
     * See Recommendations Section at
     * https://docs.microsoft.com/en-us/typography/opentype/spec/recom#OptSize for more information.
     * Versions 0 – 4: This field was not defined in versions 0 – 4. If the size of an OS/2 table
     * extends beyond the last field defined for the given version,
     * this additional data should be ignored.
     * Version 5: This field was defined in version 5 of the OS/2 table.
     *
     * @return Upper value of the size range.
     */
    public int getUpperOpticalPointSize() {
        return mUpperOpticalPointSize;
    }

    @Override
    public int getHashCode() {
        int result = Objects.hash(super.getHashCode(), mVersion, mAvgCharWidth, mWeightClass,
                mWidthClass, mType, mSubscriptXSize, mSubscriptYSize, mSubscriptXOffset,
                mSubscriptYOffset, mSuperscriptXSize, mSuperscriptYSize, mSuperscriptXOffset,
                mSuperscriptYOffset, mStrikeoutSize, mStrikeoutPosition, mFamilyClass,
                mUnicodeRange1, mUnicodeRange2, mUnicodeRange3, mUnicodeRange4, mVendID, mSelection,
                mFirstCharIndex, mLastCharIndex, mLegacy, mTypoAscender, mTypoDescender,
                mTypoLineGap, mWinAscent, mWinDescent, mCodePageRange1, mCodePageRange2, mHeight,
                mCapHeight, mDefaultChar, mBreakChar, mMaxContext, mLowerOpticalPointSize,
                mUpperOpticalPointSize);
        result = 31 * result + Arrays.hashCode(mPanose);
        return result;
    }

    @Override
    public String getString() {
        return "OS2Table{" +
                "record=" + String.valueOf(getTableRecord()) +
                ", version=" + mVersion +
                ", avgCharWidth=" + mAvgCharWidth +
                ", weightClass=" + mWeightClass +
                ", widthClass=" + mWidthClass +
                ", type=" + mType +
                ", subscriptXSize=" + mSubscriptXSize +
                ", subscriptYSize=" + mSubscriptYSize +
                ", subscriptXOffset=" + mSubscriptXOffset +
                ", subscriptYOffset=" + mSubscriptYOffset +
                ", superscriptXSize=" + mSuperscriptXSize +
                ", superscriptYSize=" + mSuperscriptYSize +
                ", superscriptXOffset=" + mSuperscriptXOffset +
                ", superscriptYOffset=" + mSuperscriptYOffset +
                ", strikeoutSize=" + mStrikeoutSize +
                ", strikeoutPosition=" + mStrikeoutPosition +
                ", familyClass=" + mFamilyClass +
                ", panose=" + Arrays.toString(mPanose) +
                ", unicodeRange1=" + mUnicodeRange1 +
                ", unicodeRange2=" + mUnicodeRange2 +
                ", unicodeRange3=" + mUnicodeRange3 +
                ", unicodeRange4=" + mUnicodeRange4 +
                ", vendID=" + mVendID +
                ", selection=" + mSelection +
                ", firstCharIndex=" + mFirstCharIndex +
                ", lastCharIndex=" + mLastCharIndex +
                ", legacy=" + mLegacy +
                ", typoAscender=" + mTypoAscender +
                ", typoDescender=" + mTypoDescender +
                ", typoLineGap=" + mTypoLineGap +
                ", winAscent=" + mWinAscent +
                ", winDescent=" + mWinDescent +
                ", codePageRange1=" + mCodePageRange1 +
                ", codePageRange2=" + mCodePageRange2 +
                ", height=" + mHeight +
                ", capHeight=" + mCapHeight +
                ", defaultChar=" + mDefaultChar +
                ", breakChar=" + mBreakChar +
                ", maxContext=" + mMaxContext +
                ", lowerOpticalPointSize=" + mLowerOpticalPointSize +
                ", upperOpticalPointSize=" + mUpperOpticalPointSize +
                '}';
    }
}
