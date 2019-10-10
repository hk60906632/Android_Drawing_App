package com.example.android_drawing_app;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    //this is the main class that control the UI, like the side menu, the spinner.
    //mainly use for the onClick listener for each button in the side menu

    private PaintView paintView;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    final private int STORAGE_PERMISSION_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner spinner_line_size = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.spinner_line_size);
        Spinner spinner_background_color = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.spinner_background_color);
        Spinner spinner_shape_size = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.spinner_shape_size);
        Spinner spinner_color = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.spinner_color);

        ArrayAdapter<CharSequence> adapter_line_size = ArrayAdapter.createFromResource(this, R.array.spinner_line_size_text, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_bg_color = ArrayAdapter.createFromResource(this, R.array.spinner_bg_color_text, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_color = ArrayAdapter.createFromResource(this, R.array.spinner_color_text, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter_shape_size = ArrayAdapter.createFromResource(this, R.array.spinner_shape_size_text, android.R.layout.simple_spinner_dropdown_item);

        adapter_line_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_color.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_shape_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_line_size.setAdapter(adapter_line_size);
        spinner_background_color.setAdapter(adapter_bg_color);
        spinner_shape_size.setAdapter(adapter_shape_size);
        spinner_color.setAdapter(adapter_color);
        spinner_color.setOnItemSelectedListener(this);
        spinner_background_color.setOnItemSelectedListener(this);
        spinner_shape_size.setOnItemSelectedListener(this);
        spinner_line_size.setOnItemSelectedListener(this);

        mDrawerlayout = (DrawerLayout) findViewById(R.id.activity_main);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout,R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init_line(metrics);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.testing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (mToggle.onOptionsItemSelected(item)){ //the top left hamburger menu button
            mDrawerlayout = (DrawerLayout) findViewById(R.id.activity_main);
            NavigationView navigationView = findViewById(R.id.nav_view);
            mDrawerlayout.openDrawer(navigationView);
            return true;
        }

        if (id == R.id.back){ //top right last step button
            paintView.last_step();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.normal:
                paintView.touch_mode_setter(drawing_mode.LINE);
                paintView.normal();
                return true;


            case R.id.blur:
                paintView.touch_mode_setter(drawing_mode.LINE);
                paintView.blur();
                return true;


            case R.id.circle_menu:
                paintView.touch_mode_setter(drawing_mode.CIRCLE);
                return true;


            case R.id.clear:
                paintView.touch_mode_setter(drawing_mode.NONE);
                paintView.clear();
                return true;

            case R.id.save:
                Log.d("called saved","called saved");
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //check permission granted or not first
                    paintView.saveBitmap();
                } else {
                    Log.d("permission not granted","permission not granted");
                    //if not granted, ask user for permission
                    requestStoragePermission();
                }

                return true;

            case R.id.rectangle_menu:
                paintView.touch_mode_setter(drawing_mode.RECTANGLE);
                return true;
        }
        return true;
    }


    private void requestStoragePermission() { //create an alert window to ask user for permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to write to the storage of your phone")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //ask user for permission to save pic into their phone
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //determine which spinner the user pressed

        int spinner_id = parent.getId();

        switch (spinner_id){
            case R.id.spinner_color:
                String text = parent.getItemAtPosition(position).toString();
                paintView.set_color(text);
                break;

            case R.id.spinner_background_color:
                String text_bg = parent.getItemAtPosition(position).toString();
                paintView.set_bg_color(text_bg);
                break;

            case R.id.spinner_shape_size:
                String text_shape_size = parent.getItemAtPosition(position).toString();
                paintView.set_shape_size(text_shape_size);
                break;

            case R.id.spinner_line_size:
                String text_line_size = parent.getItemAtPosition(position).toString();
                paintView.set_stroke_width(text_line_size);
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
