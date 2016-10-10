package com.example.student.gefriertruhapp.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.ViewPager.TitleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class StoreDetailFragment extends TitleFragment {
    private Store store;
    protected View rootView;
    protected EditText name, description;

    @Override
    public String getTitle() {
        if(store == null){
            return "Lager hinzufügen";
        }
        return store.getName();
    }

    public void setData(Store data) {
        this.store = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void inflateRootView(LayoutInflater inflater, ViewGroup container){
        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_store_detail, container, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflateRootView(inflater, container);

        name = (EditText) rootView.findViewById(R.id.store_detail_name);
        description = (EditText)rootView.findViewById(R.id.store_detail_description);

        setViewData();

        return rootView;
    }

    private void setViewData(){
        if(store != null){
            name.setText(store.getName());
            description.setText(store.getDescription());
        }
    }

    private void getViewData(){
        if(store == null){
            store = new Store(name.getText().toString(), description.getText().toString());
        }else{
            store.setName(name.getText().toString());
            store.setDescription(description.getText().toString());
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
            DataBaseSingleton.getInstance().deleteStore(store);
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();
            return true;
        }else if(id == R.id.menu_item_save){
            getViewData();
            if(store.getName() == null || store.getName().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setMessage("Es muss ein Name für das Lager eingegeben werden.")
                        .setCancelable(true)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
                return true;
            }

            List<Store> stores = DataBaseSingleton.getInstance().getStores();
            for(Store savedStore : stores){
                if(savedStore.getName().toLowerCase().equals(store.getName().toLowerCase())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                    builder.setMessage("Der Name des Lagers wird bereits von einem anderen Lager verwendet. Wählen sie einen anderen Namen.")
                            .setCancelable(true)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                    return true;
                }
            }

            DataBaseSingleton.getInstance().saveStore(store);
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();

            return true;
        } else if(id == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
