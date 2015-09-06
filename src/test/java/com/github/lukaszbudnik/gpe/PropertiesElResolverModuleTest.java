/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gpe;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.assertEquals;

public class PropertiesElResolverModuleTest {

    @Test
    public void testConfigureDefault() throws Exception {
        Injector injector = Guice.createInjector(new PropertiesElResolverModule("/test.properties"));

        TestModel testModel = injector.getInstance(TestModel.class);

        assertEquals(1234, testModel.getTestInt());
        assertEquals(1234l, testModel.getTestLong());
        assertEquals(12.34, testModel.getTestDouble(), 0.01);
        assertEquals("jak się masz?", testModel.getTestString());
        assertEquals(System.getenv("HOME"), testModel.getTestEnvHome());
        assertEquals(System.getProperty("java.version"), testModel.getTestPropertiesJavaVersion());

        if (System.getenv().containsKey("TRAVIS")) {
            assertEquals("on travis", testModel.getTestEval());
        } else {
            assertEquals("locally", testModel.getTestEval());
        }
    }

    @Test
    public void testConfigureCustom() throws Exception {
        SimpleContext context = new SimpleContext();
        ExpressionFactory factory = new ExpressionFactoryImpl();
        Map<String, String> settings = ImmutableMap.of("user", "admin");
        ValueExpression valueExpression = factory.createValueExpression(settings, Map.class);
        context.setVariable("settings", valueExpression);

        Injector injector = Guice.createInjector(new PropertiesElResolverModule(
                Arrays.asList("/test_custom_el_context.properties"),
                context));

        CustomElContextTestModel testModel = injector.getInstance(CustomElContextTestModel.class);

        assertEquals("admin", testModel.getUser());
    }

    public static class TestModel {

        @Inject
        @Named("test.int")
        private int testInt;

        @Inject
        @Named("test.long")
        private long testLong;

        @Inject
        @Named("test.double")
        private double testDouble;

        @Inject
        @Named("test.string")
        private String testString;

        @Inject
        @Named("test.env.home")
        private String testEnvHome;

        @Inject
        @Named("test.properties.javaVersion")
        private String testPropertiesJavaVersion;

        @Inject
        @Named("test.eval")
        private String testEval;

        public int getTestInt() {
            return testInt;
        }

        public void setTestInt(int testInt) {
            this.testInt = testInt;
        }

        public long getTestLong() {
            return testLong;
        }

        public void setTestLong(long testLong) {
            this.testLong = testLong;
        }

        public double getTestDouble() {
            return testDouble;
        }

        public void setTestDouble(double testDouble) {
            this.testDouble = testDouble;
        }

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
        }

        public String getTestEnvHome() {
            return testEnvHome;
        }

        public void setTestEnvHome(String testEnvHome) {
            this.testEnvHome = testEnvHome;
        }

        public String getTestPropertiesJavaVersion() {
            return testPropertiesJavaVersion;
        }

        public void setTestPropertiesJavaVersion(String testPropertiesJavaVersion) {
            this.testPropertiesJavaVersion = testPropertiesJavaVersion;
        }

        public String getTestEval() {
            return testEval;
        }

        public void setTestEval(String testEval) {
            this.testEval = testEval;
        }
    }

    public static class CustomElContextTestModel {

        @Inject
        @Named("settings.user")
        private String user;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
