package com.example.noteapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.database.NotesDatabase;
import com.example.noteapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreatNoteActivity extends AppCompatActivity {


    private EditText inTitle, inSub, inNote;
    private TextView textDate;
    private View viewsSubIndicator;
    private TextView textweb;
    private LinearLayout layoutweb;

    private String selectdColor;
    private AlertDialog alertDialogDelete;
    private AlertDialog alertDialogWeb;

    private Note alreadyAvaileble;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_note);


        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        inTitle = findViewById(R.id.inputNoteTitle);
        inSub = findViewById(R.id.inputNoteSuntitle);
        inNote = findViewById(R.id.inputNoteText);
        textDate = findViewById(R.id.textDate);
        textDate.setText(new SimpleDateFormat("EEEE , dd , MMMM , yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        ImageView imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        textweb = findViewById(R.id.textWeb);
        layoutweb = findViewById(R.id.layoutwebURL);

        viewsSubIndicator = findViewById(R.id.viewSubtitleIndicator);
        selectdColor = "#333333";

        if (getIntent().getBooleanExtra("ViewOrUpdat",false)){
            alreadyAvaileble = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        if (getIntent().getBooleanExtra("isQuick",false)){
           String type = getIntent().getStringExtra("quickType");
           if (type.equals("URL")){
               textweb.setText(getIntent().getStringExtra("URL"));
               layoutweb.setVisibility(View.VISIBLE);
           }
        }


        initMiscell();
        setSubIndicatorColor();

    }

    // Viewing and Updating Notes
    private void setViewOrUpdateNote(){
        inTitle.setText(alreadyAvaileble.getTitle());
        inSub.setText(alreadyAvaileble.getSubtitle());
        inNote.setText(alreadyAvaileble.getNoteText());
        textDate.setText(alreadyAvaileble.getDateTime());
        if (!alreadyAvaileble.getWebLink().toString().trim().isEmpty()){
            layoutweb.setVisibility(View.VISIBLE);
            textweb.setText(alreadyAvaileble.getWebLink());
        }


    }

    // Save the note in the databese
    private void saveNote () {

        if (inTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Note Title is Empty !!" ,Toast.LENGTH_SHORT).show();
            return;
        }
        else if (inSub.getText().toString().trim().isEmpty() && inNote.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note is Empty !!" ,Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(inTitle.getText().toString());
        note.setSubtitle(inSub.getText().toString());
        note.setNoteText(inNote.getText().toString());
        note.setDateTime(textDate.getText().toString());
        note.setColor(selectdColor);// in recyclerview
        note.setWebLink(textweb.getText().toString());

        if (layoutweb.getVisibility() == View.VISIBLE){
            note.setWebLink(textweb.getText().toString());
        }

        if (alreadyAvaileble!=null){
            note.setId(alreadyAvaileble.getId());
        }


        // Add to DataBAse
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }

        }

        new SaveNoteTask().execute();
    }

    /// Colors
    private void initMiscell(){
        final LinearLayout layoutmiscell = findViewById(R.id.layputMiscell);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior= BottomSheetBehavior.from(layoutmiscell);
        layoutmiscell.findViewById(R.id.textMiscell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        ///// colors
        final ImageView imgColor1 = layoutmiscell.findViewById(R.id.imgColor1);
        final ImageView imgColor2 = layoutmiscell.findViewById(R.id.imgColor2);
        final ImageView imgColor3 = layoutmiscell.findViewById(R.id.imgColor3);
        final ImageView imgColor4 = layoutmiscell.findViewById(R.id.imgColor4);
        final ImageView imgColor5 = layoutmiscell.findViewById(R.id.imgColor5);


        layoutmiscell.findViewById(R.id.viewcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectdColor = "#333333";
                imgColor1.setImageResource(R.drawable.ic_done);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                setSubIndicatorColor();
            }
        });
        layoutmiscell.findViewById(R.id.viewcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectdColor = "#944e6c";
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(R.drawable.ic_done);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                setSubIndicatorColor();
            }
        });
        layoutmiscell.findViewById(R.id.viewcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectdColor = "#83a95c";
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(R.drawable.ic_done);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(0);
                setSubIndicatorColor();
            }
        });
        layoutmiscell.findViewById(R.id.viewcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectdColor = "#FF018786";
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(R.drawable.ic_done);
                imgColor5.setImageResource(0);
                setSubIndicatorColor();
            }
        });
        layoutmiscell.findViewById(R.id.viewcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectdColor = "#ef4f4f";
                imgColor1.setImageResource(0);
                imgColor2.setImageResource(0);
                imgColor3.setImageResource(0);
                imgColor4.setImageResource(0);
                imgColor5.setImageResource(R.drawable.ic_done);
                setSubIndicatorColor();
            }
        });


        // for viwing color
        if (alreadyAvaileble != null && alreadyAvaileble.getColor()!=null && !alreadyAvaileble.getColor().trim().isEmpty()){
            switch (alreadyAvaileble.getColor()){
                case "#333333":
                    layoutmiscell.findViewById(R.id.viewcolor1).performClick();
                    break;
                case "#944e6c":
                    layoutmiscell.findViewById(R.id.viewcolor2).performClick();
                    break;
                case "#83a95c":
                    layoutmiscell.findViewById(R.id.viewcolor3).performClick();
                    break;
                case "#FF018786":
                    layoutmiscell.findViewById(R.id.viewcolor4).performClick();
                    break;
                case "#ef4f4f":
                    layoutmiscell.findViewById(R.id.viewcolor5).performClick();
                    break;
            }
        }

        if (alreadyAvaileble != null){
            layoutmiscell.findViewById(R.id.layputDeletNote).setVisibility(View.VISIBLE);
            layoutmiscell.findViewById(R.id.layputDeletNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteAlertDialog();
                }
            });
        }

        layoutmiscell.findViewById(R.id.layoutAddURLMiscell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                showDialogAddURL();
            }
        });
    }

    //// Add URL
    private void showDialogAddURL(){
        if (alertDialogWeb == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreatNoteActivity.this);
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
                        Toast.makeText(CreatNoteActivity.this, "Enter URL" ,Toast.LENGTH_SHORT).show();
                    }
                    else if(!Patterns.WEB_URL.matcher(inurl.getText().toString()).matches()){
                        Toast.makeText(CreatNoteActivity.this, "Enter Valid URL" ,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        textweb.setText(inurl.getText().toString());
                        layoutweb.setVisibility(View.VISIBLE);
                        alertDialogWeb.dismiss();
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

    //// delet dialg
    private void showDeleteAlertDialog(){
        if (alertDialogDelete == null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreatNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteContainer)
            );
            builder.setView(view);
            alertDialogDelete = builder.create();
            if (alertDialogDelete.getWindow() != null){
                alertDialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void , Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deletNote(alreadyAvaileble);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent i = new Intent();
                            i.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK,i);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogDelete.dismiss();
                }
            });
        }
        alertDialogDelete.show();
    }

    // bar next to the Subtitle
    private void setSubIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewsSubIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectdColor));
    }

}