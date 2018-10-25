package vortex.versatilemobitech.com.utilities;


public class Constants {

    public static final String APP_PREF = "vortex_preff";
    public static final int NO_INTERNET_CONNECTION = 1;


    public static final String USER_ID = "USER_ID";
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String FULLNAME = "FULLNAME";
    public static final String AUTHARIZATIONID = "AUTHARIZATIONID";
    public static final String EMAIL = "EMAIL";
    public static final String DOB = "DOB";
    public static final String GENDER = "GENDER";
    public static final String PH_NO = "PH_NO";
    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PROFILE_IMAGE = "PROFILE_IMAGE";

    public static int SPLASH_TIME_OUT = 3000;

    public static final String BASE_URL = "http://192.169.243.70:5004/users/";

    public static final String REGISTERATION_URL = BASE_URL + "createuser";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String SOCIAL_URL = BASE_URL + "socialLogins";
    public static final String FORGOT_URL = BASE_URL + "forgotpassword";
    public static final String CHANGE_URL = BASE_URL + "change_password";
    public static final String UPLOAD_URL = BASE_URL + "upload_images";

    public static final String EDITPROFILE_URL = BASE_URL + "edit_profile";
    public static final String VIEWPROFILE_URL = BASE_URL + "viewdetails";


    public static final boolean logMessageOnOrOff = true;


    public static final int LOGIN_APICODE = 21;
    public static final int REGISTER_APICODE = 22;
    public static final int SOCIAL_APICODE = 23;
    public static final int FORGOTPASSWORD_APICODE = 24;
    public static final int CHANGEPASSWORD_APICODE = 25;
    public static final int UPLOADIMAGES_APICODE = 26;
    public static final int EDITPROFILE_APICODE = 27;
    public static final int VIEWPROFILE_APICODE = 28;


}
