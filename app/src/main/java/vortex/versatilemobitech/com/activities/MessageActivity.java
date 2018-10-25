package vortex.versatilemobitech.com.activities;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.utilities.Constants;
import vortex.versatilemobitech.com.utilities.Utility;

public class MessageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 21;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, uploadImage, hometab;
    EditText messageArea;
    //profiletab
    ScrollView scrollView;
    Firebase reference1, reference2;
    String user, doctor;
    ImageView ivBack;


    //a Uri object to store file path
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initUI();
    }

    private void initUI() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);


        hometab = (ImageView) findViewById(R.id.home);

        Firebase.setAndroidContext(this);
        try {
            Utility.showLoadingDialog(this, "loading", false);
            user = Utility.getSharedPrefStringData(this, Constants.USER_ID); //userSession.getUserDetails().get("userid");//send userId not username
            Log.e("USER", "" + user);
            doctor = "doctor";//userSession.getUserDetails().get("userpassword")
            reference1 = new Firebase("https://vortex-35279.firebaseio.com/" + user);//+ UserDetails.username + "_" + UserDetails.chatWith);//"https://vortex-35279.firebaseio.com/main"
            reference2 = new Firebase("https://vortex-35279.firebaseio.com/" + doctor);//+ UserDetails.chatWith + "_" + UserDetails.username);//"https://vortex-35279.firebaseio.com/versatile"
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Utility.hideKeyboard(MessageActivity.this);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("usertype", user);//userSession.getUserDetails().get("userid"));//UserDetails.username);"hello"
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);

                }
                messageArea.setText("");
            }
        });


        reference1.addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                String message = map.get("message").toString();
                String userName = map.get("usertype").toString();
                Log.e("USERNAME1", "USERNAME1" + userName);
                Log.e("USERNAME2", "USERNAME2" + user);
                if (userName.equals("doctor")) {
                    Log.e("USERNAME3", "USERNAME3" + userName);
                    addMessageBox(message, 2);
                } else {
                    addMessageBox(message, 1);
                }
                Utility.hideLoadingDialog();
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(MessageActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            Log.e("TYPE1", "" + type);
            lp2.gravity = Gravity.RIGHT;
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setPadding(20, 20, 20, 20);
            textView.setBackgroundResource(R.drawable.verysmallchatright);
        } else {
            Log.e("TYPE2", "" + type);
            lp2.gravity = Gravity.LEFT;
            textView.setTextColor(Color.parseColor("#FF8ACCE6"));
            textView.setPadding(20, 20, 20, 20);
            textView.setBackgroundResource(R.drawable.verysmallleftchat);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        // scrollView.fullScroll(View.FOCUS_DOWN);
        //scrollView.scrollTo(0, scrollView.getBottom());
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}