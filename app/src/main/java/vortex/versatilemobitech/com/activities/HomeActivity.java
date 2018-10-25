package vortex.versatilemobitech.com.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.utilities.Utility;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView mImgChat, mImgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
    }

    private void initComponents() {
        setReferences();
        setClickListeners();
    }

    private void setReferences() {
        mImgChat = (ImageView) findViewById(R.id.imgChat);
        mImgProfile = (ImageView) findViewById(R.id.imgProfile);
    }

    private void setClickListeners() {
        mImgChat.setOnClickListener(this);
        mImgProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgChat:
                startActivity(new Intent(this, MessageActivity.class));
                break;
            case R.id.imgProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showConformationDialog();

    }

    private void showConformationDialog() {
        final Dialog dialogEventConfirmation = new Dialog(this);
        dialogEventConfirmation.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogEventConfirmation.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEventConfirmation.setContentView(R.layout.dialog_exit_confirmation);
        dialogEventConfirmation.getWindow().setGravity(Gravity.CENTER);
        dialogEventConfirmation.setCanceledOnTouchOutside(true);
        //dialogEventConfirmation.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogEventConfirmation.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txt_dialog_message = (TextView) dialogEventConfirmation.findViewById(R.id.txt_dialog_message);
        TextView tv_yes = (TextView) dialogEventConfirmation.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) dialogEventConfirmation.findViewById(R.id.tv_no);


        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEventConfirmation.dismiss();

                finishAffinity();
            }
        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEventConfirmation.dismiss();
            }
        });

        dialogEventConfirmation.show();
    }
}