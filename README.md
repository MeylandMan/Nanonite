# MyCraft
</br>
MyCraft is my personal "optimized" version of Minecraft. The project has not the purpose of being commercialize or anything like that. 
I just want to have fun playing with OpenGL while not dealing too much with all the C++ memory mangement.
</br>
</br>
If you just want to test the project, check out de releases tab to download it an application version.
</br>
Howerer, if you're here to inspect the code or anything (you just like peeping through other people's codes) than take a look !

## Build
</br>
The project is basically made using IntelliJ. So as long you start opening it with this IDE, everything should work perfectly fine !
</br>
All you have to do is just build the project and test it.

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
In the entire project, the following features will be implemented and improved for better performance or just for fun : 

- [ ] Rendering system
  - [ ] Shaders:
  	- [ ] Water simulation
	- [ ] No visual artifacts on textures
	- [ ] PBR pipeline
	- [ ] Lights
  	- [ ] Lights stored in cube maps
	- [ ] Sky Box reflection
	- [ ] SSR
	- [ ] HBAO / SSAO
	- [ ] HDR, ACES tone mapping
	- [ ] Bloom
	- [ ] Automatic exposure
	- [ ] Lens flare
	- [ ] Color grading
	- [ ] Fog
	- [ ] Underwater fog
	- [ ] God rays	
	- [ ] Fake Shadows for all light types
  	- [ ] Shadows
   	- [ ] Cascaded shadow maps
	- [ ] Depth of field
  - [ ] Adding ``#include`` in my shader programs

- [x] Chunk system
  - [ ] Blocks physics
  - [ ] Placing blocks
  - [ ] breaking blocks
- [x] World Generation
  - [ ] Biome generation
  - [ ] Water cave
  - [ ] caves (and cliffs ?)
  - [ ] Structure generations

- [ ] Multy player
    - [ ] Connection to server and handshake
    - [ ] Server can validate moves
    - [ ] Server knows player position to optimize chunk logic stuff
    - [ ] Undo Stuff On client
    - [ ] Buffering
    - [ ] Rubber banding
    - [ ] Entities

