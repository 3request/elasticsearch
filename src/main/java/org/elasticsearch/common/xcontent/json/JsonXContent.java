/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common.xcontent.json;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.elasticsearch.common.io.FastStringReader;
import org.elasticsearch.common.xcontent.*;

import java.io.*;

/**
 * A JSON based content implementation using Jackson.
 *
 *
 */
public class JsonXContent implements XContent {

    public static XContentBuilder contentBuilder() throws IOException {
        return XContentBuilder.builder(jsonXContent);
    }

    private final static JsonFactory jsonFactory;
    public final static JsonXContent jsonXContent;

    static {
        jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jsonFactory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        jsonXContent = new JsonXContent();
    }

    private JsonXContent() {
    }

    @Override
    public XContentType type() {
        return XContentType.JSON;
    }

    @Override
    public byte streamSeparator() {
        return '\n';
    }

    @Override
    public XContentGenerator createGenerator(OutputStream os) throws IOException {
        return new JsonXContentGenerator(jsonFactory.createJsonGenerator(os, JsonEncoding.UTF8));
    }

    @Override
    public XContentGenerator createGenerator(Writer writer) throws IOException {
        return new JsonXContentGenerator(jsonFactory.createJsonGenerator(writer));
    }

    @Override
    public XContentParser createParser(String content) throws IOException {
        return new JsonXContentParser(jsonFactory.createJsonParser(new FastStringReader(content)));
    }

    @Override
    public XContentParser createParser(InputStream is) throws IOException {
        return new JsonXContentParser(jsonFactory.createJsonParser(is));
    }

    @Override
    public XContentParser createParser(byte[] data) throws IOException {
        return new JsonXContentParser(jsonFactory.createJsonParser(data));
    }

    @Override
    public XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return new JsonXContentParser(jsonFactory.createJsonParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(Reader reader) throws IOException {
        return new JsonXContentParser(jsonFactory.createJsonParser(reader));
    }
}