# Music Advisor Project

The Music Advisor project is a Java application that allows users to explore music albums, playlists, and categories using the Spotify API. It provides features for retrieving new releases, featured playlists, categories, and more.

## Table of Contents

* [Project Structure](#project-structure)
* [Getting Started](#getting-started)
* [Usage](#usage)
* [Configuration](#configuration)
* [Contributing](#contributing)

## Project Structure

The project is organized into several packages and classes:

### advisor.entities

* `AbstractEntity`: An abstract class representing entities with a name attribute.
* `Album`: Represents music albums, including artists and URLs.
* `Category`: Represents music categories with unique IDs.
* `Playlist`: Represents music playlists with URLs.

### advisor

* `App`: The main class that handles user interaction and commands.
* `Authorization`: Manages Spotify API authorization and access tokens.
* `Config`: Contains configuration constants for the Spotify API and the application.
* `HttpCustomHandler`: Handles HTTP requests to the Spotify API.
* `JsonUtils`: Provides utility methods for parsing JSON responses from the Spotify API.

### advisor.commands

* `Command`: An abstract class representing commands that can be executed.
* `ExitCommand`: Exits the application.
* `NewCommand`: Retrieves new music releases.
* `FeaturedCommand`: Retrieves featured playlists.
* `CategoriesCommand`: Retrieves music categories.
* `PlaylistsCommand`: Retrieves playlists within a specific category.
* `AuthCommand`: Initiates the authorization process.

## Getting Started

To get started with the Music Advisor project, follow these steps:

1. Clone the repository to your local machine:

    ```shell
    git clone https://github.com/yourusername/music-advisor.git
    ```

2. Install the required dependencies. The project uses Java and does not require additional dependencies.

3. Configure the application by setting your Spotify API credentials in the Config class:

    ```java
   public class Config {
    public static String AUTH_SERVER_PATH = "https://accounts.spotify.com";
    public static String API_SERVER_PATH = "https://api.spotify.com";
    public static int RECORDS_FOR_PAGE = 5;
    // Add your Spotify API credentials here
    public static String CLIENT_ID = "your-client-id";
    public static String CLIENT_SECRET = "your-client-secret";
    }
   ```

4. Build and run the application:

    ```shell
    cd music-advisor
    javac advisor/Main.java
    java advisor.Main
    ```

## Usage

The Music Advisor application supports various commands for exploring music. Here are some example commands:

* `new`: Retrieves new music releases.
* `featured`: Retrieves featured playlists.
* `categories`: Retrieves music categories.
* `playlists <category name>`: Retrieves playlists within a specific category.
* `auth`: Initiates the authorization process.

You can enter these commands in the application's console to explore music and playlists.

## Configuration

You can configure the application by modifying the constants in the `Config` class. The configuration includes the Spotify API server paths, the number of records displayed per page, and your client ID and client secret for authentication.

## Contributing

Contributions to the Music Advisor project are welcome. If you have any enhancements, bug fixes, or feature requests, please open an issue or submit a pull request on the project's GitHub repository.

---
Enjoy exploring music with the Music Advisor application! If you have any questions or need further assistance, feel free to reach out.

(Note: Replace `yourusername` and update client ID and client secret in the configuration with your actual Spotify API credentials.)