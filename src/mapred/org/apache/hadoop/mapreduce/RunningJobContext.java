package org.apache.hadoop.mapreduce;

public class RunningJobContext {
  private static TaskInputOutputContext _context = null;

  public static void setContext(TaskInputOutputContext context)
  {
    _context = context;
  }

  public static TaskInputOutputContext getContext()
  {
    return _context;
  }
}
