package edu.stanford.braincat.rulepedia.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import edu.stanford.braincat.rulepedia.R;
import edu.stanford.braincat.rulepedia.model.ObjectDatabase;
import edu.stanford.braincat.rulepedia.model.Property;
import edu.stanford.braincat.rulepedia.model.Rule;
import edu.stanford.braincat.rulepedia.model.RuleDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PropertyManageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PropertyManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyManageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PropertyManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PropertyManageFragment newInstance(String param1, String param2) {
        PropertyManageFragment fragment = new PropertyManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PropertyManageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_property_manage, container, false);

        View v =  inflater.inflate(R.layout.fragment_property_manage, container, false);

        propertyListView = (ListView) v.findViewById( R.id.property_list );

        //instantiate custom adapter
        listAdapter = new PropertyListItemCustomAdapter(this.getActivity(), this.getActivity().getApplicationContext(), listItems);

        propertyListView.setAdapter(listAdapter);

        loadProperties(this.getActivity().getApplicationContext());
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    ListView propertyListView;
    ArrayList<Property> listItems = new ArrayList<Property>();
    PropertyListItemCustomAdapter listAdapter;

    private void loadProperties(Context ctx)
    {
        listItems.clear();

        try{
            ObjectDatabase db = ObjectDatabase.get();
            Collection <Property> properties = db.getAllProperties();

            Log.d("myTag", "properties.size(): " + properties.size());

            for (Property property : properties) {
                listItems.add(property);
            }

            //listItems.add(new Rule("rule 1", "desc", null, null));

            listAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Timer autoUpdate;

    @Override
    public void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //updateHTML();
                        loadProperties(getActivity().getApplicationContext());
                    }
                });
            }
        }, 0, 5000); // updates each 40 secs
    }

    @Override
    public void onPause() {
        autoUpdate.cancel();
        super.onPause();
    }

}