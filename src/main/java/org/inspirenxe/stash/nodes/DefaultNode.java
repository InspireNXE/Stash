/*
 * This file is part of Stash, licensed under the MIT License (MIT).
 *
 * Copyright (c) InspireNXE <http://github.com/InspireNXE>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.inspirenxe.stash.nodes;

import com.google.common.reflect.TypeToken;

import java.util.Optional;

public class DefaultNode<T> {

    public String key;
    public T value;
    public final Optional<Class<T>> type;

    public DefaultNode(String key, T value, Optional<Class<T>> type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>();
    }

    public static class Builder<T> {

        protected String key = "";
        protected T value = null;
        protected Optional<Class<T>> type = Optional.empty();

        /**
         * Sets the path key for the {@link DefaultNode}.
         * @param key The path to register.
         * <p>The key is split by a period. For example "path.to.node" is the equivalent of...
         * path {
         *     to {
         *         node=""
         *     }
         * }</p>
         * @return The builder.
         */
        public Builder<T> key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the value for the {@link DefaultNode}.
         * @param value The value to register.
         * @return The builder.
         */
        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }

        /**
         * Sets the type for the {@link DefaultNode}.
         * @param type The class to use for {@link TypeToken} mapping.
         * @return The builder.
         */
        public Builder<T> type(Optional<Class<T>> type) {
            this.type = type;
            return this;
        }

        /**
         * Build the {@link DefaultNode}.
         * @return A new copy of {@link DefaultNode}.
         */
        public DefaultNode<T> build() {
            return new DefaultNode<>(key, value, type);
        }
    }
}