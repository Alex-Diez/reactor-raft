package org.algorithms;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

class QueuedEventDispatcher implements EventDispatcher {
  private Map<UUID, Queue<Event>> sentNetworkEvents = new HashMap<>();
  private Map<UUID, Queue<Event>> receivedNetworkEvents = new HashMap<>();
  private Map<UUID, Queue<Event>> sentTimeEvents = new HashMap<>();
  private Map<UUID, Queue<Event>> receivedTimeEvents = new HashMap<>();

  private Map<EventType, BiConsumer<Map<UUID, Queue<Event>>, Event>> publishers = new EnumMap<>(EventType.class);
  private Map<EventType, Map<UUID, Queue<Event>>> receivers = new EnumMap<>(EventType.class);

  QueuedEventDispatcher() {
    publishers.put(EventType.ELECTION_TIMEOUT, timeEventConsumer());
    publishers.put(EventType.START_ELECTION, networkEventConsumer());
    publishers.put(EventType.VOTING, networkEventConsumer());

    receivers.put(EventType.ELECTION_TIMEOUT, receivedTimeEvents);
    receivers.put(EventType.START_ELECTION, receivedNetworkEvents);
    receivers.put(EventType.VOTING, receivedTimeEvents);
  }

  private BiConsumer<Map<UUID, Queue<Event>>, Event> timeEventConsumer() {
    return (receiver, event) -> receiver.entrySet()
        .stream()
        .filter(e -> e.getKey().equals(event.targetId))
        .map(Map.Entry::getValue)
        .forEach(pollingQueue -> pollingQueue.offer(event));
  }

  private BiConsumer<Map<UUID, Queue<Event>>, Event> networkEventConsumer() {
    return (receiver, event) -> receiver.entrySet()
        .stream()
        .filter(e -> !e.getKey().equals(event.sourceId))
        .map(Map.Entry::getValue)
        .forEach(pollingQueue -> pollingQueue.offer(event));
  }

  @Override
  public void fire(Event event) {
    publishers.get(event.type).accept(receivers.get(event.type), event);
  }

  Poller register(UUID node) {
    sentNetworkEvents.put(node, new LinkedBlockingQueue<>());
    receivedNetworkEvents.put(node, new LinkedBlockingQueue<>());
    sentTimeEvents.put(node, new LinkedBlockingQueue<>());
    receivedTimeEvents.put(node, new LinkedBlockingQueue<>());

    return new EventPoller(
        receivedNetworkEvents.get(node),
        receivedTimeEvents.get(node)
    );
  }
}
