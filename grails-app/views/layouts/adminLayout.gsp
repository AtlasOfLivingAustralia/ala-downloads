%{--
  - ﻿Copyright (C) 2013 Atlas of Living Australia
  - All Rights Reserved.
  -
  - The contents of this file are subject to the Mozilla Public
  - License Version 1.1 (the "License"); you may not use this file
  - except in compliance with the License. You may obtain a copy of
  - the License at http://www.mozilla.org/MPL/
  -
  - Software distributed under the License is distributed on an "AS
  - IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  - implied. See the License for the specific language governing
  - rights and limitations under the License.
  --}%

<g:applyLayout name="main">
    <head>
        <style type="text/css">
        .nav-pills > li.active > a {
            color: #555;
            background-color: white;
        }

        .nav-pills>li.active>a:hover {
            color: #333;
            background-color: #f2f2f2;
        }
        .nav-stacked > li > a {
            border: 1px solid #ddd;
        }
        </style>
    </head>

    <body>

    <r:require module="bootstrap"/>

    <div class="container-fluid">
        <legend>
            <table style="width: 100%">
                <tr>
                    <td>Administration<dl:navSeperator/><g:pageProperty name="page.pageTitle"/></td>
                    <td style="text-align: right"><span><g:pageProperty name="page.adminButtonBar"/></span></td>
                </tr>
            </table>
        </legend>

        <div class="row">
            <div class="col-md-3">
                <ul class="nav nav-pills nav-stacked">
                    <dl:breadcrumbItem href="${createLink(controller: 'admin', action: 'projectList')}" title="Projects" />
                    <dl:breadcrumbItem href="${createLink(controller: 'download', action: 'list')}" title="Record Downloads" />
                </ul>
            </div>

            <div class="col-md-9">
                <g:if test="${flash.errorMessage}">
                    <div class="container-fluid">
                        <div class="alert alert-error">
                            ${flash.errorMessage}
                        </div>
                    </div>
                </g:if>

                <g:if test="${flash.message}">
                    <div class="container-fluid">
                        <div class="alert alert-info">
                            ${flash.message}
                        </div>
                    </div>
                </g:if>

                <g:layoutBody/>

            </div>
        </div>
    </div>
    </body>
</g:applyLayout>