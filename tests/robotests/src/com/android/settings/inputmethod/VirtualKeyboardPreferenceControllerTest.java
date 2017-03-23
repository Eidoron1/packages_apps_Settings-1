/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.inputmethod;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.preference.Preference;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.settings.R;
import com.android.settings.SettingsRobolectricTestRunner;
import com.android.settings.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION)
public class VirtualKeyboardPreferenceControllerTest {

    @Mock
    private Context mContext;
    @Mock
    private InputMethodManager mImm;
    @Mock
    private DevicePolicyManager mDpm;
    @Mock
    private PackageManager mPm;
    @Mock
    private Preference mPreference;

    private VirtualKeyboardPreferenceController mController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mContext.getSystemService(Context.DEVICE_POLICY_SERVICE)).thenReturn(mDpm);
        when(mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mImm);
        when(mContext.getPackageManager()).thenReturn(mPm);
        mController = new VirtualKeyboardPreferenceController(mContext);
    }

    @Test
    public void shouldAlwaysBeAvailable() {
        assertThat(mController.isAvailable()).isTrue();
    }

    @Test
    public void updateState_noEnabledIMEs_setEmptySummary() {
        mController.updateState(mPreference);

        verify(mPreference).setSummary(R.string.summary_empty);
    }

    @Test
    public void updateState_singleIme_setImeLabelToSummary() {
        when(mDpm.getPermittedInputMethodsForCurrentUser()).thenReturn(null);
        final ComponentName componentName = new ComponentName("pkg", "cls");
        final List<InputMethodInfo> imis = new ArrayList<>();
        imis.add(mock(InputMethodInfo.class));
        when(imis.get(0).getPackageName()).thenReturn(componentName.getPackageName());
        when(mImm.getEnabledInputMethodList()).thenReturn(imis);
        when(imis.get(0).loadLabel(mPm)).thenReturn("label");

        mController.updateState(mPreference);

        verify(mPreference).setSummary("label");
    }
}
