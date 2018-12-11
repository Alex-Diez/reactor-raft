package org.algorithms;

import java.util.Objects;

final class Event {
  final Source source;
  final Destination destination;
  final Type type;

  private Event(Source source, Destination destination, Type type) {
    this.source = source;
    this.destination = destination;
    this.type = type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(source, destination, type);
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) return true;
    if (object != null && object.getClass().equals(getClass())) {
      Event event = (Event)object;
      return event.type == type
          && Objects.equals(event.source, source)
          && Objects.equals(event.destination, destination);
    }
    return false;
  }

  @Override
  public String toString() {
    return "Event{" +
        "source=" + source +
        ", destination=" + destination +
        ", type=" + type +
        '}';
  }

  public enum Type {
    START_ELECTION, ELECTION_TIMEOUT, VOTING,
    FOLLOWER_STATE, CANDIDATE_STATE;

    public Event event(Source source, Destination destination) {
      return new Event(source, destination, this);
    }
  }
}
