package org.algorithms;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

import static org.algorithms.Event.Type.*;

class QueuedEventDispatcher implements EventDispatcher {
  private Map<UUID, Queue<Event>> receivedNetworkEvents = new HashMap<>();
  private Map<UUID, Queue<Event>> receivedTimeEvents = new HashMap<>();

  private Map<Event.Type, BiConsumer<Map<UUID, Queue<Event>>, Event>> publishers = new EnumMap<>(Event.Type.class);
  private Map<Event.Type, Map<UUID, Queue<Event>>> receivers = new EnumMap<>(Event.Type.class);

  QueuedEventDispatcher() {
    publishers.put(ELECTION_TIMEOUT, timeEventConsumer());
    publishers.put(START_ELECTION, networkEventConsumer());
    publishers.put(VOTING, networkEventConsumer());

    receivers.put(ELECTION_TIMEOUT, receivedTimeEvents);
    receivers.put(VOTING, receivedTimeEvents);

    receivers.put(START_ELECTION, receivedNetworkEvents);
  }

  private BiConsumer<Map<UUID, Queue<Event>>, Event> timeEventConsumer() {
    return (receiver, event) -> receiver.entrySet()
        .stream()
        .filter(e -> e.getKey().equals(event.destination))
        .map(Map.Entry::getValue)
        .forEach(pollingQueue -> pollingQueue.offer(event));
  }

  private BiConsumer<Map<UUID, Queue<Event>>, Event> networkEventConsumer() {
    return (receiver, event) -> receiver.entrySet()
        .stream()
        .filter(e -> !e.getKey().equals(event.source))
        .map(Map.Entry::getValue)
        .forEach(pollingQueue -> pollingQueue.offer(event));
  }

  @Override
  public void fire(Event event) {
    publishers.get(event.type).accept(receivers.get(event.type), event);
  }

  Poller register(UUID node) {
    receivedNetworkEvents.put(node, new LinkedBlockingQueue<>());
    receivedTimeEvents.put(node, new LinkedBlockingQueue<>());

    return new EventPoller(
        receivedNetworkEvents.get(node),
        receivedTimeEvents.get(node)
    );
  }
}
