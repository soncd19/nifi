<%--
 Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<style>
    .ui-dialog {
        z-index: 10000 !important ;
    }
    .ui-button {
        width: auto;
        background: #718e9b;
        color: #ffffff;
        font-weight: bold;
        border-radius: 4px;
    }
    .card-body {
        -ms-flex: 1 1 auto;
        flex: 1 1 auto;
        padding: 1.25rem;
    }
    .form-inline .form-check-input {
        position: relative;
        -ms-flex-negative: 0;
        flex-shrink: 0;
        margin-top: 0;
        margin-right: .25rem;
        margin-left: 0;
    }
    .form-inline label {
        display: -ms-flexbox;
        display: flex;
        -ms-flex-align: center;
        align-items: center;
        -ms-flex-pack: center;
        margin-bottom: 10px;
    }
    .form-inline .form-control {
        display: inline-block;
        width: auto;
        vertical-align: middle;
        margin-left: 5px;
        margin-right: 5px;
    }
    .mr-1, .mx-1 {
        margin-right: .25rem!important;
    }
    .mt-1, .my-1 {
        margin-top: .25rem!important;
    }

    .mt-2, .my-2 {
        margin-top: .5rem!important;
    }

    .mt-3, .my-3 {
        margin-top: 1rem!important;
    }
    .ml-1, .mx-1 {
        margin-left: .25rem!important;
    }
    .form-control {
        display: block;
        width: 100%;
        height: calc(1.5em + .75rem + 2px);
        padding: .375rem .75rem;
        font-size: 1rem;
        font-weight: 400;
        line-height: 1.5;
        color: #495057;
        background-color: #fff;
        background-clip: padding-box;
        border: 1px solid #ced4da;
        border-radius: .25rem;
        transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out;
    }

    .row {
        display: -ms-flexbox;
        display: flex;
        -ms-flex-wrap: wrap;
        flex-wrap: wrap;
        margin-right: -15px;
        margin-left: -15px;
    }

    .col-2 {
        -ms-flex: 0 0 16.666667%;
        flex: 0 0 16.666667%;
        max-width: 16.666667%;
    }

</style>
<div id="processor-configuration" layout="column" class="hidden large-dialog">
    <div id="processor-configuration-status-bar"></div>
    <div class="processor-configuration-tab-container dialog-content">
        <div id="processor-configuration-tabs" class="tab-container"></div>
        <div id="processor-configuration-tabs-content">
            <div id="processor-standard-settings-tab-content" class="configuration-tab">
                <div class="settings-left">
                    <div class="setting">
                        <div class="setting-name">Name</div>
                        <div id="processor-name-container" class="setting-field">
                            <input type="text" id="processor-name" name="processor-name"/>
                            <div class="processor-enabled-container">
                                <div id="processor-enabled" class="nf-checkbox checkbox-unchecked"></div>
                                <span class="nf-checkbox-label"> Enabled</span>
                            </div>
                        </div>
                    </div>
                    <div class="setting">
                        <div class="setting-name">Id</div>
                        <div class="setting-field">
                            <span id="processor-id"></span>
                        </div>
                    </div>
                    <div class="setting">
                        <div class="setting-name">Type</div>
                        <div class="setting-field">
                            <span id="processor-type"></span>
                        </div>
                    </div>
                    <div class="setting">
                        <div class="setting-name">Bundle</div>
                        <div id="processor-bundle" class="setting-field"></div>
                    </div>
                    <div class="setting">
                        <div class="penalty-duration-setting">
                            <div class="setting-name">
                                Penalty duration
                                <div class="fa fa-question-circle" alt="Info" title="The amount of time used when this processor penalizes a FlowFile."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="penalty-duration" name="penalty-duration" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="yield-duration-setting">
                            <div class="setting-name">
                                Yield duration
                                <div class="fa fa-question-circle" alt="Info" title="When a processor yields, it will not be scheduled again until this amount of time elapses."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="yield-duration" name="yield-duration" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="setting">
                        <div class="bulletin-setting">
                            <div class="setting-name">
                                Bulletin level
                                <div class="fa fa-question-circle" alt="Info" title="The level at which this processor will generate bulletins."></div>
                            </div>
                            <div class="setting-field">
                                <div id="bulletin-level-combo"></div>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="spacer">&nbsp;</div>
                <div class="settings-right">
                    <div class="setting">
                        <div class="setting-name">
                            Automatically terminate relationships
                            <div class="fa fa-question-circle" alt="Info" title="Will automatically terminate FlowFiles sent to a given relationship if it is not defined elsewhere."></div>
                        </div>
                        <div class="setting-field">
                            <div id="auto-terminate-relationship-names"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="processor-scheduling-tab-content" class="configuration-tab">
                <div class="settings-left">
                    <div class="setting">
                        <div class="scheduling-strategy-setting">
                            <div class="setting-name">
                                Scheduling strategy
                                <div class="fa fa-question-circle" alt="Info" title="The strategy used to schedule this processor."></div>
                            </div>
                            <div class="setting-field">
                                <div type="text" id="scheduling-strategy-combo"></div>
                            </div>
                        </div>
                        <div id="event-driven-warning" class="hidden">
                            <div class="processor-configuration-warning-icon"></div>
                            This strategy is experimental
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div id="timer-driven-options" class="setting">
                        <div class="concurrently-schedulable-tasks-setting">
                            <div class="setting-name">
                                Concurrent tasks
                                <div class="fa fa-question-circle" alt="Info" title="The number of tasks that should be concurrently scheduled for this processor."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="timer-driven-concurrently-schedulable-tasks" name="timer-driven-concurrently-schedulable-tasks" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="scheduling-period-setting">
                            <div class="setting-name">
                                Run schedule
                                <div class="fa fa-question-circle" alt="Info" title="The amount of time that should elapse between task executions."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="timer-driven-scheduling-period" name="timer-driven-scheduling-period" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div id="event-driven-options" class="setting">
                        <div class="concurrently-schedulable-tasks-setting">
                            <div class="setting-name">
                                Concurrent tasks
                                <div class="fa fa-question-circle" alt="Info" title="The number of tasks that should be concurrently scheduled for this processor."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="event-driven-concurrently-schedulable-tasks" name="event-driven-concurrently-schedulable-tasks" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div id="cron-driven-options" class="setting">
                        <div class="concurrently-schedulable-tasks-setting">
                            <div class="setting-name">
                                Concurrent tasks
                                <div class="fa fa-question-circle" alt="Info" title="The number of tasks that should be concurrently scheduled for this processor."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="cron-driven-concurrently-schedulable-tasks" name="cron-driven-concurrently-schedulable-tasks" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="scheduling-period-setting">
                            <div class="setting-name">
                                Run schedule
                                <div class="fa fa-question-circle" alt="Info" title="The CRON expression that defines when this processor should run."></div>
                            </div>
                            <div class="setting-field">
                                <input type="text" id="cron-driven-scheduling-period" name="cron-driven-scheduling-period" class="small-setting-input"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div id="execution-node-options" class="setting">
                        <div class="execution-node-setting">
                            <div class="setting-name">
                                Execution
                                <div class="fa fa-question-circle" alt="Info" title="The node(s) that this processor will be scheduled to run on when clustered."></div>
                            </div>
                            <div class="setting-field">
                                <div id="execution-node-combo"></div>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="spacer">&nbsp;</div>
                <div id="run-duration-setting-container" class="settings-right">
                    <div class="setting">
                        <div class="setting-name">
                            Run duration
                            <div class="fa fa-question-circle" alt="Info"
                                 title="When scheduled to run, the processor will continue running for up to this duration. A run duration of 0ms will execute once when scheduled."></div>
                        </div>
                        <div class="setting-field" style="overflow: visible;">
                            <div id="run-duration-container">
                                <div id="run-duration-labels">
                                    <div id="run-duration-zero">0ms</div>
                                    <div id="run-duration-one">25ms</div>
                                    <div id="run-duration-two">50ms</div>
                                    <div id="run-duration-three">100ms</div>
                                    <div id="run-duration-four">250ms</div>
                                    <div id="run-duration-five">500ms</div>
                                    <div id="run-duration-six">1s</div>
                                    <div id="run-duration-seven">2s</div>
                                    <div class="clear"></div>
                                </div>
                                <div id="run-duration-slider"></div>
                                <div id="run-duration-explanation">
                                    <div id="min-run-duration-explanation">Lower latency</div>
                                    <div id="max-run-duration-explanation">Higher throughput</div>
                                    <div class="clear"></div>
                                </div>
                                <div id="run-duration-data-loss" class="hidden">
                                    <div class="processor-configuration-warning-icon"></div>
                                    Source Processors with a run duration greater than 0ms and no incoming connections could lose data when NiFi is shutdown.
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="processor-properties-tab-content" class="configuration-tab">
                <div id="processor-properties"></div>
                <div id="processor-properties-verification-results" class="verification-results">
                    <div class="verification-results-header">Verification Results</div>
                    <div id="processor-properties-verification-results-listing" class="verification-results-listing"></div>
                </div>
            </div>
            <div id="processor-relationships-tab-content" class="configuration-tab">
                <div class="settings-left">
                    <div class="setting">
                        <div class="setting-name">
                            Automatically terminate / retry relationships
                            <div class="fa fa-question-circle" alt="Info" title="Will automatically terminate and/or retry FlowFiles sent to a given relationship if it is not defined elsewhere. If both terminate and retry are selected, any retry logic will happen first, then auto-termination."></div>
                        </div>
                        <div class="setting-field">
                            <div id="auto-action-relationship-names"></div>
                        </div>
                    </div>
                </div>
                <div class="settings-right">
                    <div class="retry-count-setting setting">
                        <div class="setting-name">
                            Number of Retry Attempts
                            <div class="fa fa-question-circle" alt="Info" title="For relationships set to retry, this number indicates how many times a FlowFile will attempt to reprocess before it is routed elsewhere."></div>
                        </div>
                        <div class="setting-field">
                            <input type="text" id="retry-attempt-count" class="small-setting-input">
                        </div>
                    </div>
                    <div class="backoff-policy-setting setting">
                        <div class="setting-name">
                            Retry Back Off Policy
                            <div class="fa fa-question-circle" alt="Info" title="Penalize: Retry attempts will occur in time, but the processor will continue to process other FlowFiles.&#013;&#013;Yield: No other FlowFile processing will occur until all retry attempts have been made."></div>
                        </div>
                        <div class="setting-field">
                            <input type="radio" id="penalizeFlowFile" name="backoffPolicy" value="PENALIZE_FLOWFILE">
                            <label for="penalizeFlowFile">Penalize</label>

                            <input type="radio" class="yield-radio" id="yieldEntireProcessor" name="backoffPolicy" value="YIELD_PROCESSOR">
                            <label for="yieldEntireProcessor">Yield</label>
                        </div>
                    </div>
                    <div class="max-backoff-setting setting">
                        <div class="setting-name">
                            Retry Maximum Back Off Period
                            <div class="fa fa-question-circle" alt="Info" title="Initial retries are based on the Penalty/Yield Duration time specified in the Settings tab. The duration time is repeatedly doubled for every subsequent retry attempt. This number indicates the maximum allowable time period before another retry attempt occurs."></div>
                        </div>
                        <div class="setting-field">
                            <input type="text" id="max-backoff-period" class="small-setting-input">
                        </div>
                    </div>
                </div>
            </div>
            <div id="processor-comments-tab-content" class="configuration-tab">
                <textarea cols="30" rows="4" id="processor-comments" name="processor-comments" class="setting-input"></textarea>
            </div>
        </div>
    </div>
</div>
<div id="new-processor-property-container"></div>
<div id="dialog" title="Basic dialog">
    <div id="cronmaker"></div>
</div>


<script>
    $.fn.cronmaker = function () {
        init();

        $('#cronmaker').tabs({
            activate: function (event, ui) {
                reset();
            }
        });
        var secs, mins, hours, dayOfMonth, month, dayOfWeek, year;

        $("#btnGenCron").click(function () {
            var currTab = getCurrentTab();
            var rdoType;
            var chbType = [];
            var time;
            var txtday, txtmonth;
            var nth, selectWorkDay;

            if (currTab == "Minutes") {
                secs = 0;
                mins = "0/" + $("#txtMin").val();
                hours = "*";
                dayOfMonth = "1/1";
                month = "*";
                dayOfWeek = "?";
                year = "*";

                if (mins == "" || mins == 0) {
                    $("#lblValidation").html("Minimum minutes should be 1.");
                } else {
                    $("#lblValidation").html("");
                    printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                }
            } else if (currTab == "Hourly") {
                rdoType = $("input[name='hour']:checked").val();

                if (rdoType == "frequence") {
                    secs = 0;
                    mins = 0;
                    hours = "0/" + $("#txtEveryHours").val();
                    dayOfMonth = "1/1";
                    month = "*";
                    dayOfWeek = "?";
                    year = "*";

                    if (hours == "0/") {
                        $("#lblValidation").html("Minimum hours should be 1.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                } else { //chose time
                    secs = 0;
                    mins = $("#txtHourlyAtMinute").val();
                    hours = $("#txtHourlyAtHours").val();
                    dayOfMonth = "1/1";
                    month = "*";
                    dayOfWeek = "?";
                    year = "*";
                    printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                }
            } else if (currTab == "Daily") {
                rdoType = $("input[name='daily']:checked").val();

                if (rdoType == "frequence") {

                    secs = 0;
                    mins = $("#txtDailyAtMinute").val();
                    hours = $("#txtDailyAtHour").val();
                    dayOfMonth = "1/" + $("#txtEveryDays").val();
                    month = "*";
                    dayOfWeek = "?";
                    year = "*";

                    if (dayOfMonth == "1/") {
                        $("#lblValidation").html("Minimum days should be 1.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                } else { //chose weekday
                    secs = 0;
                    mins = $("#txtDailyAtMinute").val();
                    hours = $("#txtDailyAtHour").val();
                    dayOfMonth = "?";
                    month = "*";
                    dayOfWeek = "MON-FRI";
                    year = "*";

                    if (dayOfMonth == "1/") {
                        $("#lblValidation").html("Minimum days should be 1.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                }
            } else if (currTab == "Weekly") {
                $("input[name='weekly']:checked").each(function () {
                    chbType.push($(this).val());
                });
                secs = 0;
                mins = $("#txtWeeklyAtMinute").val();
                hours = $("#txtWeeklyAtHour").val();
                dayOfMonth = "?";
                month = "*";
                dayOfWeek = chbType.join(",");
                year = "*";

                if (dayOfWeek == "") {
                    $("#lblValidation").html("Field 'Days selection' is required.");
                } else {
                    printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                }
            } else if (currTab == "Monthly") {
                rdoType = $("input[name='monthly']:checked").val();

                if (rdoType == "day") {
                    txtday = $("#txtMonthlyDay").val();
                    txtmonth = $("#txtMonthlyMonth").val();

                    secs = 0;
                    mins = $("#txtMonthlyAtMinute").val();
                    hours = $("#txtMonthlyAtHour").val();
                    dayOfMonth = txtday;
                    month = "1/" + txtmonth;
                    dayOfWeek = "?";
                    year = "*";

                    if (txtday == "" || txtmonth == "") {
                        $("#lblValidation").html("Field 'Day' and 'every month(s)' are required.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                } else {
                    nth = $("#sddlMonthlyNth").val();
                    selectWorkDay = $("#sddlMonthlyWeekDay").val();
                    txtmonth = $("#txtMonthlyMonth2").val();

                    secs = 0;
                    mins = $("#txtMonthlyAtMinute").val();
                    hours = $("#txtMonthlyAtHour").val();
                    dayOfMonth = "?";
                    month = "1/" + txtmonth;
                    dayOfWeek = selectWorkDay + "#" + nth;
                    year = "*";

                    if (txtmonth == "") {
                        $("#lblValidation").html("Field 'every month(s)' is required.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                }
            } else if (currTab == "Yearly") {
                rdoType = $("input[name='yearly']:checked").val();

                if (rdoType == "everyDate") {
                    txtday = $("#txtYearlyDay").val();
                    txtmonth = $("#ddlYearlyMonth").val();

                    secs = 0;
                    mins = $("#txtYearlyAtMinute").val();
                    hours = $("#txtYearlyAtHour").val();
                    dayOfMonth = txtday;
                    month = txtmonth;
                    dayOfWeek = "?";
                    year = "*";

                    if (txtday == "") {
                        $("#lblValidation").html("Field 'Day' is required.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                } else {
                    nth = $("#sddlYearlyNth").val();
                    selectWorkDay = $("#sddlYearlyWeekDay").val();
                    txtmonth = $("#sddlYearlyMonth").val();

                    secs = 0;
                    mins = $("#txtYearlyAtMinute").val();
                    hours = $("#txtYearlyAtHour").val();
                    dayOfMonth = "?";
                    month = txtmonth;
                    dayOfWeek = selectWorkDay + "#" + nth;
                    year = "*";

                    if (txtmonth == "") {
                        $("#lblValidation").html("Field 'every month(s)' is required.");
                    } else {
                        printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year);
                    }
                }
            }


        });
    };

    function createLayout() {
        return "<ul><li><a href='#tabs-Minutes'>Minutes</a></li><li><a href='#tabs-Hourly'>Hourly</a></li><li><a href='#tabs-Daily'>Daily</a></li><li><a href='#tabs-Weekly'>Weekly</a></li><li><a href='#tabs-Monthly'>Monthly</a></li><li><a href='#tabs-Yearly'>Yearly</a></li></ul><div id='tabs-Minutes'></div><div id='tabs-Hourly'></div><div id='tabs-Daily'></div><div id='tabs-Weekly'></div><div id='tabs-Monthly'></div><div id='tabs-Yearly'></div>"

            + "<div style='margin-top: 50px; padding-bottom: 30px; margin-left: 10px;'><button style='display: inline-block;width: auto;position: relative; background: #718e9b; color: white;font-weight: bold;float: left;border: 1px solid;border-radius: 4px;' id='btnGenCron'>Generated Cron Expression</button><label id='lblValidation' style='color: red; display: inline-block; float:right;'></label><div style='margin-top: 10px;'><input disabled style='width: auto; margin-top: 0px; height: 28px; margin-left: 5px; border-radius: 4px;' type='text' name='txtCronExpression' id='txtCronExpression' value='' style='width:400px;' /></div></div>";
    }

    function createMinutesTab() {
        return "<div style='margin-bottom: 48px;' class='card-body' id='idb'>\n" +
            "   <div class='form-inline '>\n" +
            "      <label class='form-check-label'>\n" +
            "         Every \n" +
            "         <select id='txtMin' class='form-control' name='pnlDetails:pnlMinutes:ddMinutes'>\n" +
            "            <option selected='selected' value='1'>1</option>\n" +
            "            <option value='2'>2</option>\n" +
            "            <option value='3'>3</option>\n" +
            "            <option value='4'>4</option>\n" +
            "            <option value='5'>5</option>\n" +
            "            <option value='6'>6</option>\n" +
            "            <option value='10'>10</option>\n" +
            "            <option value='15'>15</option>\n" +
            "            <option value='20'>20</option>\n" +
            "            <option value='30'>30</option>\n" +
            "         </select>\n" +
            "         </select>minute(s)</label>\n" +
            "   </div>\n" +
            "   <div id='idd' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idf' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id10' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id11' style='display:none' data-wicket-placeholder=''></div>\n" +
            "</div>";
    }

    function createHourlyTab() {
        return "<div class='card-body' id='idb'>\n" +
            "   <div id='idc' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div class='form-inline '>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='hour' value='frequence' checked='true'>\n" +
            "         Every \n" +
            "         <select id='txtEveryHours' class='form-control ml-1 mr-1' name='pnlDetails:pnlHourly:radioGroup:ddHours'>\n" +
            "            <option selected='selected'  value='1'>1</option>\n" +
            "            <option value='2'>2</option>\n" +
            "            <option value='3'>3</option>\n" +
            "            <option value='4'>4</option>\n" +
            "            <option value='6'>6</option>\n" +
            "            <option value='12'>12</option>\n" +
            "         </select>\n" +
            "         hour(s)\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='hour' value='frequence1'>\n" +
            "         Starts at \n" +
            "         <div class='form-inline '>\n" +
            "            <select id='txtHourlyAtHours' class='form-control mr-1 ml-1' name='pnlDetails:pnlHourly'>\n" +
            "               <option value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option selected='selected' value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "            </select>\n" +
            "            :\n" +
            "            <select id='txtHourlyAtMinute' class='form-control ml-1' name='pnlDetails:pnlHourly:minutePart'>\n" +
            "               <option selected='selected' value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "               <option value='24'>24</option>\n" +
            "               <option value='25'>25</option>\n" +
            "               <option value='26'>26</option>\n" +
            "               <option value='27'>27</option>\n" +
            "               <option value='28'>28</option>\n" +
            "               <option value='29'>29</option>\n" +
            "               <option value='30'>30</option>\n" +
            "               <option value='31'>31</option>\n" +
            "               <option value='32'>32</option>\n" +
            "               <option value='33'>33</option>\n" +
            "               <option value='34'>34</option>\n" +
            "               <option value='35'>35</option>\n" +
            "               <option value='36'>36</option>\n" +
            "               <option value='37'>37</option>\n" +
            "               <option value='38'>38</option>\n" +
            "               <option value='39'>39</option>\n" +
            "               <option value='40'>40</option>\n" +
            "               <option value='41'>41</option>\n" +
            "               <option value='42'>42</option>\n" +
            "               <option value='43'>43</option>\n" +
            "               <option value='44'>44</option>\n" +
            "               <option value='45'>45</option>\n" +
            "               <option value='46'>46</option>\n" +
            "               <option value='47'>47</option>\n" +
            "               <option value='48'>48</option>\n" +
            "               <option value='49'>49</option>\n" +
            "               <option value='50'>50</option>\n" +
            "               <option value='51'>51</option>\n" +
            "               <option value='52'>52</option>\n" +
            "               <option value='53'>53</option>\n" +
            "               <option value='54'>54</option>\n" +
            "               <option value='55'>55</option>\n" +
            "               <option value='56'>56</option>\n" +
            "               <option value='57'>57</option>\n" +
            "               <option value='58'>58</option>\n" +
            "               <option value='59'>59</option>\n" +
            "            </select>\n" +
            "         </div>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idf' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id10' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id11' style='display:none' data-wicket-placeholder=''></div>\n" +
            "</div>";
    }

    function createDailyTab() {
        return "<div class='card-body' id='idb' style='margin-bottom: -29px;'>\n" +
            "   <div id='idc' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idd' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div class='form-inline '>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='daily' value='frequence' checked='true'>\n" +
            "         Every \n" +
            "         <select id='txtEveryDays' class='form-control' name='pnlDetails:pnlMinutes:ddMinutes'>\n" +
            "            <option selected='selected' value='1'>1</option>\n" +
            "            <option value='2'>2</option>\n" +
            "            <option value='3'>3</option>\n" +
            "            <option value='4'>4</option>\n" +
            "            <option value='5'>5</option>\n" +
            "            <option value='6'>6</option>\n" +
            "            <option value='7'>7</option>\n" +
            "         </select>\n" +
            "         </select>day(s)</label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-input'>\n" +
            "      <input class='form-check-input mb-1' type='radio' name='daily' value='radio1'>\n" +
            "      Every weekday</label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         Starts at \n" +
            "         <div class='form-inline '>\n" +
            "            <select id='txtDailyAtHour' class='form-control mr-1 ml-1' name='pnlDetails:pnlDaily'>\n" +
            "               <option value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option selected='selected' value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "            </select>\n" +
            "            :\n" +
            "            <select id='txtDailyAtMinute' class='form-control ml-1' name='pnlDetails:pnlDaily:minutePart'>\n" +
            "               <option selected='selected' value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "               <option value='24'>24</option>\n" +
            "               <option value='25'>25</option>\n" +
            "               <option value='26'>26</option>\n" +
            "               <option value='27'>27</option>\n" +
            "               <option value='28'>28</option>\n" +
            "               <option value='29'>29</option>\n" +
            "               <option value='30'>30</option>\n" +
            "               <option value='31'>31</option>\n" +
            "               <option value='32'>32</option>\n" +
            "               <option value='33'>33</option>\n" +
            "               <option value='34'>34</option>\n" +
            "               <option value='35'>35</option>\n" +
            "               <option value='36'>36</option>\n" +
            "               <option value='37'>37</option>\n" +
            "               <option value='38'>38</option>\n" +
            "               <option value='39'>39</option>\n" +
            "               <option value='40'>40</option>\n" +
            "               <option value='41'>41</option>\n" +
            "               <option value='42'>42</option>\n" +
            "               <option value='43'>43</option>\n" +
            "               <option value='44'>44</option>\n" +
            "               <option value='45'>45</option>\n" +
            "               <option value='46'>46</option>\n" +
            "               <option value='47'>47</option>\n" +
            "               <option value='48'>48</option>\n" +
            "               <option value='49'>49</option>\n" +
            "               <option value='50'>50</option>\n" +
            "               <option value='51'>51</option>\n" +
            "               <option value='52'>52</option>\n" +
            "               <option value='53'>53</option>\n" +
            "               <option value='54'>54</option>\n" +
            "               <option value='55'>55</option>\n" +
            "               <option value='56'>56</option>\n" +
            "               <option value='57'>57</option>\n" +
            "               <option value='58'>58</option>\n" +
            "               <option value='59'>59</option>\n" +
            "            </select>\n" +
            "         </div>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idf' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id10' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id11' style='display:none' data-wicket-placeholder=''></div>\n" +
            "</div>";
    }

    function createWeeklyTab() {
        return "<div class='card-body' id='idb'  style='margin-bottom: -28px;'>\n" +
            "   <div id='idc' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idd' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div class='row'>\n" +
            "      <div class='col-2'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input' name='weekly' value='MON'>\n" +
            "            Monday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "      <div class='col-2'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input ml-4' name='weekly' value='TUE'>\n" +
            "            Tuesday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "      <div class='col-2'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input ml-4' name='weekly' value='WED'>\n" +
            "            Wednesday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "      <div class='col-auto'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input ml-4' name='weekly' value='THU'>\n" +
            "            Thursday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "   </div>\n" +
            "   <div class='row mt-2'>\n" +
            "      <div class='col-2'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input' name='weekly' value='FRI'>\n" +
            "            Friday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "      <div class='col-2'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input ml-4' name='weekly' value='SAT'>\n" +
            "            Saturday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "      <div class='col-auto'>\n" +
            "         <div class='form-inline '>\n" +
            "            <label class='form-check-label'>\n" +
            "            <input type='checkbox' class='form-check-input ml-4' name='weekly' value='SUN'>\n" +
            "            Sunday\n" +
            "            </label>\n" +
            "         </div>\n" +
            "      </div>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         Starts at \n" +
            "         <div class='form-inline '>\n" +
            "            <select id='txtWeeklyAtHour' class='form-control mr-1 ml-1' name='pnlDetails:pnlWeekly'>\n" +
            "               <option value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option selected='selected' value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "            </select>\n" +
            "            :\n" +
            "            <select id='txtWeeklyAtMinute' class='form-control ml-1' name='pnlDetails:pnlWeekly:minutePart'>\n" +
            "               <option selected='selected' value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "               <option value='24'>24</option>\n" +
            "               <option value='25'>25</option>\n" +
            "               <option value='26'>26</option>\n" +
            "               <option value='27'>27</option>\n" +
            "               <option value='28'>28</option>\n" +
            "               <option value='29'>29</option>\n" +
            "               <option value='30'>30</option>\n" +
            "               <option value='31'>31</option>\n" +
            "               <option value='32'>32</option>\n" +
            "               <option value='33'>33</option>\n" +
            "               <option value='34'>34</option>\n" +
            "               <option value='35'>35</option>\n" +
            "               <option value='36'>36</option>\n" +
            "               <option value='37'>37</option>\n" +
            "               <option value='38'>38</option>\n" +
            "               <option value='39'>39</option>\n" +
            "               <option value='40'>40</option>\n" +
            "               <option value='41'>41</option>\n" +
            "               <option value='42'>42</option>\n" +
            "               <option value='43'>43</option>\n" +
            "               <option value='44'>44</option>\n" +
            "               <option value='45'>45</option>\n" +
            "               <option value='46'>46</option>\n" +
            "               <option value='47'>47</option>\n" +
            "               <option value='48'>48</option>\n" +
            "               <option value='49'>49</option>\n" +
            "               <option value='50'>50</option>\n" +
            "               <option value='51'>51</option>\n" +
            "               <option value='52'>52</option>\n" +
            "               <option value='53'>53</option>\n" +
            "               <option value='54'>54</option>\n" +
            "               <option value='55'>55</option>\n" +
            "               <option value='56'>56</option>\n" +
            "               <option value='57'>57</option>\n" +
            "               <option value='58'>58</option>\n" +
            "               <option value='59'>59</option>\n" +
            "            </select>\n" +
            "         </div>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div id='id10' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id11' style='display:none' data-wicket-placeholder=''></div>\n" +
            "</div>";
    }

    function createMonthlyTab() {
        return "<div class='card-body' id='idb' style='margin-bottom: -48px;'>\n" +
            "   <div id='idc' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idd' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idf' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div class='form-inline'>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='monthly' value='day'>\n" +
            "         Day\n" +
            "         <input type='text' id='txtMonthlyDay' class='form-control ml-1 mr-1' size='3' value='1' name='pnlDetails:pnlMonthly:radioGroup:txtDay'>\n" +
            "         of every\n" +
            "         <select id='txtMonthlyMonth' class='form-control ml-1 mr-1' name='pnlDetails:pnlMonthly:radioGroup:ddMonth'>\n" +
            "            <option selected='selected' value='1'>1</option>\n" +
            "            <option value='2'>2</option>\n" +
            "            <option value='3'>3</option>\n" +
            "            <option value='4'>4</option>\n" +
            "            <option value='6'>6</option>\n" +
            "         </select>\n" +
            "         month(s)\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='monthly' value='radio10' checked='checked'>\n" +
            "         The\n" +
            "         <select id='sddlMonthlyNth' class='form-control ml-1 mr-1' name='pnlDetails:pnlMonthly:radioGroup:ddRank'>\n" +
            "            <option value='1'>First</option>\n" +
            "            <option value='2'>Second</option>\n" +
            "            <option value='3'>Third</option>\n" +
            "            <option selected='selected' value='4'>Fourth</option>\n" +
            "         </select>\n" +
            "         <select id='sddlMonthlyWeekDay' class='form-control  mr-1' name='pnlDetails:pnlMonthly:radioGroup:ddDay'>\n" +
            "            <option selected='selected' value='MON'>Monday</option>\n" +
            "            <option value='TUE'>Tuesday</option>\n" +
            "            <option value='WED'>Wednesday</option>\n" +
            "            <option value='THU'>Thursday</option>\n" +
            "            <option value='FRI'>Friday</option>\n" +
            "            <option value=SAT'>Saturday</option>\n" +
            "            <option value='SUN'>Sunday</option>\n" +
            "         </select>\n" +
            "         of every\n" +
            "         <select id='txtMonthlyMonth2' class='form-control ml-1 mr-1' name='pnlDetails:pnlMonthly:radioGroup:ddMonth2'>\n" +
            "            <option selected='selected' value='1'>1</option>\n" +
            "            <option value='2'>2</option>\n" +
            "            <option value='3'>3</option>\n" +
            "            <option value='4'>4</option>\n" +
            "            <option value='6'>6</option>\n" +
            "         </select>\n" +
            "         month(s)\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         Starts at \n" +
            "         <div class='form-inline '>\n" +
            "            <select id='txtMonthlyAtHour' class='form-control mr-1 ml-1' name='pnlDetails:pnlMonthly'>\n" +
            "               <option value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option selected='selected' value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "            </select>\n" +
            "            :\n" +
            "            <select id='txtMonthlyAtMinute' class='form-control ml-1' name='pnlDetails:pnlMonthly:minutePart'>\n" +
            "               <option selected='selected' value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "               <option value='24'>24</option>\n" +
            "               <option value='25'>25</option>\n" +
            "               <option value='26'>26</option>\n" +
            "               <option value='27'>27</option>\n" +
            "               <option value='28'>28</option>\n" +
            "               <option value='29'>29</option>\n" +
            "               <option value='30'>30</option>\n" +
            "               <option value='31'>31</option>\n" +
            "               <option value='32'>32</option>\n" +
            "               <option value='33'>33</option>\n" +
            "               <option value='34'>34</option>\n" +
            "               <option value='35'>35</option>\n" +
            "               <option value='36'>36</option>\n" +
            "               <option value='37'>37</option>\n" +
            "               <option value='38'>38</option>\n" +
            "               <option value='39'>39</option>\n" +
            "               <option value='40'>40</option>\n" +
            "               <option value='41'>41</option>\n" +
            "               <option value='42'>42</option>\n" +
            "               <option value='43'>43</option>\n" +
            "               <option value='44'>44</option>\n" +
            "               <option value='45'>45</option>\n" +
            "               <option value='46'>46</option>\n" +
            "               <option value='47'>47</option>\n" +
            "               <option value='48'>48</option>\n" +
            "               <option value='49'>49</option>\n" +
            "               <option value='50'>50</option>\n" +
            "               <option value='51'>51</option>\n" +
            "               <option value='52'>52</option>\n" +
            "               <option value='53'>53</option>\n" +
            "               <option value='54'>54</option>\n" +
            "               <option value='55'>55</option>\n" +
            "               <option value='56'>56</option>\n" +
            "               <option value='57'>57</option>\n" +
            "               <option value='58'>58</option>\n" +
            "               <option value='59'>59</option>\n" +
            "            </select>\n" +
            "         </div>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div id='id11' style='display:none' data-wicket-placeholder=''></div>\n" +
            "</div>";
    }

    function createYearlyTab() {
        return "<div class='card-body' id='idb' style='margin-bottom: -64px;'>\n" +
            "   <div id='idc' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idd' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='ide' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='idf' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div id='id10' style='display:none' data-wicket-placeholder=''></div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label mr-1'>\n" +
            "      <input class='form-check-input mb-1' type='radio' name='yearly' value='everyDate' checked='true'>Every\n" +
            "      <select id='ddlYearlyMonth' class='form-control' name='pnlDetails:pnlYearly:radioGroup:ddMonth'>\n" +
            "         <option selected='selected' value='1'>January</option>\n" +
            "         <option value='2'>February</option>\n" +
            "         <option value='3'>March</option>\n" +
            "         <option value='4'>April</option>\n" +
            "         <option value='5'>May</option>\n" +
            "         <option value='6'>June</option>\n" +
            "         <option value='7'>July</option>\n" +
            "         <option value='8'>August</option>\n" +
            "         <option value='9'>September</option>\n" +
            "         <option value='10'>October</option>\n" +
            "         <option value='11'>November</option>\n" +
            "         <option value='12'>December</option>\n" +
            "      </select>\n" +
            "      <input type='text' id='txtYearlyDay' class='form-control ml-1 mr-1' size='3' value='1' name='pnlDetails:pnlYearly:radioGroup:txtDay'>\n" +
            "\t  </label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         <input class='form-check-input mb-1' type='radio' name='yearly' value='radio12'>\n" +
            "         The\n" +
            "         <select id='sddlYearlyNth' class='form-control ml-1' name='pnlDetails:pnlYearly:radioGroup:ddRank'>\n" +
            "            <option selected='selected' value='1'>First</option>\n" +
            "            <option value='2'>Second</option>\n" +
            "            <option value='3'>Third</option>\n" +
            "            <option value='4'>Fourth</option>\n" +
            "         </select>\n" +
            "         <select id='sddlYearlyWeekDay' class='form-control ml-1 mr-1' name='pnlDetails:pnlYearly:radioGroup:ddDay'>\n" +
            "            <option selected='selected' value='MON'>Monday</option>\n" +
            "            <option value='TUE'>Tuesday</option>\n" +
            "            <option value='WED'>Wednesday</option>\n" +
            "            <option value='THU'>Thursday</option>\n" +
            "            <option value='FRI'>Friday</option>\n" +
            "            <option value='SAT'>Saturday</option>\n" +
            "            <option value='SUN'>Sunday</option>\n" +
            "         </select>\n" +
            "         of\n" +
            "\t\t <select id='sddlYearlyMonth' class='form-control ml-1' name='pnlDetails:pnlYearly:radioGroup:ddMonth2'>\n" +
            "         <option selected='selected' value='1'>January</option>\n" +
            "         <option value='2'>February</option>\n" +
            "         <option value='3'>March</option>\n" +
            "         <option value='4'>April</option>\n" +
            "         <option value='5'>May</option>\n" +
            "         <option value='6'>June</option>\n" +
            "         <option value='7'>July</option>\n" +
            "         <option value='8'>August</option>\n" +
            "         <option value='9'>September</option>\n" +
            "         <option value='10'>October</option>\n" +
            "         <option value='11'>November</option>\n" +
            "         <option value='12'>December</option>\n" +
            "      </select>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "   <div class='form-inline mt-3'>\n" +
            "      <label class='form-check-label'>\n" +
            "         Starts at \n" +
            "         <div class='form-inline '>\n" +
            "            <select id='txtYearlyAtHour' class='form-control mr-1 ml-1' name='pnlDetails:pnlYearly:hourPart'>\n" +
            "               <option value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option selected='selected' value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "            </select>\n" +
            "            :\n" +
            "            <select id='txtYearlyAtMinute' class='form-control ml-1' name='pnlDetails:pnlYearly:minutePart'>\n" +
            "               <option selected='selected' value='0'>00</option>\n" +
            "               <option value='1'>01</option>\n" +
            "               <option value='2'>02</option>\n" +
            "               <option value='3'>03</option>\n" +
            "               <option value='4'>04</option>\n" +
            "               <option value='5'>05</option>\n" +
            "               <option value='6'>06</option>\n" +
            "               <option value='7'>07</option>\n" +
            "               <option value='8'>08</option>\n" +
            "               <option value='9'>09</option>\n" +
            "               <option value='10'>10</option>\n" +
            "               <option value='11'>11</option>\n" +
            "               <option value='12'>12</option>\n" +
            "               <option value='13'>13</option>\n" +
            "               <option value='14'>14</option>\n" +
            "               <option value='15'>15</option>\n" +
            "               <option value='16'>16</option>\n" +
            "               <option value='17'>17</option>\n" +
            "               <option value='18'>18</option>\n" +
            "               <option value='19'>19</option>\n" +
            "               <option value='20'>20</option>\n" +
            "               <option value='21'>21</option>\n" +
            "               <option value='22'>22</option>\n" +
            "               <option value='23'>23</option>\n" +
            "               <option value='24'>24</option>\n" +
            "               <option value='25'>25</option>\n" +
            "               <option value='26'>26</option>\n" +
            "               <option value='27'>27</option>\n" +
            "               <option value='28'>28</option>\n" +
            "               <option value='29'>29</option>\n" +
            "               <option value='30'>30</option>\n" +
            "               <option value='31'>31</option>\n" +
            "               <option value='32'>32</option>\n" +
            "               <option value='33'>33</option>\n" +
            "               <option value='34'>34</option>\n" +
            "               <option value='35'>35</option>\n" +
            "               <option value='36'>36</option>\n" +
            "               <option value='37'>37</option>\n" +
            "               <option value='38'>38</option>\n" +
            "               <option value='39'>39</option>\n" +
            "               <option value='40'>40</option>\n" +
            "               <option value='41'>41</option>\n" +
            "               <option value='42'>42</option>\n" +
            "               <option value='43'>43</option>\n" +
            "               <option value='44'>44</option>\n" +
            "               <option value='45'>45</option>\n" +
            "               <option value='46'>46</option>\n" +
            "               <option value='47'>47</option>\n" +
            "               <option value='48'>48</option>\n" +
            "               <option value='49'>49</option>\n" +
            "               <option value='50'>50</option>\n" +
            "               <option value='51'>51</option>\n" +
            "               <option value='52'>52</option>\n" +
            "               <option value='53'>53</option>\n" +
            "               <option value='54'>54</option>\n" +
            "               <option value='55'>55</option>\n" +
            "               <option value='56'>56</option>\n" +
            "               <option value='57'>57</option>\n" +
            "               <option value='58'>58</option>\n" +
            "               <option value='59'>59</option>\n" +
            "            </select>\n" +
            "         </div>\n" +
            "      </label>\n" +
            "   </div>\n" +
            "</div>";
    }

    function createWeekDropDownList(id) {
        var html;
        html = "<select id=" + id + "><option value='MON'>Monday</option><option value='TUE'>Tuesday</option><option value='WED'>Wednesday</option><option value='THU'>Thursday</option><option value='FRI'>Friday</option><option value='SAT'>Saturday</option><option value='SUN'>Sunday</option></select>";
        return html;
    }

    function createNthDropDownList(id) {
        return "<select id='" + id + "'><option value='1'>First</option><option value='2'>Second</option><option value='3'>Third</option><option value='4'>Fourth</option></select>";
    }

    function createMonthDropDownList(id) {
        return "<select id=" + id + "><option value='1'>January</option><option value='2'>February</option><option value='3'>March</option><option value='4'>April</option><option value='5'>May</option><option value='6'>June</option><option value='7'>July</option><option value='8'>August</option><option value='9'>September</option><option value='10'>October</option><option value='11'>November</option><option value='12'>December</option></select>";
    }

    function printResult(secs, mins, hours, dayOfMonth, month, dayOfWeek, year) {
        $("#lblValidation").html("");
        var generatedCron = [secs, mins, hours, dayOfMonth, month, dayOfWeek, year];
        $("#txtCronExpression").val(generatedCron.join(" "));
    }

    function init() {
        //initialize
        $("#cronmaker").html(createLayout());
        $("#tabs-Minutes").html(createMinutesTab());
        $("#tabs-Hourly").html(createHourlyTab());
        $("#tabs-Daily").html(createDailyTab());
        $("#tabs-Weekly").html(createWeeklyTab());
        $("#tabs-Monthly").html(createMonthlyTab());
        $("#tabs-Yearly").html(createYearlyTab());

        $("#divYearlyMonth2").html(createMonthDropDownList("ddlYearlyMonth2"));
        $("#divYearlyMonth").html(createMonthDropDownList("ddlYearlyMonth"));
        $("#divYearlyNth").html(createNthDropDownList("ddlYearlyNth"));
        $("#divMonthlyNth").html(createNthDropDownList("ddlMonthlyNth"));
        $("#divMonthlyWeekField").html(createWeekDropDownList("ddlMonthlyWeekDay"));
        $("#divYearlyWeekField").html(createWeekDropDownList("ddlYearlyWeekDay"));
        $("#lblValidation").html("");
    }

    function reset() {
        $("#txtCronExpression").val("");
        $(".time").timepicker();
        $(".time").timepicker('setTime', new Date());
    }

    function getCurrentTab() {
        var tabIndex = $("#cronmaker").tabs('option', 'active');
        var currentTab;

        switch (tabIndex) {
            case 0:
                currentTab = "Minutes";
                break;
            case 1:
                currentTab = "Hourly";
                break;
            case 2:
                currentTab = "Daily";
                break;
            case 3:
                currentTab = "Weekly";
                break;
            case 4:
                currentTab = "Monthly";
                break;
            case 5:
                currentTab = "Yearly";
                break;
            default:
                currentTab = "Minutes";
                break;
        }

        return currentTab;
    }

    $(function () {
        $("#cronmaker").cronmaker();
    });
    $( function() {
        $( "#dialog" ).dialog({
            title: "Scheduler",
            autoOpen: false,
            width: 800,
            show: {
                effect: "blind",
                duration: 300
            },
            hide: {
                effect: "explode",
                duration: 1000
            },
            buttons: {
                Save: function() {
                    $(this).dialog( "close" );
                    $( "#cron-driven-scheduling-period" ).val($("#txtCronExpression").val())
                }
            },
        });


        $( "#cron-driven-scheduling-period" ).on( "click", function() {
            $( "#dialog" ).dialog( "open" );
        });
    } );
</script>
