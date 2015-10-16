package com.bniproductions.android.myanimationmaker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lucasr.dspec.DesignSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 9/28/2015.
 */
public class FrameSliderNavigationFragment extends Fragment implements RecyclerAdapter.ClickListener{

    private static final String TAG = "FramesSliderNavFrag";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FrameSliderNavigationFragment mListener;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private View containerView;

    private boolean fromSavedInstanceState;

    public static FrameSliderNavigationFragment newInstance(String param1, String param2){
        FrameSliderNavigationFragment fragment = new FrameSliderNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FrameSliderNavigationFragment(){
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if(bundle != null){
            fromSavedInstanceState = true;
        }
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.fragment_about_navigation, container, false);

        //DesignSpec designSpec = DesignSpec.fromResource(layout, R.raw.navigation_drawer_dspec);
        //layout.getOverlay().add(designSpec);

        recyclerView = (RecyclerView) layout.findViewById(R.id.about_list);
        recyclerAdapter = new RecyclerAdapter(getActivity(), getData());
        recyclerAdapter.setClickListener(this);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.requestFocus();
        //layout.requestFocus();
        return layout;
    }

    public static List<AboutNavigationDrawerInfo> getData(){

        List<AboutNavigationDrawerInfo> list = new ArrayList<>();

        int[] icons = {R.drawable.folder_new, R.drawable.folder_documents_icon, R.drawable.close_icon};
        String[] titles = {"New Project", "Open Project", "Close Project"};

        for (int i = 0; i < icons.length; i++){
            AboutNavigationDrawerInfo current = new AboutNavigationDrawerInfo(icons[i], titles[i]);
            list.add(current);
        }
        return list;
    }

    public void setUp(int fragmentId, final DrawerLayout drawerlayout, final Toolbar toolbar){

        containerView = getActivity().findViewById(fragmentId);
        drawerLayout = drawerlayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close){
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

        if(!fromSavedInstanceState){
            drawerLayout.openDrawer(containerView);
        }

        drawerlayout.setDrawerListener(drawerToggle);
        drawerlayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }

    @Override
    public void itemClicked(View view, int position) {
        Intent intent;

        switch (position){
            case 0:
                intent = new Intent(getActivity(), ActivityAnimationSettings.class);
                startActivity(intent);
                break;
            case 1:
                break;
            case 2:
                getActivity().finish();
                break;
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
