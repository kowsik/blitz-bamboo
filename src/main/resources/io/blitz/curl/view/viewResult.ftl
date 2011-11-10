<html>
    <head>
        <title> [@ui.header pageKey='Blitz' object='${build.name} ${buildResults.buildNumber}' title=true /]</title>
        <meta name="tab" content="blitz-curl"/>
        ${webResourceManager.requireResource("io.blitz.bamboo-plugin:rush-dependencies")}
        ${webResourceManager.requiredResources}
        </style>
    </head>

    <body>
        [#assign customDataMap=buildResults.buildResultsSummary.customBuildData /]
        <div class="section">
            <img src="/bamboo/download/resources/io.blitz.bamboo-plugin/images/icon_small.png" style="float:left;margin:7px 5px 0 0;"/>
            <h1>${customDataMap.BLITZ_CURL_TYPE?cap_first} Results</h1>
            [#if customDataMap.BLITZ_CURL_ERROR?exists]
                <div class="rush-error">
                    <span class="icon icon-Failed"></span>
                    <strong>${customDataMap.BLITZ_CURL_ERROR}</strong>
                </div>
            [/#if]
            [#if customDataMap.BLITZ_CURL_TYPE == "sprint" && result?exists ]
                <div><strong>Region:</strong> ${result.region}</div>
                <div><strong>Duarion:</strong> ${result.duration} seconds</div>
                [#list result.steps as step]
                    <br/>
                    <h2>${step.request.line}</h2>
                    <div><strong>Duarion:</strong> ${step.duration} seconds</div>
                    <div><strong>Connect:</strong> ${step.connect} seconds</div>
                    <div><strong>Response:</strong> ${step.response.line}</div>
                    <br/>
                    <div><strong>Request headers</strong></div>
                    <ul>
                        <li><strong>Host:</strong> ${step.request.headers.Host}</li>
                        <li><strong>X-User-IP:</strong> ${step.request.headers["X-User-IP"]}</li>
                        <li><strong>X-User-ID:</strong> ${step.request.headers["X-User-ID"]}</li>
                        <li><strong>User-Agent:</strong> ${step.request.headers["User-Agent"]}</li>
                    </ul>
                    <br/>
                    <div><strong>Response headers</strong></div>
                    <ul>
                        <li><strong>Content-Type:</strong> ${step.response.headers["Content-Type"]}</li>
                        <li><strong>Content-Length:</strong> ${step.response.headers["Content-Length"]}</li>
                        <li><strong>Connection:</strong> ${step.response.headers.Connection}</li>
                    </ul>
                [/#list]
            [#elseif customDataMap.BLITZ_CURL_TYPE == "rush" && customDataMap.BLITZ_CURL_RESULT?exists]
                <div id="rush-results"></div>
                <script type="text/javascript">
                    var t = '${customDataMap.BLITZ_CURL_RESULT}';
                    var p = '${jsonPattern}';
                    blitz.curl.test(t, p).render(jQuery('#rush-results'));
                </script>
            [/#if]
        </div>
    </body>
</html>
