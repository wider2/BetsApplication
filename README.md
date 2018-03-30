Demo mobile application for betting on football matches.

Based on MVP pattern to split the source code in right way and make it more readable.

All asynchronous requests (to database, to internet connection) has been done with help of RxJava2 and Retrofit 2.30

Using Butterknife8 to simplify work with each ID of the View.

SQLlite Google Room provides their approach to store data locally when user in offline mode or in cache mode.

Dagger2 provides dependency injection approach to minimize initializations and increase speed with help of Singletons.

Persistent state for different UI layout.

Also have been made some instrumental and espresso tests.