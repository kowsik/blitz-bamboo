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
import io.blitz.bamboo.CurlTask;
import io.blitz.curl.sprint.ISprintListener;
import io.blitz.curl.sprint.SprintResult;
import io.blitz.curl.sprint.Step;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Listens to a sprint test and have the proper method called when the event 
 * happens.
 * @author ghermeto
 */
public class SprintListener extends CurlListener implements ISprintListener {

    public static final String TYPE = "sprint";
    
    public static final String REGION_LINE = "!! sprinting from {0}";
    public static final String LINE_1 = "> {0} {1}";
    public static final String LINE_2 = "< {0} {1} in {2} seconds";
    
    private TaskContext taskContext;
    private BuildLogger buildLogger;
    
    /**
     * Constructor
     * @param taskContext 
     */
    public SprintListener(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    /**
     * Called when a successful jobStatus response is received by the client. 
     * Always returns true.
     * @param result a successful sprint result
     * @return true
     */
    @Override
    public boolean onStatus(SprintResult result) {
        return true;
    }

    /**
     * Called when a successful jobStatus returns with a status equals 
     * 'completed'. Logs the task results and store the response on the 
     * CustomBuildData.
     * @param result a successful sprint result
     */
    @Override
    public void onComplete(SprintResult result) {
        String region = result.getRegion();
        String regionLine = MessageFormat.format(REGION_LINE, region);
        buildLogger.addBuildLogEntry(regionLine);

        BuildContext buildContext = taskContext.getBuildContext();
        Map<String, String> data = buildContext
                .getBuildResult().getCustomBuildData();

        //add to the custom build data, so we can use on the xwork plugin
        data.put(CurlTask.RESULT_KEY, toJson(result));
        
        Collection<Step> steps = result.getSteps();
        for(Step step : steps) {
            //get the data we need for the step
            String url = step.getRequest().getUrl();
            String method = step.getRequest().getMethod();
            String message = step.getResponse().getMessage();
            Integer status = step.getResponse().getStatus();
            Double duration = step.getDuration();
            
            //log the sprint response
            String line1 = MessageFormat.format(LINE_1, method, url);
            buildLogger.addBuildLogEntry(line1);
            String line2 = MessageFormat.format(LINE_2, status, message, duration);
            buildLogger.addBuildLogEntry(line2);
        }
    }
}
