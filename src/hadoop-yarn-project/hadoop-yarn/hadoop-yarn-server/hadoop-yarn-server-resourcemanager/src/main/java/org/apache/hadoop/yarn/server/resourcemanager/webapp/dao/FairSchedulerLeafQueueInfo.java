package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.AppSchedulable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSLeafQueue;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FairSchedulerLeafQueueInfo extends FairSchedulerQueueInfo {
  private int numPendingApps;
  private int numActiveApps;
  
  public FairSchedulerLeafQueueInfo() {
  }
  
  public FairSchedulerLeafQueueInfo(FSLeafQueue queue, FairScheduler scheduler) {
    super(queue, scheduler);
    Collection<AppSchedulable> apps = queue.getAppSchedulables();
    for (AppSchedulable app : apps) {
      if (app.getApp().isPending()) {
        numPendingApps++;
      } else {
        numActiveApps++;
      }
    }
  }
  
  public int getNumActiveApplications() {
    return numPendingApps;
  }
  
  public int getNumPendingApplications() {
    return numActiveApps;
  }
}
