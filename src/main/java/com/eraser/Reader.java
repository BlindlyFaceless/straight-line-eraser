package com.eraser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Reader 
{
    
    static ArrayList<int[]> linePos = new ArrayList<>();
    
    static File imageFileIn = null;
    static BufferedImage imageFileOut = null;

    public static void read(String path) //read a file to get data from
    {

        try 
        {
            imageFileIn = new File(path); //check file path
            imageFileOut = ImageIO.read(imageFileIn); //check image
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return;
        }

        output();
    }

    private static void output()
    {
        BufferedImage outFile = null;

        try
        {
            outFile = new BufferedImage(imageFileOut.getWidth(), imageFileOut.getHeight(), BufferedImage.TYPE_INT_ARGB); //copy image
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return;
        }
        
        findLines();
        int lookAtPos = 0;

        for(int y = 0; y < imageFileOut.getHeight(); y++)
        {
            for(int x = 0; x < imageFileOut.getWidth(); x++)
            {
                int heightMult = 2;
                double yShiftVal = y + heightMult*Math.sin(x/5);
                
                int yPos = linePos.get(lookAtPos)[1];
                int xMin = linePos.get(lookAtPos)[0];
                int xMax = linePos.get(lookAtPos)[2];
            
                if(y == yPos && x >= xMin && x <= xMax)
                {
                    if(y > heightMult && y < imageFileOut.getHeight()-heightMult)
                    {
                        outFile.setRGB(x, (int)yShiftVal, imageFileOut.getRGB(x, y)); //set pixel values
                    }
                    if(y < heightMult*2 ||  y >= imageFileOut.getHeight()-(heightMult*2))
                    {
                        outFile.setRGB(x, y, imageFileOut.getRGB(x, y));
                    }
                }
                else
                {
                    lookAtPos++;
                    outFile.setRGB(x, y, imageFileOut.getRGB(x, y));
                }
            }
            lookAtPos++;
        }

        try 
        {
            File newOut = new File("src\\images\\newOut.png"); //create new image
            ImageIO.write(outFile, "png", newOut); //set new image to edited image
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private static void findLines()
    {
        int sigLength = imageFileOut.getWidth()/10; //length of a line that is long enough to be considered a line

        for(int y = 0; y < imageFileOut.getHeight(); y++)
        {
            int currentUnbroken = 0;
            int startX = 0;
            int startY = y;
            int lastPixel = imageFileOut.getRGB(startX, y);

            for(int x = 1; x < imageFileOut.getWidth(); x++)
            {
                if(imageFileOut.getRGB(x, y) == lastPixel)
                {
                    currentUnbroken++;
                }
                else if(currentUnbroken >= sigLength)
                {
                    linePos.add(new int[] {startX, startY, x-1, y});
                    startX = x;
                    currentUnbroken = 0;
                }
                lastPixel = imageFileOut.getRGB(x, y);
            }
            if(currentUnbroken >= sigLength)
            {
                linePos.add(new int[] {startX, startY, imageFileOut.getWidth() - 1, y});
            }
        }
        
        for (int[] i : linePos)
        {
            System.out.println(i[0] + ", " + i[1] + ", " + i[2] + ", " + i[3]);
        }
        System.out.println(imageFileOut.getWidth() + ", " + imageFileOut.getHeight());
    }
}