package org.algorithms;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.algorithms.Event.Type.START_ELECTION;
import static org.algorithms.Event.Type.VOTING;

class Node {
  private final UUID id;
  private final EventDispatcher eventDispatcher;
  private final Poller poller;
  private int term;
  private Flux<State> state;

  Node(
      UUID id,
      EventDispatcher eventDispatcher,
      Poller poller,
      List<UUID> cluster) {
    this.id = id;
    this.eventDispatcher = eventDispatcher;
    this.poller = poller;
    state = poller.poll()
              .map(
                  event -> {
                    if (event.type == VOTING) {
                      return State.LEADER;
                    }
                    if (event.type == START_ELECTION) {
                      eventDispatcher.fire(VOTING.event(id, event.sourceId));
                      return State.FOLLOWER;
                    }
                    if (event.type == Event.Type.ELECTION_TIMEOUT) {
                      term++;
                      for (UUID target : cluster) {
                        eventDispatcher.fire(START_ELECTION.event(id, target));
                      }
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
