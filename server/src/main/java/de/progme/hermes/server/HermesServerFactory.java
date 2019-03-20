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

package de.progme.hermes.server;

import de.progme.hermes.server.impl.HermesConfig;
import de.progme.hermes.server.impl.HermesServerImpl;

/**
 * Created by Marvin Erkes on 30.01.2016.
 */
public final class HermesServerFactory {

    private HermesServerFactory() {
        // Do not allow instance creation
    }

    public static HermesServer create(HermesConfig hermesConfig) {

        if (hermesConfig == null) {
            throw new IllegalArgumentException("hermesConfig cannot be null");
        }

        return new HermesServerImpl(hermesConfig);
    }

    public static HermesServer create() {

        return new HermesServerImpl();
    }
}
