package org.algorithms;

import java.util.Objects;
import java.util.UUID;

final class Source {
  private final UUID id;

  static Source withNewId() {
    return withId(UUID.randomUUID());
  }

  static Source withId(UUID id) {
    return new Source(id);
  }

  private Source(UUID id) {
    this.id = id;
  }

  Destination toDestination() {
    return Destination.withId(id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj != null && obj.getClass().equals(getClass())) {
      Source source = (Source) obj;
      return Objects.equals(source.id, id);
    }
    return false;
  }

  @Override
  public String toString() {
    return id.toString();
  }
}

