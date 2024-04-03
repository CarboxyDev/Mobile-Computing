### Assignment 2

Important note:
- The application prefers to fetch the data from the internet over using it from the cache if it is present
- The application will fetch it from cache automatically if no internet is present

Implemented features:
- Fetches weather data from an API (open-meteo api)
- Caches the fetched weather date
- For future dates, estimates the temperature based on the previous data (10 years)
- For dates where the temperature is not available, estimates the temperature based on the previous data (10 years)
- Checks internet connectivity while fetching the data
- Fetches weather data from cache if internet is not available and the date's data is cached
- Shows error message if internet is not available and the date's data is not cached
- Shows error message for unexpected network or API errors

Implementation details:
- The application uses a ViewModel to manage data fetching and state updates along with caching
- The application uses LiveData to observe the data changes
- The application uses Retrofit to fetch data from the API
- The application uses Room to cache the fetched data
- The application uses coroutines to handle async operations


Running the application:
- No api key needed, just start the application.
- Enter the date via the date picker or go with default date
- Click on the "Get temperatures" button
- The application will show the min and max temperatures for the selected date

Assumptions:
- The location is hardcoded (longitude and latitude) 
- The temperature is in celsius
- The date is in the format "yyyy-MM-dd"
- The application has been tested on a physical device running Android 12