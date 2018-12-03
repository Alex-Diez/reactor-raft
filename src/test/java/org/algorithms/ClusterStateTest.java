package org.algorithms;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

class ClusterStateTest {
  @Test
  void createSimplestCluster() {
    Clock globalClock = new MockClock(Instant.now());
    QueuedEventDispatcher dispatcher = new QueuedEventDispatcher();

    long[] electionTimeouts = {1_000L, 3_000L, 5_000L};
    UUID[] nodeIds = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
    Poller[] pollers = new Poller[3];

    for (int i = 0; i < 3; i++) {
      pollers[i] = dispatcher.register(nodeIds[i]);
    }

    Node one = new Node(UUID.randomUUID(), dispatcher, pollers[0], List.of(nodeIds[1], nodeIds[2]));
    Node two = new Node(UUID.randomUUID(), dispatcher, pollers[1], List.of(nodeIds[0], nodeIds[2]));
    Node three = new Node(UUID.randomUUID(), dispatcher, pollers[2], List.of(nodeIds[0], nodeIds[1]));
  }
}
