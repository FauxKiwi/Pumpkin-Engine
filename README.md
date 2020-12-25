# Pumpkin Engine
This is the Pumpkin Game Engine. A powerful and capable Kotlin 2D game engine using LWJGL and therefore your GPU. If you like the project, please consider to give it a star and support it. Also, your help with contributing is always welcome.<br>
Current version: 1.1
***
## Screenshots
<!--### A demo of texture rendering
![Texture Rendering][textures]
### Conways Game Of Life implementation
![Game of Life][game_of_life]
### Renderer stress test: Drawing over 100,000 quads at over 90 FPS
![Stress test][stresstest]-->
### The editor with a new scene
![Empty scene][empty]
### Scene serialization: Saving and loading scenes
![File Dialog][file_dialog]
### 2D and 3D scenes with an ECS and an editor camera
![Pink Cube][pink_cube]
### Transform gizmos
![Gizmos][gizmos]
### Themes for editor
![Light Theme][light_theme]
***
## Roadmap
### Long term goals
- Fully featured 2D and 3D game engine
- Blazing fast 2D renderer
- Beautiful 3D renderer with PBR
- Audio engine
- Level editor
- Scripting in various languages
- Recorded and skeletal animation
- Multiplatform support
- Multiplayer and networking
- Vulkan support
- Builds and serialization
### Short term goals
- Multiplatform library
- Building
- Textures
- Asset library
- Scripting
- 3D
***
## Get it!
You can use a dependency management system although at the moment it's safer to clone the project.
<!--### Gradle (Groovy DSL)

    repositories {
      //...
      maven(url = 'https://www.jitpack.io')
    }
    
    dependencies {
      //...
      implementation 'fauxkiwi:pumpkin-engine:-SNAPSHOT'
    }
For other dependency management systems, please refer to the wiki.
***-->
## Use it!
Before you start writing your first game, take a look at the Sandbox.kt test. <br>
Since this is currently only using Kotlin JVM, you can also use Java.
### Creating an application and main() function
To create an application, create a class that extends `com.pumpkin.core.Application`. <br>
In your main() function, simply call

    Application.set(/*Your class name*/())
to start the game.
### Methods of Application
- `init()`: Called once on initialization
- `shutdown()`: Called once on finalisation
- `run()`: Called every frame. Currently, you have to clear the screen in here
### Layers
This engine is using layers that are stacked on top of each other. <br>
For your game, create a layer by extending `com.pumpkin.core.layer.Layer`. <br>
A layer contains five event functions:
- `onAttach()`: Called when the layer is attached
- `onDetach()`: Called when the layer is detached
- `onUpdate(ts: Timestep)`: Called every frame. ts is the time since the last frame
- `onImGuiRender()`: Called in the ImGui thread. Use it to display ImGui UIs
- `onEvent(event: Event)`: Called on every event

To attach a layer, simply call `Application#pushLayer` in your application.
### Events
Events are one of the core functions of this engine.
You can dispatch events in the onEvent functions of Application and layer. <br>
To dispatch the events, call

    val dispatcher = EventDispatcher(event)
    dispatcher.dispatch</*Event type you want to dispatch*/> {
      //Do something
    }
Of course, you can also use method references for this.
### Rendering
Rendering is probably the most important thing in a game engine. So here is how it works: <br>
Currently you have to clear the screen manually. Do this in `Application#run`:

    RendererCommand.setClearColor(/*Clear color*/) // Technically, you only have to do this once
    RendererCommand.clear()
To render something on your screen, do the following in `Layer#onUpdate`:

    Renderer2D.beginScene(/*Camera*/)
    // Render code
    Renderer2D.endScene()
For a camera, you can simply create a camera in your Application using an `OrthographicCamera`, or you can use the `OrthographicCameraController` class.
To actually render something now, call in your render code:

    Renderer2D.drawQuad(/*...*/)
With this method, you can draw quads with a given position, scale and optionally a rotation (radians). You can use textures or colors or a tinted texture.
### Full documentation
Please refer to the wiki for a full documentation of all features this engine has to offer
***
## Contributing
Of course, you're welcome to contribute to the engine.
***
Credits: kotlin-graphics for ImGui; TheCherno for inspiration

Remember this work is licensed under Apache 2.0

[empty]: https://raw.githubusercontent.com/FauxKiwi/Pumpkin-Engine/master/screenshots/empty.png

[file_dialog]: https://raw.githubusercontent.com/FauxKiwi/Pumpkin-Engine/master/screenshots/file_dialog.png

[gizmos]: https://raw.githubusercontent.com/FauxKiwi/Pumpkin-Engine/master/screenshots/gizmos.png

[light_theme]: https://raw.githubusercontent.com/FauxKiwi/Pumpkin-Engine/master/screenshots/light_theme.png

[pink_cube]: https://raw.githubusercontent.com/FauxKiwi/Pumpkin-Engine/master/screenshots/pink_cube.png

<!--[view]: https://cdn.discordapp.com/attachments/581185346465824770/781936000196149258/unknown.png

[view2]: https://cdn.discordapp.com/attachments/581185346465824770/783257581169672202/unknown.png

[view3]: https://cdn.discordapp.com/attachments/581185346465824770/784755406945517568/unknown.png

[textures]: https://cdn.discordapp.com/attachments/581185346465824770/786656957170843688/unknown.png

[game_of_life]: https://cdn.discordapp.com/attachments/581185346465824770/786663113549414440/unknown.png

[stresstest_]: https://cdn.discordapp.com/attachments/581185346465824770/786665995388452914/unknown.png

[stresstest]: https://cdn.discordapp.com/attachments/581185346465824770/787288977237475328/unknown.png-->