 <!-- QuietMiles
AI-Assisted Smart Call Management System
 Introduction
Distracted driving is a major cause of road accidents, often due to incoming phone calls. This project presents an AI-assisted smart call management system that minimizes distractions while ensuring important communication is not missed.

The system automatically detects when the user is driving and intelligently manages incoming calls using priority rules and emergency detection.

 Problem Statement
Drivers frequently receive:

Spam calls

Unknown numbers

Non-urgent calls

These interruptions reduce focus and increase accident risk. Existing solutions block all calls or allow too many, lacking intelligent filtering.

 Objectives
Reduce driver distraction

Automatically detect driving conditions

Filter non-important calls

Allow important/emergency communication

Promote safer driving behavior

 Proposed Solution
The app activates Driving Mode using GPS and motion sensors. During this mode:

Non-important calls are rejected

Auto-reply SMS is sent

Important calls are handled safely

Repeated calls are treated as emergencies

 Key Features
1. Automatic driving detection (GPS + Accelerometer)

2. Smart call filtering (important vs non-important)

3. Auto-reply SMS to callers

4. Emergency detection (repeated calls)

5. User-defined priority contacts

6. Notifications & alerts

7. System Architecture
Driving Detection (Module A)
        ↓
Driving Mode Activated
        ↓
Incoming Call Detected
        ↓
Priority Engine (Module B)
        ↓
[Allow / Reject / Silence]
        ↓
Emergency Detection
        ↓
Auto Reply + Notification
 Modules
 Module A: Interface & Context
UI Screens (Home, Contacts, Settings)

Driving detection (GPS + Sensors)

Permission handling

Driving mode state management

 Module B: Logic & Backend
Call interception (CallScreeningService)

Priority decision engine

Emergency repeated-call detection

Database (Room/SQLite)

Auto SMS responder

Tech Stack
Platform: VS code

Language: Java / Kotlin

IDE: VS Code

Database: Room / SQLite

APIs:

TelephonyManager

CallScreeningService

SmsManager

Location Services

 Project Structure
AI-Smart-Call-Management/
│
├── app/
│   ├── activities/
│   ├── services/
│   ├── receivers/
│   ├── sensors/
│   ├── database/
│   ├── ai/
│   └── utils/
│
├── docs/
├── screenshots/
├── presentation/
├── demo/
│
├── README.md
└── .gitignore
 Screenshots
Add your screenshots here

![Home Screen](screenshots/home_screen.png)
![Driving Mode](screenshots/driving_mode.png)
▶ Demo
Add your demo video link here

https://your-demo-video-link
⚙️ Installation
Clone the repository

git clone https://github.com/your-username/AI-Smart-Call-Management.git
Open in Android Studio

Sync Gradle

Run the app on emulator or device

 How It Works
Detects driving using GPS speed

Activates Driving Mode

Intercepts incoming calls

Checks priority of caller

Takes action:

Allow

Reject

Send auto-reply

Detects emergencies via repeated calls

 Future Scope
Machine learning-based call prediction

Voice assistant integration

Smart car integration

Driver behavior analysis

 Team Members

Bhavana K M
Anagha S R

License
This project is licensed under the MIT License.
 -->
