package com.example.mymenu;

import android.net.Uri;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;




public class MainActivity extends FragmentActivity{

    private MainUI mainUI;
    private LeftMenu leftmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainUI = new MainUI(this);
        setContentView(mainUI);
        leftmenu = new LeftMenu();

        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();

        t.add(MainUI.LEFT_ID, leftmenu).commit();


    }
}
