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

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.atlassian.bamboo.v2.build.BuildContext;
import io.blitz.bamboo.listener.RushListener;
import io.blitz.bamboo.listener.SprintListener;
import io.blitz.command.Curl;
import io.blitz.curl.AbstractTest;
import io.blitz.curl.IListener;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Bamboo task. Will pass if no exception is thrown during the blitz curl test.
 * Stores the blitz result data in CustomBuildData to be used by xWork for 
 * display.
 * @author ghermeto
 */
public class CurlTask implements TaskType {
    
    public static final String KEY = "io.blitz.bamboo-plugin:curl";
    
    public static final String TYPE_KEY = "BLITZ_CURL_TYPE";
    public static final String ERROR_KEY = "BLITZ_CURL_ERROR";
    public static final String RESULT_KEY = "BLITZ_CURL_RESULT";
    public static final String COMMAND_KEY = "BLITZ_CURL_COMMAND";
    
    /**
     * Executes the task. Uses blitz-java to run the tests.
     * @param taskContext
     * @return the outcome for the task
     * @throws TaskException 
     */
    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) 
            throws TaskException { 
        
        final ConfigurationMap config = taskContext.getConfigurationMap();
        final TaskResultBuilder builder = TaskResultBuilder.create(taskContext);
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        
        //gets the configuration
        String username =  config.get(CurlTaskConfigurator.USERNAME);
        String apiKey = config.get(CurlTaskConfigurator.API_KEY);
        String command = config.get(CurlTaskConfigurator.COMMAND);
        
        BuildContext buildContext = taskContext.getBuildContext();
        Map<String, String> data = buildContext
                .getBuildResult().getCustomBuildData();

        //adds the command to the custom build data
        data.put(COMMAND_KEY, command);

        // creates the proper listener
        IListener listener; 
        if(command.matches(Curl.RE_PATTERN)) {
            listener = new RushListener(taskContext);
            data.put(TYPE_KEY, RushListener.TYPE);
        }
        else {
            listener = new SprintListener(taskContext);
            data.put(TYPE_KEY, SprintListener.TYPE);
        }
        
        //runs the test by the command
        try {
            AbstractTest test = Curl.parse(username, apiKey, command);
            test.addListener(listener);
            test.execute();
            //if we got here thge test was successful
            builder.success();
            buildLogger.addBuildLogEntry("Blitz test finished successfully.");
        }
        catch(Exception e) {
            String message = "Test failed with message: " + e.getMessage();
            buildLogger.addErrorLogEntry(message);
            data.put(ERROR_KEY, message);
            //failed
            builder.failed();
        }
        
        return builder.build();
    }
}