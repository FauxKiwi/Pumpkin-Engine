# Pumpkin Engine
This is the Pumpkin Game Engine. A powerful and capable Kotlin 2D game engine using LWJGL and therefore your GPU. If you like the project, please consider to give it a star and support it. Also, your help with contributing is always welcome.<br>
Current version: 1.0
***
## A small glimpse of the engine at the moment
![View of the engine][view3]
***
## Roadmap
### Long term goals
- Fully featured 2D and 3D game engine
- Blazing fast 2D renderer
- Beautiful 3D renderer with PBR
- Level editor
- Entity component system
- Scripting in various languages
- Recorded and skeletal animation
- Multiplatform support
- Vulkan support
- Builds and serialization
### Short term goals
- Getting the 2D renderer optimized and ready
- Start working on the level editor
***
## Get it!
You can use a dependency management system although at the moment it's safer to clone the project.
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
Before you start writing your first game, take a look at the Sandbox.kt test. <br>
Since this is currently only using Kotlin JVM, you can also use Java.
### Creating an application and main() function
To create an application, create a class that extends `com.pumpkin.core.Application`. <br>
In your main() function, simply call

    Application.set(/*Your class name*/())
to start the game.
### Methods of Application
- init(): Called once on initialization
- shutdown(): Called once on finalisation
- run(): Called every frame
- onEvent(event: Event): Called with every event that happens
### Layers
This engine is using layers that are stacked on top of each other. <br>
For your game, create a layer by extending `com.pumpkin.core.layer.Layer`. <br>
A layer contains five event functions:
- onAttach(): Called when the layer is attached
- onDetach(): Called when the layer is detached
- onUpdate(ts: Timestep): Called every frame. ts is the time since the last frame
- onImGuiRender(): Called in the ImGui thread. Use it to display ImGui UIs
- onEvent(event: Event): Called on every event

To attach a layer, simply call `pushLayer(layer: Layer)` in your application.
### Events
Events are one of the core functions of this engine.
You can dispatch events in the onEvent functions of Application and layer. <br>
To dispatch the events, call

    val dispatcher = EventDispatcher(event)
    dispatcher.dispatch</*Event type you want to dispatch*/> {
      //Do something
    }
Of course, you can also use method references for this.
### Full documentation
Please refer to the wiki for a full documentation of all features this engine has to offer
***
Credits: kotlin-graphics for ImGui; TheCherno for inspiration

Remember this work is licensed under Apache 2.0

[view]: https://cdn.discordapp.com/attachments/581185346465824770/781936000196149258/unknown.png

[view2]: https://cdn.discordapp.com/attachments/581185346465824770/783257581169672202/unknown.png

[view3]: https://cdn.discordapp.com/attachments/581185346465824770/784755406945517568/unknown.png
