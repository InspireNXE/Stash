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
package org.inspirenxe.stash;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.inspirenxe.stash.nodes.CommentedDefaultNode;
import org.inspirenxe.stash.nodes.DefaultNode;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Stash {

    private ConfigurationLoader<? extends ConfigurationNode> loader;
    private ConfigurationNode rootNode;
    private final File configuration;
    private final List<DefaultNode> defaultNodes = Lists.newArrayList();
    private final Logger logger;

    public Stash(Logger logger, File configuration, ConfigurationLoader<? extends ConfigurationNode> loader) {
        this.logger = logger;
        this.loader = loader;
        this.configuration = configuration;
    }

    /**
     * Initializes the configuration file. File is created if non-existent.
     * @return {@link Stash} for chaining.
     */
    public Stash init() {
        if (!configuration.exists()) {
            try {
                configuration.createNewFile();
            } catch (IOException e) {
                logger.error("Unable to create new configuration file!", e);
            }
        }
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            logger.error("Unable to load configuration file!", e);
        }
        return this;
    }

    /**
     * Loads the configuration file.
     * @return {@link Stash} for chaining.
     */
    public Stash load() {
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            logger.error("Unable to load configuration!", e);
        }
        return this;
    }

    /**
     * Saves the configuration file.
     * @return {@link Stash} for chaining.
     */
    @SuppressWarnings("unchecked")
    public Stash save() {
        defaultNodes.stream().filter(entry -> entry.value != null).forEach(entry -> {
            final ConfigurationNode node = getChildNode(entry.key);
            if (node.getValue() == null) {
                if (entry.type.isPresent()) {
                    try {
                        getChildNode(entry.key).setValue(TypeToken.of((Class<Object>) entry.type.get()), entry.value);
                        if (entry instanceof CommentedDefaultNode && loader instanceof HoconConfigurationLoader) {
                            ((CommentedConfigurationNode) this.getChildNode(entry.key)).setComment(((CommentedDefaultNode) entry).comment);
                        }
                    } catch (ObjectMappingException e) {
                        logger.warn("Unable to map TypeToken!", e);
                    }
                } else {
                    if (entry instanceof CommentedDefaultNode && loader instanceof HoconConfigurationLoader) {
                        ((CommentedConfigurationNode) this.getChildNode(entry.key)).setComment(((CommentedDefaultNode) entry).comment);
                    }
                }
            }
        });
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            logger.error("Unable to save configuration!", e);
        }
        return this;
    }

    /**
     * Registers a default node. Calls {@link Stash#save()} and {@link Stash#load()}.
     * @param node The {@link DefaultNode} to register.
     */
    @SuppressWarnings("unchecked")
    public void registerDefaultNode(DefaultNode node) {
        final String[] nodes = node.key.split("\\.");
        final List<String> currentPath = Lists.newArrayList();
        for (int i = 0; i < nodes.length; i++) {
            if (i < nodes.length - 1) {
                currentPath.add(i, nodes[i]);
                final String joinedPath = Joiner.on(",").skipNulls().join(currentPath).replace(",", ".");
                defaultNodes.add(new DefaultNode.Builder()
                        .key(joinedPath)
                        .type(node.type)
                        .build()
                );
            } else {
                defaultNodes.add(node);
            }
        }
    }

    /**
     * Gets the node from the root node.
     * @param path The path to the node split by periods.
     * @return The {@link ConfigurationNode}.
     */
    public ConfigurationNode getChildNode(String path) {
        return rootNode.getNode((Object[]) path.split("\\."));
    }

    /**
     * Gets the value of a child node as an object.
     * @param path The path of the value to get.
     * @return The object value.
     */
    public Object getChildNodeValue(String path) {
        return getChildNode(path).getValue();
    }

    /**
     * Gets the value of a child node as a generic type.
     * @param path The path of the value to get.
     * @param clazz The type of class to use.
     * @return The value as the clazz.
     */
    @SuppressWarnings("unchecked")
    public <T> T getChildNodeValue(String path, Class<T> clazz) {
        try {
            return getChildNode(path).getValue(TypeToken.of(clazz));
        } catch (ObjectMappingException e) {
            logger.error("Unable to map object to TypeToken", e);
        }
        // Return a value even if the token doesn't successfully map
        return (T) getChildNode(path).getValue();
    }
}