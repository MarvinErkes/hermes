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

import com.google.common.base.Preconditions;
import de.progme.hermes.client.impl.HermesClientImpl;

/**
 * Created by Marvin Erkes on 17.02.2016.
 */
public final class HermesClientFactory {

    private HermesClientFactory() {
        // Do not allow instance creation
    }

    public static HermesClient create() {

        return new HermesClientImpl();
    }

    public static HermesClient create(int connectTimeout) {

        Preconditions.checkArgument(connectTimeout > 0, "connectTimeout cannot be negative");

        return new HermesClientImpl(connectTimeout);
    }

    public static HermesClient create(String baseUrl) {

        Preconditions.checkArgument(baseUrl != null, "baseUrl cannot be null");
        Preconditions.checkArgument(!baseUrl.isEmpty(), "baseUrl cannot be empty");

        return new HermesClientImpl(baseUrl);
    }
}
