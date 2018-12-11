package org.algorithms;

class EventPublisher {
  private final EventSubscriber eventSubscriber;
  private final Destination destination;

  EventPublisher(EventSubscriber eventSubscriber, Destination destination) {
    this.eventSubscriber = eventSubscriber;
    this.destination = destination;
  }

  void publish(Event.Type eventType) {
    eventSubscriber.submit(eventType.event(eventSubscriber.source(), destination));
  }
}
