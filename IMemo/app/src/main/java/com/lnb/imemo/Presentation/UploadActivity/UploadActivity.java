package com.lnb.imemo.Presentation.UploadActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.lnb.imemo.Model.Diary;
import com.lnb.imemo.Model.Link;
import com.lnb.imemo.Model.PersonProfile;
import com.lnb.imemo.Model.Resource;
import com.lnb.imemo.Model.ResponseRepo;
import com.lnb.imemo.Model.Tags;
import com.lnb.imemo.Presentation.PickTag.PickTagsActivity;
import com.lnb.imemo.Presentation.PreviewLink.PreviewActivity;
import com.lnb.imemo.Presentation.UploadActivity.Adapter.TagRecyclerViewAdapter;
import com.lnb.imemo.Presentation.UploadActivity.Adapter.UploadRecyclerViewAdapter;
import com.lnb.imemo.R;
import com.lnb.imemo.Utils.Constant;
import com.lnb.imemo.Utils.DateHelper;
import com.lnb.imemo.Utils.FileMetaData;
import com.lnb.imemo.Utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UploadActivity";
    private static final int SPEECH_TO_TEXT_CONTENT = 4;
    private static final int SPEECH_TO_TEXT_TITLE = 5;
    private TextView createMemoDate;
    private TextView createMemoTitle;
    private TextView createMemoContent;
    private Button createMemoButton;
    private RecyclerView createMemoTagsRecyclerView;
    private final Calendar diaryTime = Calendar.getInstance();
    private TagRecyclerViewAdapter tagsAdapter;
    ArrayList<Object> listObject = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    // var
    private final int GET_FILE_CODE = 1;
    private final int GET_TAGS = 2;
    private final int GET_PREVIEW_LINK = 3;
    private UploadViewModel viewModel;
    private UploadRecyclerViewAdapter resourceAdapter;
    private Boolean isSharedMemo;
    private RelativeLayout speechToTextTitle;
    private RelativeLayout speechToTextContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_upload);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        // init ui
        // ui
        ImageButton escapeButton = findViewById(R.id.create_memo_escape);
        escapeButton.setOnClickListener(this);
        LinearLayout datePicker = findViewById(R.id.create_memo_date);
        datePicker.setOnClickListener(this);
        createMemoDate = findViewById(R.id.date_textView);
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEEE', 'dd' Thg 'MM' 'yyyy', 'HH:mm");
        String[] splitDate = currentDateFormat.format(currentTime).split(",");
        createMemoDate.setText(DateHelper.convertDate(splitDate[0]) + "," + splitDate[1]);
        createMemoTitle = findViewById(R.id.create_memo_title);
        createMemoContent = findViewById(R.id.create_memo_content);
        createMemoButton = findViewById(R.id.create_memo_button);
        createMemoButton.setOnClickListener(this);
        RecyclerView createMemoResourceRecyclerView = findViewById(R.id.create_memo_resource_recyclerView);
        LinearLayout uploadFile = findViewById(R.id.upload_memo_file);
        uploadFile.setOnClickListener(this);
        LinearLayout uploadTags = findViewById(R.id.upload_memo_tag);
        uploadTags.setOnClickListener(this);
        LinearLayout uploadLinks = findViewById(R.id.upload_memo_link);
        uploadLinks.setOnClickListener(this);
        speechToTextContent = findViewById(R.id.memo_content_speech_to_text_button);
        speechToTextContent.setOnClickListener(this);
        speechToTextTitle = findViewById(R.id.memo_title_speech_to_text_button);
        speechToTextTitle.setOnClickListener(this);

        createMemoTagsRecyclerView = findViewById(R.id.upload_memo_item_tag_list);

        createMemoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createMemoButton.setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // init var
        viewModel = new UploadViewModel(this);
        subscribeViewModelObservable();

        resourceAdapter = new UploadRecyclerViewAdapter();
        createMemoResourceRecyclerView.setAdapter(resourceAdapter);
        createMemoResourceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        if (getIntent().getStringExtra(Constant.GET_FILE_CODE) != null) {
            Uri uri = Uri.parse(getIntent().getStringExtra(Constant.GET_FILE_CODE));
            FileMetaData fileMetaData = FileMetaData.getFileMetaData(this, uri);
            if (fileMetaData != null) {
                Resource resource = new Resource();
                resource.setName(fileMetaData.displayName);
                resource.setType(fileMetaData.mimeType);
                resource.setUploading(true);
                resourceAdapter.addItem(resource);
                viewModel.getUploadDiary().getResources().add(resource);
                viewModel.uploadFile(fileMetaData, uri);
            }
        } else if (getIntent().getParcelableArrayListExtra(Constant.GET_TAGS_CODE) != null
                && getIntent().getStringArrayListExtra("arrayTagIds") != null) {
            tagsAdapter = new TagRecyclerViewAdapter(getIntent().getParcelableArrayListExtra(Constant.GET_TAGS_CODE));
            createMemoTagsRecyclerView.setVisibility(View.VISIBLE);
            createMemoTagsRecyclerView.setAdapter(tagsAdapter);
            createMemoTagsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false));
            viewModel.getUploadDiary().setTagIds(getIntent().getStringArrayListExtra("arrayTagIds"));
            subscribeRemoveTag();
        } else if (getIntent().getParcelableExtra(Constant.GET_LINKS_CODE) != null) {
            Link link = getIntent().getParcelableExtra(Constant.GET_LINKS_CODE);
            Log.d(TAG, "init: " + link.toString());
            if (viewModel.getUploadDiary().getLinks() == null) {
                viewModel.getUploadDiary().createListLinks();
            }
            viewModel.getUploadDiary().getLinks().add(link);
            resourceAdapter.addItem(link);
        } else if (getIntent().getParcelableExtra("diary_edit") != null) {
            isSharedMemo = getIntent().getBooleanExtra("is_shared_memo", false);
            if (isSharedMemo) {
                Diary<PersonProfile> diary = getIntent().getParcelableExtra("diary_edit");
                PersonProfile user = getIntent().getParcelableExtra("user");
                diary.setUser(user);
                assert diary != null;
                setupViewForEditSharedMemo(diary);
            } else {
                Diary<String> diary = getIntent().getParcelableExtra("diary_edit");
                String user = getIntent().getParcelableExtra("user");
                diary.setUser(user);
                assert diary != null;
                setupViewForEdit(diary);
            }
        }

        if (getIntent().getStringExtra("content_memo") != null) {
            String content = getIntent().getStringExtra("content_memo");
            viewModel.getUploadDiary().setContent(content);
            createMemoContent.setText(content);
        }

        subscribeUploadRecyclerViewObservable();
    }

    private void setupViewForEdit(Diary<String> diary) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            diaryTime.setTime(simpleDateFormat.parse(diary.getTime()));
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEEE', 'dd' Thg 'MM' 'yyyy', 'HH:mm");
            Log.d(TAG, "setupViewForEdit: " + diaryTime.getTime().toString());
            String[] splitDate = currentDateFormat.format(diaryTime.getTime()).split(",");
            createMemoDate.setText(DateHelper.convertDate(splitDate[0]) + "," + splitDate[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        createMemoTitle.setText(diary.getTitle());
        createMemoContent.setText(diary.getContent());
        listObject.addAll(diary.getResources());
        listObject.addAll(diary.getLinks());
        resourceAdapter.setData(listObject);
        if (diary.getTags().size() == 0) {
            createMemoTagsRecyclerView.setVisibility(View.GONE);
        } else {
            tagsAdapter = new TagRecyclerViewAdapter(diary.getTags());
            tagsAdapter.setShareMemo(isSharedMemo);
            createMemoTagsRecyclerView.setVisibility(View.VISIBLE);
            createMemoTagsRecyclerView.setAdapter(tagsAdapter);
            createMemoTagsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false));
        }
        ArrayList<String> tagIds = new ArrayList<>();
        for (Tags tags : diary.getTags()) {
            tagIds.add(tags.getId());
        }
        diary.setTagIds(tagIds);
        viewModel.getUploadDiary().setDiary(diary);
        createMemoButton.setText("Cập nhật");

    }

    private void setupViewForEditSharedMemo(Diary<PersonProfile> diary) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            diaryTime.setTime(simpleDateFormat.parse(diary.getTime()));
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEEE', 'dd' Thg 'MM' 'yyyy', 'HH:mm");
            Log.d(TAG, "setupViewForEdit: " + diaryTime.getTime().toString());
            String[] splitDate = currentDateFormat.format(diaryTime.getTime()).split(",");
            createMemoDate.setText(DateHelper.convertDate(splitDate[0]) + "," + splitDate[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        createMemoTitle.setText(diary.getTitle());
        createMemoContent.setText(diary.getContent());
        listObject.addAll(diary.getResources());
        listObject.addAll(diary.getLinks());
        resourceAdapter.setData(listObject);
        if (diary.getTags().size() == 0) {
            createMemoTagsRecyclerView.setVisibility(View.GONE);
        } else {
            tagsAdapter = new TagRecyclerViewAdapter(diary.getTags());
            tagsAdapter.setShareMemo(isSharedMemo);
            createMemoTagsRecyclerView.setVisibility(View.VISIBLE);
            createMemoTagsRecyclerView.setAdapter(tagsAdapter);
            createMemoTagsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false));
        }
        ArrayList<String> tagIds = new ArrayList<>();
        for (Tags tags : diary.getTags()) {
            tagIds.add(tags.getId());
        }

        diary.setTagIds(tagIds);
        viewModel.getUploadDiary().setDiary(diary);
        createMemoButton.setText("Cập nhật");
    }


    private void subscribeUploadRecyclerViewObservable() {
        resourceAdapter.getUploadRecyclerViewObservable().subscribe(new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Integer position) {
                Log.d(TAG, "accept: " + position);
                Log.d(TAG, "accept: remove" + viewModel.getUploadDiary().getResources().toString());
                Resource resource = (Resource) viewModel.getUploadDiary().getResources().get(position);
                viewModel.getUploadDiary().getResources().remove(resource);
                resourceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        });
    }

    private void subscribeViewModelObservable() {
       viewModel.getViewModelObservable().observe(this, responseRepo -> {
           Log.d(TAG, "onChanged: " + responseRepo.toString());
           String key = responseRepo.getKey();
           if (key.equals(Constant.UPLOAD_FILE_KEY)) {
               Pair<Utils.State, Object> response = (Pair<Utils.State, Object>) responseRepo.getData();
               switch (response.first) {
                   case SUCCESS:
                       Resource resource = (Resource) viewModel.getUploadDiary().getResources().get(viewModel.getUploadDiary().getResources().size() - 1);
                       resourceAdapter.removeItemAt(viewModel.getUploadDiary().getResources().size() - 1);
                       viewModel.getUploadDiary().getResources().remove(resource);
                       resourceAdapter.addItem(response.second);
                       Object object = response.second;
                       if (object instanceof Link) {
                           viewModel.getUploadDiary().getLinks().add((Link) object);
                       } else if (object instanceof Resource) {
                           viewModel.getUploadDiary().getResources().add((Resource) object);
                           Log.d(TAG, "onChanged: " + viewModel.getUploadDiary().getResources().size());
                       }
                       break;
                   case FAILURE:
                       Toast.makeText(UploadActivity.this, "Lỗi tải file lên", Toast.LENGTH_SHORT).show();
                       break;
                   case NO_INTERNET:
                       Toast.makeText(UploadActivity.this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                       break;
               }
           } else if (key.equals(Constant.CREATE_DIARY_KEY)) {
               Log.d(TAG, "onChanged: " + responseRepo.getData().toString());
           } else if (key.equals(Constant.UPDATE_DIARY_KEY)) {
               Utils.State state = (Utils.State) responseRepo.getData();
               switch (state) {
                   case SUCCESS:
                       Toast.makeText(UploadActivity.this, "Cập nhật memo thành công", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent();
                       intent.putExtra("update_memo", viewModel.getUploadDiary());
                       if (isSharedMemo) {
                           PersonProfile personProfile = (PersonProfile) viewModel.getUploadDiary().getUser();
                           intent.putExtra("user", personProfile);
                       } else {
                           String user = (String) viewModel.getUploadDiary().getUser();
                           intent.putExtra("user", user);
                       }
                       Log.d(TAG, "subscribeViewModelObservable: " + viewModel.getUploadDiary());
                       setResult(Activity.RESULT_OK, intent);
                       finish();
                       break;
                   case FAILURE:
                       Toast.makeText(UploadActivity.this, "Cập nhật memo không thành công", Toast.LENGTH_SHORT).show();
                       break;
                   case NO_INTERNET:
                       Toast.makeText(UploadActivity.this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
                       break;
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
        tagsAdapter.getRemoveTagObservable().subscribe(new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                 disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Integer position) {
                Tags tag = (Tags) viewModel.getUploadDiary().getTags().get(position);
                viewModel.getUploadDiary().getTags().remove(tag);

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FILE_CODE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                FileMetaData fileMetaData = FileMetaData.getFileMetaData(this, data.getData());
                Resource resource = new Resource();
                if (fileMetaData != null) {
                    resource.setName(fileMetaData.displayName);
                    resource.setType(fileMetaData.mimeType);
                    resource.setUploading(true);
                    resourceAdapter.addItem(resource);
                    viewModel.getUploadDiary().getResources().add(resource);
                    viewModel.uploadFile(fileMetaData, data.getData());
                } else {
                    Toast.makeText(this, "Lỗi tải file vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
            //subscribeUploadRecyclerViewObservable();
        } else if (requestCode == GET_TAGS) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + data.getStringArrayListExtra("arrayTagIds").toString());
                ArrayList<Tags> listTags = data.getParcelableArrayListExtra("arrayTag");
                tagsAdapter = new TagRecyclerViewAdapter(listTags);
                createMemoTagsRecyclerView.setVisibility(View.VISIBLE);
                createMemoTagsRecyclerView.setAdapter(tagsAdapter);
                createMemoTagsRecyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false));
                viewModel.getUploadDiary().setTagIds(data.getStringArrayListExtra("arrayTagIds"));
                viewModel.getUploadDiary().setTags(listTags);
                //subscribeRemoveTag();
            } else {
                Log.d(TAG, "onActivityResult: failure" );
            }
        } else if (requestCode == GET_PREVIEW_LINK) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + data.getParcelableExtra("previewLink").toString());
                Link link = data.getParcelableExtra("previewLink");
                if (viewModel.getUploadDiary().getLinks() == null) {
                    viewModel.getUploadDiary().createListLinks();
                }
                viewModel.getUploadDiary().getLinks().add(link);
                resourceAdapter.addItem(link);
            } else {
                Log.d(TAG, "onActivityResult: failure");
            }
        } else if (requestCode == SPEECH_TO_TEXT_TITLE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                viewModel.getUploadDiary().setTitle(matches.get(0));
                createMemoTitle.setText(matches.get(0));
            }
        } else if (requestCode == SPEECH_TO_TEXT_CONTENT) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                viewModel.getUploadDiary().setContent(matches.get(0));
                createMemoContent.setText(matches.get(0));
            }
        }

    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
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
            new TimePickerDialog(UploadActivity.this, timeSetListener, diaryTime.get(Calendar.HOUR_OF_DAY), diaryTime.get(Calendar.MINUTE), true).show();
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
        if (createMemoContent.getText().length() != 0) {
            viewModel.getUploadDiary().setContent(createMemoContent.getText().toString());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        viewModel.getUploadDiary().setTime(simpleDateFormat.format(diaryTime.getTime()));
        Intent intent = new Intent();
        intent.putExtra("create_memo", viewModel.getUploadDiary());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void updateMemo() {
        viewModel.getUploadDiary().setTitle(createMemoTitle.getText().toString());
        if (createMemoContent.getText().length() != 0) {
            viewModel.getUploadDiary().setContent(createMemoContent.getText().toString());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        viewModel.getUploadDiary().setTime(simpleDateFormat.format(diaryTime.getTime()));
        if (isSharedMemo) {
            viewModel.updateDiaryForSharedMemo();
        } else {
            viewModel.updateDiary();
        }
    }

    private void startSpeechToText(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
        startActivityForResult(intent, code);
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_memo_button:
                if (createMemoButton.getText().toString().equals("Cập nhật")) {
                    updateMemo();
                } else {
                    createMemo();
                }
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
            case R.id.memo_title_speech_to_text_button:
                startSpeechToText(SPEECH_TO_TEXT_TITLE);
                break;
            case R.id.memo_content_speech_to_text_button:
                startSpeechToText(SPEECH_TO_TEXT_CONTENT);
                break;

        }
    }


}