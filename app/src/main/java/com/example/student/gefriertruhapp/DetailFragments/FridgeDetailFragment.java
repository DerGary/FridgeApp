package com.example.student.gefriertruhapp.DetailFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.student.gefriertruhapp.Helper.NumberPickerHelper;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.UPC.GetAsyncTask;
import com.example.student.gefriertruhapp.UPC.JsonResult;
import com.example.student.gefriertruhapp.ViewPager.PageType;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Created by student on 21.12.15.
 */
public class FridgeDetailFragment extends TitleFragment {
    protected FridgeItem item;
    protected View rootView;
    protected Button notificationButton;
    protected EditText name, notes;
    protected TextView notificationDate;
    protected DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm - dd.MM.yyyy");
    protected NumberPicker quantity, minQuantity;
    protected ImageButton searchButton;
    protected ProgressBar progressBar;
    protected ImageView deleteMark, checkMark;
    protected LinearLayout minQuantityLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void inflateRootView(LayoutInflater inflater, ViewGroup container){
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_fridge_detail, container, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflateRootView(inflater, container);

        notificationButton = (Button)rootView.findViewById(R.id.fridge_detail_notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetNotificationClicked();
            }
        });

        name = (EditText) rootView.findViewById(R.id.fridge_detail_name);
        //quantity = (EditText)rootView.findViewById(R.id.fridge_detail_quantity);
        notificationDate = (TextView) rootView.findViewById(R.id.fridge_detail_notification_date);
        quantity = (NumberPicker)rootView.findViewById(R.id.fridge_detail_quantity);
        quantity.setMaxValue(100);
        quantity.setMinValue(0);
        minQuantity = (NumberPicker)rootView.findViewById(R.id.fridge_detail_min_quantity);
        minQuantityLayout = (LinearLayout) rootView.findViewById(R.id.fridge_detail_min_quantity_container);

        minQuantity.setMaxValue(100);
        minQuantity.setMinValue(0);
        if(item instanceof ShelfItem){
            minQuantityLayout.setVisibility(View.VISIBLE);
        }
        notes = (EditText)rootView.findViewById(R.id.fridge_detail_note);

        NumberPickerHelper.setDividerColor(quantity, new ColorDrawable(getResources().getColor(R.color.material_deep_teal_200)));
        NumberPickerHelper.setDividerColor(minQuantity, new ColorDrawable(getResources().getColor(R.color.material_deep_teal_200)));
        //NumberPickerHelper.setClickAvoid(quantity);
        //NumberPickerHelper.setClickAvoid(minQuantity);

        searchButton = (ImageButton) rootView.findViewById(R.id.fridge_detail_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUPC();
            }
        });

        progressBar = (ProgressBar) rootView.findViewById(R.id.fridge_detail_progressbar);
        deleteMark = (ImageView) rootView.findViewById(R.id.fridge_detail_delete);
        checkMark = (ImageView) rootView.findViewById(R.id.fridge_detail_check_mark);

        setViewData();

        return rootView;
    }

    private void setViewData(){
        name.setText(item.getName());
        if(item.getNotificationDate() != null) {
            notificationDate.setText(formatter.print(item.getNotificationDate()));
        }
        quantity.setValue(item.getQuantity());
        notes.setText(item.getNotes());
        if(item instanceof ShelfItem){
            minQuantity.setValue(((ShelfItem) item).getMinQuantity());
        }
    }

    private void getViewData(){
        item.setName(name.getText().toString());
        item.setQuantity(quantity.getValue());
        item.setNotes(notes.getText().toString());
        if(item instanceof ShelfItem) {
            ((ShelfItem) item).setMinQuantity(minQuantity.getValue());
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getTitle());
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_item_delete){
            DataBaseSingleton.getInstance().deleteFridgeItem(this.item);
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();
            return true;
        }else if(id == R.id.menu_item_save){
            getViewData();
            DataBaseSingleton.getInstance().saveFridgeItem(this.item);
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();
            return true;
        } else if(id == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setData(FridgeItem item){
        this.item = item;
    }

    @Override
    public String getTitle() {
        if(item instanceof ShelfItem){
            return PageType.ShelfList.toString();
        }
        return PageType.FridgeList.toString();
    }

    private void onSetNotificationClicked() {
        showDatePicker(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                showTimePicker(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        DateTime notificationDateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);


                        item.setNotificationDate(notificationDateTime);

                        TextView text = (TextView) rootView.findViewById(R.id.fridge_detail_notification_date);
                        text.setText(formatter.print(notificationDateTime));
                    }
                });
            }
        });
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        dialog.show();
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
        dialog.show();
    }

    private void loadUPC(){
        GetAsyncTask<JsonResult> loadUPCs = new GetAsyncTask<JsonResult>(getActivity()) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                deleteMark.setVisibility(View.GONE);
                checkMark.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(JsonResult result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.GONE);
                if(result == null || result.getValid().equals("false")){
                    deleteMark.setVisibility(View.VISIBLE);

                } else {
                    checkMark.setVisibility(View.VISIBLE);
                    name.setText(result.getItemname());
                    notes.setText(result.getAlias()+ "\r" + result.getDescription());
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                progressBar.setVisibility(View.GONE);
                deleteMark.setVisibility(View.VISIBLE);
            }
        };
        loadUPCs.execute(item.getBarCode());
    }
}
