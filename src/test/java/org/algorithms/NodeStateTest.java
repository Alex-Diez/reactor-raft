package org.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.algorithms.Event.Type.ELECTION_TIMEOUT;
import static org.algorithms.State.CANDIDATE;
import static org.algorithms.State.FOLLOWER;
import static org.algorithms.State.LEADER;

class NodeStateTest {
  private static final UUID NODE_TIMER_ID = UUID.randomUUID();

  private Node node;
  private QueuedEventDispatcher eventDispatcher;
  private UUID nodeId;
  private Node follower;
  private Timer nodeTimer;

  @BeforeEach
  void setUp() {
    eventDispatcher = new QueuedEventDispatcher();
    eventDispatcher.register(NODE_TIMER_ID);
    nodeId = UUID.randomUUID();
    final UUID followerId = UUID.randomUUID();
    follower = new Node(
        followerId,
        eventDispatcher,
        eventDispatcher.register(followerId),
        List.of(nodeId)
    );
    node = new Node(
        nodeId,
        eventDispatcher,
        eventDispatcher.register(nodeId),
        List.of(followerId)
    );
    nodeTimer = new Timer(5L, ChronoUnit.MILLIS, NODE_TIMER_ID, nodeId);
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

    node.start()
        .as(StepVerifier::create)
        .thenConsumeWhile(CANDIDATE::equals)
        .expectNext(LEADER)
        .thenCancel()
        .verify();
  }

  private void nodeElectionTimeout() {
    nodeTimer.electionTimeoutEvent()
        .doOnNext(eventDispatcher::fire)
        .as(StepVerifier::create)
        .expectNextCount(1)
        .thenCancel()
        .verify();
  }

  private void nodeStartElection() {
    node.start()
        .as(StepVerifier::create)
        .expectNext(CANDIDATE)
        .thenCancel()
        .verify();
  }

  private void followerSendsVote() {
    follower.start()
        .as(StepVerifier::create)
        .expectNext(FOLLOWER)
        .thenCancel()
        .verify();
  }
}
