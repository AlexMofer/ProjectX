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

import java.io.IOException;
import java.util.List;

import am.project.support.font.truetype.TrueTypeReader;

/**
 * 字体解析器
 * Created by Alex on 2018/9/5.
 */
public class TrueTypeParser {

    private static final int OTF = 0x00010000;// OpenType fonts that contain TrueType outlines
    private static final int OTTO = 0x4F54544F;// OpenType fonts containing CFF data (version 1 or 2)
    private static final int TTCF = 0x74746366;// An OpenType Font Collection (formerly known as TrueType Collection)

    private final TrueTypeReader mReader;
    private boolean mInvalid;// 无效字体文件

    public TrueTypeParser(TrueTypeReader reader) {
        mReader = reader;
    }

    private void parse() throws IOException {
        if (mInvalid)
            return;
        final int begin;
        try {
            mReader.seek(0);
            begin = mReader.readInt();
        } catch (Exception e) {
            mInvalid = true;
            return;
        }
        switch (begin) {
            default:
                mInvalid = true;
                break;
            case OTF:
            case OTTO:
                parseOpenType(0);
                break;
            case TTCF:
                parseCollections();
                break;
        }
    }

    private void parseOpenType(long pos) throws IOException {
        mReader.seek(pos);
        final int sfntVersion = mReader.readInt();
        final int numTables = mReader.readUnsignedShort();
        final int searchRange = mReader.readUnsignedShort();
        final int entrySelector = mReader.readUnsignedShort();
        final int rangeShift = mReader.readUnsignedShort();
        final OffsetTable ot = new OffsetTable(sfntVersion, numTables, searchRange, entrySelector,
                rangeShift);
        for (int i = 0; i < numTables; i++) {

        }

    }

    private void parseCollections() throws IOException {
        mReader.seek(0);

    }

    public List<String> getFullNames() throws IOException {
        parse();// TODO
        return null;
    }

}
