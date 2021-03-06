/*
 * Copyright 2009,2010 Toni Menzel.
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
package org.ops4j.pax.exam;

/**
 * Test pointer to an executable test.
 * TestAddresses are graph based, so they have a parent and 0-many childs.
 *
 * @author Toni Menzel
 * @since Jan 11, 2010
 */
public interface TestAddress {

    /**
     * Identifier of a single addressable test.
     *
     * @return identifier that is associated with this address. Basically its the persistent representation.
     */
    public String identifier();

    /**
     * @return A human readable name of that test address
     */
    public String caption();

    /**
     * Test Addresses are built from other test adresses, so they build a compound tree.
     * This returns the root of the tree.
     * Sub-Adresses (so, TestAddresses that have a root()) refer usually to one indentical test deployed on differnt test containers.
     *
     * @return root of this address (where this was built from).
     */
    public TestAddress root();

    /**
     * Test Addresses can have default argument values.
     *
     * @return the arguments passed to this address by default.
     */
    public Object[] arguments();


}
