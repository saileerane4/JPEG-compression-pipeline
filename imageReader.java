
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.Math;



public class imageReader {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img,img1,img2;
	int width = 352;
	int height= 288;
	
    //used in dct transform to work on 8x8 block
	double workBlock[][] = new double[8][8];
	
	//array to save the YCRCB component value after step 1
	double[][] Yvalue= new double[width][height];
	double[][] Cbvalue= new double[width][height];
	double[][] Crvalue= new double[width][height];
	
	//array to save the RGB component values after converting YCrCb to RGB
	double[][] RR = new double[width][height];
	double[][] GG = new double[width][height];
	double[][] BB = new double[width][height];
	
	//array for saving the 8x8 blocks
	double blockY[][]= new double[width][height];
	double blockCb[][]= new double[width][height];
	double blockCr[][]= new double[width][height];
	
	//array for saving dct values
	double blockYdct[][]= new double[width][height];
	double blockCbdct[][]= new double[width][height];
	double blockCrdct[][]= new double[width][height];
	
	double blockYY[][]= new double[8][8];
	double blockCBB[][]= new double[8][8];
	double blockCRR[][]= new double[8][8];
	
	double Yvaluedct[][]= new double[width][height];
	double Cbvaluedct[][]= new double[width][height];
	double Crvaluedct[][]= new double[width][height];
	
	//array for saving inverse dct values
	
	double blockYi[][] = new double[width][height];
	double blockCbi[][]= new double[width][height];
	double blockCri[][]= new double[width][height];
	
	double blockYYi[][]= new double[8][8];
	double blockCBBi[][]= new double[8][8];
	double blockCRRi[][]= new double[8][8];
	
	double YvalueIdct[][]= new double[width][height];
	double CbvalueIdct[][]= new double[width][height];
	double CrvalueIdct[][]= new double[width][height];
	
	double workBlock2[][]= new double[8][8];
	
	//array values for ndiagonal function
	
	double blockYYd[][] = new double[8][8];
	double blockCbd[][] = new double[8][8];
	double blockCrd[][] = new double[8][8];
	
	double blockYYd1[][] = new double[8][8];
	double blockCbd1[][] = new double[8][8];
	double blockCrd1[][] = new double[8][8];
	
	double Yvaluedct1[][]= new double[width][height];
	double Cbvaluedct1[][]= new double[width][height];
	double Crvaluedct1[][]= new double[width][height];
	
	
	int nLine;
	
	
	double Quant[][] = {{16,11,10,16,24,40,51,61},
			            {12,12,14,19,26,58,60,55},
			            {14,13,16,24,40,57,69,56},
			            {14,17,22,29,51,87,80,62},
			            {18,22,37,56,68,109,103,77},
			            {24,35,55,64,81,104,113,92},
			            {49,64,78,87,103,121,120,101},
			            {72,92,95,98,112,100,103,99}};

	public void showIms(String[] args){
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
		nLine=Integer.parseInt(args[1]);
		
		try {
			File file = new File(args[0]);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int)len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}

			//n=args[1];
			int ind = 0;
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					//converting the image into ycrcb
					int Y = (int)Math.round(0.299*r+0.587*g+0.114*b);
		            int Cb=(int)Math.round(128-0.169*r-0.331*g+0.500*b);
		            int Cr =(int)Math.round(128+0.500*r-0.419*g-0.081*b);
		            Yvalue[x][y]= Y;
		            Cbvalue[x][y]= Cb;
		            Crvalue[x][y]= Cr;
		            
		            
		            int pix1 = 0xff000000 | ((Y & 0xff) << 16) | ((Cb & 0xff) << 8) | (Cr & 0xff);
		            
		            img1.setRGB(x,y,pix1);
					
					ind++;
				}
			}
			
				
			
			//dividing the image into 8x8 blocks
			
		  for(int x=0; x<height; x=x+8)
			{
				for(int y=0; y<width; y=y+8)
				{
				   for(int i=0; i<8; i++)
				   {
					   
					   for(int j=0; j<8; j++)
					   {
						   blockY[j][i]= Yvalue[y+j][x+i] ;
						   blockCb[j][i]= Cbvalue[y+j][x+i];
						   blockCr[j][i]= Crvalue[y+j][x+i];
						   
						   
					   }
				   }
				   //call dct transform 
				   blockYY=DCTtrans(blockY);
				   blockCBB=DCTtrans(blockCb);
				   blockCRR=DCTtrans(blockCr);
						   
						   for(int i=0; i<8; i++)
						   {
							   
							   for(int j=0; j<8; j++)
							   {   //Quantize the values
								   
								   
								   blockYY[j][i]=(blockYY[j][i]/Quant[j][i]);
								   
								   blockCBB[j][i]=(blockCBB[j][i]/Quant[j][i]);
								   
								   blockCRR[j][i]=(blockCRR[j][i]/Quant[j][i]);
								   
								   
							   }
						   }
						   
					
						blockYYd1=nDiagonal(blockYY);
						blockCbd1=nDiagonal(blockCBB);
						blockCrd1=nDiagonal(blockCRR);
						
						for(int i=0; i<8; i++ )
						{
							for (int j=0; j<8; j++ )
							{
								Yvaluedct1[y+j][x+i]=blockYYd1[j][i];
								Cbvaluedct1[y+j][x+i]=blockCbd1[j][i];
								Crvaluedct1[y+j][x+i]=blockCrd1[j][i];
								
								
							}
							
						}
						
						
						  
						
				}
				
			}
		  
		  //
		 
		  
			//inverse dct transform
			
			for(int x=0; x<height; x=x+8)
			{
				for(int y=0; y<width;y=y+8)
				{
				   for(int i=0; i<8; i++)
				   {
					   
					   for(int j=0; j<8; j++)
					   {
						   blockYi[j][i]= (Yvaluedct1[y+j][x+i]*Quant[j][i]) ;
						   blockCbi[j][i]= (Cbvaluedct1[y+j][x+i]*Quant[j][i]);
						   blockCri[j][i]= (Crvaluedct1[y+j][x+i]*Quant[j][i]);
						   
						   
					   }
				   }
				   //call inverse dct transform 
				   blockYYi=IDCTtrans(blockYi);
				   blockCBBi=IDCTtrans(blockCbi);
				   blockCRRi=IDCTtrans(blockCri);
						   
						   for(int i=0; i<8; i++)
						   {
							   
							   for(int j=0; j<8; j++)
							   {
								   YvalueIdct[y+j][x+i]=(blockYYi[j][i]);
								   CbvalueIdct[y+j][x+i]=(blockCBBi[j][i]);
								   CrvalueIdct[y+j][x+i]=(blockCRRi[j][i]);
								   
								   
							   }
						   }
					
				}
			}
			
			
			
			
			
			//converting the image back to rgb
			
			for(int y=0;y < height; y++)
			{
				for(int x=0;x < width; x++)
				{
					int getpix = img1.getRGB(x,y);
					int rr = (int)Math.round((YvalueIdct[x][y] + 1.40200 * (CrvalueIdct[x][y] - 0x80)));
//					if (rr>255) rr=255;//System.out.println(rr);
					if(rr<0)//rr=0;
						//{System.out.println(rr);ind1++;}
					RR[x][y]=(int)rr;
					int gg = (int)Math.round( (YvalueIdct[x][y] - 0.34414 * (CbvalueIdct[x][y] - 0x80) - 0.71414 * (CrvalueIdct[x][y] - 0x80)));
//					if (gg>255)gg=255;// System.out.println(gg);
//					if(gg<0) gg=00;//System.out.println(gg);
					GG[x][y]=(int)gg;
					int bb = (int) Math.round((YvalueIdct[x][y] + 1.77200 * (CbvalueIdct[x][y] - 0x80)));
//					if (bb>255) bb=255; //System.out.println(bb);
//					if(bb<0) bb=0;//System.out.println(bb);
					BB[x][y]=(int)bb;
					int pixvalue = 0xff000000 | ((rr & 0xff) << 16) | ((gg & 0xff) << 8)| (bb & 0xff);
					
					
					img2.setRGB(x,y,pixvalue);
					
					
				}
			}
           

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(img2));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
	}
	
	
	public double[][] DCTtrans(double[][] value)
	{
		
		double[][] f= new double[8][8];
		double cu,cv;
		
		
		
		
				
				for(int v=0; v<8; v++)
				{
					for(int u=0; u<8; u++)
					{
						if (u==0)
							cu=(1/Math.sqrt(2));
						else 
							cu=1;
						if (v==0)
							cv=(1/Math.sqrt(2));
						else 
							cv=1;
						
					
					
						for(int i=0 ; i<8 ;i++)
						{
							for (int j=0; j<8 ;j++)
							{
								workBlock[j][i]= value[j][i];
								f[u][v] = f[u][v] + workBlock[j][i] * Math.cos((((2*j)+1)*(u*Math.PI))/16) * Math.cos((((2*i)+1)*(v*Math.PI))/16);
							}
						}
						f[u][v]=(0.25*cu*cv)*f[u][v];
					}
				}
		
		
		return f;
		 
	}
	
	//inverse dct transform function
	public double[][] IDCTtrans(double[][] value1)
	{
		double[][] finv= new double[8][8];
		double[][] opInv= new double[8][8];
		double cu,cv;
		
				for(int i=0; i<8; i++)
				{
					for(int j=0; j<8; j++)
					{
						
					
					
						for(int v=0 ; v<8 ;v++)
						{
							for (int u=0; u<8 ;u++)
							{
								if (u==0)
									cu=(1/Math.sqrt(2));
								else 
									cu=1;
								if (v==0)
									cv=(1/Math.sqrt(2));
								else 
									cv=1;
								
								workBlock2[u][v]= value1[j][i];
								//workBlock[j][i]= value[u+j][v+i];
								//f[u][v] = f[u][v] + workBlock[j][i] * Math.cos((((2*j)+1)*(u*Math.PI))/16) * Math.cos((((2*i)+1)*(v*Math.PI))/16);
								
								finv[j][i] = finv[j][i] + ((cu*cv) * value1[u][v] * Math.cos((((2*j)+1)*(u*Math.PI))/16) * Math.cos((((2*i)+1)*(v*Math.PI))/16));
							
							}
						}
						
						finv[j][i]=(1/4.0)*finv[j][i];
					}
				}
		
		
		return finv;
	}
	
	public double[][] nDiagonal(double[][] value)
	{
		double blocka[][]= new double[8][8];
		
		for( int k = 0 ; k < nLine ; k++ ) {
	        for( int j = 0 ; j <= k ; j++ ) {
	            int i = k - j;
	            if( i < 8 && j < 8 ) {
	                //System.out.print( Quant[i][j] + " " );
	                blocka[i][j]=1;
	            }
	        }
	        //System.out.println();
	    }
	         
	         for(int m=0;m<8;m++)
	         {
	             for(int n=0;n<8;n++)
	             {
	                 if(blocka[n][m]==1)
	                 {
	                    blocka[n][m]=value[n][m]; 
	                 }
	                 else
	                     blocka[n][m]=0;
	                 
	                // System.out.print(blocka[n][m]+"  ");
	                     
	             }
	            // System.out.println();
	         }
	       
		
		
		

		
		return blocka;
		
		
		
	}

	public static void main(String[] args) {
		imageReader ren = new imageReader();
		ren.showIms(args);
	}

}