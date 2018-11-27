package org.algorithms;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

class Node {
  private final UUID id;
  private final long electionTimeout;
  private final Clock clock;
  private final EventDispatcher eventDispatcher;
  private final Poller poller;
  private Instant changeStateEvent;
  private int term;
  private Flux<State> state;

  Node(
      UUID id,
      long electionTimeout,
      Clock clock,
      EventDispatcher eventDispatcher,
      Poller poller) {
    this.id = id;
    this.electionTimeout = electionTimeout;
    this.clock = clock;
    this.eventDispatcher = eventDispatcher;
    this.poller = poller;
    this.changeStateEvent = clock.instant();
    state = poller.poll()
              .map(
                  event -> {
                    if (event.type == EventType.VOTING) {
                      return State.LEADER;
                    }
                    if (event.type == EventType.START_ELECTION) {
                      eventDispatcher.fire(new Event(id, event.sourceId, EventType.VOTING));
                      return State.FOLLOWER;
                    }
                    if (event.type == EventType.ELECTION_TIMEOUT) {
                      term++;
                      eventDispatcher.fire(new Event(id, event.sourceId, EventType.START_ELECTION));
                      return State.CANDIDATE;
                    } else {
                      return State.FOLLOWER;
                    }
                  }
              ).defaultIfEmpty(State.FOLLOWER);
  }

  Flux<State> start() {
    return state;
  }

  Flux<Integer> term() {
    return Mono.<Integer>create(e -> e.success(term)).repeat();
  }

}
