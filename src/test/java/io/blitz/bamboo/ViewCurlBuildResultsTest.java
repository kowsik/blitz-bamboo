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
package io.blitz.bamboo;

import com.atlassian.bamboo.resultsummary.ResultsSummary;
import io.blitz.bamboo.listener.RushListener;
import io.blitz.bamboo.listener.SprintListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ghermeto
 */
public class ViewCurlBuildResultsTest {
    
    ResultsSummary sprintResult;
    ResultsSummary rushResult;
    ResultsSummary emptyResult;
    ViewCurlBuildResults view;

    @Before
    public void setup() {
        sprintResult = mock(ResultsSummary.class);
        Map<String, String> sprintMap = new HashMap<String, String>();
        sprintMap.put(CurlTask.TYPE_KEY, SprintListener.TYPE);
        String sprint = "{\"region\":\"california\",\"duration\":10,"
                + "\"steps\":[{\"duration\":10,\"connect\":1,"
                + "\"request\":{\"line\":\"GET / HTTP/1.1\",\"method\":\"GET\","
                + "\"url\":\"http://localhost:9295\",\"headers\":{\"a\":\"b\"},"
                + "\"content\":\"abc\"},\"response\":{\"line\":\"GET / HTTP/1.1\","
                + "\"message\":\"message\",\"status\":200,"
                + "\"headers\":{\"c\":\"d\"},\"content\":\"abd\"}}]}";
        sprintMap.put(CurlTask.RESULT_KEY, sprint);
        when(sprintResult.getCustomBuildData()).thenReturn(sprintMap);
        
        rushResult = mock(ResultsSummary.class);
        Map<String, String> rushMap = new HashMap<String, String>();
        rushMap.put(CurlTask.TYPE_KEY, RushListener.TYPE);
        String rush = "{\"region\":\"california\",\"timeline\":["
                + "{\"duration\":0.1,\"total\":10,\"executed\":8,\"errors\":1,"
                + "\"timeouts\":1,\"volume\":10},"
                + "{\"duration\":0.2,\"total\":100,\"executed\":80,\"errors\":10,"
                + "\"timeouts\":10,\"volume\":100}"
                + "]}";
        rushMap.put(CurlTask.RESULT_KEY, rush);
        String command = "-p 1-10:20 -r ireland http://example.com";
        rushMap.put(CurlTask.COMMAND_KEY, command);
        when(rushResult.getCustomBuildData()).thenReturn(rushMap);
        
        emptyResult = mock(ResultsSummary.class);
        Map<String, String> emptyMap = new HashMap<String, String>();
        sprintMap.put(CurlTask.TYPE_KEY, SprintListener.TYPE);
        when(emptyResult.getCustomBuildData()).thenReturn(emptyMap);

        view = new ViewCurlBuildResults();
    }
    
    @Test
    public void sprintResult() {
        view.populateResult(sprintResult);
        assertNotNull(view.getResult());
        assertEquals("california", view.getResult().get("region"));
        assertEquals(10, view.getResult().get("duration"));
        assertNull(view.getJsonPattern());
    }
    
    @Test
    public void rushResult() {
        view.populateResult(rushResult);
        assertNotNull(view.getResult());
        assertEquals("california", view.getResult().get("region"));
        assertEquals(2, ((Collection)view.getResult().get("timeline")).size());
        assertNotNull(view.getJsonPattern());
        String json = "{\"interactions\":1,\"intervals\":[{\"interactions\":1,"
                + "\"start\":1,\"end\":10,\"duration\":20}]}";
        assertEquals(json, view.getJsonPattern());
    }
    
    @Test
    public void emptySprintResult() {
        view.populateResult(emptyResult);
        assertNull(view.getResult());
        assertNull(view.getJsonPattern());
    }
}
