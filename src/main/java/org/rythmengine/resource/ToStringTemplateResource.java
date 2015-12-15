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
package org.rythmengine.resource;

import org.rythmengine.internal.dialect.AutoToString;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 20/01/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToStringTemplateResource extends TemplateResourceBase implements ITemplateResource {

    private static final long serialVersionUID = -1428821795298562604L;
    private AutoToString.AutoToStringData meta;

    public ToStringTemplateResource(AutoToString.AutoToStringData data) {
        meta = data;
    }

    @Override
    public Object getKey() {
        return meta;
    }

    @Override
    public String asTemplateContent() {
        return ""; // the meta data is fetched via AutoToString dialect
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
        return "";
    }

    @Override
    public String getSuggestedClassName() {
        try {
            return "C" + UUID.nameUUIDFromBytes(meta.toString().getBytes("utf-8")).toString().replace('-', '_');
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
