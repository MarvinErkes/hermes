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

import de.progme.hermes.client.HermesClient;
import de.progme.hermes.client.HermesClientFactory;
import de.progme.hermes.shared.http.Body;
import de.progme.hermes.shared.http.Headers;
import de.progme.hermes.shared.http.Response;

import java.net.URL;
import java.util.Map;

/**
 * Created by Marvin Erkes on 04.02.2016.
 */
public class HermesClientTest {

    public static void main(String[] args) throws Exception {

        HermesClient client = HermesClientFactory.create();
        Response response = client.post(new URL("http://localhost:8080/master/info"), Body.form("name", "Name").form("password", "Password").build(), Headers.empty());
        System.out.println("Status: " + response.status());
        System.out.println("Body: " + new String(response.body().bytes()));
        for (Map.Entry<String, String> entry : response.headersMap().entrySet()) {
            System.out.println("Header: " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
