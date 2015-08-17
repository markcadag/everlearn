package com.everlearning.everlearning;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.everlearning.everlearning.communicator.OnClickList;
import com.everlearning.everlearning.communicator.OnRefresh;
import com.everlearning.everlearning.db.DatabaseManager;
import com.everlearning.everlearning.db.QueryExecutor;
import com.everlearning.everlearning.db.dao.HandoutsDAO;
import com.everlearning.everlearning.encryptor.Utils;
import com.everlearning.everlearning.model.Handout;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xlistview.view.XListView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement tsubjectshe {@link OnClickList}
 * interface.
 */
public class HandoutListFragment extends Fragment implements AbsListView.OnItemClickListener,XListView.IXListViewListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_POSITION = "position";
    private static final String ARG_PARAM_SUBID = "subject_id";

    private int subjectId;

    private final String TAG = HandoutListFragment.class.getSimpleName();
    // TODO: Rename and change types of parameters

    private OnClickList mListener;
    private OnRefresh mOnRefresh;

    private Utils utils = new Utils();
    /**
     * The fragment's ListView/GridView.
     */
    private XListView mListView;
    private HandsOutAdapter handsOutAdapter;
    private long id = 0;
    private Bundle args;
    private final int ALL = 0;
    private String saveDir;

    private ArrayList<Handout> handoutsArr = new ArrayList<Handout>();

    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    private ThinDownloadManager thinDownloadManager;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private List<Handout> handouts;
    private Crypto crypto;

    // TODO: Rename and change types of parameters
    public static HandoutListFragment newInstance(int position, long subject_id) {
        HandoutListFragment fragment = new HandoutListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        args.putLong(ARG_PARAM_SUBID, subject_id);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HandoutListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handouts = new ArrayList<Handout>();
        handsOutAdapter = new HandsOutAdapter();
        thinDownloadManager = new ThinDownloadManager();
        saveDir = Environment.getExternalStorageDirectory() + "/everlearn/";
        crypto = new Crypto(new CustomSharedPrefsBackedKeyChain(getActivity(),"bmd1eWVudGllbmxvbmc="),
                new SystemNativeCryptoLibrary());
        if (!crypto.isAvailable()) {
            Log.e("Crypto", "not loaded");
            return;
        } else {
            Log.i("Crypto", "loaded");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xlistview_main, container, false);

        // Set the adapter
        mListView = (XListView) view.findViewById(R.id.xListView);
        mListView.setAdapter(handsOutAdapter);
        handsOutAdapter.clear();
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setXListViewListener(this);

        args = this.getArguments();
        if (args.containsKey(ARG_PARAM_SUBID)) {
            insertData(args.getLong(ARG_PARAM_SUBID));
        } else {
            insertData(ALL);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnClickList) activity;
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_PARAM_POSITION));
            mOnRefresh = (OnRefresh) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
   //     thinDownloadManager.release();
    }

    private void onDownloadCompleted(Handout handout, File file){
        ((MainActivity)getActivity())
                .replaceFragment(PdfFragment.newInstance(file.getAbsolutePath()));
        Log.i(TAG, "on finish");
        if(getActivity().getCacheDir() != null) {
            File fileDest = new File(getActivity().getCacheDir(), file.getName());
            File fileDest2 = new File(getActivity().getCacheDir(),file.getName() + "_"  +System.currentTimeMillis()+".pdf");

            if(!fileDest.exists()) try {

                fileDest.createNewFile();
                utils.encode(file, fileDest, crypto);
              //  handout.setEncryptedPath(fileDest.getAbsolutePath());
              //  Log.i(TAG, "encrypted file is on " + handout.getEncryptedPath());
                updateHandout(handout);
              //  file.delete();
                utils.decode(fileDest, fileDest2, crypto);

                Log.i(TAG, "decode ok");
                ((MainActivity) getActivity())
                        .replaceFragment(PdfFragment.newInstance(fileDest2.getAbsolutePath()));
            }catch (Exception e){

            }
        }
    }

    private void onDownloadTest(File file){
   //     file = new File("/storage/emulated/0/Download/cad_g.pdf");
//        File fileDest = new File(getActivity().getCacheDir(), "encrypted");

       // File fileDest2 = new File(getActivity().getCacheDir(), "test.pdf");

      //  if(!fileDest.exists()) try {

       //     fileDest.createNewFile();
          //  Utils utils = new Utils();
          //  utils.encode(file, fileDest, crypto);
          //  Log.i(TAG, "encode ok");

          //  utils.decode(fileDest, fileDest2, crypto);
         //   Log.i(TAG, "decode ok");
            ((MainActivity)getActivity())
                    .replaceFragment(PdfFragment.newInstance(file.getPath()));

//        } catch (IOException e) {
  //          e.printStackTrace();
    //    }
    }

    public void downloadFile(final Handout handout, final ProgressBar progressBar) {

        final Uri downloadUri = Uri.parse(handout.getUrlPath());
        final Uri destinationUri = Uri.parse(getActivity().getFilesDir().toString()+"/" + handout.getName());
        DownloadRequest request = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int i) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.GONE);
//                        onDownloadCompleted(handout, new File(destinationUri.getPath()));
                        onDownloadTest(new File(destinationUri.getPath()));
                    }

                    @Override
                    public void onDownloadFailed(int i, int i1, String s) {
                        Log.i(TAG,"error" + s );
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onProgress(int i, long l, int i1) {
                        progressBar.setProgress(i1);
                        Log.i(TAG,"progress" + i1);
                    }
                });
        thinDownloadManager.add(request);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mOnRefresh = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Handout handout = handouts.get(position-1);
//        if(handout.getEncryptedPath() == null || handout.getEncryptedPath().isEmpty()) {
//            Log.i(TAG,"starting download");
//            downloadFile(handout, (ProgressBar) view.findViewById(R.id.progressBar));
//        } else {
//            openHandOut(handout);
//            Log.i(TAG, "opening handout");
//        }
    }

    public void openHandOut(Handout handout) {
        //TODO open from buffered stream not from file
        File decrypted = new File(getActivity().getCacheDir(),handout.getName());
        File encrypted = new File(handout.getEncryptedPath());
        utils.decode(encrypted, decrypted, crypto);
    //    replaceFragment(PdfFragment.newInstance(fileDest2.getAbsolutePath()));
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onRefresh() {
        mListView.stopRefresh();
        mOnRefresh.onRefresh(TAG);
    }

    public void test(){

    }

    public void addDummy(){
        Handout handout = new Handout();
        handout.setName("OK");
        handout.setEncryptedPath(null);
        handout.setDlState(DownloadManager.STATUS_PENDING);
        handout.setUrlPath("http://www.pdf995.com/samples/pdf.pdf");
//        handout.setUrlPath("http://dlsw.baidu.com/sw-search-sp/soft/7b/33461/freeime.1406862029.exe");
        handouts.add(handout);
        handsOutAdapter.notifyDataSetChanged();
    }

    public void insertData(final long subject_id) {
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();
        addDummy();


        //TODO uncomment this
//        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
//            @Override
//            public void run(SQLiteDatabase database) {
//                HandoutsDAO handoutsDAO = new HandoutsDAO(database, getActivity());
//                if (subject_id == ALL) {
//                    handouts = handoutsDAO.selectAll();
//                } else {
//                    handouts = handoutsDAO.selectBySubjectId(subject_id);
//                }
//                handoutsArr.clear();
//                handoutsArr.addAll(handou)
//                handsOutAdapter.notifyDataSetChanged();
//            }
//        });
    }

    public void updateHandout(final Handout handout){
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                HandoutsDAO handoutsDAO = new HandoutsDAO(database, getActivity());
                handoutsDAO.insertOrUpdate(handout);
                handsOutAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLoadMore() {
        mListView.stopLoadMore();
    }


    public class HandsOutAdapter extends BaseAdapter {

        private ColorGenerator generator = ColorGenerator.MATERIAL;
        private TextDrawable.IBuilder builder;
        private ArrayList<Handout> handoutsArr = new ArrayList<Handout>();

        public HandsOutAdapter() {
          builder  = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .round();
        }

        @Override
        public int getCount() {
            return handouts.size();
        }

        @Override
        public Handout getItem(int i) {
            return handouts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolderItem viewHolderItem;

            final Handout handout = handouts.get(position);

            if(convertView == null) {

                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.xlistlistview_item,parent,false);

                viewHolderItem = new ViewHolderItem();
                viewHolderItem.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
                viewHolderItem.button = (Button)convertView.findViewById(R.id.btnDownload);
                viewHolderItem.txtSubjectName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolderItem.imgName = (ImageView) convertView.findViewById(R.id.image_view);
                viewHolderItem.button.setVisibility(View.VISIBLE);
                viewHolderItem.handout = handout;
                convertView.setTag(viewHolderItem);

            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();

                viewHolderItem.handout.setProgressBar(null);
                viewHolderItem.handout = handout;
                viewHolderItem.handout.setProgressBar(viewHolderItem.progressBar);

            }

            viewHolderItem.progressBar.setProgress(handout.getProgress());
            handout.setProgressBar(viewHolderItem.progressBar);

            viewHolderItem.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handout.getProgressBar().setVisibility(View.VISIBLE);
                    viewHolderItem.button.setVisibility(View.GONE);
                    downloadHandout(handout);
                }
            });


            //hide or show progress bar control download
            if(handout.getDlState() == DownloadManager.STATUS_PENDING) {
                viewHolderItem.progressBar.setVisibility(View.GONE);
                viewHolderItem.button.setVisibility(View.VISIBLE);
            } else if(handout.getDlState() == DownloadManager.STATUS_SUCCESSFUL){
                viewHolderItem.progressBar.setVisibility(View.GONE);
                viewHolderItem.button.setVisibility(View.GONE);
            } else if(handout.getDlState() == DownloadManager.STATUS_RUNNING) {
                //TODO add cancel download function
                viewHolderItem.progressBar.setVisibility(View.VISIBLE);
                viewHolderItem.button.setVisibility(View.GONE);
            }

            int color1 = generator.getColor(handout.getName().charAt(0) + "");
            TextDrawable drawable = builder.build(handout.getName().charAt(0) + "", color1);
            viewHolderItem.imgName.setImageDrawable(drawable);
            viewHolderItem.txtSubjectName.setText(handout.getName());


            return convertView;
        }

        public void clear() {
            handouts.clear();
            this.notifyDataSetChanged();
        }

    }

    private void downloadHandout(final Handout handout) {
        handout.setDlState(DownloadManager.STATUS_PENDING);
        final Uri downloadUri = Uri.parse(handout.getUrlPath());
        final Uri destinationUri = Uri.parse(getActivity().getFilesDir().toString() + "/" + handout.getName());
        DownloadRequest request = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int i) {
                        handout.setDlState(DownloadManager.STATUS_SUCCESSFUL);
                        ProgressBar bar = handout.getProgressBar();
                        if(bar != null) {
                            bar.setVisibility(View.GONE);
                        }
              //          onDownloadCompleted(handout, new File(destinationUri.getPath()));
                        onDownloadTest(new File(destinationUri.getPath()));
                    }

                    @Override
                    public void onDownloadFailed(int i, int i1, String s) {
                        handout.setDlState(DownloadManager.STATUS_FAILED);
                        ProgressBar bar = handout.getProgressBar();
                        if(bar != null) {
                            bar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onProgress(int i, long l, int progress) {
                        handout.setDlState(DownloadManager.STATUS_RUNNING);
                        ProgressBar bar = handout.getProgressBar();
                        if(bar != null) {
                            bar.setVisibility(View.VISIBLE);
                            bar.setProgress(progress);
                            bar.invalidate();
                        }
                    }
                });
        thinDownloadManager.add(request);
    }

    static class ViewHolderItem {
        TextView txtSubjectName;
        ImageView imgName;
        ProgressBar progressBar;
        Button button;
        Handout handout;
    }
}

