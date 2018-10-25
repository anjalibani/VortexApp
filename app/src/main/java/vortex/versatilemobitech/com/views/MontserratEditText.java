package vortex.versatilemobitech.com.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import vortex.versatilemobitech.com.R;


public class MontserratEditText extends AppCompatEditText {

    public MontserratEditText(Context context) {
        super(context);
    }

    public MontserratEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, R.attr.font_type);
    }

    public MontserratEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        if (isInEditMode())
            return;
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MontserratTextView);

            String str = a.getString(R.styleable.MontserratTextView_font_type);
            if (str == null)
                str = "1";
            a.recycle();
            switch (Integer.parseInt(str)) {
                case 1:
                    str = "Montserrat-Regular.ttf";
                    break;
                case 2:
                    str = "Montserrat-Bold.ttf";
                    break;
                default:
                    str = "Montserrat-Regular.ttf";
                    break;
            }
            setTypeface(FontManager.getInstance(getContext()).loadFont(str));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

