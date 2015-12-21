package com.example.student.gefriertruhapp.Model;

import org.joda.time.DateTime;

/**
 * Created by student on 21.12.15.
 */
public class FridgeItem {
    private String _name;
    private int _quantity;
    private DateTime _notificationDate;
    private String _barCode;

    public FridgeItem(String _name, int _quantity, DateTime _notificationDate, String barCode) {
        this._name = _name;
        this._quantity = _quantity;
        this._notificationDate = _notificationDate;
        this._barCode = barCode;
    }


    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_quantity() {
        return _quantity;
    }

    public void set_quantity(int _quantity) {
        this._quantity = _quantity;
    }

    public DateTime get_notificationDate() {
        return _notificationDate;
    }

    public void set_notificationDate(DateTime _notificationDate) {
        this._notificationDate = _notificationDate;
    }

    public String get_barCode() {
        return _barCode;
    }

    public void set_barCode(String _barCode) {
        this._barCode = _barCode;
    }
}
