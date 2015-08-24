/**
 * This file is part of Java Decompiler Dependency Decompiler Maven Plugin.
 *
 * Java Decompiler Dependency Decompiler Maven Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java Decompiler Dependency Decompiler Maven Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java Decompiler Dependency Decompiler Maven Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.exxeta.oses.maven.plugin.decompiler.jd;

import java.io.DataInputStream;
import jd.core.loader.Loader;
import jd.core.loader.LoaderException;

/**
 *
 * @author <a href="mailto:benjamin.asbach@exxeta.com">Benjamin Asbach, 2015</a>
 */
public class DataInputStreamLoader implements Loader {

    private final DataInputStream dataInputStream;

    public DataInputStreamLoader(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }
    
    @Override
    public DataInputStream load(String string) throws LoaderException {
        return dataInputStream;
    }

    @Override
    public boolean canLoad(String string) {
        return true;
    }
}
