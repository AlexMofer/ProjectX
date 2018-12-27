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

package am.widget.multifunctionalimageview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.reflect.Constructor;

/**
 * 从XML布局文件中构造对象
 * Created by Alex on 2018/12/27.
 */
class ConstructorHelper {

    private static final Class<?>[] CONSTRUCTOR_1 =
            new Class[]{Context.class};
    private static final Class<?>[] CONSTRUCTOR_2 =
            new Class[]{Context.class, AttributeSet.class};
    private static final Class<?>[] CONSTRUCTOR_3 =
            new Class[]{Context.class, AttributeSet.class, int.class};
    private static final Class<?>[] CONSTRUCTOR_4 =
            new Class[]{Context.class, AttributeSet.class, int.class, int.class};

    @SuppressWarnings("SameParameterValue")
    static <T> T newInstance(Context context, String className,
                             boolean inEditMode, Object creator, Class<T> clazz,
                             AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (className == null || TextUtils.isEmpty(className))
            return null;
        className = className.trim();
        if (TextUtils.isEmpty(className))
            return null;
        if (className.charAt(0) == '.')
            className = context.getPackageName() + className;
        final ClassLoader classLoader = inEditMode ?
                creator.getClass().getClassLoader() : context.getClassLoader();
        if (classLoader == null)
            return null;
        final Class<?> cs;
        try {
            cs = classLoader.loadClass(className);
        } catch (Exception e) {
            return null;
        }
        final Class<? extends T> subclass;
        try {
            subclass = cs.asSubclass(clazz);
        } catch (Exception e) {
            return null;
        }
        try {
            final Constructor constructor = subclass.getConstructor(CONSTRUCTOR_4);
            constructor.setAccessible(true);
            //noinspection unchecked
            return (T) constructor.newInstance(
                    new Object[]{context, attrs, defStyleAttr, defStyleRes});
        } catch (Exception e) {
            // ignore
        }
        try {
            final Constructor constructor = subclass.getConstructor(CONSTRUCTOR_3);
            constructor.setAccessible(true);
            //noinspection unchecked
            return (T) constructor.newInstance(new Object[]{context, attrs, defStyleAttr});
        } catch (Exception e) {
            // ignore
        }
        try {
            final Constructor constructor = subclass.getConstructor(CONSTRUCTOR_2);
            constructor.setAccessible(true);
            //noinspection unchecked
            return (T) constructor.newInstance(new Object[]{context, attrs});
        } catch (Exception e) {
            // ignore
        }
        try {
            final Constructor constructor = subclass.getConstructor(CONSTRUCTOR_1);
            constructor.setAccessible(true);
            //noinspection unchecked
            return (T) constructor.newInstance(new Object[]{context});
        } catch (Exception e) {
            // ignore
        }
        try {
            final Constructor constructor = subclass.getConstructor();
            constructor.setAccessible(true);
            //noinspection unchecked
            return (T) constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
