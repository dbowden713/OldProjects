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

menu = True         # HUD menu flag
showPos = False     # HUD current position flag
showTime = False    # Use clock flag
solids = []

def InitGL(Width, Height):
    glEnable(GL_TEXTURE_2D)                             # Enable texturing
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)   # Blending function
    glEnable(GL_BLEND)                                  # Turn blending on (with the aforementioned function)
    glClearColor(0.0, 0.0, 0.0, 0.0)
    glClearDepth(1.0)
    glDepthFunc(GL_LESS)
    glEnable(GL_DEPTH_TEST)
    glShadeModel(GL_SMOOTH)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()

    gluPerspective(45.0,Width/Height,0.1,100.0)
    glMatrixMode(GL_MODELVIEW)

    
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
    glRotatef(lookupdown, 1.0, 0, 0)
    glRotatef(sceneroty, 0, 1.0, 0)
    glTranslatef(xtrans, ytrans, ztrans)
    
    glPushMatrix()
    drawFloor()         # Draws the floor
    drawRoom()          # Draw all walls
    checkCollisions()
    
    glPopMatrix()

    glutSwapBuffers()

def showHUD():
    global xpos, zpos, showTime
    
    # Display menu and position on screen
    if showPos:
        drawText(200, 450, "X: " + str(round(xpos, 2)), 0)
        drawText(240, 450, "Z: " + str(round(-zpos, 2)), 0)
    
    if menu:
        drawText(20, 415, "M - Toggle Menu", 0)
        drawText(20, 400, "Z  - Show Position", 0)

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
    global solids
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

def drawRoom():
    # Front wall
    drawWall(-1.5, 0, -1, 0); solids.append((-1.5, -.5, -1, -1.25, 0))
    drawWall( 0.5, 0, -1, 0); solids.append((0.5, 1.5, -1, -1.25, 0))
    drawWall( 1.5, 0, -1, 0); solids.append((1.5, 2.5, -1, -1.25, 0))
    drawWall( 2.5, 0, -1, 0); solids.append((2.5, 3.5, -1, -1.25, 0))
    
    # Left wall
    for i in range(7): 
        drawWall(-1.5, 0, -1*(i+1), 1); solids.append((-1.5, -1.25, -(i+1), -(i+2), 1))
    
    # Right wall
    for i in range(7): drawWall( 3.5, 0, -1*(i+2), 3); solids.append((3.25, 3.5, -(i+1), -(i+2), 3))
    
    # Back wall
    for i in range(5): drawWall(i - 1.5, 0, -8, 0); solids.append(((i-1.5), (i-.5), -8, -8.25, 0))
    
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

def checkCollisions():
    global xpos, zpos, solids
    for i in solids:
        if xpos > i[0] and xpos < i[1] and zpos < i[2] and zpos > i[3]:
            if i[4] == 0: zpos += 0.1
            if i[4] == 1: xpos += 0.1
            if i[4] == 2: zpos -= 0.1
            if i[4] == 3: xpos -= 0.1
    
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
    
glutInit(sys.argv)
glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH | GLUT_ALPHA)
glutInitWindowSize(640, 480)
glutInitWindowPosition((glutGet(GLUT_SCREEN_WIDTH)-640)/2, (glutGet(GLUT_SCREEN_HEIGHT)-480)/2)
glutCreateWindow("Assignment 11 - Collision Detection")
glutDisplayFunc(DrawGLScene)
#glutFullScreen()
glutIdleFunc(DrawGLScene)
glutReshapeFunc(resizeScreen)
glutKeyboardFunc(keyPressed)
glutSpecialFunc(specialKeyPressed)
InitGL(640, 480)
glutMainLoop()