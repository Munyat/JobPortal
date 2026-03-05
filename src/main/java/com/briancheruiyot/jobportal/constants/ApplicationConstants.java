package com.briancheruiyot.jobportal.constants;

public class ApplicationConstants {
    private ApplicationConstants() {
        throw new AssertionError("Utility class cannot be instatiated");

    }

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "$2a$10$OqUZcccTquF6B70SCwXQieVdW28CicB0d7nl41EMIQHPgw99ZawSG";
    public static final String JWT_HEADER = "Authorization";
    public static final String ROLE_JOB_SEEKER = "ROLE_JOB_SEEKER";
}
