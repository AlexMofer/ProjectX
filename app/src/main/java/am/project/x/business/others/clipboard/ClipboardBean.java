/*
 * Copyright (C) 2019 AlexMofer
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
package am.project.x.business.others.clipboard;

import java.io.Serializable;
import java.util.Random;

/**
 * 数据
 * Created by Alex on 2019/3/13.
 */
final class ClipboardBean implements Serializable {
    private byte mByte;
    private short mShort;
    private int mInt;
    private long mLong;
    private float mFloat;
    private double mDouble;
    private boolean mBoolean;
    private char mChar;
    private String mString;

    private ClipboardBean(byte mByte, short mShort, int mInt, long mLong, float mFloat,
                          double mDouble, boolean mBoolean, char mChar, String mString) {
        this.mByte = mByte;
        this.mShort = mShort;
        this.mInt = mInt;
        this.mLong = mLong;
        this.mFloat = mFloat;
        this.mDouble = mDouble;
        this.mBoolean = mBoolean;
        this.mChar = mChar;
        this.mString = mString;
    }

    static ClipboardBean test() {
        final Random random = new Random();
        final int v = random.nextInt(250);
        return new ClipboardBean((byte) (127 - v), (short) (32767 - v), Integer.MAX_VALUE / v,
                Long.MAX_VALUE / v, Float.MAX_VALUE / v,
                Double.MAX_VALUE / v, true, Character.MAX_VALUE,
                "Test:" + v);
    }

    @Override
    public String toString() {
        return "ClipboardBean{" +
                "mByte=" + Byte.toString(mByte) +
                ", mShort=" + Short.toString(mShort) +
                ", mInt=" + Integer.toString(mInt) +
                ", mLong=" + Long.toString(mLong) +
                ", mFloat=" + Float.toString(mFloat) +
                ", mDouble=" + Double.toString(mDouble) +
                ", mBoolean=" + Boolean.toString(mBoolean) +
                ", mChar=" + Character.toString(mChar) +
                ", mString='" + mString + '\'' +
                '}';
    }
}
