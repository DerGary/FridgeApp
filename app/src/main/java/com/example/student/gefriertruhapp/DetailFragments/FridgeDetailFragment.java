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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.student.gefriertruhapp.Helper.NumberPickerHelper;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Settings.Store;
import com.example.student.gefriertruhapp.UPC.GetAsyncTask;
import com.example.student.gefriertruhapp.UPC.JsonResult;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    protected Spinner storeSpinner;
    private DateTime notificationDateTime;

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
        minQuantityLayout.setVisibility(View.VISIBLE);

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

        storeSpinner = (Spinner) rootView.findViewById(R.id.fridge_detail_store_spinner);

        setViewData();

        return rootView;
    }

    private void setViewData(){
        name.setText(item.getName());
        if(item.getNotificationDate() != null) {
            notificationDateTime = item.getNotificationDate();
            notificationDate.setText(formatter.print(item.getNotificationDate()));
        }
        quantity.setValue(item.getQuantity());
        notes.setText(item.getNotes());
        minQuantity.setValue(item.getMinQuantity());


        List<Store> stores = DataBaseSingleton.getInstance().getStores();
        ArrayList<String> listStores = new ArrayList<>();
        for(Store store : stores){
            listStores.add(store.getName());
        }
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, listStores);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        storeSpinner.setAdapter(adapter);
        if(item.getStore() != null){
            storeSpinner.setSelection(listStores.indexOf(item.getStore().getName()));
        }
    }

    private void getViewData(){
        item.setName(name.getText().toString());
        item.setQuantity(quantity.getValue());
        item.setNotificationDate(notificationDateTime);
        item.setNotes(notes.getText().toString());
        item.setMinQuantity(minQuantity.getValue());
        long storeId = storeSpinner.getSelectedItemId();
        item.setStore(DataBaseSingleton.getInstance().getStores().get((int)storeId));
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
            DataBaseSingleton.getInstance().deleteItem(this.item);
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();
            return true;
        }else if(id == R.id.menu_item_save){
            DataBaseSingleton.getInstance().deleteItem(this.item);
            getViewData();
            DataBaseSingleton.getInstance().saveItem(this.item);
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
        return "Details";
    }
    private void onSetNotificationClicked() {
        showDatePicker(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                showTimePicker(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        notificationDateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
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

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year+1, month, day);
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
        if(item.getBarCode() == null){
            deleteMark.setVisibility(View.VISIBLE);
            return;
        }

        String savedName = DataBaseSingleton.getInstance().getNameByBarcode(item.getBarCode());
        if(savedName != null){
            name.setText(savedName);
        }
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
                    String upcName = result.getItemname();
                    String upcAlias = result.getAlias();
                    String upcDescription = result.getDescription();
                    if(upcName != null && !upcName.isEmpty()){
                        name.setText(upcName);
                        notes.setText(upcAlias + "\r" + upcDescription);
                    }else if(upcAlias != null && !upcAlias.isEmpty()){
                        name.setText(upcAlias);
                        notes.setText(upcDescription);
                    }else{
                        name.setText(upcDescription);
                    }
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
