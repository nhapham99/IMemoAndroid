package com.lnb.imemo.Presentation.TagsSetting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.TagsSetting.RecyclerViewAdapter.TagManagerRecyclerViewAdapter;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.Utils;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class TagsSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView managerTagRecyclerView;
    private ImageButton backButton;
    private TextView addTagButton;
    private TextInputLayout tagNameTextField;
    private View colorPicker;
    private MaterialCheckBox isDefaultTagCheckBox;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private Button createTagButton;
    private BottomSheetDialog bottomSheetDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "TagsSettingActivity";

    private TagManagerRecyclerViewAdapter tagManagerAdapter;
    private TagSettingViewModel viewModel;
    private int currentPositionTagChoose = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_setting);
        init();
    }

    private void init() {
        managerTagRecyclerView = findViewById(R.id.manager_tag_recyclerView);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
        addTagButton = findViewById(R.id.add_tag_button);
        addTagButton.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getTags();
            }
        });

        tagManagerAdapter = new TagManagerRecyclerViewAdapter();
        managerTagRecyclerView.setAdapter(tagManagerAdapter);
        managerTagRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewModel = new TagSettingViewModel();
        viewModel.getTags();
        tagManagerAdapter.getManagerTagOptionListener().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer position) throws Exception {
                currentPositionTagChoose = position;
                showBottomDialog();
            }
        });
        subscribeGetAllTagsObservable();
    }

    private void showBottomDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.manager_tag_bottom_dialog_layout, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        TextView managerDefaultTag = view.findViewById(R.id.manager_tag_default);
        TextView editTag = view.findViewById(R.id.manager_tag_edit);
        TextView deleteTag = view.findViewById(R.id.manager_tag_delete);
        if (tagManagerAdapter.getListTags().get(currentPositionTagChoose).getIsDefault()) {
            managerDefaultTag.setText("Bỏ mặc định");
            managerDefaultTag.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_undefault), null, null, null);
        } else {
            managerDefaultTag.setText("Đặt làm mặc định");
            managerDefaultTag.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_checked), null, null, null);
        }
        managerDefaultTag.setOnClickListener(this);
        editTag.setOnClickListener(this);
        deleteTag.setOnClickListener(this);
        bottomSheetDialog.show();
    }

    private void subscribeGetAllTagsObservable() {
        viewModel.getViewModelLiveData().observe(this, new Observer<ResponseRepo>() {
            @Override
            public void onChanged(ResponseRepo responseRepo) {
                String key = responseRepo.getKey();
                if (key == Constant.GET_ALL_TAGS_KEY) {
                    Pair<Utils.State, ArrayList<Tags>> response = (Pair<Utils.State, ArrayList<Tags>>) responseRepo.getData();
                    switch (response.first) {
                        case SUCCESS:
                            Log.d(TAG, "onChanged: get all tag");
                            tagManagerAdapter.updateListTags(response.second);
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            break;
                        case FAILURE:
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(TagsSettingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(TagsSettingActivity.this, "Lỗi Internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key == Constant.CREATE_TAG_KEY) {
                    Log.d(TAG, "onChanged: ");
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Thêm thẻ thành công", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi Internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key == Constant.UPDATE_TAGS_KEY) {
                    Log.d(TAG, "onChanged: update tag");
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            Toast.makeText(TagsSettingActivity.this, "Cập nhật thẻ thành công", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi Internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (key == Constant.DELETE_TAG_KEY) {
                    Log.d(TAG, "onChanged: delete tag");
                    Utils.State state = (Utils.State) responseRepo.getData();
                    switch (state) {
                        case SUCCESS:
                            Toast.makeText(TagsSettingActivity.this, "Xóa thẻ thành công", Toast.LENGTH_SHORT).show();
                            break;
                        case FAILURE:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                            break;
                        case NO_INTERNET:
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TagsSettingActivity.this, "Lỗi Internet", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
    }

    private void showCreateTag() {
        viewModel.newTag.resetTag();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_tag_layout, null);
        dialogBuilder.setView(view);

        tagNameTextField = view.findViewById(R.id.input_tag_name_textfield);
        colorPicker = view.findViewById(R.id.create_tag_color_picker);
        isDefaultTagCheckBox = view.findViewById(R.id.create_tag_is_default_tag);
        createTagButton = view.findViewById(R.id.create_tag_button);
        ImageButton dialogEscape = view.findViewById(R.id.add_tag_escape);

        colorPicker.setOnClickListener(this);
        createTagButton.setOnClickListener(this);
        alertDialog = dialogBuilder.create();
        dialogEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showEditTag(Tags tag) {
        viewModel.newTag = tag;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.create_new_tag_layout, null);
        dialogBuilder.setView(view);

        tagNameTextField = view.findViewById(R.id.input_tag_name_textfield);
        colorPicker = view.findViewById(R.id.create_tag_color_picker);
        isDefaultTagCheckBox = view.findViewById(R.id.create_tag_is_default_tag);
        createTagButton = view.findViewById(R.id.create_tag_button);
        createTagButton.setText("Cập nhật");
        ImageButton dialogEscape = view.findViewById(R.id.add_tag_escape);

        tagNameTextField.getEditText().setText(tag.getName());
        colorPicker.getBackground().setColorFilter(Color.parseColor(tag.getColor()), PorterDuff.Mode.SRC_ATOP);
        isDefaultTagCheckBox.setChecked(tag.getIsDefault());

        colorPicker.setOnClickListener(this);
        createTagButton.setOnClickListener(this);
        alertDialog = dialogBuilder.create();
        dialogEscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showColorPicker() {
        new ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Ok",
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                Log.d(TAG, "onColorSelected: " + envelope);
                                colorPicker.getBackground().setTint(envelope.getColor());
                                String hexColor = String.format("#%06X", (0xFFFFFF & envelope.getColor()));
                                viewModel.newTag.setColor(hexColor);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();
    }

    private void createNewTag() {
        if (tagNameTextField.getEditText().getText().toString().equals(null)) {
            tagNameTextField.setErrorEnabled(true);
            tagNameTextField.setError("Bạn cần điền tên thẻ");
        } else if (tagManagerAdapter.getListDefaultTags().size() >= 10) {
            Toast.makeText(this, "Bạn chỉ có thẻ tạo nhiều nhất 10 thẻ mặc định", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.newTag.setName(tagNameTextField.getEditText().getText().toString());
            Log.d(TAG, "createNewTag: " + isDefaultTagCheckBox.isChecked());
            viewModel.newTag.setIsDefault(isDefaultTagCheckBox.isChecked());
            viewModel.createNewTag();
        }
    }

    private void editTag() {
        if (tagNameTextField.getEditText().getText().toString().equals(null)) {
            tagNameTextField.setErrorEnabled(true);
            tagNameTextField.setError("Bạn cần điền tên thẻ");
        } else if (tagManagerAdapter.getListDefaultTags().size() >= 10) {
            Toast.makeText(this, "Bạn chỉ có thẻ tạo nhiều nhất 10 thẻ mặc định", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.newTag.setName(tagNameTextField.getEditText().getText().toString());
            Log.d(TAG, "createNewTag: " + isDefaultTagCheckBox.isChecked());
            viewModel.newTag.setIsDefault(isDefaultTagCheckBox.isChecked());
            viewModel.updateTag(viewModel.newTag);
        }
    }

    private void managerDefaultTag() {
        progressBar.setVisibility(View.VISIBLE);
        Tags tags = tagManagerAdapter.getListTags().get(currentPositionTagChoose);
        if (tags.getIsDefault()) {
            tags.setIsDefault(false);
            viewModel.updateTag(tags);
        } else {
            tags.setIsDefault(true);
            if (tagManagerAdapter.getListDefaultTags().size() >= 10) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Bạn chỉ có thẻ tạo nhiều nhất 10 thẻ mặc định", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.updateTag(tags);
            }
        }
    }

    private void deleteTag() {
        Tags tags = tagManagerAdapter.getListTags().get(currentPositionTagChoose);
        viewModel.deleteTag(tags);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.add_tag_button:
                showCreateTag();
                break;
            case R.id.create_tag_button:
                if (createTagButton.getText().toString().equals("Cập nhật")) {
                    Log.d(TAG, "onClick: edit");
                    editTag();
                } else {
                    createNewTag();
                }
                break;
            case R.id.create_tag_color_picker:
                showColorPicker();
                break;
            case R.id.manager_tag_default:
                bottomSheetDialog.dismiss();
                managerDefaultTag();
                break;
            case R.id.manager_tag_edit:
                bottomSheetDialog.dismiss();
                showEditTag(tagManagerAdapter.getListTags().get(currentPositionTagChoose));
                break;
            case R.id.manager_tag_delete:
                bottomSheetDialog.dismiss();
                deleteTag();
                break;
        }
    }

}