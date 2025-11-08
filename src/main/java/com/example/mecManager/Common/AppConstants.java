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
    }

    public static class ROLE{
        public static final Integer DOCTOR = 1;
        public static final Integer ADMIN = 2;
    }
}
