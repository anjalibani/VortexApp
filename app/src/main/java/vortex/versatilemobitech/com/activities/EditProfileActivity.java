package vortex.versatilemobitech.com.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.interfaces.IParseListener;
import vortex.versatilemobitech.com.model.ImageModel;
import vortex.versatilemobitech.com.utilities.Constants;
import vortex.versatilemobitech.com.utilities.FileUtils;
import vortex.versatilemobitech.com.utilities.PopUtilities;
import vortex.versatilemobitech.com.utilities.ScalingUtilities;
import vortex.versatilemobitech.com.utilities.ServerResponse;
import vortex.versatilemobitech.com.utilities.StaticUtils;
import vortex.versatilemobitech.com.utilities.Utility;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, IParseListener {
    ImageView mImgBack, mImgUserprofile, mImgEdit;
    TextView mTxtUsername, mTxtChangepassword, mTxtSave;
    EditText mEdtName, mEdtDateofbirth, mEdtGender;
    String[] gender = {"Male", "Female"};
    private String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private static final int LONG_DELAY = 15000;
    private static final int PHONE_GALLERY_CLICK = 1001;
    private static final int PHONE_CAMERA_CLICK = 1002;
    private String selectedImagePath;
    private Bitmap bitmap;
    private Uri uri;
    private String imageString = "";
    ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();
    TextView txtLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    private void setReferences() {
        txtLogout = (TextView) findViewById(R.id.txtLogout);
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgUserprofile = (ImageView) findViewById(R.id.imgProfile);
        mImgEdit = (ImageView) findViewById(R.id.imgEdit);
        mTxtUsername = (TextView) findViewById(R.id.txtName);
        mTxtChangepassword = (TextView) findViewById(R.id.txtChangepassword);
        mTxtSave = (TextView) findViewById(R.id.txtSave);
        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtDateofbirth = (EditText) findViewById(R.id.edtDateofbirth);
        mEdtGender = (EditText) findViewById(R.id.edtGender);


        if (!Utility.isValueNullOrEmpty(Utility.getSharedPrefStringData(this, Constants.PROFILE_IMAGE))) {
            Picasso.get().load(Utility.getSharedPrefStringData(this, Constants.PROFILE_IMAGE))
                    .placeholder(R.drawable.profileholder).into(mImgUserprofile);
        }
        if (!Utility.getSharedPrefStringData(this, Constants.LOGIN_TYPE).equals("Normal")) {
            mTxtChangepassword.setVisibility(View.GONE);
        }

        mTxtUsername.setText(Utility.getSharedPrefStringData(this, Constants.FULLNAME));
        mEdtName.setText(Utility.getSharedPrefStringData(this, Constants.FULLNAME));
        mEdtDateofbirth.setText(Utility.getSharedPrefStringData(this, Constants.DOB));
        mEdtGender.setText(Utility.getSharedPrefStringData(this, Constants.GENDER));
    }

    private void setClickListeners() {
        txtLogout.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgEdit.setOnClickListener(this);
        mTxtChangepassword.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
        mEdtDateofbirth.setOnClickListener(this);
        mEdtGender.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLogout: {

                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    Utility.showLog("Error", "" + e);
                }

                Utility.setSharedPrefStringData(this, Constants.USER_ID, "");
                Utility.setSharedPrefStringData(this, Constants.USER_TOKEN, "");
                Utility.setSharedPrefStringData(this, Constants.FULLNAME, "");
                Utility.setSharedPrefStringData(this, Constants.AUTHARIZATIONID, "");
                Utility.setSharedPrefStringData(this, Constants.EMAIL, "");
                Utility.setSharedPrefStringData(this, Constants.DOB, "");
                Utility.setSharedPrefStringData(this, Constants.GENDER, "");
                Utility.setSharedPrefStringData(this, Constants.PH_NO, "");
                Utility.setSharedPrefStringData(this, Constants.LOGIN_TYPE, "");
                Utility.setSharedPrefStringData(this, Constants.DESCRIPTION, "");
                Utility.setSharedPrefStringData(this, Constants.PROFILE_IMAGE, "");
                startActivity(new Intent(this, LoginActivity.class));
                break;
            }
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgEdit:
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            case R.id.txtChangepassword:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.txtSave:
                if (Utility.isNetworkAvailable(this)) {
                    if (isValidated()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            Utility.showLoadingDialog(this, "Loading...", false);
                            jsonObject.put("userId", "" + Utility.getSharedPrefStringData(this, Constants.USER_ID));
                            jsonObject.put("gender", "" + mEdtGender.getText().toString().trim());
                            jsonObject.put("dateofbirth", "" + mEdtDateofbirth.getText().toString().trim());
                            jsonObject.put("fullname", "" + mEdtName.getText().toString().trim());
                            jsonObject.put("profile", "" + imageString);

                            ServerResponse serverResponse = new ServerResponse();
                            serverResponse.serviceRequest(this, Constants.EDITPROFILE_URL, jsonObject, this, Constants.EDITPROFILE_APICODE);
                        } catch (JSONException e) {
                            Utility.showLog("error", "" + e);
                        }
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }
                break;
            case R.id.edtDateofbirth:
                showDatePicker();
                break;
            case R.id.edtGender:
                selectGender();
                break;
            default:
                break;

        }

    }

    private boolean isValidated() {
        boolean validated = false;
        if (Utility.isValueNullOrEmpty(mEdtName.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Name", null);
            mEdtName.requestFocus();
        } else if (Utility.isValueNullOrEmpty(mEdtDateofbirth.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Select Date Of Birth", null);
            mEdtDateofbirth.requestFocus();
        } else if (Utility.isValueNullOrEmpty(mEdtGender.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Select Gender", null);
            mEdtGender.requestFocus();
        } else {
            validated = true;
        }
        return validated;
    }

    private void showDatePicker() {
        Calendar mCalender = Calendar.getInstance();
        // DatePickerDialog dialog = new DatePickerDialog(this, pDateSetListener, pYear, pMonth, pDay);

        int c_date = mCalender.get(Calendar.DAY_OF_MONTH);
        int c_month = mCalender.get(Calendar.MONTH);
        int c_year = mCalender.get(Calendar.YEAR);
//        if (Utility.isMarshmallowOS()) {

        final DatePickerDialog dpd = new DatePickerDialog(this, android.R.style.ThemeOverlay_Material_Dialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mEdtDateofbirth.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);


                    }
                }, c_year, c_month, c_date);
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                } else {
                    Toast.makeText(this, "Permission was denied. You can't access to camera or gallery", Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:

                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }/*else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }*/
                break;

            default:
                break;
        }
    }

    View.OnClickListener CAMERACLICK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, PHONE_CAMERA_CLICK);
        }
    };


    View.OnClickListener GALLERYCLICK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHONE_GALLERY_CLICK);
        }
    };

    private void selectGender() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.gender_dialog, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(true);
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationexit;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gender);
        ListView lv_selectuser = (ListView) view.findViewById(R.id.lv_stageselect);
        lv_selectuser.setAdapter(adapter);
        lv_selectuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str_gender = parent.getItemAtPosition(position).toString();
                mEdtGender.setText(str_gender);

                alertDialog.dismiss();


            }
        });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHONE_GALLERY_CLICK:
                Uri selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 19) {
                    selectedImagePath = StaticUtils.getPath(this, selectedImageUri);
                    bitmap = BitmapFactory.decodeFile(selectedImagePath);
                    convertBitmapToFile(bitmap);
                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = this.getContentResolver().openFileDescriptor(selectedImageUri, "r");
                        String imagePath = FileUtils.getPath(this, selectedImageUri);
                        bitmap = StaticUtils.getResizeImage(this,
                                StaticUtils.PROFILE_IMAGE_SIZE,
                                StaticUtils.PROFILE_IMAGE_SIZE,
                                ScalingUtilities.ScalingLogic.CROP,
                                true,
                                imagePath,
                                selectedImageUri);
                        convertBitmapToFile(bitmap);
                        parcelFileDescriptor.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PHONE_CAMERA_CLICK:
                if (data != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    convertBitmapToFile(bitmap);
                }
                break;

            default:
                break;
        }
    }

    private void convertBitmapToFile(Bitmap bitmap) {
        if (bitmap != null) {
            mImgUserprofile.setImageBitmap(bitmap);
            imageString = Base64.encodeToString(StaticUtils.getBytesFromBitmap(bitmap), Base64.NO_WRAP);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void ErrorResponse(VolleyError volleyError, int requestCode) {
        Utility.hideLoadingDialog();
        PopUtilities.alertDialog(this, "Something went Wrong... Try Again", null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        Utility.hideLoadingDialog();
        if (requestCode == Constants.EDITPROFILE_APICODE) {
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

                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, ProfileActivity.class));
                    finish();

                } else {
                    PopUtilities.alertDialog(this, jsonObject.optString("message"), null);
                }
            } catch (JSONException e) {
                Utility.showLog("Error Response", "" + e);
            }

        }
    }
}
