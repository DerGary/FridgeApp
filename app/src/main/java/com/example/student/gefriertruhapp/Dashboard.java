package com.example.student.gefriertruhapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.gefriertruhapp.DoInventory.DoInventoryFragment;
import com.example.student.gefriertruhapp.FridgeList.ItemMarkedListener;
import com.example.student.gefriertruhapp.Helper.Action;
import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.Model.Category;
import com.example.student.gefriertruhapp.Preferences.SettingsFragment;
import com.example.student.gefriertruhapp.History.HistoryHelper;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Notifications.Notifier;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.Serialization.FileAccess;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.example.student.gefriertruhapp.FridgeList.FridgeListViewPagerFragment;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends DashboardBase implements SearchView.OnQueryTextListener, ItemMarkedListener {
    private SharedPrefManager sharedPrefManager;

    private SearchView mSearchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean permissionGranted = FileAccess.verifyStoragePermissions(this);
        if(permissionGranted){
            createPage();
        }
    }

    @Override
    public void createPage() {
        setContentView(R.layout.activity_dashboard);
        sharedPrefManager = new SharedPrefManager(getBaseContext());
        DataBaseSingleton.init(getBaseContext());
        DataBaseSingleton.getInstance().loadDataBase();
        _fridgeListViewPagerFragment = new FridgeListViewPagerFragment();
        _fridgeListViewPagerFragment.setMarkedListener(this);
        getFragmentManager().beginTransaction().replace(R.id.main_layout, _fridgeListViewPagerFragment).commit();
        checkIntentForID();
    }

    public void checkIntentForID() {
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        _fridgeListViewPagerFragment.setSearchQuery(s);
        MenuItemCompat.collapseActionView(searchItem);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        _menu = menu;

        getSupportActionBar().setTitle(getString(R.string.app_name));
        setMenuButtons();
        return true;
    }

    public void setNormalMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml. 
        int id = item.getItemId();

        if (id == R.id.add) {
            if (isStoreAvailable()) {
                openQRDroid(ACTIVITY_RESULT_QRDROID_ADD);
            }
            return true;
        } else if (id == R.id.delete) {
            if (isStoreAvailable()) {
                openQRDroid(ACTIVITY_RESULT_QRDROID_DEL);
            }
            return true;
        } else if (id == R.id.new_item) {
            addNewItem(null);
            return true;
        } else if (id == R.id.menu_selection_link) {
            linkSelectedItems();
            return true;
        } else if (id == R.id.menu_selection_clear) {
            clearSelection();
            return true;
        } else if (id == R.id.menu_selection_delete_items) {
            deleteSelectedItems();
            return true;
        } else if (id == R.id.action_filter) {
            showChooseCategoryDialog();
        } else if (id == R.id.action_search) {
            mSearchView.setIconified(false);
            return true;
        } else if (id == R.id.open) {
            if (isStoreAvailable()) {
                openQRDroid(ACTIVITY_RESULT_QRDROID_OPEN);
            }
            return true;
        } else if (id == R.id.preferences) {
            changeFragment(new SettingsFragment(), true);
            return true;
        } else if(id == R.id.doInventory){
            startDoInventory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showChooseCategoryDialog() {
        final List<Category> categories = DataBaseSingleton.getInstance().getCategories();

        if(categories.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_categories);
            builder.setMessage(R.string.no_categories_set);
            builder.setNegativeButton(R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.choose_category);
            List<String> strings = new ArrayList<>();
            for (Category cat : categories) {
                strings.add(cat.getName());
            }
            itemPos = 0;
            builder.setSingleChoiceItems(strings.toArray(new String[strings.size()]), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    itemPos = which;
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Category cat = categories.get(itemPos);
                    _fridgeListViewPagerFragment.setCategoryQuery(cat);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private DoInventoryFragment doInventoryFragment = null;

    private void startDoInventory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_stock_for_inventory);

        List<String> strings = new ArrayList<>();
        for(Store store : DataBaseSingleton.getInstance().getStores()){
            strings.add(store.getName());
        }
        itemPos = 0;
        builder.setSingleChoiceItems(strings.toArray(new String[strings.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPos = which;
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Store store = DataBaseSingleton.getInstance().getStores().get(itemPos);
                doInventoryFragment = new DoInventoryFragment();
                doInventoryFragment.setStore(store);
                changeFragment(doInventoryFragment, true);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean isStoreAvailable() {
        if (DataBaseSingleton.getInstance().getStores().size() == 0) {
            showNoStoreCreatedAlert(this);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            String barCode = null;

            Bundle bundle = data.getExtras();
            if(bundle.containsKey(RESULT)){
                barCode = bundle.getString(RESULT);
            }else if(bundle.containsKey(ALTERNATE_RESULT)){
                barCode = bundle.getString(ALTERNATE_RESULT);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.error_occured);
                builder.setMessage(R.string.qr_no_result);
                builder.setNeutralButton(R.string.ok, null);
                builder.create().show();
                String possibleKeys = TextUtils.join(", ", bundle.keySet());
                Log.e("tag", "QR Droid hat keine Information zurückgeliefert. Keys in Keyset of Intent: " + possibleKeys);
                FileAccess.writeLog();
                return;
            }


            if (requestCode == ACTIVITY_RESULT_QRDROID_ADD) {
                addElement(barCode);
            } else if (requestCode == ACTIVITY_RESULT_QRDROID_DEL) {
                delElement(barCode);
            } else if (requestCode == ACTIVITY_RESULT_QRDROID_OPEN) {
                openElement(barCode);
            } else if (requestCode == ACTIVITY_RESULT_QRDROID_DO_INVENTORY){
                if(doInventoryFragment != null){
                    doInventoryFragment.barCodeResult(barCode);
                }
            }
        }
    }

    private void openElement(String barCode) {
        List<FridgeItem> list = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        if (list == null || list.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.not_found);
            builder.setMessage(R.string.article_not_found_message);
            builder.setNeutralButton(R.string.ok, null);
            builder.create().show();
        } else if (list.size() == 1) {
            navigateToDetailFragment(list.get(0));
        } else {
            showChooseArticleDialog(Action.OPEN, list, barCode);
        }
    }

    private void addNewItem(String barCode) {
        int i = _fridgeListViewPagerFragment.currentView();

        if (isStoreAvailable()) {
            Store store = DataBaseSingleton.getInstance().getStores().get(0);
            if (DataBaseSingleton.getInstance().getStores().size() > i) {
                store = DataBaseSingleton.getInstance().getStores().get(i);
            }
            FridgeItem item = new FridgeItem(-1, barCode, 1, null, barCode, "", false, 1, store, null, 0, null, null);
            setNameOfFridgeItemAndNavigateToDetail(item);
        }
    }

    public void addElement(String barCode) {
        List<FridgeItem> items = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        if (items != null && !items.isEmpty()) {
            showChooseArticleDialog(Action.ADD, items, barCode);
        } else {
            addNewItem(barCode);
        }
    }


    int itemPos;



    public void showChooseArticleDialog(final Action action, final List<FridgeItem> items, final String barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (action == Action.ADD) {
            builder.setTitle(R.string.choose_article_add);
        } else if(action == Action.DELETE) {
            builder.setTitle(R.string.choose_article_remove);
        }else if(action == Action.OPEN){
            builder.setTitle(R.string.choose_article_open);
        }
        List<String> strings = new ArrayList<>();
        for (FridgeItem fridgeItem : items) {
            String text = fridgeItem.getName() + "\r\n" + fridgeItem.getNotificationDateString(this ) + "\r\n" +
                    getString(R.string.quantity) + ": " + fridgeItem.getQuantity() +" / " + fridgeItem.getMinQuantity();
            Iterable<FridgeItem> linkedItems = fridgeItem.getLinkedItems();
            if(linkedItems != null && !Collections.isEmpty(linkedItems)){
                int quantityOfAllLinkedItems = fridgeItem.getQuantity();
                for(FridgeItem linkedItem : linkedItems){
                    quantityOfAllLinkedItems += linkedItem.getQuantity();
                }
                text+= " (" + quantityOfAllLinkedItems + ")";
            }
            strings.add(text);
        }
        if (action == Action.ADD) {
            strings.add("Neu");
        }
        itemPos = 0;
        builder.setSingleChoiceItems(strings.toArray(new String[strings.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPos = which;
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (itemPos >= items.size()) {
                    addNewItem(barcode);
                } else if(action == Action.ADD || action == Action.DELETE) {
                    showChooseQuantityDialog(action, items, itemPos);
                } else if (action == Action.OPEN){
                    navigateToDetailFragment(items.get(itemPos));
                }
            }
        });
        AlertDialog dialog = builder.create();
        colorDialog(dialog, action);
        dialog.show();
    }

    int numberPickerValue;

    public void showChooseQuantityDialog(final Action action, final List<FridgeItem> items, final int itemPosition) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(R.layout.dialog_number_picker);
        if (action == Action.ADD) {
            b.setTitle(R.string.choose_count_add);
        } else if (action == Action.DELETE) {
            b.setTitle(R.string.choose_count_remove);
        }
        b.setNegativeButton(R.string.cancel, null);
        b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FridgeItem item = items.get(itemPosition);
                FridgeItem oldItem = new FridgeItem(item);
                if (action == Action.ADD) {
                    item.setQuantity(item.getQuantity() + numberPickerValue);
                } else if(action == Action.DELETE) {
                    item.setQuantity(item.getQuantity() - numberPickerValue);
                }

                DataBaseSingleton.getInstance().updateItem(oldItem, item);
                DataBaseSingleton.getInstance().saveDataBase();
                Toast.makeText(getBaseContext(),item.getName() + "\r\n" +
                        getString(R.string.new_quantity) + ": " + item.getQuantity(), Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = b.create();
        colorDialog(dialog, action);
        dialog.show();

        NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.dialog_number_picker_picker);
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

        TextView text = (TextView) dialog.findViewById(R.id.dialog_number_picker_text);
        FridgeItem item = items.get(itemPosition);
        String headerText = item.getName() + "\r\n" + item.getNotificationDateString(this) + "\r\n" +
                getString(R.string.current_quantity) + ": " + item.getQuantity() + " / " + item.getMinQuantity() ;
        Iterable<FridgeItem> linkedItems = item.getLinkedItems();
        if(linkedItems != null && !Collections.isEmpty(linkedItems)){
            int quantityOfAllLinkedItems = item.getQuantity();
            for(FridgeItem linkedItem : linkedItems){
                quantityOfAllLinkedItems += linkedItem.getQuantity();
            }
            headerText+= " (" + quantityOfAllLinkedItems + ")";
        }
        text.setText(headerText);
    }



    public void delElement(String barCode) {
        List<FridgeItem> items;
        int i = _fridgeListViewPagerFragment.currentView();
        items = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        List<FridgeItem> cleanedItems = new ArrayList<>();
        if(items != null) {
            for (FridgeItem item : items) {
                if (item.getQuantity() > 0) {
                    cleanedItems.add(item);
                }
            }
        }

        if (cleanedItems != null && cleanedItems.size() > 0) {
            if (cleanedItems.size() == 1) {
                showChooseQuantityDialog(Action.DELETE, cleanedItems, 0);
            } else {
                showChooseArticleDialog(Action.DELETE, cleanedItems, barCode);
            }
        } else {
            Toast.makeText(Dashboard.this, getString(R.string.article_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private List<FridgeItem> markedItems = new ArrayList<>();

    @Override
    public void setMarked(FridgeItem item) {
        if(!markedItems.contains(item)) {
            markedItems.add(item);
        }
        setMenuButtons();
    }

    public void setMenuButtons(){
        _menu.clear();
        if(markedItems.isEmpty()){
            setNormalMenu(_menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_selected_items, _menu);
            MenuItem linkSelectionButton = _menu.findItem(R.id.menu_selection_link);
            if(markedItems.size() >= 2){
                //link enabled
                linkSelectionButton.setVisible(true);
            }else{
                //link disabled
                linkSelectionButton.setVisible(false);
            }
        }
    }

    private void clearSelection() {
        for(FridgeItem item : markedItems){
            item.setMarked(false);
        }
        markedItems.clear();
        setMenuButtons();
    }

    @Override
    public void setUnmarked(FridgeItem item) {
        markedItems.remove(item);
        setMenuButtons();
    }

    private void linkSelectedItems() {
        DataBaseSingleton.getInstance().linkItems(markedItems);
        clearSelection();
    }

    private void deleteSelectedItems() {
        for(FridgeItem item : markedItems){
            DataBaseSingleton.getInstance().deleteItem(item);
        }
        markedItems.clear();
        DataBaseSingleton.getInstance().saveDataBase();
        _fridgeListViewPagerFragment.setData();
        setMenuButtons();
    }
}
