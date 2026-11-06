/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.turbine.binder.sym;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * A method symbol.
 *
 * <p>Methods aren't uniquely identified by their name and owner, so method symbols also have an
 * identity within their owner, which depends on where the method was read from:
 *
 * <ul>
 *   <li>Methods declared in source are identified by their index in the enclosing declaration.
 *   <li>Methods read from class files are identified by their JVM descriptor.
 * </ul>
 *
 * <p>Symbols of the two kinds are never equal. That matters if an annotation processor generates a
 * source file for a type that is also on the classpath: methods of the classpath type that a
 * processor obtained in an earlier round must not be confused with unrelated methods of the
 * generated source that happen to have the same index.
 */
@Immutable
public class MethodSymbol implements Symbol {
  /** The index of the method in its enclosing declaration, for methods declared in source. */
  private final int idx;

  /** The JVM descriptor of the method, for methods read from class files. */
  private final @Nullable String descriptor;

  private final ClassSymbol owner;
  private final String name;

  /** Creates a symbol for the method declared in source at the given index in its owner. */
  public MethodSymbol(int idx, ClassSymbol owner, String name) {
    this.idx = idx;
    this.descriptor = null;
    this.owner = owner;
    this.name = name;
  }

  /** Creates a symbol for a method read from a class file, with the given JVM descriptor. */
  public MethodSymbol(ClassSymbol owner, String name, String descriptor) {
    this.idx = -1;
    this.descriptor = descriptor;
    this.owner = owner;
    this.name = name;
  }

  /** The enclosing class. */
  public ClassSymbol owner() {
    return owner;
  }

  /** The method name. */
  public String name() {
    return name;
  }

  /** The JVM descriptor, for methods read from class files, or {@code null}. */
  public @Nullable String descriptor() {
    return descriptor;
  }

  @Override
  public int hashCode() {
    return descriptor == null
        ? Objects.hash(name, owner, idx)
        : Objects.hash(name, owner, descriptor);
  }

  @Override
  public Kind symKind() {
    return Kind.METHOD;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (!(obj instanceof MethodSymbol other)) {
      return false;
    }
    return name().equals(other.name())
        && owner().equals(other.owner())
        && idx == other.idx
        && Objects.equals(descriptor, other.descriptor);
  }

  @Override
  public String toString() {
    return owner + "#" + name;
  }
}
