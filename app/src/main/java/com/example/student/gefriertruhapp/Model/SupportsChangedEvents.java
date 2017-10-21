package com.example.student.gefriertruhapp.Model;

import java.util.ArrayList;
import java.util.List;

public class SupportsChangedEvents{
    protected List<OnPropertyChangedListener> listeners;
    protected List<OnPropertyChangedListener> getListeners(){ //cant use initializers because gson doesnt create them.
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        return listeners;
    }
    public void subscribe(OnPropertyChangedListener listener){
        if(!getListeners().contains(listener)){
            getListeners().add(listener);
        }
    }
    public void unsubscribe(OnPropertyChangedListener listener){
        getListeners().remove(listener);
    }
    protected void OnPropertyChanged(String propertyName){
        for (OnPropertyChangedListener listener : getListeners()){
            listener.onPropertyChanged(propertyName);
        }
    }
}
