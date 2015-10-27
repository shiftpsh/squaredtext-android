package net.shiftstudios.squaredtext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.SVBar;
import com.larswerkman.colorpicker.SaturationBar;
import com.larswerkman.colorpicker.ValueBar;

import net.shiftstudios.view.SquareLayout;
import net.shiftstudios.widget.AdvancedTextView;
import net.shiftstudios.widget.VerticalSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static VerticalSeekBar uiFontSizeSeekBar;

    public static TextView uiTextSizeDisplay;
    public static TextView uiFontPreview;

    public static AdvancedTextView uiMainTextView;

    public static EditText uiMainEditText;

    public static RelativeLayout uiButtonBackground1, uiButtonBackground2,
            uiButtonBackground3, uiButtonBackground4;

    public static ImageView uiMainImageView;

    public static Button uiButton1, uiButton2, uiButton3, uiButton4;

    public static SquareLayout uiMainLayout;

    public static int currentBackgroundColor = 0xff000000;
    public static int currentFontColor = 0xff000000;
    public static int currentShadowColor = 0xff000000;
    public static int currentStrokeColor = 0xff000000;

    public static int tempColor = 0xff000000;
    public static int tempInt = 0;

    public static float currentStrokeRadius = 0;
    public static float currentShadowRadius = 0;

    public static final int MIN_FONT_SIZE = 5;
    public static final int MAX_FONT_SIZE = 200;

    public TextView display;

    // File Explorer Values
    ArrayList<String> str = new ArrayList<>();

    private Boolean firstLvl = true;

    private static final String TAG = "F_PATH";

    private Item[] fileList;
    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private String chosenFile;
    private static final int DIALOG_LOAD_FILE = 1000;

    ListAdapter adapter;

    // Package Determiner Values
    public boolean isKakaoStoryExists = false;
    public boolean isKakaoTalkExists = false;

    // Toast
    public Toast finishToast;

    public boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ImageView icon = (ImageView) findViewById(android.R.id.home);
            FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon
                    .getLayoutParams();
            iconLp.topMargin = iconLp.bottomMargin = iconLp.leftMargin = 0;
            icon.setLayoutParams(iconLp);
        } catch (Exception ignored) {

        }

        this.setTitle("");

        uiFontSizeSeekBar = (VerticalSeekBar) findViewById(R.id.fontSizeSeekBar);
        uiFontSizeSeekBar.setMax(MAX_FONT_SIZE);

        uiTextSizeDisplay = (TextView) findViewById(R.id.fontSizeDisplay);
        uiMainTextView = (AdvancedTextView) findViewById(R.id.mainTextView);

        uiMainEditText = (EditText) findViewById(R.id.mainEditText);

        uiButtonBackground1 = (RelativeLayout) findViewById(R.id.rl1);
        uiButtonBackground2 = (RelativeLayout) findViewById(R.id.rl2);
        uiButtonBackground3 = (RelativeLayout) findViewById(R.id.rl3);
        uiButtonBackground4 = (RelativeLayout) findViewById(R.id.rl4);

        uiButton1 = (Button) findViewById(R.id.buttonBackgroundSelector);
        uiButton2 = (Button) findViewById(R.id.buttonForegroundSelector);
        uiButton3 = (Button) findViewById(R.id.buttonStrokeSelector);
        uiButton4 = (Button) findViewById(R.id.buttonNeonSelector);

        uiFontPreview = (TextView) findViewById(R.id.fontPreview);

        uiMainImageView = (ImageView) findViewById(R.id.mainImageView);
        uiMainImageView.setVisibility(View.INVISIBLE);

        uiMainLayout = (SquareLayout) findViewById(R.id.mainLayout);

        isKakaoStoryExists = isPackageExists("com.kakao.story");
        isKakaoTalkExists = isPackageExists("com.kakao.talk");

        finishToast = Toast.makeText(getApplicationContext(),
                "종료하시려면 취소 키를 한 번 더 눌러주세요", Toast.LENGTH_LONG);

        uiFontSizeSeekBar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        updateFontSizeInternal(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        uiMainEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                updateStringInternal(uiMainEditText.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                updateStringInternal(uiMainEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                updateStringInternal(uiMainEditText.getText().toString());
            }
        });
    }

    public static void updateFontSize(int i) {
        if (i < MIN_FONT_SIZE)
            i = MIN_FONT_SIZE;
        if (i > MAX_FONT_SIZE)
            i = MAX_FONT_SIZE;
        uiFontSizeSeekBar.setProgress(i);
        uiTextSizeDisplay.setText(i + "");
        uiMainTextView.setTextSize(i);
    }

    public void updateFontSizeInternal(int i) {
        if (i < MIN_FONT_SIZE)
            i = MIN_FONT_SIZE;
        if (i > MAX_FONT_SIZE)
            i = MAX_FONT_SIZE;
        uiTextSizeDisplay.setText(i + "");
        uiMainTextView.setTextSize(i);
    }

    public void updateStringInternal(String s) {
        uiMainTextView.setText(s);
    }

    public static void updateBackgroundColor(int color) {
        uiButtonBackground1.setBackgroundColor(color);
        currentBackgroundColor = color;
        uiButton1.setTextColor(isForegroundWhite(color) ? 0xffffffff
                : 0xff000000);
        uiMainImageView.setVisibility(View.INVISIBLE);
        uiMainLayout.setBackgroundColor(color);
    }

    public static void updateFontColor(int color) {
        uiButtonBackground2.setBackgroundColor(color);
        currentFontColor = color;
        uiButton2.setTextColor(isForegroundWhite(color) ? 0xffffffff
                : 0xff000000);
        uiMainTextView.setTextColor(color);
    }

    public static void updateStroke(int color, float radius) {
        uiButtonBackground3.setBackgroundColor(color);
        currentStrokeColor = color;
        currentStrokeRadius = radius;
        uiButton3.setTextColor(isForegroundWhite(color) ? 0xffffffff
                : 0xff000000);
        uiMainTextView.setStrokeColor(color);
        uiMainTextView.setStrokeWidth(radius);
        uiMainTextView.setText(uiMainTextView.getText());
    }

    public static void updateShadow(int color, float radius) {
        uiButtonBackground4.setBackgroundColor(color);
        currentShadowColor = color;
        currentShadowRadius = radius;
        uiButton4.setTextColor(isForegroundWhite(color) ? 0xffffffff
                : 0xff000000);
        uiMainTextView.setShadowLayer(radius, 0.0f, 0.0f, color);
        uiMainTextView.setText(uiMainTextView.getText());
    }

    public static void updateFont(Typeface font) {
        uiFontPreview.setTypeface(font);
        uiMainTextView.setTypeface(font);
    }

    public static boolean isForegroundWhite(int color) {
        return Color.alpha(color) >= 80 && getBrightness(color) <= 128;
    }

    public static double getBrightness(int color) {
        return ((0.2126 * (double) Color.red(color))
                + (0.7152 * (double) Color.green(color)) + (0.0722 * (double) Color
                .blue(color)));
    }

    public void backgroundSelect(View v) {
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_background);

        ColorPicker picker = (ColorPicker) dialog
                .findViewById(R.id.colorPicker1);

        picker.addOpacityBar((OpacityBar) dialog.findViewById(R.id.opacityBar1));
        picker.addSVBar((SVBar) dialog.findViewById(R.id.sVBar1));
        picker.addSaturationBar((SaturationBar) dialog
                .findViewById(R.id.saturationBar1));
        picker.addValueBar((ValueBar) dialog.findViewById(R.id.valueBar1));

        picker.setColor(currentBackgroundColor);
        tempColor = currentBackgroundColor;

        picker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int color) {
                tempColor = color;
            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.button1);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateBackgroundColor(tempColor);
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        Button gallery = (Button) dialog.findViewById(R.id.button3);
        gallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateBackgroundColor(0xFF000000);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(photoPickerIntent, "사진 선택..."), 1);
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    public void textColorSelect(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_foreground);

        ColorPicker picker = (ColorPicker) dialog
                .findViewById(R.id.colorPicker1);

        picker.addOpacityBar((OpacityBar) dialog.findViewById(R.id.opacityBar1));
        picker.addSVBar((SVBar) dialog.findViewById(R.id.sVBar1));
        picker.addSaturationBar((SaturationBar) dialog
                .findViewById(R.id.saturationBar1));
        picker.addValueBar((ValueBar) dialog.findViewById(R.id.valueBar1));

        picker.setColor(currentFontColor);
        tempColor = currentFontColor;

        picker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int color) {
                tempColor = color;
            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.button1);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateFontColor(tempColor);
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    public void strokeColorSelect(View v) {
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_radius);

        ColorPicker picker = (ColorPicker) dialog
                .findViewById(R.id.colorPicker1);

        picker.addOpacityBar((OpacityBar) dialog.findViewById(R.id.opacityBar1));
        picker.addSVBar((SVBar) dialog.findViewById(R.id.sVBar1));
        picker.addSaturationBar((SaturationBar) dialog
                .findViewById(R.id.saturationBar1));
        picker.addValueBar((ValueBar) dialog.findViewById(R.id.valueBar1));

        picker.setColor(currentStrokeColor);
        tempColor = currentStrokeColor;

        SeekBar seek = (SeekBar) dialog.findViewById(R.id.seekBar1);
        seek.setProgress((int) (currentStrokeRadius * 5));
        tempInt = (int) (currentStrokeRadius * 5);
        display = (TextView) dialog.findViewById(R.id.textView1);
        display.setText((double) seek.getProgress() / 5 + "");
        display = (TextView) dialog.findViewById(R.id.textView1);

        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tempInt = progress;
                display.setText((double) progress / 5 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        picker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int color) {
                tempColor = color;
            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.button1);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateStroke(tempColor, ((float) tempInt / 5));
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    public void neonColorSelect(View v) {
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_radius);

        ColorPicker picker = (ColorPicker) dialog
                .findViewById(R.id.colorPicker1);

        picker.addOpacityBar((OpacityBar) dialog.findViewById(R.id.opacityBar1));
        picker.addSVBar((SVBar) dialog.findViewById(R.id.sVBar1));
        picker.addSaturationBar((SaturationBar) dialog
                .findViewById(R.id.saturationBar1));
        picker.addValueBar((ValueBar) dialog.findViewById(R.id.valueBar1));

        picker.setColor(currentShadowColor);
        tempColor = currentShadowColor;

        SeekBar seek = (SeekBar) dialog.findViewById(R.id.seekBar1);
        seek.setProgress((int) (currentShadowRadius * 5));
        tempInt = (int) (currentShadowRadius * 5);
        display = (TextView) dialog.findViewById(R.id.textView1);
        display.setText((double) seek.getProgress() / 5 + "");
        display = (TextView) dialog.findViewById(R.id.textView1);

        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tempInt = progress;
                display.setText((double) progress / 5 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        picker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int color) {
                tempColor = color;
            }
        });

        Button confirm = (Button) dialog.findViewById(R.id.button1);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateShadow(tempColor, ((float) tempInt / 5));
                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0 && requestCode == 1) {
            Log.d("TAG", "");
            Uri photoUri = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), photoUri);
                uiMainImageView.setVisibility(View.VISIBLE);
                uiMainImageView.setImageDrawable(new BitmapDrawable(
                        getResources(), image));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void fontSelect(View v) {
        loadFileList();
        showDialog(DIALOG_LOAD_FILE);
    }

    public void alignSelect(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_align);

        Button[] buttons = new Button[9];

        buttons[0] = (Button) dialog.findViewById(R.id.button1);
        buttons[1] = (Button) dialog.findViewById(R.id.button2);
        buttons[2] = (Button) dialog.findViewById(R.id.button3);
        buttons[3] = (Button) dialog.findViewById(R.id.button4);
        buttons[4] = (Button) dialog.findViewById(R.id.button5);
        buttons[5] = (Button) dialog.findViewById(R.id.button6);
        buttons[6] = (Button) dialog.findViewById(R.id.button7);
        buttons[7] = (Button) dialog.findViewById(R.id.button8);
        buttons[8] = (Button) dialog.findViewById(R.id.button9);

        for (int i = 0; i < 9; i++) {
            buttons[i].setOnClickListener(setAlign(buttons[i]));
        }

        Button dismiss = (Button) dialog.findViewById(R.id.button10);
        dismiss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    public OnClickListener setAlign(View v) {
        final View view = v;
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                int gravity = 0;
                switch (view.getId()) {
                    case R.id.button1:
                        gravity = Gravity.TOP | Gravity.LEFT;
                        break;
                    case R.id.button2:
                        gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        break;
                    case R.id.button3:
                        gravity = Gravity.TOP | Gravity.RIGHT;
                        break;

                    case R.id.button4:
                        gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                        break;
                    case R.id.button5:
                        gravity = Gravity.CENTER_VERTICAL
                                | Gravity.CENTER_HORIZONTAL;
                        break;
                    case R.id.button6:
                        gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                        break;

                    case R.id.button7:
                        gravity = Gravity.BOTTOM | Gravity.LEFT;
                        break;
                    case R.id.button8:
                        gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        break;
                    case R.id.button9:
                        gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        break;
                }

                TextView gravityDisplay = (TextView) findViewById(R.id.textView3);
                gravityDisplay.setGravity(gravity);
                uiMainTextView.setGravity(gravity);
            }
        };

    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return ((sel.isFile() && isFontFile(sel)) || sel
                            .isDirectory()) && !sel.isHidden();

                }
            };

            String[] fList = path.list(filter);
            fileList = new Item[fList.length];
            for (int i = 0; i < fList.length; i++) {
                fileList[i] = new Item(fList[i], R.drawable.file_icon);

                // Convert into file path
                File sel = new File(path, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    fileList[i].icon = R.drawable.directory_icon;
                    Log.d("DIRECTORY", fileList[i].file);
                } else {
                    Log.d("FILE", fileList[i].file);
                }
            }

            if (!firstLvl) {
                Item temp[] = new Item[fileList.length + 1];
                System.arraycopy(fileList, 0, temp, 1, fileList.length);
                temp[0] = new Item("Up", R.drawable.directory_up);
                fileList = temp;
            }
        } else {
            Log.e(TAG, "path does not exist");
        }

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1,
                fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view
                        .findViewById(android.R.id.text1);

                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        fileList[position].icon, 0, 0, 0);

                // add margin between image and text (support various screen
                // densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);

                return view;
            }
        };

    }

    private class Item {
        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        AlertDialog.Builder builder = new Builder(this);

        if (fileList == null) {
            Log.e(TAG, "No files loaded");
            dialog = builder.create();
            WindowManager.LayoutParams wlmp = dialog.getWindow()
                    .getAttributes();
            wlmp.gravity = Gravity.TOP | Gravity.CENTER;
            dialog.getWindow().setAttributes(wlmp);
            dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            return dialog;
        }

        switch (id) {
            case DIALOG_LOAD_FILE:
                builder.setTitle("폰트 파일 선택");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenFile = fileList[which].file;
                        File sel = new File(path + "/" + chosenFile);
                        if (sel.isDirectory()) {
                            firstLvl = false;

                            // Adds chosen directory to list
                            str.add(chosenFile);
                            fileList = null;
                            path = new File(sel + "");

                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                            Log.d(TAG, path.getAbsolutePath());

                        } else if (chosenFile.equalsIgnoreCase("up")
                                && !sel.exists()) {

                            // present directory removed from list
                            String s = str.remove(str.size() - 1);

                            // path modified to exclude present directory
                            path = new File(path.toString().substring(0,
                                    path.toString().lastIndexOf(s)));
                            fileList = null;

                            // if there are no more directories in the list, then
                            // its the first level
                            if (str.isEmpty()) {
                                firstLvl = true;
                            }
                            loadFileList();

                            removeDialog(DIALOG_LOAD_FILE);
                            showDialog(DIALOG_LOAD_FILE);
                            Log.d(TAG, path.getAbsolutePath());

                        } else {
                            // Perform action with file picked
                            try {
                                Typeface tf = Typeface.createFromFile(sel);
                                updateFont(tf);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "폰트를 불러오는 데에 실패했습니다", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }

                    }
                });
                break;
            default:
                break;
        }
        dialog = builder.show();
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        return dialog;
    }

    public boolean isFontFile(File f) {
        if (f.toURI().toString().contains("ttf")) {
            return true;
        } else if (f.toURI().toString().contains("otf")) {
            return true;
        }
        return false;
    }

    public void save(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_save);

        Button saveToSd = (Button) dialog.findViewById(R.id.button1);
        saveToSd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap b = Bitmap.createBitmap(uiMainLayout.getWidth(),
                        uiMainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                uiMainLayout.draw(c);

                String root = Environment.getExternalStorageDirectory()
                        .toString();
                File myDir = new File(root + "/ss-squared-text-3");
                myDir.mkdirs();

                String fname = "Image-" + System.currentTimeMillis() + ".png";
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    b.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                    MediaScanFile.getInstance(getApplicationContext(), root
                            + "/ss-squared-text-3/" + fname);

                    Toast.makeText(getApplicationContext(),
                            "Image saved at /ss-squared-text",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        Button sendToTalk = (Button) dialog.findViewById(R.id.button2);
        sendToTalk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isKakaoTalkExists) {
                    long i = System.currentTimeMillis();
                    Bitmap b = Bitmap.createBitmap(uiMainLayout.getWidth(),
                            uiMainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(b);
                    uiMainLayout.draw(c);

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/ss-squared-text-3");
                    myDir.mkdirs();

                    String fname = "Image-" + i + ".png";
                    File file = new File(myDir, fname);
                    if (file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        b.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        MediaScanFile.getInstance(getApplicationContext(), root
                                + "/ss-squared-text-3/" + fname);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse("file://" + root + "/ss-squared-text-3/"
                                    + fname));

                    intent.setPackage("com.kakao.talk");

                    startActivity(intent);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();

                    final Dialog dialog2 = new Dialog(MainActivity.this);
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog2.setContentView(R.layout.alertdialog_alert);

                    TextView message = (TextView) dialog2
                            .findViewById(R.id.message);
                    message.setText("카카오톡이 없습니다.\n지금 다운로드하러 가시겠습니까?");

                    Button dismiss = (Button) dialog2
                            .findViewById(R.id.button1);
                    dismiss.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });

                    Button web = (Button) dialog2.findViewById(R.id.button2);
                    web.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String url = "https://play.google.com/store/apps/details?id=com.kakao.talk&hl=ko";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            dialog2.dismiss();
                        }
                    });

                    WindowManager.LayoutParams wlmp = dialog.getWindow()
                            .getAttributes();
                    wlmp.gravity = Gravity.TOP | Gravity.CENTER;
                    dialog2.getWindow().setAttributes(wlmp);
                    dialog2.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                    dialog2.show();
                }
            }

        });

        Button sendToStory = (Button) dialog.findViewById(R.id.button3);
        sendToStory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isKakaoStoryExists) {
                    long i = System.currentTimeMillis();
                    Bitmap b = Bitmap.createBitmap(uiMainLayout.getWidth(),
                            uiMainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(b);
                    uiMainLayout.draw(c);

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/ss-squared-text-3");
                    myDir.mkdirs();

                    String fname = "Image-" + i + ".png";
                    File file = new File(myDir, fname);
                    if (file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        b.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        MediaScanFile.getInstance(getApplicationContext(), root
                                + "/ss-squared-text-3/" + fname);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse("file://" + root + "/ss-squared-text-3/"
                                    + fname));

                    intent.setPackage("com.kakao.story");

                    startActivity(intent);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    dialog.dismiss();

                    final Dialog dialog2 = new Dialog(MainActivity.this);
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog2.setContentView(R.layout.alertdialog_alert);

                    TextView message = (TextView) dialog2
                            .findViewById(R.id.message);
                    message.setText("카카오스토리가 없습니다.\n지금 다운로드하러 가시겠습니까?");

                    Button dismiss = (Button) dialog2
                            .findViewById(R.id.button1);
                    dismiss.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });

                    Button web = (Button) dialog2.findViewById(R.id.button2);
                    web.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String url = "https://play.google.com/store/apps/details?id=com.kakao.story&hl=ko";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            dialog2.dismiss();
                        }
                    });

                    WindowManager.LayoutParams wlmp = dialog.getWindow()
                            .getAttributes();
                    wlmp.gravity = Gravity.TOP | Gravity.CENTER;
                    dialog2.getWindow().setAttributes(wlmp);
                    dialog2.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                    dialog2.show();
                }
            }

        });

        Button sendAnywhere = (Button) dialog.findViewById(R.id.button4);
        sendAnywhere.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long i = System.currentTimeMillis();
                Bitmap b = Bitmap.createBitmap(uiMainLayout.getWidth(),
                        uiMainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                uiMainLayout.draw(c);

                String root = Environment.getExternalStorageDirectory()
                        .toString();
                File myDir = new File(root + "/ss-squared-text-3");
                myDir.mkdirs();

                String fname = "Image-" + i + ".png";
                File file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    b.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                    MediaScanFile.getInstance(getApplicationContext(), root
                            + "/ss-squared-text-3/" + fname);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        Uri.parse("file://" + root + "/ss-squared-text-3/"
                                + fname));
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "공유할 앱 선택"));
                dialog.dismiss();
            }

        });

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(wlmp);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.show();
    }

    public boolean isPackageExists(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return false; // TODO
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!isFinish) {
                    isFinish = true;
                    finishToast.show();

                    return true;
                }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finishToast.cancel();
        isFinish = false;

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finishToast.cancel();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static class MediaScanFile extends MediaScannerConnection {
        private final static String TAG = MediaScanFile.class.getSimpleName();
        private static MediaScanFile mSingleton = null;
        private static String mPath = null;

        public static MediaScanFile getInstance(Context context, String path) {
            if (mSingleton == null) {
                mPath = path;
                mSingleton = new MediaScanFile(context, mScanClient);
                mSingleton.connect();
            }
            return mSingleton;
        }

        private MediaScanFile(Context context,
                              MediaScannerConnectionClient client) {
            super(context, client);
        }

        private static MediaScannerConnectionClient mScanClient = new MediaScannerConnectionClient() {

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.i(TAG, "onScanCompleted:" + path + ", " + uri.toString());
                mSingleton.disconnect();
                mSingleton = null;
            }

            @Override
            public void onMediaScannerConnected() {
                mSingleton.scanFile(mPath, null);

            }
        };

    }
}
