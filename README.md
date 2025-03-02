🏫 Campus Management System
🔹 Overview
This is a QR Code-based Login System integrated with multiple campus management modules, including Elections, Notifications, Reports, Bookings, Complaints, and Budget Management.

The system supports different roles:

🏛 Dean
👨‍🏫 Faculty
🎓 Student
🏥 Doctor
🛡 Security
Each user logs in using a QR code containing a JSON file with their details. Email verification is also implemented but disabled by default.

🔐 QR Code-Based Login System
Each user has a unique QR code embedded with a JSON file containing login details:
{
  "role": "Security",
  "name": "Amol Annamwar",
  "reg_no": "2022SIT053",
  "self_phone": "1234567890",
  "self_mail": "security1@sggs.ac.in",
  "parent_mail": "chaitanyamoti2005@gmail.com"
}

✅ Features:
✔ QR-based authentication 📷
✔ Role-based access control 🔑
✔ Email verification (disabled but available) 📧

📌 Features & Modules

🏠 1️⃣ Home Page
Displays Live Elections 🗳 as clickable cards.
Clicking an election navigates to the Election Fragment to view results or vote (if eligible).
Notifications 📢 (created by the Dean) are visible to Faculty & Students.

🗳 2️⃣ Election System
Elections are created by the Dean.
Faculty & Students can view elections, but only students can vote.
Live result updates 📊.

🚨 3️⃣ Code of Conduct Violation Reporting
Faculty can scan a student’s QR code and report violations.
Reported students are visible to all campus members for awareness.

📅 4️⃣ Booking System
Students & Faculty can book resources (e.g., rooms, halls, equipment).
Requests go to the Dean, who can approve/reject them.
Dean’s UI: Shows booking requests with "Accept" ✅ and "Reject" ❌ buttons.
Students & Faculty UI: Only shows the status of their requests.

📩 5️⃣ Complaint System
Users can anonymously submit complaints. 🔒
Only authorized personnel can review and take action. 🏛

💰 6️⃣ Budget Management
Dean can post budget details.
Faculty & Students can view budget updates in a list 📃.

🚨 Application System (Feature Available but Not in UI)
⚠ Note: The Application System (Leave Requests, General Applications) is fully implemented but not included in the UI due to bottom navigation limitations.
To use it:
Simply add a button or option in an existing fragment to load the Application System files.
The backend and UI components are ready to integrate.

⚙ Installation & Setup
📌 Prerequisites
Ensure you have the following installed:
✔ Android Studio 💻
✔ Firebase Authentication & Firestore 🔥
✔ QR Code Scanner Library 📷

🛠 Clone the Repository
git clone https://github.com/your-username/campus-management-system.git
cd campus-management-system
▶ Run the App
Open in Android Studio.
Connect to Firebase and set up Firestore.
Run the project on an Android Emulator 📱 or Physical Device.

👨‍💻 Contributors
👤 Shubham Rajurpalle
📧 rajurpalleshubham1802@gmail.com
👤 Shivam Kachawar
📧 shivamkachawar@gmail.com
👤 Venkatesh Bogewar
📧 venkateshbogewar@gmail.com
👤 Chaitanya Moti
📧 chaitanyamoti.cm@gmail.com

