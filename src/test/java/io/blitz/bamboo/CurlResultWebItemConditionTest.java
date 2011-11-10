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

import com.atlassian.bamboo.build.BuildDefinition;
import com.atlassian.bamboo.build.Buildable;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.plan.PlanManagerImpl;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.task.TaskDefinitionImpl;
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
public class CurlResultWebItemConditionTest {

    PlanManager planManager;
    CurlResultWebItemCondition condition;
    
    @Before
    public void setup() {
        planManager = mock(PlanManagerImpl.class);

        Buildable buildableTrue = mock(Buildable.class);
        BuildDefinition buildDefinitionTrue = mock(BuildDefinition.class);
        TaskDefinition tTrue = new TaskDefinitionImpl(1, CurlTask.KEY, "", null);
        List<TaskDefinition> listTrue = new ArrayList<TaskDefinition>();
        listTrue.add(tTrue);
        
        when(buildDefinitionTrue.getTaskDefinitions()).thenReturn(listTrue);
        when(buildableTrue.getBuildDefinition()).thenReturn(buildDefinitionTrue);
        when(planManager.getPlanByKeyIfOfType("TRUE", Buildable.class)).thenReturn(buildableTrue);

        Buildable buildableFalse = mock(Buildable.class);
        BuildDefinition buildDefinitionFalse = mock(BuildDefinition.class);
        List<TaskDefinition> listFalse = new ArrayList<TaskDefinition>();

        when(buildDefinitionFalse.getTaskDefinitions()).thenReturn(listFalse);
        when(buildableFalse.getBuildDefinition()).thenReturn(buildDefinitionFalse);
        when(planManager.getPlanByKeyIfOfType("FALSE", Buildable.class)).thenReturn(buildableFalse);

        condition = new CurlResultWebItemCondition();
        condition.setPlanManager(planManager);
    }
    
    @Test
    public void buildHasBlitzTask() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CurlResultWebItemCondition.BUILD_KEY, "TRUE");
        assertTrue(condition.shouldDisplay(map));
    }
    
    @Test
    public void buildHasNoTasks() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CurlResultWebItemCondition.BUILD_KEY, "FALSE");
        assertFalse(condition.shouldDisplay(map));
    }
}
