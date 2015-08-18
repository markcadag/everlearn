package com.everlearning.everlearning;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.everlearning.everlearning.communicator.LoadSubjectEvent;
import com.everlearning.everlearning.communicator.OnClickList;
import com.everlearning.everlearning.communicator.OnRefresh;
import com.everlearning.everlearning.communicator.SubjectsLoadedEvent;
import com.everlearning.everlearning.db.DatabaseManager;
import com.everlearning.everlearning.db.QueryExecutor;
import com.everlearning.everlearning.db.dao.UserDAO;
import com.everlearning.everlearning.model.Handout;
import com.everlearning.everlearning.model.User;
import com.facebook.crypto.Crypto;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,OnClickList, OnRefresh {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final String TAG = MainActivity.class.getSimpleName();
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Crypto crypto;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIfUserAuthenticated();

        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //TODO check if get subject or handout list is on already on request
        //       getHandouts();

//        Crypto crypto = new Crypto(new CustomSharedPrefsBackedKeyChain(this,"bmd1eWVudGllbmxvbmc="), new SystemNativeCryptoLibrary());
//        File file = new File("/storage/emulated/0/Download/cad_g.pdf");
//        File fileDest = new File(getCacheDir(), "encrypted");
//
//        File fileDest2 = new File(getCacheDir(), "test.pdf");
//
//        if(!fileDest.exists()) try {
//
//            fileDest.createNewFile();
//            Utils utils = new Utils();
//            utils.encode(file, fileDest, crypto);
//            Log.i(TAG, "encode ok");
//
//            utils.decode(fileDest, fileDest2, crypto);
//            Log.i(TAG,"decode ok");
//            replaceFragment(PdfFragment.newInstance(fileDest2.getAbsolutePath()));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void addFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void checkIfUserAuthenticated() {
        final User user = null;
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                UserDAO userDAO = new UserDAO(database, MainActivity.this);
                checkIfUserExist(userDAO.getCurrentUser());
            }
        });
    }

    private void checkIfUserExist(User user){
        if(user == null || user.getAuth_key() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private List<Handout> getDummny() {
        List<Handout> handouts = new ArrayList<>();

        for (int x=1; x<9; x++) {
            Handout handout = new Handout();
            if(x < 4) {
                handout.setSubject_id(9802);
            } else {
                handout.setSubject_id(234);
            }
            handout.setId(x);
            handout.setName("sample"+x);
            handouts.add(handout);
        }
        return handouts;
    }

    private void searchFragment(String query){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment != null) {
            if(fragment instanceof  SubjectListFragment){
                Log.i(TAG,"searching instace subject list");
                ((SubjectListFragment) fragment).searchData(query);
            }
        }
    }

    private void revertSubjectList(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
        if(fragment != null) {
            if(fragment instanceof  SubjectListFragment){
                Log.i(TAG, "searching instace subject list");
                EventBus.getDefault().post(new SubjectsLoadedEvent());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (mNavigationDrawerFragment!=null  &&!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.length() > 3) {
                        Log.i(TAG,"onchange searching instance subject list");
                        searchFragment(newText);
                    } else if(newText.length() == 0){
                        revertSubjectList();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query)  {
                    if(query.length() > 3) {
                        Log.i(TAG,"searching instance subject list");
                        searchFragment(query);
                    }
                    return true;
                }
            };
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
                    if(fragment instanceof  SubjectListFragment){
                        revertSubjectList();
                    }
                    return false;
                }
            });
            searchView.setOnQueryTextListener(textChangeListener);
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new Fragment();
        switch (position)
        {
            case 0:
                fragment = SubjectListFragment.newInstance(position);
                replaceFragment(fragment);
                break;
            case 1:
                fragment = HandoutListFragment.newInstance(position,0);
                replaceFragment(fragment);
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
    public void onClickList(long id, String name) {

        if(name.equals(SubjectListFragment.class.getSimpleName())) {
            Fragment fragment = HandoutListFragment.newInstance(1,id);
            addFragment(fragment);
            //    mNavigationDrawerFragment.selectItem(1);
        } else if(name.equals(HandoutListFragment.class.getSimpleName())){

        }
    }

    @Override
    public void onRefresh(String name) {
        if(name.equals(SubjectListFragment.class.getSimpleName())){
            EventBus.getDefault().post(new LoadSubjectEvent());
        }
    }

}
