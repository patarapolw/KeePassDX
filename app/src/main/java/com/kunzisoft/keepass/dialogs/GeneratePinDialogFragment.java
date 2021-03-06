/*
 * Copyright 2017 Brian Pellin, Jeremy Jamet / Kunzisoft,
 * Pacharapol Withayasakpunt
 *
 * This file is part of KeePass DX.
 *
 *  KeePass DX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeePass DX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePass DX.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.kunzisoft.keepass.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kunzisoft.keepass.R;
import com.kunzisoft.keepass.settings.PreferencesUtil;
import com.kunzisoft.keepass.utils.Util;
import com.patarapolw.password_generator.PasswordGenerator;
import com.patarapolw.wordify.MajorSystemPeg;

public class GeneratePinDialogFragment extends DialogFragment {
    public static final String KEY_PASSWORD_ID = "KEY_PASSWORD_ID";

    private GeneratePinListener mListener;
    private EditText lengthView;

    private EditText passwordView;
    private EditText mnemonicView;

    private PasswordGenerator passwordGenerator = new PasswordGenerator();
    private MajorSystemPeg majorSystemPeg;
    private String[] keywords = new String[]{""};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (GeneratePinListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + GeneratePinListener.class.getName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.generate_pin, null);

        majorSystemPeg = new MajorSystemPeg(getContext());

        passwordView = root.findViewById(R.id.password);
        Util.applyFontVisibilityTo(getContext(), passwordView);

        mnemonicView = root.findViewById(R.id.mnemonic);
        Util.applyFontVisibilityTo(getContext(), mnemonicView);

        lengthView = root.findViewById(R.id.length);

//        assignDefaultCharacters();

        Button genPassButton = root.findViewById(R.id.generate_password_button);
        genPassButton.setOnClickListener(v -> fillPassword());

        builder.setView(root)
                .setPositiveButton(R.string.accept, (dialog, id) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_PASSWORD_ID, passwordView.getText().toString());
                    mListener.acceptPassword(bundle);

                    dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Bundle bundle = new Bundle();
                    mListener.cancelPassword(bundle);

                    dismiss();
                });

        // Pre-populate a password to possibly save the user a few clicks
        fillPassword();

        return builder.create();
    }

    private void fillPassword() {
        String pin = passwordGenerator.generatePin(Integer.parseInt(lengthView.getText().toString()));
        keywords = majorSystemPeg.toWords(pin);

        passwordView.setText(pin);
        mnemonicView.setText(TextUtils.join(" ", keywords));
    }

    public interface GeneratePinListener {
        void acceptPassword(Bundle bundle);
        void cancelPassword(Bundle bundle);
    }
}
