package am.util.opentype.tables;

/**
 * 命名记录
 */
public class NameRecord {

    public static final int PLATFORM_UNICODE = 0;
    public static final int PLATFORM_MACINTOSH = 1;
    // Platform ID 2 (ISO) has been deprecated as of OpenType version v1.3.
    // It was intended to represent ISO/IEC 10646, as opposed to Unicode.
    // It is redundant, however, since both standards have identical character code assignments.
    @Deprecated
    public static final int PLATFORM_ISO = 2;
    public static final int PLATFORM_WINDOWS = 3;
    public static final int PLATFORM_CUSTOM = 4;

    // Platform-specific encoding and language IDs: Unicode platform (platform ID = 0)
    public static final int ENCODING_UNICODE_0 = 0;// Unicode 1.0 semantics
    public static final int ENCODING_UNICODE_1 = 1;// Unicode 1.1 semantics
    public static final int ENCODING_UNICODE_2 = 2;// ISO/IEC 10646 semantics
    public static final int ENCODING_UNICODE_3 = 3;// Unicode 2.0 and onwards semantics, Unicode BMP only ('cmap' subtable formats 0, 4, 6).
    public static final int ENCODING_UNICODE_4 = 4;// Unicode 2.0 and onwards semantics, Unicode full repertoire ('cmap' subtable formats 0, 4, 6, 10, 12).
    public static final int ENCODING_UNICODE_5 = 5;// Unicode Variation Sequences ('cmap' subtable format 14).
    public static final int ENCODING_UNICODE_6 = 6;// Unicode full repertoire ('cmap' subtable formats 0, 4, 6, 10, 12, 13).

    // Platform-specific encoding and language IDs: Windows platform (platform ID= 3)
    public static final int ENCODING_WINDOWS_0 = 0;// Symbol
    public static final int ENCODING_WINDOWS_1 = 1;// Unicode BMP
    public static final int ENCODING_WINDOWS_2 = 2;// ShiftJIS
    public static final int ENCODING_WINDOWS_3 = 3;// PRC
    public static final int ENCODING_WINDOWS_4 = 4;// Big5
    public static final int ENCODING_WINDOWS_5 = 5;// Wansung
    public static final int ENCODING_WINDOWS_6 = 6;// Johab
    public static final int ENCODING_WINDOWS_7 = 7;// Reserved
    public static final int ENCODING_WINDOWS_8 = 8;// Reserved
    public static final int ENCODING_WINDOWS_9 = 9;// Reserved
    public static final int ENCODING_WINDOWS_10 = 10;// Unicode full repertoire

    public static final int LANGUAGE_WINDOWS_af_ZA = 0x0436;// Afrikaans South Africa


    private final int mPlatformID;// Platform ID. Platform ID values 240 through 255 are reserved for user-defined platforms. This specification will never assign these values to a registered platform.
    private final int mEncodingID;// Platform-specific encoding ID.
    private final int mLanguageID;// Language ID.
    private final int mNameID;// Name ID.
    private final int mLength;// String length (in bytes).
    private final int mOffset;// String offset from start of storage area (in bytes).

    public NameRecord(int platformID, int encodingID, int languageID, int nameID,
                      int length, int offset) {
        mPlatformID = platformID;
        mEncodingID = encodingID;
        mLanguageID = languageID;
        mNameID = nameID;
        mLength = length;
        mOffset = offset;
    }
}
