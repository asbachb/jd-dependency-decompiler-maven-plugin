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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 * @author <a href="mailto:benjamin.asbach@gmail.com">Benjamin Asbach, 2015</a>
 */
@Mojo(name = "dependency:sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class DependencyDecompilerMojo extends com.exxeta.oses.maven.plugin.decompiler.DependencyDecompilerMojo{
}
