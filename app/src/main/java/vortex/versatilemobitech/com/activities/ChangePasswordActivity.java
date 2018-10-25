package vortex.versatilemobitech.com.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.interfaces.IParseListener;
import vortex.versatilemobitech.com.utilities.Constants;
import vortex.versatilemobitech.com.utilities.PopUtilities;
import vortex.versatilemobitech.com.utilities.ServerResponse;
import vortex.versatilemobitech.com.utilities.Utility;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, IParseListener {
    ImageView mImgBack;
    TextView mTxtSubmit;
    EditText mEdtOldPassword, mEdtNewPassword, mEdtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initComponents();

    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    private void setReferences() {
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mTxtSubmit = (TextView) findViewById(R.id.txtSubmit);
        mEdtOldPassword = (EditText) findViewById(R.id.edtOldpassword);
        mEdtNewPassword = (EditText) findViewById(R.id.edtNewpassword);
        mEdtConfirmPassword = (EditText) findViewById(R.id.edtConfirmpassword);

    }

    private void setClickListeners() {
        mImgBack.setOnClickListener(this);
        mTxtSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.txtSubmit:
                if (Utility.isNetworkAvailable(this)) {
                    if (checkValidation()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("_id", "" + Utility.getSharedPrefStringData(this, Constants.USER_ID));
                            jsonObject.put("password", "" + mEdtOldPassword.getText().toString().trim());
                            jsonObject.put("new_password", "" + mEdtNewPassword.getText().toString().trim());

                            Utility.showLoadingDialog(this, "Loading...", false);
                            ServerResponse serverResponse = new ServerResponse();
                            serverResponse.serviceRequest(this, Constants.CHANGE_URL, jsonObject, this, Constants.CHANGEPASSWORD_APICODE);
                        } catch (JSONException e) {
                            Utility.showLog("error", "" + e);
                        }
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }
                break;
            default:
                break;
        }
    }

    private boolean checkValidation() {
        boolean validated = false;
        if (Utility.isValueNullOrEmpty(mEdtOldPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Old Password", null);
            mEdtOldPassword.requestFocus();
       /* } else if (!Utility.isPassword(mEdtOldPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Invalid Old Password, Password  should contain one Capital Letter,Number and Special Character with more than 8 Characters", null);
            mEdtOldPassword.requestFocus();*/
        } else if (Utility.isValueNullOrEmpty(mEdtNewPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter New Password", null);
            mEdtNewPassword.requestFocus();
        } else if (!Utility.isPassword(mEdtNewPassword.getText().toString().trim()).equals("")) {
            PopUtilities.alertDialog(this, Utility.isPassword(mEdtNewPassword.getText().toString().trim()), null);
            mEdtNewPassword.requestFocus();
        } else if (mEdtNewPassword.getText().toString().trim().equals(mEdtOldPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "New Password and Old Password should not be Same", null);
            mEdtConfirmPassword.requestFocus();
        } else if (Utility.isValueNullOrEmpty(mEdtConfirmPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Confirm New Password", null);
            mEdtConfirmPassword.requestFocus();
        } else if (!mEdtNewPassword.getText().toString().trim().equals(mEdtConfirmPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Confirm password doesn’t match with New password", null);
            mEdtConfirmPassword.requestFocus();
        } else {
            validated = true;
        }
        return validated;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void ErrorResponse(VolleyError volleyError, int requestCode) {
        Utility.hideLoadingDialog();
        PopUtilities.alertDialog(this, "Something went wrong... Please try again", null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        Utility.hideLoadingDialog();
        if (requestCode == Constants.CHANGEPASSWORD_APICODE) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Utility.showLog("error", "" + e);
            }
        }
    }
}