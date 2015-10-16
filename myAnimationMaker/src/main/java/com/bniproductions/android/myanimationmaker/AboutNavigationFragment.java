package com.bniproductions.android.myanimationmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

import org.lucasr.dspec.DesignSpec;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutNavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutNavigationFragment extends Fragment implements RecyclerAdapter.ClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "AboutNavigationFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private RecyclerAdapter recyclerAdapter;

    private boolean mFromSavedInstanceState = true;
    //Bundle bundle;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutNavigationFragment newInstance(String param1, String param2) {
        AboutNavigationFragment fragment = new AboutNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutNavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d(TAG, "mParam1: "+mParam1);
            Log.d(TAG, "mParam2: "+mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_about_navigation, container, false);

        //DesignSpec designSpec = DesignSpec.fromResource(layout, R.raw.navigation_drawer_dspec);
        //layout.getOverlay().add(designSpec);

        recyclerView = (RecyclerView) layout.findViewById(R.id.about_list);
        recyclerAdapter = new RecyclerAdapter(getActivity(), getData());
        recyclerAdapter.setClickListener(this);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public static List<AboutNavigationDrawerInfo> getData(){

        int one = 1;

        List<AboutNavigationDrawerInfo> dataList = new ArrayList<>();

        int[] icons = {R.drawable.folder_new, R.drawable.folder_documents_icon, R.drawable.close_icon};
        String[] titles = {"New Project","Project List", "Close About"};

        for(int i = 0; i < icons.length  && i < titles.length; i++){
            AboutNavigationDrawerInfo current = new AboutNavigationDrawerInfo();
            current.setIconId(icons[i]);
            current.setTitle(titles[i]);
            dataList.add(current);
        }

        return dataList;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar){

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slide_offset){
                if(slide_offset < 0.6f){
                    toolbar.setAlpha(1 - slide_offset);
                }
            }
        };

        if(!mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent;

        if(position == 0) {
            intent = new Intent(getActivity(), ActivityAnimationSettings.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }else if(position == 1){
            intent = new Intent(getActivity(), FrameSliderActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }else if(position == 2){
            getActivity().finish();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
