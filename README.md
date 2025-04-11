# Nanonite
</br>
Nanonite is a JAVA project containing a integrated game engine and a Minecraft-Based game. While Nanonite provides anything abstract from a game engine, the game has just everything essentials to it like assets, scenes, entities code and so on.
</br>
</br>

## Build
</br>
This project uses Gradle as its build system. To build the project, follow these steps:

1. Install Gradle: Download and install Gradle from https://gradle.org/.
2. Open a terminal or command prompt: Navigate to the root directory of the project.
3. Run the build command: Execute the following command: ``./gradlew build``

If you're using an IDE that has a Gradle implementation already like IntelliJ, you'll just need to open the project in your IDE without downloading anything !

## Controls
</br>

### Keyboard & Mouse
</br>
Use the mouse to control where the camera is facing.
</br>
Use WASD (or ZQSD if you're on Azerty) to move the player
</br>
Hit F3 to check the debug infos (just like Minecraft haha)
</br>
Use SPACE to go up and LEFT SHIFT to go down

## Features
</br>
In the project, the following features will be the main one implemented and improved for better performance : 

- [ ] Rendering system
  - [ ] Shaders:
  	- [ ] Water simulation
	- [ ] No visual artifacts on textures
	- [ ] PBR pipeline
    - [x] Directional light
	- [ ] Lights
  	- [ ] Lights stored in cube maps
	- [ ] Sky Box reflection
	- [ ] SSR
	- [x] Ambient Occlusion
	- [ ] HDR, ACES tone mapping
	- [ ] Bloom
	- [ ] Automatic exposure
	- [ ] Lens flare
	- [ ] Color grading
	- [x] Fog (not Volumetric)
	- [x] Underwater fog
	- [ ] God rays	
  	- [ ] Shadows
  - [ ] Adding ``#eva_include`` in my shader programs

- [x] Chunk system
  - [ ] Blocks physics
  - [ ] Placing blocks
  - [ ] breaking blocks
- [x] World Generation
  - [ ] Biome generation
  - [ ] Water cave
  - [ ] caves (and cliffs ?)
  - [ ] Depths
  - [ ] Structure generations

- [ ] Multiplayer
    - [ ] Connection to server and handshake
    - [ ] Server can validate moves
    - [ ] Server knows player position to optimize chunk logic stuff
    - [ ] Undo Stuff On client
    - [ ] Buffering
    - [ ] Rubber banding
    - [ ] Entities

