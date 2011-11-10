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

import com.atlassian.bamboo.build.Buildable;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.List;
import java.util.Map;

/**
 * Selects if the blitz tab will be displayed on the job results page. Looks 
 * for a blitz-curl task in the job and if found, displays the tab.
 * @author ghermeto
 */
public class CurlResultWebItemCondition implements Condition {

    public static final String BUILD_KEY = "buildKey";

    private PlanManager planManager;

    /**
     * Initializes the condition.
     * @param map
     * @throws PluginParseException 
     */
    @Override
    public void init(Map<String, String> map) throws PluginParseException {
    }

    /**
     * Used to select if the web-item will displayed to the user or not.
     * Returns true if the build has a blitz-curl task. Returns false otherwise.
     * @param context Condition context
     * @return true if build has a blitz-curl task
     */
    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        String buildKey = (String) context.get(BUILD_KEY);
        if (buildKey == null) {
            return false;
        }
        Buildable build = planManager.getPlanByKeyIfOfType(buildKey, Buildable.class);
        if (build != null) {
            // get the list of tasks in the build
            List<TaskDefinition> tasks = build
                    .getBuildDefinition().getTaskDefinitions();
            for(TaskDefinition task : tasks) {
                //if there is a blitz-curl task
                if(task.getPluginKey().equals(CurlTask.KEY) ) {
                    
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Injects the planManager
     * @param planManager 
     */
    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

}