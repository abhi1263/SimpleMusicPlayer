package studyjam.gdg.abhishek.simplemusicplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class playActivity extends ActionBarActivity{

    TextView totalTime,songName, currentTime;

    SQLiteDatabase musicDatabase;

    private static MediaPlayer mediaPlayer;

    ImageView songImage,playBT;

    static NotificationManager manager;

    static boolean isNotifyActive = false;

    static int songID = 0;

    static int notifyID =99;

    int cur = 0 ;

    Uri songUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent i = getIntent();

        totalTime = (TextView) findViewById(R.id.totalTime);

        songName = (TextView) findViewById(R.id.songName);

        songImage = (ImageView) findViewById(R.id.songImage);

        currentTime = (TextView) findViewById(R.id.currentTime);

        songID = i.getIntExtra("SONG_ID",0);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");

        songName.setTypeface(typeface);

        playBT = (ImageView) findViewById(R.id.playButton);

        playBT.setImageResource(R.drawable.pau);

        songID = songID + 1;

        startSong(songID);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });

    }

    private void startSong(int songID) {

        musicDatabase = this.openOrCreateDatabase("MusicDatabase.db",MODE_PRIVATE,null);

        Cursor cursor = musicDatabase.rawQuery("SELECT * FROM musicList WHERE id=" + songID + ";", null);

        int nameID = cursor.getColumnIndex("name");

        int pathID = cursor.getColumnIndex("path");

        cursor.moveToFirst();

        songName.setText(cursor.getString(nameID));

        songUri = Uri.parse(cursor.getString(pathID));

        cursor.close();

        setAlbumArt(songUri);

        removeNotify();

        playSong();

    }

    private void playSong() {

        if(songUri != null){

            stopCurrentSong();

            mediaPlayer = MediaPlayer.create(getApplicationContext(),songUri);

            mediaPlayer.start();

            createNotify();

            totalTime.setText(calcTime(mediaPlayer.getDuration()));

        }

    }

    private void removeNotify(){

        if (isNotifyActive) {

            manager.cancel(notifyID);

            isNotifyActive = false;

        }

    }

    private void createNotify() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setTicker(songName.getText().toString());

        builder.setContentText(songName.getText().toString());

        builder.setContentTitle("Playing");

        builder.setSmallIcon(R.drawable.player);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(notifyID,builder.build());

        isNotifyActive = true;

    }

    private void setAlbumArt(Uri songUri){

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songUri.getPath());
        byte [] data = mmr.getEmbeddedPicture();

        if(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) != null)
        songName.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 240,true);
            songImage.setImageBitmap(bitmap);
        }
        else
        {
            songImage.setImageResource(R.drawable.image);
        }

    }

    public String calcTime(int time){

        String totTime = String.format("%02d:%02d:%02d",TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        if(totTime.substring(0,2).equals("00:")){
            totTime = totTime.replaceFirst("00:","");
        }

        return totTime;
    }

    public void stopCurrentSong(){

        if(mediaPlayer != null){
            try{
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    cur = 0;
                    removeNotify();
                }
            }catch (Exception e){}
        }

    }

    public void playPauseSong(View view) {

        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playBT.setImageResource(R.drawable.pl);
                cur = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            }
            else {
                playBT.setImageResource(R.drawable.pau);
                if(cur == 0){
                    playSong();
                }
                mediaPlayer.start();
                mediaPlayer.seekTo(cur);
            }
        }

    }

    public void nextSongPlay(View view) {

        nextSong();

    }

    public void nextSong(){

        try{

            startSong(songID + 1);

            songID++;

        }catch (Exception e){
            songID = 0;
        }

    }

    public void previousSongPlay(View view) {

        try{

            startSong(songID - 1);

            songID--;

        }catch (Exception e){
        }

    }


}