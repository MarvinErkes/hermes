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

package de.progme.hermes.shared.http;

import com.google.common.base.Preconditions;
import de.progme.hermes.shared.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;

public class Response {

    public static final String VERSION = "HTTP/1.1";

    private Status status;

    private Headers headers = new Headers();

    private Body body;

    public Response() {

    }

    public Response(Status status, Headers headers, Body body) {

        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public void addDefaultHeaders() {

        headers.header("Date", new Date().toString());
        headers.header("Server", "Hermes v2.2.2");
        headers.header("Connection", "close");
        if (body != null) {
            headers.header("Content-Length", Integer.toString(body.bytes().length));
        }
    }

    public Status status() {

        return status;
    }

    public int responseCode() {

        return status.status();
    }

    public String responseReason() {

        return status.reason();
    }

    public Headers headers() {

        return headers;
    }

    public Map<String, String> headersMap() {

        return headers.headers();
    }

    public Body body() {

        return body;
    }

    public Response header(String name, String value) {

        headers.header(name, value);

        return this;
    }

    public void headers(Headers headers) {

        this.headers = headers;
    }

    public static Builder ok() {

        return status(Status.OK);
    }

    public static Builder status(Status status) {

        return new Builder(status);
    }

    public static Response file(File file) {

        Preconditions.checkArgument(file != null, "file cannot be null");
        Preconditions.checkArgument(!file.isDirectory(), "file is a directory");
        Preconditions.checkArgument(file.exists(), "file '" + file.getName() + "' does not exists");

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            return Response.ok().header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").content(bytes).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

    public static Response file(String path) {

        return file(new File(path));
    }

    public static class Builder {

        private Status status;

        private Body body;

        private Headers headers = new Headers();

        public Builder(Status status) {

            this.status = status;
        }

        public Builder content(byte[] content) {

            this.body = new Body(content);

            return this;
        }

        public Builder content(Body body) {

            this.body = body;

            return this;
        }

        public Builder content(String content) {

            return content(content.getBytes());
        }


        public Builder header(String key, String value) {

            headers.header(key, value);

            return this;
        }

        public Builder headers(Headers headers) {

            this.headers = headers;

            return this;
        }

        public Response build() {

            Response response = new Response();
            response.status = status;
            response.body = body;
            response.headers(headers);

            return response;
        }
    }
}