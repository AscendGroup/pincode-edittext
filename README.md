# PinCodeEditText
An EditText with pin code style, It is easy to use and customizable.

Requirement
--------
Android API 15 or higher

Download
--------
Download the latest version in Gradle:
```groovy
compile 'com.oakkub:pincode-edittext:1.0.1'
```
Or Maven:
```xml
<dependency>
  <groupId>com.oakkub</groupId>
  <artifactId>pincode-edittext</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

Usage
--------
Include `PinCodeEditText` in your layout XML
```xml
<com.oakkub.android.PinCodeEditText
			android:id="@+id/simplePinCodeEditText"
			android:layout_width="match_parent"
			android:layout_height="wrap_content"
			android:maxLength="4"
			android:textColor="@android:color/white"
			app:pinBackgroundColor="@color/colorPrimary"
			app:pinBorderColor="@color/colorPrimary"
			app:pinMaskCharacter="#"
			app:pinNextPinBackgroundColor="@color/colorAccent"
			app:pinNextPinBorderColor="@color/colorAccent"
			app:pinShowAsPassword="true"
			app:pinShowHighlightNextPin="true"/>
```


License
--------

    Copyright 2017

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


