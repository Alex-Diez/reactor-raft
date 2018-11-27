package org.algorithms;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

class MockClock extends Clock {
  private Instant now;

  MockClock(Instant now) {
    this.now = now;
  }

  @Override
  public ZoneId getZone() {
    return ZoneId.systemDefault();
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return this;
  }

  @Override
  public Instant instant() {
    return now;
  }

  void tick(long timeout) {
    now = now.plusMillis(timeout);
  }
}
