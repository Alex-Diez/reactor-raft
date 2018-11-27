package org.algorithms;

import reactor.core.publisher.Flux;

interface Poller {
  Flux<Event> poll();
}
