package org.algorithms;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.algorithms.Event.Type.ELECTION_TIMEOUT;

class TimerTest {

  @Test
  void sendElectionTimeoutEventRepeatedly() {
    final UUID timerId = UUID.randomUUID();
    final UUID nodeId = UUID.randomUUID();
    Timer timer = new Timer(1L, ChronoUnit.MILLIS, timerId, nodeId);

    timer.electionTimeoutEvent()
        .as(StepVerifier::create)
        .thenAwait(Duration.of(1L, ChronoUnit.MILLIS))
        .expectNext(ELECTION_TIMEOUT.event(timerId, nodeId))
        .thenAwait(Duration.of(1L, ChronoUnit.MILLIS))
        .expectNext(ELECTION_TIMEOUT.event(timerId, nodeId))
        .thenAwait(Duration.of(1L, ChronoUnit.MILLIS))
        .expectNext(ELECTION_TIMEOUT.event(timerId, nodeId))
        .thenCancel()
        .verify();
  }
}
