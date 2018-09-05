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
 * 表记录条目
 * Created by Alex on 2018/9/5.
 */
public class TableRecordEntry {

    private final int mTableTag;// Table identifier.
    private final int mCheckSum;// CheckSum for this table.
    private final int mOffset;// Offset from beginning of TrueType font file.
    private final int mLength;// Length of this table.

    public TableRecordEntry(int tableTag, int checkSum, int offset, int length) {
        mTableTag = tableTag;
        mCheckSum = checkSum;
        mOffset = offset;
        mLength = length;
    }
}
