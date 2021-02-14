package com.lnb.imemo.Presentation.UploadActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.Adapter.TagRecyclerViewAdapter;
import com.lnb.imemo.Presentation.UploadActivity.Adapter.UploadRecyclerViewAdapter;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.DateHelper;
import com.lnb.imemo.Utils.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.functions.Consumer;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UploadActivity";
    // ui
    private ImageButton escapeButton;
    private LinearLayout datePicker;
    private TextView createMemoDate;
    private TextView createMemoTitle;
    private TextView createMemoContent;
    private Button createMemoButton;
    private RecyclerView createMemoResourceRecyclerView;
    private RecyclerView createMemoTagsRecyclerView;
    private LinearLayout uploadFile;
    private LinearLayout uploadTags;
    private LinearLayout uploadLinks;
    private Calendar diaryTime = Calendar.getInstance();
    private TagRecyclerViewAdapter tagsAdapter;

    // var
    private int GET_FILE_CODE = 1;
    private int GET_TAGS = 2;
    private int GET_PREVIEW_LINK = 3;
    private UploadViewModel viewModel;
    private UploadRecyclerViewAdapter resourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        init();
    }

    private void init() {
        // init ui
        escapeButton = findViewById(R.id.create_memo_escape);
        escapeButton.setOnClickListener(this);
        datePicker = findViewById(R.id.create_memo_date);
        datePicker.setOnClickListener(this);
        createMemoDate = findViewById(R.id.date_textView);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEEE', 'dd' Thg 'MM' 'yyyy', 'HH:mm");
        String[] splitDate = currentDateFormat.format(currentTime).split(",");
        createMemoDate.setText(DateHelper.convertDate(splitDate[0]) + "," + splitDate[1]);
        createMemoTitle = findViewById(R.id.create_memo_title);
        createMemoContent = findViewById(R.id.create_memo_content);
        createMemoButton = findViewById(R.id.create_memo_button);
        createMemoButton.setOnClickListener(this);
        createMemoResourceRecyclerView = findViewById(R.id.create_memo_resource_recyclerView);
        uploadFile = findViewById(R.id.upload_memo_file);
        uploadFile.setOnClickListener(this);
        uploadTags = findViewById(R.id.upload_memo_tag);
        uploadTags.setOnClickListener(this);
        uploadLinks = findViewById(R.id.upload_memo_link);
        uploadLinks.setOnClickListener(this);
        createMemoTagsRecyclerView = findViewById(R.id.upload_memo_item_tag_list);

        createMemoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    createMemoButton.setEnabled(false);
                } else {
                    createMemoButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // init var
        viewModel = new UploadViewModel(this);
        subscribeViewModelObservable();
        resourceAdapter = new UploadRecyclerViewAdapter();
        createMemoResourceRecyclerView.setAdapter(resourceAdapter);
        createMemoResourceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }

    private void subscribeViewModelObservable() {
       viewModel.getViewModelObservable().observe(this, new Observer<ResponseRepo>() {
           @Override
           public void onChanged(ResponseRepo responseRepo) {
               String key = responseRepo.getKey();
               if (key == Constant.UPLOAD_FILE_KEY) {
                   Pair<Utils.State, Object> response = (Pair<Utils.State, Object>) responseRepo.getData();
                   switch (response.first) {
                       case SUCCESS:
                           resourceAdapter.addItem(response.second);
                           break;
                       case FAILURE:
                           Toast.makeText(UploadActivity.this, "Lỗi tải file lên", Toast.LENGTH_SHORT).show();
                           break;
                       case NO_INTERNET:
                           Toast.makeText(UploadActivity.this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                           break;
                   }
               } else if (key == Constant.CREATE_DIARY_KEY) {
                   Log.d(TAG, "onChanged: " + responseRepo.getData().toString());
               }
           }
       });
    }

    private void startUploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, GET_FILE_CODE);
    }

    private void subscribeRemoveTag() {
        tagsAdapter.getRemoveTagObservable().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer position) throws Exception {
                viewModel.getUploadDiary().getTagIds().remove(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FILE_CODE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                viewModel.uploadFile(data.getData());
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
        } else if (requestCode == GET_TAGS) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + data.getStringArrayListExtra("arrayTagIds").toString());
                tagsAdapter = new TagRecyclerViewAdapter(data.getParcelableArrayListExtra("arrayTag"));
                createMemoTagsRecyclerView.setVisibility(View.VISIBLE);
                createMemoTagsRecyclerView.setAdapter(tagsAdapter);
                createMemoTagsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false));
                viewModel.getUploadDiary().setTagIds(data.getStringArrayListExtra("arrayTagIds"));
                subscribeRemoveTag();
            } else {
                Log.d(TAG, "onActivityResult: failure" );
            }
        } else if (requestCode == GET_PREVIEW_LINK) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + data.getParcelableExtra("previewLink").toString());
                viewModel.getUploadDiary().getLinks().add(data.getParcelableExtra("previewLink"));
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                diaryTime.set(Calendar.YEAR, year);
                diaryTime.set(Calendar.MONTH, month);
                diaryTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        diaryTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        diaryTime.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(year, month, dayOfMonth);
                        String dayOfWeek = simpledateformat.format(date);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("', 'dd' Thg 'MM' 'yyyy', 'HH:mm");
                        createMemoDate.setText(DateHelper.convertDate(dayOfWeek) + simpleDateFormat.format(diaryTime.getTime()));

                    }
                };
                new android.app.TimePickerDialog(UploadActivity.this, timeSetListener, diaryTime.get(Calendar.HOUR_OF_DAY), diaryTime.get(Calendar.MINUTE), true).show();
            }
        };
        new DatePickerDialog(UploadActivity.this, dateSetListener, diaryTime.get(Calendar.YEAR), diaryTime.get(Calendar.MONTH), diaryTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void startPickTags() {
        startActivityForResult(new Intent(UploadActivity.this, PickTagsActivity.class), GET_TAGS);
    }

    private void startAddLink() {
        startActivityForResult(new Intent(UploadActivity.this, PreviewActivity.class), GET_PREVIEW_LINK);
    }

    private void createMemo() {
        viewModel.getUploadDiary().setTitle(createMemoTitle.getText().toString());
        viewModel.getUploadDiary().setContent(createMemoContent.getText().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        viewModel.getUploadDiary().setTime(simpleDateFormat.format(diaryTime));
        Log.d(TAG, "createMemo: " + viewModel.getUploadDiary().toString());
        //viewModel.createDiary(viewModel.getUploadDiary());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_memo_button:
                createMemo();
                break;
            case R.id.create_memo_escape:
                finish();
                break;
            case R.id.create_memo_date:
                showDatePicker();
                break;
            case R.id.upload_memo_file:
                startUploadFile();
                break;
            case R.id.upload_memo_tag:
                startPickTags();
                break;
            case R.id.upload_memo_link:
                startAddLink();
                break;

        }
    }


}