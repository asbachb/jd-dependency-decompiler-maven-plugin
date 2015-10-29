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
import com.exxeta.oses.maven.plugin.decompiler.DecompilationStatistics;
import com.exxeta.oses.maven.plugin.decompiler.JarDecompiler;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import jd.core.Decompiler;
import jd.core.process.DecompilerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:benjamin.asbach@exxeta.com">Benjamin Asbach, 2015</a>
 */
@Named
public class JdJarDecompiler implements JarDecompiler {

    private final Logger logger = LoggerFactory.getLogger(JdJarDecompiler.class);

    private static final CommonPreferences DEFAULT_DECOMPILE_PREFERENCES = new CommonPreferences();

    @Override
    public DecompilationStatistics decompileJar(Path jarToDecompile, Path destination) throws IOException {
        if (Files.exists(destination)) {
            throw new FileAlreadyExistsException(destination.toString());
        }
        logger.debug("Decompiling {}", jarToDecompile);

        FileSystem jarFileSystem = createJarFileSystem(destination);
        return copyEntriesFromSourceZipToDestination(jarToDecompile, jarFileSystem);
    }

    private FileSystem createJarFileSystem(Path jar) throws IOException {
        URI zipUri = URI.create("jar:file:" + jar.toUri().getPath());
        Map<String, Object> env = new HashMap<>();
        env.put("create", "true");
        return FileSystems.newFileSystem(zipUri, env);
    }

    private DecompilationStatistics copyEntriesFromSourceZipToDestination(Path jarToDecompile, final FileSystem destinationZipFileSystem) throws IOException {
        final DecompilationStatistics stats = new DecompilationStatistics();
        final Decompiler decompiler = new DecompilerImpl();

        FileSystem sourceJarFileSystem = createJarFileSystem(jarToDecompile);
        Files.walkFileTree(sourceJarFileSystem.getPath("/"), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path dirInZip = destinationZipFileSystem.getPath(dir.toString());
                if (!Files.exists(dirInZip)) {
                    Files.createDirectory(dirInZip);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (fileIsAClass(file)) {
                    logger.debug("Decompiling class {}", file.getFileName());
                    try {
                        writeDecompiledClassToZip(file);
                        stats.recordDecompiledClass();
                    } catch (DecompilationException ex) {
                        stats.recordDecompiledErrors();
                        Path faultyDecompiledJavaClass = ex.getFaultyFile();
                        if (Files.exists(faultyDecompiledJavaClass)) {
                            Files.delete(faultyDecompiledJavaClass);
                        }
                        logger.debug("Exception during decompilation", ex);
                    }
                } else {
                    logger.debug("Since {} is not a class file. Just copy it to destionation zip", file);
                    Files.copy(file, destinationZipFileSystem.getPath(file.toString()));
                }

                return FileVisitResult.CONTINUE;
            }

            private void writeDecompiledClassToZip(Path classFile) throws IOException, DecompilationException {
                Path javaFilePath = Paths.get(classFile.toString().replace(".class", ".java"));
                Path javaFileInZip = destinationZipFileSystem.getPath(javaFilePath.toString());

                OutputStream javaFileInZipOutputStream = Files.newOutputStream(javaFileInZip);
                try (PrintStream printStream = new PrintStream(javaFileInZipOutputStream)) {
                    PlainTextPrinter printer = new PlainTextPrinter(DEFAULT_DECOMPILE_PREFERENCES, printStream);

                    PrintStream originalSystemErr = disableJdCoreErrorMessages();
                    try (DataInputStream classFileInputStream = new DataInputStream(Files.newInputStream(classFile))) {
                        decompiler.decompile(
                                DEFAULT_DECOMPILE_PREFERENCES,
                                new DataInputStreamLoader(classFileInputStream),
                                printer,
                                "");
                    } catch (Exception ex) {
                        throw new DecompilationException(ex, javaFileInZip);
                    } finally {
                        System.setErr(originalSystemErr);
                    }
                }
            }

            private boolean fileIsAClass(Path file) {
                return file.getFileName().toString().endsWith(".class");
            }
        });
        destinationZipFileSystem.close();

        return stats;
    }

    private PrintStream disableJdCoreErrorMessages() {
        PrintStream originalSystemErr = System.err;
        System.setErr(null);
        return originalSystemErr;
    }
}
