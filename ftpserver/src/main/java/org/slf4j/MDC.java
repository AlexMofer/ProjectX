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
package org.slf4j;

import java.util.Map;

/**
 * 用于替代的无效实现
 */
@SuppressWarnings("unused")
public class MDC {

    private MDC() {
    }

    /**
     * Put a diagnostic context value (the <code>val</code> parameter) as identified with the
     * <code>key</code> parameter into the current thread's diagnostic context map. The
     * <code>key</code> parameter cannot be null. The <code>val</code> parameter
     * can be null only if the underlying implementation supports it.
     *
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     *
     * @param key non-null key
     * @param val value to put in the map
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static void put(String key, String val) throws IllegalArgumentException {
        // do nothing
    }

    /**
     * Get the diagnostic context identified by the <code>key</code> parameter. The
     * <code>key</code> parameter cannot be null.
     *
     * <p>
     * This method delegates all work to the MDC of the underlying logging system.
     *
     * @param key Key
     * @return the string value identified by the <code>key</code> parameter.
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static String get(String key) throws IllegalArgumentException {
        return null;
    }

    /**
     * Remove the diagnostic context identified by the <code>key</code> parameter using
     * the underlying system's MDC implementation. The <code>key</code> parameter
     * cannot be null. This method does nothing if there is no previous value
     * associated with <code>key</code>.
     *
     * @param key Key
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static void remove(String key) throws IllegalArgumentException {
        // do nothing
    }

    /**
     * Return a copy of the current thread's context map, with keys and values of
     * type String. Returned value may be null.
     *
     * @return A copy of the current thread's context map. May be null.
     * @since 1.5.1
     */
    public static Map<String, String> getCopyOfContextMap() {
        return null;
    }

}
