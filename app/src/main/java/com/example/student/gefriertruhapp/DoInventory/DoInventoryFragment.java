package com.example.student.gefriertruhapp.DoInventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.FridgeList.FridgeRecycleViewAdapter;
import com.example.student.gefriertruhapp.FridgeList.ItemClickListener;
import com.example.student.gefriertruhapp.Helper.Action;
import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.Helper.TitleFragment;
import com.example.student.gefriertruhapp.History.HistoryHelper;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.student.gefriertruhapp.DashboardBase.ACTIVITY_RESULT_QRDROID_DO_INVENTORY;

/**
 * Created by stefa on 01-Nov-17.
 */

public class DoInventoryFragment extends TitleFragment implements ItemClickListener{
    private Store store;
    private View view;
    private RecyclerView listView;
    private FridgeRecycleViewAdapter adapter;
    private List<FridgeItem> fridgeItems;

    public void setStore(Store store){
        this.store = store;
        fridgeItems = new ArrayList<>();
        for(FridgeItem item : store.getItems()){
            //if(item.getQuantity() > 0){
            if(item.getGotQuantity() != 0){
                item.setGotQuantity(0);
            }
            fridgeItems.add(item);
            //}
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            listView = (RecyclerView) view.findViewById(R.id.recycler_view);
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            adapter = new FridgeRecycleViewAdapter(new DoInventoryFridgeItemViewHolderBuilder(), fridgeItems, this, null);
            listView.setAdapter(adapter);
            this.view = view;
        } else {
            setDataForAdapter();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_do_inventory, menu);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getTitle());
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_do_inventory_qr) {
            startQRDroid();
            return true;
        } else if (id == R.id.menu_do_inventory_save) {
            showSaveMessage();
            return true;
        } else if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSaveMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.save_question);
        builder.setMessage(R.string.inventory_save_message);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HistoryHelper.doInventory(getActivity(), store); //must be done before so we dont need to copy each item.
                for(FridgeItem item : store.getItems()) {
                    FridgeItem oldItem = new FridgeItem(item);
                    item.setQuantity(item.getGotQuantity());
                    item.setGotQuantity(0);
                    DataBaseSingleton.getInstance().updateItem(oldItem, item);
                }
                DataBaseSingleton.getInstance().saveDataBase();
                getActivity().onBackPressed();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startQRDroid() {
        ((Dashboard)getActivity()).openQRDroid(ACTIVITY_RESULT_QRDROID_DO_INVENTORY);
    }

    private void setDataForAdapter(){
        adapter.setData(fridgeItems, this, null);
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getTitle() {
        return getString(R.string.inventory)+": " + store.getName();
    }

    @Override
    public void onItemClick(Object data) {
        showChooseQuantityDialog((FridgeItem)data);
    }

    public void barCodeResult(String barCode) {
        if(barCode == null || barCode.equals("")){
            Toast.makeText(getActivity(), R.string.error_reading_qrcode, Toast.LENGTH_LONG).show();
        }

        List<FridgeItem> items = new ArrayList<>();

        for(FridgeItem item : store.getItems()){
            if(barCode.equals(item.getBarCode())){
                items.add(item);
            }
        }
        if(items.isEmpty()){
            List<FridgeItem> allWithThatBarCode = DataBaseSingleton.getInstance().getFridgeItems(barCode);
            if(allWithThatBarCode == null || allWithThatBarCode.isEmpty()){
                Toast.makeText(getActivity(), R.string.no_entry_for_qr_found, Toast.LENGTH_LONG).show();
            }else{
                StringBuilder stores = new StringBuilder("");
                for(FridgeItem item : allWithThatBarCode){
                    stores.append(item.getStore().getName()).append(",");
                }
                Toast.makeText(getActivity(), getString(R.string.no_entry_in_this_stock) + stores.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            if(items.size() > 1){
                showChooseArticleDialog(items);
            }else{
                FridgeItem item = items.get(0);
                increaseGotQuantity(item);
            }
        }
    }

    private void increaseGotQuantity(FridgeItem item){
        item.setGotQuantity(item.getGotQuantity()+1);
        Toast.makeText(getActivity(), getString(R.string.inventory_changed_for_entry_with_quantity, item.getName(), item.getGotQuantity()), Toast.LENGTH_LONG).show();
    }

    int itemPos = 0;
    public void showChooseArticleDialog(final List<FridgeItem> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.multiple_entries_for_barcode_found);

        List<String> strings = new ArrayList<>();
        for (FridgeItem fridgeItem : items) {
            String text = fridgeItem.getName() + "\r\n" + fridgeItem.getNotificationDateString(getActivity()) + "\r\n" +
                    getString(R.string.debit) + fridgeItem.getQuantity() +"\r\n" +
                    getString(R.string.credit) + fridgeItem.getGotQuantity();
            strings.add(text);
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
                FridgeItem item = items.get(itemPos);
                increaseGotQuantity(item);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    int numberPickerValue;

    public void showChooseQuantityDialog(final FridgeItem item) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setView(R.layout.dialog_number_picker);
        b.setTitle(R.string.choose_credit_quantity);
        b.setNegativeButton(R.string.cancel, null);
        b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.setGotQuantity(numberPickerValue);
                Toast.makeText(getActivity(), getString(R.string.inventory_set_for_entry_with_quantity, item.getName(), item.getGotQuantity()), Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = b.create();
        dialog.show();

        NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.dialog_number_picker_picker);
        picker.setMaxValue(100);
        picker.setMinValue(0);
        numberPickerValue = 0;
        picker.setValue(item.getGotQuantity());
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberPickerValue = newVal;
            }
        });
    }
}
