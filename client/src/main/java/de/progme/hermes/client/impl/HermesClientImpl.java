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

package de.progme.hermes.client.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import de.progme.hermes.client.HermesClient;
import de.progme.hermes.shared.RequestMethod;
import de.progme.hermes.shared.Status;
import de.progme.hermes.shared.http.Body;
import de.progme.hermes.shared.http.Headers;
import de.progme.hermes.shared.http.Response;
import de.progme.hermes.shared.util.URLUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by Marvin Erkes on 01.02.2016.
 */
public class HermesClientImpl implements HermesClient {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final int CONTENT_DISPOSITION_FILENAME_LENGTH = 10;

    private int connectTimeout;

    private Gson gson = new Gson();

    private String baseUrl;

    public HermesClientImpl() {

        this(DEFAULT_TIMEOUT, "");
    }

    public HermesClientImpl(String baseUrl) {

        this(DEFAULT_TIMEOUT, baseUrl);
    }

    public HermesClientImpl(int connectTimeout) {

        this(connectTimeout, "");
    }

    public HermesClientImpl(int connectTimeout, String baseUrl) {

        this.connectTimeout = connectTimeout;
        this.baseUrl = baseUrl;
    }

    @Override
    public Response post(URL url, Body body, Headers headers) throws IOException {

        return post(url, null, body, headers);
    }

    @Override
    public Response post(URL url, Proxy proxy, Body body, Headers headers) throws IOException {

        Preconditions.checkNotNull(url, "url cannot be null");
        Preconditions.checkNotNull(body, "body cannot be null");
        Preconditions.checkNotNull(headers, "headers cannot be null");

        HttpURLConnection connection = URLUtil.connection(url, proxy, connectTimeout, headers, RequestMethod.POST);

        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.write(body.bytes());
        }

        byte[] response = URLUtil.readResponse(connection);

        return new Response(Status.valueOf(connection.getResponseCode()), URLUtil.filterHeaders(connection.getHeaderFields()), Body.of(response));
    }

    @Override
    public Response post(URL url, Object object, Headers headers) throws IOException {

        return post(url, null, object, headers);
    }

    @Override
    public Response post(URL url, Proxy proxy, Object object, Headers headers) throws IOException {

        Preconditions.checkNotNull(object, "object cannot be null");

        return post(url, proxy, Body.of(gson.toJson(object)), headers);
    }

    @Override
    public Response post(String relativePath, Body body, Headers headers) throws IOException {

        return post(relativePath, null, body, headers);
    }

    @Override
    public Response post(String relativePath, Proxy proxy, Body body, Headers headers) throws IOException {

        return post(buildPath(relativePath), proxy, body, headers);
    }

    @Override
    public Response post(String relativePath, Object body, Headers headers) throws IOException {

        return post(relativePath, null, body, headers);
    }

    @Override
    public Response post(String relativePath, Proxy proxy, Object body, Headers headers) throws IOException {

        return post(buildPath(relativePath), proxy, body, headers);
    }

    @Override
    public Response put(URL url, Headers headers) throws IOException {

        return put(url, null, headers);
    }

    @Override
    public Response put(URL url, Proxy proxy, Headers headers) throws IOException {

        return request(url, proxy, headers, RequestMethod.PUT);
    }

    @Override
    public Response put(String relativePath, Headers headers) throws IOException {

        return put(relativePath, null, headers);
    }

    @Override
    public Response put(String relativePath, Proxy proxy, Headers headers) throws IOException {

        return put(buildPath(relativePath), proxy, headers);
    }

    @Override
    public Response patch(URL url, Headers headers) throws IOException {

        return patch(url, null, headers);
    }

    @Override
    public Response patch(String relativePath, Headers headers) throws IOException {

        return patch(relativePath, null, headers);
    }

    @Override
    public Response patch(URL url, Proxy proxy, Headers headers) throws IOException {

        return request(url, proxy, headers, RequestMethod.PATCH);
    }

    @Override
    public Response patch(String relativePath, Proxy proxy, Headers headers) throws IOException {

        return patch(buildPath(relativePath), proxy, headers);
    }

    @Override
    public Response delete(URL url, Headers headers) throws IOException {

        return delete(url, null, headers);
    }

    @Override
    public Response delete(URL url, Proxy proxy, Headers headers) throws IOException {

        return request(url, proxy, headers, RequestMethod.DELETE);
    }

    @Override
    public Response delete(String relativePath, Headers headers) throws IOException {

        return delete(relativePath, null, headers);
    }

    @Override
    public Response delete(String relativePath, Proxy proxy, Headers headers) throws IOException {

        return delete(buildPath(relativePath), proxy, headers);
    }

    @Override
    public Response get(URL url, Headers headers) throws IOException {

        return get(url, null, headers);
    }

    @Override
    public Response get(URL url, Proxy proxy, Headers headers) throws IOException {

        return request(url, proxy, headers, RequestMethod.GET);
    }

    @Override
    public Response get(String relativePath, Proxy proxy, Headers headers) throws IOException {

        return get(buildPath(relativePath), proxy, headers);
    }

    @Override
    public Response get(String relativePath, Headers headers) throws IOException {

        return get(relativePath, null, headers);
    }

    @Override
    public <T> T get(URL url, Proxy proxy, Headers headers, Class<T> clazz) throws IOException {

        Response response = request(url, proxy, headers, RequestMethod.GET);

        return gson.fromJson(response.body().content(), clazz);
    }

    @Override
    public <T> T get(URL url, Headers headers, Class<T> clazz) throws IOException {

        return get(url, null, headers, clazz);
    }

    @Override
    public <T> T get(String relativePath, Proxy proxy, Headers headers, Class<T> clazz) throws IOException {

        return get(buildPath(relativePath), proxy, headers, clazz);
    }

    @Override
    public <T> T get(String relativePath, Headers headers, Class<T> clazz) throws IOException {

        return get(relativePath, null, headers, clazz);
    }

    @Override
    public Response download(URL url, Headers headers, String folderToSaveTo) throws IOException {

        return download(url, null, headers, folderToSaveTo);
    }

    @Override
    public Response download(URL url, Proxy proxy, Headers headers, String folderToSaveTo) throws IOException {

        Preconditions.checkNotNull(url, "url cannot be null");
        Preconditions.checkNotNull(headers, "headers cannot be null");
        Preconditions.checkNotNull(folderToSaveTo, "folderToSaveTo cannot be null");
        Preconditions.checkArgument(!folderToSaveTo.isEmpty(), "folderToSaveTo cannot be empty");

        HttpURLConnection connection = URLUtil.connection(url, proxy, connectTimeout, headers, RequestMethod.GET);

        String disposition = connection.getHeaderField("Content-Disposition");

        if (disposition == null) {
            throw new IllegalArgumentException("no 'Content-Disposition' header present");
        }

        String fileName = null;

        int index = disposition.indexOf("filename=");
        if (index > 0) {
            fileName = disposition.substring(index + CONTENT_DISPOSITION_FILENAME_LENGTH, disposition.length() - 1);
        }

        if (fileName == null) {
            throw new IllegalStateException("unable to get the file name from the disposition header: " + disposition);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(folderToSaveTo + File.separator + fileName); InputStream inputStream = connection.getInputStream()) {
            byte[] chunk = new byte[URLUtil.CHUNK_SIZE];

            int i;
            while ((i = inputStream.read(chunk)) > 0) {
                fileOutputStream.write(chunk, 0, i);
            }
        }

        return new Response(Status.valueOf(connection.getResponseCode()), URLUtil.filterHeaders(connection.getHeaderFields()), Body.of("".getBytes()));
    }

    @Override
    public Response download(String relativePath, Headers headers, String folderToSaveTo) throws IOException {

        return download(relativePath, null, headers, folderToSaveTo);
    }

    @Override
    public Response download(String relativePath, Proxy proxy, Headers headers, String folderToSaveTo) throws IOException {

        return download(buildPath(relativePath), proxy, headers, folderToSaveTo);
    }

    private Response request(URL url, Proxy proxy, Headers headers, RequestMethod method) throws IOException {

        Preconditions.checkNotNull(url, "url cannot be null");
        Preconditions.checkNotNull(headers, "headers cannot be null");

        HttpURLConnection connection = URLUtil.connection(url, proxy, connectTimeout, headers, method);

        byte[] response = URLUtil.readResponse(connection);

        return new Response(Status.valueOf(connection.getResponseCode()), URLUtil.filterHeaders(connection.getHeaderFields()), Body.of(response));
    }

    private URL buildPath(String relativePath) throws IOException {

        return new URL(baseUrl + relativePath);
    }

    @Override
    public int connectTimeout() {

        return connectTimeout;
    }

    @Override
    public Gson gson() {

        return gson;
    }
}
