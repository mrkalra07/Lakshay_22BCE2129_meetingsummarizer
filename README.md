# Meeting Summarizer AI 🎙️✨

A full-stack Java application that transcribes audio from meetings, uses Google's Gemini AI to generate a summary, and stores the results for future viewing.

**Live Demo:** [Link to your deployed Render app will go here]

---

## ## Features

- **Audio Upload:** A clean, modern UI for uploading audio files (MP3, WAV, etc.).
- **AI Transcription:** Uses Google's powerful Cloud Speech-to-Text API to transcribe audio.
- **AI Summarization:** Leverages the Gemini 1.5 Pro model to analyze the transcript and extract key decisions and action items.
- **Persistent Storage:** Saves all transcripts and summaries to a PostgreSQL database.
- **Summary History:** A dedicated page to view a list of all past summaries, with a detail view for each one.
- **RESTful API:** A well-structured Spring Boot backend with clear endpoints.

---

## ## Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Frontend:** HTML5, CSS3, JavaScript (with `fetch` for async requests)
- **AI Services:**
  - **Transcription:** Google Cloud Speech-to-Text
  - **Summarization:** Google Gemini API
- **Database:** PostgreSQL
- **Build Tool:** Apache Maven

---

## ## Setup and Usage

To run this project locally, you will need:
- Java JDK 17
- Apache Maven
- PostgreSQL installed and running
- A Google Cloud account and a Gemini API Key

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
    - Follow the [Google Cloud documentation](https://cloud.google.com/docs/authentication/external/set-up-adc) to set up Application Default Credentials. This involves creating a service account, downloading a JSON key, and setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable.

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