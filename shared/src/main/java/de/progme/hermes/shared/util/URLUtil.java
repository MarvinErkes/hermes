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

package de.progme.hermes.shared.util;

import de.progme.hermes.shared.RequestMethod;
import de.progme.hermes.shared.http.Headers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Marvin Erkes on 17.02.2016.
 */
public final class URLUtil {

    public static final int CHUNK_SIZE = 2048;

    public static final String USER_AGENT = "Hermes v2.1.1";

    private URLUtil() {
        // Do not allow instance creation
    }

    public static Headers filterHeaders(Map<String, List<String>> headers) {

        Headers headersReturn = Headers.empty();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            headersReturn.header(entry.getKey(), String.join(";", entry.getValue()));
        }

        return headersReturn;
    }

    public static byte[] readResponse(HttpURLConnection connection) throws IOException {

        boolean error = connection.getResponseCode() >= 400;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); InputStream inputStream = (!error) ? connection.getInputStream() : connection.getErrorStream()) {
            byte[] chunk = new byte[CHUNK_SIZE];

            int i;
            while ((i = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, i);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

    public static HttpURLConnection connection(URL url, Proxy proxy, int connectTimeout, Headers headers, RequestMethod method) throws IOException {

        if (proxy == null) {
            proxy = Proxy.NO_PROXY;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(connectTimeout);
        connection.setRequestMethod(method.name());
        connection.setRequestProperty("User-Agent", USER_AGENT);

        for (Map.Entry<String, String> entry : headers.headers().entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        return connection;
    }
}
