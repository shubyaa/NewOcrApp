
# Android OCR App


<img src="https://github.com/shubyaa/NewOcrApp/blob/master/app/src/main/res/drawable/logo.png" alt="Logo" width="100"/>

## Badges

![Android](https://img.shields.io/badge/JDK-14.01-blue.svg)
![Java](https://img.shields.io/badge/Java-14+-red.svg)
![SQLite](https://img.shields.io/badge/SQLite-1-purple.svg)
![SDK](https://img.shields.io/badge/SDK-21-green.svg)

## Features

- Responsive UI using Scalable unit [Sdp](https://github.com/intuit/sdp).
- Save/ Update your file.
- Recognizes the text from the image using [Google ML kit](https://developers.google.com/ml-kit).
- Made using Java & SQLite Database.

## Roadmap

1. Home page
2. Permissions
3. Selection of image
4. Text Extraction
5. File Saving

## Documentation

In this project, the UI is made by me from scratch, you can drop a like [here](https://dribbble.com/shots/17266411-Basic-OCR-App-Layout-Blue).

To start with, it's important to configure with Google [Firebase](https://firebase.google.com/) & [ML kit](https://developers.google.com/ml-kit) in our project.

Then some of the dependencies are needed to sync with our **Gradle Build**

``` gradle
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // Lottie Animation
    implementation 'com.airbnb.android:lottie:4.2.2'

    //Glide
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

    // Scalable Unit
    implementation 'com.intuit.sdp:sdp-android:1.0.6'
```

Now, we are done with our dependencies. Now, we can finally hop towards the code.

## The Logic

The App starts with it's own splash screen, hence I ave used [LottieFiles](https://lottiefiles.com/) to get awesome animation to my screens.

For the **List** to display in [HomeActivity](https://github.com/shubyaa/NewOcrApp/blob/master/app/src/main/java/com/example/newocrapp/HomeActivity.java), I have implemented [recyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview). It gives my view to show all the files dynamic and user-friendly.

```xml
  <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/item_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/horizontal_opt"
        android:layout_marginHorizontal="@dimen/_15sdp"
        android:layout_marginVertical="@dimen/_8sdp"
        android:orientation="vertical" />

```

For recyclerview to work, we need adapters to adapt a particular card and put it inside our recyclerView. The Adapter class is [here](https://github.com/shubyaa/NewOcrApp/blob/master/app/src/main/java/com/example/newocrapp/Adapters/ListAdapter.java).

``` java
//The magic is in this line below
adapter.notifyDataSetChanged();

// It updates the recyclerView to display a new list in runtime.
```

## Recognizing Text

This is the heart of this project which recognizes text from the image. I have used *onDeviceImageRecognition* to recognize text from images.

```java
FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String result = firebaseVisionText.getText();
                        extracted_text.setText(result);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String result = "";
                        extracted_text.setText(result);
                    }
                });
    }

```

## Screenshots

<img src="https://github.com/shubyaa/NewOcrApp/blob/master/output/1.jpg" alt="Screenshot 1" width="200"/> <img src="https://github.com/shubyaa/NewOcrApp/blob/master/output/2.jpg" alt="Screenshot 2" width="200"/> <img src="https://github.com/shubyaa/NewOcrApp/blob/master/output/3.jpg" alt="Screenshot 3" width="200"/> <img src="https://github.com/shubyaa/NewOcrApp/blob/master/output/4.jpg" alt="Screenshot 4" width="200"/>

## Apk File

You can get the .apk file directly from [here](https://github.com/shubyaa/NewOcrApp/blob/master/output/app-debug.apk).

## Tech Stack

**Languages** :- Java, XML, SQLite, API

## 🔗 Links

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/shubham-pednekar-573369213/)

## Feedback

If you have any feedback, please reach out to me at [mail](shubya8451@gmail.com).
