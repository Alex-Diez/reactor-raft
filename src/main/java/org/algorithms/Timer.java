package org.algorithms;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.algorithms.Event.Type.ELECTION_TIMEOUT;

class Timer {
  private final UUID timerId;
  private final UUID nodeId;
  private final Duration duration;

  Timer(long duration, ChronoUnit unit, UUID timerId, UUID nodeId) {
    this.duration = Duration.of(duration, unit);
    this.timerId = timerId;
    this.nodeId = nodeId;
  }

  Flux<Event> electionTimeoutEvent() {
    return Mono.<Event>create(e -> e.success(ELECTION_TIMEOUT.event(timerId, nodeId)))
        .delayElement(duration)
        .repeat();
  }
}
