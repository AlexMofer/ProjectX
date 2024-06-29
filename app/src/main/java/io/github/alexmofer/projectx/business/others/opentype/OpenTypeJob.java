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
package io.github.alexmofer.projectx.business.others.opentype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am.font.opentype.FileOpenTypeReader;
import com.am.font.opentype.OpenType;
import com.am.font.opentype.OpenTypeCollection;
import com.am.font.opentype.OpenTypeParser;
import com.am.font.opentype.OpenTypeReader;
import com.am.font.opentype.TableRecord;
import com.am.job.Job;

import java.io.File;

/**
 * Job
 */
class OpenTypeJob extends Job<OpenTypeJob.Callback> {

    private OpenTypeJob(Callback callback, int id, Object... params) {
        super(callback, id, params);
    }

    static void parse(Callback callback, String path) {
        new OpenTypeJob(callback, 0, path).execute();
    }

    @Override
    protected void doInBackground(@NonNull Result result) {
        final String path = getParams().getString(0);
        final File font = new File(path);
        if (!font.exists() || !font.isFile() || !font.canRead())
            return;
        boolean success = false;
        boolean isCollection = false;
        OpenType ot = null;
        OpenTypeCollection otc = null;
        OpenTypeReader reader = null;
        try {
            reader = new FileOpenTypeReader(font);
            final OpenTypeParser parser = new OpenTypeParser();
            parser.parse(reader, TableRecord.TAG_CMAP, TableRecord.TAG_HEAD, TableRecord.TAG_HHEA,
                    TableRecord.TAG_MAXP, TableRecord.TAG_HMTX, TableRecord.TAG_NAME,
                    TableRecord.TAG_OS2, TableRecord.TAG_POST, TableRecord.TAG_CVT,
                    TableRecord.TAG_FPGM, TableRecord.TAG_GLYF, TableRecord.TAG_LOCA,
                    TableRecord.TAG_PREP, TableRecord.TAG_GASP, TableRecord.TAG_CFF,
                    TableRecord.TAG_CFF2, TableRecord.TAG_VORG, TableRecord.TAG_SVG,
                    TableRecord.TAG_EBDT, TableRecord.TAG_EBLC, TableRecord.TAG_EBSC,
                    TableRecord.TAG_CBDT, TableRecord.TAG_CBLC, TableRecord.TAG_SBIX,
                    TableRecord.TAG_BASE, TableRecord.TAG_GDEF, TableRecord.TAG_GPOS,
                    TableRecord.TAG_GSUB, TableRecord.TAG_JSTF, TableRecord.TAG_MATH,
                    TableRecord.TAG_AVAR, TableRecord.TAG_CVAR, TableRecord.TAG_FVAR,
                    TableRecord.TAG_GVAR, TableRecord.TAG_HVAR, TableRecord.TAG_MVAR,
                    TableRecord.TAG_STAT, TableRecord.TAG_VVAR, TableRecord.TAG_COLR,
                    TableRecord.TAG_CPAL, TableRecord.TAG_HDMX, TableRecord.TAG_KERN,
                    TableRecord.TAG_LTSH, TableRecord.TAG_MERG, TableRecord.TAG_META,
                    TableRecord.TAG_PCLT, TableRecord.TAG_VDMX, TableRecord.TAG_VHEA,
                    TableRecord.TAG_VMTX);
            if (!parser.isInvalid()) {
                success = true;
                isCollection = parser.isCollection();
                ot = parser.getOpenType();
                otc = parser.getOpenTypeCollection();
            }
        } catch (Exception e) {
            // ignore
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
        if (success) {
            result.set(true, isCollection, ot, otc);
        }
    }

    @Override
    protected void onResult(@NonNull Callback callback, @NonNull Result result) {
        super.onResult(callback, result);
        if (result.isSuccess())
            callback.onParseSuccess(result.getBoolean(0), result.get(1), result.get(2));
        else
            callback.onParseFailure();
    }

    public interface Callback {
        void onParseFailure();

        void onParseSuccess(boolean isCollection,
                            @Nullable OpenType font, @Nullable OpenTypeCollection fonts);
    }
}
