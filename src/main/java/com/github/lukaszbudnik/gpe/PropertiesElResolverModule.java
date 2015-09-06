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

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

import java.util.*;
import java.util.concurrent.Callable;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

public class PropertiesElResolverModule implements Module {

    public final static ExpressionFactory factory = new ExpressionFactoryImpl();
    private final List<String> propertiesNames;
    private final ELContext elContext;

    public PropertiesElResolverModule(String propertiesName) throws Exception {
        this(Arrays.asList(propertiesName));
    }

    public PropertiesElResolverModule(List<String> propertiesName) throws Exception {
        this(propertiesName, ((Callable<ELContext>) () -> {
            SimpleContext context = new SimpleContext();
            context.setVariable("env", factory.createValueExpression(System.getenv(), Map.class));
            context.setVariable("properties", factory.createValueExpression(System.getProperties(), Map.class));
            return context;
        }).call());
    }

    public PropertiesElResolverModule(List<String> propertiesNames, ELContext elContext) {
        this.propertiesNames = propertiesNames;
        this.elContext = elContext;
    }

    @Override
    public void configure(Binder binder) {
        final Binder skippedBinder = binder.skipSources(Names.class);
        propertiesNames.forEach((propertiesName) -> {
            try {
                Properties properties = new Properties();
                properties.load(getClass().getResourceAsStream(propertiesName));

                Enumeration<?> e = properties.propertyNames();
                while (e.hasMoreElements()) {
                    String propertyName = (String) e.nextElement();
                    String value = properties.getProperty(propertyName);
                    String valueUtf8 = new String(value.getBytes("ISO-8859-1"), "UTF-8");
                    ValueExpression expression = factory.createValueExpression(elContext, valueUtf8, String.class);
                    String evaluated = (String) expression.getValue(elContext);
                    skippedBinder.bind(Key.get(String.class, Names.named(propertyName))).toInstance(evaluated);
                }
            } catch (Exception e) {
                // if a properties file is not found this module will do nothing
                // later on injector will fail if named dependencies will not be resolved
                // so no panic :)
            }
        });
    }
}
