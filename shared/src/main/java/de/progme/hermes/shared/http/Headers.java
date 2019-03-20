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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marvin Erkes on 17.02.2016.
 */
public class Headers {

    private Map<String, String> headers = new HashMap<>();

    public Headers() {

    }

    public Headers(String name, String value) {

        header(name, value);
    }

    public Headers header(String name, String value) {

        headers.put(name, value);

        return this;
    }

    public Map<String, String> headers() {

        return headers;
    }

    public static Headers empty() {

        return new Headers();
    }

    public static Headers from(String name, String value) {

        return new Headers(name, value);
    }
}
