package com.example.feedme.models;

public class Testing {
    interface iCallback{
        public void onSuccess();
    }

    public void authenticate(iCallback callback){
        callback.onSuccess();
    }
}
