package com.example.student.gefriertruhapp.Model;

/**
 * Created by student on 21.12.15.
 */
public class ShelfItem {
    private String _name;
    private int _minQuantity;
    private int _currentQuantity;
    private String _barCode;

    public ShelfItem(String _name, int _minQuantity, int _currentQuantity, String _barCode) {
        this._name = _name;
        this._minQuantity = _minQuantity;
        this._currentQuantity = _currentQuantity;
        this._barCode = _barCode;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_minQuantity() {
        return _minQuantity;
    }

    public void set_minQuantity(int _minQuantity) {
        this._minQuantity = _minQuantity;
    }

    public int get_currentQuantity() {
        return _currentQuantity;
    }

    public void set_currentQuantity(int _currentQuantity) {
        this._currentQuantity = _currentQuantity;
    }

    public String get_barCode() {
        return _barCode;
    }

    public void set_barCode(String _barCode) {
        this._barCode = _barCode;
    }
}
