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

import am.util.opentype.OpenTypeReader;
import am.util.opentype.TableRecord;

/**
 * PCL 5 Table
 * The PCLT table is strongly discouraged for OpenType™ fonts with TrueType outlines.
 * Extra information on many of these fields can be found in the HP PCL 5 Printer Language
 * Technical Reference Manual available from Hewlett-Packard Boise Printer Division.
 */
@SuppressWarnings("unused")
public class PCL5Table {

    private final int mMajorVersion;
    private final int mMinorVersion;
    private final int mFontNumber;
    private final int mPitch;
    private final int mXHeight;
    private final int mStyle;
    private final int mTypeFamily;
    private final int mCapHeight;
    private final int mSymbolSet;
    private final int[] mTypeface;
    private final int[] mCharacterComplement;
    private final int[] mFileName;
    private final int mStrokeWeight;
    private final int mWidthType;
    private final int mSerifStyle;

    public PCL5Table(OpenTypeReader reader, TableRecord record) throws IOException {
        if (reader == null || record == null || record.getTableTag() != TableRecord.TAG_PCLT)
            throw new IOException();
        reader.seek(record.getOffset());
        final int majorVersion = reader.readUnsignedShort();
        final int minorVersion = reader.readUnsignedShort();
        final int fontNumber = reader.readUnsignedInt();
        final int pitch = reader.readUnsignedShort();
        final int xHeight = reader.readUnsignedShort();
        final int style = reader.readUnsignedShort();
        final int typeFamily = reader.readUnsignedShort();
        final int capHeight = reader.readUnsignedShort();
        final int symbolSet = reader.readUnsignedShort();
        final int[] typeface = new int[16];
        for (int i = 0; i < typeface.length; i++) {
            typeface[i] = reader.read();
        }
        final int[] characterComplement = new int[8];
        for (int i = 0; i < characterComplement.length; i++) {
            characterComplement[i] = reader.read();
        }
        final int[] fileName = new int[6];
        for (int i = 0; i < fileName.length; i++) {
            fileName[i] = reader.read();
        }
        final int strokeWeight = reader.read();
        final int widthType = reader.read();
        final int serifStyle = reader.readUnsignedByte();
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
        mFontNumber = fontNumber;
        mPitch = pitch;
        mXHeight = xHeight;
        mStyle = style;
        mTypeFamily = typeFamily;
        mCapHeight = capHeight;
        mSymbolSet = symbolSet;
        mTypeface = typeface;
        mCharacterComplement = characterComplement;
        mFileName = fileName;
        mStrokeWeight = strokeWeight;
        mWidthType = widthType;
        mSerifStyle = serifStyle;
    }

    /**
     * Major Version
     *
     * @return Major Version.
     */
    public int getMajorVersion() {
        return mMajorVersion;
    }

    /**
     * Minor Version
     *
     * @return Minor Version.
     */
    public int getMinorVersion() {
        return mMinorVersion;
    }

    /**
     * This 32-bit number is segmented in two parts. The most significant bit indicates native
     * versus converted format. Only font vendors should create fonts with this bit zeroed.
     * The 7 next most significant bits are assigned by Hewlett-Packard Boise Printer Division
     * to major font vendors. The least significant 24 bits are assigned by the vendor.
     * Font vendors should attempt to insure that each of their fonts are marked with unique values.
     * Code   Vendor
     * A      Adobe Systems
     * B      Bitstream Inc.
     * C      Agfa Corporation
     * H      Bigelow & Holmes
     * L      Linotype Company
     * M      Monotype Typography Ltd.
     *
     * @return Font Number.
     */
    public int getFontNumber() {
        return mFontNumber;
    }

    /**
     * The width of the space in FUnits (FUnits are described by the unitsPerEm field of the
     * 'head' table). Monospace fonts derive the width of all characters from this field.
     *
     * @return Pitch.
     */
    public int getPitch() {
        return mPitch;
    }

    /**
     * The height of the optical line describing the height of the lowercase x in FUnits.
     * This might not be the same as the measured height of the lowercase x.
     *
     * @return x Height.
     */
    public int getXHeight() {
        return mXHeight;
    }

    /**
     * The most significant 6 bits are reserved. The 5 next most significant bits encode structure.
     * The next 3 most significant bits encode appearance width.
     * The 2 least significant bits encode posture.
     * Structure (bits 5-9)
     * 0       Solid (normal, black)
     * 1       Outline (hollow)
     * 2       Inline (incised, engraved)
     * 3       Contour, edged (antique, distressed)
     * 4       Solid with shadow
     * 5       Outline with shadow
     * 6       Inline with shadow
     * 7       Contour, or edged, with shadow
     * 8       Pattern filled
     * 9       Pattern filled #1 (when more than one pattern)
     * 10      Pattern filled #2 (when more than two patterns)
     * 11      Pattern filled #3 (when more than three patterns)
     * 12      Pattern filled with shadow
     * 13      Pattern filled with shadow #1 (when more than one pattern or shadow)
     * 14      Pattern filled with shadow #2 (when more than two patterns or shadows)
     * 15      Pattern filled with shadow #3 (when more than three patterns or shadows)
     * 16      Inverse
     * 17      Inverse with border
     * 18-31   reserved
     * Width (bits 2-4)
     * 0       normal
     * 1       condensed
     * 2       compressed, extra condensed
     * 3       extra compressed
     * 4       ultra compressed
     * 5       reserved
     * 6       expanded, extended
     * 7       extra expanded, extra extended
     * Posture (bits 0-1)
     * 0       upright
     * 1       oblique, italic
     * 2       alternate italic (backslanted, cursive, swash)
     * 3       reserved
     *
     * @return Style.
     */
    public int getStyle() {
        return mStyle;
    }

    /**
     * The 4 most significant bits are font vendor codes. The 12 least significant bits are
     * typeface family codes. Both are assigned by HP Boise Division.
     * Vendor Codes (bits 12-15)
     * 0      reserved
     * 1      Agfa Corporation
     * 2      Bitstream Inc.
     * 3      Linotype Company
     * 4      Monotype Typography Ltd.
     * 5      Adobe Systems
     * 6      font repackagers
     * 7      vendors of unique typefaces
     * 8-15   reserved
     *
     * @return Typeface family code.
     */
    public int getTypeFamily() {
        return mTypeFamily;
    }

    /**
     * The height of the optical line describing the top of the uppercase H in FUnits.
     * This might not be the same as the measured height of the uppercase H.
     *
     * @return Cap height.
     */
    public int getCapHeight() {
        return mCapHeight;
    }

    /**
     * The most significant 11 bits are the value of the symbol set “number” field.
     * The value of the least significant 5 bits, when added to 64, is the ASCII value of
     * the symbol set “ID” field. Symbol set values are assigned by HP Boise Division.
     * Unbound fonts, or “typefaces” should have a symbol set value of 0.
     * See the PCL 5 Printer Language Technical Reference Manual or the PCL 5 Comparison Guide
     * for the most recent published list of codes.
     * Examples:
     * Type                       PCL   decimal
     * Windows 3.1 “ANSI”         19U   629
     * Windows 3.0 “ANSI”         9U    309
     * Adobe “Symbol”             19M   621
     * Macintosh                  12J   394
     * PostScript ISO Latin 1     11J   362
     * PostScript Std. Encoding   10J   330
     * Code Page 1004             9J    298
     * DeskTop                    7J    234
     *
     * @return Symbol Set.
     */
    public int getSymbolSet() {
        return mSymbolSet;
    }

    /**
     * This 16-byte ASCII string appears in the “font print” of PCL printers.
     * Care should be taken to insure that the base string for all typefaces of
     * a family are consistent, and that the designators for bold, italic, etc. are standardized.
     * Example:
     * Times New
     * Times New     Bd
     * Times New     It
     * Times New     BdIt
     * Courier New
     * Courier New   Bd
     * Courier New   It
     * Courier New   BdIt
     *
     * @return Typeface.
     */
    public int[] getTypeface() {
        return mTypeface;
    }

    /**
     * This 8-byte field identifies the symbol collections provided by the font,
     * each bit identifies a symbol collection and is independently interpreted.
     * Symbol set bound fonts should have all bits in this field set (except bit 0).
     * Example:
     * DOS/PCL Complement           0xFFFFFFFF003FFFFE
     * Windows 3.1 “ANSI”           0xFFFFFFFF37FFFFFE
     * Macintosh                    0xFFFFFFFF36FFFFFE
     * ISO 8859-1 Latin 1           0xFFFFFFFF3BFFFFFE
     * ISO 8859-1,2,9 Latin 1,2,5   0xFFFFFFFF0BFFFFFE
     * The character collections identified by each bit are as follows:
     * 31   ASCII (supports several standard interpretations)
     * 30   Latin 1 extensions
     * 29   Latin 2 extensions
     * 28   Latin 5 extensions
     * 27   Desktop Publishing Extensions
     * 26   Accent Extensions (East and West Europe)
     * 25   PCL Extensions
     * 24   Macintosh Extensions
     * 23   PostScript Extensions
     * 22   Code Page Extensions
     * The character complement field also indicates the index mechanism used with an unbound font.
     * Bit 0 must always be cleared when the font elements are provided in Unicode order.
     *
     * @return Character complement.
     */
    public int[] getCharacterComplement() {
        return mCharacterComplement;
    }

    /**
     * This 6-byte field is composed of 3 parts. The first 3 bytes are an industry standard
     * typeface family string. The fourth byte is a treatment character, such as R, B, I.
     * The last two characters are either zeroes for an unbound font or a two character mnemonic
     * for a symbol set if symbol set found.
     * Examples:
     * TNRR00   Times New (text weight, upright)
     * TNRI00   Times New Italic
     * TNRB00   Times New Bold
     * TNRJ00   Times New Bold Italic
     * COUR00   Courier
     * COUI00   Courier Italic
     * COUB00   Courier Bold
     * COUJ00   Courier Bold Italic
     * Treatment Flags
     * R   Text, normal, book, etc.
     * I   Italic, oblique, slanted, etc.
     * B   Bold
     * J   Bold Italic, Bold Oblique
     * D   Demibold
     * E   Demibold Italic, Demibold Oblique
     * K   Black
     * G   Black Italic, Black Oblique
     * L   Light
     * P   Light Italic, Light Oblique
     * C   Condensed
     * A   Condensed Italic, Condensed Oblique
     * F   Bold Condensed
     * H   Bold Condensed Italic, Bold Condensed Oblique
     * S   Semibold (lighter than demibold)
     * T   Semibold Italic, Semibold Oblique
     * other treatment flags are assigned over time.
     *
     * @return File name.
     */
    public int[] getFileName() {
        return mFileName;
    }

    /**
     * This int8 field contains the PCL stroke weight value. Only values in the range -7 to 7
     * are valid:
     * -7   Ultra Thin
     * -6   Extra Thin
     * -5   Thin
     * -4   Extra Light
     * -3   Light
     * -2   Demilight
     * -1   Semilight
     * 0    Book, text, regular, etc.
     * 1    Semibold (Medium, when darker than Book)
     * 2    Demibold
     * 3    Bold
     * 4    Extra Bold
     * 5    Black
     * 6    Extra Black
     * 7    Ultra Black, or Ultra
     * Type designers often use interesting names for weights or combinations of weights and styles,
     * such as Heavy, Compact, Inserat, Bold No. 2, etc. PCL stroke weights are assigned
     * on the basis of the entire family and use of the faces. Typically,
     * display faces don’t have a “text” weight assignment.
     *
     * @return Stroke weight.
     */
    public int getStrokeWeight() {
        return mStrokeWeight;
    }

    /**
     * This int8 field contains the PCL appearance width value. The values are not directly
     * related to those in the appearance with field of the style word above.
     * Only values in the range -5 to 5 are valid.
     * -5   Ultra Compressed
     * -4   Extra Compressed
     * -3   Compressed, or Extra Condensed
     * -2   Condensed
     * 0    Normal
     * 2    Expanded
     * 3    Extra Expanded
     *
     * @return Width type.
     */
    public int getWidthType() {
        return mWidthType;
    }

    /**
     * This uint8 field contains the PCL serif style value. The most significant 2 bits of
     * this byte specify the serif/sans or contrast/monoline characterisitics of the typeface.
     * Bottom 6 bit values:
     * 0    Sans Serif Square
     * 1    Sans Serif Round
     * 2    Serif Line
     * 3    Serif Triangle
     * 4    Serif Swath
     * 5    Serif Block
     * 6    Serif Bracket
     * 7    Rounded Bracket
     * 8    Flair Serif, Modified Sans
     * 9    Script Nonconnecting
     * 10   Script Joining
     * 11   Script Calligraphic
     * 12   Script Broken Letter
     * Top 2 bit values:
     * 0   reserved
     * 1   Sans Serif/Monoline
     * 2   Serif/Contrasting
     * 3   reserved
     *
     * @return Serif style.
     */
    public int getSerifStyle() {
        return mSerifStyle;
    }
}
