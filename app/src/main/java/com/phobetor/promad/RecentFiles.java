package com.phobetor.promad;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentFiles#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFiles extends Fragment {

    DatabaseHelper db;
    String[] stringBuffer;
    String[] adress;
    StringBuffer strContent;
    String text;
    Thread t;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public RecentFiles() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentFiles.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentFiles newInstance(String param1, String param2) {
        RecentFiles fragment = new RecentFiles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        db = new DatabaseHelper(getContext());
        Cursor res = db.getRecentFiles();
        stringBuffer = new String[res.getCount()];
        adress = new String[res.getCount()];
        for(int i=0;res.moveToNext();i++)
        {
            adress[i] = res.getString(0);
            stringBuffer[i] = res.getString(1);
        }






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recent_files, container, false);
        ListAdapter myAdapter = new CustomAdapter(getContext(), stringBuffer);
        final ListView listView = (ListView) v.findViewById(R.id.myListView);
        listView.setAdapter(myAdapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //String add = String.valueOf(parent.getItemAtPosition(position));
                String add = adress[position];

                if(isFilePresent(adress[position]))
                {
                    new ReadFile().execute(add);
                    buildAlertBox("Your file is being loaded. Click OK to proceed");
                }
                else
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage( stringBuffer[position] + " no longer exists");
                    builder.setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                                    databaseHelper.deleteFile(stringBuffer[position]);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }





            }
        });
      /*  Button butt = (Button) v.findViewById(R.id.recentButton);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = db.getRecentFiles();
                StringBuffer[] stringBuffer = new StringBuffer[res.getCount()];
                while(res.moveToNext())
                {
                    Toast.makeText(getActivity(), res.getString(0), Toast.LENGTH_LONG).show();
                }
              //  Toast.makeText(getActivity(), "Recent clicked", Toast.LENGTH_LONG).show();
            }
        });*/
        return  v;
    }

    public boolean isFilePresent(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    protected void buildAlertBox(String string)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(string);
        builder.setNeutralButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), TextLoaded.class);
                        intent.putExtra("TEXT", text);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

   public void recentClicked(View view)
   {
       Toast.makeText(getActivity(), "Recent clicked", Toast.LENGTH_LONG).show();
   }

    public class ReadFile extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {


        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                int ch;
                strContent = new StringBuffer("");
                File file = new File(params[0]);
                if(params[0].contains(".txt"))
                {
                    FileInputStream inputStream = new FileInputStream(file);
                    while ((ch = inputStream.read()) != -1) {
                        strContent.append((char) ch);
                    }
                    text = strContent.toString();
                    inputStream.close();

                }
                else if (params[0].contains(".pdf"))
                {
                    PDDocument document = PDDocument.load(file);
                    PDFTextStripper stripper = new PDFTextStripper();
                    text = stripper.getText(document);
                    document.close();

                }
                else
                {

                }

            }
            catch (Exception e)
            {
                //Toast.makeText(, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
