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
package am.util.opentype;

import java.util.Arrays;
import java.util.List;

/**
 * OpenType字体集
 * Created by Alex on 2018/9/6.
 */
@SuppressWarnings("unused")
public class OpenTypeCollection {

    private final int mTtcTag;// Font Collection ID string: 'ttcf'
    private final int mMajorVersion;// Major version of the TTC Header, = 1 or 2.
    private final int mMinorVersion;// Minor version of the TTC Header, = 0.
    private final int mNumFonts;// Number of fonts in TTC
    private final int[] mOffsetTableOffsets;// Array of offsets to the OffsetTable for each font from the beginning of the file
    private final boolean mDSIGTableEnable;// Tag indicating that a DSIG table exists, 0x44534947 ('DSIG') (null if no signature)
    private final int mDSIGLength;// The length (in bytes) of the DSIG table (null if no signature)
    private final int mDSIGOffset;// The offset (in bytes) of the DSIG table from the beginning of the TTC file (null if no signature)
    private final List<OpenType> mFonts;

    @SuppressWarnings("WeakerAccess")
    public OpenTypeCollection(int ttcTag, int majorVersion, int minorVersion, int numFonts,
                              int[] offsetTableOffsets,
                              boolean DSIGTableEnable, int DSIGLength, int DSIGOffset,
                              List<OpenType> fonts) {
        mTtcTag = ttcTag;
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
        mNumFonts = numFonts;
        mOffsetTableOffsets = offsetTableOffsets;
        mDSIGTableEnable = DSIGTableEnable;
        mDSIGLength = DSIGLength;
        mDSIGOffset = DSIGOffset;
        mFonts = fonts;
    }

    /**
     * 获取TTC ID
     *
     * @return TTC ID 其值编码为String必然为'ttcf'
     */
    public int getTtcTag() {
        return mTtcTag;
    }

    /**
     * 获取主版本号
     *
     * @return 主版本号
     */
    public int getMajorVersion() {
        return mMajorVersion;
    }

    /**
     * 获取次版本号
     *
     * @return 次版本号
     */
    public int getMinorVersion() {
        return mMinorVersion;
    }

    /**
     * 获取字体数目
     *
     * @return 字体数目
     */
    public int getOpenTypesCount() {
        return mNumFonts;
    }

    /**
     * 获取所有字体偏移表的偏移量
     *
     * @return 偏移量集
     */
    public int[] getOffsetTableOffsets() {
        return mOffsetTableOffsets;
    }

    /**
     * 判断数字签名表是否可用
     *
     * @return 数字签名表是否可用，仅2.0及以上版本才可能有数字签名表
     */
    public boolean isDSIGTableEnable() {
        return mDSIGTableEnable;
    }

    /**
     * 获取数字签名表长度
     *
     * @return 长度
     */
    public int getDSIGLength() {
        return mDSIGLength;
    }

    /**
     * 获取数字签名表偏移量
     *
     * @return 偏移量
     */
    public int getDSIGOffset() {
        return mDSIGOffset;
    }

    /**
     * 获取字体集
     *
     * @return 字体集
     */
    public List<OpenType> getOpenTypes() {
        return mFonts;
    }

    /**
     * 获取字体
     *
     * @param index 下标
     * @return 字体
     */
    public OpenType getOpenType(int index) {
        return mFonts.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenTypeCollection that = (OpenTypeCollection) o;
        return mTtcTag == that.mTtcTag &&
                mMajorVersion == that.mMajorVersion &&
                mMinorVersion == that.mMinorVersion &&
                mNumFonts == that.mNumFonts &&
                mDSIGTableEnable == that.mDSIGTableEnable &&
                mDSIGLength == that.mDSIGLength &&
                mDSIGOffset == that.mDSIGOffset &&
                Arrays.equals(mOffsetTableOffsets, that.mOffsetTableOffsets) &&
                Objects.equals(mFonts, that.mFonts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mTtcTag, mMajorVersion, mMinorVersion, mNumFonts,
                mDSIGTableEnable, mDSIGLength, mDSIGOffset, mFonts);
        result = 31 * result + Arrays.hashCode(mOffsetTableOffsets);
        return result;
    }

    @Override
    public String toString() {
        return "OpenTypeCollection{" +
                "ttcTag=" + mTtcTag +
                ", majorVersion=" + mMajorVersion +
                ", minorVersion=" + mMinorVersion +
                ", numFonts=" + mNumFonts +
                ", offsetTableOffsets=" + Arrays.toString(mOffsetTableOffsets) +
                ", DSIGTableEnable=" + mDSIGTableEnable +
                ", DSIGLength=" + mDSIGLength +
                ", DSIGOffset=" + mDSIGOffset +
                '}';
    }
}
