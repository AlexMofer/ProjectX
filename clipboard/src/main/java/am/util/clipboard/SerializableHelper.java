/*
 * Copyright (C) 2021 AlexMofer
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
package am.util.clipboard;

import android.os.ParcelFileDescriptor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化数据辅助器
 */
class SerializableHelper {

    private SerializableHelper() {
        //no instance
    }

    public static boolean write(ParcelFileDescriptor descriptor, Serializable serializable) {
        final ObjectOutputStream output;
        try {
            output = new ObjectOutputStream(
                    new ParcelFileDescriptor.AutoCloseOutputStream(descriptor));
        } catch (Exception e) {
            return false;
        }
        try {
            output.writeObject(serializable);
        } catch (Exception e) {
            try {
                output.close();
            } catch (Exception e1) {
                // ignore
            }
            return false;
        }
        boolean finish = true;
        try {
            output.close();
        } catch (Exception e1) {
            finish = false;
        }
        return finish;
    }

    public static Serializable read(ParcelFileDescriptor descriptor) {
        final ObjectInputStream input;
        try {
            input = new ObjectInputStream(
                    new ParcelFileDescriptor.AutoCloseInputStream(descriptor));
        } catch (Exception e) {
            return null;
        }
        final Serializable serializable;
        try {
            serializable = (Serializable) input.readObject();
        } catch (Exception e) {
            try {
                input.close();
            } catch (Exception e1) {
                // ignore
            }
            return null;
        }
        boolean finish = true;
        try {
            input.close();
        } catch (Exception e1) {
            finish = false;
        }
        return finish ? serializable : null;
    }
}
