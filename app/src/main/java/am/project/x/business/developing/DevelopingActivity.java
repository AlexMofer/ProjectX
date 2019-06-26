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
package am.project.x.business.developing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;

import androidx.annotation.Nullable;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.privateapi.viewtreeobserver.InternalInsetsInfo;
import am.project.x.privateapi.viewtreeobserver.OnComputeInternalInsetsListener;
import am.project.x.privateapi.viewtreeobserver.ViewTreeObserverPrivateApis;

/**
 * 正在开发
 */
public class DevelopingActivity extends BaseActivity implements OnComputeInternalInsetsListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, DevelopingActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_developing;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.developing_toolbar);

        ViewTreeObserverPrivateApis.addOnComputeInternalInsetsListener(
                getWindow().getDecorView().getRootView().getViewTreeObserver(),
                ViewTreeObserverPrivateApis.newInnerOnComputeInternalInsetsListener(this));
    }

    @Override
    public void onComputeInternalInsets(InternalInsetsInfo inoutInfo) {
        try {
            final Rect contentInsets = inoutInfo.getContentInsets();
            contentInsets.setEmpty();
            final Rect visibleInsets = inoutInfo.getVisibleInsets();
            visibleInsets.setEmpty();
            final Region touchableRegion = inoutInfo.getTouchableRegion();
            touchableRegion.set(0, 0, 1080, 100);
        } catch (Exception e) {
            // ignore
        }
        final boolean result = inoutInfo.setTouchableInsets(InternalInsetsInfo.TOUCHABLE_INSETS_REGION);
        System.out.println("lalalalaal-------------------------------------setTouchableInsets:" + result);
    }
}
