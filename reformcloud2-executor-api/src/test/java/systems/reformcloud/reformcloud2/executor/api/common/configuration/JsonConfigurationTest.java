/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.configuration;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonConfigurationTest {

    @Test
    public void testCreate() {
        JsonConfiguration configuration = new JsonConfiguration();
        assertNotNull(configuration.getJsonObject());
    }

    @Test
    public void testSetAndGet() {
        JsonConfiguration configuration = new JsonConfiguration();

        configuration.add("test", "test");
        assertEquals("test", configuration.getString("test"));

        configuration.add("test2", 0);
        assertTrue(configuration.has("test2"));

        configuration.remove("test2");
        assertFalse(configuration.has("test2"));

        assertEquals("hello", configuration.getOrDefault("test3", "hello"));
        assertEquals("hello2", configuration.getOrDefaultIf("test4", "hello2", x -> true));
    }
}
