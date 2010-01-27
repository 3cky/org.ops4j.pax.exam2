/*
 * Copyright 2008 Toni Menzel
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.rbc.internal;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.ops4j.pax.exam.rbc.Constants;
import org.ops4j.pax.swissbox.core.ContextClassLoaderUtils;

/**
 * Registers the an instance of RemoteTestRunnerService as RMI service using a port set by system property
 * pax.exam.communication.port.
 *
 * @author Toni Menzel (tonit)
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since Jun 2, 2008
 */
public class Activator
    implements BundleActivator
{

    /**
     * JCL logger.
     */
    private static final Log LOG = LogFactory.getLog( Activator.class );

    /**
     * RMI registry.
     */
    private Registry m_registry;
    /**
     * Strong reference to {@link RemoteBundleContext}.
     * !Note: this must be here otherwise JVM will garbage collect it and this will result in an
     * java.rmi.NoSuchObjectException: no such object in table
     */
    private RemoteBundleContext m_remoteBundleContext;

    /**
     * {@inheritDoc}
     */
    public void start( final BundleContext bundleContext )
        throws Exception
    {
        //!! Absolutely necessary for RMIClassLoading to work
        ContextClassLoaderUtils.doWithClassLoader(
            null, // getClass().getClassLoader()
            new Callable<Object>()
            {
                public Object call()
                    throws Exception
                {
                    try
                    {
                        // try to find port from property
                        int port = getPort();
                        LOG.debug( "Starting up RMI registry on port [" + port + "]" );
                        m_registry = LocateRegistry.createRegistry( port );
                        LOG.debug( "Binding " + RemoteBundleContext.class.getSimpleName() + " to RMI registry" );
                        m_registry.bind(
                            RemoteBundleContext.class.getName(),
                            UnicastRemoteObject.exportObject(
                                m_remoteBundleContext = new RemoteBundleContextImpl( bundleContext ),
                                port
                            )
                        );
                        LOG.info( "RMI registry started on port [" + port + "]" );
                    }
                    catch( Exception e )
                    {
                        throw new BundleException( "Cannot setup RMI registry", e );
                    }
                    return null;
                }
            }
        );
    }

    /**
     * {@inheritDoc}
     */
    public void stop( BundleContext bundleContext )
        throws Exception
    {
        LOG.debug( "Unbinding " + RemoteBundleContext.class.getSimpleName() );
        m_registry.unbind( RemoteBundleContext.class.getName() );
        UnicastRemoteObject.unexportObject( m_remoteBundleContext, true );
        UnicastRemoteObject.unexportObject( m_registry, true );
        m_registry = null;
        m_remoteBundleContext = null;
        // this is necessary, unfortunately.. RMI wouldn' stop otherwise
        System.gc();
        LOG.info( "RMI registry stopped" );
    }

    /**
     * @return the port where {@link RemoteBundleContext} is being exposed as an RMI service.
     *
     * @throws BundleException - If communication port cannot be determined
     */
    private int getPort()
        throws BundleException
    {
        // The port is usually given by starting client (owner of this process).
        try
        {
            return Integer.parseInt( System.getProperty( Constants.RMI_PORT_PROPERTY ) );
        }
        catch( NumberFormatException e )
        {
            throw new BundleException(
                "Cannot determine rmi registry port. Ensure that property "
                + Constants.RMI_PORT_PROPERTY
                + " is set to a valid Integer."
            );
        }
    }
}
