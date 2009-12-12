/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.ops4j.pax.exam.options;

import static org.ops4j.lang.NullArgumentException.*;
import org.ops4j.pax.exam.Option;

/**
 * Options specifying a boot delegation package (package for which framework will delegate to the system classloader).
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 0.3.0, December 08, 2008
 */
public class BootDelegationOption
    implements Option
{

    /**
     * Boot delegation package (cannot be null or empty).
     */
    private final String m_package;

    /**
     * Constructor
     *
     * @param pkg boot delegation package (cannot be null or empty)
     *
     * @throws IllegalArgumentException - If package is null or empty
     */
    public BootDelegationOption( final String pkg )
    {
        validateNotEmpty( pkg, true, "Package" );
        m_package = pkg;
    }

    /**
     * Getter.
     *
     * @return boot delegation package (cannot be null or empty)
     */
    public String getPackage()
    {
        return m_package;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "BootDelegationOption" );
        sb.append( "{package='" ).append( m_package ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }

}