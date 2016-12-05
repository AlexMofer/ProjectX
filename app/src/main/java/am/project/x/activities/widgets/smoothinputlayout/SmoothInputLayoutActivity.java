package am.project.x.activities.widgets.smoothinputlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.smoothinputlayout.SmoothInputLayout;

public class SmoothInputLayoutActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener, TextWatcher {

    private SmoothInputLayout lytContent;
    private View btnVoice;
    private EditText edtInput;
    private View btnEmoji;
    private View btnSendVoice;
    private View btnMore;
    private View btnSend;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_smoothinputlayout;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.smoothinputlayout_toolbar);
        lytContent = (SmoothInputLayout) findViewById(R.id.sil_lyt_content);
        btnVoice = findViewById(R.id.sil_ibtn_voice);
        edtInput = (EditText) findViewById(R.id.sil_edt_input);
        btnEmoji = findViewById(R.id.sil_ibtn_emoji);
        btnSendVoice = findViewById(R.id.sil_btn_send_voice);
        btnMore = findViewById(R.id.sil_ibtn_more);
        btnSend = findViewById(R.id.sil_ibtn_send);
        btnVoice.setOnClickListener(this);
        edtInput.addTextChangedListener(this);
        btnEmoji.setOnClickListener(this);
        btnSendVoice.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        findViewById(R.id.sil_v_list).setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sil_ibtn_voice:
                if (btnVoice.isSelected()) {
                    btnVoice.setSelected(false);
                    showInputWidget();
                    showInput();
                } else {
                    btnVoice.setSelected(true);
                    lytContent.closeInput(true);
                    showVoiceWidget();
                }
                break;
            case R.id.sil_btn_send_voice:
                Toast.makeText(getApplicationContext(), R.string.smoothinputlayout_voice,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.sil_ibtn_emoji:
                if (btnEmoji.isSelected()) {
                    btnEmoji.setSelected(false);
                    showInput();
                } else {
                    btnEmoji.setSelected(true);
                    showEmoji();
                }
                break;
            case R.id.sil_ibtn_more:
                showMore();
                break;
            case R.id.sil_ibtn_send:
                sendMessage();
                break;
        }
    }

    /**
     * 显示输入控件
     */
    private void showInputWidget() {
        edtInput.setVisibility(View.VISIBLE);
        btnEmoji.setVisibility(View.VISIBLE);
        btnSendVoice.setVisibility(View.GONE);
        afterTextChanged(edtInput.getText());
    }

    /**
     * 显示语音控件
     */
    private void showVoiceWidget() {
        edtInput.setVisibility(View.GONE);
        btnEmoji.setVisibility(View.GONE);
        btnSendVoice.setVisibility(View.VISIBLE);
        btnMore.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.GONE);
    }

    /**
     * 显示输入面板
     */
    private void showInput() {
        lytContent.showInput();
    }

    /**
     *  显示Emoji面板
     */
    private void showEmoji() {
        // TODO
    }

    /**
     * 显示更多面板
     */
    private void showMore() {
        // TODO
    }

    private void sendMessage() {
        Toast.makeText(getApplicationContext(), edtInput.getText().toString().trim(),
                Toast.LENGTH_SHORT).show();
        edtInput.setText(null);
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().trim().length() > 0) {
            btnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        } else {
            btnMore.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        lytContent.closeInput(true);
        return false;
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SmoothInputLayoutActivity.class));
    }
}
