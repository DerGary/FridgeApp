package com.example.student.gefriertruhapp.History;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.student.gefriertruhapp.Helper.NumberPickerHelper;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Settings.Store;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 11-10-16.
 */
public class HistoryFragment extends TitleFragment {
    private String title;
    private String text;
    private View rootView;
    private TextView textView;

    @Override
    public String getTitle() {
        return title;
    }

    public void setData(String title, String text) {
        this.title = title;
        this.text = text;
        if (rootView != null) {
            setViewData();
        }
    }


    protected void inflateRootView(LayoutInflater inflater, ViewGroup container) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_history, container, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflateRootView(inflater, container);

        textView = (TextView) rootView.findViewById(R.id.history_text);

        setViewData();

        return rootView;
    }

    private void setViewData() {
        textView.setText(text);
    }
}