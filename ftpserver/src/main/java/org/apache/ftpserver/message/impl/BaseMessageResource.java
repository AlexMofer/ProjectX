/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ftpserver.message.impl;

import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.message.MessageResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Class to get FtpServer reply messages.
 * Created by Alex on 2018/1/21.
 */

public class BaseMessageResource implements MessageResource {

    private static final String BASE_NAME = "FTPStatusMessage";
    private final HashSet<String> languages = new HashSet<>();
    private final HashMap<String, ResourceBundle> messagesMap = new HashMap<>();
    private ResourceBundle defaultMessages;

    public BaseMessageResource() {
        ResourceBundle def;
        try {
            def = ResourceBundle.getBundle(BASE_NAME);
        } catch (Exception e) {
            def = null;
        }
        if (def == null) {
            throw new FtpServerConfigurationException("Failed to load default messages.");
        }
        defaultMessages = def;
        add(Locale.ENGLISH);
        add(Locale.SIMPLIFIED_CHINESE);
        add(Locale.TRADITIONAL_CHINESE);
    }

    private void add(Locale locale) {
        ResourceBundle resource;
        try {
            resource = ResourceBundle.getBundle(BASE_NAME, locale);
        } catch (Exception e) {
            resource = null;
        }
        if (resource != null) {
            add(locale.toString(), resource);
        }
    }

    public void setDefault(ResourceBundle messages) {
        if (messages == null)
            return;
        defaultMessages = messages;
    }

    public void add(String language, ResourceBundle messages) {
        if (language == null || language.length() <= 0 || messages == null)
            return;
        languages.add(language);
        messagesMap.put(language, messages);
    }

    public boolean contains(String language) {
        return languages.contains(language);
    }

    public ResourceBundle remove(String language) {
        return languages.remove(language) ? messagesMap.remove(language) : null;
    }


    @Override
    public List<String> getAvailableLanguages() {
        return languages.isEmpty() ? null : Collections.unmodifiableList(new ArrayList<>(languages));
    }

    @Override
    public String getMessage(int code, String subId, String language) {
        final ResourceBundle messages = getMessagesByLanguage(language);
        final String key = subId != null ? Integer.toString(code) + "." + subId
                : Integer.toString(code);
        Object message;
        try {
            message = messages.getObject(key);
        } catch (Exception e) {
            message = null;
        }
        if (message == null) {
            try {
                message = defaultMessages.getObject(key);
            } catch (Exception e) {
                message = null;
            }
        }
        if (message == null)
            return null;
        return message instanceof String ? (String) message : message.toString();
    }

    private ResourceBundle getMessagesByLanguage(String language) {
        ResourceBundle messages = messagesMap.get(language);
        if (messages == null) {
            messages = defaultMessages;
        }
        return messages;
    }

    @Override
    public Map<String, String> getMessages(String language) {
        final ResourceBundle messages = getMessagesByLanguage(language);
        final Enumeration<String> keys = messages.getKeys();
        final HashMap<String, String> result = new HashMap<>();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            final Object message = messages.getObject(key);
            result.put(key, message instanceof String ? (String) message : message.toString());
        }
        return Collections.unmodifiableMap(result);
    }
}
