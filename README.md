# Meeting Summarizer AI 🎙️✨

A full-stack Java application that transcribes audio from meetings, uses Google's Gemini AI to generate a summary, and stores the results for future viewing.

---

## ## Features

- **Audio Upload:** A clean, modern UI for uploading audio files (MP3, WAV, FLAC, etc.).
- **Scalable AI Transcription:** Uses Google's powerful Cloud Speech-to-Text API. It handles large audio files (up to 8 hours) by first uploading them to **Google Cloud Storage (GCS)**, bypassing the API's 1-minute limit for direct uploads.
- **AI Summarization:** Leverages the Gemini 2.5 flash model to analyze the transcript and extract key decisions and action items.
- **Persistent Storage:** Saves all transcripts and summaries to a PostgreSQL database.
- **Summary History:** A dedicated page to view a list of all past summaries, with a detail view for each one and the ability to delete entries.
- **RESTful API:** A well-structured Spring Boot backend with clear endpoints.

---

## ## Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Frontend:** HTML5, CSS3, JavaScript (with `fetch` for async requests)
- **AI Services:**
  - **Transcription:** Google Cloud Speech-to-Text
  - **Summarization:** Google Gemini API
- **Cloud Storage:** Google Cloud Storage (GCS)
- **Database:** PostgreSQL
- **Build Tool:** Apache Maven

---

## ## Architectural Decisions

A key architectural decision in this project was how to handle audio file transcription for different file sizes.

The initial implementation sent the audio data directly to the Google Speech-to-Text API's synchronous endpoint. While simple, this approach is limited to audio files under 60 seconds. To build a more robust and scalable solution, the architecture was refactored to use an asynchronous pipeline:

1.  **Upload to GCS:** All audio files, regardless of size, are first uploaded to a private **Google Cloud Storage (GCS) bucket**.
2.  **Process via URI:** The application then provides the GCS URI of the file (e.g., `gs://your-bucket-name/file.mp3`) to the Speech-to-Text API's `longRunningRecognize` method.

This asynchronous approach allows the application to process long-running transcription jobs for audio files up to 8 hours long, making it suitable for real-world use cases like transcribing full-length meetings or lectures.

---

## ## Setup and Usage

To run this project locally, you will need:
- Java JDK 17
- Apache Maven
- PostgreSQL installed and running
- A Google Cloud account with a project, a GCS bucket, and a Gemini API Key

### ### Local Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/mrkalra07/meeting-summarizer-java.git](https://github.com/mrkalra07/meeting-summarizer-java.git)
    cd meeting-summarizer-java
    ```

2.  **Set up the Database:**
    - Open `psql` or a tool like pgAdmin.
    - Create a new database:
      ```sql
      CREATE DATABASE meetingsummarizer;
      ```

3.  **Configure your API keys and Database:**
    - Navigate to `src/main/resources/` and open the file `application.properties`.
    - Update the properties with your database password and Gemini API key:
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/meetingsummarizer
      spring.datasource.username=postgres
      spring.datasource.password=your_postgres_password

      gemini.api.key=your_gemini_api_key_here
      ```

4.  **Set up Google Cloud Credentials:**
    - Follow the [Google Cloud documentation](https://cloud.google.com/docs/authentication/external/set-up-adc) to set up Application Default Credentials. This involves creating a service account with the appropriate permissions (Cloud Speech Editor, Storage Object Admin), downloading a JSON key, and setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable.

5.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will be available at `http://localhost:8080`. You can access the UI by opening the `upload.html` file in your browser.

---

## ## API Endpoints

### ### Upload and Summarize Audio
- **URL:** `/api/meetings/summarize`
- **Method:** `POST`
- **Body:** `multipart/form-data`
  - **Key:** `file`
  - **Value:** The audio file to be processed.

### ### Get All Summaries
- **URL:** `/api/meetings`
- **Method:** `GET`
- **Success Response:** A JSON array of all saved `Meeting` objects, sorted by most recent.

### ### Get a Single Summary by ID
- **URL:** `/api/meetings/{id}`
- **Method:** `GET`
- **Success Response:** A single JSON `Meeting` object.

### ### Delete Endpoints
- **Delete Single Summary:** `DELETE /api/meetings/{id}`
- **Delete All Summaries:** `DELETE /api/meetings`
