package org.algorithms;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

class ClusterStateTest {
  @Test
  void createSimplestCluster() throws Exception {
    Clock globalClock = new MockClock(Instant.now());
    QueuedEventDispatcher dispatcher = new QueuedEventDispatcher();

    long[] electionTimeouts = {1_000L, 3_000L, 5_000L};
    UUID[] nodeIds = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
    Poller[] pollers = new Poller[3];

    for (int i = 0; i < 3; i++) {
      pollers[i] = dispatcher.register(nodeIds[i]);
    }

    Node one = new Node(UUID.randomUUID(), electionTimeouts[0], globalClock, dispatcher, pollers[0]);
    Node two = new Node(UUID.randomUUID(), electionTimeouts[1], globalClock, dispatcher, pollers[1]);
    Node three = new Node(UUID.randomUUID(), electionTimeouts[2], globalClock, dispatcher, pollers[2]);
  }
}
