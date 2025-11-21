package com.example.mecManager.Common;

public class AppConstants {

    public static class STATUS {
        public static final Integer SUCCESS = 200;
        public static final Integer ERROR = 500;
        public static final Integer UNAUTHORIZED = 401;
        public static final Integer NOT_FOUND = 404;
        public static final Integer INTERNAL_SERVER_ERROR = 500;
        public static final Integer BAD_REQUEST = 400;
        public static final Integer ACTIVE = 1;// trang thai hoat dong user
        public static final Integer INACTIVE = 0;// trang thai hoat dong user
        public static final Integer ALREADY_EXISTS = 3;
    }

    public static class ROLE{
        public static final String DOCTOR = "DOCTOR";
        public static final String ADMIN = "ADMIN";
    }

    public static class URL{
        public static final String IMG_URL = "https://i.ibb.co/FL5DXK4f/avatar-trang-4.jpg";
        public static final String API_URL = "/apiMecManager";

        public static final String[] ALL_URLS = {
                API_URL + "/prescription/**",
                API_URL + "/patient/**",
                API_URL + "/medicine/**",
                API_URL + "/doctor/**",
        };

        public static final String[] PUBLIC_URLS = {
                API_URL + "/user/login",
                API_URL + "/user/register",
                "/ws/**"
        };

    }

    public static class GENDER{
        public static final Integer MALE = 1;
        public static final Integer FEMALE = 2;
        public static final Integer OTHER = 3;
    }

}
