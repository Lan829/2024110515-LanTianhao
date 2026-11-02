package com.lantianhao.cardgame.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.lantianhao.cardgame.R;
import com.lantianhao.cardgame.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 个人信息 Activity（Controller）：修改姓名、学号、电话、邮件地址、
 * 性别、爱好与头像，点击“保存”后通过 setResult 返回主界面。
 *
 * <p>按实验要求，修改后的信息只保存在内存中（通过 Intent 回传），
 * 不写入文件。</p>
 */
public class PersonInfoActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final String STATE_AVATAR_URI = "state_avatar_uri";

    private ImageView avatarImage;
    private TextInputEditText inputName;
    private TextInputEditText inputStudentId;
    private TextInputEditText inputPhone;
    private TextInputEditText inputEmail;
    private RadioGroup groupGender;
    private CheckBox checkTravel;
    private CheckBox checkReading;
    private CheckBox checkGame;
    private CheckBox checkShopping;

    private String avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        bindViews();

        avatarUri = savedInstanceState != null
                ? savedInstanceState.getString(STATE_AVATAR_URI)
                : getIntent().getStringExtra(Player.EXTRA_AVATAR_URI);
        fillForm();
        renderAvatar();

        findViewById(R.id.button_pick_avatar).setOnClickListener(v -> pickAvatar());
        findViewById(R.id.button_save).setOnClickListener(v -> save());
        findViewById(R.id.button_cancel).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_AVATAR_URI, avatarUri);
    }

    private void bindViews() {
        avatarImage = findViewById(R.id.avatar_image);
        inputName = findViewById(R.id.input_name);
        inputStudentId = findViewById(R.id.input_student_id);
        inputPhone = findViewById(R.id.input_phone);
        inputEmail = findViewById(R.id.input_email);
        groupGender = findViewById(R.id.group_gender);
        checkTravel = findViewById(R.id.check_travel);
        checkReading = findViewById(R.id.check_reading);
        checkGame = findViewById(R.id.check_game);
        checkShopping = findViewById(R.id.check_shopping);
    }

    private void fillForm() {
        Intent intent = getIntent();
        inputName.setText(intent.getStringExtra(Player.EXTRA_NAME));
        inputStudentId.setText(intent.getStringExtra(Player.EXTRA_STUDENT_ID));
        inputPhone.setText(intent.getStringExtra(Player.EXTRA_PHONE));
        inputEmail.setText(intent.getStringExtra(Player.EXTRA_EMAIL));

        String gender = intent.getStringExtra(Player.EXTRA_GENDER);
        boolean female = getString(R.string.default_player_gender_female).equals(gender);
        groupGender.check(female ? R.id.radio_female : R.id.radio_male);

        List<String> hobbies = splitHobbies(intent.getStringExtra(Player.EXTRA_HOBBIES));
        checkTravel.setChecked(hobbies.contains(getString(R.string.hobby_travel)));
        checkReading.setChecked(hobbies.contains(getString(R.string.hobby_reading)));
        checkGame.setChecked(hobbies.contains(getString(R.string.hobby_game)));
        checkShopping.setChecked(hobbies.contains(getString(R.string.hobby_shopping)));
    }

    private List<String> splitHobbies(String hobbies) {
        if (hobbies == null || hobbies.isEmpty()) {
            return new ArrayList<>();
        }
        String separator = getString(R.string.hobby_separator);
        return Arrays.asList(hobbies.split(separator));
    }

    private void pickAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.title_pick_avatar)),
                    REQUEST_PICK_IMAGE);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_gallery, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // 部分相册返回的 Uri 不支持持久化授权，当前会话内仍可显示。
                }
                avatarUri = uri.toString();
                renderAvatar();
            }
        }
    }

    private void renderAvatar() {
        if (avatarUri != null && !avatarUri.isEmpty()) {
            avatarImage.setImageURI(Uri.parse(avatarUri));
        } else {
            avatarImage.setImageResource(R.drawable.ic_avatar_placeholder);
        }
        avatarImage.setClipToOutline(true);
    }

    private void save() {
        String name = textOf(inputName);
        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.error_name_required));
            inputName.requestFocus();
            return;
        }
        String studentId = textOf(inputStudentId);
        if (TextUtils.isEmpty(studentId)) {
            inputStudentId.setError(getString(R.string.error_student_id_required));
            inputStudentId.requestFocus();
            return;
        }

        Intent data = new Intent();
        data.putExtra(Player.EXTRA_NAME, name);
        data.putExtra(Player.EXTRA_STUDENT_ID, studentId);
        data.putExtra(Player.EXTRA_PHONE, textOf(inputPhone));
        data.putExtra(Player.EXTRA_EMAIL, textOf(inputEmail));
        data.putExtra(Player.EXTRA_GENDER, selectedGender());
        data.putExtra(Player.EXTRA_HOBBIES, selectedHobbies());
        data.putExtra(Player.EXTRA_AVATAR_URI, avatarUri);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private String selectedGender() {
        boolean female = groupGender.getCheckedRadioButtonId() == R.id.radio_female;
        return getString(female
                ? R.string.default_player_gender_female
                : R.string.default_player_gender_male);
    }

    private String selectedHobbies() {
        List<String> hobbies = new ArrayList<>();
        addIfChecked(hobbies, checkTravel, R.string.hobby_travel);
        addIfChecked(hobbies, checkReading, R.string.hobby_reading);
        addIfChecked(hobbies, checkGame, R.string.hobby_game);
        addIfChecked(hobbies, checkShopping, R.string.hobby_shopping);
        return TextUtils.join(getString(R.string.hobby_separator), hobbies);
    }

    private void addIfChecked(List<String> hobbies, CheckBox checkBox, int stringRes) {
        if (checkBox.isChecked()) {
            hobbies.add(getString(stringRes));
        }
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
