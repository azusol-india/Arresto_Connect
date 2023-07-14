package app.com.arresto.arresto_connect.interfaces;

public interface ObjectListener {
        void onResponse(Object obj);

        void onError(String error);
    }