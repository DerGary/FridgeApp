package com.example.student.gefriertruhapp.StoreList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.Store;
import com.example.student.gefriertruhapp.R;
import com.example.student.gefriertruhapp.Helper.TitleFragment;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.List;

/**
 * Created by Stefan on 22-07-16.
 */
public class StoreDetailFragment extends TitleFragment implements View.OnClickListener{
    private Store store;
    protected View rootView;
    protected EditText name, description;
    private Button colorButton;
    private ImageView colorView;
    private GradientDrawable drawable;

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
        colorButton = (Button) rootView.findViewById(R.id.store_detail_button_color);
        colorView = (ImageView) rootView.findViewById(R.id.store_detail_color_view);
        drawable = (GradientDrawable)getResources().getDrawable(R.drawable.rectangle, null);
        colorView.setBackground(drawable);

        colorButton.setOnClickListener(this);

        setViewData();

        return rootView;
    }

    private void setViewData(){
        if(store != null){
            name.setText(store.getName());
            description.setText(store.getDescription());
            drawable.setColor(store.getColor());
        }else{
            drawable.setColor(colorToSave);
        }
    }

    private void getViewData(){
        if(store == null){
            store = new Store(name.getText().toString(), description.getText().toString());
        }else{
            store.setName(name.getText().toString());
            store.setDescription(description.getText().toString());
        }
        store.setColor(colorToSave);
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
            boolean update = false;
            Store oldStore = null;
            if(store != null){
                update = true;
                oldStore = new Store(store);
            }
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
                if(savedStore != store && savedStore.getName().toLowerCase().equals(store.getName().toLowerCase())){
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
            if(update){
                DataBaseSingleton.getInstance().updateStore(oldStore, store);
            }else{
                DataBaseSingleton.getInstance().saveStore(store);
            }
            DataBaseSingleton.getInstance().saveDataBase();
            getActivity().onBackPressed();

            return true;
        } else if(id == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int colorToSave = Color.rgb(0,0,0);

    @Override
    public void onClick(View v) {
        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(getActivity());
        builder.setTitle("Farbe wählen");
        int[] colors = new int[]{
                Color.rgb(255,188,0),
                Color.rgb(232,143,0),
                Color.rgb(255,115,0),
                Color.rgb(232,63,0),
                Color.rgb(255,23,0),

                Color.rgb(255,0,96),
                Color.rgb(232,0,229),
                Color.rgb(171,0,255),
                Color.rgb(78,0,232),
                Color.rgb(0,0,255),

                Color.rgb(0,78,255),
                Color.rgb(0,146,232),
                Color.rgb(0,243,255),
                Color.rgb(0,232,163),
                Color.rgb(0,255,91),

                Color.rgb(0,255,46),
                Color.rgb(48,232,0),
                Color.rgb(161,255,0),
                Color.rgb(232,228,0),
        };
        builder.setColors(colors);
        builder.setNegativeButtonText("Abbrechen");
        builder.setPositiveButtonText("Ok");
        SpectrumDialog dialog = builder.build();
        dialog.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                if(positiveResult) {
                    colorToSave = color;
                    drawable.setColor(colorToSave);
                }
            }
        });
        dialog.show(((Dashboard)getActivity()).getSupportFragmentManager(), "tag");
    }
}
