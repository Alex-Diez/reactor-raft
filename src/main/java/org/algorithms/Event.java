package org.algorithms;

import java.util.Objects;
import java.util.UUID;

class Event {
  final UUID sourceId;
  final UUID targetId;
  final EventType type;

  Event(UUID sourceId, UUID targetId, EventType type) {
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o != null && getClass().equals(o.getClass())) {
      Event event = (Event) o;
      return type == event.type &&
          Objects.equals(sourceId, event.sourceId) &&
          Objects.equals(targetId, event.targetId);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceId, type);
  }

  @Override
  public String toString() {
    return "Event{" +
        "sourceId=" + sourceId +
        ", targetId=" + targetId +
        ", type=" + type +
        '}';
  }
}
