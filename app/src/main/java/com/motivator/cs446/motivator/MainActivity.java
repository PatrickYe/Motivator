package com.motivator.cs446.motivator;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements MainFragment.OnFragmentInteractionListener{
    private MainFragment mainFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                // Add the fragment on initial activity setup
                mainFragment = new MainFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, mainFragment)
                        .commit();
            } else {
                // Or set the fragment from restored state info
                mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
            }
        }

        public void onFragmentInteraction(Uri uri){
        }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onResume() {
            super.onResume();

            // Logs 'install' and 'app activate' App Events.
//            com.facebook.AppEventsLogger.activateApp(this);
        }

        @Override
        protected void onPause() {
            super.onPause();

            // Logs 'app deactivate' App Event.
//            com.facebook.AppEventsLogger.deactivateApp(this);
        }
}
