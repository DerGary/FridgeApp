package com.example.student.gefriertruhapp.FridgeList;

import android.view.View;

/**
 * Created by stefa on 01-Nov-17.
 */

public interface ViewHolderBuilder<T> {
    public T build(View view);
    public int getLayout();
}
