package com.example.mecManager.Common;

public class AppConstants {

    public static class STATUS {
        // HTTP Status Codes
        public static final Integer SUCCESS = 200;
        public static final Integer CREATED = 201;
        public static final Integer BAD_REQUEST = 400;
        public static final Integer UNAUTHORIZED = 401;
        public static final Integer FORBIDDEN = 403;
        public static final Integer NOT_FOUND = 404;
        public static final Integer CONFLICT = 409;
        public static final Integer INTERNAL_SERVER_ERROR = 500;
    }

    public static class ACTIVE_STATUS {
        public static final Boolean ACTIVE = true;
        public static final Boolean INACTIVE = false;
    }

    public static class ROLE {
        public static final String DOCTOR = "DOCTOR";
        public static final String ADMIN = "ADMIN";
    }

    public static class URL {
        public static final String IMG_URL = "https://i.ibb.co/FL5DXK4f/avatar-trang-4.jpg";
        public static final String API_BASE = "/api/v1";

        public static final String[] ALL_URLS = {
                API_BASE + "/prescriptions/**",
                API_BASE + "/patients/**",
                API_BASE + "/medicines/**",
                API_BASE + "/doctors/**",
        };

        public static final String[] PUBLIC_URLS = {
                API_BASE + "/auth/login",
                API_BASE + "/auth/register",
                "/ws/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api-docs",
                "/api-docs/**",
        };
    }

    public static class GENDER {
        public static final Integer MALE = 1;
        public static final Integer FEMALE = 2;
        public static final Integer OTHER = 3;
    }

}