package vortex.versatilemobitech.com.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, IParseListener {
    ImageView mImgBack, mImgProfile, mImgFirst, mImgSecond, mImgThird, mImgFourth, mImgFifth;
    TextView mTxtName, mTxtSave;
    EditText mEdtDescription;
    LinearLayout mLlEditProfile;
    private String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private static final int LONG_DELAY = 15000;
    private static final int PHONE_GALLERY_CLICK = 1001;
    private static final int PHONE_CAMERA_CLICK = 1002;
    private String selectedImagePath;
    private Bitmap bitmap;
    private Uri uri;
    private String imageString = "";
    String selectedImageView;
    String base64One, base64Two, base64Three, base64Four, base64Fifth;
    JSONArray jsonArray;
    ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initComponents();
    }

    private void initComponents() {
        jsonArray = new JSONArray();
        selectedImageView = "";
        base64One = "";
        base64Two = "";
        base64Three = "";
        base64Four = "";
        base64Fifth = "";
        setReferences();
        setClickListeners();
        callServiceForGetProfile();
    }

    private void callServiceForGetProfile() {
        if (Utility.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", "" + Utility.getSharedPrefStringData(this, Constants.USER_ID));
                Utility.showLoadingDialog(this, "Loading", false);
                ServerResponse serverResponse = new ServerResponse();
                serverResponse.serviceRequest(this, Constants.VIEWPROFILE_URL, jsonObject, this, Constants.VIEWPROFILE_APICODE);
            } catch (JSONException e) {
                Utility.showLog("Error", "" + e);
            }
        } else {
            PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
        }
    }


    private void setReferences() {
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgProfile = (ImageView) findViewById(R.id.imgProfile);
        mImgFirst = (ImageView) findViewById(R.id.imgFirst);
        mImgSecond = (ImageView) findViewById(R.id.imgSecond);
        mImgThird = (ImageView) findViewById(R.id.imgThird);
        mImgFourth = (ImageView) findViewById(R.id.imgFourth);
        mImgFifth = (ImageView) findViewById(R.id.imgFifth);
        mTxtName = (TextView) findViewById(R.id.txtprofilename);
        mTxtSave = (TextView) findViewById(R.id.txtSave);
        mEdtDescription = (EditText) findViewById(R.id.edtDecription);
        mLlEditProfile = (LinearLayout) findViewById(R.id.llEditprofile);

        mImgFirst.setEnabled(true);
        mImgSecond.setEnabled(true);
        mImgThird.setEnabled(true);
        mImgFourth.setEnabled(true);
        mImgFifth.setEnabled(true);

        if (!Utility.isValueNullOrEmpty(Utility.getSharedPrefStringData(this, Constants.PROFILE_IMAGE))) {
            Picasso.get().load(Utility.getSharedPrefStringData(this, Constants.PROFILE_IMAGE))
                    .placeholder(R.drawable.profileholder).into(mImgProfile);
        }

        mTxtName.setText(Utility.getSharedPrefStringData(this, Constants.FULLNAME));
    }

    private void setClickListeners() {
        mImgFirst.setOnClickListener(this);
        mImgSecond.setOnClickListener(this);
        mImgThird.setOnClickListener(this);
        mImgFourth.setOnClickListener(this);
        mImgFifth.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
        mLlEditProfile.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.llEditprofile:
                startActivity(new Intent(this, EditProfileActivity.class));
                break;
            case R.id.txtSave:
                jsonArray = new JSONArray();
                if (!Utility.isValueNullOrEmpty(base64One)) {
                    jsonArray.put(base64One);
                }
                if (!Utility.isValueNullOrEmpty(base64Two)) {
                    jsonArray.put(base64Two);
                }
                if (!Utility.isValueNullOrEmpty(base64Three)) {
                    jsonArray.put(base64Three);
                }
                if (!Utility.isValueNullOrEmpty(base64Four)) {
                    jsonArray.put(base64Four);
                }
                if (!Utility.isValueNullOrEmpty(base64Fifth)) {
                    jsonArray.put(base64Fifth);
                }
                if (Utility.isNetworkAvailable(this)) {

                    Utility.showLoadingDialog(this, "Loading...", false);
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("userId", Utility.getSharedPrefStringData(this, Constants.USER_ID));
                        jsonObject.put("description", "" + mEdtDescription.getText().toString().trim());
                        jsonObject.put("images_info", jsonArray);

                        ServerResponse serverResponse = new ServerResponse();
                        serverResponse.serviceRequest(this, Constants.UPLOAD_URL, jsonObject, this, Constants.UPLOADIMAGES_APICODE);
                    } catch (JSONException e) {
                        Utility.showLog("Error", "" + e);
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }
                break;
            case R.id.imgFirst:
                selectedImageView = "one";
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            case R.id.imgSecond:
                selectedImageView = "two";
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            case R.id.imgThird:
                selectedImageView = "three";
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            case R.id.imgFourth:
                selectedImageView = "four";
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            case R.id.imgFifth: {
                selectedImageView = "five";
                if (!Utility.hasPermissions(this, PERMISSIONS)) {
                    Log.e("First", "You Clicked on");
                    requestPermissions(PERMISSIONS, 1);
                } else {
                    Log.e("Second", "You Clicked on Second");
                    PopUtilities.cameraDialog(this, CAMERACLICK, GALLERYCLICK);
                }
                break;
            }
            default:
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHONE_GALLERY_CLICK:
              /*  Uri selectedImageUri = data.getData();
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
                }*/
                Bitmap bm = null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
                        convertBitmapToFile(bm);
                    } catch (Exception e) {
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
            //mImgUserprofile.setImageBitmap(bitmap);
            imageString = Base64.encodeToString(StaticUtils.getBytesFromBitmap(bitmap), Base64.NO_WRAP);
//            jsonArray.put(imageString);
            switch (selectedImageView) {
                case "one": {
                    mImgFirst.setImageBitmap(bitmap);
                    base64One = imageString;
                    break;
                }
                case "two": {
                    mImgSecond.setImageBitmap(bitmap);
                    base64Two = imageString;
                    break;
                }
                case "three": {
                    mImgThird.setImageBitmap(bitmap);
                    base64Three = imageString;
                    break;
                }
                case "four": {
                    mImgFourth.setImageBitmap(bitmap);
                    base64Four = imageString;
                    break;
                }
                case "five": {
                    mImgFifth.setImageBitmap(bitmap);
                    base64Fifth = imageString;
                    break;
                }
                default:
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void ErrorResponse(VolleyError volleyError, int requestCode) {
        Utility.hideLoadingDialog();
        PopUtilities.alertDialog(this, "Something went wrong... Try Again", null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        Utility.hideLoadingDialog();
        if (requestCode == Constants.UPLOADIMAGES_APICODE) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
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
        } else if (requestCode == Constants.VIEWPROFILE_APICODE) {
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

                    mEdtDescription.setText(Utility.getSharedPrefStringData(this, Constants.DESCRIPTION));
                    mTxtName.setText(Utility.getSharedPrefStringData(this, Constants.FULLNAME));

                    Picasso.get().load(Utility.getSharedPrefStringData(this, Constants.PROFILE_IMAGE))
                            .placeholder(R.drawable.profileholder)
                            .error(R.drawable.profileholder)
                            .into(mImgProfile);


                    switch (imageModelArrayList.size()) {
                        case 1:
                            Utility.showLog("img1", "" + imageModelArrayList.get(0).getImageUrl());
                            Picasso.get().load(imageModelArrayList.get(0).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFirst);
                            mImgFirst.setEnabled(false);
                            break;
                        case 2:
                            Utility.showLog("img1", "" + imageModelArrayList.get(0).getImageUrl());
                            Utility.showLog("img2", "" + imageModelArrayList.get(1).getImageUrl());

                            Picasso.get().load(imageModelArrayList.get(0).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFirst);
                            Picasso.get().load(imageModelArrayList.get(1).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgSecond);
                            mImgFirst.setEnabled(false);
                            mImgSecond.setEnabled(false);
                            break;

                        case 3:
                            Utility.showLog("img1", "" + imageModelArrayList.get(0).getImageUrl());
                            Utility.showLog("img2", "" + imageModelArrayList.get(1).getImageUrl());
                            Utility.showLog("img3", "" + imageModelArrayList.get(2).getImageUrl());

                            Picasso.get().load(imageModelArrayList.get(0).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFirst);
                            Picasso.get().load(imageModelArrayList.get(1).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgSecond);
                            Picasso.get().load(imageModelArrayList.get(2).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgThird);

                            mImgFirst.setEnabled(false);
                            mImgSecond.setEnabled(false);
                            mImgThird.setEnabled(false);
                            break;
                        case 4:
                            Utility.showLog("img1", "" + imageModelArrayList.get(0).getImageUrl());
                            Utility.showLog("img2", "" + imageModelArrayList.get(1).getImageUrl());
                            Utility.showLog("img3", "" + imageModelArrayList.get(2).getImageUrl());
                            Utility.showLog("img4", "" + imageModelArrayList.get(3).getImageUrl());


                            Picasso.get().load(imageModelArrayList.get(0).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFirst);
                            Picasso.get().load(imageModelArrayList.get(1).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgSecond);
                            Picasso.get().load(imageModelArrayList.get(2).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgThird);
                            Picasso.get().load(imageModelArrayList.get(3).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFourth);

                            mImgFirst.setEnabled(false);
                            mImgSecond.setEnabled(false);
                            mImgThird.setEnabled(false);
                            mImgFourth.setEnabled(false);
                            break;
                        case 5:
                            Utility.showLog("img1", "" + imageModelArrayList.get(0).getImageUrl());
                            Utility.showLog("img2", "" + imageModelArrayList.get(1).getImageUrl());
                            Utility.showLog("img3", "" + imageModelArrayList.get(2).getImageUrl());
                            Utility.showLog("img4", "" + imageModelArrayList.get(3).getImageUrl());
                            Utility.showLog("img5", "" + imageModelArrayList.get(4).getImageUrl());


                            Picasso.get().load(imageModelArrayList.get(0).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFirst);
                            Picasso.get().load(imageModelArrayList.get(1).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgSecond);
                            Picasso.get().load(imageModelArrayList.get(2).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgThird);
                            Picasso.get().load(imageModelArrayList.get(3).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFourth);
                            Picasso.get().load(imageModelArrayList.get(4).getImageUrl())
                                    .placeholder(R.drawable.pictureimg).into(mImgFifth);

                            mImgFirst.setEnabled(false);
                            mImgSecond.setEnabled(false);
                            mImgThird.setEnabled(false);
                            mImgFourth.setEnabled(false);
                            mImgFifth.setEnabled(false);
                            break;
                        default:
                            break;
                    }
                } else {
                    PopUtilities.alertDialog(this, jsonObject.optString("message"), null);
                }
            } catch (JSONException e) {
                Utility.showLog("Error", "" + e);
            }
        }
    }
}