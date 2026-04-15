package com.gbc.dormio_mobile_app.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
    const val CONNECT_TIMEOUT: Long = 10
    const val READ_TIMEOUT: Long = 10
    const val WRITE_TIMEOUT: Long = 10
    const val API_AUTH_LOGIN = "auth/login"
    const val API_AUTH_REGISTER = "auth/register"
    const val API_AUTH_LOGOUT = "auth/logout"
    const val API_AUTH_VERIFY = "auth/verify"
    const val API_AUTH_DEVICE_TOKEN = "auth/device-token"
    const val API_USERS_FCM_TOKEN = "users/device-token"

    // maintenance endpoints
    // STUDENT ENDPOINTS
    const val API_MAINTENANCE_REQUESTS_CREATE = "maintenance/create"
    const val API_MAINTENANCE_MY_REQUESTS = "maintenance/myReq"
    const val API_MAINTENANCE_REQUEST_DETAIL_STUDENT = "maintenance/myReq/{id}"
    const val API_MAINTENANCE_REQUEST_UPDATE_STUDENT = "maintenance/student/{id}"

    // ADMIN ENDPOINTS
    const val API_MAINTENANCE_REQUESTS_ALL = "maintenance/"
    const val API_MAINTENANCE_REQUEST_DETAIL_ADMIN = "maintenance/{id}"
    const val API_MAINTENANCE_REQUEST_UPDATE_STATUS = "maintenance/{id}/status"

    //BOTH USER ENDPOINTS
    const val API_MAINTENANCE_REQUEST_DELETE = "maintenance/delete/{id}"
    // end of maintenance endpoints

    //MEAL PLAN ENDPOINTS
    const val API_MEAL_PLANS = "meal-plans"
    const val API_MEAL_PLAN_DETAIL_WEEK = "meal-plans/{mealPlanId}"
    const val API_MEAL_PLAN_DAY = "meal-plans/{mealPlanTypeId}/day/{dayOfWeek}"
    const val API_MEAL_PLAN_SUBSCRIBE = "meal-plans/subscribe"
    const val API_MEAL_PLAN_USER_DETAIL = "meal-plans/my-plan"
    const val API_MEAL_PLAN_UPSERT = "meal-plans/templates"
    const val API_MEAL_PLAN_MEAL_ITEMS = "meal-plans/meal-items"


    //PROFILE MANAGEMENT ENDPOINTS
    const val API_PROFILE = "profile"
    const val API_PROFILE_AVATAR = "profile/avatar"
    const val KEY_PROFILE_IMAGE = "profileImage"

    //CHORES ENDPOINTS
    const val API_CHORES_DASHBOARD = "chores/dashboard"
    const val API_CHORES_CREATE = "chores"
    const val API_CHORES_HOUSEMATES = "chores/housemates"
    const val API_CHORES_COMPLETE = "chores/{id}/complete"
    const val API_CHORES_UPDATE = "chores/{id}"
    const val API_CHORES_BY_ID = "chores/{id}"

    //SCHEDULE / CALENDAR ENDPOINTS
    const val API_CALENDAR = "calendar"
    const val API_CALENDAR_DETAIL = "calendar/{id}"

    //BILL ENDPOINTS
    const val API_BILLS = "bills"
    const val API_BILL_DETAIL = "bills/{id}"
    const val API_BILL_MY_SHARES = "bills/my-shares"
    const val API_BILL_SPLIT = "bills/{id}/split"
    const val API_BILL_SHARES = "bills/{id}/shares"
    const val API_BILL_SHARE_PAY = "bills/{id}/shares/{shareId}/pay"

    //EXPENSE ENDPOINTS
    const val API_EXPENSES = "expenses"
    const val API_EXPENSE_DETAIL = "expenses/{id}"

}