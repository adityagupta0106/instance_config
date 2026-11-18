package com.serviceplus.InstanceConfigRegistry.util;

public class ApplicationConstants {

	public static final Integer REDIS_KEY_EXIPRATION_DURATION_IN_MINUTES = 120;
	final public static String GLOBAL_ERROR_MESSAGE = "There is some issue while processing the request.";
	final public static String DEFAULT_TENANT_ID = "507f1f77bcf86cd799439011";
	final public static String HOST_HEADER = "SP-Client-Domain";
    final public static String BASIC_TAB = "BASIC";
    final public static String JANPARICHAY_TAB = "JANPARICHAY";
    final public static String STATUS_NEW = "NEW";
    final public static String STATUS_PROCESSING = "PROCESSING";
    final public static String STATUS_COMPLETED = "COMPLETED";
    final public static String STATUS_FAILED = "FAILED";
    final public static String ACTION_CREATE = "CREATE";
    public static final String OUTBOX_TOKEN_PREFIX="OB";
    public static final String SETUP_ENVIRONMENT="X-Environment";

}
