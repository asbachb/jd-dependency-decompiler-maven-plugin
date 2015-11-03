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

import com.exxeta.oses.maven.plugin.decompiler.DecompilationException;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.inject.Named;

import com.exxeta.oses.maven.plugin.decompiler.ClassDecompiler;

import jd.core.Decompiler;
import jd.core.process.DecompilerImpl;

/**
 *
 * @author <a href="mailto:benjamin.asbach@exxeta.com">Benjamin Asbach, 2015</a>
 */
@Named
public class JdJarDecompiler extends ClassDecompiler {

    private static final CommonPreferences DEFAULT_DECOMPILE_PREFERENCES = new CommonPreferences();

    @Override
    public void decompileClass(InputStream compiledClass, OutputStream decompiledClass) throws DecompilationException {
        try (PrintStream printStream = new PrintStream(decompiledClass)) {
            PlainTextPrinter printer = new PlainTextPrinter(DEFAULT_DECOMPILE_PREFERENCES, printStream);

            PrintStream originalSystemErr = disableJdCoreErrorMessages();
            try (DataInputStream classFileInputStream = new DataInputStream(compiledClass)) {
                Decompiler decompiler = new DecompilerImpl();
                decompiler.decompile(
                        DEFAULT_DECOMPILE_PREFERENCES,
                        new DataInputStreamLoader(classFileInputStream),
                        printer,
                        ""
                );
            } catch (Exception ex) {
                throw new DecompilationException(ex);
            } finally {
                System.setErr(originalSystemErr);
            }
        }
    }

    private PrintStream disableJdCoreErrorMessages() {
        PrintStream originalSystemErr = System.err;
        System.setErr(null);
        return originalSystemErr;
    }
}
