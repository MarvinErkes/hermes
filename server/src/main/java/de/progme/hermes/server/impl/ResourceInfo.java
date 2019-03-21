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

import de.progme.hermes.server.http.Request;
import de.progme.hermes.shared.RequestMethod;
import de.progme.hermes.shared.Status;
import de.progme.hermes.shared.http.Response;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
public class ResourceInfo {

    private Object object;

    private String rootPath;

    private Map<String, Entry> methods = new HashMap<>();

    public ResourceInfo(Object object, String rootPath) {

        this.object = object;
        this.rootPath = rootPath;
    }

    public Response execute(String path, Request httpRequest) {

        Map.Entry<String, Entry> matchedEntry = null;

        // Search the right entry from the path requested because the path can have path parameters
        for (Map.Entry<String, Entry> entry : methods.entrySet()) {
            if (path.startsWith(entry.getKey()) && httpRequest.method() == entry.getValue().requestMethod()) {
                matchedEntry = entry;
            }
        }

        if(matchedEntry == null) return process("", null, null);

        return process(matchedEntry.getKey(), matchedEntry.getValue(), httpRequest);
    }

    private Response process(String mainPath, Entry entry, Request httpRequest) {

        if (entry != null) {
            if (!entry.acceptContentType.equals("*/*") && !entry.acceptContentType.equals(httpRequest.header("Content-Type"))) {
                return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
            }

            if (!entry.requestMethod.equals(httpRequest.method())) {
                return Response.status(Status.METHOD_NOT_ALLOWED).build();
            }

            try {
                // TODO: Better implementation
                Object[] objects = {httpRequest};

                if (httpRequest.method() == RequestMethod.POST || httpRequest.method() == RequestMethod.PUT) {
                    objects = new Object[entry.postParameters.size() + 1];
                    objects[0] = httpRequest;
                    int i = 1;
                    for (String parameter : entry.postParameters) {
                        objects[i] = httpRequest.post(parameter);
                        i++;
                    }
                }

                // Are there any path params?
                if (entry.pathParameters.size() > 0) {
                    objects = new Object[entry.pathParameters.size() + 1];
                    objects[0] = httpRequest;

                    // Get all params from the location
                    String paramString = httpRequest.location().substring(httpRequest.location().indexOf(rootPath + mainPath) + rootPath.length() + mainPath.length(), httpRequest.location().length());

                    if (paramString.length() > 0) {
                        // Remove the first slash
                        paramString = paramString.substring(1);

                        // Decode with the URL decoder
                        paramString = URLDecoder.decode(paramString, "UTF-8");

                        // Split with "/" to get all params
                        String[] params = paramString.split("/");

                        // Fast array copy
                        System.arraycopy(params, 0, objects, 1, params.length/* + 1 - 1*/);
                    }
                }

                Response response = ((Response) entry.method.invoke(object, objects));
                response.header("Content-Type", entry.contentType);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Response.status(Status.NOT_FOUND).build();
    }

    public void add(String path, Entry entry) {

        methods.put(path, entry);
    }

    public String rootPath() {

        return rootPath;
    }

    public Method method(String path) {

        return methods.get(path).method;
    }

    public static class Entry {

        private Method method;

        private String contentType;

        private String acceptContentType;

        private RequestMethod requestMethod;

        private List<String> postParameters = new ArrayList<>();

        private List<String> pathParameters = new ArrayList<>();

        public Entry(Method method, String contentType, String acceptContentType, RequestMethod requestMethod) {

            this.method = method;
            this.contentType = contentType;
            this.acceptContentType = acceptContentType;
            this.requestMethod = requestMethod;
        }

        public Entry(Method method, RequestMethod requestMethod) {

            this(method, "text/html; charset=utf-8", "*/*", requestMethod);
        }

        public void addPostKey(String postKey) {

            postParameters.add(postKey);
        }

        public void addPathKey(String pathKey) {

            pathParameters.add(pathKey);
        }

        public Method method() {

            return method;
        }

        public String contentType() {

            return contentType;
        }

        public String acceptContentType() {

            return acceptContentType;
        }

        public RequestMethod requestMethod() {

            return requestMethod;
        }

        public List<String> postParameters() {

            return postParameters;
        }
    }
}
