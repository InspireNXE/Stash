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

import java.util.Optional;

public class CommentedDefaultNode<T> extends DefaultNode<T> {

    public String comment;

    public CommentedDefaultNode(String key, T value, Optional<Class<T>> type, String comment) {
        super(key, value, type);
        this.comment = comment;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public static class Builder<T> extends DefaultNode.Builder<T> {

        private String comment = null;

        public Builder() {
            super();
        }

        /**
         * Sets the type for the {@link DefaultNode}.
         * @param clazz The class to use for {@link com.google.common.reflect.TypeToken} mapping.
         */
        public Builder(Class<T> clazz) {
            super(clazz);
        }

        @Override
        public Builder<T> key(String key) {
            return (Builder<T>) super.key(key);
        }

        @Override
        public Builder<T> value(T value) {
            return (Builder<T>) super.value(value);
        }

        /**
         * Sets the comment for the {@link CommentedDefaultNode}
         * @param comment The comment to register.
         * @return The builder.
         */
        public Builder<T> comment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Build the {@link DefaultNode}.
         * @return A new copy of {@link DefaultNode}.
         */
        @Override
        public CommentedDefaultNode<T> build() {
            return new CommentedDefaultNode<>(super.key, super.value, super.type, comment);
        }
    }
}
