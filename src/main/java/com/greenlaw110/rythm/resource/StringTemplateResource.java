/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.resource;

import com.greenlaw110.rythm.RythmEngine;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private String content;

    public StringTemplateResource(String templateContent) {
        content = templateContent;
    }

    public StringTemplateResource(String templateContent, RythmEngine engine) {
        super(engine);
        content = templateContent;
    }

    @Override
    public String getKey() {
        return content;
    }

    @Override
    public String asTemplateContent() {
        return content;
    }

    @Override
    protected long lastModified() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected long defCheckInterval() {
        return -1;
    }

    @Override
    protected Long userCheckInterval() {
        return null;
    }

    @Override
    protected String reload() {
        return content;
    }

    @Override
    public String getSuggestedClassName() {
        try {
            return "C" + UUID.nameUUIDFromBytes(content.getBytes("utf-8")).toString().replace('-', '_');
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof StringTemplateResource) {
            StringTemplateResource that = (StringTemplateResource) obj;
            return that.getKey().equals(getKey());
        }
        return false;
    }
}
