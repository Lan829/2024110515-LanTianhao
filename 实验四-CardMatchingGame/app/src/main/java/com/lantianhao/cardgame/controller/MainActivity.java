package com.lantianhao.cardgame.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lantianhao.cardgame.R;
import com.lantianhao.cardgame.data.HighScoreStore;
import com.lantianhao.cardgame.model.Player;

/**
 * 主 Activity（Controller）：显示玩家基本信息与历史最高分，
 * 并作为“修改个人信息”“翻牌游戏”两个二级 Activity 的入口。
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT_INFO = 1;
    private static final String STATE_PLAYER = "state_player";

    private Player player;

    private ImageView avatarImage;
    private TextView valueName;
    private TextView valueGender;
    private TextView valueStudentId;
    private TextView valuePhone;
    private TextView valueEmail;
    private TextView valueHobbies;
    private TextView valueHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();

        if (savedInstanceState != null
                && savedInstanceState.getSerializable(STATE_PLAYER) instanceof Player) {
            player = (Player) savedInstanceState.getSerializable(STATE_PLAYER);
        } else {
            player = createDefaultPlayer();
        }

        findViewById(R.id.button_edit_info).setOnClickListener(v -> openPersonInfo());
        findViewById(R.id.button_play_game).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CardGameActivity.class)));
        renderPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setHighScore(HighScoreStore.getHighScore(this));
            valueHighScore.setText(getString(R.string.points_format, player.getHighScore()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_PLAYER, player);
    }

    private void bindViews() {
        avatarImage = findViewById(R.id.avatar_image);
        valueName = findViewById(R.id.value_name);
        valueGender = findViewById(R.id.value_gender);
        valueStudentId = findViewById(R.id.value_student_id);
        valuePhone = findViewById(R.id.value_phone);
        valueEmail = findViewById(R.id.value_email);
        valueHobbies = findViewById(R.id.value_hobbies);
        valueHighScore = findViewById(R.id.value_high_score);
    }

    /**
     * 默认玩家资料全部来自字符串资源，避免在代码中硬编码界面文字。
     */
    private Player createDefaultPlayer() {
        Player defaultPlayer = new Player();
        defaultPlayer.setName(getString(R.string.default_player_name));
        defaultPlayer.setGender(getString(R.string.default_player_gender_male));
        defaultPlayer.setStudentId(getString(R.string.default_student_id));
        defaultPlayer.setPhone(getString(R.string.default_phone));
        defaultPlayer.setEmail(getString(R.string.default_email));
        defaultPlayer.setHobbies(getString(R.string.default_hobbies));
        defaultPlayer.setAvatarUri(null);
        defaultPlayer.setHighScore(HighScoreStore.getHighScore(this));
        return defaultPlayer;
    }

    private void renderPlayer() {
        valueName.setText(player.getName());
        valueGender.setText(player.getGender());
        valueStudentId.setText(player.getStudentId());
        valuePhone.setText(player.getPhone());
        valueEmail.setText(player.getEmail());
        valueHobbies.setText(player.getHobbies());
        valueHighScore.setText(getString(R.string.points_format, player.getHighScore()));
        renderAvatar();
    }

    private void renderAvatar() {
        String avatarUri = player.getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            avatarImage.setImageURI(Uri.parse(avatarUri));
        } else {
            avatarImage.setImageResource(R.drawable.ic_avatar_placeholder);
        }
        avatarImage.setClipToOutline(true);
    }

    private void openPersonInfo() {
        Intent intent = new Intent(this, PersonInfoActivity.class);
        intent.putExtra(Player.EXTRA_NAME, player.getName());
        intent.putExtra(Player.EXTRA_GENDER, player.getGender());
        intent.putExtra(Player.EXTRA_STUDENT_ID, player.getStudentId());
        intent.putExtra(Player.EXTRA_PHONE, player.getPhone());
        intent.putExtra(Player.EXTRA_EMAIL, player.getEmail());
        intent.putExtra(Player.EXTRA_HOBBIES, player.getHobbies());
        intent.putExtra(Player.EXTRA_AVATAR_URI, player.getAvatarUri());
        startActivityForResult(intent, REQUEST_EDIT_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_INFO && resultCode == RESULT_OK && data != null) {
            player.setName(data.getStringExtra(Player.EXTRA_NAME));
            player.setGender(data.getStringExtra(Player.EXTRA_GENDER));
            player.setStudentId(data.getStringExtra(Player.EXTRA_STUDENT_ID));
            player.setPhone(data.getStringExtra(Player.EXTRA_PHONE));
            player.setEmail(data.getStringExtra(Player.EXTRA_EMAIL));
            player.setHobbies(data.getStringExtra(Player.EXTRA_HOBBIES));
            player.setAvatarUri(data.getStringExtra(Player.EXTRA_AVATAR_URI));
            renderPlayer();
        }
    }
}
