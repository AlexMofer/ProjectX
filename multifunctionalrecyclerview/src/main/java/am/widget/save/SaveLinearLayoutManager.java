/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.save;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.PublicLinearLayoutManager;
import android.util.AttributeSet;

/**
 * 简化状态恢复与保存
 * Created by Alex on 2017/11/3.
 */
@SuppressWarnings("unused")
public class SaveLinearLayoutManager extends PublicLinearLayoutManager {


    public SaveLinearLayoutManager(Context context) {
        super(context);
    }

    public SaveLinearLayoutManager(Context context, int orientation,
                                   boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SaveLinearLayoutManager(Context context, AttributeSet attrs,
                                   int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        return new BaseSavedState(bundle, (SavedState) super.onSaveInstanceState());
    }

    /**
     * 存储状态
     *
     * @param bundle 数据包
     */
    protected void onSaveInstanceState(Bundle bundle) {
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState savedState = (BaseSavedState) state;
        onRestoreInstanceState(savedState.getState());
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    /**
     * 恢复状态
     *
     * @param bundle 数据包
     */
    protected void onRestoreInstanceState(Bundle bundle) {
    }

    private static class BaseSavedState implements Parcelable {

        public static final Creator<BaseSavedState> CREATOR
                = new Creator<SaveLinearLayoutManager.BaseSavedState>() {
            @Override
            public SaveLinearLayoutManager.BaseSavedState createFromParcel(Parcel in) {
                return new SaveLinearLayoutManager.BaseSavedState(in);
            }

            @Override
            public SaveLinearLayoutManager.BaseSavedState[] newArray(int size) {
                return new SaveLinearLayoutManager.BaseSavedState[size];
            }
        };
        private final SavedState mSuperState;
        private final Bundle mState;

        private BaseSavedState(Bundle state, SavedState superState) {
            mState = state;
            mSuperState = superState;
        }

        @SuppressWarnings("all")
        private BaseSavedState(Parcel source) {
            mSuperState = source.readParcelable(SavedState.class.getClassLoader());
            mState = source.readBundle();
        }

        private Parcelable getSuperState() {
            return mSuperState;
        }

        private Bundle getState() {
            return mState;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mSuperState, flags);
            dest.writeBundle(mState);
        }
    }
}
