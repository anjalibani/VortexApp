package vortex.versatilemobitech.com.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by USER on 10-05-2018.
 */


public interface IParseListener {
    void ErrorResponse(VolleyError volleyError, int requestCode);

    void SuccessResponse(String response, int requestCode);
}
