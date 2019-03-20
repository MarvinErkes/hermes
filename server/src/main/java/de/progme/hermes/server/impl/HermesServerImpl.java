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

import de.progme.hermes.server.HermesServer;
import de.progme.hermes.server.filter.FilteredRequest;
import de.progme.hermes.server.filter.RequestFilter;
import de.progme.hermes.server.http.ConnectionHandler;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Consumes;
import de.progme.hermes.server.http.annotation.FormParam;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.DELETE;
import de.progme.hermes.server.http.annotation.method.PATCH;
import de.progme.hermes.server.http.annotation.method.POST;
import de.progme.hermes.server.http.annotation.method.PUT;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.RequestMethod;
import de.progme.hermes.shared.Status;
import de.progme.hermes.shared.http.Response;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
public class HermesServerImpl implements HermesServer {

    private boolean running;

    private HermesConfig hermesConfig;

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    private Map<String, ResourceInfo> resourceInfo = new HashMap<>();

    private List<RequestFilter> filters = new ArrayList<>();

    public HermesServerImpl() {

    }

    public HermesServerImpl(HermesConfig hermesConfig) {

        this.hermesConfig = hermesConfig;
        this.executorService = new ThreadPoolExecutor(hermesConfig.corePoolSize, hermesConfig.maxPoolSize, hermesConfig.threadPoolTimeout, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        initConfig();
    }

    private void initConfig() {

        filters.clear();
        resourceInfo.clear();

        hermesConfig.classes.forEach(this::scanClass);

        for (Class<? extends RequestFilter> filter : hermesConfig.filters) {
            try {
                filters.add(filter.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scanClass(Class<?> clazz) {

        ResourceInfo resourceInfo = null;
        try {
            resourceInfo = new ResourceInfo(clazz.newInstance(), clazz.getAnnotation(Path.class).value());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resourceInfo == null) {
            return;
        }

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Path.class)/* && method.getParameterCount() == 1*/) {
                if (method.getParameterTypes()[0].isAssignableFrom(Request.class)) {
                    RequestMethod requestMethod = RequestMethod.GET;
                    if (method.isAnnotationPresent(POST.class)) {
                        requestMethod = RequestMethod.POST;
                    } else if (method.isAnnotationPresent(DELETE.class)) {
                        requestMethod = RequestMethod.DELETE;
                    } else if (method.isAnnotationPresent(PUT.class)) {
                        requestMethod = RequestMethod.PUT;
                    } else if (method.isAnnotationPresent(PATCH.class)) {
                        requestMethod = RequestMethod.PATCH;
                    }

                    ResourceInfo.Entry resEntry;

                    String path = method.getAnnotation(Path.class).value();
                    String copyPath = path;

                    // If path parameters are present, remove them to get the root path
                    if (path.contains("{")) {
                        path = path.substring(0, path.indexOf("{") - 1);
                    }

                    resourceInfo.add(path, (method.isAnnotationPresent(Produces.class)) ? resEntry = new ResourceInfo.Entry(method, method.getAnnotation(Produces.class).value().type(), (method.isAnnotationPresent(Consumes.class)) ? method.getAnnotation(Consumes.class).value().type() : ContentType.ALL.type(), requestMethod) : (resEntry = new ResourceInfo.Entry(method, requestMethod)));

                    if (method.getParameterCount() > 1) {
                        for (Parameter parameter : method.getParameters()) {
                            if (parameter.isAnnotationPresent(FormParam.class)) {
                                String postKey = parameter.getAnnotation(FormParam.class).value();
                                resEntry.addPostKey(postKey);
                            }
                        }
                    }

                    // Getting path variables
                    String[] splitted = copyPath.split("/");
                    List<String> rawParams = new ArrayList<>();

                    for (String p : splitted) {
                        if (!p.isEmpty()) {
                            if (p.startsWith("{") && p.endsWith("}")) {
                                p = p.replaceAll("\\{|\\}", "");
                                rawParams.add(p);
                            }
                        }
                    }

                    if (rawParams.size() > 0) {
                        if (rawParams.size() != method.getParameterCount() - 1) {
                            throw new IllegalArgumentException("the path parameter count is not equal with the method parameter count of method " + method.getName());
                        }

                        rawParams.forEach(resEntry::addPathKey);
                    }
                }
            }
        }

        this.resourceInfo.put(resourceInfo.rootPath(), resourceInfo);
    }

    @Override
    public void start() {

        if (running) {
            throw new IllegalStateException("server is already running");
        }

        if (hermesConfig == null) {
            throw new IllegalStateException("config cannot be null");
        }

        try {
            serverSocket = new ServerSocket(hermesConfig.port, hermesConfig.backLog, InetAddress.getByName(hermesConfig.host));

            running = true;

            executorService.execute(new HermesServerThread());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

        if (!running) {
            throw new IllegalStateException("server is already stopped");
        }

        running = false;

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignore) {

            }
        }

        executorService.shutdown();
    }

    @Override
    public void config(HermesConfig hermesConfig) {

        this.hermesConfig = hermesConfig;

        initConfig();
    }

    public void filter(FilteredRequest filteredRequest) {

        for (RequestFilter filter : filters) {
            filter.filter(filteredRequest);
        }
    }

    public Response handleRequest(Request request) {

        for (Map.Entry<String, ResourceInfo> entry : resourceInfo.entrySet()) {
            if (request.location().startsWith(entry.getKey())) {

                return entry.getValue().execute(request.location().replace(entry.getKey(), ""), request);
            }
        }

        return Response.status(Status.NOT_FOUND).build();
    }

    private class HermesServerThread implements Runnable {

        @Override
        public void run() {

            while (running) {
                try {
                    executorService.execute(new ConnectionHandler(serverSocket.accept(), HermesServerImpl.this));
                } catch (IOException ignore) {
                    break;
                }
            }

            // Stop the server if it's still running
            if (running) {
                stop();
            }
        }
    }
}
