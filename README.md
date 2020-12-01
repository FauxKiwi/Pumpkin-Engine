# Pumpkin-Engine
The new JVM only implementation
***
## A small glimpse of the engine at the moment
![View of the engine][view2]
***
## Get it!
### Gradle (Groovy DSL)

    repositories {
      //...
      maven(url = 'https://www.jitpack.io')
    }
    
    dependencies {
      //...
      implementation 'fauxkiwi:pumpkin-engine:-SNAPSHOT'
    }
For other dependency management systems, please refer to the wiki.
***
## Use it!
Before you start writing your first game, take a look at the Sandbox.kt test.
Since this is in Kotlin, you can also use Java.
### Creating an application and main() function
To create an application, create a class that extends {com.pumpkin.core.Application}.
In your main() function, simply call

    /*com.pumpkin.core.*/Application.set(/*Your class name*/())
to start the game.
### Methods of Application
- init(): Called once on initialization
- shutdown(): Called once on finalisation
- run(): Called every frame
- onEvent(event: Event): Called with every event that happens
***
Remember this work is licensed under Apache 2.0

[view]: https://cdn.discordapp.com/attachments/581185346465824770/781936000196149258/unknown.png

[view2]: https://cdn.discordapp.com/attachments/581185346465824770/783257581169672202/unknown.png
