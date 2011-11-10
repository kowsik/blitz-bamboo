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
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.v2.build.BuildContext;
import io.blitz.bamboo.CurlTask;
import io.blitz.bamboo.CurlTaskConfigurator;
import io.blitz.curl.rush.IRushListener;
import io.blitz.curl.rush.Point;
import io.blitz.curl.rush.RushResult;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Listens to a rush test and have the proper method called when the event 
 * happens.
 * @author ghermeto
 */
public class RushListener extends CurlListener implements IRushListener{

    public static final String TYPE = "rush";
    
    public static final String REGION_LINE = "!! rushing from {0}";
    
    private TaskContext taskContext;
    private BuildLogger buildLogger;
    
    /**
     * Constructor
     * @param taskContext 
     */
    public RushListener(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.buildLogger = taskContext.getBuildLogger();
    }

    /**
     * Called when a successful jobStatus response is received by the client. 
     * Always returns true.
     * @param result successful rush result
     * @return true
     */
    @Override
    public boolean onStatus(RushResult result) {
        return true;
    }

    /**
     * Called when a successful jobStatus returns with a status equals 
     * 'completed'. Logs the task results and store the response on the 
     * CustomBuildData.
     * @param result successful rush result
     */
    @Override
    public void onComplete(RushResult result) {
        String region = result.getRegion();
        String regionLine = MessageFormat.format(REGION_LINE, region);
        buildLogger.addBuildLogEntry(regionLine);

        BuildContext buildContext = taskContext.getBuildContext();
        Map<String, String> data = buildContext
                .getBuildResult().getCustomBuildData();

        //add to the custom build data, so we can use on the xwork plugin
        data.put(CurlTask.RESULT_KEY, toJson(result));
        
        final ConfigurationMap config = taskContext.getConfigurationMap();
        String percentStr = config.get(CurlTaskConfigurator.PERCENTAGE);
        double percentage = Double.parseDouble(percentStr);
        
        //gets the last point
        List<Point> timeline = (List<Point>)result.getTimeline();
        int errors = timeline.get(timeline.size()-1).getErrors();
        int timeouts = timeline.get(timeline.size()-1).getTimeouts();
        int total = timeline.get(timeline.size()-1).getTotal();
        
        double errorPercentage = ((errors + timeouts)*100)/total;
        
        if(errorPercentage > percentage) {
            throw new RuntimeException("Too many errors.");
        }
    }
}