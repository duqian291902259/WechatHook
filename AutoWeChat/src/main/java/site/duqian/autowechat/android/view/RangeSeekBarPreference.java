package site.duqian.autowechat.android.view;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import site.duqian.autowechat.R;
import site.duqian.autowechat.utils.SPUtils;

public class RangeSeekBarPreference extends DialogPreference {

    private static final String TAG = RangeSeekBarPreference.class.getSimpleName();
    private CrystalRangeSeekbar rangeSeekbar;

    private TextView textView;

    private String hintText = "自动回复";

    private String delayStart, delayEnd;

    private Context context;
    public RangeSeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setDialogLayoutResource(R.layout.preference_rangeseekbar);


        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attr = attrs.getAttributeName(i);
            if (attr.equalsIgnoreCase("start")) {
                delayStart = attrs.getAttributeValue(i);
            } else if (attr.equalsIgnoreCase("end")) {
                delayEnd = attrs.getAttributeValue(i);
            }
        }
        //LogUtils.debug(TAG,"delayStart="+delayStart+",delayEnd="+delayEnd);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        rangeSeekbar = (CrystalRangeSeekbar) view.findViewById(R.id.seekBar);

        //SharedPreferences pref = getSharedPreferences();
        int delayStartValue = SPUtils.getInt(context,delayStart, 0);
        int delayEndValue = SPUtils.getInt(context,delayEnd, 0);

        this.rangeSeekbar.setMinStartValue(delayStartValue).setMaxStartValue(delayEndValue).apply();

        textView = (TextView) view.findViewById(R.id.pref_seekbar_textview);
        textView.setText("立即" + hintText);

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                if (0 == maxValue.intValue()) {
                    textView.setText("立即" + hintText);
                } else if (minValue.equals(maxValue)) {
                    textView.setText("延迟" + minValue + "秒" + hintText);
                } else {
                    textView.setText("随机延迟" + minValue + "-" + maxValue + "秒" + hintText);
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SPUtils.putInt(context,delayStart, rangeSeekbar.getSelectedMinValue().intValue());
            SPUtils.putInt(context,delayEnd, rangeSeekbar.getSelectedMaxValue().intValue());
        }
        super.onDialogClosed(positiveResult);
    }
}
