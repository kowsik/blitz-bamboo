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
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.configuration.ConfigurationMapImpl;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.v2.build.BuildContext;
import com.atlassian.bamboo.v2.build.CurrentBuildResult;
import io.blitz.bamboo.CurlTask;
import io.blitz.bamboo.CurlTaskConfigurator;
import io.blitz.curl.rush.Point;
import io.blitz.curl.rush.RushResult;
import io.blitz.curl.rush.Step;
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
public class RushListenerTest {

    TaskContext taskContext;
    Map<String, String> data;
    ConfigurationMap map;
    RushResult rushResult;
    RushListener rushListener;
    
    @Before
    public void setup() {
        taskContext = mock(TaskContext.class);
        BuildLogger buildLogger = mock(BuildLogger.class);
        BuildContext buildContext = mock(BuildContext.class);
        CurrentBuildResult result = mock(CurrentBuildResult.class);
        map = new ConfigurationMapImpl();
        data = new HashMap<String, String>();
        
        when(result.getCustomBuildData()).thenReturn(data);
        when(buildContext.getBuildResult()).thenReturn(result);
        when(taskContext.getBuildContext()).thenReturn(buildContext);
        when(taskContext.getBuildLogger()).thenReturn(buildLogger);
        when(taskContext.getConfigurationMap()).thenReturn(map);
        
        List<Point> points = new ArrayList<Point>();
        List<Step> steps = new ArrayList<Step>();
        Step step = new Step(0.32, 0.1, 2, 2, 0);
        steps.add(step);
        Point p = new Point(0.5, 0.32, 10, 6, 2, 2, 100, 100, 100, steps);
        points.add(p);
        rushResult = new RushResult("california", points);
        
        rushListener = new RushListener(taskContext);
    }
    
    @Test
    public void completeSuccessfully() {
        map.put(CurlTaskConfigurator.PERCENTAGE, "40");
        rushListener.onComplete(rushResult);
        assertTrue(data.containsKey(CurlTask.RESULT_KEY));
        String result = "{\"region\":\"california\",\"timeline\":["
                + "{\"timestamp\":0.5,\"duration\":0.32,\"total\":10,\"hits\":6,"
                + "\"errors\":2,\"timeouts\":2,\"volume\":100,\"txBytes\":100,"
                + "\"rxBytes\":100,\"steps\":[{\"duration\":0.32,\"connect\":0.1,"
                + "\"errors\":2,\"timeouts\":2,\"asserts\":0}]}]}";
        assertEquals(result, data.get(CurlTask.RESULT_KEY));
    }
    
    @Test
    public void tooManyErrors() {
        map.put(CurlTaskConfigurator.PERCENTAGE, "39");
        boolean errorThrown = false;
        try {
            rushListener.onComplete(rushResult);
        }
        catch(RuntimeException e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);
    }
}
