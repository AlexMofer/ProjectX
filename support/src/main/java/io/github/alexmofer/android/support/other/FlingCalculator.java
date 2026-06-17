/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.other;

import android.content.Context;
import android.view.ViewConfiguration;

/**
 * Fling 飞行距离计算工具类
 * Created by Alex on 2026/6/11.
 */
public final class FlingCalculator {

    // 系统特有的衰减速率常数 (Math.log(0.78) / Math.log(0.9))
    private static final float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    // 物理微调常数
    private static final float INFLEXION = 0.35f;

    private final float mPhysicalCoeff;
    private float mFriction = ViewConfiguration.getScrollFriction();

    public FlingCalculator(Context context) {
        // 1. 依据系统的 ViewConfiguration 核心公式，计算出当前设备的物理减速系数
        final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
        // 39.37f 是 英寸转米 换算常数， 0.84f 是系统内部写死的阻力微调系数
        mPhysicalCoeff = 9.80665f * 39.37f * ppi * 0.84f;
    }

    /**
     * 修改摩擦系数（如果需要自定义滑动阻力，可以调用此方法，默认使用系统内置阻力）
     */
    public void setFriction(float friction) {
        mFriction = friction;
    }

    /**
     * 根据初速度计算 Fling 的绝对飞行距离
     *
     * @param velocity 离屏初速度（pixels/second）
     * @return 最终滑行的物理像素距离
     */
    public double getFlingDistance(float velocity) {
        if (velocity == 0) {
            return 0;
        }
        final double l = getSplineDeceleration(velocity);
        final double decelerationDelta = DECELERATION_RATE - 1.0;
        return mFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelerationDelta * l);
    }

    /**
     * 根据初速度计算 Fling 的持续时间（毫秒）
     * 如果你配合 Spring 动画做预测，这个值也可以作为参考依据
     */
    public int getFlingDuration(float velocity) {
        if (velocity == 0) {
            return 0;
        }
        final double l = getSplineDeceleration(velocity);
        final double decelerationDelta = DECELERATION_RATE - 1.0;
        return (int) (1000.0 * Math.exp(l / decelerationDelta));
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFriction * mPhysicalCoeff));
    }
}
