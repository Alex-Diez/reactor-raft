package org.algorithms;

import java.util.Queue;

import reactor.core.publisher.Flux;

public class EventPoller implements Poller {
  private final Queue<Event> networkEvents;
  private final Queue<Event> timeEvents;

  EventPoller(Queue<Event> networkEvents, Queue<Event> timeEvents) {
    this.networkEvents = networkEvents;
    this.timeEvents = timeEvents;
  }

  @Override
  public Flux<Event> poll() {
    return Flux.create(
        emitter -> {
          while (!networkEvents.isEmpty() || !timeEvents.isEmpty()) {
            if (!networkEvents.isEmpty()) {
              emitter.next(networkEvents.poll());
            }
            if (!timeEvents.isEmpty()) {
              emitter.next(timeEvents.poll());
            }
          }
          emitter.complete();
        }
    );
  }
}
