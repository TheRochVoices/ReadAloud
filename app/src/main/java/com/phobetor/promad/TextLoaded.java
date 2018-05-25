package com.phobetor.promad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;


public class TextLoaded extends AppCompatActivity {
    TextView textView;
    TextToSpeech tts;
    int currentPosition=0;


    String string = null;
    ImageButton playButton, backwardButton, forwardButton;;
    MediaPlayer mediaPlayer;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    boolean flag;

    private double startTime = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_loaded);

        Intent intent = getIntent();
         string = intent.getExtras().getString("TEXT");
        textView = (TextView) findViewById(R.id.fileText);
        textView.setText(string);

        playButton = (ImageButton) findViewById(R.id.playButton);
        forwardButton = (ImageButton) findViewById(R.id.forwardButton);
        backwardButton = (ImageButton) findViewById(R.id.backwardButton);
        playButton.setEnabled(false);
        forwardButton.setEnabled(false);
        backwardButton.setEnabled(false);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
       // mediaPlayer = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/tts/qqqe.wav"));




    }

    public void loadClicked(View view)
    {
       // new MediaLoad().execute();
      //  HashMap<String, String> parmas = new HashMap<>();
        Bundle bundle = new Bundle();
        //parmas.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + 2);
        File dir = new File("/storage/emulated/0/tts/qqqe.wav");
        //dir.mkdirs();
        int stat =  tts.synthesizeToFile(string, bundle, dir, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

        if(stat==-1)
        {
            Toast.makeText(this, "Your android version doesn't support this feature. We are working on it", Toast.LENGTH_LONG).show();
        }
        else {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?");
            builder.setNeutralButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadMedia();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            playButton.setEnabled(true);
            forwardButton.setEnabled(true);
            backwardButton.setEnabled(true);

        }



    }

    public void loadMedia()
    {

        mediaPlayer = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/tts/qqqe.wav"));
       /* mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(getBaseContext(), "FIle ready to be payed", Toast.LENGTH_LONG).show();
            }
        });*/
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading file");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();
        progressDialog.setCancelable(false);

        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(500);
                        jumpTime += 10;
                        progressDialog.setProgress(jumpTime);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                progressDialog.cancel();
            }
        };
        t.start();
      /* while(true) {
            flag = false;
           mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
               public void onPrepared(MediaPlayer mp) {
                   flag = true;
                   Button play = (Button) findViewById(R.id.playButton);
                   play.setEnabled(true);
               }
           });

           if(flag==true)
               break;
       }*/

    }
    public void playClicked(View view)
    {
        mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/tts/qqqe.wav"));

       /* mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();

            }
        });*/

                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                view.setEnabled(false);
                forwardButton.setEnabled(true);
                backwardButton.setEnabled(true);

    }
    public void pausePressed(View view)
    {
        if(mediaPlayer.isPlaying())
        {
           currentPosition =  mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playButton.setEnabled(true);
            backwardButton.setEnabled(false);
            forwardButton.setEnabled(false);

        }
    }

    public void forwardButtonClicked(View view)
    {

        float speed = mediaPlayer.getPlaybackParams().getSpeed();
        speed += .10f;
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        Toast.makeText(this, "Current playback speed: "+ speed, Toast.LENGTH_LONG).show();

    }
    public void backwardButtonClicked(View view)
    {
        float speed = mediaPlayer.getPlaybackParams().getSpeed();
        speed -=.10f;
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        Toast.makeText(this, "Current playback speed: "+ speed, Toast.LENGTH_LONG).show();

    }


    public class MediaLoad extends AsyncTask<String, String, String>
    {
        int stat;
        @Override
        protected void onPreExecute() {
            Toast.makeText(TextLoaded.this, "FUCK", Toast.LENGTH_SHORT).show();
            progressDialog=new ProgressDialog(getBaseContext());
            progressDialog.setMessage("Loading file");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgress(0);
            progressDialog.show();
            progressDialog.setCancelable(false);



        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.cancel();
        }

        @Override
        protected String doInBackground(String... params) {
            //mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/tts/qqqe.wav"));
            try {
                wait(3000);
            }
            catch(Exception e)
            {

            }
            return null;

        }
    }
}

