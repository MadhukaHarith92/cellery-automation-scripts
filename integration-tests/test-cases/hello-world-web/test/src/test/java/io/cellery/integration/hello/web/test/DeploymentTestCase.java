/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package io.cellery.integration.hello.web.test;

import io.cellery.integration.base.test.BaseTestCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static io.github.bonigarcia.wdm.DriverManagerType.CHROME;

public class DeploymentTestCase extends BaseTestCase {
    private static final String orgName = "wso2cellerytest";
    private static final String imageName = "hello-world-web";
    private static final String version = "1.0.0";
    private static final String helloWorldInstance = "hello-world-inst";
    private static final String defaultURL = "http://hello-world.com";
    private static final String webPageContent = "hello cellery";
    private WebDriver webDriver;

    @BeforeClass
    public void setup() {
        WebDriverManager.getInstance(CHROME).setup();
        webDriver = new ChromeDriver(new ChromeOptions().setHeadless(true));
    }

    @Test
    public void build() throws Exception {
        build("hello-world.bal", orgName, imageName, version,
                Paths.get(getCelleryTestRoot(), "hello-world-web", "src").toFile().getAbsolutePath());
    }

    @Test
    public void run() throws Exception {
        run(orgName, imageName, version, helloWorldInstance, 120);
    }

    @Test(dependsOnMethods = "run")
    public void invoke() {
        webDriver.get(defaultURL);
        validateWebPage();
    }

    @Test(dependsOnMethods = "run")
    public void terminate() throws Exception {
        terminateCell(helloWorldInstance);
    }

    @Test(dependsOnMethods = "terminate")
    @ExpectedExceptions(Exception.class)
    public void repeatTerminate() throws Exception {
        terminateCell(helloWorldInstance);
    }

    private void validateWebPage() {
        String searchHeader = webDriver.findElement(By.cssSelector("H1")).getText().toLowerCase();
        Assert.assertEquals(searchHeader, webPageContent, "Web page is content is not as expected");
    }

    @AfterClass
    public void cleanup() {
        webDriver.close();
        try {
            terminateCell(helloWorldInstance);
        } catch (Exception ignored) {
        }
    }
}