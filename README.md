# JPEG-compression-pipeline
Implementing a lossless compression algorithm for transferring a .RGB image file

## Steps:

1.) Convert the original .RGB image file into .YCrCb file format

2.) Divide the image into sizes of 8x8 blocks to handle the compression efficiently. The boundary values were handled by the program to be included in one of the existing blocks

3.) Apply DCT tranform on each block formed in step 2

4.) Quanitze the values of the existing blocks by using the 8x8 quanitization matrix

5.) Apply inverse DCT to the indiviual blocks 

6.) Convert the image from YCrCb format to .RGB format.

7.) Display the results by showing the original image on the left and the transformed image on the right.

Unzip the folder to where you want.
To run the code from command line, first compile with:

### >> javac imageReader.java

and then, you can run to read a sample image (image1.rgb) as:

### >> java imageReader Image1.rgb 352 288

where, the first parameter is the image file name, second is the width and third is the height.
