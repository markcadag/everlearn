package com.everlearning.everlearning;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.everlearning.everlearning.communicator.LoadSubjectEvent;
import com.everlearning.everlearning.communicator.OnClickList;
import com.everlearning.everlearning.communicator.OnRefresh;
import com.everlearning.everlearning.communicator.SubjectsLoadedEvent;
import com.everlearning.everlearning.db.DatabaseManager;
import com.everlearning.everlearning.db.QueryExecutor;
import com.everlearning.everlearning.db.dao.SubjectDAO;
import com.everlearning.everlearning.model.Subject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
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
public class SubjectListFragment extends Fragment implements AbsListView.OnItemClickListener,XListView.IXListViewListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "name";

    private final String TAG = SubjectListFragment.class.getSimpleName();
    // TODO: Rename and change types of parameters

    private OnClickList mListener;
    private OnRefresh mOnRefresh;

    /**
     * The fragment's ListView/GridView.
     */
    private XListView mListView;
    private SubjectListAdapter subjectListAdapter;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private List<Subject> subjects;

    // TODO: Rename and change types of parameters
    public static SubjectListFragment newInstance(int position) {
        SubjectListFragment fragment = new SubjectListFragment();
        Bundle args = new Bundle();
        args.putInt("section_number", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new LoadSubjectEvent());
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubjectListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjects = new ArrayList<Subject>();
        subjectListAdapter = new SubjectListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xlistview_main, container, false);

        // Set the adapter
        mListView = (XListView) view.findViewById(R.id.xListView);
        mListView.setAdapter(subjectListAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mListView.setXListViewListener(this);

        onEvent(new SubjectsLoadedEvent());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnClickList) activity;
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt("section_number"));
            mOnRefresh = (OnRefresh) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mOnRefresh = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onClickList(subjects.get(position - 1).getId(), TAG);
        }
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

    public void onEvent(SubjectsLoadedEvent subjectsLoadedEvent){
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                SubjectDAO subjectDAO = new SubjectDAO(database, getActivity());
                subjects = subjectDAO.selectAll();
                subjectListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLoadMore() {
        mListView.stopLoadMore();
    }

    public void searchData(final String query){
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                SubjectDAO subjectDAO = new SubjectDAO(database, getActivity());
                subjects = subjectDAO.searchSubject(query);
                subjectListAdapter.notifyDataSetChanged();
            }
        });
    }


    public class SubjectListAdapter extends BaseAdapter {

        private ColorGenerator generator = ColorGenerator.MATERIAL;
        private TextDrawable.IBuilder builder;

        public SubjectListAdapter() {
          builder  = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .round();
        }

        @Override
        public int getCount() {
            return subjects.size();
        }

        @Override
        public Subject getItem(int i) {
            return subjects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolderItem;

            if(convertView == null) {

                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.xlistlistview_item,parent,false);

                viewHolderItem = new ViewHolderItem();
                viewHolderItem.txtSubjectName = (TextView) convertView.findViewById(R.id.txt_name);
                viewHolderItem.imgName = (ImageView) convertView.findViewById(R.id.image_view);
                convertView.setTag(viewHolderItem);

            } else {
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }

            Subject subject = subjects.get(position);

            if(subject != null) {

                int color1 = generator.getColor(subject.getName().charAt(0)+"");

                TextDrawable drawable = builder.build(subject.getName().charAt(0)+"", color1);
                viewHolderItem.imgName.setImageDrawable(drawable);
                viewHolderItem.txtSubjectName.setText(subject.getName());
            }


            return convertView;
        }

    }

    static class ViewHolderItem {
        TextView txtSubjectName;
        ImageView imgName;
    }
}

