/*
 * Copyright 2010 John Hopper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jps.util;

/**
 *
 * @author zinic
 */
public final class Join {

    private Join() {
    }

    public static final String strings(Object... elements) {
        final StringBuilder buffer = new StringBuilder();

        for (Object nextString : elements) {
            buffer.append(nextString);
        }

        return buffer.toString();
    }
}
