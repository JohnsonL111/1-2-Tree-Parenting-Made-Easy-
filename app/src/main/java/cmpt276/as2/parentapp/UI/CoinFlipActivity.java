package cmpt276.as2.parentapp.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;

import java.util.ArrayList;

import cmpt276.as2.parentapp.R;
import cmpt276.as2.parentapp.model.ChildManager;
import cmpt276.as2.parentapp.model.CoinFlip;
import cmpt276.as2.parentapp.model.CoinFlipMenuAdapter;

/**
 * The activity to handle th coin flip ui, will let user chose between head and tail and show a animation of coin toss then show the result.
 */
public class CoinFlipActivity extends AppCompatActivity {
    private CoinFlip coinFlip;
    public static final String COIN_TAG = "CoinToss";
    public static final String COIN_HISTORY = "TossHistory";
    private ChildManager childManager;

    private ViewPager2 viewPager2;
    private CoinFlipMenuAdapter adapter;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        getChildManager();
        coinFlip = new CoinFlip(childManager.getNameList(), getData(COIN_HISTORY), this);
        videoView = findViewById(R.id.flip);
        viewPager2 = findViewById(R.id.coin_viewpager2);

        setPageAdapter();
        viewPager2.setAdapter(adapter);

        setBoardCallBack();
    }

    private void getChildManager() {
        SharedPreferences prefs = this.getSharedPreferences(EditChildActivity.CHILD_LIST_TAG, MODE_PRIVATE);
        if (!prefs.contains(EditChildActivity.CHILD_LIST)) {
            childManager = ChildManager.getInstance();
        } else {
            Gson gson = new Gson();
            childManager = gson.fromJson(prefs.getString(EditChildActivity.CHILD_LIST, ""), ChildManager.class);
        }
    }

    private void setPageAdapter() {
        if (coinFlip.getPickerList().size() != 0) {
            adapter = new CoinFlipMenuAdapter(this,
                    getString(R.string.coin_toss_picker,
                            coinFlip.getPickerList().get(0)),
                    getResources().getStringArray(R.array.coin_two_side_name));
        } else {
            adapter = new CoinFlipMenuAdapter(this,
                    "",
                    getResources().getStringArray(R.array.coin_two_side_name));
        }
    }

    private void setBoardCallBack() {
        CoinFlipMenuAdapter.clickObserverAnimation obs = () -> tossCoin();
        CoinFlipMenuAdapter.clickObserverEditChild obsEdit = () ->
        {
            Intent childIntent = EditChildActivity.makeIntent(CoinFlipActivity.this);
            startActivity(childIntent);
        };

        adapter.registerChangeCallBack(obs);
        adapter.registerChangeCallBack(obsEdit);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        reset();
    }

    private void tossCoin() {
        coinFlip.setResult(adapter.getResult());
        coinFlip.tossTheCoin();

        Uri videoUri;
        if (coinFlip.getResultInt() == 0) {
            videoUri = Uri.parse(getString(R.string.resources) + getPackageName() + "/" + R.raw.full_h1);
        } else {
            videoUri = Uri.parse(getString(R.string.resources) + getPackageName() + "/" + R.raw.full_t1);
        }

        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.VISIBLE);
        viewPager2.setVisibility(View.INVISIBLE);
        videoView.setZOrderOnTop(true);
        videoView.requestFocus();
        videoView.start();

        saveResult();

        videoView.setOnCompletionListener(mediaPlayer -> showResult());
    }

    private ArrayList getData(String tag) {
        SharedPreferences prefs = this.getSharedPreferences(COIN_TAG, MODE_PRIVATE);
        if (!prefs.contains(tag)) {
            return new ArrayList<String>();
        }
        Gson gson = new Gson();

        return gson.fromJson(prefs.getString(tag, ""), ArrayList.class);
    }

    private void showResult() {
        View v = LayoutInflater.from(this).inflate(R.layout.coin_flip_result, null);
        TextView textView = v.findViewById(R.id.coin_flip_result_text);
        ImageView imageView = v.findViewById(R.id.coin_flip_result_image);

        textView.setText(coinFlip.getResult());
        if (coinFlip.pickerWin()) {
            imageView.setImageResource(R.drawable.win);
        } else {
            imageView.setImageResource(R.drawable.loss);
        }

        AlertDialog.Builder build = new AlertDialog.Builder(this).setView(v)
                .setTitle(R.string.Result)
                .setPositiveButton(R.string.result_repeat_option, (dialogInterface, i) -> reset())
                .setNegativeButton(R.string.result_leave_option, (dialogInterface, i) -> this.finish());

        Dialog dialog = build.create();
        dialog.show();
    }

    private void reset() {
        viewPager2.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        getChildManager();
        coinFlip = new CoinFlip(childManager.getNameList(), getData(COIN_HISTORY), this);

        setPageAdapter();

        viewPager2.setAdapter(adapter);
        setBoardCallBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coin_acitivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_back:
                this.finish();
                return true;
            case R.id.history:
                Intent intent = CoinFlipHistoryActivity.makeIntent(this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveResult() {
        coinFlip.saveResult();
        SharedPreferences prefs = this.getSharedPreferences(COIN_TAG, MODE_PRIVATE);
        SharedPreferences prefsPub = this.getSharedPreferences(EditChildActivity.CHILD_LIST_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        SharedPreferences.Editor editor1 = prefsPub.edit();

        Gson gson = new Gson();
        editor.putString(COIN_HISTORY, gson.toJson(coinFlip.getHistory()));
        childManager.tossCoin();
        editor1.putString(EditChildActivity.CHILD_LIST, gson.toJson(childManager));
        editor.apply();
        editor1.apply();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }
}