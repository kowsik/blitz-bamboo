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
package io.blitz.bamboo.listener;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import io.blitz.bamboo.CurlTask;
import io.blitz.curl.sprint.Request;
import io.blitz.curl.sprint.Response;
import io.blitz.curl.sprint.SprintResult;
import io.blitz.curl.sprint.Step;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ghermeto
 */
public class SprintListenerTest {

    TaskContext taskContext;
    Map<String, String> data;
    SprintResult sprintResult;
    SprintListener sprintListener;
    
    @Before
    public void setup() {
        taskContext = mock(TaskContext.class);
        BuildLogger buildLogger = mock(BuildLogger.class);
        BuildContext buildContext = mock(BuildContext.class);
        CurrentBuildResult result = mock(CurrentBuildResult.class);
        data = new HashMap<String, String>();
        
        when(result.getCustomBuildData()).thenReturn(data);
        when(buildContext.getBuildResult()).thenReturn(result);
        when(taskContext.getBuildContext()).thenReturn(buildContext);
        when(taskContext.getBuildLogger()).thenReturn(buildLogger);
        
        List<Step> steps = new ArrayList<Step>();
        Request req = new Request("GET /", "GET", 
                "http://example.com", new HashMap<String, Object>(), "");
        Response res = new Response("GET /", 200, 
                "OK", new HashMap<String, Object>(), "");
        Step step = new Step(0.32, 0.1, req, res);
        steps.add(step);
        sprintResult = new SprintResult("california", 0.32, steps);
        
        sprintListener = new SprintListener(taskContext);
    }
    
    @Test
    public void completeSuccessfully() {
        sprintListener.onComplete(sprintResult);
        assertTrue(data.containsKey(CurlTask.RESULT_KEY));
        String result = "{\"region\":\"california\",\"duration\":0.32,"
                + "\"steps\":[{\"duration\":0.32,\"connect\":0.1,"
                + "\"request\":{\"line\":\"GET /\",\"method\":\"GET\","
                + "\"url\":\"http://example.com\",\"headers\":{},\"content\":\"\"},"
                + "\"response\":{\"line\":\"GET /\",\"status\":200,"
                + "\"message\":\"OK\",\"headers\":{},\"content\":\"\"}}]}";
        assertEquals(result, data.get(CurlTask.RESULT_KEY));
    }
}
