package studyjam.gdg.abhishek.simplemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.entity.FileEntity;

import java.io.File;
import java.net.FileNameMap;
import java.util.logging.FileHandler;
import java.util.zip.Inflater;

/**
 * Created by Kumar Abhishek on 3/29/2015.
 */
public class MyAdapter extends ArrayAdapter<String>{

    Context contextLayout;

    String[] path;

    Bitmap bitmap;

    Uri uri;

    public MyAdapter(Context context, String[] songPath) {
        super(context, R.layout.song_list, R.id.songName ,songPath);
        this.contextLayout = context;
        this.path = songPath;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v =convertView;

        MyViewHolder holder = null;

        if(v == null) {

            LayoutInflater inflater = (LayoutInflater) contextLayout.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.song_list, parent, false);

            holder = new MyViewHolder(v);

            v.setTag(holder);

        }else{

            holder = (MyViewHolder)v.getTag();

        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        uri = Uri.parse(path[position]);

        mmr.setDataSource(uri.getPath());

        holder.name.setText(uri.getLastPathSegment().replace(".mp3",""));

        holder.album.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));


        byte [] data = mmr.getEmbeddedPicture();
        if(data != null) {

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
            holder.imageView.setImageBitmap(bitmap);
        }else{
            holder.imageView.setImageResource(R.drawable.image);
        }

        return v;
    }

}

class MyViewHolder{

    TextView name,album;

    ImageView imageView;

    MyViewHolder(View v) {

        this.name = (TextView) v.findViewById(R.id.songName);

        this.album = (TextView) v.findViewById(R.id.album);

        this.imageView = (ImageView) v.findViewById(R.id.song_image);

    }
}