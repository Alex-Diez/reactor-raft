package org.algorithms;

import reactor.core.publisher.Flux;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

class EventSubscriber {
  private final Queue<Event> events = new ArrayDeque<>();
  private final Source source;
  private final AtomicBoolean consumption = new AtomicBoolean(false);

  EventSubscriber(Source source) {
    this.source = source;
  }

  Source source() {
    return source;
  }

  EventPublisher publisher(Destination destination) {
    return new EventPublisher(this, destination);
  }

  void submit(Event event) {
    events.offer(event);
  }

  Flux<Event> poll() {
    return Flux.create(
        e -> {
          while (!consumption.get()) {}
          while (!events.isEmpty()) {
            e.next(events.poll());
          }
          e.complete();
        }
    );
  }

  void start() {
    consumption.compareAndSet(false, true);
  }
}
