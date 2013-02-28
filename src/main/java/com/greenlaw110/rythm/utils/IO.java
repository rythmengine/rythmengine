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
package com.greenlaw110.rythm.utils;

import java.io.*;
import java.net.URL;

/**
 * IO utilities
 */
// Most of the code come from Play!Framework IO.java, under Apache License 2.0
public class IO {

    /**
     * Read file content to a String (always use utf-8)
     *
     * @param file The file to read
     * @return The String content
     */
    public static String readContentAsString(File file) {
        return readContentAsString(file, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param url The url resource to read
     * @return The String content
     */
    public static String readContentAsString(URL url, String encoding) {
        try {
            return readContentAsString(url.openStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read file content to a String (always use utf-8)
     *
     * @param url the url resource to read
     * @return The String content
     */
    public static String readContentAsString(URL url) {
        return readContentAsString(url, "utf-8");
    }

    /**
     * Read file content to a String
     *
     * @param file The file to read
     * @return The String content
     */
    public static String readContentAsString(File file, String encoding) {
        try {
            return readContentAsString(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readContentAsString(InputStream is) {
        return readContentAsString(is, "utf-8");
    }

    public static String readContentAsString(InputStream is, String encoding) {
        try {
            StringWriter result = new StringWriter();
            PrintWriter out = new PrintWriter(result);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                if (lineNo++ > 0) out.println();
                out.print(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    /**
     * Write String content to a file (always use utf-8)
     *
     * @param content The content to write
     * @param file    The file to write
     */
    public static void writeContent(CharSequence content, File file) {
        writeContent(content, file, "utf-8");
    }

    /**
     * Write String content to a file (always use utf-8)
     *
     * @param content The content to write
     * @param file    The file to write
     */
    public static void writeContent(CharSequence content, File file, String encoding) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(os, encoding));
            printWriter.println(content);
            printWriter.flush();
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) os.close();
            } catch (Exception e) {
                //
            }
        }
    }

    public static void writeContent(CharSequence content, Writer writer) {
        try {
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.println(content);
            printWriter.flush();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (Exception e) {
                //
            }
        }
    }


}
