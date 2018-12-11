package org.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.algorithms.Event.Type.ELECTION_TIMEOUT;

class PublishSubscribeEventTest {

  private Source source;
  private EventSubscriber eventSubscriber;

  private Destination firstDestination;
  private EventPublisher publisher;

  @BeforeEach
  void setUp() {
    source = Source.withNewId();
    eventSubscriber = new EventSubscriber(source);

    firstDestination = Destination.withNewId();
    publisher = eventSubscriber.publisher(firstDestination);
  }

  @Test
  void manyPublishers_oneSubscriber() {
    eventSubscriber.start();
    final Destination secondDestination = Destination.withNewId();
    EventPublisher secondPublisher = eventSubscriber.publisher(secondDestination);

    publisher.publish(ELECTION_TIMEOUT);
    secondPublisher.publish(ELECTION_TIMEOUT);

    eventSubscriber.poll()
        .as(StepVerifier::create)
        .expectNext(ELECTION_TIMEOUT.event(source, firstDestination))
        .expectNext(ELECTION_TIMEOUT.event(source, secondDestination))
        .thenCancel()
        .verify();
  }

  @Test
  void subscriberPollsEventually() {
    final Destination secondDestination = Destination.withNewId();
    EventPublisher secondPublisher = eventSubscriber.publisher(secondDestination);

    eventSubscriber.poll()
        .subscribeOn(Schedulers.elastic())
        .as(StepVerifier::create)
        .then(() -> publisher.publish(ELECTION_TIMEOUT))
        .then(() -> secondPublisher.publish(ELECTION_TIMEOUT))
        .then(() -> eventSubscriber.start())
        .expectNext(ELECTION_TIMEOUT.event(source, firstDestination))
        .expectNext(ELECTION_TIMEOUT.event(source, secondDestination))
        .thenCancel()
        .verify();
  }

  @Test
  void subscriberIsEmpty_whenCreated() {
    eventSubscriber.start();
    eventSubscriber.poll()
        .as(StepVerifier::create)
//        .expectSubscription()
        .expectNoEvent(Duration.ofMillis(20))
        .thenCancel()
        .verify();
  }
}
