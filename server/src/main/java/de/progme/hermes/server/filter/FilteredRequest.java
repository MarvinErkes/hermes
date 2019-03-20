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

package de.progme.hermes.server.filter;

import de.progme.hermes.server.http.Request;
import de.progme.hermes.shared.RequestMethod;
import de.progme.hermes.shared.http.Response;

/**
 * Created by Marvin Erkes on 31.01.2016.
 */
public class FilteredRequest {

    private Request request;

    private Response response;

    public FilteredRequest(Request request) {

        this.request = request;
    }

    public void abortWith(Response response) {

        this.response = response;
    }

    public RequestMethod method() {

        return request.method();
    }

    public String location() {

        return request.location();
    }

    public String header(String key) {

        return request.header(key);
    }

    public Response response() {

        return response;
    }
}
