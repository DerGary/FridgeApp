package com.example.student.gefriertruhapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.student.gefriertruhapp.DetailFragments.FridgeDetailFragment;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.Notifications.Notifier;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.example.student.gefriertruhapp.ViewPager.PageType;
import com.example.student.gefriertruhapp.ViewPager.ViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends ActionBarActivity implements SearchView.OnQueryTextListener {
    //Actions
    public static final String SCAN = "la.droid.qr.scan";
    public static final String ENCODE = "la.droid.qr.encode";
    public static final String DECODE = "la.droid.qr.decode";

    //Parameters
    //SCAN / DECODE
    public static final String COMPLETE = "la.droid.qr.complete"; //Default: false
    //ENCODE
    public static final String CODE =  "la.droid.qr.code"; //Required
    public static final String SIZE = "la.droid.qr.size"; //Default: Fit screen
    //ENCODE / DECODE
    public static final String IMAGE =  "la.droid.qr.image"; //Default for encode: false / Required for decode

    //Result
    public static final String RESULT = "la.droid.qr.result";

    private static final int ACTIVITY_RESULT_QRDROID_ADD = 500;
    private static final int ACTIVITY_RESULT_QRDROID_DEL = 600;
    private ViewPagerFragment _viewPagerFragment;
    private SharedPrefManager sharedPrefManager;

    private Menu _menu;
    private SearchView mSearchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);


        createPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        _menu = menu;

        searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

        getSupportActionBar().setTitle("Gefriertruhen App");
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml. 
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        } else
        if(id == R.id.add){
            openQRDroid(ACTIVITY_RESULT_QRDROID_ADD);
        } else if(id == R.id.delete){
            openQRDroid(ACTIVITY_RESULT_QRDROID_DEL);
        } else if(id == R.id.new_item){
            addItemWithoutQRCode();
        } else if(id == R.id.action_search){
            mSearchView.setIconified(false);
            return true;
        }else{
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openQRDroid(int requestCode){
        //Create a new Intent to send to QR Droid
        Intent qrDroid = new Intent( SCAN ); //Set action "la.droid.qr.scan"

        //Send intent and wait result
        try {
            startActivityForResult(qrDroid, requestCode);
        } catch (ActivityNotFoundException activity) {
            qrDroidRequired(this);
        }
    }

    private void addItemWithoutQRCode(){
        FridgeItem item;
        if(_viewPagerFragment.currentView() == PageType.FridgeList.getI()){
            item = new FridgeItem(sharedPrefManager.getNewID(), "", 1, null, null, "", 1);
        } else {
            item = new ShelfItem(sharedPrefManager.getNewID(), "", 1, null, null, "", 1);
        }
        navigateToDetailFragment(item);
    }

    /**
     * Display a message stating that QR Droid is requiered, and lets the user download it for free
     * @param activity
     */
    public static void qrDroidRequired( final Activity activity ) {
        //Apparently, QR Droid is not installed, or it's previous to version 3.5
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Kein QR Droid")
                .setCancelable(true)
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Google Play", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=la.droid.qr")));
                    }
                })
                .setNeutralButton("QR Droid Download", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://droid.la/apk/qr/")));
                    }
                });
        builder.create().show();
    }

    public void changeFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(
                R.animator.slide_in_from_right, R.animator.slide_out_to_left, R.animator.slide_in_from_left, R.animator.slide_out_to_right
        ).replace(R.id.main_layout, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.getExtras() != null) {
            String barCode = data.getExtras().getString(RESULT);

            if(requestCode == ACTIVITY_RESULT_QRDROID_ADD){
                addElement(barCode);
            }
            else if(requestCode == ACTIVITY_RESULT_QRDROID_DEL){
                delElement(barCode);
            }

        }
    }

    int itemPos;
    public void addElement(String barCode){
        int i = _viewPagerFragment.currentView();

        if(i == PageType.FridgeList.getI()){
            List<FridgeItem> item = DataBaseSingleton.getInstance().getFridgeItems(barCode);
            if (item != null) {
                showChooseArticleDialog(true, item, barCode);

            } else {
                FridgeItem fridgeItem = new FridgeItem(sharedPrefManager.getNewID(), barCode, 1, null, barCode, null, 1);
                navigateToDetailFragment(fridgeItem);
            }
        } else {
            List<FridgeItem> item = (List<FridgeItem>)(List<?>)DataBaseSingleton.getInstance().getShelfItems(barCode);
            if (item != null) {
                showChooseArticleDialog(true, item, barCode);
            } else {
                ShelfItem shelfItem = new ShelfItem(sharedPrefManager.getNewID(), barCode, 1, null, barCode, null, 1);
                navigateToDetailFragment(shelfItem);
            }
        }
    }

    public void showChooseArticleDialog(final boolean add, final List<FridgeItem> items, final String barcode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Artikel wählen");
        List<String> strings = new ArrayList<>();
        for(FridgeItem fridgeItem : items){
            strings.add(fridgeItem.getNotificationDateString());
        }
        if(add) {
            strings.add("Neu");
        }
        itemPos = 0;
        builder.setSingleChoiceItems(strings.toArray(new String[strings.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPos = which;
            }
        });
        builder.setNegativeButton("Abbrechen", null);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(itemPos >= items.size()){
                    FridgeDetailFragment fragment = new FridgeDetailFragment();
                    FridgeItem item;
                    int i = _viewPagerFragment.currentView();
                    if(i == PageType.FridgeList.getI()){
                        item = new FridgeItem(sharedPrefManager.getNewID(), barcode, 1, null, barcode, null, 1);
                    }else{
                        item = new ShelfItem(sharedPrefManager.getNewID(), barcode, 1, null, barcode, null, 1);
                    }
                    fragment.setData(item);
                    changeFragment(fragment, true);
                }else {
                    showChooseQuantity(add, items, itemPos);
                }
            }
        });
        builder.create().show();
    }
    int numberPickerValue;
    public void showChooseQuantity(final boolean add, final List<FridgeItem> items,final int itemPosition){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(R.layout.dialog_number_picker);
        b.setTitle("Anzahl wählen");
        b.setNegativeButton("Abbrechen", null);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FridgeItem item = items.get(itemPosition);
                if (add) {
                    item.setQuantity(item.getQuantity() + numberPickerValue);
                } else {
                    item.setQuantity(item.getQuantity() - numberPickerValue);
                }

                int i = _viewPagerFragment.currentView();
                if (i == PageType.FridgeList.getI()) {
                    DataBaseSingleton.getInstance().saveFridgeItem(item);
                } else {
                    DataBaseSingleton.getInstance().saveShelfItem((ShelfItem) item);
                }
                DataBaseSingleton.getInstance().saveDataBase();
                _viewPagerFragment.setData();

            }
        });
        AlertDialog dialog = b.create();
        dialog.show();

        NumberPicker picker = (NumberPicker)dialog.findViewById(R.id.dialog_number_picker_picker);
        picker.setMaxValue(100);
        picker.setMinValue(1);
        numberPickerValue = 1;
        picker.setValue(1);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberPickerValue = newVal;
            }
        });
    }


    public void delElement(String barCode){
        List<FridgeItem> items;
        int i = _viewPagerFragment.currentView();
        if(i == PageType.FridgeList.getI()) {
            items = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        }else{
            items = (List<FridgeItem>)(List<?>)DataBaseSingleton.getInstance().getShelfItems(barCode);
        }
        if(items != null && items.size() > 0){
            if(items.size() == 1) {
                showChooseQuantity(false, items, 0);
            }else {
                showChooseArticleDialog(false, items, barCode);
            }
        }else{
            Toast.makeText(Dashboard.this, "Artikel nicht gefunden", Toast.LENGTH_SHORT).show();
        }
    }

    public void createPage() {

        sharedPrefManager = new SharedPrefManager(getBaseContext());
        DataBaseSingleton.init(getBaseContext());
        DataBaseSingleton.getInstance().loadDataBase();
        _viewPagerFragment = new ViewPagerFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_layout, _viewPagerFragment).commit();
        checkIntentForID();
    }
    public void checkIntentForID(){
        Intent callingIntent = getIntent();
        int itemID = -1;
        if (callingIntent != null) {
            itemID = callingIntent.getIntExtra(Notifier.ITEM_ID, -1);
        }
        if (itemID > -1) {
            FridgeItem item = DataBaseSingleton.getInstance().getItemByID(itemID);
            navigateToDetailFragment(item);
        }
    }
    public Menu get_menu() {
        return _menu;
    }

    private void navigateToDetailFragment(FridgeItem item){
        FridgeDetailFragment fragment = new FridgeDetailFragment();
        fragment.setData(item);
        changeFragment(fragment, true);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        _viewPagerFragment.setSearchQuery(s);
        MenuItemCompat.collapseActionView(searchItem);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
