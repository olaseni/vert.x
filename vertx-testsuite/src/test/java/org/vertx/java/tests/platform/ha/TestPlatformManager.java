/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package org.vertx.java.tests.platform.ha;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.impl.DefaultPlatformManager;
import org.vertx.java.platform.impl.Deployment;
import org.vertx.java.platform.impl.FailoverCompleteHandler;

import java.util.Map;

public class TestPlatformManager extends DefaultPlatformManager {

  public TestPlatformManager(int port, String hostname, int quorumSize, String haGroup) {
    super(port, hostname, quorumSize, haGroup);
  }

  void failDuringFailover(boolean fail) {
    haManager.failDuringFailover(fail);
  }

  public void simulateKill() {
    if (haManager != null) {
      haManager.simulateKill();
    }
    super.stop();
  }

  // For testing only
  public Map<String, Deployment> getDeployments() {
    return deployments;
  }

  private boolean setFailoverCompleteHandler;
  private FailoverCompleteHandler failoverCompleteHandler;

  public synchronized void failoverCompleteHandler(FailoverCompleteHandler failoverCompleteHandler) {
    if (haManager != null) {
      if (!setFailoverCompleteHandler) {
        haManager.addFailoverCompleteHandler(new FailoverCompleteHandler() {
          @Override
          public void handle(String nodeID, JsonObject haInfo, boolean failed) {
            if (TestPlatformManager.this.failoverCompleteHandler != null) {
              TestPlatformManager.this.failoverCompleteHandler.handle(nodeID, haInfo, failed);
            }
          }
        });
        setFailoverCompleteHandler = true;
      }
      this.failoverCompleteHandler = failoverCompleteHandler;
    }
  }

}
