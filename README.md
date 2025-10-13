# Meeting Summarizer AI 🎙️✨

A full-stack Java application that transcribes audio from meetings and uses Google's Gemini AI to generate a summary of key decisions and action items.

**Live Demo:** [Link to your deployed Render app will go here]

---

## ## Features

- **Audio Upload:** A clean, modern UI for uploading audio files (MP3, WAV, etc.).
- **AI Transcription:** Uses Google's powerful Cloud Speech-to-Text API to transcribe audio, with support for large files.
- **AI Summarization:** Leverages the Gemini 1.5 Pro model to analyze the transcript and extract key decisions and action items.
- **RESTful API:** Built with a well-structured Spring Boot backend.

---

## ## Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **AI Services:**
  - **Transcription:** Google Cloud Speech-to-Text
  - **Summarization:** Google Gemini API
- **Database:** PostgreSQL (for deployment) / H2 (for local development)
- **Build Tool:** Apache Maven
- **Deployment:** Render, Git



---

## ## Setup and Usage

To run this project locally, you will need:
- Java JDK 17
- Apache Maven
- A Google Cloud account with a project set up
- A Gemini API Key

### ### Local Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/meeting-summarizer-java.git](https://github.com/your-username/meeting-summarizer-java.git)
    cd meeting-summarizer-java
    ```

2.  **Configure your API keys:**
    - Navigate to `src/main/resources/` and create a file named `application.properties`.
    - Add your Gemini API key:
      ```properties
      gemini.api.key=your_gemini_api_key_here
      ```

3.  **Set up Google Cloud Credentials:**
    - Follow the [Google Cloud documentation](https://cloud.google.com/docs/authentication/external/set-up-adc) to set up Application Default Credentials. This usually involves creating a service account, downloading a JSON key, and setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable.

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will be available at `http://localhost:8080`.

---

## ## API Endpoint

### ### Upload and Summarize Audio

- **URL:** `/api/meetings/summarize`
- **Method:** `POST`
- **Body:** `multipart/form-data`
  - **Key:** `file`
  - **Value:** The audio file to be processed.
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:** A Markdown-formatted string containing the summary.
