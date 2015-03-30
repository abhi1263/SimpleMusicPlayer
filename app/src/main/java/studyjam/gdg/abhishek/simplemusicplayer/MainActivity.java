package studyjam.gdg.abhishek.simplemusicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    SQLiteDatabase musicDatabase = null;

    ListView musicList;

    String[] items;

    TextView songHead;

    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicList = (ListView) findViewById(R.id.musicList);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");

        songHead = (TextView) findViewById(R.id.songHead);

        songHead.setTypeface(typeface);

        musicDatabase = this.openOrCreateDatabase("MusicDatabase.db", MODE_PRIVATE, null);

        musicDatabase.execSQL("CREATE TABLE IF NOT EXISTS musicList(id integer primary key, path VARCHAR, name VARCHAR, album VARCHAR);");

        File database = getApplicationContext().getDatabasePath("MusicDatabase.db");

        if (database.exists()) {
            Cursor cursor = musicDatabase.rawQuery("SELECT path FROM musicList", null);

            int nameID = cursor.getColumnIndex("path");

            cursor.moveToFirst();

            int i = 0;



            if (cursor != null && cursor.getCount() > 0) {

                items = new String[cursor.getCount()];

                do {

                    items[i] = cursor.getString(nameID);

                    i++;

                } while (cursor.moveToNext());

                cursor.close();

            }else {

                String name;
                File root = Environment.getExternalStorageDirectory().getParentFile();
                mySongs = findSongs(root);
                items = new String[mySongs.size()];
                android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                for (int j = 0; j < mySongs.size(); j++) {
                    items[j] = mySongs.get(j).getPath();
                    name = mySongs.get(j).getName().replace(".mp3","");
                    musicDatabase.execSQL("INSERT INTO musicList(path,name) VALUES('" + items[j] + "','" + name + "');");
                }

            }
        }

        if(items == null){

            Toast.makeText(getApplicationContext(),"NO SONG TO PLAY",Toast.LENGTH_LONG).show();

        }else {

            MyAdapter adapter = new MyAdapter(getApplicationContext(),items);

            musicList.setAdapter(adapter);
        }

        musicList.setOnItemClickListener(this);

    }

    public ArrayList<File> findSongs(File root) {
        ArrayList<File> al = new ArrayList<File>();
        try {
            File[] files = root.listFiles();
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    al.addAll(findSongs(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3"))
                        al.add(singleFile);
                }
            }
        }catch (Exception e){}
        return al;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), playActivity.class);
        i.putExtra("SONG_ID", position);
        startActivity(i);
    }
}