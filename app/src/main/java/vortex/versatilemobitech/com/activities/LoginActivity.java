package vortex.versatilemobitech.com.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.internal.Util;
import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.interfaces.IParseListener;
import vortex.versatilemobitech.com.model.ImageModel;
import vortex.versatilemobitech.com.utilities.Constants;
import vortex.versatilemobitech.com.utilities.PopUtilities;
import vortex.versatilemobitech.com.utilities.ServerResponse;
import vortex.versatilemobitech.com.utilities.Utility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IParseListener {

    private static final int RC_SIGN_IN = 1;
    FirebaseAuth auth;
    ImageView ImgGoogle, ImgFacebook;
    TextView txtForgotPassword;
    TextView edtEmail, edtPassword;
    TextView txtLogin;
    ImageView ivPassword;
    boolean showhide = false;
    TextView txtRegister;
    String base64ImageString;
    ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        initUi();
    }

    private void initUi() {

        auth = FirebaseAuth.getInstance();

        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        ImgFacebook = (ImageView) findViewById(R.id.imgFacebook);
        ImgGoogle = (ImageView) findViewById(R.id.imgGoogle);
        ivPassword = (ImageView) findViewById(R.id.ivPassword);

        ImgFacebook.setOnClickListener(this);
        ImgGoogle.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        ivPassword.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Log.e("User", "onActivityResult: " + auth.getCurrentUser());

                @SuppressLint("RestrictedApi") Uri uri = response.getUser().getPhotoUri();
                String image = "" + uri;
                @SuppressLint("RestrictedApi") String userName = response.getUser().getName();

                Log.e("username", "onActivityResult: " + image);


                String logintype = response.getProviderType().replace(".com", "");

                //callServicewithBitmap(this, image, response.getIdpToken(), response.getEmail(), logintype, userName);
                if (Utility.isNetworkAvailable(this)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("autharizationid", "" + response.getIdpToken());
                        jsonObject.put("email", "" + response.getEmail());
                        jsonObject.put("logintype", "" + logintype);
                        jsonObject.put("fullname", "" + userName);
                        jsonObject.put("profile_image", "");

                        ServerResponse serverResponse = new ServerResponse();
                        serverResponse.serviceRequest(this, Constants.SOCIAL_URL, jsonObject, this, Constants.SOCIAL_APICODE);
                    } catch (JSONException e) {
                        Utility.showLog("Error", "" + e);
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }

//                startActivity(new Intent(this, HomeActivity.class));
            } else {
            /*    if (response.getError().getErrorCode() instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }*/
                // Sign in failed
                LoginManager.getInstance().logOut();
                Utility.showLog("Error Code", "" + response.getError().getErrorCode());

                Utility.showLog("Error Code", "" + response.getError().getErrorCode());
                PopUtilities.alertDialog(this, "Authetication Failed. Try with other Login", null);
                if (response.equals(null)) {
                    PopUtilities.alertDialog(this, "Authetication Failed. Try with other. Login", null);
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtRegister: {
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
            }
            case R.id.txtLogin: {
                if (Utility.isNetworkAvailable(this)) {
                    if (validation()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", "" + edtEmail.getText().toString().trim());
                            jsonObject.put("password", "" + edtPassword.getText().toString().trim());

                            Utility.showLoadingDialog(this, "Loading...", false);

                            ServerResponse serverResponse = new ServerResponse();
                            serverResponse.serviceRequest(this, Constants.LOGIN_URL, jsonObject, this, Constants.LOGIN_APICODE);
                        } catch (JSONException e) {
                            Utility.showLog("Error", "" + e);
                        }
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }
                break;
            }
            case R.id.imgGoogle: {
                if (Utility.isNetworkAvailable(this)) {
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);

                }

                break;
            }
            case R.id.imgFacebook: {
                if (Utility.isNetworkAvailable(this)) {
                    startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);

                }
                break;
            }
            case R.id.txtForgotPassword: {
                showForgotPasswordDialog();
                break;
            }
            case R.id.ivPassword: {

                if (!showhide) {
                    showhide = true;
                    ivPassword.setImageResource(R.drawable.password_show);
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showhide = false;
                    ivPassword.setImageResource(R.drawable.password_hide);
                    edtPassword.setInputType(129);
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean validation() {
        boolean validated = false;
        if (Utility.isValueNullOrEmpty(edtEmail.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Email", null);
            edtEmail.requestFocus();
        } else if (!Utility.isEmail(edtEmail.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Valid Email", null);
            edtEmail.requestFocus();
        } else if (Utility.isValueNullOrEmpty(edtPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Password", null);
            edtPassword.requestFocus();
        /*} else if (!Utility.isPassword(edtPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this,"Password should contain one Capital Letter,Number and Special Character with more than 8 Characters",null);
            edtPassword.requestFocus();*/
        } else {
            validated = true;
        }
        return validated;
    }

    private void showForgotPasswordDialog() {
        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText edtEmail = (EditText) dialog.findViewById(R.id.edtEmail);
        final TextView tvSubmit = (TextView) dialog.findViewById(R.id.txtSubmit);
        final ImageView ivCancel = dialog.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                    Utility.setSnackBarEnglish(LoginActivity.this, edtEmail, "Please Enter EMail Id");
                    edtEmail.requestFocus();
                } else if (!Utility.isEmail(edtEmail.getText().toString().trim())) {
                    Utility.setSnackBarEnglish(LoginActivity.this, edtEmail, "Please Enter Valid EmailId");
                    edtEmail.requestFocus();
                } else if (Utility.isNetworkAvailable(LoginActivity.this)) {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("email", edtEmail.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utility.showLoadingDialog(LoginActivity.this, "Loading...", false);
                    ServerResponse serverResponse = new ServerResponse();
                    serverResponse.serviceRequest(LoginActivity.this, Constants.FORGOT_URL, jsonObject, LoginActivity.this, Constants.FORGOTPASSWORD_APICODE);
                    dialog.dismiss();
                } else {
                    Utility.showSettingDialog(LoginActivity.this,
                            LoginActivity.this.getResources().getString(R.string.please_check_internet),
                            LoginActivity.this.getResources().getString(R.string.internet_connection), Constants.NO_INTERNET_CONNECTION).show();
                }

            }
        });
        dialog.show();
    }

    @Override
    public void ErrorResponse(VolleyError volleyError, int requestCode) {
        Utility.hideLoadingDialog();
        PopUtilities.alertDialog(this, "Something went Wrong... Try Again", null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        Utility.hideLoadingDialog();
        if (requestCode == Constants.LOGIN_APICODE) {
            //{"status":400,"message":"passwod does not matched","error_field":"passwod does not matched"}
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
                    imageModelArrayList.clear();
                    String message = jsonObject.optString("message");
                    String token = jsonObject.optString("token");
                    String profilepath = jsonObject.optString("profilepath");
                    JSONObject jsonObjectesult = jsonObject.optJSONObject("result");
                    String fullname = jsonObjectesult.optString("fullname");
                    String dateofbirth = jsonObjectesult.optString("dateofbirth");
                    String gender = jsonObjectesult.optString("gender");
                    String phonenumber = jsonObjectesult.optString("phonenumber");
                    String password1 = jsonObjectesult.optString("password1");
                    String profile = jsonObjectesult.optString("profile");
                    String logintype = jsonObjectesult.optString("logintype");
                    String autharizationid = jsonObjectesult.optString("autharizationid");
                    String userId = jsonObjectesult.optString("userId");
                    String description = jsonObjectesult.optString("description");
                    String date = jsonObjectesult.optString("date");
                    String id = jsonObjectesult.optString("_id");
                    String email = jsonObjectesult.optString("email");
                    String password = jsonObjectesult.optString("password");
                    int v = jsonObjectesult.optInt("__v");
                    JSONArray jsonArrayImages = jsonObjectesult.optJSONArray("userImage");

                    for (int i = 0; i < jsonArrayImages.length(); i++) {
                        JSONObject jsonObjectImage = jsonArrayImages.optJSONObject(i);
                        String image = jsonObjectImage.optString("image");
                        String imagId = jsonObjectImage.optString("_id");
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImageId(imagId);
                        imageModel.setImageUrl(profilepath + image);

                        imageModelArrayList.add(imageModel);
                    }
                    Utility.setSharedPrefStringData(this, Constants.USER_ID, id);
                    Utility.setSharedPrefStringData(this, Constants.FULLNAME, fullname);
                    Utility.setSharedPrefStringData(this, Constants.USER_TOKEN, token);

                    Utility.setSharedPrefStringData(this, Constants.AUTHARIZATIONID, autharizationid);
                    Utility.setSharedPrefStringData(this, Constants.EMAIL, email);
                    Utility.setSharedPrefStringData(this, Constants.DOB, dateofbirth);
                    Utility.setSharedPrefStringData(this, Constants.GENDER, gender);
                    Utility.setSharedPrefStringData(this, Constants.PH_NO, phonenumber);
                    Utility.setSharedPrefStringData(this, Constants.LOGIN_TYPE, logintype);
                    Utility.setSharedPrefStringData(this, Constants.DESCRIPTION, description);
                    Utility.setSharedPrefStringData(this, Constants.PROFILE_IMAGE, profilepath + profile);

                    startActivity(new Intent(this, HomeActivity.class));
                    finish();

                } else {
                    PopUtilities.alertDialog(this, jsonObject.optString("message"), null);
                }
            } catch (JSONException e) {
                Utility.showLog("Error Response", "" + e);
            }

        } else if (requestCode == Constants.FORGOTPASSWORD_APICODE) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Utility.showLog("Error", "" + e);
            }
        } else if (requestCode == Constants.SOCIAL_APICODE) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
                    imageModelArrayList.clear();
                    String message = jsonObject.optString("message");
                    String token = jsonObject.optString("token");
                    String profilepath = jsonObject.optString("profilepath");
                    JSONObject jsonObjectesult = jsonObject.optJSONObject("result");
                    String fullname = jsonObjectesult.optString("fullname");
                    String dateofbirth = jsonObjectesult.optString("dateofbirth");
                    String gender = jsonObjectesult.optString("gender");
                    String phonenumber = jsonObjectesult.optString("phonenumber");
                    String password1 = jsonObjectesult.optString("password1");
                    String profile = jsonObjectesult.optString("profile");
                    String logintype = jsonObjectesult.optString("logintype");
                    String autharizationid = jsonObjectesult.optString("autharizationid");
                    String userId = jsonObjectesult.optString("userId");
                    String description = jsonObjectesult.optString("description");
                    String date = jsonObjectesult.optString("date");
                    String id = jsonObjectesult.optString("_id");
                    String email = jsonObjectesult.optString("email");
                    String password = jsonObjectesult.optString("password");
                    int v = jsonObjectesult.optInt("__v");
                    JSONArray jsonArrayImages = jsonObjectesult.optJSONArray("userImage");

                    for (int i = 0; i < jsonArrayImages.length(); i++) {
                        JSONObject jsonObjectImage = jsonArrayImages.optJSONObject(i);
                        String image = jsonObjectImage.optString("image");
                        String imagId = jsonObjectImage.optString("_id");
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImageId(imagId);
                        imageModel.setImageUrl(image);

                        imageModelArrayList.add(imageModel);
                    }
                    Utility.setSharedPrefStringData(this, Constants.USER_ID, id);
                    Utility.setSharedPrefStringData(this, Constants.FULLNAME, fullname);
                    Utility.setSharedPrefStringData(this, Constants.USER_TOKEN, token);

                    Utility.setSharedPrefStringData(this, Constants.AUTHARIZATIONID, autharizationid);
                    Utility.setSharedPrefStringData(this, Constants.EMAIL, email);
                    Utility.setSharedPrefStringData(this, Constants.DOB, dateofbirth);
                    Utility.setSharedPrefStringData(this, Constants.GENDER, gender);
                    Utility.setSharedPrefStringData(this, Constants.PH_NO, phonenumber);
                    Utility.setSharedPrefStringData(this, Constants.LOGIN_TYPE, logintype);
                    Utility.setSharedPrefStringData(this, Constants.DESCRIPTION, description);
                    Utility.setSharedPrefStringData(this, Constants.PROFILE_IMAGE, profilepath + profile);

                    startActivity(new Intent(this, HomeActivity.class));
                    finish();

                } else {
                    PopUtilities.alertDialog(this, jsonObject.optString("message"), null);
                }
            } catch (JSONException e) {
                Utility.showLog("Error", "" + e);
            }
        }

    }

    private void callServicewithBitmap(final Context context, final String image, final String idpToken, final String email, final String logintype, final String userName) {
        Picasso.get().load(image).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                base64ImageString = Utility.encodeImage(bitmap);
                Utility.showLog("base64ImageString", "succe" + base64ImageString);
                if (Utility.isNetworkAvailable(context)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("autharizationid", "" + idpToken);
                        jsonObject.put("email", "" + email);
                        jsonObject.put("logintype", "" + logintype);
                        jsonObject.put("fullname", "" + userName);
                        jsonObject.put("profile_image", "" + base64ImageString);

                        ServerResponse serverResponse = new ServerResponse();
                        serverResponse.serviceRequest(context, Constants.SOCIAL_URL, jsonObject, (IParseListener) context, Constants.SOCIAL_APICODE);
                    } catch (JSONException e) {
                        Utility.showLog("Error", "" + e);
                    }
                } else {
                    PopUtilities.alertDialog(context, Utility.getResourcesString(context, R.string.please_check_internet), null);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (Utility.isNetworkAvailable(context)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("autharizationid", "" + idpToken);
                        jsonObject.put("email", "" + email);
                        jsonObject.put("logintype", "" + logintype);
                        jsonObject.put("fullname", "" + userName);
                        jsonObject.put("profile_image", "");

                        ServerResponse serverResponse = new ServerResponse();
                        serverResponse.serviceRequest(context, Constants.SOCIAL_URL, jsonObject, (IParseListener) context, Constants.SOCIAL_APICODE);
                    } catch (JSONException e1) {
                        Utility.showLog("Error", "" + e1);
                    }
                } else {
                    PopUtilities.alertDialog(context, Utility.getResourcesString(context, R.string.please_check_internet), null);
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
