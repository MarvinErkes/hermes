/*
 * Copyright (c) 2017 "Marvin Erkes"
 *
 * This file is part of Hermes.
 *
 * Hermes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.progme.hermes.server.impl;

import de.progme.hermes.server.filter.RequestFilter;
import de.progme.hermes.server.http.annotation.Path;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
public abstract class HermesConfig {

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    protected String host;

    protected int port;

    protected int backLog;

    protected int corePoolSize = 2;

    protected int maxPoolSize = 2;

    protected int threadPoolTimeout = 60;

    protected final List<Class<?>> classes = new ArrayList<>();

    protected final List<Class<? extends RequestFilter>> filters = new ArrayList<>();

    public void host(String host) {

        if (host == null)
            throw new IllegalArgumentException("host cannot be null");

        this.host = host;
    }

    public void port(int port) {

        if (port < 0)
            throw new IllegalArgumentException("port cannot be negative");

        this.port = port;
    }

    public void backLog(int backLog) {

        this.backLog = backLog;
    }

    public void corePoolSize(int corePoolSize) {

        if(corePoolSize < 2) {
            throw new IllegalArgumentException("corePoolSize needs to be at least 2");
        }

        this.corePoolSize = corePoolSize;
    }

    public void maxPoolSize(int maxPoolSize) {

        if(maxPoolSize < 2) {
            throw new IllegalArgumentException("maxPoolSize needs to be at least 2");
        }

        this.maxPoolSize = maxPoolSize;
    }

    public void threadPoolTimeout(int threadPoolTimeout) {

        if (threadPoolTimeout < 1) {
            throw new IllegalArgumentException("threadPoolTimeout needs to be at least 1");
        }

        this.threadPoolTimeout = threadPoolTimeout;
    }

    public void filter(Class<? extends RequestFilter> filter) {

        if (filter == null) {
            throw new IllegalArgumentException("filter cannot be null");
        }

        this.filters.add(filter);
    }

    public void register(Class<?> clazz) {

        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null");
        }

        if (!clazz.isAnnotationPresent(Path.class)) {
            throw new IllegalArgumentException("class " + clazz.getName() + " needs a Path annotation");
        }

        if (classes.contains(clazz)) {
            throw new IllegalArgumentException("class " + clazz.getName() + " is already registered");
        }

        classes.add(clazz);
    }

    public void register(String packageName) {

        scanPackage(packageName).forEach(this::register);
    }

    private List<Class<?>> scanPackage(String packageName) {

        String scannedPath = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL url = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (url == null) {
            throw new IllegalArgumentException("package " + packageName + " does not exist");
        }

        File scannedDir = new File(url.getFile());
        List<Class<?>> classes = new ArrayList<>();

        File[] files = scannedDir.listFiles();
        if (files == null) {
            throw new IllegalStateException();
        }

        for (File file : files) {
            classes.addAll(scanSubPackages(file, packageName));
        }

        return classes;
    }

    private List<Class<?>> scanSubPackages(File file, String scannedPackage) {

        List<Class<?>> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new IllegalStateException();
            }

            for (File child : files) {
                classes.addAll(scanSubPackages(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }

        return classes;
    }
}
