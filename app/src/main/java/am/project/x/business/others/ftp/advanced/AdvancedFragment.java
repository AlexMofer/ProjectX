/*
 * Copyright (C) 2019 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.project.x.business.others.ftp.advanced;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.am.appcompat.app.Fragment;
import com.am.tool.support.utils.InputMethodUtils;

import java.util.Locale;

import am.project.x.R;
import am.project.x.business.others.ftp.FtpFragmentCallback;
import am.project.x.utils.ContextUtils;

/**
 * 高级实现方案
 * Created by Alex on 2019/10/10.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class AdvancedFragment extends Fragment implements TextWatcher,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final int ACTIVITY_REQUEST_URI = 100;
    private static final int FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
    private EditText mVPort;
    private CheckBox mVAuto;
    private Button mVUri;
    private AdvancedFtpConfig mConfig;

    public static AdvancedFragment newInstance() {
        return new AdvancedFragment();
    }

    public AdvancedFragment() {
        super(R.layout.fragment_ftp_advanced);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mVPort = findViewById(R.id.advanced_edt_port);
        mVAuto = findViewById(R.id.advanced_cb_auto);
        mVUri = findViewById(R.id.advanced_btn_uri);

        mConfig = new AdvancedFtpConfig(requireContext());
        mVPort.setText(String.format(Locale.getDefault(), "%d", mConfig.getPort()));
        mVAuto.setChecked(mConfig.isAutoChangePort());
        mVUri.setText(mConfig.getUri());

        mVPort.addTextChangedListener(this);
        mVAuto.setOnCheckedChangeListener(this);
        mVUri.addTextChangedListener(this);
        findViewById(R.id.advanced_btn_open).setOnClickListener(this);
        findViewById(R.id.advanced_btn_close).setOnClickListener(this);
        mVUri.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ftp_advanced, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ftp_legacy) {
            final Activity activity = getActivity();
            if (activity instanceof FtpFragmentCallback)
                ((FtpFragmentCallback) activity).onSwitch(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_URI &&
                resultCode == Activity.RESULT_OK && data != null) {
            final Uri uri = data.getData();
            if (uri != null) {
                requireActivity().getContentResolver().takePersistableUriPermission(uri, FLAGS);
                mVUri.setText(uri.toString());
            }
        }
    }

    // Listener
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == mVPort.getText()) {
            final int port = getPort();
            if (port <= 0 || port > 65535)
                return;
            mConfig.setPort(port);
        } else if (s == mVUri.getText()) {
            final String uri = mVUri.getText().toString().trim();
            if (uri.length() > 0)
                mConfig.setUri(uri);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mVAuto == buttonView)
            mConfig.setAutoChangePort(isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advanced_btn_open:
                open();
                break;
            case R.id.advanced_btn_close:
                AdvancedFtpService.stop(requireContext());
                break;
            case R.id.advanced_btn_uri:
                final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                        .addFlags(FLAGS | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, ACTIVITY_REQUEST_URI);
                break;
        }
    }

    private int getPort() {
        try {
            return Integer.parseInt(mVPort.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void open() {
        if (AdvancedFtpService.isStarted()) {
            Toast.makeText(requireContext(), R.string.ftp_toast_running, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ContextUtils.isWifiConnected(requireContext())) {
            Toast.makeText(requireContext(), R.string.ftp_toast_no_wifi, Toast.LENGTH_SHORT).show();
            return;
        }
        final int port = getPort();
        if (port <= 0 || port > 65535) {
            mVPort.setText(null);
            InputMethodUtils.openInputMethod(mVPort);
            Toast.makeText(requireContext(), R.string.ftp_toast_bad_port,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final String uri = mVUri.getText().toString().trim();
        if (TextUtils.isEmpty(uri)) {
            Toast.makeText(requireContext(), R.string.ftp_toast_bad_path,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final int state = requireContext().checkUriPermission(Uri.parse(uri),
                Binder.getCallingPid(), Binder.getCallingUid(), FLAGS);
        if (state != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), R.string.ftp_toast_permission,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AdvancedFtpService.start(requireContext());
    }
}
