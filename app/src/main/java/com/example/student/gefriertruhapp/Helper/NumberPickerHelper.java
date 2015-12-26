package com.example.student.gefriertruhapp.Helper;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by student on 23.12.15.
 */
public class NumberPickerHelper {
    public static void setDividerColor(NumberPicker picker, ColorDrawable color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {

                    pf.set(picker, color);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void setClickAvoid(NumberPicker picker){
        picker.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE && v.getParent() != null) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }

                v.onTouchEvent(event);
                return true;
            }
        });
    }

}
