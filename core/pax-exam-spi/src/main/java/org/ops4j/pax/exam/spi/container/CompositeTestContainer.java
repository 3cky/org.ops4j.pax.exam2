package org.ops4j.pax.exam.spi.container;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionDescription;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.TimeoutException;

public class CompositeTestContainer implements TestContainer
{

    final private List<TestContainer> m_targets;

    public CompositeTestContainer( List<TestContainer> mTargets )
    {
        m_targets = mTargets;
    }

    public OptionDescription getOptionDescription()
    {
        final List<Option> used = new ArrayList<Option>();
        final List<Option> unused = new ArrayList<Option>();

        for( TestContainer container : m_targets )
        {
            OptionDescription od = container.getOptionDescription();
            used.addAll( Arrays.asList( od.getUsedOptions() ) );
            unused.addAll( Arrays.asList( od.getIgnoredOptions() ) );
        }
        return new OptionDescription()
        {

            public Option[] getUsedOptions()
            {
                return used.toArray( new Option[used.size()] );
            }

            public Option[] getIgnoredOptions()
            {
                return unused.toArray( new Option[unused.size()] );
            }
        };
    }

    public void setBundleStartLevel( long bundleId, int startLevel )
        throws TestContainerException
    {
        for( TestContainer container : m_targets )
        {
            container.setBundleStartLevel( bundleId, startLevel );
        }
    }

    public TestContainer start()
        throws TimeoutException
    {
        for( TestContainer container : m_targets )
        {
            container.start();
        }
        return this;
    }

    public TestContainer stop()
        throws TimeoutException
    {
        for( TestContainer container : m_targets )
        {
            container.stop();
        }
        return this;
    }

    public void waitForState( long bundleId, int state, long timeoutInMillis )
        throws TimeoutException
    {
        for( TestContainer container : m_targets )
        {
            container.waitForState( bundleId, state, timeoutInMillis );
        }

    }

    public void cleanup()
    {
        for( TestContainer container : m_targets )
        {
            container.cleanup();
        }
    }

    public <T> T getService( Class<T> serviceType, String filter, long timeoutInMillis )
        throws TestContainerException
    {
        final List<T> services = new ArrayList<T>();
        for( TestContainer container : m_targets )
        {
            services.add( container.getService( serviceType, filter, timeoutInMillis ) );
        }
        // proxy:
        return (T) Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[]{ serviceType }, new InvocationHandler()
        {

            public Object invoke( Object proxy, Method method, Object[] args )
                throws Throwable
            {
                List<Object> results = new ArrayList<Object>();
                for( T t : services )
                {
                    // actually invoke exactly that method on the service<t>
                    results.add( method.invoke( t, args ) );
                    // then also proxy the result

                }
                // users need to cast it back to List<Object> to retrieve individual results.
                return results;
            }

        }
        );
    }

    public long install( InputStream stream )
    {
        // need to cache:

        long id = 0;
        for( TestContainer container : m_targets )
        {
            id = container.install( stream );
        }
        return id;
    }

}