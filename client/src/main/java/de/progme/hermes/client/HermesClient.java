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

package de.progme.hermes.client;

import com.google.gson.Gson;
import de.progme.hermes.shared.http.Body;
import de.progme.hermes.shared.http.Headers;
import de.progme.hermes.shared.http.Response;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by Marvin Erkes on 17.02.2016.
 */
public interface HermesClient {

    Response post(URL url, Body body, Headers headers) throws IOException;

    Response post(String relativePath, Body body, Headers headers) throws IOException;

    Response post(URL url, Proxy proxy, Body body, Headers headers) throws IOException;

    Response post(String relativePath, Proxy proxy, Body body, Headers headers) throws IOException;

    Response post(URL url, Object body, Headers headers) throws IOException;

    Response post(String relativePath, Object body, Headers headers) throws IOException;

    Response post(URL url, Proxy proxy, Object body, Headers headers) throws IOException;

    Response post(String relativePath, Proxy proxy, Object body, Headers headers) throws IOException;

    Response put(URL url, Headers headers) throws IOException;

    Response put(String relativePath, Headers headers) throws IOException;

    Response put(URL url, Proxy proxy, Body body, Headers headers) throws IOException;

    Response put(String relativePath, Proxy proxy, Body body, Headers headers) throws IOException;

    Response patch(URL url, Headers headers) throws IOException;

    Response patch(String relativePath, Headers headers) throws IOException;

    Response patch(URL url, Proxy proxy, Headers headers) throws IOException;

    Response patch(String relativePath, Proxy proxy, Headers headers) throws IOException;

    Response delete(URL url, Headers headers) throws IOException;

    Response delete(String relativePath, Headers headers) throws IOException;

    Response delete(URL url, Proxy proxy, Headers headers) throws IOException;

    Response delete(String relativePath, Proxy proxy, Headers headers) throws IOException;

    Response get(URL url, Proxy proxy, Headers headers) throws IOException;

    Response get(String relativePath, Proxy proxy, Headers headers) throws IOException;

    Response get(URL url, Headers headers) throws IOException;

    Response get(String relativePath, Headers headers) throws IOException;

    <T> T get(URL url, Proxy proxy, Headers headers, Class<T> clazz) throws IOException;

    <T> T get(String relativePath, Proxy proxy, Headers headers, Class<T> clazz) throws IOException;

    <T> T get(URL url, Headers headers, Class<T> clazz) throws IOException;

    <T> T get(String relativePath, Headers headers, Class<T> clazz) throws IOException;

    Response download(URL url, Headers headers, String folderToSaveTo) throws IOException;

    Response download(String relativePath, Headers headers, String folderToSaveTo) throws IOException;

    Response download(URL url, Proxy proxy, Headers headers, String folderToSaveTo) throws IOException;

    Response download(String relativePath, Proxy proxy, Headers headers, String folderToSaveTo) throws IOException;

    int connectTimeout();

    Gson gson();
}
