# CorPredict-AI 🫀🤖
Machine Learning powered cardiovascular diagnostic terminal using Java and Python.

## 📌 Project Overview
CorPredict-AI is a secure, local-first diagnostic terminal designed for clinical environments. It processes 13 critical clinical parameters through a locally hosted Machine Learning model to deliver rapid cardiovascular risk assessments, acting as a reliable 'second opinion' tool for medical professionals.

## ⚙️ Architecture (3-Tier)
* **Frontend (Client Tier):** Built with Core Java (Swing) to provide a secure, air-gapped user interface without browser dependency.
* **Middleware (Logic Tier):** A Python Flask REST API handles local HTTP data routing and JSON payload processing.
* **Backend (Data Tier):** A Scikit-Learn predictive model, trained on the Cleveland Heart Disease dataset, processes normalized vectors to output diagnostic probabilities.

## 🚀 Tech Stack
* **UI Engine:** Java, Swing
* **API Server:** Python, Flask
* **AI/ML Core:** Scikit-Learn, Pandas, NumPy
* **Data Logging:** Local CSV Database architecture

## 🛠️ Execution Protocol
1. **Initialize AI Engine:** Run `python app.py` to start the local Flask server.
2. **Compile Terminal:** Run `javac CorPredictUI.java`
3. **Launch Interface:** Run `java CorPredictUI`
