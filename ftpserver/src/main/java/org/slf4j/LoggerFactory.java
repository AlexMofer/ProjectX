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

/**
 * 用于替代的无效实现
 */
@SuppressWarnings("unused")
public final class LoggerFactory {

    private static final Logger LOGGER = new NoLogger();

    private LoggerFactory() {
        //no instance
    }

    public static Logger getLogger(String name) {
        return LOGGER;
    }

    public static Logger getLogger(Class<?> clazz) {
        return LOGGER;
    }

    private static class NoLogger implements Logger {
        @Override
        public void trace(String msg) {
            // do nothing
        }

        @Override
        public void trace(String format, Object arg) {
            // do nothing
        }

        @Override
        public void trace(String msg, Throwable t) {
            // do nothing
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String msg) {
            // do nothing
        }

        @Override
        public void debug(String format, Object arg) {
            // do nothing
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            // do nothing
        }

        @Override
        public void debug(String format, Object... arguments) {
            // do nothing
        }

        @Override
        public void debug(String msg, Throwable t) {
            // do nothing
        }

        @Override
        public void info(String msg) {
            // do nothing
        }

        @Override
        public void info(String format, Object arg) {
            // do nothing
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            // do nothing
        }

        @Override
        public void info(String msg, Throwable t) {
            // do nothing
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String msg) {
            // do nothing
        }

        @Override
        public void warn(String format, Object arg) {
            // do nothing
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            // do nothing
        }

        @Override
        public void warn(String msg, Throwable t) {
            // do nothing
        }

        @Override
        public void error(String msg) {
            // do nothing
        }

        @Override
        public void error(String format, Object arg) {
            // do nothing
        }

        @Override
        public void error(String msg, Throwable t) {
            // do nothing
        }
    }
}
