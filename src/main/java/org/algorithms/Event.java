package org.algorithms;

import java.util.Objects;
import java.util.UUID;

final class Event {
  final UUID sourceId;
  final UUID targetId;
  final Type type;

  private Event(UUID sourceId, UUID targetId, Type type) {
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.type = type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceId, targetId, type);
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) return true;
    if (object != null && object.getClass().equals(getClass())) {
      Event event = (Event)object;
      return event.type == type
          && event.sourceId.equals(sourceId)
          && event.targetId.equals(targetId);
    }
    return false;
  }

  enum Type {
    START_ELECTION, ELECTION_TIMEOUT, VOTING;

    Event event(UUID source, UUID target) {
      return new Event(source, target, this);
    }
  }
}
