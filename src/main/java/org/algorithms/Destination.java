package org.algorithms;

import java.util.Objects;
import java.util.UUID;

final class Destination {
  private final UUID id;

  static Destination withNewId() {
    return withId(UUID.randomUUID());
  }

  static Destination withId(UUID id) {
    return new Destination(id);
  }

  private Destination(UUID id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj != null && obj.getClass().equals(getClass())) {
      Destination destination = (Destination)obj;
      return Objects.equals(destination.id, id);
    }
    return false;
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
