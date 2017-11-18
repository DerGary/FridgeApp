package com.example.student.gefriertruhapp.FridgeList;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.Helper.NumberPickerHelper;
import com.example.student.gefriertruhapp.Model.Category;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.OnPropertyChangedListener;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.UPC.GetAsyncTask;
import com.example.student.gefriertruhapp.UPC.JsonResult;
import com.example.student.gefriertruhapp.Helper.TitleFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by student on 21.12.15.
 */
public class FridgeDetailFragment extends TitleFragment implements ItemClickListener, OnPropertyChangedListener {
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
    protected Spinner categorySpinner;
    protected RecyclerView linkedItemsListView;
    private DateTime notificationDateTime;

    private List<Category> categories;
    private List<String> categoryNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void inflateRootView(LayoutInflater inflater, ViewGroup container) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_fridge_detail, container, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflateRootView(inflater, container);

        notificationButton = (Button) rootView.findViewById(R.id.fridge_detail_notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetNotificationClicked();
            }
        });

        name = (EditText) rootView.findViewById(R.id.fridge_detail_name);
        //quantity = (EditText)rootView.findViewById(R.id.fridge_detail_quantity);
        notificationDate = (TextView) rootView.findViewById(R.id.fridge_detail_notification_date);
        quantity = (NumberPicker) rootView.findViewById(R.id.fridge_detail_quantity);
        quantity.setMaxValue(100);
        quantity.setMinValue(0);
        minQuantity = (NumberPicker) rootView.findViewById(R.id.fridge_detail_min_quantity);
        minQuantityLayout = (LinearLayout) rootView.findViewById(R.id.fridge_detail_min_quantity_container);

        minQuantity.setMaxValue(100);
        minQuantity.setMinValue(0);
        minQuantityLayout.setVisibility(View.VISIBLE);

        notes = (EditText) rootView.findViewById(R.id.fridge_detail_note);

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
        categorySpinner = (Spinner) rootView.findViewById(R.id.fridge_detail_category_spinner);

        linkedItemsListView = (RecyclerView)rootView.findViewById(R.id.fridge_detail_linked_items);

        categories = DataBaseSingleton.getInstance().getCategories();
        categoryNames = new ArrayList<>();
        categoryNames.add("Keine");
        categoryNames.add("Neu");
        for(Category category : categories){
            categoryNames.add(category.getName());
        }

        setViewData();

        return rootView;
    }
    private FridgeRecycleViewAdapter recycleViewAdapter;
    private void setViewData() {
        if(item == null){
            return;
        }
        name.setText(item.getName());
        if (item.getNotificationDate() != null) {
            notificationDateTime = item.getNotificationDate();
            notificationDate.setText(formatter.print(item.getNotificationDate()));
        }
        quantity.setValue(item.getQuantity());
        notes.setText(item.getNotes());
        minQuantity.setValue(item.getMinQuantity());


        List<Store> stores = DataBaseSingleton.getInstance().getStores();
        ArrayList<String> listStores = new ArrayList<>();
        for (Store store : stores) {
            listStores.add(store.getName());
        }
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), R.layout.spinner_text_item, listStores);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        storeSpinner.setAdapter(adapter);
        if (item.getStore() != null) {
            storeSpinner.setSelection(listStores.indexOf(item.getStore().getName()));
        }

        setCategorySpinner();
        if(item.getCategory() != null){
            int index = categories.indexOf(item.getCategory());
            categorySpinner.setSelection(index + 2);
        }

        linkedItemsListView.setHasFixedSize(true);
        linkedItemsListView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.recycleViewAdapter = new FridgeRecycleViewAdapter(new FridgeViewHolderBuilder(), Collections.makeList(item.getLinkedItems()), this, null);
        linkedItemsListView.setAdapter(this.recycleViewAdapter);
    }

    private void setCategorySpinner(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), R.layout.spinner_text_item, categoryNames);
// Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if("Neu".equals(categoryNames.get(position))){
                    //create new category
                    showTextInput();
                }else if("Keine".equals(categoryNames.get(position)) && categoryText != null){
                    categoryNames.remove(categoryText);
                    categoryText = null;
                    setCategorySpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getViewData() {
        item.unsubscribe(this); // we need to unsubscribe the events otherwise the view gets refreshed with the old data when setName is called and the other properties are set to the old values.

        item.setName(name.getText().toString());
        item.setQuantity(quantity.getValue());
        item.setNotificationDate(notificationDateTime);
        item.setNotes(notes.getText().toString());
        item.setMinQuantity(minQuantity.getValue());
        long storeId = storeSpinner.getSelectedItemId();
        item.setStore(DataBaseSingleton.getInstance().getStores().get((int) storeId));
        long catpos = categorySpinner.getSelectedItemId();
        if(catpos == 0) {
            // no category
            item.setCategoryId(0);
            item.setCategory(null);
        }else if (categoryText != null) {
            //new category
            Category cat = DataBaseSingleton.getInstance().createNewCategory(categoryText);
            item.setCategory(cat);
            item.setCategoryId(cat.getId());
        }else {
            catpos = catpos-2;
            Category cat = categories.get((int)catpos);
            item.setCategory(cat);
            item.setCategoryId(cat.getId());
        }
    }

    String categoryText;

    private void showTextInput(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kategorie erstellen");

// Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(categoryText != null){
                    categoryNames.remove(categoryNames.size() - 1);
                }
                categoryText = input.getText().toString();
                categoryNames.add(categoryText);
                setCategorySpinner();
                categorySpinner.setSelection(categoryNames.size()-1);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
        if (id == R.id.menu_item_delete) {
            deleteItem();
            getActivity().onBackPressed();
            return true;
        } else if (id == R.id.menu_item_save) {
            saveItem();
            getActivity().onBackPressed();
            return true;
        } else if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (id == R.id.menu_item_remove_links) {
            removeLinks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeLinks() {
        DataBaseSingleton.getInstance().removeLinks(this.item);
    }

    private void deleteItem() {
        DataBaseSingleton.getInstance().deleteItem(this.item);
        DataBaseSingleton.getInstance().saveDataBase();
    }

    private void saveItem() {
        if(item.getId() == -1){//new item
            getViewData();
            DataBaseSingleton.getInstance().updateItem(null, item);
        }else{ //change item
            FridgeItem oldItem = new FridgeItem(item);
            getViewData();
            DataBaseSingleton.getInstance().updateItem(oldItem, item);
        }
        DataBaseSingleton.getInstance().saveDataBase();
    }

    public void setData(FridgeItem item) {
        if(this.item != null){
            this.item.unsubscribe(this);
        }
        this.item = item;
        this.item.subscribe(this);
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

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year + 1, month, day);
        dialog.show();
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
        dialog.show();
    }

    private void loadUPC() {
        if (item.getBarCode() == null) {
            deleteMark.setVisibility(View.VISIBLE);
            return;
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        String upcKey = sharedPref.getString("UPCLOOKUPAPIKEY", "");
        if(upcKey == null || upcKey.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("UPC API Key nicht gefunden");
            builder.setMessage("Für diese Funktion muss ein UPC API Key in den Einstellungen hinterlegt werden. Dieser kann kostenlos auf www.upcdatabase.org erstellt werden.");
            builder.setNeutralButton("Ok", null);
            builder.create().show();
            return;
        }

        GetAsyncTask<JsonResult> loadUPCs = new GetAsyncTask<JsonResult>(getActivity(), upcKey) {
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
                if (result == null || result.getValid().equals("false")) {
                    deleteMark.setVisibility(View.VISIBLE);
                } else {
                    checkMark.setVisibility(View.VISIBLE);
                    String upcName = result.getItemname();
                    String upcAlias = result.getAlias();
                    String upcDescription = result.getDescription();
                    if (upcName != null && !upcName.isEmpty()) {
                        name.setText(upcName);
                        notes.setText(upcAlias + "\r" + upcDescription);
                    } else if (upcAlias != null && !upcAlias.isEmpty()) {
                        name.setText(upcAlias);
                        notes.setText(upcDescription);
                    } else {
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

    @Override
    public void onItemClick(Object data) {
        FridgeDetailFragment fragment = new FridgeDetailFragment();
        fragment.setData((FridgeItem)data);
        ((Dashboard) getActivity()).changeFragment(fragment, true);
    }

    @Override
    public void onPropertyChanged(String name) {
        setViewData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        item.unsubscribe(this);
    }
}
