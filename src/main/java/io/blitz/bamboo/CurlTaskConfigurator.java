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

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import io.blitz.command.Curl;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Configures the curl task form and visualization.
 * @author ghermeto
 */
public class CurlTaskConfigurator extends AbstractTaskConfigurator {

    public static final String USERNAME = "username";
    public static final String API_KEY = "apiKey";
    public static final String COMMAND = "command";
    public static final String PERCENTAGE = "percentage";

    private TextProvider textProvider;

    /**
     * Creates the config map, that will be used to give to the task access to 
     * the configuration options.
     * @param params
     * @param previousTaskDefinition
     * @return config map
     */
    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(
            @NotNull final ActionParametersMap params,
            @Nullable final TaskDefinition previousTaskDefinition) {

        final Map<String, String> config = super
                .generateTaskConfigMap(params, previousTaskDefinition);
        config.put(USERNAME, params.getString(USERNAME));
        config.put(API_KEY, params.getString(API_KEY));
        config.put(COMMAND, params.getString(COMMAND));
        config.put(PERCENTAGE, params.getString(PERCENTAGE));
        return config;
    }

    /**
     * Used to set the data that will pre-populate the task config form during 
     * creation. Added the default values here.
     * @param context 
     */
    @Override
    public void populateContextForCreate(
            @NotNull final Map<String, Object> context) {

        super.populateContextForCreate(context);
        context.put(USERNAME, "");
        context.put(API_KEY, "");
        context.put(COMMAND, "-p 1-10:10 -r california http://example.com");
        context.put(PERCENTAGE, "0");
    }

    /**
     * Used to populate the config task from during edition.
     * @param context
     * @param taskDefinition 
     */
    @Override
    public void populateContextForEdit(
            @NotNull final Map<String, Object> context,
            @NotNull final TaskDefinition taskDefinition) {

        super.populateContextForEdit(context, taskDefinition);
        context.put(USERNAME, taskDefinition.getConfiguration().get(USERNAME));
        context.put(API_KEY, taskDefinition.getConfiguration().get(API_KEY));
        context.put(COMMAND, taskDefinition.getConfiguration().get(COMMAND));
        context.put(PERCENTAGE, taskDefinition.getConfiguration().get(PERCENTAGE));
    }

    /**
     * Used to display the config task options.
     * @param context
     * @param taskDefinition 
     */
    @Override
    public void populateContextForView(
            @NotNull final Map<String, Object> context,
            @NotNull final TaskDefinition taskDefinition) {

        super.populateContextForView(context, taskDefinition);
        context.put(USERNAME, taskDefinition.getConfiguration().get(USERNAME));
        context.put(API_KEY, taskDefinition.getConfiguration().get(API_KEY));
        context.put(COMMAND, taskDefinition.getConfiguration().get(COMMAND));
        context.put(PERCENTAGE, taskDefinition.getConfiguration().get(PERCENTAGE));
    }

    /**
     * Used for the task form field validation.
     * @param params
     * @param errorCollection 
     */
    @Override
    public void validate(
            @NotNull final ActionParametersMap params,
            @NotNull final ErrorCollection errorCollection) {

        super.validate(params, errorCollection);
        //validates the curl command line using the blitz-java curl parser
        final String command = params.getString(COMMAND);
        try {
            Curl.parse(null, null, command);
        } catch (IllegalArgumentException e) {
            String message = textProvider
                    .getText("io.blitz.command.error", "Error parsing command");
            errorCollection.addError(COMMAND, message + ": " + e.getMessage());
        }
        //validates username required
        final String username = params.getString(USERNAME);
        if (StringUtils.isEmpty(username)) {
            String message = textProvider
                    .getText("io.blitz.username.error", "Username is required");
            errorCollection.addError(USERNAME, message);
        }
        //validates api-key required
        final String apiKey = params.getString(API_KEY);
        if (StringUtils.isEmpty(apiKey)) {
            String message = textProvider
                    .getText("io.blitz.apiKey.error", "API-Key is required");
            errorCollection.addError(API_KEY, message);
        }
        //validates percentage between 0 and 100
        final String percentage = params.getString(PERCENTAGE);
        Double percent = null;
        try {
            percent = Double.parseDouble(percentage);
        }
        catch(NumberFormatException e) {}
        if (percent == null || percent < 0.0 || percent > 100.0) {
            String defaultMessage = "Must be a number between 0 and 100";
            String message = textProvider
                    .getText("io.blitz.percentage.error", defaultMessage);
            errorCollection.addError(PERCENTAGE, message);
        }
    }

    /**
     * Inject the text provider
     * @param textProvider 
     */
    public void setTextProvider(final TextProvider textProvider) {
        this.textProvider = textProvider;
    }
}
