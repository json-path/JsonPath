/*
 * Copyright 2003-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A template which generates an HTML report from the bincompat XML report
 */
modelTypes = {
    String title
    String baseline
    String archive
    Map<String,Map<String,List<String>>> violations
}

def severityMapping = [
        error  : 'danger',
        warning: 'warning',
        info   : 'info',
        ignore : 'success'
]

yieldUnescaped '<!DOCTYPE html>'


html {
    head {
        meta 'charset': "utf-8"
        meta 'http-equiv': "content-type", content: "text/html; charset=utf-8"
        meta 'http-equiv': "X-UA-Compatible", content: "IE=edge"
        meta name: "viewport", content: "width=device-width, initial-scale=1"

        title(title)
        link href: "http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css", rel: "stylesheet"
        link href: "http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css", rel: "stylesheet"
        link href: "http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css", rel: "stylesheet"
    }

    body {
        div(class:'navbar navbar-inverse navbar-fixed-top', role:'navigation') {
            div(class:'container') {
                div(class:'navbar-header') {
                    button(type:'button', class:'navbar-toggle', 'data-toggle':'collapse', 'data-target':'navbar-collaspe') {
                        span(class:'sr-only', 'Toggle navigation')
                        span(class:'icon-bar'){}
                        span(class:'icon-bar'){}
                        span(class:'icon-bar'){}
                    }
                    a(class:'navbar-brand',href:'#', 'Binary compatibility report')
                }
                div(class:'navbar-collapse collapse') {
                    ul(class:"nav navbar-nav") {
                        li(class: 'dropdown') {
                            a(id: 'severityDropdown', href: '#', class: 'dropdown-toggle', 'data-toggle': 'dropdown', 'Severity <span class="caret"></span>')
                            ul(class: "dropdown-menu dropdown-severity", role: "menu") {
                                li(role: 'presentation', class: 'active') {
                                    a(role: 'menuitem', tabindex: '-1', href: '#', 'All levels')
                                }
                                li(role: 'presentation') { a(role: 'menuitem', tabindex: '-1', href: '#', 'Error') }
                                li(role: 'presentation') { a(role: 'menuitem', tabindex: '-1', href: '#', 'Warning') }
                                li(role: 'presentation') { a(role: 'menuitem', tabindex: '-1', href: '#', 'Info') }
                            }
                        }

                    }
                }
            }
        }


        div(class: 'container') {
            div(class:'jumbotron') {
                div(class:'container') {
                    div(class: 'page-header') {
                        h1 'Binary compatibility'
                        p "Comparing ${archive} to reference ${baseline}"
                        p {
                            yield "Be warned that this report is not perfect and depends on what "
                            a(href: 'https://github.com/siom79/japicmp', 'JApicmp')
                            yield " is capable to detect."
                        }
                    }
                }
            }
            violations.each { fqcn, classViolations ->
                def errors = classViolations.keySet()
                def severities = errors.collect { "severity-${it}" }
                div(class: "panel panel-default ${severities.join(' ')}") {
                    div(class: "panel-heading") {
                        h3(class: 'panel-title', "Class $fqcn")
                    }
                    div(class: 'panel-body') {
                        table(class: "table table-striped table-bordered") {
                            tbody {
                                classViolations.each { err, list ->
                                    list.each { item ->
                                        tr(class: "bincompat-error severity-${err}") {
                                            td {
                                                h4 {
                                                    span(class: "label label-${severityMapping[err]}", err.capitalize())
                                                }
                                            }
                                            td { span(item) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            script(src: "http://code.jquery.com/jquery-1.11.0.min.js") {}
            script(src: "http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js") {}
            script {
                yieldUnescaped '''
$(document).ready(function () {
        var severity = null;
        doFilter();
        function doFilter() {
          var severityClass = "severity-" + severity;
          $('.panel').hide();
          $('.bincompat-error').hide();
          $('.bincompat-error').filter(function () {
              return (severity==null || $(this).hasClass(severityClass));
          }).show();
          $('.panel').filter(function () {
              return (severity==null || $(this).hasClass(severityClass));
          }).show();
        }
        $(".dropdown-severity li a").click(function() {
          severity = $(this).text().toLowerCase();
          if (severity==="all levels") {
            severity = null;
          }
          doFilter();
        });
});'''
            }
        }
    }
}