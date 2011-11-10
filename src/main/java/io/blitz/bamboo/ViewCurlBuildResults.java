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

import com.atlassian.bamboo.build.ViewBuildResults;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import io.blitz.bamboo.util.JsonConverter;
import io.blitz.command.Curl;
import io.blitz.curl.AbstractTest;
import io.blitz.curl.Rush;
import io.blitz.curl.config.Pattern;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * Used as a view controller, makes the blitz curl test result and pattern 
 * available from the view template.
 * @author ghermeto
 */
public class ViewCurlBuildResults extends ViewBuildResults {

    public static final String RUSH_REPORT_KEY = "io.blitz.bamboo-plugin:rush";
    
    private Map<String, Object> result;
    private String jsonPattern;
    
    /**
     * Called before render the result view.
     * @return the action result that will be rendered
     * @throws Exception 
     */
    @Override
    public String execute() throws Exception {
        String superResult = super.doExecute();

        if (ERROR.equals(superResult)) {
            return ERROR;
        }
        populateResult(this.getResultsSummary());
        return superResult;
    }
    
    /**
     * Gets the JSON result from CustomBuildData map deserialize it and stoes it
     * on result. If the test is a rush, parse the command and serialize the 
     * pattern as JSON.
     * @param summary summary of the test result
     */
    protected void populateResult(ResultsSummary summary) {
        Map<String, String> data = summary.getCustomBuildData();
        String json = data.get(CurlTask.RESULT_KEY);
        if(!StringUtils.isEmpty(json)) {
            result = JsonConverter.fromJson(json);
            String type = data.get(CurlTask.TYPE_KEY);
            if("rush".equals(type)) {
                String command = data.get(CurlTask.COMMAND_KEY);
                AbstractTest test = Curl.parse(null, null, command);
                Pattern pattern = ((Rush)test).getPattern();
                jsonPattern = JsonConverter.toJson(pattern);
            }
        }
    }
    
    /**
     * Getter for the deserialized blitz curl test result
     * @return deserialized blitz curl test result
     */
    public Map<String, Object> getResult() {
        return result;
    }
    
    /**
     * Getter for the serialized pattern
     * @return serialized pattern
     */
    public String getJsonPattern() {
        return jsonPattern;
    }
}