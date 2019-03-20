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

package de.progme.hermes.server.http;

import de.progme.hermes.shared.RequestMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
public class Request {

    private final String raw;

    private RequestMethod method;

    protected String location;

    private String version;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> postData = new HashMap<>();

    public Request(String raw) {

        this.raw = raw;
        parse();
    }

    private void parse() {

        StringTokenizer tokenizer = new StringTokenizer(raw);

        method = RequestMethod.valueOf(tokenizer.nextToken());
        location = tokenizer.nextToken();
        // Remove last slash
        if (location.endsWith("/")) {
            location = location.substring(0, location.lastIndexOf('/'));
        }
        version = tokenizer.nextToken();

        String[] lines = raw.split("\\n");
        for (int i = 1; i < lines.length; i++) {
            String[] keyVal = lines[i].split(":", 2);

            if (keyVal.length == 2) {
                headers.put(keyVal[0].trim(), keyVal[1].trim());
            }
        }
    }

    public RequestMethod method() {

        return method;
    }

    public String location() {

        return location;
    }

    public String version() {

        return version;
    }

    public String header(String key) {

        return headers.get(key);
    }

    protected void postData(String data) {

        String[] splitted = data.split("&");
        for (String aSplitted : splitted) {
            String[] keyVal = aSplitted.split("=");
            if (keyVal.length == 2) {
                postData.put(keyVal[0], keyVal[1]);
            }
        }
    }

    public String post(String key) {

        return postData.get(key);
    }

    @Override
    public String toString() {

        return "HTTPRequest{" +
                ", method=" + method +
                ", location='" + location + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                '}';
    }
}
