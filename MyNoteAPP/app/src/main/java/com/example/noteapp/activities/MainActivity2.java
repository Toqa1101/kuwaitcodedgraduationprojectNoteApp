package com.example.noteapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.adapters.NotesAdapter;
import com.example.noteapp.database.NotesDatabase;
import com.example.noteapp.entities.Note;
import com.example.noteapp.listeners.NotesListeners;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements NotesListeners {


    public static final int REQUEST_CODE_ADD_NOTE= 1; // add note
    public static final int REQUEST_CODE_UPDATE = 2; // update note
    public static final int REQUEST_CODE_SHOW_NOTE =3; // display

    private RecyclerView noteRV;
    private List <Note> noteList;
    private NotesAdapter notesAdapter;

    private  int noteClickedPosition =-1;

    private AlertDialog alertDialogWeb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // user name
        TextView name = findViewById(R.id.textUsername);
        Bundle bn = getIntent().getExtras();
        name.setText(bn.getString("userName"));


        ImageView imgAddMain = findViewById(R.id.imgAddNoteMAin);
        imgAddMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreatNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        noteRV = findViewById(R.id.notesRecyclerview);
        noteRV.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );


        // RV connection
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        noteRV.setAdapter(notesAdapter);

        getNote(REQUEST_CODE_SHOW_NOTE,false);

        //searching
        EditText insearch = findViewById(R.id.inputSearch);
        insearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() !=0){
                    notesAdapter.searchNotes(s.toString());
                }

            }
        });


        findViewById(R.id.imgWeb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddURL();
            }
        });


        findViewById(R.id.imgAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreatNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });



    }

    private void getNote(final int requestCode, final boolean isNoteDeleted){

        @SuppressLint("StaticFieldLeak")
        class GetNoteTask extends AsyncTask <Void , Void , List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(requestCode == REQUEST_CODE_SHOW_NOTE){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();;
                }
                else if (requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyDataSetChanged();
                    noteRV.smoothScrollToPosition(0);
                }
                else if (requestCode == REQUEST_CODE_UPDATE){
                    noteList.remove(noteClickedPosition);

                    // if the  note is deleted or not
                    if (isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }
                    else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

            }
        }
        new GetNoteTask().execute();
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNote(REQUEST_CODE_ADD_NOTE, false);
        }
        else if(requestCode== REQUEST_CODE_UPDATE && resultCode== RESULT_OK){
            if(data !=null){
                getNote(REQUEST_CODE_UPDATE,data.getBooleanExtra("isNoteDeleted",false));
            }

        }
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreatNoteActivity.class);
        intent.putExtra("ViewOrUpdat", true);
        intent.putExtra("note",note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE);

    }

    private void showDialogAddURL(){
        if (alertDialogWeb == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity2.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddURLContainer )
            );
            builder.setView(view);
            alertDialogWeb = builder.create();
            if (alertDialogWeb.getWindow() != null){
                alertDialogWeb.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inurl = view.findViewById(R.id.inputURL);
            inurl.requestFocus();
            view.findViewById(R.id.textAddURL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inurl.getText().toString().trim().isEmpty()){
                        Toast.makeText(MainActivity2.this, "Enter URL" ,Toast.LENGTH_SHORT).show();
                    }
                    else if(!Patterns.WEB_URL.matcher(inurl.getText().toString()).matches()){
                        Toast.makeText(MainActivity2.this, "Enter Valid URL" ,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        alertDialogWeb.dismiss();
                        Intent intent = new Intent(getApplicationContext(), CreatNoteActivity.class);
                        intent.putExtra("isQuick", true);
                        intent.putExtra("quickType", "URL");
                        intent.putExtra("URL", inurl.getText().toString());
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);

                    }
                }
            });
            view.findViewById(R.id.textCancelURL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogWeb.dismiss();
                }
            });

            alertDialogWeb.show();

        }}
}
