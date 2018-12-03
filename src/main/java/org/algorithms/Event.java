package org.algorithms;

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
}
