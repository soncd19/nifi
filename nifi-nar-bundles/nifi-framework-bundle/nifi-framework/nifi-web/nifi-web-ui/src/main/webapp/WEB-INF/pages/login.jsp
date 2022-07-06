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
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>CDC Login</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link rel="shortcut icon" href="images/nifi16.ico"/>
        <link rel="stylesheet" href="assets/reset.css/reset.css" type="text/css" />
        <link rel="stylesheet" href="fonts/flowfont/flowfont.css" type="text/css" />
        <link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css" type="text/css" />
        ${nf.login.style.tags}
        <link rel="stylesheet" href="js/jquery/modal/jquery.modal.css?${project.version}" type="text/css" />
        <link rel="stylesheet" href="assets/qtip2/dist/jquery.qtip.min.css?" type="text/css" />
        <link rel="stylesheet" href="assets/jquery-ui-dist/jquery-ui.min.css" type="text/css" />
        <link rel="stylesheet" href="fonts/flowfont/flowfont.css" type="text/css" />
        <link rel="stylesheet" href="assets/angular-material/angular-material.min.css" type="text/css" />
        <link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css" type="text/css" />
        <script type="text/javascript" src="assets/jquery/dist/jquery.min.js"></script>
        <script type="text/javascript" src="js/jquery/jquery.base64.js"></script>
        <script type="text/javascript" src="js/jquery/jquery.count.js"></script>
        <script type="text/javascript" src="js/jquery/jquery.center.js"></script>
        <script type="text/javascript" src="js/jquery/modal/jquery.modal.js?${project.version}"></script>
        <script type="text/javascript" src="assets/qtip2/dist/jquery.qtip.min.js"></script>
        <script type="text/javascript" src="assets/jquery-ui-dist/jquery-ui.min.js"></script>
        <script type="text/javascript" src="js/nf/nf-namespace.js?${project.version}"></script>
        <script type="text/javascript" src="assets/lodash/lodash.min.js"></script>
        <script type="text/javascript" src="assets/moment/min/moment.min.js"></script>
        ${nf.login.script.tags}

        <style type="text/css">
            div#login-container {
                width: 412px;
                box-shadow: 1px 1px 5px #e3e3e3;
                padding: 40px 20px;
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%,-50%);
            }

            .login-title {
                font-size: 30px;
                font-weight: bold;
                font-family: Roboto Slab;
                margin-bottom: 40px;
                color: #728e9b;
            }

            .setting {
                margin-bottom: 25px;
            }

            .setting-name {
                padding-bottom: 8px;
            }

            div.login-container {
                width: 100%;
                text-align: center;
            }

            body.login-body input, body.login-body textarea {
                width: 412px;
                outline: none;
                background-color: #eaeef0;
                box-shadow: none;
                border: none;
            }

            #login-submission-button{
                height: 45px;
                width: 181px;
                border-radius: 5px;
                padding: 0 8px;
                text-transform: uppercase;
                font-weight: 500;
                font-size: 11px;
                line-height: 45px;
                text-align: center;
                border: 0;
                float: none;
                display: inline-block;
                /* position: relative; */
                background: #728e9b;
                color: #fff;
                cursor: pointer;
                font-size: 20px;
                margin-top: 20px;
            }
        </style>

    </head>
    <body class="login-body">
    <div id="login-user-links-container"  style="display: none;">
        <ul id="login-user-links" class="links">
            <li id="user-logout-container">
                <span id="user-logout" class="link">log out</span>
            </li>
            <li>
                <span id="user-home" class="link">home</span>
            </li>
        </ul>
        <div class="clear"></div>
    </div>
    <div id="login-contents-container">
        <jsp:include page="/WEB-INF/partials/login/login-message.jsp"/>
        <div id="login-container" class="hidden">
            <div class="login-title">Log In</div>
            <div class="setting">
                <div class="setting-name">User</div>
                <div class="setting-field">
                    <input type="text" placeholder="user" id="username"/>
                </div>
            </div>
            <div class="setting">
                <div class="setting-name">Password</div>
                <div class="setting-field">
                    <input type="password" placeholder="password" id="password" autocomplete="off"/>
                </div>
            </div>

            <div id="login-submission-container" class="login-container hidden">
                <div id="login-submission-button" class="button">Log in</div>
            </div>
        </div>
        <jsp:include page="/WEB-INF/partials/login/login-progress.jsp"/>
    </div>
    <jsp:include page="/WEB-INF/partials/ok-dialog.jsp"/>
    </body>
</html>
