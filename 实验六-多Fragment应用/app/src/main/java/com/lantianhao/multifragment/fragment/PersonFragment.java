package com.lantianhao.multifragment.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.lantianhao.multifragment.R;
import com.lantianhao.multifragment.data.ObjectStore;
import com.lantianhao.multifragment.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 个人信息 Fragment：修改头像与基本资料，并通过对象序列化保存到
 * openFileOutput("player.dat")，重新打开应用后恢复。
 */
public class PersonFragment extends Fragment {

    private static final int REQUEST_PICK_IMAGE = 1;

    private Player player;
    private String avatarUri;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);

        player = ObjectStore.read(requireContext(), ObjectStore.PLAYER_FILE);
        if (player == null) {
            player = createDefaultPlayer();
        }
        avatarUri = player.getAvatarUri();
        fillForm();
        renderAvatar();

        view.findViewById(R.id.button_pick_avatar).setOnClickListener(v -> pickAvatar());
        view.findViewById(R.id.button_save).setOnClickListener(v -> save());
    }

    private void bindViews(View view) {
        avatarImage = view.findViewById(R.id.avatar_image);
        inputName = view.findViewById(R.id.input_name);
        inputStudentId = view.findViewById(R.id.input_student_id);
        inputPhone = view.findViewById(R.id.input_phone);
        inputEmail = view.findViewById(R.id.input_email);
        groupGender = view.findViewById(R.id.group_gender);
        checkTravel = view.findViewById(R.id.check_travel);
        checkReading = view.findViewById(R.id.check_reading);
        checkGame = view.findViewById(R.id.check_game);
        checkShopping = view.findViewById(R.id.check_shopping);
    }

    private Player createDefaultPlayer() {
        Player defaultPlayer = new Player();
        defaultPlayer.setName(getString(R.string.default_player_name));
        defaultPlayer.setGender(getString(R.string.default_player_gender_male));
        defaultPlayer.setStudentId(getString(R.string.default_student_id));
        defaultPlayer.setPhone(getString(R.string.default_phone));
        defaultPlayer.setEmail(getString(R.string.default_email));
        defaultPlayer.setHobbies(getString(R.string.default_hobbies));
        return defaultPlayer;
    }

    private void fillForm() {
        inputName.setText(player.getName());
        inputStudentId.setText(player.getStudentId());
        inputPhone.setText(player.getPhone());
        inputEmail.setText(player.getEmail());

        boolean female = getString(R.string.default_player_gender_female)
                .equals(player.getGender());
        groupGender.check(female ? R.id.radio_female : R.id.radio_male);

        List<String> hobbies = splitHobbies(player.getHobbies());
        checkTravel.setChecked(hobbies.contains(getString(R.string.hobby_travel)));
        checkReading.setChecked(hobbies.contains(getString(R.string.hobby_reading)));
        checkGame.setChecked(hobbies.contains(getString(R.string.hobby_game)));
        checkShopping.setChecked(hobbies.contains(getString(R.string.hobby_shopping)));
    }

    private List<String> splitHobbies(String hobbies) {
        if (hobbies == null || hobbies.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(hobbies.split(getString(R.string.hobby_separator)));
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
            Toast.makeText(requireContext(), R.string.error_no_gallery, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // 部分相册返回的 Uri 不支持持久化授权，本次显示不受影响。
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

        player.setName(name);
        player.setStudentId(studentId);
        player.setPhone(textOf(inputPhone));
        player.setEmail(textOf(inputEmail));
        player.setGender(selectedGender());
        player.setHobbies(selectedHobbies());
        player.setAvatarUri(avatarUri);

        ObjectStore.write(requireContext(), ObjectStore.PLAYER_FILE, player);
        Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
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
