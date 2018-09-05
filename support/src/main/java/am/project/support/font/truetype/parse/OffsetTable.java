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
package am.project.support.font.truetype.parse;

/**
 * 偏移表
 * Created by Xiang Zhicheng on 2018/9/5.
 */
public class OffsetTable {

    private final int mSFNTVersion;// 32-bit unsigned integer, 0x00010000 or 0x4F54544F ('OTTO') or 0x74746366 ('ttcf')
    private final int mNumTables;// 16-bit unsigned integer, Number of tables.
    private final int mSearchRange;// 16-bit unsigned integer, (Maximum power of 2 <= numTables) x 16.
    private final int mEntrySelector;// 16-bit unsigned integer, Log2(maximum power of 2 <= numTables).
    private final int mRangeShift;// 16-bit unsigned integer, NumTables x 16-searchRange.

    public OffsetTable(int sfntVersion, int numTables, int searchRange, int entrySelector,
                       int rangeShift) {
        mSFNTVersion = sfntVersion;
        mNumTables = numTables;
        mSearchRange = searchRange;
        mEntrySelector = entrySelector;
        mRangeShift = rangeShift;
    }

    public int getSFNTVersion() {
        return mSFNTVersion;
    }

    public int getNumTables() {
        return mNumTables;
    }

    public int getSearchRange() {
        return mSearchRange;
    }

    public int getEntrySelector() {
        return mEntrySelector;
    }

    public int getRangeShift() {
        return mRangeShift;
    }
}
