/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.blitz.bamboo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.blitz.gson.ArrayDeserializer;
import io.blitz.gson.MapDeserializer;
import java.util.Collection;
import java.util.Map;

/**
 * Used to serialize and deserialize JSON.
 * @author ghermeto
 */
public class JsonConverter {

    /**
     * Serializes a object into a JSON string
     * @param result object to be serialized
     * @return JSON string
     */
    public static String toJson(Object result) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(result);
        return json;
    }
    
    /**
     * Deserializes a JSON string
     * @param json JSON string
     * @return object from JSON
     */
    public static Map<String, Object> fromJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Map.class, new MapDeserializer())
                .registerTypeAdapter(Collection.class, new ArrayDeserializer())
                .disableHtmlEscaping().create();
        
        return gson.fromJson(json, Map.class);
    }
    
}
