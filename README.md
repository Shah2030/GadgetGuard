# 📱 GadgetGuard - Gadget Addiction Tracker App

<p align="center">
  <img src="Screenshots/splash.png" alt="Splash" width="300"/>
</p>

**GadgetGuard** is a secure and intelligent Android app designed to help users track gadget usage and predict gadget addiction using a local TensorFlow Lite model. It includes a chatbot to suggest healthy digital habits, biometric security, data visualization, and PDF export features.

---

## ✨ Features

- 🔐 **Biometric Lock** for secure access
- 📝 **Register/Login** using Firebase Authentication
- 📊 **Track Gadget Usage** (Gadget name, Hours used)
- 🔍 **Addiction Prediction** using TensorFlow Lite
- 📈 **Pie Chart Visualization** of usage
- 🧠 **Chatbot** to help reduce screen time
- 📄 **PDF Export** of usage data with elegant design
- ☁️ **Firestore** storage for usage history
- 🧾 Sticky date headers for enhanced report viewing

---

## 📸 Screenshots

| Splash Screen | Login | Register |
|---------------|--------|----------|
| ![Splash](Screenshots/splash.png) | ![Login](Screenshots/login.png) | ![Register](Screenshots/register.png) |

| Dashboard | Usage Entry | Prediction |
|-----------|-------------|------------|
| ![Dashboard](Screenshots/dashboard.png) | ![GU](Screenshots/gu.png) | ![Prediction](Screenshots/pred.png) |

| PDF Export | Report | Chatbot |
|------------|--------|---------|
| ![PDF](Screenshots/exppdf.png) | ![Report](Screenshots/report.png) | ![Chatbot](Screenshots/chatbot.png) |

---

## ⚙️ Setup Instructions

### 🧩 Prerequisites

- Android Studio (Electric Eel or later recommended)
- Java 17+
- Firebase Project with:
  - Authentication (Email/Password)
  - Firestore Database
- TensorFlow Lite `.tflite` model file (place it under `app/src/main/assets/`)
- Internet connection

### 🔌 Dependencies Used

- `Firebase Authentication & Firestore`
- `TensorFlow Lite`
- `MPAndroidChart` for pie charts
- `iText7` for PDF generation
- `BiometricPrompt` for fingerprint auth

---
### 📦 Prerequisites
- Android Studio (Electric Eel or later recommended)
- Android SDK 33+
- Firebase account

### 🔧 Open in Android Studio
- File → Open → Select the GadgetGuard folder or Clone the repository:
   ```bash
   git clone https://github.com/your-username/GadgetGuard.git
   cd GadgetGuard


### 🔥 Configure Firebase
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a Firebase project
3. Enable **Authentication** and **Firestore**
4. Download `google-services.json`
5. Place it inside your project at: GadgetGuard/app/google-services.json
