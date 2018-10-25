package vortex.versatilemobitech.com.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import vortex.versatilemobitech.com.R;
import vortex.versatilemobitech.com.interfaces.ReturnValue;

/**
 * Created by uday kiran on 10-04-2017.
 */

public class PopUtilities {

    public static Dialog dialog;

    public static void dialogListShow(Context mContext, ArrayList arrayList, String mTitle, final EditText mEditText) {
        if (Utility.isMarshmallowOS()) {
            dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
        } else {
            dialog = new Dialog(mContext);
        }
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorWhite);
        dialog.setContentView(R.layout.dialog_popup);
        dialog.setTitle(mTitle);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        "OnCancelListener",
//                        Toast.LENGTH_LONG).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        "OnDismissListener",
//                        Toast.LENGTH_LONG).show();
            }
        });

//Prepare ListView in dialog
        ListView dialog_ListView = (ListView) dialog.findViewById(R.id.dialogNationality_listview);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, arrayList);
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        parent.getItemAtPosition(position).toString() + " clicked",
//                        Toast.LENGTH_LONG).show();
//                value = parent.getItemAtPosition(position).toString();
                mEditText.setText(parent.getItemAtPosition(position).toString());
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public static void dialogListShowTextView(Context mContext, ArrayList arrayList, String mTitle, final TextView mTextView,
                                              final ReturnValue returnValue) {
        if (Utility.isMarshmallowOS()) {
            dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
        } else {
            dialog = new Dialog(mContext);
        }
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorWhite);
        dialog.setContentView(R.layout.dialog_popup);
        dialog.setTitle(mTitle);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        "OnCancelListener",
//                        Toast.LENGTH_LONG).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        "OnDismissListener",
//                        Toast.LENGTH_LONG).show();
            }
        });

//Prepare ListView in dialog
        ListView dialog_ListView = (ListView) dialog.findViewById(R.id.dialogNationality_listview);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, arrayList);
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
// TODO Auto-generated method stub
//                Toast.makeText(RegisterActivity.this,
//                        parent.getItemAtPosition(position).toString() + " clicked",
//                        Toast.LENGTH_LONG).show();
//                value = parent.getItemAtPosition(position).toString();

                returnValue.returnValues(parent.getItemAtPosition(position).toString(), position);

                mTextView.setText(parent.getItemAtPosition(position).toString());
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public static void dialogListReturnValue(Context mContext, List arrayList, String mTitle,
                                             final TextView mTextView, final ReturnValue returnValue) {
        if (Utility.isMarshmallowOS()) {
            dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
        } else {
            dialog = new Dialog(mContext);
        }
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorWhite);
        dialog.setContentView(R.layout.dialog_popup);
        dialog.setTitle(mTitle);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
// TODO Auto-generated method stub
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
// TODO Auto-generated method stub
            }
        });

//Prepare ListView in dialog
        ListView dialog_ListView = (ListView) dialog.findViewById(R.id.dialogNationality_listview);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, arrayList);
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                returnValue.returnValues(parent.getItemAtPosition(position).toString(), position);

                mTextView.setText(parent.getItemAtPosition(position).toString());
                dialog.dismiss();

            }
        });
        dialog.show();
    }



    public static void alertDialog(final Context mContext, String message, final View.OnClickListener okClick) {
        TextView mTxtOk, mTxtMessage;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog, null);
        mTxtOk = (TextView) v.findViewById(R.id.txtNo);
        mTxtMessage = (TextView) v.findViewById(R.id.txtMessage);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtMessage.setText(message);
        mTxtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (okClick != null) {
                    okClick.onClick(v);
                }
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(false);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);
        dialog.show();
    }
    public static void cameraDialog(final Context mContext, final View.OnClickListener cameraClick,
                                    final View.OnClickListener galleryClick) {
        TextView mTxtCamera, mTxtGallery, mTxtCancel;
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(mContext).inflate(R.layout.alert_camera, null);
        mTxtCamera = (TextView) v.findViewById(R.id.txtCamera);
        mTxtGallery = (TextView) v.findViewById(R.id.txtGallery);
        mTxtCancel = (TextView) v.findViewById(R.id.txtCancel);

        dialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mTxtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (cameraClick != null)
                    cameraClick.onClick(v);
            }
        });
        mTxtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (galleryClick != null)
                    galleryClick.onClick(v);
            }
        });

        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(v);
        dialog.setCancelable(true);

        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.30);
        dialog.getWindow().setLayout(width, lp.height);

        dialog.show();
    }

    public static String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }
}
