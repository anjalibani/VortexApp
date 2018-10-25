package vortex.versatilemobitech.com.utilities;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vortex.versatilemobitech.com.interfaces.IParseListener;


public class ServerResponse {

    private int requestCode;
    private RequestQueue requestQueue;

    public void serviceRequest(Context mContext, final String url, final JSONObject params,
                               final IParseListener iParseListener, final int requestCode) {
        this.requestCode = requestCode;

        try {
            Utility.showLog("Params is", "" + params);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Utility.showLog("response is", "" + response);
                    iParseListener.SuccessResponse("" + response, requestCode);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utility.showLog("Error is", "" + error);
                    iParseListener.ErrorResponse(error, requestCode);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(mContext);
            }
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Utility.showLog("Error", "" + e);
        }
    }
}