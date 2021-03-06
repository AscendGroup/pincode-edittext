package com.oakkub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.oakkub.android.PinCodeEditText;

public class MainActivity extends AppCompatActivity {

	private PinCodeEditText mSimplePinCodeEditText;
	private Button mToggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mSimplePinCodeEditText = (PinCodeEditText) findViewById(R.id.simplePinCodeEditText);
		mToggleButton = (Button) findViewById(R.id.toggleButton);

		setCheckboxText();
		mToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSimplePinCodeEditText.setShowAsPassword(!mSimplePinCodeEditText.isShowAsPassword());
				setCheckboxText();
			}
		});
	}

	private void setCheckboxText() {
		mToggleButton.setText(mSimplePinCodeEditText.isShowAsPassword() ? "Show password" : "Hide password");
	}
}
