# Nanonite
</br>
Nanonite is a personnal JAVA project containing a integrated game engine and a Minecraft Clone. While Nanonite provides me anything to abstract from a game engine, the game has just everything essentials to it like assets, scenes, entities code and so on. I decided to take Java just because of "proving" that YOU can make an optmized Voxel game with that language.
</br>
</br>

## Build
</br>
The build System used in this one is Gradle. You first need to download it, and then following these steps.

### IntelliJ Idea
IntelliJ comes with a direct support for Gradle. All you have to do is just open the project, create an application, and you're done !

### Eclipse 
TBA

### Others
You first need to type this command to build the project ``./gradlew build`` (try to check your Gradle version by typing ``./gradlew --version``
Then, just use ``./gradlew run`` to run the project.

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
	- [ ] Underwater fog
	- [ ] God rays	
	- [ ] Fake Shadows for all light types
  	- [ ] Shadows
   	- [ ] Cascaded shadow maps
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

