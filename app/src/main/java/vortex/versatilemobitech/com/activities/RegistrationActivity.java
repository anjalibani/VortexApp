package vortex.versatilemobitech.com.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.interfaces.IParseListener;
import vortex.versatilemobitech.com.utilities.Constants;
import vortex.versatilemobitech.com.utilities.PopUtilities;
import vortex.versatilemobitech.com.utilities.ServerResponse;
import vortex.versatilemobitech.com.utilities.Utility;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, IParseListener {
    TextView mTxtRegister, mTxtLogin;
    EditText mEdtFullname, mEdtEmail, mEdtPassword;
    ImageView mImgPassword;
    boolean showhide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }


    private void setReferences() {
        mTxtRegister = (TextView) findViewById(R.id.txtRegister);
        mTxtLogin = (TextView) findViewById(R.id.txtLogin);
        mEdtFullname = (EditText) findViewById(R.id.edtFullname);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mImgPassword = (ImageView) findViewById(R.id.ivPassword);
    }

    private void setClickListeners() {
        mTxtRegister.setOnClickListener(this);
        mTxtLogin.setOnClickListener(this);
        mImgPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLogin:
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                break;
            case R.id.txtRegister:
                if (Utility.isNetworkAvailable(this)) {
                    if (isValidated()) {
                        callServiceForRegistration();
                    }
                } else {
                    PopUtilities.alertDialog(this, Utility.getResourcesString(this, R.string.please_check_internet), null);
                }
                break;
            case R.id.ivPassword:
                if (!showhide) {
                    showhide = true;
                    mImgPassword.setImageResource(R.drawable.password_show);
                    mEdtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showhide = false;
                    mImgPassword.setImageResource(R.drawable.password_hide);
                    mEdtPassword.setInputType(129);
                }
                break;
            default:
                break;
        }
    }

    private void callServiceForRegistration() {
        JSONObject jsonObject = new JSONObject();
        try {
            Utility.showLoadingDialog(this, "Loading...", false);

            jsonObject.put("email", "" + mEdtEmail.getText().toString().trim());
            jsonObject.put("password", "" + mEdtPassword.getText().toString().trim());
            jsonObject.put("fullname", "" + mEdtFullname.getText().toString().trim());

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.serviceRequest(this, Constants.REGISTERATION_URL, jsonObject, this, Constants.REGISTER_APICODE);
        } catch (JSONException e) {
            Utility.showLog("Error", "" + e);
        }
    }

    private boolean isValidated() {
        boolean validated = false;
        if (Utility.isValueNullOrEmpty(mEdtFullname.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Full Name", null);
            mEdtFullname.requestFocus();
        } else if (Utility.isValueNullOrEmpty(mEdtEmail.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Enter Email Id", null);
            mEdtEmail.requestFocus();
        } else if (!Utility.isEmail(mEdtEmail.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Enter Valid Email Id", null);
            mEdtEmail.requestFocus();
        } else if (Utility.isValueNullOrEmpty(mEdtPassword.getText().toString().trim())) {
            PopUtilities.alertDialog(this, "Please Enter Password", null);
            mEdtPassword.requestFocus();
        } else if (!Utility.isPassword(mEdtPassword.getText().toString().trim()).equals("")) {
            PopUtilities.alertDialog(this, Utility.isPassword(mEdtPassword.getText().toString().trim()), null);
            mEdtPassword.requestFocus();
        } else {
            validated = true;
        }
        return validated;
    }

    @Override
    public void ErrorResponse(VolleyError volleyError, int requestCode) {
        Utility.hideLoadingDialog();
        PopUtilities.alertDialog(this, "Something went Wrong... Try Again", null);
    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        Utility.hideLoadingDialog();
        if (requestCode == Constants.REGISTER_APICODE) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
//                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
//                    String fullname = jsonObjectData.optString("fullname");

                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                } else {
                    PopUtilities.alertDialog(this, jsonObject.optString("message"), null);
                }
            } catch (JSONException e) {
                Utility.showLog("Error", "" + e);
            }
        }
    }
}