package com.example.student.gefriertruhapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.student.gefriertruhapp.Helper.Action;
import com.example.student.gefriertruhapp.Serialization.CSVHelper;
import com.example.student.gefriertruhapp.History.HistoryHelper;
import com.example.student.gefriertruhapp.History.HistoryViewPagerFragment;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Notifications.Notifier;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.StoreList.StoresListFragment;
import com.example.student.gefriertruhapp.SharedPreferences.SharedPrefManager;
import com.example.student.gefriertruhapp.FridgeList.FridgeListViewPagerFragment;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends DashboardBase implements SearchView.OnQueryTextListener {
    private SharedPrefManager sharedPrefManager;

    private SearchView mSearchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createPage();
    }

    public void createPage() {
        sharedPrefManager = new SharedPrefManager(getBaseContext());
        DataBaseSingleton.init(getBaseContext());
        DataBaseSingleton.getInstance().loadDataBase();
        _fridgeListViewPagerFragment = new FridgeListViewPagerFragment();
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
        } else if (id == R.id.action_search) {
            mSearchView.setIconified(false);
            return true;
        } else if (id == R.id.settings) {
            changeFragment(new StoresListFragment(), true);
            return true;
        } else if (id == R.id.history) {
            changeFragment(new HistoryViewPagerFragment(), true);
            return true;
        } else if (id == R.id.csv_export) {
            saveCSV();
            return true;
        } else if (id == R.id.open) {
            if (isStoreAvailable()) {
                openQRDroid(ACTIVITY_RESULT_QRDROID_OPEN);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCSV() {
        for (Store store : DataBaseSingleton.getInstance().getStores()) {
            CSVHelper.writeStore(store);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fertig");
        builder.setMessage("Export wurde erfolgreich fertiggestellt.");
        builder.setNeutralButton("Ok", null);
        builder.create().show();
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
            String barCode = data.getExtras().getString(RESULT);

            if (requestCode == ACTIVITY_RESULT_QRDROID_ADD) {
                addElement(barCode);
            } else if (requestCode == ACTIVITY_RESULT_QRDROID_DEL) {
                delElement(barCode);
            } else if (requestCode == ACTIVITY_RESULT_QRDROID_OPEN) {
                openElement(barCode);
            }
        }
    }

    private void openElement(String barCode) {
        List<FridgeItem> list = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        if (list == null || list.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nicht gefunden");
            builder.setMessage("Der gescannte Artikel wurde im aktuellen Bestand nicht gefunden.");
            builder.setNeutralButton("Ok", null);
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
            FridgeItem item = new FridgeItem(-1, barCode, 1, null, barCode, "", 1, store);
            navigateToDetailFragment(item);
        }
    }

    public void addElement(String barCode) {
        List<FridgeItem> item = DataBaseSingleton.getInstance().getFridgeItems(barCode);
        if (item != null) {
            showChooseArticleDialog(Action.ADD, item, barCode);
        } else {
            addNewItem(barCode);
        }
    }


    int itemPos;

    public void showChooseArticleDialog(final Action action, final List<FridgeItem> items, final String barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (action == Action.ADD) {
            builder.setTitle("Artikel wählen (Hinzufügen)");
        } else if(action == Action.DELETE) {
            builder.setTitle("Artikel wählen (Herausnehmen)");
        }else if(action == Action.OPEN){
            builder.setTitle("Artikel wählen (Öffnen)");
        }
        List<String> strings = new ArrayList<>();
        for (FridgeItem fridgeItem : items) {
            strings.add(fridgeItem.getNotificationDateString() + " Anzahl: " + fridgeItem.getQuantity());
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
        builder.setNegativeButton("Abbrechen", null);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
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
            b.setTitle("Anzahl wählen (Hinzufügen)");
        } else if (action == Action.DELETE) {
            b.setTitle("Anzahl wählen (Herausnehmen)");
        }
        b.setNegativeButton("Abbrechen", null);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FridgeItem item = items.get(itemPosition);
                FridgeItem oldItem = new FridgeItem(item);
                if (action == Action.ADD) {
                    item.setQuantity(item.getQuantity() + numberPickerValue);
                } else if(action == Action.DELETE) {
                    item.setQuantity(item.getQuantity() - numberPickerValue);
                }

                int i = _fridgeListViewPagerFragment.currentView();
                DataBaseSingleton.getInstance().saveItem(item);
                DataBaseSingleton.getInstance().saveDataBase();
                HistoryHelper.changeItem(oldItem, item);
                _fridgeListViewPagerFragment.setData();
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

    }


    public void delElement(String barCode) {
        List<FridgeItem> items;
        int i = _fridgeListViewPagerFragment.currentView();
        items = DataBaseSingleton.getInstance().getFridgeItems(barCode);

        if (items != null && items.size() > 0) {
            if (items.size() == 1) {
                showChooseQuantityDialog(Action.DELETE, items, 0);
            } else {
                showChooseArticleDialog(Action.DELETE, items, barCode);
            }
        } else {
            Toast.makeText(Dashboard.this, "Artikel nicht gefunden", Toast.LENGTH_SHORT).show();
        }
    }
}
