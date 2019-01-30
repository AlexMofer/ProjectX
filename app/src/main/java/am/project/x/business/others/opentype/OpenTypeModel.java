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
package am.project.x.business.others.opentype;

import android.content.Context;

import am.project.x.ProjectXApplication;
import am.project.x.R;
import am.util.mvp.AMModel;
import am.util.opentype.OpenType;
import am.util.opentype.OpenTypeCollection;
import am.util.opentype.TableRecord;
import am.util.opentype.tables.BaseTable;
import am.util.opentype.tables.NamingTable;
import androidx.annotation.Nullable;

/**
 * Model
 */
class OpenTypeModel extends AMModel<OpenTypePresenter> implements OpenTypeViewModel,
        OpenTypeJob.Callback {

    private boolean mCollection = false;
    private OpenType mFont;
    private OpenTypeCollection mFonts;

    OpenTypeModel(OpenTypePresenter presenter) {
        super(presenter);
    }

    // AdapterViewModel
    @Override
    public int getItemCount() {
        if (mFont == null && mFonts == null)
            return 0;
        if (mFonts == null) {
            return mFont.getTablesSize() + 1;
        } else {
            if (mFont == null)
                return 1;
            return mFont.getTablesSize() + 2;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mFont == null && mFonts == null)
            return null;
        if (mFonts == null) {
            return position == 0 ? mFont : mFont.getTableRecordByIndex(position - 1);
        } else {
            if (mFont == null)
                return mFonts;
            if (position == 0)
                return mFonts;
            else if (position == 1)
                return mFont;
            return mFont.getTableRecordByIndex(position - 2);
        }
    }

    @Override
    public String getItemLabel(Object item) {
        final Context context = ProjectXApplication.getInstance();
        if (item instanceof OpenTypeCollection)
            return context.getString(R.string.ot_title_collection);
        if (item instanceof OpenType)
            return context.getString(R.string.ot_title_font);
        if (item instanceof TableRecord) {
            final TableRecord record = (TableRecord) item;
            switch (record.getTableTag()) {
                default:
                    return context.getString(R.string.ot_title_unknown_table);
                case TableRecord.TAG_CMAP:
                    return "cmap";
                case TableRecord.TAG_HEAD:
                    return "head";
                case TableRecord.TAG_HHEA:
                    return "hhea";
                case TableRecord.TAG_HMTX:
                    return "hmtx";
                case TableRecord.TAG_MAXP:
                    return "maxp";
                case TableRecord.TAG_NAME:
                    return "name";
                case TableRecord.TAG_OS2:
                    return "OS/2";
                case TableRecord.TAG_POST:
                    return "post";
                case TableRecord.TAG_CVT:
                    return "cvt";
                case TableRecord.TAG_FPGM:
                    return "fpgm";
                case TableRecord.TAG_GLYF:
                    return "glyf";
                case TableRecord.TAG_LOCA:
                    return "loca";
                case TableRecord.TAG_PREP:
                    return "prep";
                case TableRecord.TAG_GASP:
                    return "gasp";
                case TableRecord.TAG_CFF:
                    return "CFF";
                case TableRecord.TAG_CFF2:
                    return "CFF2";
                case TableRecord.TAG_VORG:
                    return "VORG";
                case TableRecord.TAG_SVG:
                    return "SVG";
                case TableRecord.TAG_EBDT:
                    return "EBDT";
                case TableRecord.TAG_EBLC:
                    return "EBLC";
                case TableRecord.TAG_EBSC:
                    return "EBSC";
                case TableRecord.TAG_CBDT:
                    return "CBDT";
                case TableRecord.TAG_CBLC:
                    return "CBLC";
                case TableRecord.TAG_SBIX:
                    return "sbix";
                case TableRecord.TAG_BASE:
                    return "BASE";
                case TableRecord.TAG_GDEF:
                    return "GDEF";
                case TableRecord.TAG_GPOS:
                    return "GPOS";
                case TableRecord.TAG_GSUB:
                    return "GSUB";
                case TableRecord.TAG_JSTF:
                    return "JSTF";
                case TableRecord.TAG_MATH:
                    return "MATH";
                case TableRecord.TAG_AVAR:
                    return "avar";
                case TableRecord.TAG_CVAR:
                    return "cvar";
                case TableRecord.TAG_FVAR:
                    return "fvar";
                case TableRecord.TAG_GVAR:
                    return "gvar";
                case TableRecord.TAG_HVAR:
                    return "HVAR";
                case TableRecord.TAG_MVAR:
                    return "MAVR";
                case TableRecord.TAG_STAT:
                    return "STAT";
                case TableRecord.TAG_VVAR:
                    return "VVAR";
                case TableRecord.TAG_COLR:
                    return "COLR";
                case TableRecord.TAG_CPAL:
                    return "CPAL";
                case TableRecord.TAG_DSIG:
                    return "DSIG";
                case TableRecord.TAG_HDMX:
                    return "hdmx";
                case TableRecord.TAG_KERN:
                    return "kern";
                case TableRecord.TAG_LTSH:
                    return "LTSH";
                case TableRecord.TAG_MERG:
                    return "MERG";
                case TableRecord.TAG_META:
                    return "meta";
                case TableRecord.TAG_PCLT:
                    return "PCLT";
                case TableRecord.TAG_VDMX:
                    return "VDMX";
                case TableRecord.TAG_VHEA:
                    return "vhea";
                case TableRecord.TAG_VMTX:
                    return "vmtx";
            }
        }
        return null;
    }

    @Override
    public String getItemInfo(Object item) {
        if (item instanceof OpenTypeCollection || item instanceof OpenType)
            return item.toString();
        if (item instanceof TableRecord && mFont != null) {
            final BaseTable table = mFont.getTable(((TableRecord) item).getTableTag());
            return table == null ? ProjectXApplication.getInstance().getString(
                    R.string.ot_content_unknown_table) : table.toString();
        }
        return null;
    }

    // PickerViewModel
    @Override
    public int getSubCount() {
        return mFonts == null ? 0 : mFonts.getOpenTypesCount();
    }

    @Override
    public Object getSubItem(int position) {
        return mFonts == null ? null : mFonts.getOpenType(position);
    }

    @Override
    public String getSubName(Object item) {
        if (item instanceof OpenType) {
            final OpenType font = (OpenType) item;
            final NamingTable naming = font.getNamingTable();
            if (naming != null)
                return naming.getFullName();
        }
        return null;
    }

    // ViewModel
    @Override
    public void parse(String path) {
        OpenTypeJob.parse(this, path);
    }

    @Override
    public boolean isCollection() {
        return mCollection;
    }

    @Override
    public void setCollectionItem(int position) {
        if (mFonts == null)
            return;
        mFont = mFonts.getOpenType(position);
    }

    // Callback
    @Override
    public void onParseFailure() {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onParseFailure();
    }

    @Override
    public void onParseSuccess(boolean isCollection,
                               @Nullable OpenType font, @Nullable OpenTypeCollection fonts) {
        mCollection = isCollection;
        mFont = font;
        mFonts = fonts;
        if (isDetachedFromPresenter())
            return;
        getPresenter().onParseSuccess(isCollection);
    }
}
