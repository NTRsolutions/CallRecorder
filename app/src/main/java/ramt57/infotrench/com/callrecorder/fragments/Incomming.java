package ramt57.infotrench.com.callrecorder.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import ramt57.infotrench.com.callrecorder.BroadcastReciver.ExtendedReciver;
import ramt57.infotrench.com.callrecorder.MainActivity;
import ramt57.infotrench.com.callrecorder.R;
import ramt57.infotrench.com.callrecorder.SqliteDatabase.DatabaseHelper;
import ramt57.infotrench.com.callrecorder.adapter.IncommingAdapter;
import ramt57.infotrench.com.callrecorder.contacts.ContactProvider;
import ramt57.infotrench.com.callrecorder.pojo_classes.Contacts;

/**
 * A simple {@link Fragment} subclass.
 */
public class Incomming extends Fragment {
   private IncommingAdapter recyclerAdapter;
//    RecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    Context ctx;
    boolean mensu=false;
    int temp;
    ArrayList<Contacts> searchPeople=new ArrayList<>();
    ArrayList<Contacts> allContactList=new ArrayList<>();
    ArrayList<String> recordings=new ArrayList<>();
    ArrayList<Integer> integers=new ArrayList<>();
    ArrayList<Contacts> recordedContacts=new ArrayList<>();
    public Incomming() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_blank,container,false);
        ctx=view.getContext();
        recyclerView=view.findViewById(R.id.recyclerView);
//        MyItemDecorator decoration = new MyItemDecorator(getContext(), Color.parseColor("#dadde2"), 0.5f);
//        recyclerView.addItemDecoration(decoration);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.parseColor("#dadde2"))
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter=new IncommingAdapter();
//        recyclerAdapter=new RecyclerAdapter();

        recyclerView.setAdapter(recyclerAdapter);
        Bundle bundle;
        bundle=getArguments();
        recordings=bundle.getStringArrayList("RECORDING");
        allContactList= ContactProvider.getContacts(view.getContext());
        boolean hascontact=false;
        recordedContacts=ContactProvider.getCallList(view.getContext(),recordings,"IN");
        recyclerAdapter.setContacts(recordedContacts);
        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.setListener(new IncommingAdapter.itemClickListener() {
            @Override
            public void onClick(View v, int position) {
                ArrayList<String> records=ContactProvider.getRecordingList(v.getContext(),recordings,"IN");
                if(mensu){
                    Contacts contacts1=searchPeople.get(position);
                    ContactProvider.openMaterialSheetDialog(getLayoutInflater(),position,records.get(integers.get(position)),contacts1);
                }else {
                    ContactProvider.openMaterialSheetDialog(getLayoutInflater(),position,records.get(position),recordedContacts.get(position));
                }
                ContactProvider.setItemrefresh(new ContactProvider.refresh() {
                    @Override
                    public void refreshList(boolean var) {
                        if(var)
                            recyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        MainActivity.setQueylistener2(new MainActivity.querySearch2() {
            @Override
            public void Search_name2(String name) {
                ArrayList<Contacts> records=new ArrayList<Contacts>();
                DatabaseHelper databaseHelper=new DatabaseHelper(ctx);
                records=databaseHelper.AllContacts();
                if(name.length()>1){
                    mensu=true;
                    searchPeople.clear();
                    temp=0;
                    for(Contacts contacts:recordedContacts){
                        if(contacts.getNumber().contains(name)){
                            //dsd
                            integers.add(temp);
                            searchPeople.add(contacts);
                            ++temp;
                            continue;
                        }
                        if(contacts.getName()!=null&&contacts.getName().toLowerCase().contains(name.toLowerCase())){
                            searchPeople.add(contacts);
                            integers.add(temp);
                        }
                        ++temp;
                    }
                    recyclerAdapter.setContacts(searchPeople);
                    recyclerAdapter.notifyDataSetChanged();
                }else{
                    mensu=false;
                    recyclerAdapter.setContacts(recordedContacts);
                    recyclerAdapter.notifyDataSetChanged();
                }

            }
        });
        return view;
    }

}
