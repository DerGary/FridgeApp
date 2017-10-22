package com.example.student.gefriertruhapp.FridgeList;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.student.gefriertruhapp.Helper.Collections;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.OnPropertyChangedListener;
import com.example.student.gefriertruhapp.R;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by Stefan on 18-05-15.
 */
public class FridgeViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, OnPropertyChangedListener {
    public static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
    protected final TextView name, date, quantity;
    protected FridgeItem _data;
    protected View itemView;
    protected OnMarkedListener markedListener;

    public FridgeViewHolder(View itemView) {
        super(itemView);
        itemView.setOnLongClickListener(this);
        name = ((TextView) itemView.findViewById(R.id.item_name));
        date = ((TextView) itemView.findViewById(R.id.item_date));
        quantity = ((TextView) itemView.findViewById(R.id.item_quantity));
        this.itemView = itemView;
    }

    public void setMarkedListener(OnMarkedListener listener){
        markedListener = listener;
    }

    public void assignData(FridgeItem data) {
        if(_data != null) {
            _data.unsubscribe(this);
        }
        _data = data;
        _data.subscribe(this);
        populateViewWithData(_data);
    }

    private void populateViewWithData(FridgeItem data){
        this.name.setText(data.getName());
        if(data.getNotificationDate() != null) {
            if(data.getNotificationDate().isBeforeNow()){
                this.date.setTextColor(Color.RED);
            }else{
                this.date.setTextColor(Color.BLACK);
            }
            this.date.setText(formatter.print(data.getNotificationDate()));
        }else{
            this.date.setText("");
        }
        int quantityOfAllLinkedItems = data.getQuantity();
        Iterable<FridgeItem> linkedItems = data.getLinkedItems();
        if(linkedItems != null && !Collections.isEmpty(linkedItems)) {
            for (FridgeItem linkedItem : data.getLinkedItems()) {
                quantityOfAllLinkedItems += linkedItem.getQuantity();
            }
            this.quantity.setText(data.getQuantity() + " / " + data.getMinQuantity() + " (" + quantityOfAllLinkedItems + ")");
        }else{
            this.quantity.setText(data.getQuantity() + " / " + data.getMinQuantity());
        }

        if(quantityOfAllLinkedItems == 0){
            this.quantity.setTextColor(Color.RED);
        } else if(quantityOfAllLinkedItems < data.getMinQuantity()){
            this.quantity.setTextColor(Color.rgb(255,135,0));
        }else{
            this.quantity.setTextColor(Color.rgb(49,232,2));
        }
        SetBackgroundColor(itemView);
    }

    public FridgeItem get_data() {
        return _data;
    }

    @Override
    public boolean onLongClick(View v) {
        if(markedListener == null){
            return false;
        }else{
            setMarked(v);
            return true;
        }
    }

    private void setMarked(View v){
        if(markedListener != null){
            boolean isMarked = _data.isMarked();
            if(isMarked){
                _data.setMarked(false);
                markedListener.setUnmarked(_data);
            }else{
                _data.setMarked(true);
                markedListener.setMarked(_data);
            }
        }
    }

    private void SetBackgroundColor(View v){
        if(markedListener == null) {
            v.setBackgroundColor(Color.WHITE);
        }else{
            if(_data.isMarked()){
                v.setBackgroundColor(Color.parseColor("#ff80cbc4"));
            }else{
                v.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onPropertyChanged(String name) {
        populateViewWithData(_data);
    }
}
