from OpenGL.GLUT import *    	
from OpenGL.GL import *		
from OpenGL.GLU import *	
import time     		
from math import *    		
import sys
import random
from Image import *

ESCAPE = 27

xrot = 0
yrot = 0

walkbias = 0
walkbiasangle = 0

lookupdown = 0.0
piover180 = 0.0174532925

xpos, zpos = (0.0, 0.0)

timer = 0           # Continuously counts up

image = 0           # Image data
curTex = ""         # Holds the name of texture currently in use
texture = 0         # Holds the name of a new texture being loaded

win = False

enemyGroup = []     # References to all enemies in the game
numEnemies = 1

scale = 1
animation = 0
animation_counter = 10

menu = True         # HUD menu flag
showPos = False     # HUD current position flag
showFog = False     # Fog flag
showLight = True    # Lighting flag
showTime = False    # Use clock flag

class Enemy:
    """
    Represents an enemy in the game.
    """
    x = 0
    y = 0
    z = 0
    time_to_move = 0
    state = ""
    animation_time = 8
    enemy_speed = .1
    def __init__(self, enemy_x, enemy_y, enemy_z):
        """Place an enemy at the coordinates given."""
        self.x = enemy_x
        self.y = enemy_y
        self.z = enemy_z
        self.time_to_move = 30
        self.state = "idle"
    def display(self):
        """
        Show the enemy on the screen
        Enemy is automatically rotated to face the player
        """
        glPushMatrix()
        glTranslatef(self.x, self.y, self.z)
        if not (xpos == self.x):  # Stay inside trigonometric domain
            #  Orient Billboard - Prevents 'popping' when crossing the enemy's local quadrants
            angle = -degrees(atan((zpos-self.z)/(xpos-self.x)))
            rot = angle+90 if self.x < xpos else angle-90  # Fancy!
            glRotatef(rot , 0, 1, 0)
        self.animate()
        glBegin(GL_QUADS)
        glColor3f(1, 1, 1)

        glTexCoord2f(0.0, 0.0); glVertex3f(-0.375, -1.00,  0.00)  # Bottom Left Of The Texture and Quad
        glTexCoord2f(1.0, 0.0); glVertex3f( 0.375, -1.00,  0.00)  # Bottom Right Of The Texture and Quad
        glTexCoord2f(1.0, 1.0); glVertex3f( 0.375,  0.00,  0.00)  # Top Right Of The Texture and Quad
        glTexCoord2f(0.0, 1.0); glVertex3f(-0.375,  0.00,  0.00)  # Top Left Of The Texture and Quad
        
        glEnd();
        glPopMatrix()
    def movetowards(self, obj_x, obj_z):
        """Move the enemy towards the player at a regulated speed"""
        # Rough distance calculation
        if abs(abs(obj_x) - abs(self.x)) > 2 or abs(abs(obj_z)-abs(self.z)) > 2:
            self.state = "idle"
            return
        self.state = "walking"
        self.time_to_move -= 1
        if self.time_to_move <= 0:
            self.time_to_move = 30
            if self.x < obj_x: self.x += self.enemy_speed
            if self.x > obj_x: self.x -= self.enemy_speed
            if self.z < obj_z: self.z += self.enemy_speed
            if self.z > obj_z: self.z -= self.enemy_speed
    def repos(self):
        """Randomizes enemy position (used when game restarts)"""
        self.x = random.randint(1, 4)-1.5
        self.z = -random.randint(2, 7)
    def animate(self):
        """Animate the enemy based on its current state"""
        self.animation_time -= .25
        if self.animation_time < 0: self.animation_time = 8
        if self.state == "idle":
            LoadTextures("enemy/enemy" + str(int(self.animation_time%8)) + ".png")
        if self.state == "walking":
            LoadTextures("enemy/enemy" + str(int(self.animation_time%4 + 8)) + ".png")

def InitGL(Width, Height):
    glEnable(GL_TEXTURE_2D)                             # Enable texturing
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)   # Blending function
    glEnable(GL_BLEND)                                  # Turn blending on (with the aforementioned function)
    glEnable(GL_LIGHTING)                               # Enable lighting
    glClearColor(0.0, 0.0, 0.0, 0.0)
    glClearDepth(1.0)
    glDepthFunc(GL_LESS)
    glEnable(GL_DEPTH_TEST)
    glShadeModel(GL_SMOOTH)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()

    gluPerspective(45.0,Width/Height,0.1,100.0)
    glMatrixMode(GL_MODELVIEW)

def loadLights():
    """
       Set up lighting in the scene
    """
    
    if showLight: glEnable(GL_LIGHTING)
    else: glDisable(GL_LIGHTING)
    
    # Calculate lighting for each side of the same plane
    #glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE)
    
    glPushMatrix()
    glEnable(GL_LIGHT1)
    glLightfv(GL_LIGHT1, GL_AMBIENT,  [1,1,1,1])
    #glLightfv(GL_LIGHT1, GL_DIFFUSE,  [.8,.8,.8,1])
    #glLightfv(GL_LIGHT1, GL_POSITION, [0,.5,-.5,1])
    glPopMatrix()

def loadFog():
    global showFog
    
    # Check fog flag
    if showFog: glEnable(GL_FOG)
    else: glDisable(GL_FOG)
    
    fogfilter= 0
    fogColor = [0.5, 0.5, 0.5, 1.0]

    # Fog type. 1 = geometry only.  2 = world fog
    if not showFog == 1: glClearColor(0, 0, 0, 1)
    if showFog == 2: glClearColor(0.5, 0.5, 0.5, 1.0)
     
    glFogi(GL_FOG_MODE, GL_LINEAR)
    glFogfv(GL_FOG_COLOR, fogColor)
    glFogf(GL_FOG_DENSITY, 0.1)
    glHint(GL_FOG_HINT, GL_NICEST)
    glFogf(GL_FOG_START, 1.0)
    glFogf(GL_FOG_END, 9.0)
    
def LoadTextures(texture):
    global image, curTex
    
    if texture == curTex: return    # Don't load a texture if it is already in use
                                    # Prevents lagging caused by constantly reloading the texture data
    
    curTex = texture                # New texture gets marked as the current loaded texture
    
    # All of my images are jpgs except the enemy.  I would have to change this later if I added more png textures
    image = open(texture)
	
    ix = image.size[0]
    iy = image.size[1]

    # Convert the IMAGE DATA to RGBA before storing it.  Images with no alpha channel (jpgs) will still work properly.
    image = image.convert("RGBA").tostring("raw", "RGBA", 0, -1)
    glPixelStorei(GL_UNPACK_ALIGNMENT,1)
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)

def resizeScreen(Width, Height):
    if (Height==0): Height=1
    glViewport(0, 0, Width, Height)		

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()

    gluPerspective(45.0,float(Width)/float(Height),0.1,100.0)
    glMatrixMode(GL_MODELVIEW)

def DrawGLScene():
    global image
    xtrans = -xpos
    ztrans = -zpos
    ytrans = -walkbias - .75
    sceneroty = 360.0 - yrot

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)	
    glLoadIdentity()
    
    showHUD()           # Calculate and display the current time on the HUD
    #checkWinLose()      # Check winning/losing conditions
    glRotatef(lookupdown, 1.0, 0, 0)
    glRotatef(sceneroty, 0, 1.0, 0)
    glTranslatef(xtrans, ytrans, ztrans)
    loadLights()        # Load lights into the scene (from above)
    loadFog()
    
    glPushMatrix()
    drawFloor()         # Draws the floor
    drawMaze()          # Draw all walls
    
    drawTV(-1, 3, -7)
    drawTorch(3, 0, -6)
    glPopMatrix()

    # Enemies
    for i in enemyGroup:
        i.movetowards(xpos, zpos)  # This will keep enemies from moving when the game has ended
        i.display()                                 # Display the enemies on the screen

    glutSwapBuffers()

def showHUD():
    global xpos, zpos, showTime
    
    # Display menu and position on screen
    if showPos:
        drawText(200, 450, "X: " + str(round(xpos, 1)), 0)
        drawText(240, 450, "Z: " + str(round(-zpos, 1)), 0)
    
    if menu:
        drawText(20, 415, "F  - Toggle Fog", 0)
        drawText(20, 400, "L  - Toggle Lights", 0)
        drawText(20, 385, "M - Toggle Menu", 0)
        drawText(20, 370, "Z  - Show Position", 0)

def drawFloor():
    """
    Places floor
    """
    glPushMatrix()
    glTranslatef(-1.5, 0, -1)
    LoadTextures("ground.jpg")
    glBegin(GL_QUADS)
    glColor3f(1, 1, 1)

    glTexCoord2f(0.0, 0.0); glVertex3f(0, 0,  0)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(2.5, 0.0); glVertex3f(5, 0,  0)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(2.5, 4.0); glVertex3f(5, 0, -7)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 4.0); glVertex3f(0, 0, -7)    # Top Left Of The Texture and Quad
    
    glEnd();
    
    glPopMatrix()

def drawWall(x, y, z, dir):
    """
    Places a wall starting at the given x,y,z coordinates.
    dir - Direction in which wall is built
        0 - build in x direction
        1 - build in z direction
        2 - flip x (for debugging)
        3 - flip z (for debugging)
    """
    glPushMatrix()
    glTranslatef(x, y, z)
    LoadTextures("wall.jpg")
    if dir == 1:
        glRotatef(90, 0, 1, 0)
    if dir == 2:
        glRotatef(180, 0, 1, 0)
    if dir == 3:
        glRotatef(270, 0, 1, 0)
    glBegin(GL_QUADS)
    glColor3f(1, 1, 1)
    
    # Front Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0,  0,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(1,  0,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(1,  1,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0,  1,  0.00)    # Top Left Of The Texture and Quad
    
    # Back Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(1,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(1,  1, -0.25)	# Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0,  1, -0.25)    # Top Left Of The Texture and Quad
    
    # Top Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0,  1,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(1,  1,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(1,  1, -0.25)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0,  1, -0.25)    # Top Left Of The Texture and Quad
    
    # Bottom Face       
    glTexCoord2f(0.0, 0.0); glVertex3f(0,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(1,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(1,  0,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0,  0,  0.00)    # Top Left Of The Texture and Quad
    
    # Right face
    glTexCoord2f(0.0, 0.0); glVertex3f(1,  0,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(1,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(1,  1, -0.25)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(1,  1,  0.00)    # Top Left Of The Texture and Quad
    
    # Left Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0,  0,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0,  1,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0,  1, -0.25)    # Top Left Of The Texture and Quad
    
    glEnd();
    glPopMatrix()
    
def drawTorch(x, y, z):
    """
    Places a torch at the given x,y,z coordinates.
    """
    global xpos, zpos, animation, animation_counter, scale
    glPushMatrix()
    glTranslatef(x, y, z)
    LoadTextures("torch.png")
    glBegin(GL_QUADS)
    glColor3f(1, 1, 1)
    # Front Face
    glTexCoord2f(0.0, 0.0); glVertex3f(-.125,  0,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.125,  0,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.125,  1,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.125,  1,  0.00)    # Top Left Of The Texture and Quad
    
    # Back Face
    glTexCoord2f(0.0, 0.0); glVertex3f(-.125,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.125,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.125,  1, -0.25)	# Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.125,  1, -0.25)    # Top Left Of The Texture and Quad
    
    # Top Face
    glTexCoord2f(0.0, 0.0); glVertex3f(-.125,  1,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.125,  1,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.125,  1, -0.25)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.125,  1, -0.25)    # Top Left Of The Texture and Quad
    
    # Bottom Face       
    glTexCoord2f(0.0, 0.0); glVertex3f(-.125,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.125,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.125,  0,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.125,  0,  0.00)    # Top Left Of The Texture and Quad
    
    # Right face
    glTexCoord2f(0.0, 0.0); glVertex3f(.125,  0,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.125,  0, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.125,  1, -0.25)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(.125,  1,  0.00)    # Top Left Of The Texture and Quad
    
    # Left Face
    glTexCoord2f(0.0, 0.0); glVertex3f(-.125,  0, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(-.125,  0,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(-.125,  1,  0.00)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.125,  1, -0.25)    # Top Left Of The Texture and Quad
    
    glEnd();
    
    # Flame Billboard
    glTranslate(0, 0, -.125)
    LoadTextures("flame.png")
    animation_counter -= 1
    if animation_counter < 0:
        animation_counter = 10
        scale = random.randint(1, 10)/10.0+.75
    glScale(scale, 1, scale)
    if not (xpos == x):  # Stay inside trigonometric domain
            #  Orient Billboard - Prevents 'popping' when crossing the enemy's local quadrants
            angle = -degrees(atan((zpos-z)/(xpos-x)))
            rot = angle+90 if x < xpos else angle-90  # Fancy!
            glRotatef(rot, 0, 1, 0)
    glTranslate(0, -.125, .25)
    glBegin(GL_QUADS)
    glTexCoord2f(0.0, 0.0); glVertex3f(-.2, 1, 0)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(.2,  1,  0)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(.2,  1.5,  0)    # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(-.2,  1.5,  0)    # Top Left Of The Texture and Quad
    glEnd();
    glPopMatrix()
    
    glPushMatrix()
    glEnable(GL_LIGHT3)
    lightmod = scale
    glLightfv(GL_LIGHT3, GL_DIFFUSE,  [0.3*lightmod,0.1*lightmod,0,1])
    glLightfv(GL_LIGHT3, GL_POSITION, [x,y+2,z,1])
    glPopMatrix()

def drawTV(x, y, z):
    """
    Places a crate at the given x,y,z coordinates.
    """
    global animation
    glPushMatrix()
    glTranslatef(x, y, z)
    glScale(3, 3, 3)
    glRotate(45, 0, 1, 0)
    LoadTextures("tv.jpg")
    glBegin(GL_QUADS)
    glColor3f(1, 1, 1)
    
    # Front Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0.00, -1.00,  0.00)  # Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.25, -1.00,  0.00)  # Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.25, -0.75,  0.00)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.00, -0.75,  0.00)  # Top Left Of The Texture and Quad
    
    # Back Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0.00, -1.00, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.25, -1.00, -0.25)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.25, -0.75, -0.25)	# Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.00, -0.75, -0.25)  # Top Left Of The Texture and Quad
    
    # Top Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0.00, -0.75,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.25, -0.75,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.25, -0.75, -0.25)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.00, -0.75, -0.25)  # Top Left Of The Texture and Quad
    
    # Bottom Face       
    glTexCoord2f(0.0, 0.0); glVertex3f(0.00, -1.00,  0.00)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.25, -1.00,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.25, -1.00, -0.25)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.00, -1.00, -0.25)  # Top Left Of The Texture and Quad
    
    # Right face
    glTexCoord2f(0.0, 0.0); glVertex3f(0.25, -1.00, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.25, -1.00,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.25, -0.75,  0.00)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.25, -0.75, -0.25)  # Top Left Of The Texture and Quad
    
    # Left Face
    glTexCoord2f(0.0, 0.0); glVertex3f(0.00, -1.00, -0.25)	# Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.00, -1.00,  0.00)	# Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.00, -0.75,  0.00)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.00, -0.75, -0.25)  # Top Left Of The Texture and Quad
    
    glEnd();
    
    animation -= .5
    if animation < 0: animation = 14
    LoadTextures("static/static" + str(int(animation%7)) + ".jpg")
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL)
    
    glBegin(GL_QUADS)
    glTexCoord2f(0.0, 0.0); glVertex3f(0.025, -0.95,  0.001)  # Bottom Left Of The Texture and Quad
    glTexCoord2f(1.0, 0.0); glVertex3f(0.225, -0.95,  0.001)  # Bottom Right Of The Texture and Quad
    glTexCoord2f(1.0, 1.0); glVertex3f(0.225, -0.80,  0.001)  # Top Right Of The Texture and Quad
    glTexCoord2f(0.0, 1.0); glVertex3f(0.025, -0.80,  0.001)  # Top Left Of The Texture and Quad
    glEnd();
    
    lightmod = .5*sin((animation/14.0)*3.14)
    glEnable(GL_LIGHT2)
    glLightfv(GL_LIGHT2, GL_DIFFUSE,  [lightmod,lightmod,lightmod,1])
    glLightfv(GL_LIGHT2, GL_POSITION, [0,0,0,1])
    glPopMatrix()

def drawMaze():
    # Front wall
    drawWall(-1.5, 0, -1, 0)
    drawWall( 0.5, 0, -1, 0)
    drawWall( 1.5, 0, -1, 0)
    drawWall( 2.5, 0, -1, 0)
    
    # Left wall
    for i in range(7): drawWall(-1.5, 0, -1*(i+1), 1)
    
    # Right wall
    for i in range(7): drawWall( 3.5, 0, -1*(i+2), 3)
    
    # Back wall
    for i in range(5): drawWall(i - 1.5, 0, -8, 0)
    
def drawText(x, y, text, size):
    """
    Draws text on the screen at the given x,y coordinates.
    size - Specifies the text size
        0 - small text
        1 - large text
    """
    glPushMatrix()
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL)
    glColor3f(1, 1, 1)
    glWindowPos2d(x, y)
    for i in text:
        if size == 0: glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_10, ord(i))
        if size == 1: glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, ord(i))
    glPopMatrix()

def keyPressed(key, x, y):
    global light, lookupdown, walkbiasangle, walkbias
    global yrot, xpos, zpos, enemyGroup, walls, menu, showPos, showFog, showLight, showTime
    
    if ord(key) == ESCAPE: sys.exit()
     
    elif key == 'W' or key == 'w':
        xpos -= sin(yrot*piover180) * 0.05
        zpos -= cos(yrot*piover180) * 0.05
        if (walkbiasangle >= 359.0):
            walkbiasangle = 0.0
        else:
            walkbiasangle+= 10
            walkbias = sin(walkbiasangle * piover180)/20.0
    
    elif key == 'S' or key == 's':
        xpos += sin(yrot*piover180) * 0.05
        zpos += cos(yrot*piover180) * 0.05
        if (walkbiasangle <= 1.0):
            walkbiasangle = 359.0
        else:
            walkbiasangle-= 10
            walkbias = sin(walkbiasangle * piover180)/20.0
        
    elif key == 'A' or key == 'a':
        xpos -= sin((yrot+90)*piover180) * 0.05
        zpos -= cos((yrot+90)*piover180) * 0.05
        
    elif key == 'D' or key == 'd':
        xpos += sin((yrot+90)*piover180) * 0.05
        zpos += cos((yrot+90)*piover180) * 0.05
        
    # Toggle position shown in HUD
    elif key == 'Z' or key == 'z': 
        showPos = not showPos
        
    # Toggle menu
    elif key == "M" or key == "m": 
        menu = not menu
    
    # Toggle Fog
    elif key == "F" or key == "f":
        showFog = (showFog + 1)%3
    
    # Toggle lighting
    elif key == "L" or key == "l":
        showLight = not showLight

    else: print("Key %d pressed. No action there yet.\n"%(ord(key)))

def specialKeyPressed(key, x, y):
    global lookupdown, walkbiasangle, walkbias, yrot, xpos, zpos 
    
    if key == GLUT_KEY_UP:
        lookupdown -= 4

    elif key ==GLUT_KEY_DOWN:
        lookupdown += 4

    elif key == GLUT_KEY_LEFT: yrot += 4

    elif key == GLUT_KEY_RIGHT: yrot -= 4

    else: print("Special key %d pressed. No action there yet.\n"%(key))
    
for i in range(numEnemies):
    enemyGroup.append(Enemy(random.randint(1, 4)-1.5, 1, -random.randint(2, 7)))
    
glutInit(sys.argv)
glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH | GLUT_ALPHA)
glutInitWindowSize(640, 480)
glutInitWindowPosition((glutGet(GLUT_SCREEN_WIDTH)-640)/2, (glutGet(GLUT_SCREEN_HEIGHT)-480)/2)
glutCreateWindow("Assignment 7 - Animation & Lighting")
glutDisplayFunc(DrawGLScene)
#glutFullScreen()
glutIdleFunc(DrawGLScene)
glutReshapeFunc(resizeScreen)
glutKeyboardFunc(keyPressed)
glutSpecialFunc(specialKeyPressed)
InitGL(640, 480)
glutMainLoop()