package org.algorithms;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.algorithms.Event.Type.*;

class SingleNodeStateTest {
  @Test
  void nodeStartsInFollowerState() {
    final Source networkSource = Source.withNewId();
    EventSubscriber networkSubscriber = new EventSubscriber(networkSource);
    networkSubscriber.start();

    final Source instanceSource = Source.withNewId();
    final Destination instanceDestination = instanceSource.toDestination();
    EventPublisher networkPublisher = networkSubscriber.publisher(instanceDestination);

    EventSubscriber instanceEvents = new EventSubscriber(instanceSource);
    instanceEvents.start();

    EventPublisher timeEventGenerator = instanceEvents.publisher(Destination.withNewId());
    EventPublisher networkEventReceiver = instanceEvents.publisher(Destination.withNewId());

    Instance instance = new Instance(instanceEvents, networkPublisher);

    instance.start();

    networkSubscriber.poll()
        .as(StepVerifier::create)
        .expectNext(FOLLOWER_STATE.event(networkSource, instanceDestination))
        .thenCancel()
        .verify();
  }

  @Test
  @Disabled("subscriber poll should be controlled outside of subscriber")
  void nodeBecomesCandidate_afterElectionTimeout() {
    final Source networkSource = Source.withNewId();
    EventSubscriber networkSubscriber = new EventSubscriber(networkSource);
    networkSubscriber.start();

    final Source instanceSource = Source.withNewId();
    final Destination instanceDestination = instanceSource.toDestination();
    EventPublisher networkPublisher = networkSubscriber.publisher(instanceDestination);

    EventSubscriber instanceEvents = new EventSubscriber(instanceSource);
    EventPublisher timeEventGenerator = instanceEvents.publisher(instanceDestination);
    EventPublisher networkEventReceiver = instanceEvents.publisher(instanceDestination);

    Instance instance = new Instance(instanceEvents, networkPublisher);

    instance.start();

    timeEventGenerator.publish(ELECTION_TIMEOUT);

    networkSubscriber.poll()
        .as(StepVerifier::create)
        .expectNext(FOLLOWER_STATE.event(networkSource, instanceDestination))
        .expectNext(CANDIDATE_STATE.event(networkSource, instanceDestination))
        .thenCancel()
        .verify();
  }

  private static class Instance {
    private final EventSubscriber instanceEvents;
    private final EventPublisher networkSender;

    Instance(EventSubscriber instanceEvents, EventPublisher networkSender) {
      this.instanceEvents = instanceEvents;
      this.networkSender = networkSender;
    }

    void start() {
      networkSender.publish(FOLLOWER_STATE);
      instanceEvents.poll()
          .doOnSubscribe(s -> instanceEvents.start())
          .subscribe(e -> {
            if (e.type == ELECTION_TIMEOUT) {
              networkSender.publish(CANDIDATE_STATE);
            }
          });
    }
  }
}
