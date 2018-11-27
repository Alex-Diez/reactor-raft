package org.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import reactor.test.StepVerifier;

import static org.algorithms.State.CANDIDATE;
import static org.algorithms.State.FOLLOWER;
import static org.algorithms.State.LEADER;

class NodeStateTest {
  private static final long ELECTION_TIMEOUT = 5_000L;
  private static final UUID NODE_TIMER_ID = UUID.randomUUID();

  private Node node;
  private MockClock clock;
  private QueuedEventDispatcher eventDispatcher;
  private UUID nodeId;
  private Node follower;
  private UUID followerId;

  @BeforeEach
  void setUp() {
    clock = new MockClock(Instant.now());
    eventDispatcher = new QueuedEventDispatcher();
    eventDispatcher.register(NODE_TIMER_ID);
    nodeId = UUID.randomUUID();
    followerId = UUID.randomUUID();
    follower = new Node(
        followerId,
        ELECTION_TIMEOUT + 1_000L,
        clock,
        eventDispatcher,
        eventDispatcher.register(followerId)
    );
    node = new Node(
        nodeId,
        ELECTION_TIMEOUT,
        clock,
        eventDispatcher,
        eventDispatcher.register(nodeId)
    );
  }

  @Test
  void nodeStartsAsFollower() {
    node.start().as(StepVerifier::create).expectNext(FOLLOWER).thenCancel().verify();
    node.term().as(StepVerifier::create).expectNext(0).thenCancel().verify();
  }

  @Test
  void nodeBecomeLeader_whenElectionTimeoutIsPassed() {
    nodeElectionTimeout();

    node.start().as(StepVerifier::create).expectNext(CANDIDATE).thenCancel().verify();
    node.term().as(StepVerifier::create).expectNext(1).thenCancel().verify();
  }

  @Test
  void otherNode_votesForCurrentNode_ifItIsFollower() {
    nodeElectionTimeout();
    nodeStartElection();
    followerSendsVote();

    node.start().as(StepVerifier::create).thenConsumeWhile(CANDIDATE::equals).expectNext(LEADER).thenCancel().verify();
  }

  private void nodeElectionTimeout() {
    eventDispatcher.fire(new Event(NODE_TIMER_ID, nodeId, EventType.ELECTION_TIMEOUT));
  }

  private void nodeStartElection() {
    node.start().as(StepVerifier::create).expectNext(CANDIDATE).thenCancel().verify();
  }

  private void followerSendsVote() {
    follower.start().as(StepVerifier::create).expectNext(FOLLOWER).thenCancel().verify();
  }
}
