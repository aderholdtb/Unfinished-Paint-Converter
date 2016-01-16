//import java.awt.Color;
//Things to do
/*
 * Need to resize the image correctly
 * Detect out of place thing (resize the paint program)
 * Fix the white spaces in the picture
 * Try and make the program faster
 * save the image after it is complete
 * Make a window with options
 * Try and make an algorithm for painting instead of copying it
 */
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
//import java.awt.Robot;
//import java.awt.AWTException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.activation.MimetypesFileTypeMap;
//import java.awt.*;
import javax.imageio.ImageIO;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
//make sure to take into account the different screen sizes for different computers
public class Main {
	final static int[] startButton      = {1333, 17};//these are the coordinates for all locations on the desktop
	final static int[] programs         = {1046, 558};
	final static int[] editColors       = {993, 74};
	final static int[] colorInit        = {631, 386};
	final static int[] redInput         = {846, 455};
	final static int[] greenInput       = {846, 478};
	final static int[] blueInput        = {846, 500};
	final static int[] okButtonColor    = {464, 518};
	final static int[] resizePicture    = {187, 77};
	final static int[] zoomOut          = {187, 77};
	final static int[] pixelRatio       = {215, 173};
	final static int[] maintainAspect   = {66, 284};
	final static int[] horizontalPixels = {244, 208};
	final static int[] verticalPixels   = {244, 248};
	final static int[] okButtonResize   = {151, 464};
	final static int[] desktop          = {73, 146};
	final static int[] fileName         = {178, 398};
	final static int[] saveButton       = {517, 462};
	final static int[] maxPageSize125   = {10336, 4728};
	final static int[] maxPageSize25    = {5168, 2364};
	final static int[] maxPageSize50    = {2584, 1182};
	final static int[] maxPageSize100   = {1292, 591};
	final static int[] target1Colors    = {231, 135, 121};
	final static int[] target2Colors    = {131, 180, 206};
	final static int[] target1ColorsOpen= {231, 74, 50};
	final static int[] target2ColorsOpen= {106, 185, 74};
	final static int[] edgesOfPortrait  = {5, 140,
								    	   5, 730,
								    	   1296, 140,
								    	   1296, 730};
	
	static int[] newCoordinates = edgesOfPortrait;//these are the new coordinates for the change in image size
	final static int waitTime = 750;//in Milliseconds
	static int imageX = 0, imageY = 0;//this is the images dimensions 
	static Vector<Integer> red = new Vector<Integer>(), green = new Vector<Integer>(), blue = new Vector<Integer>();//these are the colors for each pixel
	static Scanner input = new Scanner(System.in);//takes in input
	final static int sharpness = 10;//this is the quality of the image
	static int[][][] colors = new int[(int)(255/sharpness) + 1][(int)(255/sharpness) + 1][(int)(255/sharpness) + 1];//makes room for the colors per pixel
	public static Vector<Integer> values = new Vector<Integer>();//these are the values for the coordinates for a certain pixel color
	public static boolean color = true;//black and white or colored picture
	
	public static void main(String[] args) throws IOException, InterruptedException, AWTException {
    	//commands go here

		loadPaintInfo();//loads the picture info into the variables
    	openPaint();//opens paint and sets up the image for painting
    	detectFrame();
    	resizeImage();//if the image is too large then resize it so that it fits
    	startPainting();//loop painting until it is complete
    	saveImage();
    }
    
    public static void loadPaintInfo() throws IOException{
    	System.out.println("Enter the file name location of the image");//takes a user input for loading a certain image
    	String filename = "C:\\Users\\Owner\\Desktop\\mona.jpg";//the image location on the computer
      
    	File file= new File(filename);//loads the file into a variable
      
    	String mimetype= new MimetypesFileTypeMap().getContentType(file);
    	String type = mimetype.split("/")[0];
      
    	if(!file.exists()){//checks if the image exists
    		System.out.println("ERROR: THE LOCATION ENTERED DOES NOT EXIST");
    		System.exit(0);
    	}

    	BufferedImage image = ImageIO.read(file);//reads the image so that colors per pixel can be determined
   	  
    	imageX = image.getWidth();//gets the image width
    	imageY = image.getHeight();//gets the image height
    	
    	int avgColor = 0;//takes the avg color(for black and white only)
    	
    	for(int x = 0; x < imageX; x++){//takes the picture pixel by pixel and determines the colors in each one
    		for(int y = 0; y < imageY; y++){ 
    			int clr =  image.getRGB(x, y);//gets the color
    			int tempRed = (clr >> 16) & 0xff;//breaks down the color into red amount
    			int tempGreen = (clr >> 8) & 0xff;//breaks down the color into the green amount
    			int tempBlue = (clr) & 0xff;//breaks down the color into the blue amount
    			
    			if(color){//if print in color then load regular colors per pixel
    				red.addElement(tempRed);//adds colors to their vector
    				green.addElement(tempGreen);
    				blue.addElement(tempBlue);
    				//This signals that there is a color in that amount(mostly to speed things up)
    				colors[(int)(tempRed/sharpness)][(int)(tempGreen/sharpness)][(int)(tempBlue/sharpness)] = 1;
    			}
    			else{//if the picture is to be printed in black and white
    				avgColor = (int)(tempRed + tempGreen + tempBlue)/3;//takes the avg color per pixel
    				red.addElement(avgColor);//adds the avg into their vector
    				green.addElement(avgColor);
    				blue.addElement(avgColor);
    				//This signals that there is a color in that amount(mostly to speed things up)
    				colors[(int)(avgColor/sharpness)][(int)(avgColor/sharpness)][(int)(avgColor/sharpness)] = 1;
    			}
    		}
    	}
    }
    
    //detects if the frame is full screen
    public static void detectFrame() throws AWTException, InterruptedException{
    	 Robot robot = new Robot();//to detect the colors per pixel
    	 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//grabs the screen size
    	 double width = screenSize.getWidth();
    	 double height = screenSize.getHeight();
    	 
    	 for(int y = 0; y < height; y++){
    		 for(int x = 0; x < width; x++){
    			 Color color = robot.getPixelColor(x, y);//gets the colors for a certain x, y location
    			 
    			 //if it detects the correct color
    			 if(color.getRed() == target1Colors[0] && color.getGreen() == target1Colors[1] && color.getBlue() == target1Colors[2]){
    				 color = robot.getPixelColor(x + 7, y);
    				 System.out.println("Found color 1" + " x = " + x  + " y = " + y);
    				 //checks a second color
    				 if(color.getRed() == target2Colors[0] && color.getGreen() == target2Colors[1] && color.getBlue() == target2Colors[2]){
    					 System.out.println("Found Color 2" + " x = " + (x + 7) + " y = " + y);
    					 System.out.println("Trying to resize the image");//trying to detect the resize button
    					 if(x != 4 && y != 10){
    						 for(int i = x; i < width; i++){
    							 color = robot.getPixelColor(i, y - 3);
    							 if(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0){
    								 color = robot.getPixelColor(i + 8, y - 3);
    								 System.out.println("(x, y) = (" + i + ", " + (y - 3) + ")");
    								 if(color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0){
    									 click(i + 8, y - 3);//clicks on the resize image
    									 TimeUnit.MILLISECONDS.sleep(waitTime);
    									 System.out.println("Paint has been maximized!!!");
    									 return;
    								 }
    							 }
    						 }
    					 }
    					 else{//if there are no problems with the coordinates
    						 System.out.println("Paint is already full screen!!!");
    						 return;//Paint is full screen
    					 }
    				 }
    			 }
    		 }
    	 }
    	//can't detect paint at all
    	 frameFront();//clicks on the paint icon
    	 System.out.println("Cant detect paint is open, opening it now");
    }
    
    //if paint isn't in front then change it
    public static void frameFront() throws AWTException{
     Robot robot = new Robot();
   	 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
   	 double height = screenSize.getHeight();
   	 int xTarget1 = 1328;//checks the side bar for the paint icon, these are the x coordiates to check
   	 int xTarget2 = 1333;
   	 
    	for(int y = 0; y < height; y++){
    		Color color = robot.getPixelColor(xTarget1, y);
    		//if the color has been detected on the side bar
    		if(color.getRed() == target1ColorsOpen[0] && color.getGreen() == target1ColorsOpen[1] && color.getBlue() == target1ColorsOpen[2]){
    			color = robot.getPixelColor(xTarget2, y - 4);
    			if(color.getRed() == target2ColorsOpen[0] && color.getGreen() == target2ColorsOpen[1] && color.getBlue() == target2ColorsOpen[2]){
    				click(xTarget1, y);//clicks on the target paint icon
    				System.out.println("opening paint again");
    				return;//paint has been reopened
    			}
    		}
    	}
    	//Paint is closed, shutting down
    	System.exit(0);
    }
    
    //grabs all the pixels that fall into a range of colors using the sharpness integer
    public static void getPixels(int r, int g, int b){
    	values.clear();//clears the previous colors from the vector
    	Vector<Integer> tempIndex = new Vector<Integer>();
    	
    	for(int x = 0; x < imageX; x++){
			 for(int y = 0; y < imageY; y++){

				 //checks if the pixel is in the color range
				 if((red.get((x * imageY) + y) >= r)   && (red.get((x * imageY) + y) < r + sharpness) &&
					(green.get((x * imageY) + y) >= g) && (green.get((x * imageY) + y) < g + sharpness) &&
					(blue.get((x * imageY) + y) >= b)  && (blue.get((x * imageY) + y) < b + sharpness)){
					 	colors[r/sharpness][g/sharpness][b/sharpness] = 1;
					 	values.addElement(x + newCoordinates[0]);
					 	values.addElement(y + newCoordinates[1]);
					 	tempIndex.addElement((x * imageY) + y);
				 	}
				 }
			 }
    	
    	if(values.size() < 20){//if there is less than 10 values then raise there color values to the next 
    		values.clear();
        	int tempValue = 0;
        	int tempElement = 0;
        	
    		for(int i = 0; i < tempIndex.size(); i++){//from the temp indexes grab all the values
    			tempValue = tempIndex.get(i);//raises the red value of the color for the next call
    			tempElement = red.get(tempValue);
    			red.set(tempValue, tempElement + sharpness);
    		}
    	}
    }
  
    //formats the image if the picture is too large for the current screen
    @SuppressWarnings("null")
	public static void formatImage(int percentage){
    	Vector<Integer> newRed = new Vector<Integer>(), newGreen = new Vector<Integer>(), newBlue = new Vector<Integer>();
    	int avgRed = 0, avgGreen = 0, avgBlue = 0;
    	int newImageX = (int) (imageX / percentage), newImageY = (int) (imageY / percentage);

    	for(int x = 0; x < imageX; x++){//goes from top to bottom, takes the average of consecutive values
    		for(int y = 0; y < imageY; y += percentage){
    			for(int i = 0; i < percentage; i++){
    				avgRed += (int)red.get((x * imageY) + y + i);//gets the values from the pixels
    				avgGreen += (int)green.get((x * imageY) + y + i);
    				avgBlue += (int)blue.get((x * imageY) + y + i);
    			}
    			
    			avgRed /= (int)percentage;//gets the average of the current pixel(resize the image)
    			avgGreen /= (int)percentage;
    			avgBlue /= (int)percentage;
    			
    			newRed.addElement(avgRed);//adds the new pixel to the vector
    			newGreen.addElement(avgGreen);
    			newBlue.addElement(avgBlue);
    			avgRed = 0; avgGreen = 0; avgBlue = 0;
    		}
    	}
    	
    	red.clear();//clears the vector so that the vector came be re-initialized
    	green.clear();
    	blue.clear();
    	imageY = newImageY;//gets the new width of the picture
    	
    	
    	//goes from left to right resizing the image
    	for(int y = 0; y < imageY; y++){
    		for(int x = 0; x < imageX; x += percentage){
    			for(int i = 0; i < percentage; i++){
    				avgRed += (int)newRed.get(((x + i) * imageY) + y);
    				avgGreen += (int)newGreen.get(((x + i) * imageY) + y);
    				avgBlue += (int)newBlue.get(((x + i) * imageY) + y);
    			}
    			
    			avgRed /= (int)percentage;
    			avgGreen /= (int)percentage;
    			avgBlue /= (int)percentage;
    			
    			red.addElement(avgRed);
    			green.addElement(avgGreen);
    			blue.addElement(avgBlue);
    			avgRed = 0; avgGreen = 0; avgBlue = 0;
    		}
    	}
    	
    	newRed.clear();
    	newGreen.clear();
    	newBlue.clear();
    	imageX = newImageX;
    	
    	//converting back to inserting by column
    	//formatting the way the elements are stored int he vector
    	for(int x = 0; x < imageX; x++){
    		for(int y = 0; y < imageY; y++){
				newRed.addElement(red.get((y * imageX) + x));
				newGreen.addElement(green.get((y * imageX) + x));
				newBlue.addElement(blue.get((y * imageX) + x));
    		}
    	}
    	
    	red.clear();
    	green.clear();
    	blue.clear();
    	
    	red = newRed;
    	green = newGreen;
    	blue = newBlue;
    }
    
    
    public static void openPaint() throws InterruptedException, AWTException{
    	click(startButton[0], startButton[1]);//clicks on the start icon
    	 TimeUnit.MILLISECONDS.sleep(2000);
    	click(programs[0], programs[1]);//clicks on the search bar
    	 TimeUnit.MILLISECONDS.sleep(waitTime);
        type("paint");//types paint into the search bar
        TimeUnit.MILLISECONDS.sleep(waitTime);
    	hit("enter");//presses the enter key
    	 TimeUnit.MILLISECONDS.sleep(waitTime);
    }
    
    
    //figures out how to resize the image based on the original picture size
    public static void resizeImage() throws InterruptedException, AWTException{
    	if(imageX < maxPageSize100[0] && imageY < maxPageSize100[1]){
    		newCoordinates[7] = newCoordinates[3] = (imageY / maxPageSize100[1]) * edgesOfPortrait[1];
    		newCoordinates[4] = newCoordinates[6] = (imageX / maxPageSize100[0]) * edgesOfPortrait[4];
    	}
    	else if(imageX < maxPageSize50[0] && imageY < maxPageSize50[1]){
    		newCoordinates[7] = newCoordinates[3] = (imageY / maxPageSize50[1]) * edgesOfPortrait[1];
    		newCoordinates[4] = newCoordinates[6] = (imageX / maxPageSize50[0]) * edgesOfPortrait[4];
    		formatImage(2);
    		click(zoomOut[0], zoomOut[1]);
    	}
     	else if(imageX < maxPageSize25[0] && imageY < maxPageSize25[1]){
    		newCoordinates[7] = newCoordinates[3] = (imageY / maxPageSize25[1]) * edgesOfPortrait[1];
    		newCoordinates[4] = newCoordinates[6] = (imageX / maxPageSize25[0]) * edgesOfPortrait[4];
    		formatImage(4);
    		click(zoomOut[0], zoomOut[1]);
    		click(zoomOut[0], zoomOut[1]);
     	}
     	else if(imageX < maxPageSize125[0] && imageY < maxPageSize125[1]){
    		newCoordinates[7] = newCoordinates[3] = (imageY / maxPageSize125[1]) * edgesOfPortrait[1];
    		newCoordinates[4] = newCoordinates[6] = (imageX / maxPageSize125[0]) * edgesOfPortrait[4];
    		formatImage(8);
    		click(zoomOut[0], zoomOut[1]);
    		click(zoomOut[0], zoomOut[1]);
    		click(zoomOut[0], zoomOut[1]);
     	}
     	else{
     		System.out.println("ERROR: IMAGE SIZE IS LARGER THAN 10336x4728");	
     		System.exit(0);
     	}
    	
    	//resizes the canvas in paint
    click(resizePicture[0], resizePicture[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	click(pixelRatio[0], pixelRatio[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	click(maintainAspect[0], maintainAspect[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	click(horizontalPixels[0], horizontalPixels[1]);
   	click(horizontalPixels[0], horizontalPixels[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	type(Integer.toString(imageX));
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	click(verticalPixels[0], verticalPixels[1]);
   	click(verticalPixels[0], verticalPixels[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	type(Integer.toString(imageY));
    TimeUnit.MILLISECONDS.sleep(waitTime);
   	click(okButtonResize[0],  okButtonResize[1]);
    TimeUnit.MILLISECONDS.sleep(waitTime);
    }
    
    //saves the image
    public static void saveImage() throws InterruptedException, AWTException{
    	hit("ctrl + s");
    	TimeUnit.MILLISECONDS.sleep(300);
    	click(desktop[0], desktop[1]);
    	TimeUnit.MILLISECONDS.sleep(300);
    	click(fileName[0], fileName[1]);
    	TimeUnit.MILLISECONDS.sleep(300);
    	System.out.print("Please type in the file name now: ");
    	String filename = input.next();
    	TimeUnit.MILLISECONDS.sleep(300);
    	type(filename);
    	TimeUnit.MILLISECONDS.sleep(300);
    	click(saveButton[0], saveButton[1]);
    	TimeUnit.MILLISECONDS.sleep(300);
    }
    
    
    //clicks on a certain coordinate on the screen
    public static void click(int x, int y) throws AWTException{
        Robot bot = new Robot();
        bot.mouseMove(x, y);    
        bot.mousePress(InputEvent.BUTTON1_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    
    //hits a certain key on the keyboard, takes a string 
    public static void hit(String input) throws AWTException{
    	Robot bot = new Robot();
    	
    	if(input.equals("enter")){
    		bot.keyPress(KeyEvent.VK_ENTER);
    		bot.keyRelease(KeyEvent.VK_ENTER);
    	}
    	else if(input.equals("ctrl + s")){
    		bot.keyPress(KeyEvent.VK_CONTROL);
    		bot.keyPress(KeyEvent.VK_S);
    		bot.keyRelease(KeyEvent.VK_CONTROL);
    		bot.keyRelease(KeyEvent.VK_S);
    	}
    }
    
    //types a certain given string
    public static void type(String input) throws AWTException{
    	Robot bot = new Robot();
    	int keycode = 0;
    	
    	for(int i = 0; i < input.length(); i++){
    		if (Character.isLetter(input.charAt(i)))
    			keycode = (int)input.charAt(i) - 32;
    		else
    			keycode = (int)input.charAt(i);

    		bot.keyPress(keycode);
    	}
    	
    }
    
    
    public static void scroll(int x, int y){
    	//probably don't need this
    }
    
    //loops the painting cycle until all the pixels are covered
    public static void startPainting() throws AWTException, InterruptedException{
    	//int[] currentPixel = {5, 140};

    	int prevX = 0, prevY = 0;
    	boolean startedPainting = false;
    	//int currentColor = 0;
    	
			 for(int r = 0; r < 254; r+=sharpness){
				 for(int g = 0; g < 254; g+=sharpness){
					 for(int b = 0; b < 254; b+=sharpness){
						 
						 if(colors[r/sharpness][g/sharpness][b/sharpness] == 1){
							 getPixels(r, g, b);
							 startedPainting = false;
							 
							 if(values.size() == 0)
								 continue;
							 
							 click(editColors[0], editColors[1]);
							 TimeUnit.MILLISECONDS.sleep(500);
							 click(colorInit[0], colorInit[1]);
							 
							 click(redInput[0], redInput[1]);
							 click(redInput[0], redInput[1]);
							 
							 type(Integer.toString(r));
							 	
							 click(greenInput[0], greenInput[1]);
							 click(greenInput[0], greenInput[1]);
									 	
							 type(Integer.toString(g));
									 
							 click(blueInput[0], blueInput[1]);
							 click(blueInput[0], blueInput[1]);
									 
							 type(Integer.toString(b));
									 
							 click(okButtonColor[0], okButtonColor[1]);
							 TimeUnit.MILLISECONDS.sleep(500);	
								 	
							 for(int i = 0; i < values.size(); i += 2){
								 if(startedPainting)
									 checkForPause(prevX, prevY);
								 else
									 startedPainting = true;
										 
								 click(values.get(i), values.get(i + 1));
								 TimeUnit.MICROSECONDS.sleep(100);
								 prevX = values.get(i);
								 prevY = values.get(i + 1);
								 startedPainting = true;
							 }
						 }
					 }
				 }
			 }
    }
			 
   //checks for a pause by the user moving the mouse, will stop painting all together and start again after a couple seconds
   public static void checkForPause(int prevX, int prevY) throws AWTException, InterruptedException{
           PointerInfo mousePtr = MouseInfo.getPointerInfo();
           Point position = mousePtr.getLocation();
           
           int x = (int) position.getX();
           int y = (int) position.getY();
           
           if(x != prevX || y != prevY){
        	   System.out.println("x : " + x + " y: " + y + " prevx: " + prevX + " prevY: " + prevY);
        	   click(prevX, prevY);
        	   TimeUnit.MILLISECONDS.sleep(2000);
        	   System.out.println("paused");
        	   detectFrame();
           }
   		}
    }
