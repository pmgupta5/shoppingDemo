package com.lti.productservice;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com.lti.productservice")
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue= {"stepDefinitions"},
        plugin = {"pretty",
                "html:target/report/cucumber.html",
                "json:target/report/cucumber.json",
                "junit:target/report/Cucumber.xml"
        }
       )
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value="junit:target/cucumber-reports/Cucumber.xml," +
                " json:target/cucumber-reports/Cucumber.json, " +
                "html:target/cucumber-reports/Cucumber.html, " +
                "timeline:target/cucumber-reports/CucumberTimeline")
public class CucumberIT {

}