/*
 * Copyright 2011 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.inject.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.exam.util.Injector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    public void start( BundleContext context ) throws Exception
    {
        Injector injector = new ServiceInjector();
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put( "type", "default" );
        context.registerService( Injector.class.getName(), injector, props );
    }

    public void stop( BundleContext context ) throws Exception
    {
        // empty
    }
}
