# WebView PDF Viewer KMP

A powerful Kotlin Multiplatform PDF Viewer library for Android and iOS using native platform WebView implementations.

Built with:

- Kotlin Multiplatform (KMP)
- Compose Multiplatform
- Android WebView
- iOS WKWebView

Supports direct PDF URL loading with native behavior on both Android and iOS.

---

# Maven Central

```kotlin
implementation("io.github.karun02525:webview-pdf-viewer:1.0.3")
```

---

# Features

✅ Direct PDF URL loading  
✅ Native Android WebView support  
✅ Native iOS WKWebView support  
✅ Compose Multiplatform support  
✅ Native back navigation support  
✅ Dynamic title support  
✅ Zoom support with gestures  
✅ Pinch-to-zoom support  
✅ Page indicator support  
✅ Smooth scrolling behavior  
✅ Native PDF rendering  
✅ Loading state callback  
✅ Lightweight implementation  
✅ Shared KMP module support  
✅ LRU memory management  
✅ Optimized bitmap memory handling  
✅ Android & iOS native behavior  
✅ Simple API integration  
✅ Production-ready architecture  
✅ Coroutine support  
✅ Fast PDF loading  
✅ Native lifecycle handling  
✅ Gesture support  
✅ Modern Compose UI support  

---

# Native Platform Behavior

## Android

Uses native Android `WebView`

### Android Features

- Native WebView rendering
- PDF URL loading
- JavaScript support
- Native lifecycle handling
- Back press support
- Gesture zoom support
- Optimized rendering
- LRU bitmap cache handling

---

## iOS

Uses native iOS `WKWebView`

### iOS Features

- Native WKWebView rendering
- Native PDF preview support
- Smooth scrolling
- Gesture zoom support
- Native navigation delegates
- Native memory handling
- Optimized PDF rendering

---

# Installation

## Step 1 — Add Maven Central

```kotlin
repositories {
    mavenCentral()
}
```

---

## Step 2 — Add Dependency

```kotlin
implementation("io.github.karun02525:webview-pdf-viewer:1.0.3")
```

---

# Usage

```kotlin
OpenWebView(
    modifier = Modifier.fillMaxSize(),
    url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
)
```

---

# Example PDF URL

```text
https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf
```

---

# Platform Support

| Platform | Support |
|----------|----------|
| Android  | ✅ |
| iOS      | ✅ |

---

# Memory Optimization

This library includes:

- LRU memory cache management
- Optimized bitmap rendering
- Native memory handling
- Reduced memory usage
- Smooth scrolling optimization

---

# Screenshots

## Android Preview

<p align="center">
  <img src="https://github.com/user-attachments/assets/3455e38a-5934-4dda-9ffa-cab8e9aaba30" width="20%" max-height="350px" alt="Android Preview 1" />   
  <img src="https://github.com/user-attachments/assets/35ab47c1-cd10-425c-be4b-977a61b51b50" width="20%" max-height="350px" alt="Android Preview 2" />
</p>

---

## iOS Preview

<p align="center">
  <img src="https://github.com/user-attachments/assets/0cf3a1f9-9909-4da9-bac9-f39a48cc782a" width="20%" max-height="350px" alt="iOS Preview 1" />   
  <img src="https://github.com/user-attachments/assets/03eb7c49-c952-47b4-be69-23b6f0c4f0f2" width="20%" max-height="350px" alt="iOS Preview 2" />   
  <img src="https://github.com/user-attachments/assets/3f1608db-d8fa-4f4b-a747-68707010cd49" width="20%" max-height="350px" alt="iOS Preview 3" />
</p>

---

# Video Preview
<p align="center">
  <h3>App Interaction Demo Walkthrough</h3>
  <video src="https://github.com/user-attachments/assets/8362c8a0-f843-4afe-9e8e-54ff1ec5f3b5" width="70%" controls>
    Your browser does not support the video tag.
  </video>
</p>

<p align="center">
  <h3>Gesture Control Validation Recording</h3>
  <video src="https://github.com/user-attachments/assets/e91f6d4c-2f32-4f3c-a52d-a31a37dd2029" width="70%" controls>
    Your browser does not support the video tag.
  </video>
</p>

---

### Local Asset Path
* 💾 Local Path Reference: `preview/demo.mp4`

# Recommended GitHub Folder Structure

```text
screenshots/
 ├── android-preview.png
 └── ios-preview.png

# Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Android WebView
- iOS WKWebView
- Kotlin Coroutines

---

# Project Structure

```text
shared/
 ├── commonMain
 ├── androidMain
 └── iosMain
```

---

# Upcoming Features

🚀 PDF download support  
🚀 Thumbnail preview  
🚀 Search inside PDF  
🚀 Dark mode improvements  


---

# Why Use This Library?

- Native platform behavior
- Simple integration
- KMP-ready
- Compose-first API
- Lightweight implementation
- Smooth performance
- Optimized memory management
- Android + iOS support
- Production-ready setup

---

# Author

Karun Kumar

---

# Contact

📧 Email  
karunkumar02525@gmail.com

📱 Phone  
8920828585

🌐 GitHub  
https://github.com/karun02525

---

# Repository

https://github.com/karun02525/webview-pdf-viewer

---

# License

Apache License 2.0

Licensed under the Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0

---

# Contribution

Pull requests and contributions are welcome.

If you find issues or want improvements, feel free to create an issue or PR.

---

# Support

If you like this project, please ⭐ the repository on GitHub.
