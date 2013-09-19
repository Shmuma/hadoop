package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.classification.InterfaceStability.Evolving;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.resource.Resources;
import org.apache.hadoop.yarn.util.BuilderUtils;

@Private
@Evolving
public class FairSchedulerConfiguration extends Configuration {
  public static final String FS_CONFIGURATION_FILE = "fair-scheduler.xml";

  private static final String CONF_PREFIX =  "yarn.scheduler.fair.";

  protected static final String ALLOCATION_FILE = CONF_PREFIX + "allocation.file";
  protected static final String EVENT_LOG_DIR = "eventlog.dir";

  /** Whether to use the user name as the queue name (instead of "default") if
   * the request does not specify a queue. */
  protected static final String  USER_AS_DEFAULT_QUEUE = CONF_PREFIX + "user-as-default-queue";
  protected static final boolean DEFAULT_USER_AS_DEFAULT_QUEUE = true;

  protected static final float  DEFAULT_LOCALITY_THRESHOLD = -1.0f;

  /** Cluster threshold for node locality. */
  protected static final String LOCALITY_THRESHOLD_NODE = CONF_PREFIX + "locality.threshold.node";
  protected static final float  DEFAULT_LOCALITY_THRESHOLD_NODE =
		  DEFAULT_LOCALITY_THRESHOLD;

  /** Cluster threshold for rack locality. */
  protected static final String LOCALITY_THRESHOLD_RACK = CONF_PREFIX + "locality.threshold.rack";
  protected static final float  DEFAULT_LOCALITY_THRESHOLD_RACK =
		  DEFAULT_LOCALITY_THRESHOLD;

  /** Whether preemption is enabled. */
  protected static final String  PREEMPTION = CONF_PREFIX + "preemption";
  protected static final boolean DEFAULT_PREEMPTION = false;
  
  protected static final String PREEMPTION_INTERVAL = CONF_PREFIX + "preemptionInterval";
  protected static final int DEFAULT_PREEMPTION_INTERVAL = 5000;
  protected static final String WAIT_TIME_BEFORE_KILL = CONF_PREFIX + "waitTimeBeforeKill";
  protected static final int DEFAULT_WAIT_TIME_BEFORE_KILL = 15000;

  /** Whether to assign multiple containers in one check-in. */
  protected static final String  ASSIGN_MULTIPLE = CONF_PREFIX + "assignmultiple";
  protected static final boolean DEFAULT_ASSIGN_MULTIPLE = false;

  /** Whether to give more weight to apps requiring many resources. */
  protected static final String  SIZE_BASED_WEIGHT = CONF_PREFIX + "sizebasedweight";
  protected static final boolean DEFAULT_SIZE_BASED_WEIGHT = false;

  /** Maximum number of containers to assign on each check-in. */
  protected static final String MAX_ASSIGN = CONF_PREFIX + "max.assign";
  protected static final int DEFAULT_MAX_ASSIGN = -1;

  public FairSchedulerConfiguration(Configuration conf) {
    super(conf);
    addResource(FS_CONFIGURATION_FILE);
  }

  public Resource getMinimumAllocation() {
    int mem = getInt(
        YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB);
    int cpu = getInt(
        YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);
    return Resources.createResource(mem, cpu);
  }

  public Resource getMaximumAllocation() {
    int mem = getInt(
        YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_MB,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
    int cpu = getInt(
        YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_CORES,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_CORES);
    return Resources.createResource(mem, cpu);
  }

  public boolean getUserAsDefaultQueue() {
    return getBoolean(USER_AS_DEFAULT_QUEUE, DEFAULT_USER_AS_DEFAULT_QUEUE);
  }

  public float getLocalityThresholdNode() {
    return getFloat(LOCALITY_THRESHOLD_NODE, DEFAULT_LOCALITY_THRESHOLD_NODE);
  }

  public float getLocalityThresholdRack() {
    return getFloat(LOCALITY_THRESHOLD_RACK, DEFAULT_LOCALITY_THRESHOLD_RACK);
  }

  public boolean getPreemptionEnabled() {
    return getBoolean(PREEMPTION, DEFAULT_PREEMPTION);
  }

  public boolean getAssignMultiple() {
    return getBoolean(ASSIGN_MULTIPLE, DEFAULT_ASSIGN_MULTIPLE);
  }

  public int getMaxAssign() {
    return getInt(MAX_ASSIGN, DEFAULT_MAX_ASSIGN);
  }

  public boolean getSizeBasedWeight() {
    return getBoolean(SIZE_BASED_WEIGHT, DEFAULT_SIZE_BASED_WEIGHT);
  }

  public String getAllocationFile() {
    return get(ALLOCATION_FILE);
  }

  public String getEventlogDir() {
    return get(EVENT_LOG_DIR, new File(System.getProperty("hadoop.log.dir",
    		"/tmp/")).getAbsolutePath() + File.separator + "fairscheduler");
  }
  
  public int getPreemptionInterval() {
    return getInt(PREEMPTION_INTERVAL, DEFAULT_PREEMPTION_INTERVAL);
  }
  
  public int getWaitTimeBeforeKill() {
    return getInt(WAIT_TIME_BEFORE_KILL, DEFAULT_WAIT_TIME_BEFORE_KILL);
  }
  
  /**
   * Parses a resource config value of a form like "1024", "1024 mb",
   * or "1024 mb, 3 vcores". If no units are given, megabytes are assumed.
   * 
   * @throws AllocationConfigurationException
   */
  public static Resource parseResourceConfigValue(String val)
      throws AllocationConfigurationException {
    try {
      int memory = findResource(val, "mb");
      int vcores = findResource(val, "vcores");
      return BuilderUtils.newResource(memory, vcores);
    } catch (AllocationConfigurationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new AllocationConfigurationException(
          "Error reading resource config", ex);
    }
  }
  
  private static int findResource(String val, String units)
    throws AllocationConfigurationException {
    Pattern pattern = Pattern.compile("(\\d+) ?" + units);
    Matcher matcher = pattern.matcher(val);
    if (!matcher.find()) {
      throw new AllocationConfigurationException("Missing resource: " + units);
    }
    return Integer.parseInt(matcher.group(1));
  }
}
